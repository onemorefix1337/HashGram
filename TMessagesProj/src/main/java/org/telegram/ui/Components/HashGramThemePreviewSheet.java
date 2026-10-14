package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.content.res.ColorStateList;
import android.util.SparseIntArray;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.NotificationCenter;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;

import static org.telegram.messenger.AndroidUtilities.dp;

/**
 * Preview of a built-in theme: two miniature screens (chat list and chat) painted with the theme's own colours,
 * and an "Apply" button that switches the theme with the app's circular-reveal animation.
 */
public class HashGramThemePreviewSheet {

    public static void show(Context context, Theme.ThemeInfo info, String title, String subtitle, Runnable onApplied) {
        final SparseIntArray colors = Theme.getThemeFileValues(null, info.assetName, null);
        final boolean toDark = info.isDark();

        BottomSheet.Builder builder = new BottomSheet.Builder(context);
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(dp(20), dp(20), dp(20), dp(16));

        TextView titleView = new TextView(context);
        titleView.setText(title);
        titleView.setTextSize(20);
        titleView.setTypeface(AndroidUtilities.bold());
        titleView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        layout.addView(titleView, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.LEFT));

        TextView subView = new TextView(context);
        subView.setText(subtitle);
        subView.setTextSize(14);
        subView.setTextColor(Theme.getColor(Theme.key_dialogTextGray2));
        layout.addView(subView, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.LEFT, 0, 4, 0, 14));

        layout.addView(new MockView(context, colors), LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 270));

        final int accent = colors.get(Theme.key_featuredStickers_addButton, 0xFF6A4DFF);
        final int accent2 = colors.get(Theme.key_chat_outBubbleGradient1, accent);
        TextView apply = new TextView(context);
        apply.setText("Применить");
        apply.setTextColor(0xFFFFFFFF);
        apply.setTextSize(16);
        apply.setTypeface(AndroidUtilities.bold());
        apply.setGravity(Gravity.CENTER);
        GradientDrawable pill = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{accent, accent2});
        pill.setCornerRadius(dp(26));
        apply.setBackground(new RippleDrawable(ColorStateList.valueOf(0x33FFFFFF), pill, null));
        layout.addView(apply, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 52, Gravity.CENTER_HORIZONTAL, 0, 18, 0, 0));

        TextView cancel = new TextView(context);
        cancel.setText("Отмена");
        cancel.setTextColor(Theme.getColor(Theme.key_dialogTextGray2));
        cancel.setTextSize(15);
        cancel.setGravity(Gravity.CENTER);
        layout.addView(cancel, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 44, Gravity.CENTER_HORIZONTAL, 0, 4, 0, 0));

        builder.setCustomView(layout);
        final BottomSheet sheet = builder.show();

        cancel.setOnClickListener(v -> sheet.dismiss());
        apply.setOnClickListener(v -> {
            int[] loc = new int[2];
            v.getLocationInWindow(loc);
            final int[] pos = new int[]{loc[0] + v.getMeasuredWidth() / 2, loc[1] + v.getMeasuredHeight() / 2};
            sheet.dismiss();
            // let the sheet leave the screen so the window snapshot used by the reveal animation is clean
            AndroidUtilities.runOnUIThread(() -> {
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needSetDayNightTheme, info, false, pos, -1, toDark, null);
                if (onApplied != null) {
                    onApplied.run();
                }
            }, 220);
        });
    }

    /** Two miniature phones painted with a theme's colours. */
    private static class MockView extends View {
        private final SparseIntArray c;
        private final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final RectF r = new RectF();

        MockView(Context context, SparseIntArray colors) {
            super(context);
            this.c = colors;
        }

        private int col(int key, int def) {
            return c.get(key, def);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            final float gap = dp(14);
            final float w = (getWidth() - gap) / 2f;
            final float h = getHeight();
            drawList(canvas, 0, w, h);
            drawChat(canvas, w + gap, w, h);
        }

        private void fill(Canvas canvas, float l, float t, float rr, float b, float radius, int color) {
            p.setShader(null);
            p.setColor(color);
            r.set(l, t, rr, b);
            canvas.drawRoundRect(r, radius, radius, p);
        }

        private void drawList(Canvas canvas, float x, float w, float h) {
            canvas.save();
            r.set(x, 0, x + w, h);
            canvas.clipRect(r);
            // frame
            fill(canvas, x, 0, x + w, h, dp(20), col(Theme.key_windowBackgroundWhite, 0xFF101018));
            // header with a small avatar + wordmark-like bar
            fill(canvas, x + dp(10), dp(12), x + dp(10) + dp(14), dp(12) + dp(14), dp(5), col(Theme.key_featuredStickers_addButton, 0xFF6A4DFF));
            fill(canvas, x + dp(30), dp(15), x + dp(30) + dp(42), dp(15) + dp(8), dp(4), col(Theme.key_windowBackgroundWhiteBlueText, 0xFFA99CFF));
            // search bar
            fill(canvas, x + dp(8), dp(34), x + w - dp(8), dp(34) + dp(14), dp(7), col(Theme.key_windowBackgroundGray, 0xFF08080D));
            // rows
            int[] avatars = {Theme.key_avatar_backgroundBlue, Theme.key_avatar_backgroundOrange, Theme.key_avatar_backgroundGreen, Theme.key_avatar_backgroundPink};
            int[] defaults = {0xFF4A90E2, 0xFFE8963A, 0xFF52B04C, 0xFFD9558A};
            for (int i = 0; i < 4; i++) {
                float ry = dp(56) + i * dp(36);
                fill(canvas, x + dp(8), ry, x + dp(8) + dp(26), ry + dp(26), dp(9), col(avatars[i], defaults[i]));
                fill(canvas, x + dp(40), ry + dp(3), x + dp(40) + dp(40 + (i * 7) % 18), ry + dp(3) + dp(6), dp(3), col(Theme.key_chats_name, 0xFFF4F3FF));
                fill(canvas, x + dp(40), ry + dp(14), x + w - dp(34), ry + dp(14) + dp(5), dp(2.5f), col(Theme.key_chats_message, 0xFF8C8AA6));
                if (i == 0 || i == 2) {
                    fill(canvas, x + w - dp(24), ry + dp(8), x + w - dp(10), ry + dp(8) + dp(12), dp(6), col(Theme.key_chats_unreadCounter, 0xFF7A5CFF));
                }
            }
            // bottom bar + FAB
            fill(canvas, x + dp(10), h - dp(34), x + w - dp(10), h - dp(10), dp(12), col(Theme.key_chats_menuBackground, 0xFF171724));
            r.set(x + w - dp(34), h - dp(66), x + w - dp(10), h - dp(42));
            p.setShader(null);
            p.setColor(col(Theme.key_featuredStickers_addButton, 0xFF6A4DFF));
            canvas.drawCircle(r.centerX(), r.centerY(), dp(12), p);
            canvas.restore();
        }

        private void drawChat(Canvas canvas, float x, float w, float h) {
            canvas.save();
            r.set(x, 0, x + w, h);
            canvas.clipRect(r);
            // wallpaper gradient
            int w1 = col(Theme.key_chat_wallpaper, 0xFF0B0820);
            int w2 = col(Theme.key_chat_wallpaper_gradient_to1, w1);
            p.setShader(new LinearGradient(x, 0, x + w, h, w1, w2, Shader.TileMode.CLAMP));
            r.set(x, 0, x + w, h);
            canvas.drawRoundRect(r, dp(20), dp(20), p);
            p.setShader(null);
            // header
            fill(canvas, x, 0, x + w, dp(40), 0, col(Theme.key_actionBarDefault, 0xFF0F0F17));
            fill(canvas, x + dp(34), dp(12), x + dp(34) + dp(14), dp(12) + dp(14), dp(5), col(Theme.key_avatar_backgroundBlue, 0xFF4A90E2));
            fill(canvas, x + dp(54), dp(15), x + dp(54) + dp(36), dp(15) + dp(8), dp(4), col(Theme.key_actionBarDefaultTitle, 0xFFFFFFFF));
            // incoming
            int inBubble = col(Theme.key_chat_inBubble, 0xFF1A1A28);
            int txtIn = col(Theme.key_chat_messageTextIn, 0xFFF4F3FF);
            fill(canvas, x + dp(8), dp(52), x + dp(8) + dp(84), dp(52) + dp(34), dp(12), inBubble);
            fill(canvas, x + dp(16), dp(60), x + dp(16) + dp(60), dp(60) + dp(5), dp(2.5f), txtIn);
            fill(canvas, x + dp(16), dp(71), x + dp(16) + dp(40), dp(71) + dp(5), dp(2.5f), txtIn);
            // outgoing (gradient)
            int o1 = col(Theme.key_chat_outBubble, 0xFF6A4DFF);
            int o2 = col(Theme.key_chat_outBubbleGradient1, o1);
            float ol = x + w - dp(8) - dp(88), ot = dp(96), orr = x + w - dp(8), ob = dp(96) + dp(34);
            p.setShader(new LinearGradient(ol, ot, orr, ob, o1, o2, Shader.TileMode.CLAMP));
            r.set(ol, ot, orr, ob);
            canvas.drawRoundRect(r, dp(12), dp(12), p);
            p.setShader(null);
            int txtOut = col(Theme.key_chat_messageTextOut, 0xFFFFFFFF);
            fill(canvas, ol + dp(8), ot + dp(8), ol + dp(8) + dp(64), ot + dp(8) + dp(5), dp(2.5f), txtOut);
            fill(canvas, ol + dp(8), ot + dp(19), ol + dp(8) + dp(34), ot + dp(19) + dp(5), dp(2.5f), txtOut);
            // second incoming + outgoing
            fill(canvas, x + dp(8), dp(140), x + dp(8) + dp(66), dp(140) + dp(24), dp(12), inBubble);
            fill(canvas, x + dp(16), dp(149), x + dp(16) + dp(44), dp(149) + dp(5), dp(2.5f), txtIn);
            p.setShader(new LinearGradient(x + w - dp(60), dp(172), x + w - dp(8), dp(196), o1, o2, Shader.TileMode.CLAMP));
            r.set(x + w - dp(60), dp(172), x + w - dp(8), dp(196));
            canvas.drawRoundRect(r, dp(12), dp(12), p);
            p.setShader(null);
            fill(canvas, x + w - dp(52), dp(181), x + w - dp(52) + dp(32), dp(181) + dp(5), dp(2.5f), txtOut);
            // input panel
            fill(canvas, x + dp(8), h - dp(34), x + w - dp(8), h - dp(10), dp(12), col(Theme.key_chat_messagePanelBackground, 0xFF0F0F17));
            fill(canvas, x + dp(18), h - dp(25), x + dp(18) + dp(46), h - dp(25) + dp(6), dp(3), col(Theme.key_chat_messagePanelHint, 0xFF8C8AA6));
            canvas.restore();
        }
    }
}
