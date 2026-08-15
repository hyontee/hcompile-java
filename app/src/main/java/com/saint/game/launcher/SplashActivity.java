package com.saint.game.launcher;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.saint.game.R;

import java.util.Timer;
import java.util.TimerTask;
/*Project made by vk.com/mazan172 */
public class SplashActivity extends AppCompatActivity
{
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
                    || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
            } else {
                startTimer();
            }
        } else {
            startTimer();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000) {
            startTimer();
        }
    }
    private void startTimer()
    {
        Timer t = new Timer();
        t.schedule(new TimerTask(){

            @Override
            public void run() {
                startLauncher();
                //CheckUpdates();
            }
        }, 10000L);
    }

    private void startLauncher()
    {
        startActivity(new Intent(this, com.saint.game.launcher.MainActivity.class));
        finish();
    }
}
