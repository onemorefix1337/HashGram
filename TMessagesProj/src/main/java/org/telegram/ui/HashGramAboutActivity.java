package org.telegram.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.browser.Browser;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Components.LayoutHelper;

import java.util.ArrayList;

public class HashGramAboutActivity extends BaseFragment {

    @Override
    public View createView(Context context) {
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setAllowOverlayTitle(true);
        actionBar.setTitle("О проекте HashGram");
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                }
            }
        });

        fragmentView = new FrameLayout(context);
        fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        FrameLayout frameLayout = (FrameLayout) fragmentView;

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        frameLayout.addView(linearLayout, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL | Gravity.TOP, 0, 72, 0, 0));

        ImageView imageView = new ImageView(context);
        imageView.setImageResource(R.drawable.hashgram_glyph);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        int pad = AndroidUtilities.dp(14);
        imageView.setPadding(pad, pad, pad, pad);
        android.graphics.drawable.GradientDrawable tile = new android.graphics.drawable.GradientDrawable(
                android.graphics.drawable.GradientDrawable.Orientation.TL_BR, new int[]{0xFF7A5CFF, 0xFF3E7BFF});
        tile.setCornerRadius(AndroidUtilities.dp(22));
        imageView.setBackground(tile);
        linearLayout.addView(imageView, LayoutHelper.createLinear(80, 80, Gravity.CENTER_HORIZONTAL));

        TextView titleTextView = new org.telegram.ui.Components.TypefaceTextView(context);
        titleTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);
        titleTextView.setTypeface(AndroidUtilities.bold());
        titleTextView.setText("HashGram");
        titleTextView.setGravity(Gravity.CENTER_HORIZONTAL);
        linearLayout.addView(titleTextView, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL, 0, 16, 0, 0));

        String versionName = "?";
        try {
            versionName = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0).versionName;
        } catch (Exception e) {
            org.telegram.messenger.FileLog.e(e);
        }

        TextView versionTextView = new org.telegram.ui.Components.TypefaceTextView(context);
        versionTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
        versionTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        versionTextView.setText("v" + org.telegram.messenger.BuildConfig.HASHGRAM_VERSION + " (" + org.telegram.messenger.BuildConfig.HASHGRAM_BUILD_HASH + ")  \u00b7  Telegram " + versionName);
        versionTextView.setGravity(Gravity.CENTER_HORIZONTAL);
        linearLayout.addView(versionTextView, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL, 0, 6, 0, 0));

        TextView descriptionTextView = new org.telegram.ui.Components.TypefaceTextView(context);
        descriptionTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
        descriptionTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        descriptionTextView.setGravity(Gravity.CENTER_HORIZONTAL);
        descriptionTextView.setLineSpacing(AndroidUtilities.dp(3), 1f);
        descriptionTextView.setText("Клиент Telegram для тех, кто ценит приватность, эстетику и свободу.");
        linearLayout.addView(descriptionTextView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL, 48, 28, 48, 0));

        TextView githubButton = new org.telegram.ui.Components.TypefaceTextView(context);
        githubButton.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4));
        githubButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        githubButton.setTypeface(AndroidUtilities.bold());
        githubButton.setGravity(Gravity.CENTER_HORIZONTAL);
        githubButton.setText("Исходный код на GitHub");
        githubButton.setPadding(AndroidUtilities.dp(24), AndroidUtilities.dp(14), AndroidUtilities.dp(24), AndroidUtilities.dp(14));
        githubButton.setBackground(Theme.createSelectorDrawable(Theme.getColor(Theme.key_listSelector), 2));
        githubButton.setOnClickListener(v -> Browser.openUrl(getParentActivity(), "https://github.com/onemorefix1337/HashGram"));
        linearLayout.addView(githubButton, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL, 0, 20, 0, 0));

        return fragmentView;
    }

    @Override
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        themeDescriptions.add(new ThemeDescription(fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        return themeDescriptions;
    }
}
