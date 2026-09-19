package com.brilliant.game.ui.hud;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.joom.paranoid.Obfuscate;
import com.brilliant.game.R;
import com.brilliant.game.core.Samp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

@Obfuscate
public class Hud {
    private Activity activity;
    public boolean isShow;
    native void HudInit();
    private boolean d = false;
    public DecimalFormat formatter;
    private int current_real_money;
    private int current_visual_money;
    Thread thread_update_money;
    boolean shop_action = false;
    Timer money_timer;
    private boolean isHudSetPos = false;
    native void alt();
    native void N();
    native void onWeaponChanged();
    native void sendG();
    native void SetRadarBgPos(float x1, float y1, float x2, float y2);
    native void SetRadarPos(float x1, float y1, float size);
    private ArrayList<ImageView> hud_wanted;
    public ViewGroup viewGroup = null;


    private int lastHealth = -1;
    private int lastArmour = -1;

    private String playerName = "EGOR KUZN";
    private int playerId = 0;

    private ConstraintLayout mainHud;
    private ConstraintLayout hudInfoLayout;
    private ConstraintLayout healthLayout;
    private TextView healthText;
    private ConstraintLayout armourLayout;
    private TextView armourText;
    private ConstraintLayout eatLayout;
    private TextView eatText;
    private TextView hudMoney;
    private ConstraintLayout sampButtonsLayout;
    private ImageView hudButtonMenu;
    private ImageView hudButtonStar;
    private ImageView hudButtonInv;
    private ImageView hudButtonShop;
    private ImageView hudButtonHelp;
    private ImageView radarZone;
    private ConstraintLayout radarLayout;
    private TextView hudAmmo;
    private ConstraintLayout selectWeapon;
    private ImageView weaponButton;
    private ImageView star1;
    private ImageView star2;
    private ImageView star3;
    private ImageView star4;
    private ImageView star5;
    private ConstraintLayout hudLogoLayout;
    private ImageView hudLogoImg;
    private TextView hudLogoText;

    private LinearLayout brpHudLogo;
    private ImageView brpHudX2;
    private ConstraintLayout brpHudOnline;
    private TextView hudOnlineText;
    private ConstraintLayout brpHudMain;
    private ProgressBar hudHealthPb;
    private TextView hudHealthText;
    private ProgressBar hudArmourPb;
    private TextView hudArmourText;
    private ProgressBar hudEatPb;
    private TextView hudEatText;
    private TextView hudBalanceText;
    private ArrayList<ImageView> hudWantedX32;
    private ImageView hudFistIcon;
    private LinearLayout hudAmmoLayoutX32;
    private TextView hudAmmoTextX32;
    private TextView hudMaxAmmoTextX32;
    private ConstraintLayout brpHudDate;
    private TextView hudDateText;
    private ConstraintLayout brpHudTime;
    private TextView hudTimeText;
    private ImageView hudSeatButton;

    private static final int BUTTON_MENU = 0;
    private static final int BUTTON_STAR = 1;
    private static final int BUTTON_INV = 2;
    private static final int BUTTON_SHOP = 3;
    private static final int BUTTON_HELP = 4;

    private static final int BUTTON_ALT = 5;
    private static final int BUTTON_H = 6;
    private static final int BUTTON_N = 7;
    private static final int BUTTON_Y = 8;

    private ImageButton btnAlt;
    private ImageButton btnH;
    private ImageButton btnN;
    private ImageButton btnY;
    private ImageButton btnToggle;
    private boolean isExtendedOpen = false;


    private Handler handler = new Handler(Looper.getMainLooper());

    native void nativeClick(int buttonId);

