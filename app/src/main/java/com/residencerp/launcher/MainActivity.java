package com.residencerp.launcher;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // ВАЖНО: package игрового клиента, который лаунчер запускает после установки кэша.
    // Для CRMP/SA-MP на базе GTA:SA обычно это "com.rockstargames.gtasa".
    // Поменяй на package своего клиента, если он другой.
    private static final String GAME_PACKAGE = "com.rockstargames.gtasa";

    private Prefs prefs;
    private CacheManager cacheManager;
    private final List<Server> servers = new ArrayList<>();

    private Spinner spinnerServers;
    private TextView tvNick;
    private Button btnPlay;
    private ProgressBar progressBar;
    private TextView tvStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = new Prefs(this);
        cacheManager = new CacheManager(this);

        spinnerServers = findViewById(R.id.spinnerServers);
        tvNick = findViewById(R.id.tvNick);
        btnPlay = findViewById(R.id.btnPlay);
        progressBar = findViewById(R.id.progressBar);
        tvStatus = findViewById(R.id.tvStatus);
        ImageButton btnSettings = findViewById(R.id.btnSettings);

        setupServers();

        btnSettings.setOnClickListener(v ->
                startActivity(new Intent(this, SettingsActivity.class)));

        btnPlay.setOnClickListener(v -> onPlayClicked());
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateNick();
    }

    private void setupServers() {
        // Список серверов RESIDENCE RP. Добавляй свои сервера здесь.
        servers.clear();
        servers.add(new Server("RESIDENCE RP | Main", "127.0.0.1", 7777));
        servers.add(new Server("RESIDENCE RP | Two", "127.0.0.1", 7778));
        servers.add(new Server("RESIDENCE RP | Test", "127.0.0.1", 7779));

        ArrayAdapter<Server> adapter = new ArrayAdapter<>(
                this, R.layout.item_spinner, servers);
        adapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        spinnerServers.setAdapter(adapter);

        int saved = prefs.getServerIndex();
        if (saved >= 0 && saved < servers.size()) {
            spinnerServers.setSelection(saved);
        }

        spinnerServers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                prefs.setServerIndex(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    private void updateNick() {
        String nick = prefs.getNickname();
        if (nick == null || nick.isEmpty()) {
            tvNick.setText(getString(R.string.nick_not_set));
        } else {
            tvNick.setText(getString(R.string.nick_prefix, nick));
        }
    }

    private void onPlayClicked() {
        String nick = prefs.getNickname();

        // 1) Проверяем ник
        if (nick == null || nick.isEmpty()) {
            Toast.makeText(this, R.string.error_no_nick, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, SettingsActivity.class));
            return;
        }

        // 2) Проверяем кэш
        if (!prefs.isCacheInstalled()) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.cache_needed_title)
                    .setMessage(R.string.cache_needed_msg)
                    .setPositiveButton(R.string.download, (d, w) -> startCacheDownload())
                    .setNegativeButton(R.string.cancel, null)
                    .show();
            return;
        }

        // 3) Записываем настройки игры и запускаем
        writeGameConfig(nick);
        launchGame();
    }

    private void startCacheDownload() {
        setUiLoading(true);
        String url = prefs.getCacheUrl();

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
                Toast.makeText(MainActivity.this, R.string.cache_done, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String message) {
                setUiLoading(false);
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(R.string.error_title)
                        .setMessage(message)
                        .setPositiveButton(android.R.string.ok, null)
                        .show();
            }
        });
    }

    private void setUiLoading(boolean loading) {
        btnPlay.setEnabled(!loading);
        spinnerServers.setEnabled(!loading);
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        tvStatus.setVisibility(loading ? View.VISIBLE : View.GONE);
        if (loading) progressBar.setProgress(0);
    }

    /**
     * Записывает ник и адрес сервера в конфиг внутри кэша.
     * Формат — простой ini, подстрой под свой клиент при необходимости.
     */
    private void writeGameConfig(String nick) {
        try {
            Server server = servers.get(spinnerServers.getSelectedItemPosition());
            File configFile = new File(cacheManager.getCacheDir(), "launcher_settings.ini");
            try (FileWriter writer = new FileWriter(configFile)) {
                writer.write("[settings]\n");
                writer.write("nickname=" + nick + "\n");
                writer.write("server_ip=" + server.ip + "\n");
                writer.write("server_port=" + server.port + "\n");
            }
        } catch (Exception e) {
            Toast.makeText(this, "Не удалось записать настройки: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void launchGame() {
        Intent intent = getPackageManager().getLaunchIntentForPackage(GAME_PACKAGE);
        if (intent != null) {
            // Передаём данные в игру через extras (клиент может их прочитать)
            Server server = servers.get(spinnerServers.getSelectedItemPosition());
            intent.putExtra("nickname", prefs.getNickname());
            intent.putExtra("server_ip", server.ip);
            intent.putExtra("server_port", server.port);
            startActivity(intent);
        } else {
            // Игра не установлена — предлагаем открыть Play Market
            new AlertDialog.Builder(this)
                    .setTitle(R.string.game_not_found_title)
                    .setMessage(R.string.game_not_found_msg)
                    .setPositiveButton(R.string.open_market, (d, w) -> {
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("market://details?id=" + GAME_PACKAGE)));
                        } catch (Exception e) {
                            startActivity(new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("https://play.google.com/store/apps/details?id=" + GAME_PACKAGE)));
                        }
                    })
                    .setNegativeButton(R.string.cancel, null)
                    .show();
        }
    }
}
