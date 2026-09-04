import re

with open('/Users/user/FireGram/TMessagesProj/src/main/java/org/telegram/ui/ChatActivity.java', 'r') as f:
    content = f.read()

replacement = """options.add(OPTION_COPY);
                    if (org.telegram.messenger.SharedConfig.fg_copy_part) {
                        items.add("Выделить текст");
                        options.add(OPTION_FG_SELECT_TEXT);
                        icons.add(R.drawable.msg_edit);
                    }"""
# Note: we need to handle the indentation correctly, but Python re.sub can just match `options.add(OPTION_COPY);` and replace it with `options.add(OPTION_COPY);\n if (org.telegram.messenger.SharedConfig.fg_copy_part) {\n items.add("Выделить текст");\n options.add(OPTION_FG_SELECT_TEXT);\n icons.add(R.drawable.msg_edit);\n }`
# Wait, this would replace ALL occurrences of OPTION_COPY, including for Gifts where we don't want it!
