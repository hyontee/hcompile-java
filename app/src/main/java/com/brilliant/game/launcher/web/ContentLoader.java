package com.brilliant.game.launcher.web;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.brilliant.game.launcher.model.Lists;
import com.brilliant.game.launcher.model.News;
import com.brilliant.game.launcher.model.Servers;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ContentLoader {

    private static final String TAG = "ContentLoader";

    private static final ExecutorService executor = Executors.newFixedThreadPool(2);
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());

    public interface Callback {
        void onNewsLoaded(boolean success);
        void onServersLoaded(boolean success);
    }

    public static void loadAll(Callback callback) {
        loadNews(callback);
        loadServers(callback);
    }

    private static void loadNews(Callback callback) {
        executor.execute(() -> {
            boolean success = false;
            try {
                String json = fetch(LauncherApi.getNewsInfo());
                JSONArray arr = new JSONArray(json);

                Lists.nlist.clear();
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject o = arr.getJSONObject(i);
                    Lists.nlist.add(new News(o.getString("image"), o.getString("title")));
                }
                success = true;
            } catch (Exception e) {
                Log.e(TAG, "news.json error: " + e.getMessage());
            }

            boolean finalSuccess = success;
            mainHandler.post(() -> callback.onNewsLoaded(finalSuccess));
        });
    }

    private static void loadServers(Callback callback) {
        executor.execute(() -> {
            boolean success = false;
            try {
                String json = fetch(LauncherApi.getServersListInfo());
                JSONArray arr = new JSONArray(json);

                Lists.slist.clear();
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject o = arr.getJSONObject(i);
                    Lists.slist.add(new Servers(
                            o.getString("ip"),
                            o.getInt("port"),
                            o.getString("name"),
                            o.optInt("online", 0),
                            o.optInt("maxonline", 0)
                    ));
                }
                success = true;
            } catch (Exception e) {
                Log.e(TAG, "servers.json error: " + e.getMessage());
            }

            boolean finalSuccess = success;
            mainHandler.post(() -> callback.onServersLoaded(finalSuccess));
        });
    }

    private static String fetch(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);

        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK)
            throw new Exception("HTTP " + responseCode);

        InputStream inputStream = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null)
            response.append(line);

        reader.close();
        inputStream.close();
        connection.disconnect();

        return response.toString();
    }
}
