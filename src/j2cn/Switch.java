package j2cn;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;

/** iOS-style switch with label. FIRE or left/right toggles. */
public class Switch extends Widget {
    public String text;
    public boolean on;

    public Switch(String text, boolean on) {
        tag = "switch";
        this.text = text;
        this.on = on;
        focusable = true;
    }

    public int preferredHeight(int width) {
        return UiFont.LINE + style.padV * 2 + 2;
    }

    public void paint(Graphics g, UiFont font, Theme theme) {
        int fg = style.fg >= 0 ? style.fg : theme.foreground;
        if (focused) paintFocusHalo(g, theme, 9);
        int sw = 34;
        int sh = 18;
        int sx = x + w - style.padH - sw;
        int sy = y + (h - sh) / 2;
        g.setColor(on ? theme.primary : theme.muted);
        fillRound(g, sx, sy, sw, sh, 9);
        g.setColor(on ? theme.primaryForeground : theme.mutedForeground);
        int knob = sh - 4;
        g.fillArc(on ? sx + sw - knob - 2 : sx + 2, sy + 2, knob, knob, 0, 360);
        if (focused) {
            g.setColor(theme.focus);
            g.drawArc(sx - 1, sy - 1, sw + 2, sh + 2, 0, 360);
            g.drawArc(sx - 2, sy - 2, sw + 4, sh + 4, 0, 360);
        }
        font.drawLine(g, text, x + style.padH, y + (h - UiFont.LINE) / 2,
                w - style.padH * 2 - sw - 6, 0, focused, fg);
    }

    public boolean handleKey(int keyCode, int gameAction) {
        if (gameAction == Canvas.FIRE || gameAction == Canvas.LEFT || gameAction == Canvas.RIGHT
                || keyCode == Canvas.KEY_NUM5) {
            on = !on;
            return true;
        }
        return false;
    }
}
