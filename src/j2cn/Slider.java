package j2cn;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;

/** 0..100 slider. Left/right adjusts by 5. */
public class Slider extends Widget {
    public String label;
    public int value;

    public Slider(String label, int value) {
        tag = "slider";
        this.label = label;
        set(value);
        focusable = true;
    }

    public void set(int v) {
        value = v < 0 ? 0 : v > 100 ? 100 : v;
    }

    public int preferredHeight(int width) {
        return UiFont.LINE * 2 + style.padV * 2 + 2;
    }

    public void paint(Graphics g, UiFont font, Theme theme) {
        font.drawLine(g, label + " " + value + "%", x + style.padH, y + style.padV,
                w - style.padH * 2, 0, false, theme.mutedForeground);
        int ty = y + style.padV + UiFont.LINE + 4;
        int th = 6;
        g.setColor(theme.muted);
        fillRound(g, x + style.padH, ty, w - style.padH * 2, th, 3);
        int fw = (w - style.padH * 2) * value / 100;
        g.setColor(theme.primary);
        if (fw > 0) fillRound(g, x + style.padH, ty, fw, th, 3);
        int kx = x + style.padH + fw - 6;
        if (kx < x + style.padH) kx = x + style.padH;
        g.setColor(theme.card);
        g.fillArc(kx, ty - 4, 12, 12, 0, 360);
        g.setColor(focused ? theme.ring : theme.border);
        g.drawArc(kx, ty - 4, 12, 12, 0, 360);
    }

    public boolean handleKey(int keyCode, int gameAction) {
        if (gameAction == Canvas.LEFT) {
            set(value - 5);
            return true;
        }
        if (gameAction == Canvas.RIGHT) {
            set(value + 5);
            return true;
        }
        return false;
    }
}
