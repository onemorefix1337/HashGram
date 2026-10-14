package org.telegram.ui;

import android.view.View;
import java.util.ArrayList;

import org.telegram.messenger.R;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.Components.UniversalAdapter;
import org.telegram.ui.Components.UniversalFragment;

public class HashGramSettingsActivity extends UniversalFragment {

    @Override
    protected CharSequence getTitle() {
        return "Настройки HashGram";
    }

    @Override
    protected void fillItems(ArrayList<UItem> items, UniversalAdapter adapter) {
        items.add(UItem.asCustom(createHero(getContext()), 150));
        items.add(UItem.asHeader("Настройки клиента"));
        items.add(UItem.asButton(1, R.drawable.msg_settings, "Основные"));
        items.add(UItem.asButton(2, R.drawable.msg_theme, "Оформление"));
        items.add(UItem.asButton(3, R.drawable.msg_secret, "Приватность"));
        items.add(UItem.asShadow(null));

        items.add(UItem.asHeader("Медиа"));
        items.add(UItem.asButton(4, R.drawable.msg_camera, "Камера и Медиа"));
        items.add(UItem.asShadow(null));

        items.add(UItem.asButton(99, R.drawable.msg_info, "О проекте HashGram"));
        items.add(UItem.asShadow(null));
    }

    private static android.view.View createHero(android.content.Context context) {
        android.widget.LinearLayout box = new android.widget.LinearLayout(context);
        box.setOrientation(android.widget.LinearLayout.VERTICAL);
        box.setGravity(android.view.Gravity.CENTER);

        android.widget.ImageView logo = new android.widget.ImageView(context);
        logo.setImageResource(R.drawable.hashgram_glyph);
        logo.setScaleType(android.widget.ImageView.ScaleType.CENTER_INSIDE);
        int pad = org.telegram.messenger.AndroidUtilities.dp(10);
        logo.setPadding(pad, pad, pad, pad);
        android.graphics.drawable.GradientDrawable tile = new android.graphics.drawable.GradientDrawable(
                android.graphics.drawable.GradientDrawable.Orientation.TL_BR, new int[]{0xFF7A5CFF, 0xFF3E7BFF});
        tile.setCornerRadius(org.telegram.messenger.AndroidUtilities.dp(18));
        logo.setBackground(tile);
        box.addView(logo, org.telegram.ui.Components.LayoutHelper.createLinear(64, 64, android.view.Gravity.CENTER_HORIZONTAL));

        android.widget.TextView title = new android.widget.TextView(context);
        android.text.SpannableString wordmark = new android.text.SpannableString("HashGram");
        wordmark.setSpan(new org.telegram.ui.Components.HashGramGradientSpan(wordmark, 0xFFA796FF, 0xFF46C6FF), 0, wordmark.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        title.setText(wordmark);
        title.setTextColor(0xFFFFFFFF);
        title.setTextSize(22);
        title.setTypeface(org.telegram.messenger.AndroidUtilities.bold());
        box.addView(title, org.telegram.ui.Components.LayoutHelper.createLinear(org.telegram.ui.Components.LayoutHelper.WRAP_CONTENT, org.telegram.ui.Components.LayoutHelper.WRAP_CONTENT, android.view.Gravity.CENTER_HORIZONTAL, 0, 8, 0, 0));

        android.widget.TextView sub = new android.widget.TextView(context);
        sub.setText("v" + org.telegram.messenger.BuildConfig.HASHGRAM_VERSION + " • " + org.telegram.messenger.BuildConfig.HASHGRAM_BUILD_HASH);
        sub.setTextSize(13);
        sub.setTextColor(org.telegram.ui.ActionBar.Theme.getColor(org.telegram.ui.ActionBar.Theme.key_windowBackgroundWhiteGrayText));
        box.addView(sub, org.telegram.ui.Components.LayoutHelper.createLinear(org.telegram.ui.Components.LayoutHelper.WRAP_CONTENT, org.telegram.ui.Components.LayoutHelper.WRAP_CONTENT, android.view.Gravity.CENTER_HORIZONTAL, 0, 2, 0, 0));
        return box;
    }

    @Override
    protected void onClick(UItem item, View view, int position, float x, float y) {
        if (item.id == 1) {
            presentFragment(new HashGramGeneralActivity());
        } else if (item.id == 2) {
            presentFragment(new HashGramAppearanceActivity());
        } else if (item.id == 3) {
            presentFragment(new HashGramPrivacyActivity());
        } else if (item.id == 4) {
            presentFragment(new HashGramMediaActivity());
        } else if (item.id == 99) {
            presentFragment(new HashGramAboutActivity());
        }
    }

    @Override
    protected boolean onLongClick(UItem item, View view, int position, float x, float y) {
        return false;
    }
}
