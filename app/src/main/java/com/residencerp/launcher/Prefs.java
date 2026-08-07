package com.residencerp.launcher;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Обёртка над SharedPreferences: хранит ник, выбранный сервер,
 * ссылку на кэш и флаг "кэш установлен".
 */
public class Prefs {

    private static final String NAME = "residence_prefs";

    private static final String KEY_NICK = "nickname";
    private static final String KEY_SERVER = "server_index";
    private static final String KEY_CACHE_URL = "cache_url";
    private static final String KEY_CACHE_INSTALLED = "cache_installed";

    // Базовый URL папки с кэшем (VDS Ubuntu 22.04, RESIDENCE RP).
    // Файлы лежат распакованными прямо в этой папке.
    // В корне папки должен лежать файл-манифест "filelist.txt"
    // со списком всех файлов (по одному относительному пути в строке).
    // ВАЖНО: адрес должен заканчиваться на "/".
    public static final String DEFAULT_CACHE_URL = "http://62.109.17.78/game/";

    private final SharedPreferences sp;

    public Prefs(Context context) {
        sp = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
    }

    public String getNickname() {
        return sp.getString(KEY_NICK, "");
    }

    public void setNickname(String nick) {
        sp.edit().putString(KEY_NICK, nick).apply();
    }

    public int getServerIndex() {
        return sp.getInt(KEY_SERVER, 0);
    }

    public void setServerIndex(int index) {
        sp.edit().putInt(KEY_SERVER, index).apply();
    }

    public String getCacheUrl() {
        return sp.getString(KEY_CACHE_URL, DEFAULT_CACHE_URL);
    }

    public void setCacheUrl(String url) {
        sp.edit().putString(KEY_CACHE_URL, url).apply();
    }

    public boolean isCacheInstalled() {
        return sp.getBoolean(KEY_CACHE_INSTALLED, false);
    }

    public void setCacheInstalled(boolean installed) {
        sp.edit().putBoolean(KEY_CACHE_INSTALLED, installed).apply();
    }
}
