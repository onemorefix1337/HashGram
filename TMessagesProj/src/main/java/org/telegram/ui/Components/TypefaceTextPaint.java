package org.telegram.ui.Components;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.SharedConfig;

public class TypefaceTextPaint extends TextPaint {
    public TypefaceTextPaint() {
        super();
        initTypeface();
    }

    public TypefaceTextPaint(int flags) {
        super(flags);
        initTypeface();
    }

    public TypefaceTextPaint(Paint p) {
        super(p);
        initTypeface();
    }

    private void initTypeface() {
        if (SharedConfig.fg_font_type != 0) {
            Typeface custom = AndroidUtilities.getTypeface(null);
            if (custom != null) {
                setTypeface(custom);
            }
        }
    }
}
