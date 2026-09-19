package com.brilliant.game.launcher.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Минимальная замена Glide (которого нет в зависимостях проекта) —
 * просто грузит картинку по URL в фоне и ставит в ImageView.
 * Без дискового кэша — для ленты новостей на главном экране этого достаточно.
 */
public class SimpleImageLoader {

    private static final ExecutorService executor = Executors.newFixedThreadPool(3);
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());

    public static void load(String url, ImageView target) {
        // Тег на вьюхе — чтобы при переиспользовании ViewHolder'а RecyclerView
        // старая асинхронная загрузка не перезаписала картинку не в тот item.
        target.setTag(url);

        executor.execute(() -> {
            Bitmap bitmap = null;

            try {
                URL u = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) u.openConnection();
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.setDoInput(true);
                connection.connect();

                InputStream input = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(input);
                input.close();
                connection.disconnect();
            } catch (Exception ignored) {
            }

            Bitmap finalBitmap = bitmap;
            mainHandler.post(() -> {
                if (url.equals(target.getTag()) && finalBitmap != null)
                    target.setImageBitmap(finalBitmap);
            });
        });
    }
}
