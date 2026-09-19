package com.brilliant.game.launcher.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.brilliant.game.R;
import com.brilliant.game.launcher.common.BaseActivity;
import com.brilliant.game.launcher.web.SocialApi;

import org.ini4j.Wini;

import java.io.File;
import java.io.IOException;

/**
 * Экран настроек — портирован из x32 (SettingsActivity.java / activity_settings.xml).
 * Ник теперь задаётся здесь (не на главном экране), пишется в тот же
 * settings.ini/[client]/name, который читает HomeActivity перед подключением.
 */
public class SettingsActivity extends BaseActivity {
    private static final String TAG = "SettingsActivity";

    private Button backButton;
    private Button reinstallButton;
    private EditText nicknameEditText;
    private ImageView telegramButton;
    private ImageView vkButton;
    private ImageView discordButton;

    private Wini mWini = null;
    private boolean isLoadingNickname = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        backButton = findViewById(R.id.brp_launcher_settings_back);
        reinstallButton = findViewById(R.id.brp_launcher_reinstall);
        nicknameEditText = findViewById(R.id.brp_launcher_settings_nick);
        telegramButton = findViewById(R.id.imageViewTelegram);
        vkButton = findViewById(R.id.imageViewVK);
        discordButton = findViewById(R.id.imageViewDiscord);

        loadNickname();

        nicknameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Пока идёт программная подстановка сохранённого ника в loadNickname() —
                // не пытаемся тут же его "сохранить" повторно.
                if (!isLoadingNickname) {
                    saveNickname(s.toString());
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                finish();
            }
        });

        reinstallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoadGameActivity.class);
                intent.putExtra("is_reinstall", true);
                startActivity(intent);
                finish();
            }
        });

        telegramButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLink(SocialApi.Social.TELEGRAM);
            }
        });

        vkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLink(SocialApi.Social.VK);
            }
        });

        discordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLink(SocialApi.Social.DISCORD);
            }
        });
    }

    private void openLink(String url) {
        try {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            if (browserIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(browserIntent);
            } else {
                Toast.makeText(this, "Нет приложения для открытия ссылки", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error opening link: " + e.getMessage());
        }
    }

    private void loadNickname() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    File externalDir = getExternalFilesDir(null);
                    if (externalDir == null) return;

                    File sampDir = new File(externalDir, "Samp");
                    if (!sampDir.exists()) sampDir.mkdirs();

                    File file = new File(sampDir, "settings.ini");
                    mWini = new Wini(file);
                    final String nickname = mWini.get("client", "name");

                    if (nickname != null && !nickname.isEmpty()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                isLoadingNickname = true;
                                nicknameEditText.setText(nickname);
                                isLoadingNickname = false;
                            }
                        });
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Error loading nickname: " + e.getMessage());
                } catch (Exception e) {
                    Log.e(TAG, "Error accessing settings.ini: " + e.getMessage());
                }
            }
        }).start();
    }

    private void saveNickname(final String nickname) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    File externalDir = getExternalFilesDir(null);
                    if (externalDir == null) return;

                    File sampDir = new File(externalDir, "Samp");
                    if (!sampDir.exists()) sampDir.mkdirs();

                    File file = new File(sampDir, "settings.ini");
                    if (mWini == null) {
                        mWini = new Wini(file);
                    }
                    mWini.put("client", "name", nickname);
                    mWini.store();
                } catch (IOException e) {
                    Log.e(TAG, "Error saving nickname: " + e.getMessage());
                } catch (Exception e) {
                    Log.e(TAG, "Error accessing settings.ini for save: " + e.getMessage());
                }
            }
        }).start();
    }
}
