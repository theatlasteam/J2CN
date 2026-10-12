package j2cn;

import javax.microedition.lcdui.Graphics;

/** Read-only progress bar 0..100. */
public class Progress extends Widget {
    public int value;

    public Progress(int value) {
        tag = "progress";
        set(value);
    }

    public void set(int v) {
        value = v < 0 ? 0 : v > 100 ? 100 : v;
    }

    public int preferredHeight(int width) {
        return 8 + style.padV * 2;
    }

    public void paint(Graphics g, UiFont font, Theme theme) {
        int th = 8;
        int ty = y + (h - th) / 2;
        g.setColor(theme.muted);
        fillRound(g, x + style.padH, ty, w - style.padH * 2, th, 4);
        int fw = (w - style.padH * 2) * value / 100;
        g.setColor(theme.primary);
        if (fw > 0) fillRound(g, x + style.padH, ty, fw, th, 4);
    }
}
