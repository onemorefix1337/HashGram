package org.telegram.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.telegram.messenger.ApplicationLoader;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

import java.util.ArrayList;

public class HashGramSettingsIconsActivity extends BaseFragment {

    private ListAdapter listAdapter;
    private RecyclerListView listView;

    private int rowCount;
    private int oldStyleRow;
    private int unifiedColorEnableRow;
    private int unifiedColorValueRow;
    private int individualHeaderRow;
    private int individualStartRow;
    private int individualEndRow;
    private int sectionRow;

    private static class IconItem {
        int iconResId;
        String name;

        IconItem(int resId, String n) {
            iconResId = resId;
            name = n;
        }
    }

    private ArrayList<IconItem> iconItems = new ArrayList<>();

    @Override
    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        
        iconItems.add(new IconItem(org.telegram.messenger.R.drawable.msg_settings, "Настройки HashGram"));
        iconItems.add(new IconItem(org.telegram.messenger.R.drawable.settings_account, "Мой аккаунт"));
        iconItems.add(new IconItem(org.telegram.messenger.R.drawable.settings_chat, "Настройки чатов"));
        iconItems.add(new IconItem(org.telegram.messenger.R.drawable.settings_privacy, "Конфиденциальность"));
        iconItems.add(new IconItem(org.telegram.messenger.R.drawable.settings_sounds, "Уведомления и звуки"));
        iconItems.add(new IconItem(org.telegram.messenger.R.drawable.settings_data, "Данные и память"));
        iconItems.add(new IconItem(org.telegram.messenger.R.drawable.settings_folders, "Папки с чатами"));
        iconItems.add(new IconItem(org.telegram.messenger.R.drawable.settings_devices, "Устройства"));
        iconItems.add(new IconItem(org.telegram.messenger.R.drawable.settings_power, "Энергосбережение"));
        iconItems.add(new IconItem(org.telegram.messenger.R.drawable.settings_language, "Язык"));
        iconItems.add(new IconItem(org.telegram.messenger.R.drawable.settings_premium, "Telegram Premium"));
        iconItems.add(new IconItem(org.telegram.messenger.R.drawable.settings_stars, "Звезды Telegram"));
        iconItems.add(new IconItem(org.telegram.messenger.R.drawable.settings_business, "Telegram для бизнеса"));
        iconItems.add(new IconItem(org.telegram.messenger.R.drawable.settings_gift, "Подарить Premium"));

