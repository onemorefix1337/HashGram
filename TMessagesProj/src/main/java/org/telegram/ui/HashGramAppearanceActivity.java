package org.telegram.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.SharedConfig;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextDetailSettingsCell;
import org.telegram.ui.Cells.ThemePreviewMessagesCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

import java.util.ArrayList;

public class HashGramAppearanceActivity extends BaseFragment {

    private ListAdapter listAdapter;
    private RecyclerListView listView;

    private int rowCount;
    private int themeHeaderRow;
    private int themeAmoledRow;
    private int themeMintRow;
    private int themeLavenderRow;
    private int themeHackerRow;
    private int themeBloodRedRow;
    private int themeSakuraRow;
    private int themeSolarizedLightRow;
    private int themeMidnightBlueRow;
    private int themeSectionRow;

    private int customHeaderRow;
    private int avatarShapeRow;
    private int fontTypeRow;
    private int hideMicCamRow;
    private int customSectionRow;

    @Override
    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        updateRows();
        return true;
    }

    private void updateRows() {
        rowCount = 0;
        themeHeaderRow = rowCount++;
        themeAmoledRow = rowCount++;
        themeMintRow = rowCount++;
        themeLavenderRow = rowCount++;
        themeHackerRow = rowCount++;
        themeBloodRedRow = rowCount++;
        themeSakuraRow = rowCount++;
        themeSolarizedLightRow = rowCount++;
        themeMidnightBlueRow = rowCount++;
        themeSectionRow = rowCount++;

        customHeaderRow = rowCount++;
        avatarShapeRow = rowCount++;
        fontTypeRow = rowCount++;
        hideMicCamRow = rowCount++;
        customSectionRow = rowCount++;
    }

    @Override
    public View createView(Context context) {
        actionBar.setBackButtonImage(org.telegram.messenger.R.drawable.ic_ab_back);
        actionBar.setTitle("Настройки HashGram");
        if (AndroidUtilities.isTablet()) {
            actionBar.setOccupyStatusBar(false);
        }
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
            if (position >= themeAmoledRow && position <= themeMidnightBlueRow) {
                applyThemePreview(position);
            } else if (position == avatarShapeRow) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                builder.setTitle("Форма аватарок");
                builder.setItems(new CharSequence[]{"Круг (по умолчанию)", "Квадрат", "Закругленный квадрат"}, (dialog, which) -> {
                    SharedConfig.fg_avatar_shape = which;
                    SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("hashgram_config", Context.MODE_PRIVATE);
                    preferences.edit().putInt("fg_avatar_shape", which).apply();
                    if (listView != null) {
                        listView.invalidateViews();
                    }
                });
                showDialog(builder.create());
            } else if (position == fontTypeRow) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                builder.setTitle("Шрифт");
                builder.setItems(new CharSequence[]{"По умолчанию (Telegram)", "Системный", "Пользовательский"}, (dialog, which) -> {
                    if (which == 2) {
                        if (android.os.Build.VERSION.SDK_INT >= 30) {
                            if (!android.os.Environment.isExternalStorageManager()) {
                                android.content.Intent intent = new android.content.Intent(android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                                intent.setData(android.net.Uri.parse("package:" + org.telegram.messenger.ApplicationLoader.applicationContext.getPackageName()));
                                getParentActivity().startActivity(intent);
                                android.widget.Toast.makeText(getParentActivity(), "Пожалуйста, предоставьте доступ ко всем файлам для выбора шрифтов.", android.widget.Toast.LENGTH_LONG).show();
                                return;
                            }
                        } else if (android.os.Build.VERSION.SDK_INT >= 23 && getParentActivity().checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                            getParentActivity().requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 4);
                            return;
                        }
                        java.io.File fontsDir = new java.io.File(android.os.Environment.getExternalStorageDirectory(), "Download/HashGram/fonts");
                        if (!fontsDir.exists()) {
                            fontsDir.mkdirs();
                        }
                        java.io.File[] files = fontsDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".ttf") || name.toLowerCase().endsWith(".otf"));
                        if (files == null || files.length == 0) {
                            AlertDialog.Builder emptyBuilder = new AlertDialog.Builder(getParentActivity());
                            emptyBuilder.setTitle("Нет шрифтов");
                            emptyBuilder.setMessage("Поместите файлы шрифтов (.ttf или .otf) в папку Download/HashGram/fonts и попробуйте снова. Также убедитесь, что приложению выданы права на доступ к памяти.");
                            emptyBuilder.setPositiveButton("OK", null);
                            showDialog(emptyBuilder.create());
                            return;
                        }
                        CharSequence[] fileNames = new CharSequence[files.length];
                        for (int i = 0; i < files.length; i++) {
                            fileNames[i] = files[i].getName();
                        }
                        AlertDialog.Builder pickerBuilder = new AlertDialog.Builder(getParentActivity());
                        pickerBuilder.setTitle("Выберите шрифт");
                        pickerBuilder.setItems(fileNames, (pickerDialog, fileIndex) -> {
                            SharedConfig.fg_font_type = 2;
                            SharedConfig.fg_custom_font_path = files[fileIndex].getAbsolutePath();
                            SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("hashgram_config", Context.MODE_PRIVATE);
                            preferences.edit().putInt("fg_font_type", 2).putString("fg_custom_font_path", SharedConfig.fg_custom_font_path).apply();
                            if (listView != null) {
                                listView.invalidateViews();
                            }
                            AlertDialog.Builder restartBuilder = new AlertDialog.Builder(getParentActivity());
                            restartBuilder.setTitle(LocaleController.getString("AppName", org.telegram.messenger.R.string.AppName));
                            restartBuilder.setMessage("Для применения шрифта необходимо перезапустить приложение.");
                            restartBuilder.setPositiveButton(LocaleController.getString("OK", org.telegram.messenger.R.string.OK), (restartDialog, i) -> {
                                System.exit(0);
                            });
                            showDialog(restartBuilder.create());
                        });
                        showDialog(pickerBuilder.create());
                    } else {
                        SharedConfig.fg_font_type = which;
                        SharedConfig.fg_custom_font_path = "";
                        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("hashgram_config", Context.MODE_PRIVATE);
                        preferences.edit().putInt("fg_font_type", which).putString("fg_custom_font_path", "").apply();
                        if (listView != null) {
                            listView.invalidateViews();
                        }
                        AlertDialog.Builder restartBuilder = new AlertDialog.Builder(getParentActivity());
                        restartBuilder.setTitle(LocaleController.getString("AppName", org.telegram.messenger.R.string.AppName));
                        restartBuilder.setMessage("Для применения шрифта необходимо перезапустить приложение.");
                        restartBuilder.setPositiveButton(LocaleController.getString("OK", org.telegram.messenger.R.string.OK), (dialogInterface, i) -> {
                            System.exit(0);
                        });
                        showDialog(restartBuilder.create());
                    }
                });
                showDialog(builder.create());
            } else if (position == hideMicCamRow) {
                SharedConfig.fg_hide_mic_cam = !SharedConfig.fg_hide_mic_cam;
                SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("hashgram_config", Context.MODE_PRIVATE);
                preferences.edit().putBoolean("fg_hide_mic_cam", SharedConfig.fg_hide_mic_cam).apply();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(SharedConfig.fg_hide_mic_cam);
                }
            }
        });

        return fragmentView;
    }

    private void applyThemePreview(int position) {
        String themeName = "HashGram Theme";
        boolean isDark = true;
        int actionbarDefaultColor = 0xFF000000;
        int backgroundColor = 0xFF000000;
        int msgInColor = 0xFF333333;
        int msgOutColor = 0xFF555555;
        int textColor = 0xFFFFFFFF; // added for better text visibility

        Theme.ThemeInfo baseTheme = Theme.getTheme("Night");

        if (position == themeAmoledRow) {
            themeName = "AMOLED";
            isDark = true;
            baseTheme = Theme.getTheme("Night");
            actionbarDefaultColor = 0xFF000000;
            backgroundColor = 0xFF000000;
            msgInColor = 0xFF111111;
            msgOutColor = 0xFF222222;
            textColor = 0xFFFFFFFF;
        } else if (position == themeMintRow) {
            themeName = "Mint";
            isDark = false;
            baseTheme = Theme.getTheme("Day");
            actionbarDefaultColor = 0xFF98FF98;
            backgroundColor = 0xFFE0FFE0;
            msgInColor = 0xFFFFFFFF;
            msgOutColor = 0xFFC0FFC0;
            textColor = 0xFF000000;
        } else if (position == themeLavenderRow) {
            themeName = "Lavender";
            isDark = false;
            baseTheme = Theme.getTheme("Day");
            actionbarDefaultColor = 0xFFE6E6FA;
            backgroundColor = 0xFFF8F8FF;
            msgInColor = 0xFFFFFFFF;
            msgOutColor = 0xFFD8BFD8;
            textColor = 0xFF000000;
        } else if (position == themeHackerRow) {
            themeName = "Hacker";
            isDark = true;
            baseTheme = Theme.getTheme("Night");
            actionbarDefaultColor = 0xFF000000;
            backgroundColor = 0xFF000000;
            msgInColor = 0xFF001100;
            msgOutColor = 0xFF003300;
            textColor = 0xFF00FF00;
        } else if (position == themeBloodRedRow) {
            themeName = "Blood Red";
            isDark = true;
            baseTheme = Theme.getTheme("Night");
            actionbarDefaultColor = 0xFF660000;
            backgroundColor = 0xFF110000;
            msgInColor = 0xFF330000;
            msgOutColor = 0xFF550000;
            textColor = 0xFFFFFFFF;
        } else if (position == themeSakuraRow) {
            themeName = "Sakura";
            isDark = false;
            baseTheme = Theme.getTheme("Day");
            actionbarDefaultColor = 0xFFFFB7C5;
            backgroundColor = 0xFFFFF0F5;
            msgInColor = 0xFFFFFFFF;
            msgOutColor = 0xFFFFC0CB;
            textColor = 0xFF000000;
        } else if (position == themeSolarizedLightRow) {
            themeName = "Solarized Light";
            isDark = false;
            baseTheme = Theme.getTheme("Day");
            actionbarDefaultColor = 0xFFFDF6E3;
            backgroundColor = 0xFFEEE8D5;
            msgInColor = 0xFFFDF6E3;
            msgOutColor = 0xFFE8DDB5;
            textColor = 0xFF657B83;
        } else if (position == themeMidnightBlueRow) {
            themeName = "Midnight Blue";
            isDark = true;
            baseTheme = Theme.getTheme("Night");
            actionbarDefaultColor = 0xFF191970;
            backgroundColor = 0xFF0A0A2A;
            msgInColor = 0xFF111144;
            msgOutColor = 0xFF222266;
            textColor = 0xFFFFFFFF;
        }

        // Safe preview workflow
        
        // 1. Get base colors synchronously
        android.util.SparseIntArray baseColors = null;
        if (baseTheme.pathToFile != null) {
            baseColors = Theme.getThemeFileValues(new java.io.File(baseTheme.pathToFile), baseTheme.assetName, null);
        } else {
            baseColors = Theme.getThemeFileValues(null, baseTheme.assetName, null);
        }
        
        // 2. Create new ThemeInfo using the public factory method
        Theme.ThemeInfo newTheme = Theme.createPreviewTheme(themeName);
        
        // 3. Manually write .attheme file using baseColors
        try {
            java.io.FileOutputStream stream = new java.io.FileOutputStream(newTheme.pathToFile);
            StringBuilder sb = new StringBuilder();
            
            if (baseColors != null) {
                for (int i = 0; i < baseColors.size(); i++) {
                    int key = baseColors.keyAt(i);
                    int color = baseColors.valueAt(i);
                    
                    if (key == Theme.key_actionBarDefault || key == Theme.key_actionBarDefaultSelector) {
                        color = actionbarDefaultColor;
                    } else if (key == Theme.key_windowBackgroundWhite || key == Theme.key_windowBackgroundGray) {
                        color = backgroundColor;
                    } else if (key == Theme.key_chat_inBubble) {
                        color = msgInColor;
                    } else if (key == Theme.key_chat_outBubble) {
                        color = msgOutColor;
                    } else if (key == Theme.key_chat_messageTextIn || 
                               key == Theme.key_chat_messageTextOut || 
                               key == Theme.key_windowBackgroundWhiteBlackText || 
                               key == Theme.key_actionBarDefaultTitle ||
                               key == Theme.key_actionBarDefaultIcon) {
                        if (position == themeHackerRow) {
                            color = 0xFF00FF00;
                        } else if (position == themeSolarizedLightRow) {
                            color = 0xFF657B83;
                        } else {
                            color = textColor;
                        }
                    }
                    
                    String colorName = org.telegram.ui.ActionBar.ThemeColors.getStringName(key);
                    if (colorName != null) {
                        sb.append(colorName).append("=").append(color).append("\n");
                    }
                }
            }
            stream.write(sb.toString().getBytes());
            stream.close();
        } catch (Exception e) {
            org.telegram.messenger.FileLog.e(e);
        }
        
        // 4. Present preview
        Theme.ThemeInfo appliedPreviewTheme = Theme.applyThemeFile(new java.io.File(newTheme.pathToFile), themeName, null, true);
        if (appliedPreviewTheme != null) {
            presentFragment(new ThemePreviewActivity(appliedPreviewTheme, true, 0 /* SCREEN_TYPE_PREVIEW */, false, isDark));
        } else {
            presentFragment(new ThemePreviewActivity(newTheme, true, 0 /* SCREEN_TYPE_PREVIEW */, false, isDark));
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {

        private Context mContext;

        public ListAdapter(Context context) {
            mContext = context;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int position = holder.getAdapterPosition();
            return (position >= themeAmoledRow && position <= themeMidnightBlueRow) ||
                   position == avatarShapeRow || position == fontTypeRow || position == hideMicCamRow;
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
                    view = new TextCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 2:
                    view = new ShadowSectionCell(mContext);
                    break;
                case 3:
                    view = new TextDetailSettingsCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 4:
                default:
                    view = new TextCheckCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 0: {
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    if (position == themeHeaderRow) {
                        headerCell.setText("Темы");
                    } else if (position == customHeaderRow) {
                        headerCell.setText("Кастомизация");
                    }
                    break;
                }
                case 1: {
                    TextCell textCell = (TextCell) holder.itemView;
                    if (position == themeAmoledRow) textCell.setText("AMOLED", true);
                    else if (position == themeMintRow) textCell.setText("Mint", true);
                    else if (position == themeLavenderRow) textCell.setText("Lavender", true);
                    else if (position == themeHackerRow) textCell.setText("Hacker", true);
                    else if (position == themeBloodRedRow) textCell.setText("Blood Red", true);
                    else if (position == themeSakuraRow) textCell.setText("Sakura", true);
                    else if (position == themeSolarizedLightRow) textCell.setText("Solarized Light", true);
                    else if (position == themeMidnightBlueRow) textCell.setText("Midnight Blue", false);
                    break;
                }
                case 2: {
                    ShadowSectionCell shadowCell = (ShadowSectionCell) holder.itemView;
                    if (position == themeSectionRow) {
                        shadowCell.setBackgroundDrawable(Theme.getThemedDrawable(mContext, org.telegram.messenger.R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                    } else if (position == customSectionRow) {
                        shadowCell.setBackgroundDrawable(Theme.getThemedDrawable(mContext, org.telegram.messenger.R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                    }
                    break;
                }
                case 3: {
                    TextDetailSettingsCell textCell = (TextDetailSettingsCell) holder.itemView;
                    if (position == avatarShapeRow) {
                        String shape;
                        if (SharedConfig.fg_avatar_shape == 1) shape = "Квадрат";
                        else if (SharedConfig.fg_avatar_shape == 2) shape = "Закругленный квадрат";
                        else shape = "Круг (по умолчанию)";
                        textCell.setTextAndValue("Форма аватарок", shape, true);
                    } else if (position == fontTypeRow) {
                        String font;
                        if (SharedConfig.fg_font_type == 1) font = "Системный";
                        else if (SharedConfig.fg_font_type == 2) {
                            font = "Пользовательский";
                            if (SharedConfig.fg_custom_font_path != null && !SharedConfig.fg_custom_font_path.isEmpty()) {
                                java.io.File f = new java.io.File(SharedConfig.fg_custom_font_path);
                                font += " (" + f.getName() + ")";
                            }
                        }
                        else font = "По умолчанию (Telegram)";
                        textCell.setTextAndValue("Шрифт", font, true);
                    }
                    break;
                }
                case 4: {
                    TextCheckCell checkCell = (TextCheckCell) holder.itemView;
                    if (position == hideMicCamRow) {
                        checkCell.setTextAndCheck("Скрыть кнопку микрофона/камеры", SharedConfig.fg_hide_mic_cam, false);
                    }
                    break;
                }
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position == themeHeaderRow || position == customHeaderRow) {
                return 0;
            } else if (position >= themeAmoledRow && position <= themeMidnightBlueRow) {
                return 1;
            } else if (position == themeSectionRow || position == customSectionRow) {
                return 2;
            } else if (position == avatarShapeRow || position == fontTypeRow) {
                return 3;
            } else if (position == hideMicCamRow) {
                return 4;
            }
            return 1;
        }
    }

    @Override
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{HeaderCell.class, TextCell.class, TextDetailSettingsCell.class, TextCheckCell.class}, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        return themeDescriptions;
    }
}
