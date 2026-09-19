package com.brilliant.game.launcher.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.brilliant.game.R;
import com.brilliant.game.core.Samp;
import com.brilliant.game.launcher.common.BaseActivity;
import com.brilliant.game.launcher.web.SocialApi;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.ini4j.Wini;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HomeActivity extends BaseActivity {
    private static final String TAG = "HomeActivity";
    private final Handler handler = new Handler(Looper.getMainLooper());
    private FirebaseAnalytics mFirebaseAnalytics;

    private Button playButton;
    private Button settingsButton;
    private TextView donateButton;

    private ConstraintLayout rootLayout;

    private Wini mWini = null;

    private boolean isHudShown = false;

    private static final int ANIMATION_DURATION = 100;
    private static final float SCALE_NORMAL = 1.0f;
    private static final float SCALE_PRESSED = 0.92f;
    private androidx.recyclerview.widget.RecyclerView rvNews;
    private androidx.recyclerview.widget.RecyclerView rvServers;
    private com.brilliant.game.launcher.adapter.NewsAdapter newsAdapter;
    private com.brilliant.game.launcher.adapter.ServersAdapter serversAdapter;

    private static final String APK_INFO_URL = "https://bkuzn.ru/bk/last_apk_file_info.json";
    private static final String VERSION_DIR = Environment.getExternalStorageDirectory() + "/Android/data/com.brilliant.game/files/";
    private static final String VERSION_FILE = VERSION_DIR + "apk_version.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate started");

        try {
            setContentView(R.layout.launcher_home_activity);

            Log.d(TAG, "ContentView set");

            initFirebaseAnalytics();
            initViews();
            setupClickListeners();
            setupContentRibbons();

            if (isFromGameReturn()) {
                checkHudStatus();
            }

            checkCacheOnStart();

            checkForApkUpdates();

            Log.d(TAG, "onCreate completed successfully");

        } catch (Exception e) {
            Log.e(TAG, "Critical error in onCreate: " + e.getMessage(), e);
            Toast.makeText(this, "Ошибка запуска приложения", Toast.LENGTH_LONG).show();
            finishAffinity();
        }
    }

    private boolean isFromGameReturn() {
        SharedPreferences prefs = getSharedPreferences("game_prefs", Context.MODE_PRIVATE);
        boolean fromGame = prefs.getBoolean("from_game_return", false);
        if (fromGame) {
            prefs.edit().remove("from_game_return").apply();
        }
        return fromGame;
    }

    private void initFirebaseAnalytics() {
        try {
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
            if (mFirebaseAnalytics != null) {
                trackAppOpen();
            } else {
                Log.w(TAG, "FirebaseAnalytics is null");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting FirebaseAnalytics: " + e.getMessage());
            mFirebaseAnalytics = null;
        }
    }

    private void checkCacheOnStart() {
        try {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!isCacheInstalled()) {
                        startCacheInstallation(false);
                    }
                }
            }, 1500);
        } catch (Exception e) {
            Log.e(TAG, "Error in checkCacheOnStart: " + e.getMessage());
        }
    }

    private void startCacheInstallation(boolean isReinstall) {
        try {
            trackCacheInstallStarted();
            Intent installIntent = new Intent(HomeActivity.this, LoadGameActivity.class);
            installIntent.putExtra("is_reinstall", isReinstall);
            startActivity(installIntent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        } catch (Exception e) {
            Log.e(TAG, "Error in startCacheInstallation: " + e.getMessage());
            Toast.makeText(this, "Ошибка запуска установки кэша", Toast.LENGTH_SHORT).show();
        }
    }

    public void onHudShown() {
        Log.d(TAG, "HUD shown callback received");
        isHudShown = true;

        try {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 1000);
        } catch (Exception e) {
            Log.e(TAG, "Error in onHudShown: " + e.getMessage());
        }
    }

    private void checkHudStatus() {
        try {
            SharedPreferences prefs = getSharedPreferences("game_prefs", Context.MODE_PRIVATE);
            if (prefs.getBoolean("hud_shown", false)) {
                Log.d(TAG, "HUD status checked - HUD was shown");
                prefs.edit().remove("hud_shown").apply();
                onHudShown();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in checkHudStatus: " + e.getMessage());
        }
    }

    private void trackAppOpen() {
        if (mFirebaseAnalytics == null) {
            Log.w(TAG, "FirebaseAnalytics is null, skipping app open event");
            return;
        }

        try {
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, "HomeActivity");
            bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "HomeActivity");
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, bundle);
            Log.d(TAG, "App open event logged");
        } catch (Exception e) {
            Log.e(TAG, "Error logging app open: " + e.getMessage());
        }
    }

    private void trackButtonClick(String buttonName) {
        if (mFirebaseAnalytics == null) {
            Log.w(TAG, "FirebaseAnalytics is null, skipping button click event");
            return;
        }

        try {
            Bundle bundle = new Bundle();
            bundle.putString("button_name", buttonName);
            bundle.putString("screen", "HomeActivity");
            mFirebaseAnalytics.logEvent("button_click", bundle);
            Log.d(TAG, "Button click: " + buttonName);
        } catch (Exception e) {
            Log.e(TAG, "Error logging button click: " + e.getMessage());
        }
    }

    private void trackGameLaunch() {
        if (mFirebaseAnalytics == null) {
            Log.w(TAG, "FirebaseAnalytics is null, skipping game launch event");
            return;
        }

        try {
            Bundle bundle = new Bundle();
            bundle.putBoolean("cache_installed", isCacheInstalled());
            mFirebaseAnalytics.logEvent("game_launch", bundle);
            Log.d(TAG, "Game launch event logged");
        } catch (Exception e) {
            Log.e(TAG, "Error logging game launch: " + e.getMessage());
        }
    }

    private void initViews() {
        try {
            playButton = findViewById(R.id.brp_launcher_play);
            settingsButton = findViewById(R.id.brp_launcher_settings_btn);
            donateButton = findViewById(R.id.brp_launcher_donate);

            rvNews = findViewById(R.id.rvNews);
            rvServers = findViewById(R.id.rvServers);

            rootLayout = findViewById(R.id.launcher_home_root);

            if (rootLayout == null) {
                rootLayout = this.findViewById(android.R.id.content);
            }

            if (playButton == null) Log.w(TAG, "playButton not found");
            if (settingsButton == null) Log.w(TAG, "settingsButton not found");
            if (donateButton == null) Log.w(TAG, "donateButton not found");

        } catch (Exception e) {
            Log.e(TAG, "Error in initViews: " + e.getMessage());
            Toast.makeText(this, "Ошибка инициализации интерфейса", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupContentRibbons() {
        try {
            if (rvNews != null) {
                rvNews.setHasFixedSize(true);
                rvNews.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(
                        this, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false));
                newsAdapter = new com.brilliant.game.launcher.adapter.NewsAdapter(
                        this, com.brilliant.game.launcher.model.Lists.nlist);
                rvNews.setAdapter(newsAdapter);
            }

            if (rvServers != null) {
                rvServers.setHasFixedSize(true);
                rvServers.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(
                        this, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false));
                serversAdapter = new com.brilliant.game.launcher.adapter.ServersAdapter(
                        this, com.brilliant.game.launcher.model.Lists.slist);
                rvServers.setAdapter(serversAdapter);
            }

            com.brilliant.game.launcher.web.ContentLoader.loadAll(new com.brilliant.game.launcher.web.ContentLoader.Callback() {
                @Override
                public void onNewsLoaded(boolean success) {
                    if (newsAdapter != null) newsAdapter.notifyDataSetChanged();
                    if (!success) Log.w(TAG, "Не удалось загрузить новости (проверь LauncherApi.getNewsInfo())");
                }

                @Override
                public void onServersLoaded(boolean success) {
                    if (serversAdapter != null) serversAdapter.notifyDataSetChanged();
                    if (!success) Log.w(TAG, "Не удалось загрузить список серверов (проверь LauncherApi.getServersListInfo())");
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error in setupContentRibbons: " + e.getMessage());
        }
    }

    private boolean isCacheInstalled() {
        try {
            File[] possiblePaths = {
                    new File(Environment.getExternalStorageDirectory() + "/Android/data/com.brilliant.game/files/version.txt"),
                    new File(getExternalFilesDir(null), "version.txt"),
                    new File(getFilesDir(), "version.txt")
            };

            for (File file : possiblePaths) {
                if (file.exists()) {
                    Log.d(TAG, "Cache found at: " + file.getAbsolutePath());
                    return true;
                }
            }

            return false;
        } catch (Exception e) {
            Log.e(TAG, "Error checking cache: " + e.getMessage());
            return false;
        }
    }

    /**
     * Улучшенная анимация нажатия с обратным вызовом
     */
    private void applyButtonAnimation(View button, final Runnable onClickAction) {
        if (button == null) return;

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                v.animate()
                        .scaleX(SCALE_PRESSED)
                        .scaleY(SCALE_PRESSED)
                        .setDuration(ANIMATION_DURATION)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                v.animate()
                                        .scaleX(SCALE_NORMAL)
                                        .scaleY(SCALE_NORMAL)
                                        .setDuration(ANIMATION_DURATION)
                                        .withEndAction(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (onClickAction != null) {
                                                    onClickAction.run();
                                                }
                                            }
                                        })
                                        .start();
                            }
                        })
                        .start();
            }
        });
    }

    private void setupClickListeners() {
        applyButtonAnimation(playButton, new Runnable() {
            @Override
            public void run() {
                handlePlayButtonClick();
            }
        });

        applyButtonAnimation(donateButton, new Runnable() {
            @Override
            public void run() {
                handleDonateButtonClick();
            }
        });

        applyButtonAnimation(settingsButton, new Runnable() {
            @Override
            public void run() {
                trackButtonClick("settings_button");
                startActivity(new Intent(HomeActivity.this, SettingsActivity.class));
            }
        });
    }

    private void handlePlayButtonClick() {
        try {
            trackButtonClick("play_button");

            String nickname = readSavedNickname();
            if (nickname == null || nickname.trim().isEmpty()) {
                Toast.makeText(HomeActivity.this,
                        "Сначала задайте ник в настройках",
                        Toast.LENGTH_LONG).show();
                return;
            }

            if (!isCacheInstalled()) {
                startCacheInstallation(false);
            } else {
                trackGameLaunch();
                try {
                    File externalDir = getExternalFilesDir(null);
                    if (externalDir != null) {
                        File sampDir = new File(externalDir, "Samp");
                        if (!sampDir.exists()) {
                            sampDir.mkdirs();
                        }

                        File file = new File(sampDir, "settings.ini");
                        if (mWini == null) {
                            mWini = new Wini(file);
                        }
                        mWini.put("client", "host", "185.207.214.14");
                        mWini.put("client", "port", "3277");
                        mWini.store();
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Error saving server host/port: " + e.getMessage());
                } catch (Exception e) {
                    Log.e(TAG, "Error accessing settings.ini for server write: " + e.getMessage());
                }

                Intent gameIntent = new Intent(HomeActivity.this, Samp.class);
                startActivity(gameIntent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in play button click: " + e.getMessage());
            Toast.makeText(this, "Ошибка при запуске игры", Toast.LENGTH_SHORT).show();
        }
    }

    private String readSavedNickname() {
        try {
            File externalDir = getExternalFilesDir(null);
            if (externalDir == null) return null;

            File file = new File(externalDir, "Samp/settings.ini");
            if (!file.exists()) return null;

            if (mWini == null) {
                mWini = new Wini(file);
            }
            return mWini.get("client", "name");
        } catch (Exception e) {
            Log.e(TAG, "Error reading saved nickname: " + e.getMessage());
            return null;
        }
    }

    private void handleDonateButtonClick() {
        try {
            trackButtonClick("donate_button");

            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(SocialApi.Social.DONATE_URL));
            if (browserIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(browserIntent);
            } else {
                Toast.makeText(this, "Нет браузера для открытия ссылки", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in donate button click: " + e.getMessage());
        }
    }

    private void trackCacheInstallStarted() {
        if (mFirebaseAnalytics == null) {
            Log.w(TAG, "FirebaseAnalytics is null, skipping cache install event");
            return;
        }

        try {
            Bundle bundle = new Bundle();
            bundle.putString("install_type", "cache_installation");
            mFirebaseAnalytics.logEvent("install_started", bundle);
            Log.d(TAG, "Cache install started");
        } catch (Exception e) {
            Log.e(TAG, "Error logging install start: " + e.getMessage());
        }
    }

    private void checkForApkUpdates() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d(TAG, "Checking for APK updates...");

                    ApkInfo latestApkInfo = getLatestApkInfo();

                    if (latestApkInfo == null) {
                        Log.e(TAG, "Failed to get latest APK info");
                        return;
                    }

                    if (isUpdateNeeded(latestApkInfo)) {
                        Log.d(TAG, "Update available! Latest version: " + latestApkInfo.versionCode);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                startApkUpdate(latestApkInfo);
                            }
                        });
                    } else {
                        Log.d(TAG, "No update needed. Current version is up to date.");
                    }

                } catch (Exception e) {
                    Log.e(TAG, "Error checking for updates: " + e.getMessage());
                }
            }
        }).start();
    }

    private ApkInfo getLatestApkInfo() {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(APK_INFO_URL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.connect();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.e(TAG, "HTTP error: " + connection.getResponseCode());
                return null;
            }

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            JSONObject json = new JSONObject(response.toString());
            String versionName = json.getString("versionName");
            int versionCode = json.getInt("versionCode");
            String fileName = json.getString("fileName");
            String fileUrl = json.getString("fileUrl");

            return new ApkInfo(versionName, versionCode, fileName, fileUrl);

        } catch (Exception e) {
            Log.e(TAG, "Error getting APK info: " + e.getMessage());
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private boolean isUpdateNeeded(ApkInfo newApkInfo) {
        try {
            int currentVersionCode = getPackageManager()
                    .getPackageInfo(getPackageName(), 0).versionCode;

            Log.d(TAG, "Current version code: " + currentVersionCode);
            Log.d(TAG, "Latest version code: " + newApkInfo.versionCode);

            return newApkInfo.versionCode > currentVersionCode;

        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Error getting current version: " + e.getMessage());
            return false;
        }
    }

    private void startApkUpdate(ApkInfo apkInfo) {
        try {
            trackApkUpdateStarted();

            Intent apkUpdateIntent = new Intent(HomeActivity.this, LoadApkActivity.class);
            apkUpdateIntent.putExtra("is_reinstall", true);
            apkUpdateIntent.putExtra("apk_version_name", apkInfo.versionName);
            apkUpdateIntent.putExtra("apk_version_code", apkInfo.versionCode);
            apkUpdateIntent.putExtra("apk_file_url", apkInfo.fileUrl);
            startActivity(apkUpdateIntent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();

        } catch (Exception e) {
            Log.e(TAG, "Error starting APK update: " + e.getMessage());
            Toast.makeText(this, "Ошибка запуска обновления", Toast.LENGTH_SHORT).show();
        }
    }

    private void trackApkUpdateStarted() {
        if (mFirebaseAnalytics == null) return;

        try {
            Bundle bundle = new Bundle();
            bundle.putString("update_type", "apk_update");
            bundle.putString("auto_update", "true");
            mFirebaseAnalytics.logEvent("update_started", bundle);
            Log.d(TAG, "APK update started automatically");
        } catch (Exception e) {
            Log.e(TAG, "Error tracking APK update: " + e.getMessage());
        }
    }

    private static class ApkInfo {
        String versionName;
        int versionCode;
        String fileName;
        String fileUrl;

        ApkInfo(String versionName, int versionCode, String fileName, String fileUrl) {
            this.versionName = versionName;
            this.versionCode = versionCode;
            this.fileName = fileName;
            this.fileUrl = fileUrl;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            handler.removeCallbacksAndMessages(null);
        } catch (Exception e) {
            Log.e(TAG, "Error in onDestroy: " + e.getMessage());
        }
    }
}