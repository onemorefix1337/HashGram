#!/usr/bin/env python3
"""Generates TMessagesProj/src/main/assets/hashgram_dark.attheme from night.attheme.

HashGram "Aurora" palette: ink-black surfaces with a faint violet tint, violet -> azure accent,
gradient outgoing bubbles. Everything not explicitly overridden is hue-remapped from the stock Night theme
(blue accents become violet, neutrals get a subtle cool-violet tint) so the theme stays coherent.
"""
import colorsys, os, sys

ASSETS = os.path.join(os.path.dirname(__file__), '..', 'TMessagesProj', 'src', 'main', 'assets')

def parse(path):
    d = {}
    for line in open(path):
        line = line.strip()
        if '=' in line:
            k, v = line.split('=', 1)
            if v.startswith('#'):
                h = v[1:]
                v = int(h, 16) if len(h) == 8 else 0xFF000000 | int(h, 16)
                v = to_signed(v)
            else:
                try:
                    v = int(v)
                except ValueError:
                    continue
            d[k] = v
    return d

def to_signed(argb):
    argb &= 0xFFFFFFFF
    return argb - (1 << 32) if argb & 0x80000000 else argb

def argb(a, rgb):
    return to_signed((a << 24) | (rgb & 0xFFFFFF))

def rgb_of(v):
    v &= 0xFFFFFFFF
    return (v >> 24) & 0xFF, (v >> 16) & 0xFF, (v >> 8) & 0xFF, v & 0xFF

ACC_HUE = 258 / 360.0   # violet

def remap(v):
    a, r, g, b = rgb_of(v)
    h, s, val = colorsys.rgb_to_hsv(r / 255, g / 255, b / 255)
    hd = h * 360
    if s > 0.25 and 190 <= hd <= 255:                    # blues -> violet
        h = ((258 + (hd - 215) * 0.25) % 360) / 360
        s = min(1.0, s * 1.05)
    elif s <= 0.25 and val < 0.55:                       # dark neutrals -> ink with violet tint
        h = 250 / 360
        s = 0.30 * (1 - val) if val > 0.02 else 0.0
        val = val * 0.78
    elif s <= 0.25:                                      # light neutrals: whisper of tint
        h = 250 / 360
        s = min(s, 0.05) if val > 0.8 else s
    else:
        return v
    r2, g2, b2 = colorsys.hsv_to_rgb(h, s, val)
    return argb(a, (round(r2 * 255) << 16) | (round(g2 * 255) << 8) | round(b2 * 255))

def c(hex6, alpha=0xFF):
    return argb(alpha, int(hex6.lstrip('#'), 16))

night = parse(os.path.join(ASSETS, 'night.attheme'))
day = parse(os.path.join(ASSETS, 'day.attheme'))
out = {k: remap(v) for k, v in night.items()}

BG        = '#08080D'   # app background / wallpaper
SURFACE   = '#0F0F17'   # lists, cards
ELEVATED  = '#171724'   # panels, dialogs, popups
BUBBLE_IN = '#1A1A28'
ACCENT    = '#8B7BFF'
ACCENT_2  = '#46C6FF'
TEXT      = '#F4F3FF'
HINT      = '#8C8AA6'

