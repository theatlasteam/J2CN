package j2cn;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;

/** ShadCN-style button. Variants via classes: btn-secondary, btn-outline, btn-ghost, btn-destructive. */
public class Button extends Widget {
    public String text;
    public Runnable action;

    public Button(String text, String classes, Runnable action) {
        tag = "button";
        this.text = text;
        this.classes = classes == null ? "btn" : classes;
        this.action = action;
        focusable = true;
    }

    public int preferredHeight(int width) {
        return UiFont.LINE + style.padV * 2 + 4;
    }

    public void paint(Graphics g, UiFont font, Theme theme) {
        int bg = style.bg >= 0 ? style.bg : theme.primary;
        int fg = style.fg >= 0 ? style.fg : theme.primaryForeground;
        g.setColor(bg);
        fillRound(g, x, y, w, h, style.radius);
        if (style.borderWidth > 0) {
            int b = style.border >= 0 ? style.border : theme.border;
            g.setColor(b);
            drawRound(g, x, y, w, h, style.radius);
        }
        font.drawLine(g, text, x + style.padH, y + (h - UiFont.LINE) / 2,
                w - style.padH * 2, 1, style.bold != 0, fg);
    }

    public boolean handleKey(int keyCode, int gameAction) {
        if (gameAction == Canvas.FIRE || keyCode == Canvas.KEY_NUM5) {
            if (action != null) action.run();
            return true;
        }
        return false;
    }
}
