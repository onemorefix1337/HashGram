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

public class HashGramPremiumActivity extends UniversalFragment {

    private SharedPreferences prefs;

    @Override
    public boolean onFragmentCreate() {
        prefs = ApplicationLoader.applicationContext.getSharedPreferences("hashgram_config", Context.MODE_PRIVATE);
        return super.onFragmentCreate();
    }

    @Override
    protected CharSequence getTitle() {
        return "Premium и подарки";
    }

    @Override
    protected void fillItems(ArrayList<UItem> items, UniversalAdapter adapter) {
        items.add(UItem.asHeader("Локальный Premium"));
        items.add(UItem.asCheck(1, "Локальный Premium").setChecked(prefs.getBoolean("fg_local_premium", false)));
        items.add(UItem.asCheck(2, "Вырезать спонсорские сообщения (Реклама)").setChecked(prefs.getBoolean("fg_anti_ad", false)));
        items.add(UItem.asCheck(3, "Отключить цензуру (18+ чувствительный контент)").setChecked(prefs.getBoolean("fg_disable_censor", false)));
        items.add(UItem.asShadow(null));

        items.add(UItem.asHeader("Каналы и чаты"));
        items.add(UItem.asCheck(4, "Бесконечный закреп диалогов").setChecked(prefs.getBoolean("fg_unlimited_pins", false)));
        items.add(UItem.asCheck(5, "Снять запрет на копирование/пересылку").setChecked(prefs.getBoolean("fg_anti_noforwards", false)));
        items.add(UItem.asCheck(6, "Копирование выделенной части текста").setChecked(prefs.getBoolean("fg_copy_part", false)));
        items.add(UItem.asShadow(null));

        items.add(UItem.asHeader("Звёзды и подарки"));
        items.add(UItem.asCheck(7, "Скрыть всплывающие окна подарков").setChecked(prefs.getBoolean("fg_hide_gift_popups", false)));
        items.add(UItem.asShadow(null));
    }

    @Override
    protected void onClick(UItem item, View view, int position, float x, float y) {
        item.checked = !item.checked;
        String key = null;
        switch (item.id) {
            case 1: key = "fg_local_premium"; break;
            case 2: key = "fg_anti_ad"; break;
            case 3: key = "fg_disable_censor"; break;
            case 4: key = "fg_unlimited_pins"; break;
            case 5: key = "fg_anti_noforwards"; break;
            case 6: key = "fg_copy_part"; break;
            case 7: key = "fg_hide_gift_popups"; break;
        }
        if (key != null) {
            prefs.edit().putBoolean(key, item.checked).apply();
            if (listView != null && listView.adapter != null) {
                listView.adapter.notifyItemChanged(position);
            }
            BulletinFactory.of(this).createSimpleBulletin(R.raw.done, "Настройка сохранена").show();
        }
    }

    @Override
    protected boolean onLongClick(UItem item, View view, int position, float x, float y) {
        return false;
    }
}
