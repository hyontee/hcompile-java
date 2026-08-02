package com.shadowrp.admintool;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.os.Bundle;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Shadow RP mobil klienti ustida ishlaydi:
 *  - Adminning o'zi yozgan /pm, /ans, /arep buyruqlarini repost sifatida hisoblaydi.
 *  - "[ID] Ism yordam so'radi" kabi formatdagi report xabarlarini aniqlaydi va
 *    sozlangan rejimga qarab shablon javobni tayyorlaydi yoki avtomatik yuboradi.
 *
 * MUHIM: bu servis faqat o'yin klienti chat matnini ODDIY Android View (TextView/EditText)
 * sifatida chizsa ishlaydi. Agar chat matni to'g'ridan-to'g'ri OpenGL/Canvas orqali
 * chizilsa, AccessibilityNodeInfo uni ko'ra olmaydi - bu holda faqat chat INPUT maydoni
 * (yozish uchun EditText) bilan ishlash mumkin bo'ladi.
 */
public class AccessibilityServiceGM extends AccessibilityService {

    private static final Pattern ADMIN_CMD_PATTERN =
            Pattern.compile("/(pm|ans|arep)\\b");

    private static AccessibilityServiceGM activeInstance;

    private PrefsHelper prefs;
    private Pattern reportPattern;
    private String lastHandledReportText = "";
    private final Set<String> processedNodeTexts = new HashSet<>();

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        activeInstance = this;
        prefs = new PrefsHelper(this);
        rebuildReportPattern();
        applyTargetPackageFilter();
    }

    /** OverlayService sozlamalarni saqlagach shu yerdan chaqiradi. */
    public static void reloadSettingsIfRunning() {
        if (activeInstance != null) {
            activeInstance.rebuildReportPattern();
            activeInstance.applyTargetPackageFilter();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (activeInstance == this) activeInstance = null;
    }

    /** Sozlamalar o'zgarganda (masalan overlay paneldan) qayta chaqiriladi. */
    public void rebuildReportPattern() {
        try {
            reportPattern = Pattern.compile(prefs.getReportRegex());
        } catch (Exception e) {
            // Noto'g'ri regex kiritilgan bo'lsa standart patternga qaytamiz
            reportPattern = Pattern.compile("\\[(\\d+)]\\s*(.+?)\\s+yordam so'radi");
        }
    }

    private void applyTargetPackageFilter() {
        AccessibilityServiceInfo info = getServiceInfo();
        if (info == null) return;
        String pkg = prefs.getTargetPackage();
        if (pkg != null && !pkg.trim().isEmpty()) {
            info.packageNames = new String[]{pkg.trim()};
        } else {
            info.packageNames = null; // barcha ilovalarda ishlaydi (sozlanmagan bo'lsa)
        }
        setServiceInfo(info);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (prefs == null) prefs = new PrefsHelper(this);
        if (!prefs.isToolEnabled()) return;

        CharSequence eventText = event.getText() != null && !event.getText().isEmpty()
                ? event.getText().get(0) : null;

        if (eventText != null) {
            handlePossibleAdminCommand(eventText.toString());
            handlePossibleReport(eventText.toString());
        }

        // Ba'zi klientlarda matn faqat root tugunlar ichida bo'ladi, event.getText() bilan kelmaydi
        AccessibilityNodeInfo root = getRootInActiveWindow();
        if (root != null) {
            scanNodeForReports(root);
        }
    }

    private void handlePossibleAdminCommand(String text) {
        Matcher m = ADMIN_CMD_PATTERN.matcher(text);
        if (m.find()) {
            prefs.incrementReportsToday();
        }
    }

    private void scanNodeForReports(AccessibilityNodeInfo node) {
        if (node == null) return;
        CharSequence text = node.getText();
        if (text != null && text.length() > 0) {
            handlePossibleReport(text.toString());
        }
        int count = node.getChildCount();
        for (int i = 0; i < count; i++) {
            AccessibilityNodeInfo child = node.getChild(i);
            if (child != null) {
                scanNodeForReports(child);
                child.recycle();
            }
        }
    }

    private void handlePossibleReport(String text) {
        if (reportPattern == null || prefs.getAutoReplyMode() == PrefsHelper.MODE_OFF) return;
        if (text.equals(lastHandledReportText)) return; // bir xil xabarni qayta ishlamaslik uchun

        Matcher m = reportPattern.matcher(text);
        if (!m.find()) return;

        String id;
        String name;
        try {
            id = m.group(prefs.getIdGroupIndex());
            name = m.group(prefs.getNameGroupIndex());
        } catch (Exception e) {
            return;
        }
        if (id == null) return;
        if (name == null) name = "";

        lastHandledReportText = text;
        prefs.incrementReportsToday();

        // Standart shablon: birinchi saqlangan shablon ishlatiladi.
        // (Overlay panelda kelajakda "har bir shablon uchun kalit so'z" mosligi qo'shilishi mumkin)
        String templateText = firstTemplateOrDefault();
        String finalMessage = templateText.replace("{id}", id).replace("{name}", name);

        if (prefs.getAutoReplyMode() == PrefsHelper.MODE_FULL) {
            sendMessageAutomatically(finalMessage);
        } else {
            OverlayService.prepareMessage(finalMessage);
        }
    }

    private String firstTemplateOrDefault() {
        for (String v : prefs.getTemplates().values()) {
            if (!v.isEmpty()) return v;
        }
        return "/pm {id} Asalomu alaykum, yordamga boryabman.";
    }

    /**
     * To'liq avtomatik rejim: chat INPUT maydonini topib, matnni kiritadi va
     * yuborish tugmasini bosadi. Bu ikkala tugun ID/klass nomlari o'yin klientiga
     * qarab moslashtirilishi kerak - quyida umumiy (generic) qidiruv ishlatilgan.
     */
    private void sendMessageAutomatically(String message) {
        AccessibilityNodeInfo root = getRootInActiveWindow();
        if (root == null) return;

        AccessibilityNodeInfo inputField = findFirstEditable(root);
        if (inputField != null) {
            Bundle args = new Bundle();
            args.putCharSequence(
                    AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, message);
            inputField.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, args);

            // Yuborish odatda IME "enter"/"send" action orqali amalga oshiriladi.
            inputField.performAction(AccessibilityNodeInfo.ACTION_IME_ENTER);
            inputField.recycle();
        } else {
            // Input topilmasa - xavfsizroq variant: tayyorlab qo'yish rejimiga tushamiz
            OverlayService.prepareMessage(message);
        }
    }

    private AccessibilityNodeInfo findFirstEditable(AccessibilityNodeInfo node) {
        if (node == null) return null;
        if (node.isEditable()) return node;
        int count = node.getChildCount();
        for (int i = 0; i < count; i++) {
            AccessibilityNodeInfo child = node.getChild(i);
            if (child == null) continue;
            AccessibilityNodeInfo result = findFirstEditable(child);
            if (result != null) {
                if (result != child) child.recycle();
                return result;
            }
            child.recycle();
        }
        return null;
    }

    @Override
    public void onInterrupt() {
        // kerak emas
    }
}
