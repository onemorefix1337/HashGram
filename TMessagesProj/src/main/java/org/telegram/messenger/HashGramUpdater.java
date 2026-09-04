package org.telegram.messenger;

import android.app.Activity;
import android.text.TextUtils;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.telegram.messenger.browser.Browser;
import org.telegram.ui.ActionBar.AlertDialog;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HashGramUpdater {

    public static void checkUpdate(Activity activity, boolean showToastIfLatest) {
        Utilities.globalQueue.postRunnable(() -> {
            try {
                URL url = new URL("https://api.github.com/repos/onemorefix1337/HashGram/releases/latest");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                if (connection.getResponseCode() == 200) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    JSONObject jsonObject = new JSONObject(response.toString());
                    String tagName = jsonObject.optString("tag_name", "");
                    
                    String downloadUrl = "";
                    JSONArray assets = jsonObject.optJSONArray("assets");
                    if (assets != null && assets.length() > 0) {
                        JSONObject asset = assets.getJSONObject(0);
                        downloadUrl = asset.optString("browser_download_url", "");
                    }

                    String currentHash = BuildConfig.BUILD_HASH;
                    if (TextUtils.isEmpty(currentHash)) {
                        currentHash = "unknown";
                    }

                    final String finalTagName = tagName;
                    final String finalDownloadUrl = downloadUrl;
                    final String finalCurrentHash = currentHash;

                    AndroidUtilities.runOnUIThread(() -> {
                        if (!TextUtils.isEmpty(finalTagName) && !finalTagName.equals(finalCurrentHash) && !TextUtils.isEmpty(finalDownloadUrl)) {
                            // Update available
                            showUpdateDialog(activity, finalTagName, finalDownloadUrl);
                        } else if (showToastIfLatest) {
                            Toast.makeText(activity, "У вас последняя версия (" + finalCurrentHash + ")", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        });
    }

    private static void showUpdateDialog(Activity activity, String newVersion, String downloadUrl) {
        if (activity == null || activity.isFinishing()) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Доступно обновление");
        builder.setMessage("Вышла новая версия мода: " + newVersion + "\nХотите обновить?");
        builder.setPositiveButton("Скачать", (dialog, which) -> {
            Browser.openUrl(activity, downloadUrl);
        });
        builder.setNegativeButton("Позже", null);
        builder.show();
    }
}
