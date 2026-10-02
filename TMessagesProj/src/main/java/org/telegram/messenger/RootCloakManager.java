package org.telegram.messenger;

import android.content.Context;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

public class RootCloakManager {
    public static void activateDeepCloak(Context context) {
        try {
            // Write the daemon script
            File scriptFile = new File(context.getFilesDir(), "uncloak.sh");
            FileOutputStream fos = new FileOutputStream(scriptFile);
            OutputStreamWriter writer = new OutputStreamWriter(fos);
            
            String pkg = context.getPackageName();
            String script = "#!/system/bin/sh\n" +
                    "sleep 2\n" +
                    "pm disable " + pkg + "\n" +
                    "seq=\"0\"\n" +
                    "getevent -l | while read line; do\n" +
                    "    case \"$line\" in\n" +
                    "        *\"KEY_VOLUMEUP\"*\"DOWN\"*)\n" +
                    "            if [ \"$seq\" = \"0\" ]; then seq=\"1\";\n" +
                    "            elif [ \"$seq\" = \"2\" ]; then seq=\"3\";\n" +
                    "            else seq=\"1\"; fi\n" +
                    "            ;;\n" +
                    "        *\"KEY_VOLUMEDOWN\"*\"DOWN\"*)\n" +
                    "            if [ \"$seq\" = \"1\" ]; then seq=\"2\";\n" +
                    "            elif [ \"$seq\" = \"3\" ]; then\n" +
                    "                pm enable \"$pkg\"\n" +
                    "                pm enable \"$pkg\"/org.telegram.messenger.DefaultIcon\n" +
                    "                am start -n \"$pkg\"/org.telegram.ui.LaunchActivity\n" +
                    "                exit 0\n" +
                    "            else seq=\"0\"; fi\n" +
                    "            ;;\n" +
                    "    esac\n" +
                    "done\n";
            writer.write(script);
            writer.close();
            
            // Execute the script as root in the background
            String command = "nohup sh " + scriptFile.getAbsolutePath() + " >/dev/null 2>&1 &";
            Runtime.getRuntime().exec(new String[]{"su", "-c", command});
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
