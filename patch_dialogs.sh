#!/bin/bash
FILE="TMessagesProj/src/main/java/org/telegram/ui/DialogsActivity.java"

# Add HashGramUpdater import
if ! grep -q "import org.telegram.messenger.HashGramUpdater;" "$FILE"; then
    sed -i '' '/import org.telegram.messenger.MessagesController;/a\
import org.telegram.messenger.HashGramUpdater;
' "$FILE"
fi

# Add update icon variable
if ! grep -q "private ActionBarMenuItem updateItem;" "$FILE"; then
    sed -i '' '/private ActionBarMenuItem searchItem;/a\
    private ActionBarMenuItem updateItem;
' "$FILE"
fi

# Add the update icon in createMenu
if ! grep -q "updateItem = menu.addItem(2, R.drawable.msg_download);" "$FILE"; then
    sed -i '' '/passcodeItem = menu.addItem(1, R.drawable.outline_header_lock_24);/a\
            updateItem = menu.addItem(2, R.drawable.msg_download);\
            updateItem.setContentDescription("Скачивание обновления");\
            updateItem.setOnClickListener(v -> {\
                HashGramUpdater.showProgressBottomSheet(getParentActivity());\
            });\
            updateItem.setVisibility(HashGramUpdater.isDownloading ? View.VISIBLE : View.GONE);
' "$FILE"
fi

# Add UpdaterDelegate to DialogsActivity
if ! grep -q "HashGramUpdater.delegate = new HashGramUpdater.UpdaterDelegate" "$FILE"; then
    sed -i '' '/super.onResume();/a\
        if (updateItem != null) {\
            updateItem.setVisibility(HashGramUpdater.isDownloading ? View.VISIBLE : View.GONE);\
        }\
        HashGramUpdater.delegate = new HashGramUpdater.UpdaterDelegate() {\
            @Override\
            public void onProgressChanged(int progress) {\
                if (updateItem != null && updateItem.getVisibility() != View.VISIBLE) {\
                    updateItem.setVisibility(View.VISIBLE);\
                }\
            }\
            @Override\
            public void onDownloadComplete() {\
                if (updateItem != null) updateItem.setVisibility(View.GONE);\
            }\
            @Override\
            public void onDownloadFailed() {\
                if (updateItem != null) updateItem.setVisibility(View.GONE);\
            }\
        };\
' "$FILE"
fi

