package org.telegram.ui.Components;

import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.text.TextPaint;
import android.text.style.CharacterStyle;
import android.text.style.UpdateAppearance;

/** Paints the spanned text with a horizontal two-colour gradient (used for the HashGram wordmark). */
public class HashGramGradientSpan extends CharacterStyle implements UpdateAppearance {

    private final int from, to;
    private final CharSequence text;
    private float cachedSize = -1, cachedWidth;
    private Shader shader;

    public HashGramGradientSpan(CharSequence text, int from, int to) {
        this.text = text;
        this.from = from;
        this.to = to;
    }

    @Override
    public void updateDrawState(TextPaint paint) {
        if (shader == null || cachedSize != paint.getTextSize()) {
            cachedSize = paint.getTextSize();
            cachedWidth = Math.max(1f, paint.measureText(text, 0, text.length()));
            shader = new LinearGradient(0, 0, cachedWidth, 0, from, to, Shader.TileMode.CLAMP);
        }
        paint.setShader(shader);
    }
}
