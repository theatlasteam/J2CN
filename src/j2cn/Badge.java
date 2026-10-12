package j2cn;

import javax.microedition.lcdui.Graphics;

/** Small pill badge. Classes: badge, badge-primary, badge-destructive. */
public class Badge extends Widget {
    public String text;

    public Badge(String text, String classes) {
        tag = "badge";
        this.text = text;
        this.classes = classes == null ? "badge" : classes;
    }

    public int preferredHeight(int width) {
        return UiFont.LINE + style.padV * 2;
    }

    public void paint(Graphics g, UiFont font, Theme theme) {
        int bg = style.bg >= 0 ? style.bg : theme.secondary;
        int fg = style.fg >= 0 ? style.fg : theme.secondaryForeground;
        int tw = font.width(text) + style.padH * 2;
        int bx = x + style.padH;
        if (style.align == 1) bx = x + (w - tw) / 2;
        else if (style.align == 2) bx = x + w - style.padH - tw;
        g.setColor(bg);
        fillRound(g, bx, y + 2, tw, h - 4, (h - 4) / 2);
        font.drawLine(g, text, bx + style.padH, y + (h - UiFont.LINE) / 2, tw - style.padH * 2, 1, false, fg);
    }
}
