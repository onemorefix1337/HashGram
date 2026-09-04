package org.telegram.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import java.util.ArrayList;

import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.SharedConfig;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.Components.UniversalAdapter;
import org.telegram.ui.Components.UniversalFragment;

public class HashGramAppearanceActivity extends UniversalFragment {

    public static SharedPreferences getPrefs() {
        return ApplicationLoader.applicationContext.getSharedPreferences("hashgram_config", Context.MODE_PRIVATE);
    }

    private SharedPreferences prefs;

    @Override
    public boolean onFragmentCreate() {
        prefs = getPrefs();
        return super.onFragmentCreate();
    }

    @Override
    protected CharSequence getTitle() {
        return "Оформление";
    }

    @Override
    protected void fillItems(ArrayList<UItem> items, UniversalAdapter adapter) {
        items.add(UItem.asHeader("Чаты и Папки"));
        items.add(UItem.asCheck(1, "Скрыть вкладку «Все чаты»").setChecked(prefs.getBoolean("fg_hide_all_chats", false)));
        items.add(UItem.asCheck(3, "Компактный список чатов").setChecked(prefs.getBoolean("fg_compact_chats", false)));
        items.add(UItem.asCheck(12, "Полоска папок снизу (iOS)").setChecked(prefs.getBoolean("fg_folders_bottom", false)));
        items.add(UItem.asCheck(19, "Отключение свайпа папок").setChecked(prefs.getBoolean("fg_disable_folder_swipe", false)));
        items.add(UItem.asShadow(null));

        items.add(UItem.asHeader("Сообщения и Медиа"));
        items.add(UItem.asCheck(4, "Карандаш вместо «изменено»").setChecked(prefs.getBoolean("fg_pencil_edited", false)));
        items.add(UItem.asCheck(8, "Отключить большие эмодзи").setChecked(!SharedConfig.allowBigEmoji));
        items.add(UItem.asCheck(2, "Скрыть Истории (Stories)").setChecked(prefs.getBoolean("fg_hide_stories", false)));
        items.add(UItem.asCheck(21, "Отключить зацикливание стикеров").setChecked(prefs.getBoolean("fg_disable_sticker_loop", false)));
        items.add(UItem.asCheck(6, "Точное время (с секундами)").setChecked(prefs.getBoolean("fg_seconds_time", false)));
        items.add(UItem.asShadow(null));
    }

    @Override
    protected void onClick(UItem item, View view, int position, float x, float y) {
        item.checked = !item.checked;
        
        if (item.id == 8) {
            SharedConfig.toggleBigEmoji();
        } else {
            String key = null;
            switch (item.id) {
                case 1: key = "fg_hide_all_chats"; break;
                case 3: key = "fg_compact_chats"; break;
                case 12: key = "fg_folders_bottom"; break;
                case 19: key = "fg_disable_folder_swipe"; break;
                case 4: key = "fg_pencil_edited"; break;
                case 2: key = "fg_hide_stories"; break;
                case 21: key = "fg_disable_sticker_loop"; break;
                case 6: key = "fg_seconds_time"; break;
            }

            if (key != null) {
                prefs.edit().putBoolean(key, item.checked).apply();
                if (key.equals("fg_disable_folder_swipe")) SharedConfig.fg_disable_folder_swipe = item.checked;
                if (key.equals("fg_disable_sticker_loop")) SharedConfig.fg_disable_sticker_loop = item.checked;
            }
        }
        if (listView.getAdapter() != null) {
            listView.getAdapter().notifyItemChanged(position);
        }
        
        org.telegram.messenger.NotificationCenter.getGlobalInstance().postNotificationName(org.telegram.messenger.NotificationCenter.mainUserInfoChanged);
        org.telegram.messenger.NotificationCenter.getInstance(org.telegram.messenger.UserConfig.selectedAccount).postNotificationName(org.telegram.messenger.NotificationCenter.dialogFiltersUpdated);
        org.telegram.messenger.NotificationCenter.getInstance(org.telegram.messenger.UserConfig.selectedAccount).postNotificationName(org.telegram.messenger.NotificationCenter.updateInterfaces, org.telegram.messenger.MessagesController.UPDATE_MASK_ALL);
    }

    @Override
    protected boolean onLongClick(UItem item, View view, int position, float x, float y) {
        return false;
    }
}
