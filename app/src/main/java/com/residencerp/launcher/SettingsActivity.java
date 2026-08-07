package com.residencerp.launcher;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

public class SettingsActivity extends AppCompatActivity {

    private Prefs prefs;
    private CacheManager cacheManager;

    private EditText etNick;
    private ProgressBar progressBar;
    private TextView tvStatus;
    private Button btnSave;
    private Button btnReinstall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        prefs = new Prefs(this);
        cacheManager = new CacheManager(this);

        etNick = findViewById(R.id.etNick);
        progressBar = findViewById(R.id.progressBarSettings);
        tvStatus = findViewById(R.id.tvStatusSettings);
        btnSave = findViewById(R.id.btnSave);
        btnReinstall = findViewById(R.id.btnReinstall);
        Button btnBack = findViewById(R.id.btnBack);

        // Загружаем текущие значения
        etNick.setText(prefs.getNickname());

        btnSave.setOnClickListener(v -> saveSettings());
        btnReinstall.setOnClickListener(v -> confirmReinstall());
        btnBack.setOnClickListener(v -> finish());
    }

    private void saveSettings() {
        String nick = etNick.getText().toString().trim();

        if (TextUtils.isEmpty(nick)) {
            etNick.setError(getString(R.string.error_no_nick));
            return;
        }
        // Простая валидация ника: латиница, цифры, _ и [] длиной 3-20
        if (!nick.matches("[a-zA-Z0-9_\\[\\]]{3,20}")) {
            etNick.setError(getString(R.string.error_bad_nick));
            return;
        }

        prefs.setNickname(nick);
        Toast.makeText(this, R.string.settings_saved, Toast.LENGTH_SHORT).show();
        finish();
    }

    private void confirmReinstall() {
        // Ссылка на кэш зашита в коде (Prefs.DEFAULT_CACHE_URL)
        String url = Prefs.DEFAULT_CACHE_URL;

        new AlertDialog.Builder(this)
                .setTitle(R.string.reinstall_title)
                .setMessage(R.string.reinstall_msg)
                .setPositiveButton(R.string.reinstall, (d, w) -> reinstallCache(url))
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void reinstallCache(String url) {
        setUiLoading(true);
        prefs.setCacheInstalled(false);
        cacheManager.wipeCache();

        cacheManager.downloadAndInstall(url, new CacheManager.Callback() {
            @Override
            public void onProgress(int percent, String status) {
                if (percent > 0) progressBar.setProgress(percent);
                tvStatus.setText(status);
            }

            @Override
            public void onComplete(File cacheDir) {
                prefs.setCacheInstalled(true);
                setUiLoading(false);
                Toast.makeText(SettingsActivity.this, R.string.cache_done, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String message) {
                setUiLoading(false);
                new AlertDialog.Builder(SettingsActivity.this)
                        .setTitle(R.string.error_title)
                        .setMessage(message)
                        .setPositiveButton(android.R.string.ok, null)
                        .show();
            }
        });
    }

    private void setUiLoading(boolean loading) {
        btnSave.setEnabled(!loading);
        btnReinstall.setEnabled(!loading);
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        tvStatus.setVisibility(loading ? View.VISIBLE : View.GONE);
        if (loading) progressBar.setProgress(0);
    }
}
