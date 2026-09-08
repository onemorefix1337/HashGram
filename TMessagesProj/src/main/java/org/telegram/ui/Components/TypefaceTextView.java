package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.SharedConfig;

public class TypefaceTextView extends TextView {
    public TypefaceTextView(Context context) {
        super(context);
        initTypeface();
    }

    public TypefaceTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initTypeface();
    }

    public TypefaceTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initTypeface();
    }

    public TypefaceTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
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
