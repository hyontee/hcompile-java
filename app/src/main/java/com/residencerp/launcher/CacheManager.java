package com.residencerp.launcher;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Отвечает за скачивание кэша RESIDENCE RP.
 *
 * На сервере (VDS Ubuntu 22.04) кэш лежит РАСПАКОВАННЫМИ файлами прямо в папке,
 * например: http://62.109.17.78/game/
 *
 * В корне этой папки должен лежать текстовый файл-манифест "filelist.txt"
 * со списком всех файлов кэша — по одному относительному пути в строке, например:
 *
 *   SAMP/samp.saa
 *   models/gta3.img
 *   data/handling.cfg
 *   texdb/effects.txd
 *
 * Лаунчер читает этот список и качает каждый файл, сохраняя структуру папок.
 *
 * Кэш кладётся в app-specific external storage:
 *   /Android/data/com.residencerp.launcher/files/game_cache
 * Это не требует разрешений на Android 10+.
 */
public class CacheManager {

    /** Имя файла-манифеста в корне папки кэша на сервере. */
    private static final String MANIFEST_NAME = "filelist.txt";

    public interface Callback {
        void onProgress(int percent, String status);
        void onComplete(File cacheDir);
        void onError(String message);
    }

    private final Context context;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler main = new Handler(Looper.getMainLooper());

    public CacheManager(Context context) {
        this.context = context.getApplicationContext();
    }

    /** Папка, куда скачивается кэш. */
    public File getCacheDir() {
        File dir = new File(context.getExternalFilesDir(null), "game_cache");
        if (!dir.exists()) dir.mkdirs();
        return dir;
    }

    /** Полностью удаляет установленный кэш (для переустановки). */
    public void wipeCache() {
        deleteRecursive(getCacheDir());
    }

    /**
     * Скачивает кэш по указанному базовому URL папки.
     * Все колбэки приходят в главном потоке.
     *
     * @param baseUrl базовый URL папки, ОБЯЗАТЕЛЬНО с "/" в конце,
     *                например http://62.109.17.78/game/
     */
    public void downloadAndInstall(final String baseUrl, final Callback callback) {
        executor.execute(() -> {
            try {
                final String base = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";

                post(callback, 0, "Получение списка файлов...");

                // 1) Скачиваем манифест filelist.txt
                List<String> files = downloadManifest(base + MANIFEST_NAME);
                if (files.isEmpty()) {
                    postError(callback, "Список файлов пуст или не найден: " + base + MANIFEST_NAME);
                    return;
                }

                // 2) Очищаем старый кэш
                File cacheDir = getCacheDir();
                deleteRecursive(cacheDir);
                cacheDir.mkdirs();

                // 3) Качаем файлы по очереди
                int totalFiles = files.size();
                for (int i = 0; i < totalFiles; i++) {
                    String relPath = files.get(i);
                    int percent = (int) ((long) i * 100 / totalFiles);
                    post(callback, percent,
                            "Загрузка " + (i + 1) + "/" + totalFiles + ": " + relPath);

                    File outFile = new File(cacheDir, relPath);

                    // Защита от Path Traversal
                    if (!outFile.getCanonicalPath()
                            .startsWith(cacheDir.getCanonicalPath() + File.separator)) {
                        postError(callback, "Некорректный путь в списке: " + relPath);
                        return;
                    }

                    File parent = outFile.getParentFile();
                    if (parent != null && !parent.exists()) parent.mkdirs();

                    // URL-кодируем каждый сегмент пути (пробелы/кириллица)
                    String fileUrl = base + encodePath(relPath);
                    boolean ok = downloadFile(fileUrl, outFile);
                    if (!ok) {
                        postError(callback, "Не удалось скачать файл: " + relPath);
                        return;
                    }
                }

                post(callback, 100, "Кэш установлен");
                postComplete(callback, cacheDir);

            } catch (Exception e) {
                postError(callback, "Ошибка: " + e.getMessage());
            }
        });
    }

    /** Скачивает и парсит манифест: непустые строки без комментариев (#). */
    private List<String> downloadManifest(String manifestUrl) throws Exception {
        List<String> result = new ArrayList<>();
        HttpURLConnection connection = null;
        try {
            URL url = new URL(manifestUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(20000);
            connection.setRequestMethod("GET");
            connection.connect();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new Exception("манифест вернул код " + connection.getResponseCode());
            }

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String trimmed = line.trim().replace("\\", "/");
                    if (trimmed.isEmpty() || trimmed.startsWith("#")) continue;
                    if (trimmed.startsWith("/")) trimmed = trimmed.substring(1);
                    result.add(trimmed);
                }
            }
        } finally {
            if (connection != null) connection.disconnect();
        }
        return result;
    }

    /** Скачивает один файл. Возвращает true при успехе. */
    private boolean downloadFile(String fileUrl, File outFile) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(fileUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(30000);
            connection.setRequestMethod("GET");
            connection.connect();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return false;
            }

            try (InputStream input = connection.getInputStream();
                 OutputStream output = new FileOutputStream(outFile)) {
                byte[] buffer = new byte[8192];
                int count;
                while ((count = input.read(buffer)) != -1) {
                    output.write(buffer, 0, count);
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            if (connection != null) connection.disconnect();
        }
    }

    /** URL-кодирует каждый сегмент пути отдельно, чтобы не сломать "/". */
    private String encodePath(String relPath) {
        String[] parts = relPath.split("/");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) sb.append("/");
            try {
                sb.append(java.net.URLEncoder.encode(parts[i], "UTF-8").replace("+", "%20"));
            } catch (Exception e) {
                sb.append(parts[i]);
            }
        }
        return sb.toString();
    }

    private void deleteRecursive(File file) {
        if (file == null || !file.exists()) return;
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) deleteRecursive(child);
            }
        }
        file.delete();
    }

    // --- вспомогательные методы для колбэков в UI-потоке ---
    private void post(Callback cb, int percent, String status) {
        main.post(() -> cb.onProgress(percent, status));
    }

    private void postComplete(Callback cb, File dir) {
        main.post(() -> cb.onComplete(dir));
    }

    private void postError(Callback cb, String msg) {
        main.post(() -> cb.onError(msg));
    }
}
