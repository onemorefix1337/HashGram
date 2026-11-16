package org.telegram.ui;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.view.Display;
import android.view.View;
import java.util.ArrayList;

import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.R;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.Components.UniversalAdapter;
import org.telegram.ui.Components.UniversalFragment;

public class HashGramPerformanceActivity extends UniversalFragment {

    private SharedPreferences prefs;

    @Override
    public boolean onFragmentCreate() {
        prefs = ApplicationLoader.applicationContext.getSharedPreferences("hashgram_config", Context.MODE_PRIVATE);
        return super.onFragmentCreate();
    }

    @Override
    protected CharSequence getTitle() {
        return "Производительность";
    }

    @Override
    protected void fillItems(ArrayList<UItem> items, UniversalAdapter adapter) {
        items.add(UItem.asHeader("Статистика устройства"));

        String refreshRateStr = "60 Hz";
        try {
            if (getParentActivity() != null) {
                Display display = getParentActivity().getWindowManager().getDefaultDisplay();
                refreshRateStr = String.format("%.0f Hz", display.getRefreshRate());
            }
        } catch (Exception ignore) {}

        String memStr = getMemoryStats();

        items.add(UItem.asDetailSettingsCell(100, R.drawable.msg_speed, "Частота экрана", "Текущая: " + refreshRateStr + " • 120 FPS Unlocked"));
        items.add(UItem.asDetailSettingsCell(101, R.drawable.files_storage, "Оперативная память", memStr));
        items.add(UItem.asDetailSettingsCell(102, R.drawable.msg_info, "Устройство", Build.MANUFACTURER + " " + Build.MODEL + " • Android " + Build.VERSION.RELEASE));
        items.add(UItem.asShadow(null));

        items.add(UItem.asHeader("Оптимизация и скорость"));
        items.add(UItem.asCheck(1, "Принудительный 120 FPS режим").setChecked(prefs.getBoolean("fg_force_120hz", true)));
        items.add(UItem.asCheck(2, "Обход ограничения скорости загрузки").setChecked(prefs.getBoolean("fg_premium_speed", false)));
        items.add(UItem.asCheck(3, "Аппаратное ускорение медиа").setChecked(prefs.getBoolean("fg_hardware_accel", true)));
        items.add(UItem.asCheck(4, "Быстрая анимация интерфейса").setChecked(prefs.getBoolean("fg_fast_animations", true)));
        items.add(UItem.asShadow(null));

        items.add(UItem.asHeader("Память и кэш"));
        items.add(UItem.asButton(10, R.drawable.msg_clear, "Очистить кэш базы данных"));
        items.add(UItem.asShadow(null));
    }

    private String getMemoryStats() {
        try {
            ActivityManager actManager = (ActivityManager) ApplicationLoader.applicationContext.getSystemService(Context.ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
            if (actManager != null) {
                actManager.getMemoryInfo(memInfo);
                long availMB = memInfo.availMem / (1024 * 1024);
                long totalMB = memInfo.totalMem / (1024 * 1024);
                long usedMB = totalMB - availMB;
                return usedMB + " МБ / " + totalMB + " МБ (свободно " + availMB + " МБ)";
            }
        } catch (Exception ignore) {}
        return "Доступно";
    }

    @Override
    protected void onClick(UItem item, View view, int position, float x, float y) {
        if (item.id == 1) {
            item.checked = !item.checked;
            prefs.edit().putBoolean("fg_force_120hz", item.checked).apply();
            BulletinFactory.of(this).createSimpleBulletin(R.raw.done, item.checked ? "120 FPS включен" : "120 FPS выключен").show();
            if (listView != null && listView.adapter != null) listView.adapter.notifyItemChanged(position);
        } else if (item.id == 2) {
            item.checked = !item.checked;
            prefs.edit().putBoolean("fg_premium_speed", item.checked).apply();
            if (listView != null && listView.adapter != null) listView.adapter.notifyItemChanged(position);
        } else if (item.id == 3) {
            item.checked = !item.checked;
            prefs.edit().putBoolean("fg_hardware_accel", item.checked).apply();
            if (listView != null && listView.adapter != null) listView.adapter.notifyItemChanged(position);
        } else if (item.id == 4) {
            item.checked = !item.checked;
            prefs.edit().putBoolean("fg_fast_animations", item.checked).apply();
            if (listView != null && listView.adapter != null) listView.adapter.notifyItemChanged(position);
        } else if (item.id == 10) {
            getMessagesStorage().clearLocalDatabase();
            BulletinFactory.of(this).createSimpleBulletin(R.raw.done, "Кэш базы данных очищен").show();
        }
    }

    @Override
    protected boolean onLongClick(UItem item, View view, int position, float x, float y) {
        return false;
    }
}
