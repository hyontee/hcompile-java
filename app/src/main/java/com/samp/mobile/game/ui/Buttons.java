package com.samp.mobile.game.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.View;
import android.view.animation.AnimationUtils;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.samp.mobile.R;
import com.samp.mobile.game.SAMP;
import com.samp.mobile.launcher.util.Util;

public class Buttons {
    private Activity activity;
    private ConstraintLayout buttonsLayout;
    private View changer, alt, tab, y, n, f;
    private Boolean m_bSpecialKeysState = false;
    private Boolean m_bLastState;

    @SuppressLint("WrongViewCast")
    public Buttons(Activity activity) {
        this.activity = activity;
        buttonsLayout = (ConstraintLayout) activity.getLayoutInflater().inflate(R.layout.wn_buttons, null);
        ((SAMP) activity).getParentLayout().addView(buttonsLayout, new ConstraintLayout.LayoutParams(-1, -1));
        changer = activity.findViewById(R.id.btn_sl);
        alt = activity.findViewById(R.id.btn_alt);
        tab = activity.findViewById(R.id.btn_tab);
        y = activity.findViewById(R.id.btn_y);
        n = activity.findViewById(R.id.btn_n);

        initClickers();
        toggleVisibility(false);
    }

    public void toggleVisibility(Boolean state) {
        if(state)
            Util.ShowLayout(buttonsLayout, true);
        else
            Util.HideLayout(buttonsLayout, true);
    }

    public void toggleSpecial(Boolean state) {
        Activity activity;
        int i;
        this.y.setVisibility(state.booleanValue() ? 0 : 8);
        this.n.setVisibility(state.booleanValue() ? 0 : 8);
        this.alt.setVisibility(state.booleanValue() ? 0 : 8);
        this.tab.setVisibility(state.booleanValue() ? 0 : 8);
        View view = this.changer;
        if (state.booleanValue()) {
            activity = this.activity;
            i = R.drawable.ic_hud_btn_sl;
        } else {
            activity = this.activity;
            i = R.drawable.ic_hud_btn_sr;
        }
        view.setBackground(Util.getRes(activity, i));
        this.m_bSpecialKeysState = Boolean.valueOf(!this.m_bSpecialKeysState.booleanValue());
    }

    private void initClickers() {
        changer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                changer.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.click));
                toggleSpecial(!m_bSpecialKeysState);
            }
        });
        alt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                alt.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.click));
                SAMP.getInstance().specialCall(0);
            }
        });
        tab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                tab.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.click));
                SAMP.getInstance().specialCall(1);
            }
        });
        y.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                y.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.click));
                SAMP.getInstance().specialCall(2);
            }
        });
        n.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                n.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.click));
                SAMP.getInstance().specialCall(3);
            }
        });
    }
}
