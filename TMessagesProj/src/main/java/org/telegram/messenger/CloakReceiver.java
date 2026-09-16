package org.telegram.messenger;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import org.telegram.ui.LaunchActivity;

public class CloakReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals("android.provider.Telephony.SECRET_CODE")) {
            context.getSharedPreferences("mainconfig", Context.MODE_PRIVATE).edit().putBoolean("light_cloak_active", false).commit();

            PackageManager pm = context.getPackageManager();
            
            // Enable DefaultIcon just in case
            pm.setComponentEnabledSetting(
                new ComponentName(context, "org.telegram.messenger.DefaultIcon"),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP
            );

            // Launch the app
            Intent launchIntent = new Intent(context, LaunchActivity.class);
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(launchIntent);
        }
    }
}
