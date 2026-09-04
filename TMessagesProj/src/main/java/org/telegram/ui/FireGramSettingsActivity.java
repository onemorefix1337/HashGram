package org.telegram.ui;

import android.view.View;
import java.util.ArrayList;

import org.telegram.messenger.R;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.Components.UniversalAdapter;
import org.telegram.ui.Components.UniversalFragment;

public class FireGramSettingsActivity extends UniversalFragment {

    @Override
    protected CharSequence getTitle() {
        return "Настройки FireGram";
    }

    @Override
    protected void fillItems(ArrayList<UItem> items, UniversalAdapter adapter) {
        items.add(UItem.asHeader("Настройки клиента"));
        items.add(UItem.asButton(1, R.drawable.msg_settings, "Основные"));
        items.add(UItem.asButton(2, R.drawable.msg_theme, "Оформление"));
        items.add(UItem.asButton(3, R.drawable.msg_secret, "Приватность"));
        items.add(UItem.asShadow(null));

        items.add(UItem.asButton(99, R.drawable.msg_info, "О проекте FireGram"));
        items.add(UItem.asShadow(null));
    }

    @Override
    protected void onClick(UItem item, View view, int position, float x, float y) {
        if (item.id == 1) {
            presentFragment(new FireGramGeneralActivity());
        } else if (item.id == 2) {
            // TODO: presentFragment(new FireGramAppearanceActivity());
        } else if (item.id == 3) {
            // TODO: presentFragment(new FireGramPrivacyActivity());
        }
    }

    @Override
    protected boolean onLongClick(UItem item, View view, int position, float x, float y) {
        return false;
    }
}
