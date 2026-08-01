package ru.matrp.launcher.fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.matrp.launcher.activity.MainActivity;
import ru.matrp.launcher.model.Details;
import ru.matrp.launcher.model.Servers;
import ru.matrp.launcher.other.Interface;
import ru.matrp.launcher.other.Lists;
import ru.matrp.bonus.R;

public class CabinetFragment extends MainActivity {

    public TextView info_exp;
    public TextView info_money;
    public TextView info_bank;
    public TextView info_fraction;
    public TextView info_house;
    public TextView info_biz1;
    public View info_biz2_divider;
    public LinearLayout info_biz2_layout;
    public TextView info_biz2;
    public TextView info_donate;
    public TextView info_phone;
    public FrameLayout btn_back;
    public LinearLayout header_layout;
    public ConstraintLayout server_layout;
    public ScrollView details_layout;
    public ConstraintLayout footer_layout;
    public ImageView btn_settings;
    public ImageView btn_faq;
    public ConstraintLayout select_server_layout;
    public ImageView server_item_image;
    public ImageView server_background;
    public LinearLayout select_layout;
    public LinearLayout serverinfo_layout;
    public TextView serverinfo_online;
    public TextView serverinfo_name;
    public CardView serverinfo_person_card;
    public TextView serverinfo_person_text;
    public TextView serverinfo_person_name;
    public ImageView info_skin;
    public TextView info_level;

    public CabinetFragment() {
        super();
        cabinetInit();
    }

