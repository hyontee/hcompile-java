package com.shadowrp.admintool;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(32, 64, 32, 32);
        root.setGravity(Gravity.CENTER_HORIZONTAL);

        TextView title = new TextView(this);
        title.setText("Shadow Admin Tool - sozlash");
        title.setTextSize(18);
        title.setPadding(0, 0, 0, 48);
        root.addView(title);

        Button btnAccessibility = new Button(this);
        btnAccessibility.setText(R.string.btn_open_accessibility);
        btnAccessibility.setOnClickListener(v ->
                startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)));
        root.addView(btnAccessibility);

        Button btnOverlay = new Button(this);
        btnOverlay.setText(R.string.btn_open_overlay_perm);
        btnOverlay.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Intent intent = new Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            }
        });
        root.addView(btnOverlay);

        Button btnStart = new Button(this);
        btnStart.setText(R.string.btn_start_service);
        btnStart.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && !Settings.canDrawOverlays(this)) {
                Toast.makeText(this,
                        "Avval overlay ruxsatini bering", Toast.LENGTH_SHORT).show();
                return;
            }
            startService(new Intent(this, OverlayService.class));
            Toast.makeText(this, "Panel ishga tushdi", Toast.LENGTH_SHORT).show();
        });
        root.addView(btnStart);

        setContentView(root);
    }
}