o = {
    'windowBackgroundGray': c(BG), 'windowBackgroundWhite': c(SURFACE),
    'actionBarDefault': c(SURFACE), 'actionBarDefaultSelector': c('#FFFFFF', 0x14),
    'actionBarDefaultSubmenuBackground': c(ELEVATED), 'actionBarActionModeDefault': c(ELEVATED),
    'actionBarActionModeDefaultTop': c(ELEVATED),
    'dialogBackground': c(ELEVATED), 'chats_menuBackground': c(SURFACE), 'chats_menuTopBackgroundCats': c(SURFACE),
    'chat_wallpaper': c('#0B0820'), 'chat_wallpaper_gradient_to': c('#07131F'), 'key_chat_wallpaper_gradient_to2': c('#150A2B'),
    'chat_wallpaper_gradient_rotation': 315, 'chat_messagePanelBackground': c(SURFACE),
    'chat_inBubble': c(BUBBLE_IN), 'chat_inBubbleSelected': c('#232338'),
    'chat_outBubble': c('#6A4DFF'), 'chat_outBubbleGradient': c('#3E7BFF'),
    'chat_outBubbleSelected': c('#5A3FE6'),
    'chat_outTimeText': c('#E2DCFF', 0xCC), 'chat_outViews': c('#E2DCFF', 0xCC), 'chat_outSentClock': c('#E2DCFF', 0xCC),
    'chat_outSentCheck': c('#FFFFFF'), 'chat_outSentCheckRead': c('#FFFFFF'),
    'chat_messageTextIn': c(TEXT), 'chat_messageTextOut': c('#FFFFFF'),
    'chat_messageLinkIn': c('#A99CFF'), 'chat_messageLinkOut': c('#E6E2FF'),
    'chat_outReplyLine': c('#D9D3FF'), 'chat_inReplyLine': c(ACCENT),
    'chat_messagePanelSend': c(ACCENT), 'chats_actionBackground': c('#6A4DFF'),
    'chats_unreadCounter': c('#7A5CFF'), 'chats_unreadCounterMuted': c('#33334A'),
    'chats_onlineCircle': c(ACCENT_2), 'chats_name': c(TEXT), 'chats_message': c(HINT),
    'chats_date': c('#6F6D8A'), 'chats_nameMessage': c('#C9C4FF'),
    'chats_sentCheck': c(ACCENT_2), 'chats_sentReadCheck': c(ACCENT_2),
    'windowBackgroundWhiteBlackText': c(TEXT), 'windowBackgroundWhiteGrayText': c(HINT),
    'windowBackgroundWhiteBlueText': c('#A99CFF'), 'windowBackgroundWhiteBlueHeader': c(ACCENT),
    'windowBackgroundWhiteLinkText': c('#A99CFF'), 'windowBackgroundWhiteInputFieldActivated': c(ACCENT),
    'switchTrackChecked': c('#6A4DFF'), 'radioBackgroundChecked': c(ACCENT),
    'featuredStickers_addButton': c('#6A4DFF'), 'featuredStickers_addButtonPressed': c('#5A3FE6'),
    'dialogFloatingButton': c('#6A4DFF'), 'dialogFloatingButtonPressed': c('#5A3FE6'),
    'dialogTextBlue': c('#A99CFF'), 'dialogButton': c('#A99CFF'), 'dialogButtonSelector': c(ACCENT, 0x24),
}
out.update(o)

# Keys that the stock Night theme leaves to the (light, Telegram-blue) built-in defaults: pull the blue ones in too,
# otherwise buttons/FABs/progress bars stay Telegram-blue on the violet theme.
import re
TC = os.path.join(ASSETS, '..', 'java', 'org', 'telegram', 'ui', 'ActionBar', 'ThemeColors.java')
src = open(TC).read()
consts = {m.group(1): int(m.group(2), 16) for m in re.finditer(r'static final int (\w+) = (0x[0-9A-Fa-f]+);', src) if True}
consts = {k: to_signed(v) for k, v in consts.items()}
names = dict(re.findall(r'colorKeysMap\.put\((key_\w+), "(\w+)"\)', src))
filled = 0
for key, expr in re.findall(r'defaultColors\[(key_\w+)\] = ([^;]+);', src):
    name = names.get(key)
    if not name or name in out:
        continue
    expr = expr.strip()
    if expr in consts:
        val = consts[expr]
    elif re.fullmatch(r'0x[0-9A-Fa-f]+', expr):
        val = to_signed(int(expr, 16))
    else:
        continue
    _, r, g, b = rgb_of(val)
    hh, ss, vv = colorsys.rgb_to_hsv(r / 255, g / 255, b / 255)
    if ss > 0.25 and 190 <= hh * 360 <= 255:
        nv = remap(val)
        _, r, g, b = rgb_of(nv)
        hh, ss, vv = colorsys.rgb_to_hsv(r / 255, g / 255, b / 255)
        r, g, b = colorsys.hsv_to_rgb(hh, min(ss, 0.75), max(vv, 0.9))
        out[name] = argb(rgb_of(val)[0], (round(r * 255) << 16) | (round(g * 255) << 8) | round(b * 255))
        filled += 1
out.update({k: v for k, v in o.items()})
print('filled', filled, 'blue defaults')
out['chat_outBubbleGradientAnimated'] = 0

