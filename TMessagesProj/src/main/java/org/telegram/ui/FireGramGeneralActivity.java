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

public class FireGramGeneralActivity extends UniversalFragment {

    public static SharedPreferences getPrefs() {
        return ApplicationLoader.applicationContext.getSharedPreferences("firegram_config", Context.MODE_PRIVATE);
    }

    private SharedPreferences prefs;

    @Override
    public boolean onFragmentCreate() {
        prefs = getPrefs();
        return super.onFragmentCreate();
    }

    @Override
    protected CharSequence getTitle() {
        return "Основные твики";
    }

    @Override
    protected void fillItems(ArrayList<UItem> items, UniversalAdapter adapter) {
        items.add(UItem.asHeader("Чаты и Папки"));
        items.add(UItem.asCheck(1, "Скрыть вкладку «Все чаты»").setChecked(prefs.getBoolean("fg_hide_all_chats", false)));
        items.add(UItem.asCheck(2, "Скрыть Истории (Stories)").setChecked(prefs.getBoolean("fg_hide_stories", false)));
        items.add(UItem.asCheck(3, "Компактный список чатов").setChecked(prefs.getBoolean("fg_compact_chats", false)));
        items.add(UItem.asCheck(12, "Полоска папок снизу (iOS)").setChecked(prefs.getBoolean("fg_folders_bottom", false)));
        items.add(UItem.asCheck(15, "Бесконечный закреп чатов").setChecked(prefs.getBoolean("fg_unlimited_pins", false)));
        items.add(UItem.asCheck(19, "Отключение свайпа папок").setChecked(prefs.getBoolean("fg_disable_folder_swipe", false)));
        items.add(UItem.asShadow(null));

        items.add(UItem.asHeader("Сообщения"));
        items.add(UItem.asCheck(4, "Карандаш вместо «изменено»").setChecked(prefs.getBoolean("fg_pencil_edited", false)));
        items.add(UItem.asCheck(5, "Подтверждение голосовых/видео").setChecked(prefs.getBoolean("fg_confirm_voice", false)));
        items.add(UItem.asCheck(6, "Точное время (с секундами)").setChecked(prefs.getBoolean("fg_seconds_time", false)));
        items.add(UItem.asCheck(14, "Без реакций по двойному тапу").setChecked(prefs.getBoolean("fg_disable_double_tap", false)));
        items.add(UItem.asCheck(16, "Вырезать спонсорские сообщения").setChecked(prefs.getBoolean("fg_anti_ad", false)));
        items.add(UItem.asCheck(17, "Копирование части сообщения").setChecked(prefs.getBoolean("fg_copy_part", false)));
        items.add(UItem.asShadow(null));

        items.add(UItem.asHeader("Медиа"));
        items.add(UItem.asCheck(7, "Использовать системную камеру").setChecked(!SharedConfig.inappCamera));
        items.add(UItem.asCheck(8, "Отключить большие эмодзи").setChecked(!SharedConfig.allowBigEmoji));
        items.add(UItem.asCheck(11, "Не паузить музыку при записи ГС").setChecked(!SharedConfig.pauseMusicOnRecord));
        items.add(UItem.asCheck(18, "Оригинальное качество фото по умолчанию").setChecked(prefs.getBoolean("fg_original_photo", false)));
        items.add(UItem.asCheck(20, "Обход ограничения скорости (Premium)").setChecked(prefs.getBoolean("fg_premium_speed", false)));
        items.add(UItem.asCheck(21, "Отключить зацикливание стикеров").setChecked(prefs.getBoolean("fg_disable_sticker_loop", false)));
        items.add(UItem.asShadow(null));

        items.add(UItem.asHeader("Профиль"));
        items.add(UItem.asCheck(9, "Показывать ID и DC").setChecked(prefs.getBoolean("fg_show_id_dc", false)));
        items.add(UItem.asCheck(10, "Скрыть свой номер телефона").setChecked(prefs.getBoolean("fg_hide_phone", false)));
        items.add(UItem.asCheck(13, "Отключить цензуру (18+)").setChecked(prefs.getBoolean("fg_disable_censor", false)));
        items.add(UItem.asShadow(null));
    }

    @Override
    protected void onClick(UItem item, View view, int position, float x, float y) {
        item.checked = !item.checked;
        
        if (item.id == 7) {
            SharedConfig.toggleInappCamera();
        } else if (item.id == 8) {
            SharedConfig.toggleBigEmoji();
        } else if (item.id == 11) {
            SharedConfig.togglePauseMusicOnRecord();
        } else {
            String key = null;
            switch (item.id) {
                case 1: key = "fg_hide_all_chats"; break;
                case 2: key = "fg_hide_stories"; break;
                case 3: key = "fg_compact_chats"; break;
                case 4: key = "fg_pencil_edited"; break;
                case 5: key = "fg_confirm_voice"; break;
                case 6: key = "fg_seconds_time"; break;
                case 9: key = "fg_show_id_dc"; break;
                case 10: key = "fg_hide_phone"; break;
                case 12: key = "fg_folders_bottom"; break;
                case 13: key = "fg_disable_censor"; break;
                case 14: key = "fg_disable_double_tap"; break;
                case 15: key = "fg_unlimited_pins"; break;
                case 16: key = "fg_anti_ad"; break;
                case 17: key = "fg_copy_part"; break;
                case 18: key = "fg_original_photo"; break;
                case 19: key = "fg_disable_folder_swipe"; break;
                case 20: key = "fg_premium_speed"; break;
                case 21: key = "fg_disable_sticker_loop"; break;
            }

            if (key != null) {
                prefs.edit().putBoolean(key, item.checked).apply();
                if (key.equals("fg_anti_ad")) SharedConfig.fg_anti_ad = item.checked;
                if (key.equals("fg_copy_part")) SharedConfig.fg_copy_part = item.checked;
                if (key.equals("fg_original_photo")) SharedConfig.fg_original_photo = item.checked;
                if (key.equals("fg_disable_folder_swipe")) SharedConfig.fg_disable_folder_swipe = item.checked;
                if (key.equals("fg_premium_speed")) SharedConfig.fg_premium_speed = item.checked;
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
