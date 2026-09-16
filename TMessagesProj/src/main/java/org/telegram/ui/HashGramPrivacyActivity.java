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
        return "Приватность и Larp";
    }

    @Override
    protected void fillItems(ArrayList<UItem> items, UniversalAdapter adapter) {
                items.add(UItem.asHeader("Режим маскировки"));
        items.add(UItem.asButton(100, "Light Cloak (Скрыть иконку)"));
        items.add(UItem.asButton(101, "Deep Cloak (Требуется Root)"));
        items.add(UItem.asShadow("Light Cloak убирает иконку лаунчера (возврат через код *#*#4274#*#* в звонилке). Deep Cloak полностью замораживает приложение (возврат: Вверх-Вниз-Вверх-Вниз)."));

        items.add(UItem.asHeader("Паранойя (Экстремальная приватность)"));
        items.add(UItem.asCheck(103, "Кнопка Паники (Выход по тряске)").setChecked(prefs.getBoolean("fg_panic_button", false)));
        items.add(UItem.asButton(104, "Установить Фейковый Пароль (Пин-код)"));
        items.add(UItem.asShadow("Кнопка паники мгновенно завершит сессию на всех аккаунтах, если сильно потрясти телефон. Фейковый пароль при вводе скрывает все чаты (нужно включить код-пароль)."));

        items.add(UItem.asHeader("Невидимка (Ghost)"));
        items.add(UItem.asCheck(20, "Призрак (Скрыть статус \"В сети\")").setChecked(prefs.getBoolean("fg_hide_online", false)));
        items.add(UItem.asCheck(24, "Всегда в сети").setChecked(prefs.getBoolean("fg_always_online", false)));
        items.add(UItem.asShadow("Скрывает или замораживает ваш статус онлайна. Обе опции не могут работать одновременно."));
        
        items.add(UItem.asCheck(2, "Нечиталка").setChecked(prefs.getBoolean("fg_ghost_read", false)));
        items.add(UItem.asCheck(4, "Умная Нечиталка (Чтение при ответе)").setChecked(prefs.getBoolean("fg_smart_ghost", false)));
        items.add(UItem.asCheck(21, "Скрыть статус \"Печатает...\"").setChecked(prefs.getBoolean("fg_hide_typing", false)));
        items.add(UItem.asCheck(27, "Полная анонимность (Отправка с задержкой 12с)").setChecked(prefs.getBoolean("fg_anon_mode", false)));
        items.add(UItem.asShadow("Нечиталка не позволит собеседнику узнать, что вы прочли сообщение. Полная анонимность будет отправлять сообщения через Отложенные (с задержкой 12с), чтобы сервер не показывал вас в сети."));

        items.add(UItem.asHeader("Профиль и Чаты"));
        items.add(UItem.asCheck(10, "Скрыть свой номер телефона").setChecked(prefs.getBoolean("fg_hide_phone", false)));
        items.add(UItem.asCheck(9, "Показывать ID и DC").setChecked(prefs.getBoolean("fg_show_id_dc", false)));
        items.add(UItem.asCheck(3, "Сохранение удаленок").setChecked(prefs.getBoolean("fg_save_deleted", false)));
        items.add(UItem.asCheck(5, "Подтверждение голосовых/видео").setChecked(prefs.getBoolean("fg_confirm_voice", false)));
        items.add(UItem.asCheck(14, "Без реакций по двойному тапу").setChecked(prefs.getBoolean("fg_disable_double_tap", false)));
        items.add(UItem.asCheck(22, "Разрешить скриншоты везде").setChecked(prefs.getBoolean("fg_allow_screenshots", false)));
        items.add(UItem.asCheck(23, "Скрыть просмотр историй").setChecked(prefs.getBoolean("fg_hide_stories", false)));
        items.add(UItem.asCheck(26, "Снять запрет на пересылку/копирование").setChecked(prefs.getBoolean("fg_anti_noforwards", false)));
        items.add(UItem.asShadow(null));

        items.add(UItem.asHeader("Локальный Premium и Прочее"));
        items.add(UItem.asCheck(25, "Локальный Premium").setChecked(prefs.getBoolean("fg_local_premium", false)));
        items.add(UItem.asCheck(16, "Вырезать спонсорские сообщения").setChecked(prefs.getBoolean("fg_anti_ad", false)));
        items.add(UItem.asCheck(13, "Отключить цензуру (18+)").setChecked(prefs.getBoolean("fg_disable_censor", false)));
        items.add(UItem.asShadow(null));
    }

    @Override
    protected void onClick(UItem item, View view, int position, float x, float y) {
        item.checked = !item.checked;
        
        String key = null;
        
        if (item.id == 100) {
            org.telegram.ui.ActionBar.AlertDialog.Builder builder = new org.telegram.ui.ActionBar.AlertDialog.Builder(getParentActivity());
            builder.setTitle("Light Cloak");
            builder.setMessage("Приложение исчезнет с рабочего стола. Чтобы вернуть его, наберите *#*#4274#*#* в звонилке (телефоне). Продолжить?");
            builder.setPositiveButton("Да", (dialogInterface, i) -> {
                org.telegram.messenger.ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", android.content.Context.MODE_PRIVATE).edit().putBoolean("light_cloak_active", true).commit();
                android.content.pm.PackageManager pm = getParentActivity().getPackageManager();
                String[] aliases = new String[] {
                    "org.telegram.messenger.DefaultIcon",
                    "org.telegram.messenger.MintIcon",
                    "org.telegram.messenger.GreenIcon",
                    "org.telegram.messenger.VintageIcon",
                    "org.telegram.messenger.AquaIcon",
                    "org.telegram.messenger.PremiumIcon",
                    "org.telegram.messenger.TurboIcon",
                    "org.telegram.messenger.NoxIcon"
                };
                for (String alias : aliases) {
                    pm.setComponentEnabledSetting(
                        new android.content.ComponentName(getParentActivity(), alias),
                        android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                        0
                    );
                }
                getParentActivity().finishAffinity();
                System.exit(0);
            });
            builder.setNegativeButton("Отмена", null);
            showDialog(builder.create());
            return;
        } else if (item.id == 101) {
            org.telegram.ui.ActionBar.AlertDialog.Builder builder = new org.telegram.ui.ActionBar.AlertDialog.Builder(getParentActivity());
            builder.setTitle("Deep Cloak (Root)");
            builder.setMessage("Приложение будет полностью заморожено (pm disable) и исчезнет. Для возврата нажмите кнопки громкости: Вверх-Вниз-Вверх-Вниз. Потребуется доступ Superuser. Продолжить?");
            builder.setPositiveButton("Да", (dialogInterface, i) -> {
                org.telegram.messenger.RootCloakManager.activateDeepCloak(getParentActivity());
                getParentActivity().finishAffinity();
            });
            builder.setNegativeButton("Отмена", null);
            showDialog(builder.create());
            return;
        } else if (item.id == 104) {
            if (SharedConfig.passcodeHash.length() == 0) {
                org.telegram.ui.ActionBar.AlertDialog.Builder builder = new org.telegram.ui.ActionBar.AlertDialog.Builder(getParentActivity());
                builder.setTitle("Ошибка");
                builder.setMessage("Сначала установите обычный код-пароль в настройках Telegram!");
                builder.setPositiveButton("ОК", null);
                showDialog(builder.create());
                return;
            }
            PasscodeActivity fragment = new PasscodeActivity(PasscodeActivity.TYPE_SETUP_CODE);
            fragment.setFakePasscodeMode(true);
            presentFragment(fragment);
            return;
        }
        switch (item.id) {
            case 103: key = "fg_panic_button"; break;
            case 20: key = "fg_hide_online"; break;
            case 24: key = "fg_always_online"; break;
            case 2: key = "fg_ghost_read"; break;
            case 4: key = "fg_smart_ghost"; break;
            case 21: key = "fg_hide_typing"; break;
            case 10: key = "fg_hide_phone"; break;
            case 9: key = "fg_show_id_dc"; break;
            case 3: key = "fg_save_deleted"; break;
            case 5: key = "fg_confirm_voice"; break;
            case 14: key = "fg_disable_double_tap"; break;
            case 22: key = "fg_allow_screenshots"; break;
            case 23: key = "fg_hide_stories"; break;
            case 26: key = "fg_anti_noforwards"; break;
            case 27: key = "fg_anon_mode"; break;
            case 25: key = "fg_local_premium"; break;
            case 16: key = "fg_anti_ad"; break;
            case 13: key = "fg_disable_censor"; break;
        }

        if (key != null) {
            prefs.edit().putBoolean(key, item.checked).apply();
            if (key.equals("fg_panic_button")) SharedConfig.fg_panic_button = item.checked;
            if (key.equals("fg_anti_ad")) SharedConfig.fg_anti_ad = item.checked;
            if (key.equals("fg_local_premium") || key.equals("fg_ghost_read") || key.equals("fg_smart_ghost")) {
                org.telegram.messenger.UserConfig.getInstance(org.telegram.messenger.UserConfig.selectedAccount).saveConfig(false);
            }
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