    private boolean isTablet() {
        return (activity.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    @SuppressLint("ClickableViewAccessibility")
    public Hud(Activity activity) {
        this.activity = activity;

        viewGroup = activity.findViewById(R.id.main_hud);

        initViews();
        HudInit();

        loadPlayerNameFromSettings();

        updateLogoText();

        if (weaponButton != null) {
            weaponButton.setOnClickListener(v -> onWeaponChanged());
        }

        setupButtonWithAnimation(hudButtonMenu, BUTTON_MENU);
        setupButtonWithAnimation(hudButtonStar, BUTTON_STAR);
        setupButtonWithAnimation(hudButtonInv, BUTTON_INV);
        setupButtonWithAnimation(hudButtonShop, BUTTON_SHOP);
        setupButtonWithAnimation(hudButtonHelp, BUTTON_HELP);
        setupButtonWithAnimation(btnAlt, BUTTON_ALT);
        setupButtonWithAnimation(btnH, BUTTON_H);
        setupButtonWithAnimation(btnN, BUTTON_N);
        setupButtonWithAnimation(btnY, BUTTON_Y);

        if (btnToggle != null) {
            btnToggle.setOnClickListener(v -> {
                animateButtonPress(btnToggle);
                toggleExtendedButtons();
            });
        }


        formatter = new DecimalFormat();
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
        symbols.setGroupingSeparator(',');
        formatter.setDecimalFormatSymbols(symbols);

        hud_wanted = new ArrayList<>();
        if (star1 != null) hud_wanted.add(star1);
        if (star2 != null) hud_wanted.add(star2);
        if (star3 != null) hud_wanted.add(star3);
        if (star4 != null) hud_wanted.add(star4);
        if (star5 != null) hud_wanted.add(star5);
        for (int i = 0; i < hud_wanted.size(); i++) {
            hud_wanted.get(i).setVisibility(View.VISIBLE);
        }

        if (eatText != null) {
            eatText.setText("100%");
        }

        Animation animation = AnimationUtils.loadAnimation(activity, R.anim.button_click);

        if (brpHudOnline != null) {
            brpHudOnline.setOnClickListener(v -> {
                v.startAnimation(animation);
                ((Samp) activity).requestShowTab();
            });
        }

        if (hudFistIcon != null) {
            hudFistIcon.setOnClickListener(v -> onWeaponChanged());
        }

        if (hudSeatButton != null) {
            hudSeatButton.setOnClickListener(v -> {
                v.startAnimation(animation);
                sendG();
            });
        }


        isShow = false;
        viewVisible(viewGroup, View.GONE);
    }

    private void setupButtonWithAnimation(ImageView button, int buttonId) {
        if (button == null) return;
        button.setOnClickListener(v -> {
            animateButtonPress(button);
            nativeClick(buttonId);
        });
    }

    private void animateButtonPress(ImageView button) {
        button.animate()
                .scaleX(0.8f)
                .scaleY(0.8f)
                .setDuration(100)
                .withEndAction(() -> {
                    button.animate()
                            .scaleX(1.0f)
                            .scaleY(1.0f)
                            .setDuration(100)
                            .start();
                })
                .start();

        button.animate()
                .alpha(0.5f)
                .setDuration(100)
                .withEndAction(() -> {
                    button.animate()
                            .alpha(1.0f)
                            .setDuration(100)
                            .start();
                })
                .start();
    }

    private void setupButtonWithHighlight(ImageView button, int buttonId) {
        button.setOnClickListener(v -> {
            float originalAlpha = button.getAlpha();

            button.setColorFilter(0x88FFFFFF, android.graphics.PorterDuff.Mode.SRC_ATOP);
            button.animate()
                    .scaleX(0.9f)
                    .scaleY(0.9f)
                    .setDuration(80)
                    .withEndAction(() -> {
                        handler.postDelayed(() -> {
                            button.clearColorFilter();
                            button.animate()
                                    .scaleX(1.0f)
                                    .scaleY(1.0f)
                                    .setDuration(80)
                                    .start();
                        }, 50);
                    })
                    .start();

            nativeClick(buttonId);
        });
    }

    public void attachToContainer(FrameLayout container) {
        if (viewGroup != null && container != null) {
            container.addView(viewGroup, -1, -1);
        }
    }

    private void initViews() {
        mainHud = viewGroup.findViewById(R.id.main_hud);
        hudInfoLayout = viewGroup.findViewById(R.id.hud_info_layout);
        healthLayout = viewGroup.findViewById(R.id.health_layout);
        healthText = viewGroup.findViewById(R.id.health_text);
        armourLayout = viewGroup.findViewById(R.id.armour_layout);
        armourText = viewGroup.findViewById(R.id.armour_text);
        eatLayout = viewGroup.findViewById(R.id.eat_layout);
        eatText = viewGroup.findViewById(R.id.eat_text);
        hudMoney = viewGroup.findViewById(R.id.hud_money);
        sampButtonsLayout = viewGroup.findViewById(R.id.samp_buttons_layout);
        radarZone = viewGroup.findViewById(R.id.radar_zone);
        radarLayout = viewGroup.findViewById(R.id.radar_layout);
        btnToggle = viewGroup.findViewById(R.id.btn_toggle);
        btnAlt = viewGroup.findViewById(R.id.btn_alt);
        btnH = viewGroup.findViewById(R.id.btn_h);
        btnN = viewGroup.findViewById(R.id.btn_n);
        btnY = viewGroup.findViewById(R.id.btn_y);

        brpHudLogo = viewGroup.findViewById(R.id.brp_hud_logo);
        brpHudX2 = viewGroup.findViewById(R.id.brp_hud_x2);
        brpHudOnline = viewGroup.findViewById(R.id.brp_hud_online);
        hudOnlineText = viewGroup.findViewById(R.id.hud_online_text);
        brpHudMain = viewGroup.findViewById(R.id.brp_hud_main);
        hudHealthPb = viewGroup.findViewById(R.id.hud_health_pb);
        hudHealthText = viewGroup.findViewById(R.id.hud_health_text);
        hudArmourPb = viewGroup.findViewById(R.id.hud_armour_pb);
        hudArmourText = viewGroup.findViewById(R.id.hud_armour_text);
        hudEatPb = viewGroup.findViewById(R.id.hud_eat_pb);
        hudEatText = viewGroup.findViewById(R.id.hud_eat_text);
        hudBalanceText = viewGroup.findViewById(R.id.hud_balance_text);
        hudFistIcon = viewGroup.findViewById(R.id.hud_fist_icon);
        hudAmmoLayoutX32 = viewGroup.findViewById(R.id.hud_ammo_layout);
        hudAmmoTextX32 = viewGroup.findViewById(R.id.hud_ammo_text);
        hudMaxAmmoTextX32 = viewGroup.findViewById(R.id.hud_max_ammo_text);
        brpHudDate = viewGroup.findViewById(R.id.brp_hud_date);
        hudDateText = viewGroup.findViewById(R.id.hud_date_text);
        brpHudTime = viewGroup.findViewById(R.id.brp_hud_time);
        hudTimeText = viewGroup.findViewById(R.id.hud_time_text);
        hudSeatButton = viewGroup.findViewById(R.id.hud_seat_button);

        hudWantedX32 = new ArrayList<>();
        hudWantedX32.add((ImageView) viewGroup.findViewById(R.id.hud_star_1));
        hudWantedX32.add((ImageView) viewGroup.findViewById(R.id.hud_star_2));
        hudWantedX32.add((ImageView) viewGroup.findViewById(R.id.hud_star_3));
        hudWantedX32.add((ImageView) viewGroup.findViewById(R.id.hud_star_4));
        hudWantedX32.add((ImageView) viewGroup.findViewById(R.id.hud_star_5));
    }

    private String formatPlayerName(String rawName) {
        if (rawName == null || rawName.isEmpty()) {
            return "EGOR KUZN";
        }

        String formatted = rawName.replace('_', ' ');
        formatted = formatted.toUpperCase(Locale.getDefault());

        return formatted;
    }

    private void loadPlayerNameFromSettings() {
        try {
            File file = new File("/storage/emulated/0/Android/data/com.brilliant.game/files/Samp/settings.ini");

            if (file.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line;
                String rawName = null;

                while ((line = reader.readLine()) != null) {
                    if (line.trim().startsWith("name")) {
                        String[] parts = line.split("=");
                        if (parts.length >= 2) {
                            rawName = parts[1].trim();
                            Log.d("HUD", "Loaded raw player name from settings: " + rawName);
                            break;
                        }
                    }
                }
                reader.close();

                if (rawName == null || rawName.equals("Egor_Kuzn")) {
                    reader = new BufferedReader(new FileReader(file));
                    boolean inClientSection = false;

                    while ((line = reader.readLine()) != null) {
                        line = line.trim();

                        if (line.startsWith("[") && line.endsWith("]")) {
                            inClientSection = line.equalsIgnoreCase("[client]");
                            continue;
                        }

                        if (inClientSection && line.startsWith("name")) {
                            String[] parts = line.split("=");
                            if (parts.length >= 2) {
                                rawName = parts[1].trim();
                                Log.d("HUD", "Loaded raw player name from [client] section: " + rawName);
                                break;
                            }
                        }
                    }
                    reader.close();
                }

                if (rawName != null && !rawName.isEmpty()) {
                    playerName = formatPlayerName(rawName);
                    Log.d("HUD", "Formatted player name: " + playerName);
                } else {
                    playerName = "EGOR KUZN";
                }
            } else {
                Log.e("HUD", "Samp settings file not found: " + file.getAbsolutePath());
                playerName = "EGOR KUZN";
            }
        } catch (IOException e) {
            Log.e("HUD", "Error reading Samp settings: " + e.getMessage());
            playerName = "EGOR KUZN";
        }

        if (playerName.equals("EGOR KUZN")) {
            try {
                File file = new File(activity.getExternalFilesDir(null) + "/Samp/settings.ini");
                if (file.exists()) {
                    BufferedReader br = new BufferedReader(new FileReader(file));
                    String line;
                    String rawName = null;

                    while ((line = br.readLine()) != null) {
                        line = line.trim();
                        if (line.toLowerCase().contains("name") && line.contains("=")) {
                            String[] parts = line.split("=", 2);
                            if (parts.length == 2) {
                                rawName = parts[1].trim();
                                Log.d("HUD", "Fallback loaded raw player name: " + rawName);
                                break;
                            }
                        }
                    }
                    br.close();

                    if (rawName != null && !rawName.isEmpty()) {
                        playerName = formatPlayerName(rawName);
                        Log.d("HUD", "Fallback formatted player name: " + playerName);
                    }
                }
            } catch (Exception e) {
                Log.e("HUD", "Fallback error: " + e.getMessage());
            }
        }
    }

    private String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
        return sdf.format(Calendar.getInstance().getTime());
    }

    private void updateLogoText() {
        if (hudLogoText != null) {
            String logoText = String.format(Locale.getDefault(),
                    "ID %d: %s\n%s",
                    playerId,
                    playerName,
                    getCurrentDateTime());
            hudLogoText.setText(logoText);
        }
    }

    native void nativeBoolShowHud(boolean toggle);

    public void toggleAll(boolean toggle, boolean isChat) {
        isShow = toggle;
        nativeBoolShowHud(isShow);
        activity.runOnUiThread(() -> {
            System.out.println("\n[toggle] HUD: " + toggle);
            if (toggle) {
                viewVisible(viewGroup, View.VISIBLE);
                radarZone.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        radarZone.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        if (!isHudSetPos) {
                            SetRadarBgPos(radarZone.getX(), radarZone.getY(),
                                    radarZone.getWidth(), radarZone.getHeight());

                            int screenwidth = viewGroup.getWidth();
                            int screenheight = viewGroup.getHeight();

                            float real_prcX = ((radarZone.getX() + (radarZone.getWidth() / 2)) / screenwidth) * 100;
                            float real_prcY = ((radarZone.getY() + (radarZone.getHeight() / 2.2f)) / screenheight) * 100;

                            float gtaX = (640 * (real_prcX / 100f));
                            float gtaY = (480 * (real_prcY / 100f));

                            float sizeMultiplier = isTablet() ? 1.4f : 1.0f;
                            float size = 36.0f * sizeMultiplier;

                            SetRadarPos(gtaX, gtaY, size);

                            activity.runOnUiThread(() -> {
                                radarZone.setVisibility(View.INVISIBLE);
                            });
                            isHudSetPos = true;
                        }
                    }
                });
            } else {
                viewVisible(viewGroup, View.GONE);
            }
        });
    }

    public void updatePlayerInfo(String str, int i) {
        this.playerName = formatPlayerName(str);
        this.playerId = i;

        activity.runOnUiThread(() -> {
            if (hudLogoText != null) {
                String date = getCurrentDateTime();
                hudLogoText.setText("ID " + i + ": " + playerName + "\n" + date);
            }

            if (hudOnlineText != null) {
                hudOnlineText.setText(Integer.toString(i));
            }
            if (hudTimeText != null) {
                hudTimeText.setText(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(Calendar.getInstance().getTime()));
            }
            if (hudDateText != null) {
                hudDateText.setText(new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Calendar.getInstance().getTime()));
            }
        });
    }

    public void UpdateHudInfo(int health, int armour) {
        activity.runOnUiThread(() -> {
            if (lastHealth != health) {
                healthText.setText(health + "%");
                lastHealth = health;
            }

            if (lastArmour != armour) {
                armourText.setText(armour + "%");
                lastArmour = armour;
            }

            if (eatText != null) {
                eatText.setText("100%");
            }

            if (hudHealthPb != null) hudHealthPb.setProgress(Math.max(0, Math.min(100, health)));
            if (hudHealthText != null) hudHealthText.setText(Integer.toString(health));
            if (hudArmourPb != null) hudArmourPb.setProgress(Math.max(0, Math.min(100, armour)));
            if (hudArmourText != null) hudArmourText.setText(Integer.toString(armour));
            if (hudEatPb != null) hudEatPb.setProgress(100);
            if (hudEatText != null) hudEatText.setText("100");
        });
    }

    public void UpdateAmmo(int weaponid, int ammo, int ammoclip) {
        activity.runOnUiThread(() -> {
            if (weaponid == 0) {
                if (selectWeapon != null) selectWeapon.setVisibility(View.VISIBLE);
                if (hudAmmo != null) hudAmmo.setVisibility(View.GONE);
                if (weaponButton != null) weaponButton.setImageResource(R.drawable.weapon_0);

                if (hudFistIcon != null) hudFistIcon.setImageResource(R.drawable.weapon_0);
                if (hudAmmoLayoutX32 != null) hudAmmoLayoutX32.setVisibility(View.INVISIBLE);
                if (hudAmmoTextX32 != null) hudAmmoTextX32.setText("0");
                if (hudMaxAmmoTextX32 != null) hudMaxAmmoTextX32.setText("/0");
                return;
            }

            if (selectWeapon != null) selectWeapon.setVisibility(View.VISIBLE);
            if (hudAmmo != null) hudAmmo.setVisibility(View.VISIBLE);

            int id = activity.getResources().getIdentifier(
                    String.format("weapon_%d", weaponid), "drawable", activity.getPackageName());
            if (id != 0) {
                if (weaponButton != null) weaponButton.setImageResource(id);
                if (hudFistIcon != null) hudFistIcon.setImageResource(id);
            } else {
                if (weaponButton != null) weaponButton.setImageResource(R.drawable.weapon_0);
                if (hudFistIcon != null) hudFistIcon.setImageResource(R.drawable.weapon_0);
            }

            if (weaponid > 15 && weaponid < 44 && weaponid != 21) {
                String ss = String.format("%d<font color='#B0B0B0'> / %d</font>", ammoclip, ammo - ammoclip);
                if (hudAmmo != null) hudAmmo.setText(Html.fromHtml(ss));
            } else {
                if (hudAmmo != null) hudAmmo.setText("∞ / -");
            }

            if (hudAmmoLayoutX32 != null) hudAmmoLayoutX32.setVisibility(View.VISIBLE);
            if (hudAmmoTextX32 != null) hudAmmoTextX32.setText(String.valueOf(ammoclip));
            if (hudMaxAmmoTextX32 != null) hudMaxAmmoTextX32.setText("/" + (ammo - ammoclip));
        });
    }

    public void setMoneyImmediate(int money) {
        current_real_money = money;
        current_visual_money = money;
        activity.runOnUiThread(() -> {
            if (hudMoney != null && formatter != null) {
                hudMoney.setText("₽ " + formatter.format(money));
            }
            if (hudBalanceText != null && formatter != null) {
                hudBalanceText.setText(formatter.format(money));
            }
        });
    }

    public void updateMoney(int money) {
        setMoneyImmediate(money);
    }

    public void UpdateWanted(int wantedLVL) {
        Log.i("HUD::updateWanted", "Called method");
        activity.runOnUiThread(() -> {
            for (int i = 0; i < hud_wanted.size(); i++) {
                if (i < wantedLVL) {
                    hud_wanted.get(i).setVisibility(View.VISIBLE);
                } else {
                    hud_wanted.get(i).setVisibility(View.GONE);
                }
            }

            if (hudWantedX32 != null) {
                int wanted = Math.min(wantedLVL, 5);
                for (int i = 0; i < hudWantedX32.size(); i++) {
                    ImageView star = hudWantedX32.get(i);
                    if (star == null) continue;
                    if (i < wanted) {
                        star.setBackgroundResource(R.drawable.ic_y_star);
                    } else {
                        star.setImageResource(R.drawable.ic_star);
                        star.setBackground(null);
                    }
                }
            }
        });
    }

    public void toggleLogo(boolean toggle) {
        activity.runOnUiThread(() -> {
            if (hudLogoLayout == null) return;
            if (toggle) {
                hudLogoLayout.setVisibility(View.VISIBLE);
            } else {
                hudLogoLayout.setVisibility(View.GONE);
            }
        });
    }

    public void toggleSeatButton(boolean toggle) {
        activity.runOnUiThread(() -> {
            if (hudSeatButton == null) return;
            hudSeatButton.setVisibility(toggle ? View.VISIBLE : View.GONE);
        });
    }

    public void viewVisible(ViewGroup viewGroup, int view) {
        if (viewGroup != null) {
            viewGroup.setAlpha(view == View.VISIBLE ? 1.0f : 0.0f);
            viewGroup.setVisibility(view);
        }
    }
    private void toggleExtendedButtons() {
        isExtendedOpen = !isExtendedOpen;

        int visibility = isExtendedOpen ? View.VISIBLE : View.GONE;

        if (btnAlt != null) btnAlt.setVisibility(visibility);
        if (btnH != null) btnH.setVisibility(visibility);
        if (btnN != null) btnN.setVisibility(visibility);
        if (btnY != null) btnY.setVisibility(visibility);
    }
}