package com.brilliant.game.ui.azs;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.text.Editable;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.brilliant.game.R;
import com.brilliant.game.core.Samp;
import com.brilliant.game.launcher.util.Util;
import com.nvidia.devtech.CustomEditText;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Formatter;

public class AZS {

    private final Activity mActivity;
    private final ConstraintLayout mInputLayout;
    private final ArrayList<ImageView> mItems;
    private final CustomEditText fuelET;

    private int mFuel = 0;
    private int mMaxFuel = 0;
    private int mCurrentFuel = 0;
    private int mPrice = 0;
    private int mReleasePrice = 0;
    private int mPlayerBalance = 0;

    private final TextView mMaxFuelTV;
    private final TextView mDoFuelTV;
    private final TextView mPriceTV;
    private final Drawable[] mDrawables;

    public AZS(Activity activity) {
        this.mActivity = activity;
        this.mInputLayout = activity.findViewById(R.id.brp_azs_main);

        this.mItems = new ArrayList<>();

        if (this.mInputLayout == null) {
            mDrawables = new Drawable[0];
            mMaxFuelTV = null;
            mDoFuelTV = null;
            mPriceTV = null;
            fuelET = null;
            return;
        }

        mDrawables = new Drawable[]{
                activity.getDrawable(R.drawable.ic_azs_ai92),
                activity.getDrawable(R.drawable.ic_azs_ai95),
                activity.getDrawable(R.drawable.ic_azs_ai98),
                activity.getDrawable(R.drawable.ic_azs_dt)
        };

        ImageView close = activity.findViewById(R.id.brp_azs_close);
        close.setOnClickListener(v -> hide());

        mMaxFuelTV = activity.findViewById(R.id.brp_azs_max_fuel);
        mDoFuelTV = activity.findViewById(R.id.brp_azs_do_fuel);
        mPriceTV = activity.findViewById(R.id.brp_azs_fuel_price);

        Button doFull = activity.findViewById(R.id.brp_azs_full_btn);
        Button startRefuel = activity.findViewById(R.id.brp_azs_start_btn);

        doFull.setOnClickListener(view -> {
            view.startAnimation(AnimationUtils.loadAnimation(this.mActivity, R.anim.btn_click));

            update(500);
        });

        startRefuel.setOnClickListener(view -> {
            view.startAnimation(AnimationUtils.loadAnimation(this.mActivity, R.anim.btn_click));

            // Игрок мог ввести число и сразу нажать "НАЧАТЬ ЗАПРАВКУ", не подтвердив
            // ввод Done/Next на клавиатуре — тогда mFuel всё ещё 0. Досчитываем перед отправкой.
            commitFuelInput();

            hide();

            if (this.mFuel <= 0) {
                Samp.getInstance().showNotification(0, "Укажите количество топлива", 5, "", "");
                return;
            }

            if (this.mPlayerBalance >= this.mReleasePrice)
                Samp.getInstance().refuelTheCar(this.mFuel, this.mPrice);
            else {
                Samp.getInstance().showNotification(0, "Недостаточно средств", 5, "", "");
            }
        });

        this.fuelET = activity.findViewById(R.id.brp_azs_fuel_et);
        this.fuelET.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_DONE || i == EditorInfo.IME_ACTION_NEXT) {
                commitFuelInput();
            }

            return false;
        });

        LinearLayout items1 = activity.findViewById(R.id.brp_azs_items_1);
        LinearLayout items2 = activity.findViewById(R.id.brp_azs_items_2);

        for (int i = 0; i < items1.getChildCount(); i++)
            mItems.add((ImageView) items1.getChildAt(i));

        for (int i = 0; i < items2.getChildCount(); i++)
            mItems.add((ImageView) items2.getChildAt(i));

        Util.HideLayout(this.mInputLayout, false);
    }

    public void show(int fuelId, int maxFuel, float currentFuel, int price, int balance) {
        if (mInputLayout == null) return;

        // Спидометр рисуется поверх main_render_screen (отдельный addContentView),
        // поэтому визуально перекрывает нижнюю часть окна АЗС — прячем его на время диалога
        if (Samp.getInstance() != null && Samp.getInstance().getSpeedometr() != null) {
            Samp.getInstance().getSpeedometr().tempToggle(false);
        }

        Util.ShowLayout(this.mInputLayout, true);

        this.mInputLayout.animate().translationX(500).setDuration(0).start();
        this.mInputLayout.animate().translationX(0).setDuration(500).start();

        this.mMaxFuel = maxFuel;
        this.mCurrentFuel = Math.round(currentFuel);
        this.mPrice = price;

        this.mPlayerBalance = balance;

        this.mMaxFuelTV.setText(new Formatter().format("%d L", maxFuel).toString());

        for (int i = 0; i < mItems.size(); i++) {
            ImageView item = mItems.get(i);

            if (i != fuelId) {
                Drawable incorrectFuelDrawable = mActivity.getDrawable(R.drawable.ic_azs_incorrect_fuel);
                Drawable currentFuelDrawable = mDrawables[i];

                LayerDrawable finalDrawable = new LayerDrawable(new Drawable[]{currentFuelDrawable, incorrectFuelDrawable});

                item.setImageDrawable(finalDrawable);
                item.setOnClickListener(null);
            } else
                item.setOnClickListener(view -> view.startAnimation(AnimationUtils.loadAnimation(this.mActivity, R.anim.btn_click)));
        }
    }

    public void hide() {
        if (mInputLayout == null) return;

        if (Samp.getInstance() != null && Samp.getInstance().getSpeedometr() != null) {
            Samp.getInstance().getSpeedometr().tempToggle(true);
        }

        Util.HideLayout(this.mInputLayout, true);

        this.mDoFuelTV.setText("0 L");
        this.mMaxFuelTV.setText("0 L");
        this.mPriceTV.setText("0 RUB");

        this.fuelET.setText("");

        for (int i = 0; i < mItems.size(); i++) {
            ImageView item = mItems.get(i);

            int drawableIndex = Math.min(i, 3);

            item.setImageDrawable(this.mDrawables[drawableIndex]);
        }

        this.mInputLayout.animate().translationX(-500).setDuration(500).start();
    }

    private void commitFuelInput() {
        if (this.fuelET == null) return;

        Editable editableText = this.fuelET.getText();

        if (editableText != null && editableText.length() > 0) {
            try {
                int fuel = Integer.parseInt(editableText.toString());
                update(fuel);
            } catch (NumberFormatException ignored) {
            }
        }
    }

    private void update(int fuel) {
        int maxFuel = this.mMaxFuel - this.mCurrentFuel;

        this.mFuel = fuel;

        if (this.mFuel > maxFuel) {
            this.mFuel = maxFuel;

            this.fuelET.setText(String.valueOf(this.mFuel));
        }

        this.mReleasePrice = this.mFuel * this.mPrice;

        this.mDoFuelTV.setText(new Formatter().format("%d L", this.mFuel).toString());
        this.mPriceTV.setText(new Formatter().format("%s RUB", new DecimalFormat("###,###,###").format(this.mReleasePrice)).toString());
    }

    public ConstraintLayout getInputLayout() {
        return mInputLayout;
    }
}
