package com.saint.game.gui;

import android.app.Activity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.nvidia.devtech.NvEventQueueActivity;
import com.saint.game.R;
import com.saint.game.gui.util.Utils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class HudManager {
    public Activity activity;

    public Animation animation;

    public ConstraintLayout hud_layout;
    public ConstraintLayout hud_online;

    public ProgressBar hud_health;
    public ProgressBar hud_hunger;
    public ProgressBar hud_armour;

    public TextView hud_health_t;
    public TextView hud_hunger_t;
    public TextView hud_armour_t;

    public TextView hud_time;
    public TextView hud_date;
    public TextView hud_online_text;

    public TextView hud_money;

    public ImageView hud_weapon;

    public ArrayList<ImageView> hud_wanted;

    public HudManager(Activity aactivity) {
        activity = aactivity;

        Animation animation = AnimationUtils.loadAnimation(aactivity, R.anim.button_click);

        hud_layout = aactivity.findViewById(R.id.bhud_main);
        hud_layout.setVisibility(View.GONE);
        hud_online = aactivity.findViewById(R.id.brp_hud_online);

        hud_health = aactivity.findViewById(R.id.hud_health_pb);
        hud_hunger = aactivity.findViewById(R.id.hud_eat_pb);
        hud_armour = aactivity.findViewById(R.id.hud_armour_pb);

        hud_health_t = aactivity.findViewById(R.id.hud_health_text);
        hud_hunger_t = aactivity.findViewById(R.id.hud_eat_text);
        hud_armour_t = aactivity.findViewById(R.id.hud_armour_text);

        hud_money = aactivity.findViewById(R.id.hud_balance_text);
        hud_weapon = aactivity.findViewById(R.id.hud_fist_icon);

        hud_time = aactivity.findViewById(R.id.hud_time_text);
        hud_date = aactivity.findViewById(R.id.hud_date_text);
        hud_online_text = aactivity.findViewById(R.id.hud_online_text);

        hud_wanted = new ArrayList<>();
        hud_wanted.add(activity.findViewById(R.id.hud_star_1));
        hud_wanted.add(activity.findViewById(R.id.hud_star_2));
        hud_wanted.add(activity.findViewById(R.id.hud_star_3));
        hud_wanted.add(activity.findViewById(R.id.hud_star_4));
        hud_wanted.add(activity.findViewById(R.id.hud_star_5));

        hud_online.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(animation);
                openTab();
            }
        });
    }

    public void UpdateHudInfo(int health, int armour, int hunger, int weaponid, int ammo, int playerid, int money, int wanted)
    {
        hud_health.setProgress(health);
        hud_hunger.setProgress(hunger);
        hud_armour.setProgress(armour);

        hud_health_t.setText(Integer.toString(health));
        hud_armour_t.setText(Integer.toString(armour));
        hud_hunger_t.setText(Integer.toString(hunger));

        hud_online_text.setText(Integer.toString(playerid));

        DecimalFormat formatter = new DecimalFormat();
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
        symbols.setGroupingSeparator(' ');
        formatter.setDecimalFormatSymbols(symbols);
        String s = formatter.format(money).toString();
        hud_money.setText(String.valueOf(s));

        int id = activity.getResources().getIdentifier(new Formatter().format("weapon_%d", Integer.valueOf(weaponid)).toString(), "drawable", activity.getPackageName());
        hud_weapon.setImageResource(id);

        hud_weapon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NvEventQueueActivity.getInstance().onWeaponChanged();
            }
        });
        if(wanted > 5) wanted = 5;
        for (int i2 = 0; i2 < wanted; i2++) {
            hud_wanted.get(i2).setBackgroundResource(R.drawable.ic_y_star);
        }
        hud_time.setText(new SimpleDateFormat("HH:mm").format(Calendar.getInstance().getTime()));
        hud_date.setText(new SimpleDateFormat("dd.MM.yyyy").format(Calendar.getInstance().getTime()));
    }

    private void openTab()
    {
        Timer t = new Timer();
        t.schedule(new TimerTask(){
            @Override
            public void run() {
              //  NvEventQueueActivity.getInstance().showTab();
            }
        }, 300L);
    }

    public void ShowHud() {
        Utils.ShowLayout(hud_layout, false);
    }

    public void HideHud() {
        Utils.HideLayout(hud_layout, false);
    }

}
