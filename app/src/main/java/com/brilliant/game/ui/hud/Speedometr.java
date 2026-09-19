package com.brilliant.game.ui.hud;

import android.app.Activity;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.brilliant.game.R;
import com.brilliant.game.core.Samp;
import com.brilliant.game.launcher.util.SeekArc;

public class Speedometr {
    private final int BUTTON_ENGINE = 0;
    private final int BUTTON_LIGHT = 1;
    private final int BUTTON_DOOR = 5;

    private View rootView;
    private boolean menuVisible = false;
    private FrameLayout customContainer;

    // ==== Точные id из x32 (bspeed_layout.xml) ====
    private ImageView speedBg;
    private ImageView speedArrow;
    private TextView speedText;
    private SeekArc speedLine;
    private TextView mileageText;

    private ImageView speedEngineIco;
    private ImageView speedLightIco;
    private ImageView speedBeltIco;
    private ImageView speedLockIco;

    private TextView fuelText;
    private SeekArc fuelLine;

    private TextView carHpText;
    private SeekArc carHpLine;

    private Handler notificationHandler = new Handler();
    private Runnable hideNotificationRunnable;

    native void nativeSendClick(int id);
    native void nativeInit();
    private Activity activity;

    public Speedometr(Activity activity) {
        this.activity = activity;
        nativeInit();

        rootView = LayoutInflater.from(activity).inflate(R.layout.speedometr, null);

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        speedBg = rootView.findViewById(R.id.speed_bg);
        speedArrow = rootView.findViewById(R.id.speed_arrow);
        speedText = rootView.findViewById(R.id.speed_text);
        speedLine = rootView.findViewById(R.id.speed_line);
        mileageText = rootView.findViewById(R.id.speed_mileage_text);

        speedEngineIco = rootView.findViewById(R.id.speed_engine_ico);
        speedLightIco = rootView.findViewById(R.id.speed_light_ico);
        speedBeltIco = rootView.findViewById(R.id.speed_belt_ico);
        speedLockIco = rootView.findViewById(R.id.speed_lock_ico);

        fuelText = rootView.findViewById(R.id.speed_fuel_text);
        fuelLine = rootView.findViewById(R.id.speed_fuel_line);

        carHpText = rootView.findViewById(R.id.speed_car_hp_text);
        carHpLine = rootView.findViewById(R.id.speed_car_hp_line);
    }

    private void setupClickListeners() {
        // В x32 клик по speed_bg открывает отдельное меню (SpeedometerMenu);
        // такого меню пока нет, поэтому сразу шлём команду, как раньше делал engine_start
        if (speedBg != null) {
            speedBg.setOnClickListener(v -> {
                nativeSendClick(BUTTON_ENGINE);
            });
        }
    }

    public void setContainer(FrameLayout container) {
        this.customContainer = container;
    }

    public void showMenu(boolean show) {
        menuVisible = show;
    }

    public void viewVisible(ViewGroup viewGroup, int view) {
        if (viewGroup != null) {
            viewGroup.setAlpha(view == View.VISIBLE ? 1.0f : 0.0f);
            viewGroup.setVisibility(view);
        }
    }

    public void show() {
        activity.runOnUiThread(() -> {
            if (rootView.getParent() != null) {
                ((ViewGroup) rootView.getParent()).removeView(rootView);
            }

            FrameLayout container = null;

            if (customContainer != null) {
                container = customContainer;
            } else {
                Samp samp = Samp.getInstance();
                if (samp != null) {
                    container = samp.getBackUILayout();
                }
            }

            if (container != null) {
                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT
                );
                lp.gravity = Gravity.BOTTOM;
                container.addView(rootView, lp);
                rootView.setVisibility(View.VISIBLE);
            } else {
                if (activity != null && activity.getWindow() != null) {
                    FrameLayout decorView = (FrameLayout) activity.getWindow().getDecorView();
                    FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.WRAP_CONTENT
                    );
                    lp.gravity = Gravity.BOTTOM;
                    decorView.addView(rootView, lp);
                    rootView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public void tempToggle(boolean toggle) {
        activity.runOnUiThread(() -> {
            rootView.setVisibility(toggle ? View.VISIBLE : View.GONE);
        });
    }

    public void hide() {
        activity.runOnUiThread(() -> {
            if (rootView.getParent() != null) {
                ((ViewGroup) rootView.getParent()).removeView(rootView);
            }
        });
    }

    private void showColoredNotification(String prefix, String suffix, boolean isOn) {
        // TODO: хуйня
    }

    private void hideNotification() {
        // TODO: хуйня
    }

    public void updateSpeed(int speed) {
        // Формула поворота стрелки — как в x32
        float rot = (float) (((((double) speed) * 0.938d) - 0.0341796875d) - 122);
        if (rot > 121.8f) {
            rot = 121.8f;
        }
        final float rotation = rot;

        activity.runOnUiThread(() -> {
            if (speedText != null) {
                speedText.setText(String.valueOf(speed));
            }
            if (speedArrow != null) {
                speedArrow.setRotation(rotation);
            }
            if (speedLine != null) {
                speedLine.setProgress(Math.min(speed, 260));
            }
        });
    }

    public void update(int fuel, int hp, int engine, int light, int lock, int mileage) {
        int hpPercent = Math.min(hp / 10, 100);
        if (hpPercent < 0) hpPercent = 0;
        if (fuel > 100) fuel = 100;
        else if (fuel < 0) fuel = 0;
        final int fuelLiters = fuel;
        final int hpPercentFinal = hpPercent;

        activity.runOnUiThread(() -> {
            if (speedEngineIco != null) {
                speedEngineIco.setBackgroundResource(engine == 1 ? R.drawable.ico_engine_on : R.drawable.ico_engine_off);
            }

            if (speedLightIco != null) {
                speedLightIco.setBackgroundResource(light == 1 ? R.drawable.ico_lights_on : R.drawable.ico_lights_off);
            }

            if (speedLockIco != null) {
                speedLockIco.setBackgroundResource(lock == 1 ? R.drawable.ico_lock_on : R.drawable.ico_lock_off);
            }

            // Ремень — данных с сервера нет (как и в x32, где belt всегда шлётся 0), оставляем "off"
            if (speedBeltIco != null) {
                speedBeltIco.setBackgroundResource(R.drawable.ico_seatbelt_off);
            }

            if (fuelLine != null) {
                fuelLine.setProgress(fuelLiters);
            }
            if (fuelText != null) {
                fuelText.setText(fuelLiters + " л");
            }

            if (carHpLine != null) {
                carHpLine.setProgress(hpPercentFinal);
            }
            if (carHpText != null) {
                carHpText.setText(hpPercentFinal + " %");
            }

            if (mileageText != null) {
                mileageText.setText(String.format("%06d", mileage));
            }
        });
    }
}