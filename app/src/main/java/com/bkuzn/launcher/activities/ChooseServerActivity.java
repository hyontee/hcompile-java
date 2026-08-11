package com.bkuzn.launcher.activities;
//      by t.me/bkuzn
//      ⣿⣿⡟⠄⠄⠄⠄⠄⠄⠄⠄⠄⠄⠄⣿
//      ⣿⣿⡇⠄⠄⠄⠐⠄⠄⠄⠄⠄⠄⠄⠠⣿
//      ⣿⣿⡇⠄⢀⡀⠠⠃⡐⡀⠠⣶⠄⠄⢀⣿
//      ⣿⣿⣶⠄⠰⣤⣕⣿⣾⡇⠄⢛⠃⠄⢈⣿
//      ⣿⣿⣿⡇⢀⣻⠟⣻⣿⡇⠄⠧⠄⢀⣾
//      ⣿⣿⣿⣟⢸⣻⣭⡙⢄⢀⠄⠄⠄⠈⢹⣯
//      ⣿⣿⣿⣭⣿⣿⣿⣧⢸⠄⠄⠄⠄⠄⠈⢸
//      ⣿⣿⣿⣼⣿⣿⣿⣽⠘⡄⠄⠄⠄⠄⢀⠸
//      ⡿⣿⣳⣿⣿⣿⣿⣿⠄⠓⠦⠤⠤⠤⠼⢸
//      ⡹⣧⣿⣿⣿⠿⣿⣿⣿⣿⣿⣿⣿⢇⣓⣾
//      ⡞⣸⣿⣿⢏⣼⣶⣶⣶⣶⣤⣶⡤⠐⣿
//      ⣯⣽⣛⠅⣾⣿⣿⣿⣿⣿⡽⣿⣧⡸⢿
//      ⣿⣿⣿⡷⠹⠛⠉⠁⠄⠄⠄⠄⠄⠄⠐⠛⠻
//      ⣿⣿⣿⠃⠄⠄⠄⠄⠄⣠⣤⣤⣤⡄⢤⣤⣤⣤⡘
//      ⣿⣿⡟⠄⠄⣀⣤⣶⣿⣿⣿⣿⣿⣿⣆⢻⣿⣿⣿⡎
//      ⣿⡏⠄⢀⣼⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡎⣿⣿⣿⣿
//      ⣿⡏⣲⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⢇⣿⣿⣿⡟
//      ⣿⡠⠜⣿⣿⣿⣿⣟⡛⠿⠿⠿⠿⠟⠃⠾⠿⢟⡋
//      ⣿⣧⣄⠙⢿⣿⣿⣿⣿⣿⣷⣦⡀⢰⣾⣿⣿⡿
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bkuzn.game.R;
import com.bkuzn.game.SAMP;

public class ChooseServerActivity extends AppCompatActivity {

    private ImageView playCrimeBtn;
    private ImageView playGoldBtn;
    private ImageView playRublBtn;
    private ConstraintLayout backBtn;
    private Handler handler = new Handler(Looper.getMainLooper());
    private boolean isGameLaunched = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setFullScreenMode();

        setContentView(R.layout.launcher_choose_server_activity);

        getWindow().setStatusBarColor(android.graphics.Color.TRANSPARENT);
        getWindow().setNavigationBarColor(android.graphics.Color.TRANSPARENT);

        initViews();
        setupClickListeners();
    }

    private void setFullScreenMode() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().setAttributes(params);
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            );
        }
    }

    private void initViews() {
        playCrimeBtn = findViewById(R.id.play_crime_btn);
        playGoldBtn = findViewById(R.id.play_gold_btn);
        playRublBtn = findViewById(R.id.play_rubl_btn);
        backBtn = findViewById(R.id.back_btn);
    }

    private void setupClickListeners() {
        playCrimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchGameWithLoading("Crime bkuzn");
            }
        });

        playGoldBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchGameWithLoading("Gold bkuzn");
            }
        });

        playRublBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchGameWithLoading("Rubl bkuzn");
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void launchGameWithLoading(String serverName) {
        if (isGameLaunched) {
            return;
        }
        isGameLaunched = true;
        launchGame();
    }

    private void launchGame() {
        Intent gameIntent = new Intent(ChooseServerActivity.this, SAMP.class);
        startActivity(gameIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
