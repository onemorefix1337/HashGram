package org.telegram.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import java.util.ArrayList;

import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.Components.UniversalAdapter;
import org.telegram.ui.Components.UniversalFragment;

public class HashGramMediaActivity extends UniversalFragment {

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
        return "Камера и Медиа";
    }

    @Override
    protected void fillItems(ArrayList<UItem> items, UniversalAdapter adapter) {
        items.add(UItem.asHeader("Камера"));
        items.add(UItem.asCheck(5, "Спрашивать камеру перед записью кружка").setChecked(prefs.getBoolean("fg_ask_camera_before_record", false)));

        items.add(UItem.asCheck(1, "Использовать системную камеру").setChecked(!SharedConfig.inappCamera));
        items.add(UItem.asCheck(2, "Включить Camera2 API").setChecked(SharedConfig.isUsingCamera2(UserConfig.selectedAccount)));
        items.add(UItem.asShadow(null));

        items.add(UItem.asHeader("Медиа"));
        items.add(UItem.asCheck(3, "Не паузить музыку при записи ГС").setChecked(!SharedConfig.pauseMusicOnRecord));
        items.add(UItem.asCheck(4, "Оригинальное качество фото по умолчанию").setChecked(prefs.getBoolean("fg_original_photo", false)));
        items.add(UItem.asShadow(null));
    }

    @Override
    protected void onClick(UItem item, View view, int position, float x, float y) {
        item.checked = !item.checked;
        
        if (item.id == 1) {
            SharedConfig.toggleInappCamera();
        } else if (item.id == 2) {
            SharedConfig.toggleUseCamera2(UserConfig.selectedAccount);
        } else if (item.id == 3) {
            SharedConfig.togglePauseMusicOnRecord();
        } else if (item.id == 5) {
            prefs.edit().putBoolean("fg_ask_camera_before_record", item.checked).apply();
            SharedConfig.fg_ask_camera_before_record = item.checked;

        } else if (item.id == 4) {
            prefs.edit().putBoolean("fg_original_photo", item.checked).apply();
            SharedConfig.fg_original_photo = item.checked;
        }

        if (listView.getAdapter() != null) {
            listView.getAdapter().notifyItemChanged(position);
        }
    }

    @Override
    protected boolean onLongClick(UItem item, View view, int position, float x, float y) {
        return false;
    }
}
