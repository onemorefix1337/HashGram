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

public class HashGramGeneralActivity extends UniversalFragment {

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
        return "Основные твики";
    }

    @Override
    protected void fillItems(ArrayList<UItem> items, UniversalAdapter adapter) {
        items.add(UItem.asHeader("Общие"));
        items.add(UItem.asCheck(15, "Бесконечный закреп чатов").setChecked(prefs.getBoolean("fg_unlimited_pins", false)));
        items.add(UItem.asCheck(17, "Копирование части сообщения").setChecked(prefs.getBoolean("fg_copy_part", false)));
        items.add(UItem.asCheck(20, "Обход ограничения скорости (Premium)").setChecked(prefs.getBoolean("fg_premium_speed", false)));
        items.add(UItem.asShadow(null));
        items.add(UItem.asHeader("Профиль и Чаты"));
        items.add(UItem.asCheck(10, "Скрыть свой номер телефона").setChecked(prefs.getBoolean("fg_hide_phone", false)));
        items.add(UItem.asCheck(9, "Показывать ID и DC").setChecked(prefs.getBoolean("fg_show_id_dc", false)));
        items.add(UItem.asCheck(3, "Сохранение удаленок").setChecked(prefs.getBoolean("fg_save_deleted", false)));
        items.add(UItem.asCheck(5, "Подтверждение голосовых/видео").setChecked(prefs.getBoolean("fg_confirm_voice", false)));
        items.add(UItem.asCheck(14, "Без реакций по двойному тапу").setChecked(prefs.getBoolean("fg_disable_double_tap", false)));
        items.add(UItem.asCheck(22, "Разрешить скриншоты везде").setChecked(prefs.getBoolean("fg_allow_screenshots", false)));
        items.add(UItem.asCheck(23, "Скрыть просмотр историй").setChecked(prefs.getBoolean("fg_hide_stories", false)));
        items.add(UItem.asCheck(26, "Снять запрет на пересылку/копирование").setChecked(prefs.getBoolean("fg_anti_noforwards", false)));
        items.add(UItem.asShadow(null));
        items.add(UItem.asHeader("Локальный Premium и Прочее"));
        items.add(UItem.asCheck(25, "Локальный Premium").setChecked(prefs.getBoolean("fg_local_premium", false)));
        items.add(UItem.asCheck(16, "Вырезать спонсорские сообщения").setChecked(prefs.getBoolean("fg_anti_ad", false)));
        items.add(UItem.asCheck(13, "Отключить цензуру (18+)").setChecked(prefs.getBoolean("fg_disable_censor", false)));
        items.add(UItem.asShadow(null));
    }

    @Override
    protected void onClick(UItem item, View view, int position, float x, float y) {
        item.checked = !item.checked;
        
        String key = null;
        switch (item.id) {
            case 15: key = "fg_unlimited_pins"; break;
            case 17: key = "fg_copy_part"; break;
            case 20: key = "fg_premium_speed"; break;
            case 10: key = "fg_hide_phone"; break;
            case 9: key = "fg_show_id_dc"; break;
            case 3: key = "fg_save_deleted"; break;
            case 5: key = "fg_confirm_voice"; break;
            case 14: key = "fg_disable_double_tap"; break;
            case 22: key = "fg_allow_screenshots"; break;
            case 23: key = "fg_hide_stories"; break;
            case 26: key = "fg_anti_noforwards"; break;
            case 25: key = "fg_local_premium"; break;
            case 16: key = "fg_anti_ad"; break;
            case 13: key = "fg_disable_censor"; break;
        }

        if (key != null) {
            prefs.edit().putBoolean(key, item.checked).apply();
            if (key.equals("fg_copy_part")) SharedConfig.fg_copy_part = item.checked;
            if (key.equals("fg_premium_speed")) SharedConfig.fg_premium_speed = item.checked;
            if (key.equals("fg_anti_ad")) SharedConfig.fg_anti_ad = item.checked;
            if (key.equals("fg_local_premium")) {
                org.telegram.messenger.UserConfig.getInstance(org.telegram.messenger.UserConfig.selectedAccount).saveConfig(false);
            }
        }
        if (listView.getAdapter() != null) {
            listView.getAdapter().notifyItemChanged(position);
        }

        org.telegram.messenger.NotificationCenter.getGlobalInstance().postNotificationName(org.telegram.messenger.NotificationCenter.mainUserInfoChanged);
        org.telegram.messenger.NotificationCenter.getInstance(org.telegram.messenger.UserConfig.selectedAccount).postNotificationName(org.telegram.messenger.NotificationCenter.updateInterfaces, org.telegram.messenger.MessagesController.UPDATE_MASK_ALL);
    }

    @Override
    protected boolean onLongClick(UItem item, View view, int position, float x, float y) {
        return false;
    }
}
