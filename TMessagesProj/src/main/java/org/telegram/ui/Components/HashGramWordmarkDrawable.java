package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

import org.telegram.messenger.AndroidUtilities;

/** "HashGram" wordmark with a violet -> azure gradient, replaces the Telegram logo in the chat list header. */
public class HashGramWordmarkDrawable extends Drawable {

    private static final String TEXT = "HashGram";
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.SUBPIXEL_TEXT_FLAG);
    private final float textWidth;

    public HashGramWordmarkDrawable() {
        paint.setTypeface(AndroidUtilities.bold());
        paint.setTextSize(AndroidUtilities.dp(21));
        paint.setLetterSpacing(-0.01f);
        textWidth = paint.measureText(TEXT);
        paint.setShader(new LinearGradient(0, 0, textWidth, 0, 0xFFA796FF, 0xFF46C6FF, Shader.TileMode.CLAMP));
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        canvas.drawText(TEXT, getBounds().left, getBounds().centerY() + AndroidUtilities.dp(7.5f), paint);
    }

    @Override
    public int getIntrinsicWidth() {
        return (int) Math.ceil(textWidth);
    }

    @Override
    public int getIntrinsicHeight() {
        return AndroidUtilities.dp(22);
    }

    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        // keep the gradient: the header recolours the stock logo with a flat tint, which would mud it
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}