    public void cabinetInit() {
        if(viewGroup != null) {
            return;
        }
        viewGroup = (ViewGroup) ((LayoutInflater) MainActivity.getMainActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.fragment_cabinet, (ViewGroup) null);
        MainActivity.getMainActivity().front_ui_layout.addView(viewGroup, -1, -1);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) viewGroup.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        viewGroup.setLayoutParams(layoutParams);
        header_layout = (LinearLayout) viewGroup.findViewById(R.id.header_layout);
        server_layout = (ConstraintLayout) viewGroup.findViewById(R.id.server_layout);
        details_layout = (ScrollView) viewGroup.findViewById(R.id.details_layout);
        footer_layout = (ConstraintLayout) viewGroup.findViewById(R.id.footer_layout);
        btn_settings = (ImageView) viewGroup.findViewById(R.id.btn_settings);
        btn_faq = (ImageView) viewGroup.findViewById(R.id.btn_faq);
        select_server_layout = (ConstraintLayout) viewGroup.findViewById(R.id.select_server_layout);
        server_item_image = (ImageView) viewGroup.findViewById(R.id.server_item_image);
        server_background = (ImageView) viewGroup.findViewById(R.id.server_background);
        select_layout = (LinearLayout) viewGroup.findViewById(R.id.select_layout);
        serverinfo_layout = (LinearLayout) viewGroup.findViewById(R.id.serverinfo_layout);
        serverinfo_online = (TextView) viewGroup.findViewById(R.id.serverinfo_online);
        serverinfo_name = (TextView) viewGroup.findViewById(R.id.serverinfo_name);
        serverinfo_person_card = (CardView) viewGroup.findViewById(R.id.serverinfo_person_card);
        serverinfo_person_text = (TextView) viewGroup.findViewById(R.id.serverinfo_person_text);
        serverinfo_person_name = (TextView) viewGroup.findViewById(R.id.serverinfo_person_name);
        info_skin = (ImageView) viewGroup.findViewById(R.id.info_skin);
        info_level = (TextView) viewGroup.findViewById(R.id.info_level);
        info_exp = (TextView) viewGroup.findViewById(R.id.info_exp);
        info_money = (TextView) viewGroup.findViewById(R.id.info_money);
        info_bank = (TextView) viewGroup.findViewById(R.id.info_bank);
        info_fraction = (TextView) viewGroup.findViewById(R.id.info_fraction);
        info_house = (TextView) viewGroup.findViewById(R.id.info_house);
        info_biz1 = (TextView) viewGroup.findViewById(R.id.info_biz1);
        info_biz2_divider = viewGroup.findViewById(R.id.info_biz2_divider);
        info_biz2_layout = (LinearLayout) viewGroup.findViewById(R.id.info_biz2_layout);
        info_biz2 = (TextView) viewGroup.findViewById(R.id.info_biz2);
        info_donate = (TextView) viewGroup.findViewById(R.id.info_donate);
        info_phone = (TextView) viewGroup.findViewById(R.id.info_phone);
        btn_back = (FrameLayout) viewGroup.findViewById(R.id.btn_back);
        btn_settings.setOnTouchListener(new MainActivity.animClickBtn(MainActivity.getMainActivity(), btn_settings));
        btn_settings.setOnClickListener(view -> {
            MainActivity.getMainActivity().settingsNeFragment.show();
        });
        btn_faq.setOnTouchListener(new MainActivity.animClickBtn(MainActivity.getMainActivity(), btn_faq));
        btn_faq.setOnClickListener(view -> {
            MainActivity.getMainActivity().faqFragment.show();
        });
        select_server_layout.setOnTouchListener(new MainActivity.animClickBtn(MainActivity.getMainActivity(), select_server_layout));
        select_server_layout.setOnClickListener(view -> {
            MainActivity.getMainActivity().serverSelectFragment.show();
        });
        btn_back.setOnTouchListener(new MainActivity.animClickBtn(MainActivity.getMainActivity(), btn_back));
        btn_back.setOnClickListener(view -> {
            hide();
            MainActivity.getMainActivity().mainFragment.show();
        });
        server_background.setColorFilter(-8637479);
        select_layout.setVisibility(View.VISIBLE);
        serverinfo_layout.setVisibility(View.GONE);
        details_layout.setVisibility(View.GONE);
        viewGroup.setVisibility(View.GONE);
    }

    public void UpdateServers() {
        if (isAuth) {
            if (server_id == null) {
                server_background.setColorFilter(-8637479);
                server_item_image.setColorFilter(-8637479);
                select_layout.setVisibility(View.VISIBLE);
                serverinfo_layout.setVisibility(View.GONE);
                //server_alert.setVisibility(View.GONE);
            } else {
                if (sApi) {
                    ArrayList<Servers> servers = Lists.slist;
                    Servers ser = servers.get(server_id);
                    server_background.setColorFilter(Color.parseColor("#" + ser.getColor()) - 16777216);
                    server_item_image.setColorFilter(Color.parseColor("#" + ser.getColor()) - 16777216);
                    select_layout.setVisibility(View.GONE);
                    serverinfo_layout.setVisibility(View.VISIBLE);
                    serverinfo_name.setText(ser.getName());
                    if (MainActivity.nickName == null) {
                        serverinfo_person_card.setCardBackgroundColor(-1711292128);
                        serverinfo_person_text.setText("Нажмите \"Играть\" и создайте персонажа");
                        serverinfo_person_name.setText("");
                        serverinfo_person_name.setVisibility(View.GONE);
                        details_layout.setVisibility(View.GONE);
                    } else {
                        serverinfo_person_card.setCardBackgroundColor(-1725591005);
                        serverinfo_person_text.setText("Персонаж: ");
                        serverinfo_person_name.setText(MainActivity.nickName);
                        serverinfo_person_name.setVisibility(View.VISIBLE);
                        UpdateDetales();
                    }
                }
            }
        } else {
            server_background.setColorFilter(-8637479);
            server_item_image.setColorFilter(-8637479);
            select_layout.setVisibility(View.VISIBLE);
            serverinfo_layout.setVisibility(View.GONE);
        }
    }

    public void UpdateDetales() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api-free.edgars.site/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Interface sInterface = retrofit.create(Interface.class);

        Call<List<Details>> call = sInterface.getAccountDetails(Lists.accountDetailsUrl, MainActivity.nickName);

        call.enqueue(new Callback<List<Details>>() {
            @Override
            public void onResponse(Call<List<Details>> call, Response<List<Details>> response) {

                List<Details> details = response.body();
                for (Details detail : details) {
                    Lists.detailslist.add(new Details(detail.getStatus(), detail.getName(), detail.getSkin(), detail.getSex(), detail.getLvl(), detail.getExp(), detail.getNeedExp(), detail.getCash(), detail.getBank(), detail.getNumber(), detail.getFraction(), detail.getRank(), detail.getPromo(), detail.getAdmin(), detail.getHouse(), detail.getDonate(), detail.getBusiness()));

                    SpannableString spannableString = new SpannableString(detail.getLvl());
                    spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, spannableString.length(), 33);
                    info_level.setText(TextUtils.concat("Уровень: ", spannableString));
                    SpannableString spannableString2 = new SpannableString(detail.getExp() + "/" + detail.getNeedExp());
                    spannableString2.setSpan(new StyleSpan(Typeface.BOLD), 0, spannableString2.length(), 33);
                    info_exp.setText(TextUtils.concat("Exp: ", spannableString2));
                    SpannableString spannableString3 = new SpannableString(detail.getCash());
                    spannableString3.setSpan(new StyleSpan(Typeface.BOLD), 0, spannableString3.length(), 33);
                    info_money.setText(TextUtils.concat("Денег: ", spannableString3));
                    SpannableString spannableString4 = new SpannableString(detail.getBank());
                    spannableString4.setSpan(new StyleSpan(Typeface.BOLD), 0, spannableString4.length(), 33);
                    info_bank.setText(TextUtils.concat("Денег в банке: ", spannableString4));
                    SpannableString spannableString5 = new SpannableString(detail.getFraction());
                    spannableString5.setSpan(new StyleSpan(Typeface.BOLD), 0, spannableString5.length(), 33);
                    int i = Integer.parseInt(detail.getRank());
                    if (i <= 0) {
                        info_fraction.setText(TextUtils.concat("Фракция: ", spannableString5));
                    } else {
                        SpannableString spannableString6 = new SpannableString(detail.getRank());
                        spannableString6.setSpan(new StyleSpan(Typeface.BOLD), 0, spannableString6.length(), 33);
                        info_fraction.setText(TextUtils.concat("Фракция: ", spannableString5, ". Ранг: ", spannableString6));
                    }
                    SpannableString spannableString7 = new SpannableString(detail.getHouse());
                    spannableString7.setSpan(new StyleSpan(Typeface.BOLD), 0, spannableString7.length(), 33);
                    info_house.setText(TextUtils.concat("Дом/квартира: ", spannableString7));
                    SpannableString spannableString8 = new SpannableString(detail.getBusiness());
                    spannableString8.setSpan(new StyleSpan(Typeface.BOLD), 0, spannableString8.length(), 33);
                    info_biz1.setText(TextUtils.concat("Бизнес: ", spannableString8));
                    /*if (string4.isEmpty()) {
                        i.this.G.setVisibility(8);
                        i.this.H.setVisibility(8);
                    } else {
                        i.this.G.setVisibility(0);
                        i.this.H.setVisibility(0);
                        SpannableString spannableString9 = new SpannableString(string4);
                        spannableString9.setSpan(new StyleSpan(Typeface.BOLD), 0, spannableString9.length(), 33);
                        i.this.I.setText(TextUtils.concat("Бизнес: ", spannableString9));
                    }*/
                    SpannableString spannableString10 = new SpannableString(detail.getDonate() + " руб.");
                    spannableString10.setSpan(new StyleSpan(Typeface.BOLD), 0, spannableString10.length(), 33);
                    info_donate.setText(TextUtils.concat("Донат счёт: ", spannableString10));
                    SpannableString spannableString11 = new SpannableString(detail.getNumber());
                    spannableString11.setSpan(new StyleSpan(Typeface.BOLD), 0, spannableString11.length(), 33);
                    info_phone.setText(TextUtils.concat("Номер телефона: ", spannableString11));

                    Glide.with(MainActivity.getMainActivity()).load(Lists.skinsCDNUrl + detail.getSkin() + ".png").into(info_skin);

                }
                if (details_layout.getVisibility() == View.GONE) {
                    details_layout.clearAnimation();
                    details_layout.setVisibility(View.VISIBLE);
                    details_layout.setAlpha(0.0f);
                    details_layout.animate().alpha(1.0f).setDuration(300L).start();
                }
            }

            @Override
            public void onFailure(Call<List<Details>> call, Throwable t) {
                Toast.makeText(MainActivity.getMainActivity(), "Ошибка при получении данных" + Lists.accountDetailsUrl, Toast.LENGTH_SHORT).show();
                System.out.println(t.toString());
            }
        });
    }

    public void show() {
        mHandler.removeCallbacksAndMessages(null);
        Point f10 = MainActivity.getPointSz(MainActivity.getMainActivity().getWindowManager().getDefaultDisplay());
        viewGroup.clearAnimation();
        viewGroup.setAlpha(0.0f);
        viewGroup.setVisibility(View.VISIBLE);
        viewGroup.animate().alpha(1.0f).setDuration(300L).start();
        header_layout.clearAnimation();
        header_layout.setAlpha(0.0f);
        header_layout.setTranslationY(-f10.y);
        header_layout.animate().setDuration(300L).alpha(1.0f).translationY(0.0f).start();
        //mHandler.post(new anim(2));
        server_layout.clearAnimation();
        server_layout.setAlpha(0.0f);
        server_layout.setTranslationY(-f10.y);
        server_layout.animate().setDuration(300L).translationY(0.0f).alpha(1.0f).start();
        //mHandler.postDelayed(new anim(3), 150L);
        footer_layout.clearAnimation();
        footer_layout.setAlpha(0.0f);
        footer_layout.setTranslationY(f10.y);
        footer_layout.animate().setDuration(300L).alpha(1.0f).translationY(0.0f).start();
        //mHandler.post(new anim(4));
        if (MainActivity.nickName != null) {
            details_layout.clearAnimation();
            details_layout.setVisibility(View.VISIBLE);
            details_layout.setAlpha(0.0f);
            details_layout.animate().alpha(1.0f).setDuration(300L).start();
        }
    }

    public void hide() {
        mHandler.removeCallbacksAndMessages(null);
        Point f10 = MainActivity.getPointSz(MainActivity.getMainActivity().getWindowManager().getDefaultDisplay());
        header_layout.clearAnimation();
        header_layout.setAlpha(1.0f);
        header_layout.setTranslationY(0.0f);
        header_layout.animate().setDuration(300L).translationY(-f10.y).start();
        server_layout.clearAnimation();
        server_layout.setAlpha(1.0f);
        server_layout.setTranslationY(0.0f);
        server_layout.animate().setDuration(300L).alpha(0.0f).start();
        details_layout.clearAnimation();
        details_layout.setAlpha(1.0f);
        details_layout.setTranslationY(0.0f);
        details_layout.animate().setDuration(300L).alpha(0.0f).start();
        footer_layout.clearAnimation();
        footer_layout.setAlpha(1.0f);
        footer_layout.setTranslationY(0.0f);
        footer_layout.animate().setDuration(300L).translationY(f10.y).alpha(0.0f).start();
        viewGroup.clearAnimation();
        viewGroup.setAlpha(1.0f);
        viewGroup.setVisibility(View.VISIBLE);
        viewGroup.animate().alpha(0.0f).setDuration(300L);
        mHandler.postDelayed(new ssetVisibility(), 300L);
    }

    public class ssetVisibility implements Runnable {
        public ssetVisibility() {
        }

        @Override
        public final void run() {
            viewGroup.setVisibility(View.GONE);
        }
    }

    public class anim implements Runnable {
        int t;

        public anim(int y) {
            t = y;
        }

        @Override
        public void run() {
            if (t == 2) {
                header_layout.setTranslationY(-(header_layout.getHeight()));
                header_layout.animate().setDuration(150L).translationY(0.0f).alpha(1.0f).start();
            } else if (t == 3) {
                server_layout.setTranslationY(-server_layout.getHeight());
                server_layout.animate().setDuration(150L).translationY(0.0f).alpha(1.0f).start();
            } else if (t == 4) {
                footer_layout.setTranslationY(footer_layout.getHeight());
                footer_layout.animate().setDuration(300L).translationY(0.0f).alpha(1.0f).start();
            }
        }
    }
}
