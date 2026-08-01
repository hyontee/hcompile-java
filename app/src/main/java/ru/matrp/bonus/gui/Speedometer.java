package ru.matrp.bonus.gui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.nvidia.devtech.NvEventQueueActivity;

import java.util.Formatter;

import ru.matrp.bonus.InterfacesManager;
import ru.matrp.bonus.R;
import ru.matrp.bonus.gui.util.CircularProgressBar;

public class Speedometer {
    public NvEventQueueActivity nvEventQueueActivity = null;
    public ViewGroup viewGroup = null;
    public TextView mCarHP;
    public FrameLayout mStrela;
    public FrameLayout mStrela2;
    public ImageView mEngine;
    public TextView mFuel;
    public ImageView mLight;
    public TextView[] textViews;
    public ImageView mLock;
    public TextView mMileage;
    public TextView mSpeed;
    public CircularProgressBar mSpeedLine;
    public ImageView povv, povv2;
    public int Pov, Pov2;

    native void sendClick(int clickId);

    public Speedometer(NvEventQueueActivity nvEventQueueActivity, int guiId) {
        this.nvEventQueueActivity = nvEventQueueActivity;
        viewGroup = InterfacesManager.getInterfacesManager().viewGroup[guiId];
        textViews = new TextView[11];
        show();
    }

    public void show() {
        if (viewGroup != null) {
            //Log.e("edgar", "view" + viewGroup.toString());
            return;
        }
        viewGroup = (ViewGroup) ((LayoutInflater) NvEventQueueActivity.getInstance().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.speedometer, (ViewGroup) null);
        NvEventQueueActivity.getInstance().getBackUILayout().addView(viewGroup, -2, -2);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) viewGroup.getLayoutParams();
        layoutParams.gravity = 81;
        viewGroup.setLayoutParams(layoutParams);

        for (int i10 = 0; i10 < 11; i10++) {
            TextView[] textViewArr = textViews;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("speed_");
            stringBuilder.append(i10);
            textViewArr[i10] = (TextView) viewGroup.findViewById(nvEventQueueActivity.getResources().getIdentifier(stringBuilder.toString(), "id", nvEventQueueActivity.getPackageName()));
        }

        mSpeed = viewGroup.findViewById(R.id.speed_text);
        mStrela = viewGroup.findViewById(R.id.turn_left);
        mStrela2 = viewGroup.findViewById(R.id.turn_right);
        mFuel = viewGroup.findViewById(R.id.fuel_text);
        mCarHP = viewGroup.findViewById(R.id.hp_text);
        mMileage = viewGroup.findViewById(R.id.mileage);
        mSpeedLine = viewGroup.findViewById(R.id.speed_progress);
        mEngine = viewGroup.findViewById(R.id.in_engine);
        mLock = viewGroup.findViewById(R.id.in_key);
        povv = viewGroup.findViewById(R.id.in_left);
        povv2 = viewGroup.findViewById(R.id.in_right);

        mStrela.setOnClickListener( view -> {
            if (Pov == 0)
            {
                view.startAnimation(AnimationUtils.loadAnimation(nvEventQueueActivity, R.anim.button_click));
                sendClick(0);
                povv.setColorFilter(Color.parseColor("#00FF00"), PorterDuff.Mode.SRC_IN);
                Pov = 1;
            }else{
                view.startAnimation(AnimationUtils.loadAnimation(nvEventQueueActivity, R.anim.button_click));
                sendClick(0);
                povv.setColorFilter(Color.parseColor("#FF0000"), PorterDuff.Mode.SRC_IN);
                Pov = 0;
            }
        });
        mStrela2.setOnClickListener( view -> {
            if (Pov2 == 0)
            {
                view.startAnimation(AnimationUtils.loadAnimation(nvEventQueueActivity, R.anim.button_click));
                //sendClick(1);
                povv2.setColorFilter(Color.parseColor("#00FF00"), PorterDuff.Mode.SRC_IN);
                Pov2 = 1;
            }else{
                view.startAnimation(AnimationUtils.loadAnimation(nvEventQueueActivity, R.anim.button_click));
                //sendClick(2);
                povv2.setColorFilter(Color.parseColor("#FF0000"), PorterDuff.Mode.SRC_IN);
                Pov2 = 0;
            }
        });
        InterfacesManager.getInterfacesManager().AnimVisibale(viewGroup, View.GONE);
    }

    public void UpdateSpeedInfo(int speed, int fuel, int hp, int mileage, int engine, int light, int belt, int lock) {
        hp= (int) hp/10;
        mFuel.setText(new Formatter().format("%d", Integer.valueOf(fuel)).toString());
        mMileage.setText(new Formatter().format("%06d", Integer.valueOf(mileage)).toString());
        mCarHP.setText(new Formatter().format("%d%s", Integer.valueOf(hp), "%").toString());
        mSpeedLine.setValue(Math.max(0, Math.min(200, speed)) * (mSpeedLine.getEndValue() / 200));
        for (int i12 = 0; i12 < 11; i12++) {
            int abs = Math.abs(Math.max(0, Math.min(200, speed)) - (i12 * 20));
            if (abs > 20) {
                textViews[i12].setAlpha(0.3f);
                textViews[i12].setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            } else {
                if (abs < 10) {
                    textViews[i12].setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                } else {
                    textViews[i12].setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                }
                textViews[i12].setAlpha(((1.0f - (abs / 20.0f)) * 0.7f) + 0.3f);
            }
        }
        //mSpeedLine.setProgressMax(1000); )float) ((int) speed)
       // mSpeedLine.setProgress(prog);
        mSpeed.setText(String.valueOf(speed));
        /*if(speed == 0)
            mSpeed.setAlpha((float) 0.5);
            mSpeed.setText("000");
            //mSpeed.setText("pososi");
            mSpeed.setTextColor(activity.getResources().getColor(R.color.black));
        if(speed > 0)
            mSpeed.setAlpha((float) 1.0);
            mSpeed.setText(String.valueOf(speed));
            mSpeed.setTextColor(activity.getResources().getColor(R.color.white));*/
        if(engine == 1)
            mEngine.setColorFilter(Color.parseColor("#00FF00"), PorterDuff.Mode.SRC_IN);
        else
            mEngine.setColorFilter(Color.parseColor("#FF0000"), PorterDuff.Mode.SRC_IN);
        if(lock == 1)
            mLock.setColorFilter(Color.parseColor("#00FF00"), PorterDuff.Mode.SRC_IN);
        else
            mLock.setColorFilter(Color.parseColor("#FF0000"), PorterDuff.Mode.SRC_IN);
    }

    public void ShowSpeed() {
        InterfacesManager.getInterfacesManager().showViewGroup(viewGroup);
    }

    public void HideSpeed() {
        InterfacesManager.getInterfacesManager().AnimVisibale(viewGroup, View.GONE);
    }
}
