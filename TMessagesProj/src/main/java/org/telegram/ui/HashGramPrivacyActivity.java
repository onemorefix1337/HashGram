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

public class HashGramPrivacyActivity extends UniversalFragment {

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
        return "Приватность";
    }

    @Override
    protected void fillItems(ArrayList<UItem> items, UniversalAdapter adapter) {
        items.add(UItem.asHeader("Профиль"));
        items.add(UItem.asCheck(10, "Скрыть свой номер телефона").setChecked(prefs.getBoolean("fg_hide_phone", false)));
        items.add(UItem.asCheck(9, "Показывать ID и DC").setChecked(prefs.getBoolean("fg_show_id_dc", false)));
        items.add(UItem.asShadow(null));

        items.add(UItem.asHeader("Сообщения"));
        items.add(UItem.asCheck(5, "Подтверждение голосовых/видео").setChecked(prefs.getBoolean("fg_confirm_voice", false)));
        items.add(UItem.asCheck(14, "Без реакций по двойному тапу").setChecked(prefs.getBoolean("fg_disable_double_tap", false)));
        items.add(UItem.asCheck(16, "Вырезать спонсорские сообщения").setChecked(prefs.getBoolean("fg_anti_ad", false)));
        items.add(UItem.asCheck(13, "Отключить цензуру (18+)").setChecked(prefs.getBoolean("fg_disable_censor", false)));
        items.add(UItem.asShadow(null));
    }

    @Override
    protected void onClick(UItem item, View view, int position, float x, float y) {
        item.checked = !item.checked;
        
        String key = null;
        switch (item.id) {
            case 10: key = "fg_hide_phone"; break;
            case 9: key = "fg_show_id_dc"; break;
            case 5: key = "fg_confirm_voice"; break;
            case 14: key = "fg_disable_double_tap"; break;
            case 16: key = "fg_anti_ad"; break;
            case 13: key = "fg_disable_censor"; break;
        }

        if (key != null) {
            prefs.edit().putBoolean(key, item.checked).apply();
            if (key.equals("fg_anti_ad")) SharedConfig.fg_anti_ad = item.checked;
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
