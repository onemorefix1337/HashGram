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

        items.add(UItem.asHeader("Камера и Медиа"));
        items.add(UItem.asCheck(7, "Использовать системную камеру").setChecked(!SharedConfig.inappCamera));
        items.add(UItem.asCheck(11, "Не паузить музыку при записи ГС").setChecked(!SharedConfig.pauseMusicOnRecord));
        items.add(UItem.asCheck(18, "Оригинальное качество фото по умолчанию").setChecked(prefs.getBoolean("fg_original_photo", false)));
        items.add(UItem.asShadow(null));
    }

    @Override
    protected void onClick(UItem item, View view, int position, float x, float y) {
        item.checked = !item.checked;
        
        if (item.id == 7) {
            SharedConfig.toggleInappCamera();
        } else if (item.id == 11) {
            SharedConfig.togglePauseMusicOnRecord();
        } else {
            String key = null;
            switch (item.id) {
                case 15: key = "fg_unlimited_pins"; break;
                case 17: key = "fg_copy_part"; break;
                case 18: key = "fg_original_photo"; break;
                case 20: key = "fg_premium_speed"; break;
            }

            if (key != null) {
                prefs.edit().putBoolean(key, item.checked).apply();
                if (key.equals("fg_copy_part")) SharedConfig.fg_copy_part = item.checked;
                if (key.equals("fg_original_photo")) SharedConfig.fg_original_photo = item.checked;
                if (key.equals("fg_premium_speed")) SharedConfig.fg_premium_speed = item.checked;
            }
        }
        if (listView.getAdapter() != null) {
            listView.getAdapter().notifyItemChanged(position);
        }
    }
}
