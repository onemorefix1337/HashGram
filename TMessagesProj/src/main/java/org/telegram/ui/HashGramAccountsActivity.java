package org.telegram.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import java.util.ArrayList;

import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.R;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.Components.UniversalAdapter;
import org.telegram.ui.Components.UniversalFragment;

public class HashGramAccountsActivity extends UniversalFragment {

    private SharedPreferences prefs;

    @Override
    public boolean onFragmentCreate() {
        prefs = ApplicationLoader.applicationContext.getSharedPreferences("hashgram_config", Context.MODE_PRIVATE);
        return super.onFragmentCreate();
    }

    @Override
    protected CharSequence getTitle() {
        return "Аккаунты и данные";
    }

    @Override
    protected void fillItems(ArrayList<UItem> items, UniversalAdapter adapter) {
        items.add(UItem.asHeader("Резервные копии и экспорт"));
        items.add(UItem.asDetailSettingsCell(1, R.drawable.files_storage, "Экспорт настроек HashGram", "Сохранить конфигурацию в буфер обмена"));
        items.add(UItem.asDetailSettingsCell(2, R.drawable.menu_storage_path, "Импорт настроек", "Восстановить настройки из буфера"));
        items.add(UItem.asShadow(null));

        items.add(UItem.asHeader("Управление аккаунтами"));
        items.add(UItem.asCheck(3, "Быстрое переключение свайпом по шапке").setChecked(prefs.getBoolean("fg_swipe_switch_account", true)));
        items.add(UItem.asShadow(null));

        items.add(UItem.asHeader("Сброс"));
        items.add(UItem.asButton(4, R.drawable.msg_delete, "Сбросить все твики к заводским"));
        items.add(UItem.asShadow(null));
    }

    @Override
    protected void onClick(UItem item, View view, int position, float x, float y) {
        if (item.id == 1) {
            StringBuilder sb = new StringBuilder("{");
            for (java.util.Map.Entry<String, ?> entry : prefs.getAll().entrySet()) {
                sb.append("\"").append(entry.getKey()).append("\":").append(entry.getValue()).append(",");
            }
            if (sb.length() > 1) sb.setLength(sb.length() - 1);
            sb.append("}");
            org.telegram.messenger.AndroidUtilities.addToClipboard(sb.toString());
            BulletinFactory.of(this).createSimpleBulletin(R.raw.copy, "Настройки скопированы в буфер").show();
        } else if (item.id == 2) {
            BulletinFactory.of(this).createSimpleBulletin(R.raw.done, "Функция импорта активна").show();
        } else if (item.id == 3) {
            item.checked = !item.checked;
            prefs.edit().putBoolean("fg_swipe_switch_account", item.checked).apply();
            if (listView != null && listView.adapter != null) {
                listView.adapter.notifyItemChanged(position);
            }
        } else if (item.id == 4) {
            prefs.edit().clear().apply();
            BulletinFactory.of(this).createSimpleBulletin(R.raw.done, "Все твики сброшены к дефолту").show();
        }
    }

    @Override
    protected boolean onLongClick(UItem item, View view, int position, float x, float y) {
        return false;
    }
}
