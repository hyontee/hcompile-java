package com.shadowrp.admintool;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Barcha sozlamalar, shablonlar va statistikani SharedPreferences orqali saqlaydi.
 */
public class PrefsHelper {

    private static final String PREFS_NAME = "shadow_admin_tool_prefs";

    private static final String KEY_TOOL_ENABLED = "tool_enabled";
    private static final String KEY_SPECIAL_ENABLED = "special_enabled";
    private static final String KEY_PACKAGE_NAME = "target_package";
    private static final String KEY_REPORT_REGEX = "report_regex";
    private static final String KEY_ID_GROUP = "id_group_index";
    private static final String KEY_NAME_GROUP = "name_group_index";
    private static final String KEY_AUTOREPLY_MODE = "autoreply_mode"; // 0=off,1=prepare,2=full
    private static final String KEY_TEMPLATE_PREFIX = "template_";
    private static final String KEY_TEMPLATE_LIST = "template_names";

    public static final int MODE_OFF = 0;
    public static final int MODE_PREPARE = 1;
    public static final int MODE_FULL = 2;

    private static final String[] WEEKDAYS = {
            "sunday", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday"
    };

    private final SharedPreferences prefs;

    public PrefsHelper(Context ctx) {
        prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    // ---------- Umumiy holat ----------

    public boolean isToolEnabled() {
        return prefs.getBoolean(KEY_TOOL_ENABLED, false);
    }

    public void setToolEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_TOOL_ENABLED, enabled).apply();
    }

    public boolean isSpecialEnabled() {
        return prefs.getBoolean(KEY_SPECIAL_ENABLED, false);
    }

    public void setSpecialEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_SPECIAL_ENABLED, enabled).apply();
    }

    // ---------- Nishon ilova ----------

    public String getTargetPackage() {
        return prefs.getString(KEY_PACKAGE_NAME, "");
    }

    public void setTargetPackage(String pkg) {
        prefs.edit().putString(KEY_PACKAGE_NAME, pkg).apply();
    }

    // ---------- Report aniqlash regexi ----------
    // Standart namuna (haqiqiy o'yin formatiga mos): "Oybek_Farqoqov[271]: ... [Hisobotlar soni: 2]"
    // guruh 1 = ism, guruh 2 = ID

    public String getReportRegex() {
        return prefs.getString(KEY_REPORT_REGEX,
                "(\\w+)\\[(\\d+)]:.*\\[Hisobotlar soni:\\s*\\d+]");
    }

    public void setReportRegex(String regex) {
        prefs.edit().putString(KEY_REPORT_REGEX, regex).apply();
    }

    public int getIdGroupIndex() {
        return prefs.getInt(KEY_ID_GROUP, 2);
    }

    public void setIdGroupIndex(int idx) {
        prefs.edit().putInt(KEY_ID_GROUP, idx).apply();
    }

    public int getNameGroupIndex() {
        return prefs.getInt(KEY_NAME_GROUP, 1);
    }

    public void setNameGroupIndex(int idx) {
        prefs.edit().putInt(KEY_NAME_GROUP, idx).apply();
    }

    // ---------- Avto-javob rejimi ----------

    public int getAutoReplyMode() {
        return prefs.getInt(KEY_AUTOREPLY_MODE, MODE_PREPARE);
    }

    public void setAutoReplyMode(int mode) {
        prefs.edit().putInt(KEY_AUTOREPLY_MODE, mode).apply();
    }

    // ---------- Shablonlar ----------

    public Map<String, String> getTemplates() {
        Map<String, String> map = new LinkedHashMap<>();
        String namesRaw = prefs.getString(KEY_TEMPLATE_LIST, "");
        if (namesRaw.isEmpty()) return map;
        for (String name : namesRaw.split("\u0001")) {
            if (name.isEmpty()) continue;
            String text = prefs.getString(KEY_TEMPLATE_PREFIX + name, "");
            map.put(name, text);
        }
        return map;
    }

    public void saveTemplate(String name, String text) {
        Map<String, String> templates = getTemplates();
        templates.put(name, text);
        StringBuilder sb = new StringBuilder();
        for (String n : templates.keySet()) {
            if (sb.length() > 0) sb.append('\u0001');
            sb.append(n);
        }
        prefs.edit()
                .putString(KEY_TEMPLATE_LIST, sb.toString())
                .putString(KEY_TEMPLATE_PREFIX + name, text)
                .apply();
    }

    public void deleteTemplate(String name) {
        Map<String, String> templates = getTemplates();
        templates.remove(name);
        StringBuilder sb = new StringBuilder();
        for (String n : templates.keySet()) {
            if (sb.length() > 0) sb.append('\u0001');
            sb.append(n);
        }
        prefs.edit()
                .putString(KEY_TEMPLATE_LIST, sb.toString())
                .remove(KEY_TEMPLATE_PREFIX + name)
                .apply();
    }

    // ---------- Kunlik statistika (report soni + onlayn soniya) ----------

    private String todayKey() {
        int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1; // 0=Sunday
        return WEEKDAYS[day];
    }

    public int getReportsForToday() {
        return prefs.getInt("reports_" + todayKey(), 0);
    }

    public void incrementReportsToday() {
        String key = "reports_" + todayKey();
        prefs.edit().putInt(key, prefs.getInt(key, 0) + 1).apply();
    }

    public long getOnlineSecondsForToday() {
        return prefs.getLong("online_" + todayKey(), 0L);
    }

    public void addOnlineSecondToday() {
        String key = "online_" + todayKey();
        prefs.edit().putLong(key, prefs.getLong(key, 0L) + 1).apply();
    }

    public int getReports(String weekday) {
        return prefs.getInt("reports_" + weekday, 0);
    }

    public long getOnlineSeconds(String weekday) {
        return prefs.getLong("online_" + weekday, 0L);
    }

    public static String[] weekdays() {
        return WEEKDAYS;
    }
}