with open(os.path.join(ASSETS, 'hashgram_dark.attheme'), 'w') as f:
    for k in sorted(out):
        f.write('%s=%d\n' % (k, out[k]))
print('wrote', len(out), 'colors')


# ---------------------------------------------------------------------------------------------
# HashGram Light: stock defaults (Telegram "Blue") + day.attheme, remapped, with explicit overrides.
# ---------------------------------------------------------------------------------------------
light = {}
for key, expr in re.findall(r'defaultColors\[(key_\w+)\] = ([^;]+);', src):
    name = names.get(key)
    if not name:
        continue
    expr = expr.strip()
    if expr in consts:
        light[name] = consts[expr]
    elif re.fullmatch(r'0x[0-9A-Fa-f]+', expr):
        light[name] = to_signed(int(expr, 16))
    elif re.fullmatch(r'-?\d+', expr):
        light[name] = int(expr)
light.update(day)
lout = {k: remap(v) for k, v in light.items()}

L_BG, L_SURFACE, L_ELEV = '#F3F2FA', '#FFFFFF', '#FFFFFF'
L_TEXT, L_HINT = '#14131F', '#77758F'
lo = {
    'windowBackgroundGray': c(L_BG), 'windowBackgroundWhite': c(L_SURFACE),
    'actionBarDefault': c(L_SURFACE), 'actionBarDefaultTitle': c(L_TEXT), 'actionBarDefaultIcon': c('#5B4AD6'),
    'actionBarDefaultSelector': c('#6A4DFF', 0x1A),
    'dialogBackground': c(L_ELEV), 'chats_menuBackground': c(L_SURFACE),
    'chat_wallpaper': c('#F1EEFF'), 'chat_wallpaper_gradient_to': c('#E8F1FF'), 'key_chat_wallpaper_gradient_to2': c('#F4ECFF'),
    'chat_wallpaper_gradient_rotation': 315,
    'chat_messagePanelBackground': c(L_SURFACE),
    'chat_inBubble': c('#FFFFFF'), 'chat_inBubbleSelected': c('#EEEAFF'),
    'chat_outBubble': c('#7A5CFF'), 'chat_outBubbleGradient': c('#4F8BFF'), 'chat_outBubbleSelected': c('#6A4DFF'),
    'chat_messageTextIn': c(L_TEXT), 'chat_messageTextOut': c('#FFFFFF'),
    'chat_messageLinkIn': c('#5B3FE6'), 'chat_messageLinkOut': c('#FFFFFF'),
    'chat_outReplyLine': c('#FFFFFF'), 'chat_inReplyLine': c('#6A4DFF'),
    'chat_messagePanelSend': c('#6A4DFF'), 'chats_actionBackground': c('#6A4DFF'),
    'chats_unreadCounter': c('#6A4DFF'), 'chats_onlineCircle': c('#2FB8F5'),
    'chats_name': c(L_TEXT), 'chats_message': c(L_HINT), 'chats_nameMessage': c('#5B3FE6'),
    'chats_sentCheck': c('#2FB8F5'), 'chats_sentReadCheck': c('#2FB8F5'),
    'windowBackgroundWhiteBlackText': c(L_TEXT), 'windowBackgroundWhiteGrayText': c(L_HINT),
    'windowBackgroundWhiteBlueText': c('#5B3FE6'), 'windowBackgroundWhiteBlueHeader': c('#6A4DFF'),
    'windowBackgroundWhiteLinkText': c('#5B3FE6'), 'windowBackgroundWhiteInputFieldActivated': c('#6A4DFF'),
    'featuredStickers_addButton': c('#6A4DFF'), 'featuredStickers_addButtonPressed': c('#5A3FE6'),
    'dialogFloatingButton': c('#6A4DFF'), 'dialogFloatingButtonPressed': c('#5A3FE6'),
    'switchTrackChecked': c('#6A4DFF'), 'radioBackgroundChecked': c('#6A4DFF'),
    'dialogTextBlue': c('#5B3FE6'), 'dialogButton': c('#5B3FE6'),
}
lout.update(lo)
with open(os.path.join(ASSETS, 'hashgram_light.attheme'), 'w') as f:
    for k in sorted(lout):
        f.write('%s=%d\n' % (k, lout[k]))
print('wrote light', len(lout), 'colors')
