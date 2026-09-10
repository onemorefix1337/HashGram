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

public class HashGramLarpActivity extends UniversalFragment {

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
        return "Larp";
    }

    @Override
    protected void fillItems(ArrayList<UItem> items, UniversalAdapter adapter) {
        items.add(UItem.asHeader("Невидимка"));
        items.add(UItem.asCheck(1, "Призрак").setChecked(prefs.getBoolean("fg_ghost_mode", false)));
        items.add(UItem.asCheck(2, "Нечиталка").setChecked(prefs.getBoolean("fg_ghost_read", false)));
        items.add(UItem.asShadow("В режиме призрака вас не увидят онлайн, а нечиталка не позволит собеседнику узнать, что вы прочли сообщение."));

        items.add(UItem.asHeader("Удаленные сообщения"));
        items.add(UItem.asCheck(3, "Сохранение удаленок").setChecked(prefs.getBoolean("fg_save_deleted", false)));
        items.add(UItem.asShadow("Удаленные другими пользователями сообщения будут оставаться у вас в чате."));
    }

    @Override
    protected void onClick(UItem item, View view, int position, float x, float y) {
        item.checked = !item.checked;
        
        String key = null;
        switch (item.id) {
            case 1: key = "fg_ghost_mode"; break;
            case 2: key = "fg_ghost_read"; break;
            case 3: key = "fg_save_deleted"; break;
        }

        if (key != null) {
            prefs.edit().putBoolean(key, item.checked).apply();
            // Assuming SharedConfig is updated if needed, but standard HashGram handles this via prefs
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
