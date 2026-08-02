package com.shadowrp.admintool;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Map;

public class OverlayService extends Service {

    // "Tayyorlab qo'yish" rejimida chaqiriladigan statik ko'prik - servis ichidan UI'ni yangilash uchun
    private static OverlayService activeInstance;

    private WindowManager windowManager;
    private View panelView;
    private WindowManager.LayoutParams panelParams;
    private PrefsHelper prefs;
    private final Handler tickHandler = new Handler(Looper.getMainLooper());

    private EditText editTemplateName, editTemplateText, editPackageName, editReportRegex;
    private RadioGroup radioAutoReplyMode;
    private TextView txtStats;

    private final Runnable onlineTicker = new Runnable() {
        @Override
        public void run() {
            if (prefs != null && prefs.isToolEnabled()) {
                prefs.addOnlineSecondToday();
                refreshStats();
            }
            tickHandler.postDelayed(this, 1000);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        activeInstance = this;
        prefs = new PrefsHelper(this);
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        buildPanel();
        tickHandler.post(onlineTicker);
    }

    private void buildPanel() {
        LayoutInflater inflater = LayoutInflater.from(this);
        panelView = inflater.inflate(R.layout.overlay_layout, null);

        int overlayType = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                : WindowManager.LayoutParams.TYPE_PHONE;

        panelParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                overlayType,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        panelParams.gravity = Gravity.TOP | Gravity.START;
        panelParams.x = 0;
        panelParams.y = 100;

        makeDraggable(panelView, panelParams);
        wireControls();
        windowManager.addView(panelView, panelParams);
    }

    /** Panelning istalgan bo'sh joyidan ushlab surish (drag) imkoni. */
    private void makeDraggable(View view, WindowManager.LayoutParams params) {
        view.setOnTouchListener(new View.OnTouchListener() {
            float initialX, initialY, touchX, touchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        touchX = event.getRawX();
                        touchY = event.getRawY();
                        return false; // ichidagi tugmalar o'z bosilishini davom ettirsin
                    case MotionEvent.ACTION_MOVE:
                        params.x = (int) (initialX + (event.getRawX() - touchX));
                        params.y = (int) (initialY + (event.getRawY() - touchY));
                        windowManager.updateViewLayout(view, params);
                        return false;
                }
                return false;
            }
        });
    }

    private void wireControls() {
        Button btnEnable = panelView.findViewById(R.id.btnEnable);
        Button btnDisable = panelView.findViewById(R.id.btnDisable);
        Button btnSpecial = panelView.findViewById(R.id.btnSpecial);
        editTemplateName = panelView.findViewById(R.id.editTemplateName);
        editTemplateText = panelView.findViewById(R.id.editTemplateText);
        Button btnSaveTemplate = panelView.findViewById(R.id.btnSaveTemplate);
        Button btnDeleteTemplate = panelView.findViewById(R.id.btnDeleteTemplate);
        editPackageName = panelView.findViewById(R.id.editPackageName);
        editReportRegex = panelView.findViewById(R.id.editReportRegex);
        radioAutoReplyMode = panelView.findViewById(R.id.radioAutoReplyMode);
        Button btnSaveSettings = panelView.findViewById(R.id.btnSaveSettings);
        txtStats = panelView.findViewById(R.id.txtStats);

        btnEnable.setOnClickListener(v -> {
            prefs.setToolEnabled(true);
            Toast.makeText(this, "Tuls yoqildi", Toast.LENGTH_SHORT).show();
        });

        btnDisable.setOnClickListener(v -> {
            prefs.setToolEnabled(false);
            Toast.makeText(this, "Tuls o'chirildi", Toast.LENGTH_SHORT).show();
        });

        btnSpecial.setOnClickListener(v -> {
            prefs.setSpecialEnabled(true);
            startActivity(new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        });

        btnSaveTemplate.setOnClickListener(v -> {
            String name = editTemplateName.getText().toString().trim();
            String text = editTemplateText.getText().toString().trim();
            if (name.isEmpty() || text.isEmpty()) {
                Toast.makeText(this, "Nom va matnni to'ldiring", Toast.LENGTH_SHORT).show();
                return;
            }
            prefs.saveTemplate(name, text);
            Toast.makeText(this, "Shablon saqlandi", Toast.LENGTH_SHORT).show();
        });

        btnDeleteTemplate.setOnClickListener(v -> {
            String name = editTemplateName.getText().toString().trim();
            if (!name.isEmpty()) {
                prefs.deleteTemplate(name);
                editTemplateText.setText("");
                Toast.makeText(this, "O'chirildi", Toast.LENGTH_SHORT).show();
            }
        });

        // Sozlamalarni oldindan to'ldirish
        editPackageName.setText(prefs.getTargetPackage());
        editReportRegex.setText(prefs.getReportRegex());
        int mode = prefs.getAutoReplyMode();
        if (mode == PrefsHelper.MODE_OFF) radioAutoReplyMode.check(R.id.radioOff);
        else if (mode == PrefsHelper.MODE_FULL) radioAutoReplyMode.check(R.id.radioFull);
        else radioAutoReplyMode.check(R.id.radioPrepare);

        btnSaveSettings.setOnClickListener(v -> {
            prefs.setTargetPackage(editPackageName.getText().toString().trim());
            prefs.setReportRegex(editReportRegex.getText().toString().trim());

            int checkedId = radioAutoReplyMode.getCheckedRadioButtonId();
            int newMode = PrefsHelper.MODE_PREPARE;
            if (checkedId == R.id.radioOff) newMode = PrefsHelper.MODE_OFF;
            else if (checkedId == R.id.radioFull) newMode = PrefsHelper.MODE_FULL;
            prefs.setAutoReplyMode(newMode);
            AccessibilityServiceGM.reloadSettingsIfRunning();

            Toast.makeText(this, "Sozlamalar saqlandi", Toast.LENGTH_SHORT).show();
        });

        refreshStats();
    }

    private void refreshStats() {
        if (txtStats == null) return;
        StringBuilder sb = new StringBuilder();
        String[] labels = {"Yakshanba", "Dushanba", "Seshanba", "Chorshanba",
                "Payshanba", "Juma", "Shanba"};
        String[] keys = PrefsHelper.weekdays();
        for (int i = 0; i < keys.length; i++) {
            int reports = prefs.getReports(keys[i]);
            long seconds = prefs.getOnlineSeconds(keys[i]);
            sb.append(labels[i]).append(" | Repostlar: ").append(reports)
                    .append(" | Onlayn: ").append(formatSeconds(seconds)).append("\n");
        }
        txtStats.setText(sb.toString());
    }

    private String formatSeconds(long totalSeconds) {
        long h = totalSeconds / 3600;
        long m = (totalSeconds % 3600) / 60;
        long s = totalSeconds % 60;
        return String.format("%02d:%02d:%02d", h, m, s);
    }

    /**
     * "Faqat tayyorlab qo'y" rejimida AccessibilityServiceGM tomonidan chaqiriladi:
     * shablon matn maydonini tayyor javob bilan to'ldiradi, admin faqat o'yin ichida yuboradi.
     */
    public static void prepareMessage(String message) {
        if (activeInstance == null || activeInstance.editTemplateText == null) return;
        activeInstance.tickHandler.post(() -> {
            activeInstance.editTemplateText.setText(message);
            Toast.makeText(activeInstance,
                    "Yangi report uchun javob tayyor", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        tickHandler.removeCallbacks(onlineTicker);
        if (panelView != null && windowManager != null) {
            windowManager.removeView(panelView);
        }
        if (activeInstance == this) activeInstance = null;
    }
}
