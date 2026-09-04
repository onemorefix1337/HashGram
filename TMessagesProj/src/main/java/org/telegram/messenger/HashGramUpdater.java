package org.telegram.messenger;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.widget.Toast;
import androidx.core.content.FileProvider;

import org.json.JSONArray;
import org.json.JSONObject;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.BottomSheet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ProgressBar;
import org.telegram.ui.Components.LayoutHelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HashGramUpdater {

    public static boolean isDownloading = false;
    public static int downloadProgress = 0;

    public interface UpdaterDelegate {
        void onProgressChanged(int progress);
        void onDownloadComplete();
        void onDownloadFailed();
    }

    public static UpdaterDelegate delegate;

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
                            showUpdateDialog(activity, finalTagName, finalDownloadUrl);
                        } else if (showToastIfLatest) {
                            Toast.makeText(activity, "У вас последняя версия", Toast.LENGTH_SHORT).show();
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
        builder.setMessage("Вышла новая версия мода: " + newVersion + "\nСкачать прямо сейчас?");
        builder.setPositiveButton("Скачать", (dialog, which) -> {
            startDownload(activity, downloadUrl, newVersion);
        });
        builder.setNegativeButton("Позже", null);
        builder.show();
    }

    private static void startDownload(Activity activity, String downloadUrl, String version) {
        if (isDownloading) return;
        isDownloading = true;
        downloadProgress = 0;
        if (delegate != null) {
            delegate.onProgressChanged(0);
        }
        
        Toast.makeText(activity, "Загрузка обновления началась...", Toast.LENGTH_SHORT).show();

        Utilities.globalQueue.postRunnable(() -> {
            try {
                URL url = new URL(downloadUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                int fileLength = connection.getContentLength();
                InputStream input = connection.getInputStream();
                
                File dir = new File(ApplicationLoader.applicationContext.getCacheDir(), "updates");
                if (!dir.exists()) dir.mkdirs();
                File outputFile = new File(dir, "HashGram_" + version + ".apk");
                
                FileOutputStream output = new FileOutputStream(outputFile);

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                long lastTime = System.currentTimeMillis();

                while ((count = input.read(data)) != -1) {
                    if (!isDownloading) {
                        output.close();
                        input.close();
                        outputFile.delete();
                        return;
                    }
                    total += count;
                    output.write(data, 0, count);

                    if (fileLength > 0 && System.currentTimeMillis() - lastTime > 100) {
                        lastTime = System.currentTimeMillis();
                        int progress = (int) (total * 100 / fileLength);
                        AndroidUtilities.runOnUIThread(() -> {
                            downloadProgress = progress;
                            if (delegate != null) delegate.onProgressChanged(progress);
                        });
                    }
                }
                
                output.flush();
                output.close();
                input.close();

                AndroidUtilities.runOnUIThread(() -> {
                    isDownloading = false;
                    downloadProgress = 100;
                    if (delegate != null) delegate.onDownloadComplete();
                    installApk(activity, outputFile);
                });

            } catch (Exception e) {
                FileLog.e(e);
                AndroidUtilities.runOnUIThread(() -> {
                    isDownloading = false;
                    if (delegate != null) delegate.onDownloadFailed();
                    Toast.makeText(activity, "Ошибка загрузки обновления", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private static void installApk(Activity activity, File apkFile) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT >= 24) {
                Uri apkUri = FileProvider.getUriForFile(activity, ApplicationLoader.applicationContext.getPackageName() + ".provider", apkFile);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            } else {
                intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
            }
            activity.startActivity(intent);
        } catch (Exception e) {
            FileLog.e(e);
            Toast.makeText(activity, "Не удалось запустить установку", Toast.LENGTH_SHORT).show();
        }
    }

    public static void showProgressBottomSheet(Context context) {
        BottomSheet.Builder builder = new BottomSheet.Builder(context);
        
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(AndroidUtilities.dp(24), AndroidUtilities.dp(24), AndroidUtilities.dp(24), AndroidUtilities.dp(24));
        
        TextView title = new TextView(context);
        title.setText("Скачивание обновления...");
        title.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        title.setTextSize(18);
        title.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        layout.addView(title, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, 0, 0, 16));
        
        ProgressBar progressBar = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
        progressBar.setMax(100);
        progressBar.setProgress(downloadProgress);
        layout.addView(progressBar, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 20));
        
        TextView progressText = new TextView(context);
        progressText.setText(downloadProgress + "%");
        progressText.setTextColor(Theme.getColor(Theme.key_dialogTextGray2));
        progressText.setTextSize(14);
        progressText.setGravity(Gravity.CENTER_HORIZONTAL);
        layout.addView(progressText, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, 8, 0, 0));
        
        builder.setCustomView(layout);
        BottomSheet sheet = builder.show();
        
        UpdaterDelegate oldDelegate = delegate;
        delegate = new UpdaterDelegate() {
            @Override
            public void onProgressChanged(int progress) {
                progressBar.setProgress(progress);
                progressText.setText(progress + "%");
                if (oldDelegate != null) oldDelegate.onProgressChanged(progress);
            }

            @Override
            public void onDownloadComplete() {
                sheet.dismiss();
                if (oldDelegate != null) oldDelegate.onDownloadComplete();
            }

            @Override
            public void onDownloadFailed() {
                sheet.dismiss();
                if (oldDelegate != null) oldDelegate.onDownloadFailed();
            }
        };
        
        sheet.setOnDismissListener(dialog -> {
            delegate = oldDelegate;
        });
    }
}