        updateRows();
        return true;
    }

    private void updateRows() {
        rowCount = 0;
        oldStyleRow = rowCount++;
        unifiedColorEnableRow = rowCount++;
        unifiedColorValueRow = rowCount++;
        sectionRow = rowCount++;
        
        individualHeaderRow = rowCount++;
        individualStartRow = rowCount;
        rowCount += iconItems.size();
        individualEndRow = rowCount;
    }

    @Override
    public View createView(Context context) {
        actionBar.setBackButtonImage(org.telegram.messenger.R.drawable.ic_ab_back);
        actionBar.setTitle("Иконки настроек");
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                }
            }
        });

        listAdapter = new ListAdapter(context);
        fragmentView = new FrameLayout(context);
        fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        FrameLayout frameLayout = (FrameLayout) fragmentView;

        listView = new RecyclerListView(context);
        listView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        listView.setVerticalScrollBarEnabled(false);
        listView.setAdapter(listAdapter);
        frameLayout.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        listView.setOnItemClickListener((view, position) -> {
            SharedPreferences prefs = ApplicationLoader.applicationContext.getSharedPreferences("hashgram_config", Context.MODE_PRIVATE);
            
            if (position == oldStyleRow) {
                boolean val = prefs.getBoolean("fg_old_settings_icons", false);
                prefs.edit().putBoolean("fg_old_settings_icons", !val).apply();
                if (view instanceof TextCheckCell) ((TextCheckCell) view).setChecked(!val);
            } else if (position == unifiedColorEnableRow) {
                boolean val = prefs.getBoolean("fg_unified_settings_icons", false);
                prefs.edit().putBoolean("fg_unified_settings_icons", !val).apply();
                if (view instanceof TextCheckCell) ((TextCheckCell) view).setChecked(!val);
                listAdapter.notifyDataSetChanged();
            } else if (position == unifiedColorValueRow) {
                showColorPicker(context, prefs, "fg_unified_icon_color", 0xff1488E1);
            } else if (position >= individualStartRow && position < individualEndRow) {
                int iconResId = iconItems.get(position - individualStartRow).iconResId;
                showColorPicker(context, prefs, "fg_icon_color_" + iconResId, 0);
            }
        });

        return fragmentView;
    }
    
    private void showColorPicker(Context context, SharedPreferences prefs, String prefKey, int defValue) {
        // We'll use a simple list of colors in an AlertDialog to avoid complex ColorPicker integration
        int[] colors = new int[]{
            0xFF1488E1, // Blue (Default)
            0xFFE77512, // Orange
            0xFF60B044, // Green
            0xFFDF3955, // Red
            0xFFB659FF, // Purple
            0xFF00C7C7, // Cyan
            0xFF949494, // Gray
            0xFF000000, // Black
            0xFFFFFFFF, // White
            0           // Reset (Default/transparent depending on mode)
        };
        
        String[] names = new String[]{
            "Синий (Blue)",
            "Оранжевый (Orange)",
            "Зеленый (Green)",
            "Красный (Red)",
            "Фиолетовый (Purple)",
            "Бирюзовый (Cyan)",
            "Серый (Gray)",
            "Черный (Black)",
            "Белый (White)",
            "Сбросить (По умолчанию)"
        };
        
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle("Выберите цвет");
        builder.setItems(names, (dialog, which) -> {
            int selectedColor = colors[which];
            if (selectedColor == 0) {
                prefs.edit().remove(prefKey).apply();
            } else {
                prefs.edit().putInt(prefKey, selectedColor).apply();
            }
            if (listView != null) {
                listView.invalidateViews();
            }
        });
        showDialog(builder.create());
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {

        private Context mContext;

        public ListAdapter(Context context) {
            mContext = context;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int position = holder.getAdapterPosition();
            return position != sectionRow && position != individualHeaderRow;
        }

        @Override
        public int getItemCount() {
            return rowCount;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new HeaderCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 1:
                    view = new org.telegram.ui.Cells.TextSettingsCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 2:
                    view = new ShadowSectionCell(mContext);
                    break;
                case 3:
                default:
                    view = new TextCheckCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            SharedPreferences prefs = ApplicationLoader.applicationContext.getSharedPreferences("hashgram_config", Context.MODE_PRIVATE);
            switch (holder.getItemViewType()) {
                case 0: {
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    if (position == individualHeaderRow) {
                        headerCell.setText("Цвета иконок по отдельности");
                    }
                    break;
                }
                case 1: {
                    org.telegram.ui.Cells.TextSettingsCell textCell = (org.telegram.ui.Cells.TextSettingsCell) holder.itemView;
                    if (position == unifiedColorValueRow) {
                        textCell.setText("Выбрать единый цвет", false);
                    } else if (position >= individualStartRow && position < individualEndRow) {
                        IconItem item = iconItems.get(position - individualStartRow);
                        int color = prefs.getInt("fg_icon_color_" + item.iconResId, 0);
                        String colorStr = color == 0 ? "(По умолчанию)" : "(Настроен)";
                        textCell.setText(item.name + " " + colorStr, position != individualEndRow - 1);
                    }
                    break;
                }
                case 2: {
                    ShadowSectionCell shadowCell = (ShadowSectionCell) holder.itemView;
                    if (position == sectionRow) {
                        shadowCell.setBackgroundDrawable(Theme.getThemedDrawable(mContext, org.telegram.messenger.R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                    }
                    break;
                }
                case 3: {
                    TextCheckCell checkCell = (TextCheckCell) holder.itemView;
                    if (position == oldStyleRow) {
                        boolean val = prefs.getBoolean("fg_old_settings_icons", false);
                        checkCell.setTextAndCheck("Старый стиль (без фона)", val, true);
                    } else if (position == unifiedColorEnableRow) {
                        boolean val = prefs.getBoolean("fg_unified_settings_icons", false);
                        checkCell.setTextAndCheck("Единый цвет иконок", val, true);
                    }
                    break;
                }
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position == individualHeaderRow) return 0;
            if (position == unifiedColorValueRow || (position >= individualStartRow && position < individualEndRow)) return 1;
            if (position == sectionRow) return 2;
            if (position == oldStyleRow || position == unifiedColorEnableRow) return 3;
            return 1;
        }
    }

    @Override
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{HeaderCell.class, org.telegram.ui.Cells.TextSettingsCell.class, TextCheckCell.class}, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        return themeDescriptions;
    }
}
