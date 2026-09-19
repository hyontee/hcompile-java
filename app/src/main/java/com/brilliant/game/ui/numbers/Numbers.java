package com.brilliant.game.ui.numbers;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;

import android.app.Activity;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;

import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;

import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;

import com.brilliant.game.R;
import com.brilliant.game.core.Samp;
import com.brilliant.game.launcher.util.Util;
import com.nvidia.devtech.CustomEditText;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class Numbers {

    private static final int PRICE_RANDOM = 5000;
    private static final int PRICE_CUSTOM = 500000;

    private final Activity mActivity;

    private final ConstraintLayout mInputLayout;
    private final ConstraintLayout mNumbersMain;

    private int mCurrentCountryId = 0;
    private int mBalance = 0;

    private final ArrayList<ConstraintLayout> mCountries;

    private final Drawable mItemBackgroundWithStroke;
    private final Drawable mItemBackgroundWithStrokeExclusive;
    private final Drawable mItemBackgroundWithoutStroke;

    // "Кастомный" режим — ручной ввод номера (500 000₽), в отличие от
    // случайной генерации (5 000₽). Раньше называлось "exclusive" в x32.
    private boolean mIsCustomMode = false;

    private final CustomEditText mNumber;
    private final CustomEditText mRegion;

    private final TextView mExampleNumber;
    private final TextView mDigitsNumber;

    private final TextView mPrices1;
    private final TextView mResultPrice;

    private boolean mHasGeneratedNumber = false;

    private String mLastRuNumber = "";
    private String mLastRuRegion = "";

    private String mLastUaNumber = "";

    private String mLastByNumber = "";

    private String mLastKzNumber = "";
    private String mLastKzRegion = "";

    char[] mAllowedHighBy = {'A', 'B', 'C', 'E', 'H', 'K', 'M', 'O', 'P', 'T', 'X', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ' ', '-'};

    char[] mAllowedHigh = {'A', 'B', 'C', 'E', 'H', 'K', 'M', 'O', 'P', 'T', 'X', 'Y', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ' '};
    char[] mAllowedHighChars = {'A', 'B', 'C', 'E', 'H', 'K', 'M', 'O', 'P', 'T', 'X'};

    char[] mAllowedLower = {'A', 'B', 'C', 'E', 'H', 'K', 'M', 'O', 'P', 'T', 'X', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

    public Numbers(Activity activity) {
        this.mActivity = activity;

        this.mInputLayout = activity.findViewById(R.id.brp_numbers_main);
        this.mNumbersMain = activity.findViewById(R.id.brp_numbers_number_bg);

        if (this.mInputLayout == null) {
            mCountries = new ArrayList<>();
            mItemBackgroundWithStroke = null;
            mItemBackgroundWithStrokeExclusive = null;
            mItemBackgroundWithoutStroke = null;
            mNumber = null;
            mRegion = null;
            mPrices1 = null;
            mResultPrice = null;
            mExampleNumber = null;
            mDigitsNumber = null;
            return;
        }

        this.mItemBackgroundWithStroke = activity.getDrawable(R.drawable.numbers_country_item_bg);
        this.mItemBackgroundWithStrokeExclusive = activity.getDrawable(R.drawable.numbers_country_item_exclusive_bg);
        this.mItemBackgroundWithoutStroke = activity.getDrawable(R.drawable.numbers_country_item_bg_without_stroke);

        this.mNumber = activity.findViewById(R.id.brp_numbers_number);
        this.mRegion = activity.findViewById(R.id.brp_numbers_region);

        this.mPrices1 = activity.findViewById(R.id.brp_numbers_prices_1);

        this.mResultPrice = activity.findViewById(R.id.brp_numbers_result_price);

        this.mCountries = new ArrayList<>();

        Animation animation = AnimationUtils.loadAnimation(activity, R.anim.btn_click);

        Drawable[] countryDrawables = new Drawable[]{
                activity.getDrawable(R.drawable.ic_russia),
                activity.getDrawable(R.drawable.ic_ukraine),
                activity.getDrawable(R.drawable.ic_belarus),
                activity.getDrawable(R.drawable.ic_kazakhstan)
        };

        String[] countryNames = new String[]{
                "RU", "UA", "BY", "KZ"
        };

        LinearLayout countries = activity.findViewById(R.id.brp_numbers_countries);

        ImageView newBackground = activity.findViewById(R.id.brp_numbers_main_new_bg);
        ImageView background = activity.findViewById(R.id.brp_numbers_main_bg);

        this.mExampleNumber = activity.findViewById(R.id.brp_numbers_example_number);
        this.mDigitsNumber = activity.findViewById(R.id.brp_numbers_digits);

        TextView repeatButton = activity.findViewById(R.id.brp_numbers_repeat_btn);
        repeatButton.setOnClickListener(view -> {
            view.startAnimation(animation);

            // Генерация — бесплатное косметическое действие на клиенте,
            // деньги списываются сервером только в момент реальной покупки.
            generateNumber();
        });

        TextView buyButton = activity.findViewById(R.id.brp_numbers_buy_btn);
        buyButton.setOnClickListener(view -> {
            view.startAnimation(animation);

            String number = this.mNumber.getText().toString();
            String region = this.mRegion.getText().toString();

            int price = this.mIsCustomMode ? PRICE_CUSTOM : PRICE_RANDOM;

            if (!this.mIsCustomMode && !this.mHasGeneratedNumber) {
                Toast.makeText(mActivity.getApplicationContext(), "Сгенерируйте номер", Toast.LENGTH_SHORT).show();
                return;
            }

            if (this.mIsCustomMode && !isValidNumber(number)) {
                Toast.makeText(mActivity.getApplicationContext(), "Неверный формат номера", Toast.LENGTH_SHORT).show();
                return;
            }

            if (this.mIsCustomMode && !isRegCorrect(region)) {
                Toast.makeText(mActivity.getApplicationContext(), "Укажите регион", Toast.LENGTH_LONG).show();
                return;
            }

            if (this.mBalance < price) {
                Toast.makeText(mActivity.getApplicationContext(), "У вас не достаточно денег для покупки номера", Toast.LENGTH_SHORT).show();
                return;
            }

            Samp.getInstance().sendBuyNumber(this.mCurrentCountryId, this.mIsCustomMode ? 1 : 0, number, region);
        });

        TextView exitButton = activity.findViewById(R.id.brp_numbers_exit_btn);
        exitButton.setOnClickListener(view -> {
            view.startAnimation(animation);

            hide();
        });

        ImageView exclusiveButton = activity.findViewById(R.id.brp_numbers_exclusive_btn);
        exclusiveButton.setOnClickListener(view -> {
            view.startAnimation(animation);

            Drawable exampleBackground;
            Drawable mainBackground;

            Drawable exclusiveButtonBackground;
            Drawable buyButtonBackground;

            if (this.mIsCustomMode) {
                exampleBackground = activity.getDrawable(R.drawable.shape_numbers);
                mainBackground = activity.getDrawable(R.drawable.numbers_bg);

                exclusiveButtonBackground = activity.getDrawable(R.drawable.ic_exclusive_numbers_btn);
                buyButtonBackground = activity.getDrawable(R.drawable.ic_numbers_buy_btn);
            } else {
                exampleBackground = activity.getDrawable(R.drawable.shape_numbers_exclusive);
                mainBackground = activity.getDrawable(R.drawable.numbers_exclusive_bg);

                exclusiveButtonBackground = activity.getDrawable(R.drawable.ic_change_numbers_btn);
                buyButtonBackground = activity.getDrawable(R.drawable.ic_numbers_buy_exclusive_btn);
            }

            this.mIsCustomMode = !this.mIsCustomMode;

            if (this.mIsCustomMode) {
                Util.enableEditText(this.mNumber);
                Util.enableEditText(this.mRegion);

                this.mNumber.setText("");
                this.mRegion.setText("");
            } else {
                Util.disableEditText(this.mNumber);
                Util.disableEditText(this.mRegion);
            }

            updateCountry();

            // -- Animation
            background.setBackground(newBackground.getBackground());
            newBackground.setBackground(mainBackground);

            int finalRadius = Math.max(background.getWidth(), background.getHeight());

            Animator anim = ViewAnimationUtils.createCircularReveal(background, background.getRight(), background.getTop(), finalRadius, 0);
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);

                    background.setBackgroundColor(Color.TRANSPARENT);

                    mExampleNumber.setBackground(exampleBackground);
                    mDigitsNumber.setBackground(exampleBackground);

                    exclusiveButton.setImageDrawable(exclusiveButtonBackground);
                    buyButton.setBackground(buyButtonBackground);
                }
            });
            anim.start();
        });

        for (int i = 0; i < countries.getChildCount(); i++) {
            ConstraintLayout child = (ConstraintLayout) countries.getChildAt(i);

            TextView countryName = child.findViewById(R.id.brp_numbers_country_item_country);
            ImageView countryDrawable = child.findViewById(R.id.brp_numbers_country_item_drawable);

            countryName.setText(countryNames[i]);
            countryDrawable.setImageDrawable(countryDrawables[i]);

            int finalI = i;

            child.setOnClickListener(view -> {
                this.mCurrentCountryId = finalI;
                this.mHasGeneratedNumber = false;

                updateCountry();
            });

            this.mCountries.add(child);
        }

        this.mNumber.addTextChangedListener(new TextWatcher() {
            boolean resetChanges = false;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                char[] cArr = mAllowedLower;

                if (mCurrentCountryId == 1)
                    cArr = mAllowedHigh;

                if (mCurrentCountryId == 2)
                    cArr = mAllowedHighBy;

                this.resetChanges = !isValidNumberText(charSequence.toString(), cArr);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (this.resetChanges && mIsCustomMode) {
                    mNumber.setText(editable.toString().substring(0, editable.toString().length() - 1));

                    Samp.getInstance().showNotification(0, "Вы используете некорректные символы", 5, "", "");
                }
            }
        });

        Util.disableEditText(this.mNumber);
        Util.disableEditText(this.mRegion);

        Util.HideLayout(this.mInputLayout, false);

        updateCountry();
    }

    public void show(int balance) {
        if (mInputLayout == null) return;

        if (mInputLayout.getVisibility() == View.VISIBLE)
            return;

        this.mBalance = balance;

        Util.ShowLayout(this.mInputLayout, true);
    }

    public void hide() {
        if (mInputLayout == null) return;

        Util.HideLayout(this.mInputLayout, true);
    }

    public void buyResult(boolean success, int reason) {
        if (success) {
            Samp.getInstance().showNotification(0, "Вы успешно купили номер!", 5, "", "");
            hide();
            return;
        }

        String text;
        switch (reason) {
            case 1:
                text = "У вас нет транспорта для установки номера";
                break;
            case 2:
                text = "Недостаточно средств для покупки номера";
                break;
            case 3:
                text = "Неверный формат номера или региона";
                break;
            default:
                text = "Не удалось купить номер";
                break;
        }

        Samp.getInstance().showNotification(0, text, 5, "", "");
    }

    private void generateNumber() {
        String resultNum = "";
        String resultRegion = "";

        switch (this.mCurrentCountryId) {
            case 0:
                resultNum += Util.randomString(1, new String(mAllowedHighChars));
                resultNum += new DecimalFormat("000").format(1 + (int) (Math.random() * 999));
                resultNum += Util.randomString(2, new String(mAllowedHighChars));

                resultNum = resultNum.toLowerCase();

                resultRegion = String.valueOf(1 + (int) (Math.random() * 999));

                this.mLastRuNumber = resultNum;
                this.mLastRuRegion = resultRegion;
                break;
            case 1:
                resultNum += Util.randomString(2, new String(mAllowedHighChars)) + " ";
                resultNum += new DecimalFormat("0000").format(1 + (int) (Math.random() * 9999)) + " ";
                resultNum += Util.randomString(2, new String(mAllowedHighChars));

                this.mLastUaNumber = resultNum;
                break;
            case 2:
                resultNum += new DecimalFormat("0000").format(1 + (int) (Math.random() * 9999)) + " ";
                resultNum += Util.randomString(2, new String(mAllowedHighChars)) + "-";
                resultNum += (int) (Math.random() * 7);

                this.mLastByNumber = resultNum;
                break;
            case 3:
                resultNum += new DecimalFormat("000").format(1 + (int) (Math.random() * 999));
                resultNum += Util.randomString(3, new String(mAllowedHighChars));

                resultNum = resultNum.toLowerCase();

                resultRegion = new DecimalFormat("00").format(1 + (int) (Math.random() * 16));

                this.mLastKzNumber = resultNum;
                this.mLastKzRegion = resultRegion;
                break;
        }

        this.mResultPrice.setText(Html.fromHtml("<font color='#6BFF73'>₽ " + new DecimalFormat("###,###").format(PRICE_RANDOM) + "</font>"));

        this.mNumber.setText(resultNum);
        this.mRegion.setText(resultRegion);

        this.mHasGeneratedNumber = true;
    }

    private void updateCountry() {
        for (int i = 0; i < this.mCountries.size(); i++) {
            ConstraintLayout country = this.mCountries.get(i);
            ConstraintLayout countryBg = country.findViewById(R.id.brp_numbers_country_item_bg);

            if (i == this.mCurrentCountryId) {
                country.animate().setDuration(250).translationY(mActivity.getResources().getDimensionPixelSize(R.dimen._minus15sdp)).start();
                countryBg.setBackground(this.mIsCustomMode ? this.mItemBackgroundWithStrokeExclusive : this.mItemBackgroundWithStroke);
            } else {
                country.animate().setDuration(250).translationY(0).start();
                countryBg.setBackground(this.mItemBackgroundWithoutStroke);
            }
        }

        ViewGroup.MarginLayoutParams numberParams = (ViewGroup.MarginLayoutParams) this.mNumber.getLayoutParams();
        ViewGroup.MarginLayoutParams regionParams = (ViewGroup.MarginLayoutParams) this.mRegion.getLayoutParams();

        Drawable background = mActivity.getDrawable(R.drawable.ic_numbers_ru_num);

        int _55sdp = this.mActivity.getResources().getDimensionPixelSize(R.dimen._55sdp);
        int _65sdp = this.mActivity.getResources().getDimensionPixelSize(R.dimen._65sdp);

        Typeface typeface = ResourcesCompat.getFont(this.mActivity.getApplicationContext(), R.font.plate);

        this.mHasGeneratedNumber = false;

        switch (this.mCurrentCountryId) {
            case 0: // -- RU
                numberParams.width = this.mActivity.getResources().getDimensionPixelSize(R.dimen._190sdp);
                numberParams.leftMargin = 0;
                numberParams.topMargin = 0;

                regionParams.width = this.mActivity.getResources().getDimensionPixelSize(R.dimen._100sdp);
                regionParams.height = this.mActivity.getResources().getDimensionPixelSize(R.dimen._50sdp);

                this.mNumber.setText(this.mLastRuNumber);
                this.mNumber.setHint("x777am");
                this.mNumber.setTextSize(this.mActivity.getResources().getDimensionPixelSize(R.dimen._20sdp));

                this.mRegion.setText(this.mLastRuRegion);
                this.mRegion.setHint("777");
                this.mRegion.setTextSize(this.mActivity.getResources().getDimensionPixelSize(R.dimen._12sdp));
                this.mRegion.setVisibility(View.VISIBLE);

                this.mExampleNumber.setText(R.string.example_ru_num);
                this.mDigitsNumber.setText("a b c e h k m o p t x y");
                break;
            case 1: // -- UA
                numberParams.width = this.mActivity.getResources().getDimensionPixelSize(R.dimen._220sdp);
                numberParams.leftMargin = _65sdp;
                numberParams.topMargin = this.mActivity.getResources().getDimensionPixelSize(R.dimen._10sdp);

                this.mNumber.setText(this.mLastUaNumber);
                this.mNumber.setHint("AA 7777 AA");
                this.mNumber.setTextSize(this.mActivity.getResources().getDimensionPixelSize(R.dimen._15sdp));

                this.mRegion.setVisibility(View.GONE);

                typeface = ResourcesCompat.getFont(this.mActivity.getApplicationContext(), R.font.ua_font);

                background = mActivity.getDrawable(R.drawable.ic_numbers_ua_num);

                this.mExampleNumber.setText(R.string.example_ua_num);
                this.mDigitsNumber.setText("A B C E H K M O P T X Y");
                break;
            case 2: // -- BY
                numberParams.width = this.mActivity.getResources().getDimensionPixelSize(R.dimen._230sdp);
                numberParams.leftMargin = _55sdp;
                numberParams.topMargin = this.mActivity.getResources().getDimensionPixelSize(R.dimen._10sdp);

                this.mNumber.setText(this.mLastByNumber);
                this.mNumber.setHint("1234 AB-7");
                this.mNumber.setTextSize(this.mActivity.getResources().getDimensionPixelSize(R.dimen._15sdp));

                this.mRegion.setVisibility(View.GONE);

                typeface = ResourcesCompat.getFont(this.mActivity.getApplicationContext(), R.font.ua_font);

                background = mActivity.getDrawable(R.drawable.ic_numbers_bl_num);

                this.mExampleNumber.setText(R.string.example_by_num);
                this.mDigitsNumber.setText("A B C E H K M O P T X Y");
                break;
            case 3: // -- KZ
                numberParams.width = this.mActivity.getResources().getDimensionPixelSize(R.dimen._150sdp);
                numberParams.leftMargin = _55sdp;
                numberParams.topMargin = 0;

                regionParams.width = this.mActivity.getResources().getDimensionPixelSize(R.dimen._85sdp);
                regionParams.height = this.mActivity.getResources().getDimensionPixelSize(R.dimen._70sdp);

                this.mNumber.setText(this.mLastKzNumber);
                this.mNumber.setHint("444epa");
                this.mNumber.setTextSize(this.mActivity.getResources().getDimensionPixelSize(R.dimen._20sdp));

                this.mRegion.setText(this.mLastKzRegion);
                this.mRegion.setHint("77");
                this.mRegion.setTextSize(this.mActivity.getResources().getDimensionPixelSize(R.dimen._20sdp));
                this.mRegion.setVisibility(View.VISIBLE);

                background = mActivity.getDrawable(R.drawable.ic_numbers_kz_num);

                this.mExampleNumber.setText(R.string.example_kz_num);
                this.mDigitsNumber.setText("A B C E H K M O P T X Y");
                break;
        }

        this.mNumber.setTypeface(typeface);

        this.mNumber.setLayoutParams(numberParams);
        this.mRegion.setLayoutParams(regionParams);

        this.mNumbersMain.setBackground(background);

        if (this.mIsCustomMode) {
            this.mNumber.setText("");
            this.mRegion.setText("");

            this.mExampleNumber.setText("свой номер и регион");

            this.mPrices1.setText(Html.fromHtml("Кастомный номер\t\t\t\t <font color='#FFDA44'>₽ " + new DecimalFormat("###,###").format(PRICE_CUSTOM) + "</font>"));
            this.mResultPrice.setText(Html.fromHtml("<font color='#FFDA44'>₽ " + new DecimalFormat("###,###").format(PRICE_CUSTOM) + "</font>"));
        } else {
            this.mPrices1.setText(Html.fromHtml("Случайный номер\t\t\t\t <font color='#6BFF73'>₽ " + new DecimalFormat("###,###").format(PRICE_RANDOM) + "</font>"));
            this.mResultPrice.setText(Html.fromHtml("₽ 0"));
        }
    }

    private boolean isValidNumberText(String str, char[] cArr) {
        for (int i = 0; i < str.length(); i++) {
            boolean z = false;

            for (char c : cArr) {
                if (str.charAt(i) == c) {
                    z = true;
                    break;
                }
            }

            if (!z)
                return false;
        }

        return true;
    }

    private boolean isValidNumber(String str) {
        switch (this.mCurrentCountryId) {
            case 0:
                return str.length() == 6 && !Character.isDigit(str.charAt(0)) && Character.isDigit(str.charAt(1)) && Character.isDigit(str.charAt(2)) && Character.isDigit(str.charAt(3)) && !Character.isDigit(str.charAt(4)) && !Character.isDigit(str.charAt(5));
            case 1:
                return str.length() == 10 && !Character.isDigit(str.charAt(0)) && !Character.isDigit(str.charAt(1)) && str.charAt(2) == ' ' && Character.isDigit(str.charAt(3)) && Character.isDigit(str.charAt(4)) && Character.isDigit(str.charAt(5)) && Character.isDigit(str.charAt(6)) && str.charAt(7) == ' ' && !Character.isDigit(str.charAt(8)) && !Character.isDigit(str.charAt(9));
            case 2:
                return str.length() == 9 && Character.isDigit(str.charAt(8)) && str.charAt(7) == '-' && Character.isDigit(str.charAt(0)) && Character.isDigit(str.charAt(1)) && Character.isDigit(str.charAt(2)) && Character.isDigit(str.charAt(3)) && str.charAt(4) == ' ' && !Character.isDigit(str.charAt(5)) && !Character.isDigit(str.charAt(6));
            case 3:
                return str.length() == 6 && Character.isDigit(str.charAt(0)) && Character.isDigit(str.charAt(1)) && Character.isDigit(str.charAt(2)) && !Character.isDigit(str.charAt(3)) && !Character.isDigit(str.charAt(4)) && !Character.isDigit(str.charAt(5));
        }

        return false;
    }

    private boolean isRegCorrect(String str) {
        if (this.mCurrentCountryId == 0 || this.mCurrentCountryId == 3)
            return str.length() != 0;

        return true;
    }
}