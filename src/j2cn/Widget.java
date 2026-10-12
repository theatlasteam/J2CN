package j2cn;

import javax.microedition.lcdui.Graphics;

/** Base widget. Java 1.3 compatible: no generics, no enums. */
public abstract class Widget {

    public String tag = "widget";
    public String id;
    public String classes;
    public int x, y, w, h;
    public boolean focusable;
    public boolean focused;
    public boolean visible = true;
    protected Style style;

    public void resolve(StyleSheet sheet, Style base) {
        style = sheet.styleFor(tag, id, classes, base);
        resolveChildren(sheet);
    }

    protected void resolveChildren(StyleSheet sheet) {}

    public Style style() {
        return style;
    }

    /** Preferred height at the given width (font line = 18px). */
    public abstract int preferredHeight(int width);

    public abstract void paint(Graphics g, UiFont font, Theme theme);

    /** Return true if the key was consumed. */
    public boolean handleKey(int keyCode, int gameAction) {
        return false;
    }

    public void collectFocusable(java.util.Vector out) {
        if (visible && focusable) out.addElement(this);
    }

    // --- drawing helpers (MIDP has no roundRect) ---

    public static void fillRound(Graphics g, int x, int y, int w, int h, int r) {
        if (r <= 0 || w <= 2 * r || h <= 2 * r) {
            g.fillRect(x, y, w, h);
            return;
        }
        g.fillRect(x + r, y, w - 2 * r, h);
        g.fillRect(x, y + r, w, h - 2 * r);
        g.fillArc(x, y, 2 * r, 2 * r, 90, 90);
        g.fillArc(x + w - 2 * r, y, 2 * r, 2 * r, 0, 90);
        g.fillArc(x, y + h - 2 * r, 2 * r, 2 * r, 180, 90);
        g.fillArc(x + w - 2 * r, y + h - 2 * r, 2 * r, 2 * r, 270, 90);
    }

    public static void drawRound(Graphics g, int x, int y, int w, int h, int r) {        if (r <= 0 || w <= 2 * r || h <= 2 * r) {
            g.drawRect(x, y, w - 1, h - 1);
            return;
        }
        g.drawLine(x + r, y, x + w - r - 1, y);
        g.drawLine(x + r, y + h - 1, x + w - r - 1, y + h - 1);
        g.drawLine(x, y + r, x, y + h - r - 1);
        g.drawLine(x + w - 1, y + r, x + w - 1, y + h - r - 1);
        g.drawArc(x, y, 2 * r, 2 * r, 90, 90);
        g.drawArc(x + w - 2 * r, y, 2 * r, 2 * r, 0, 90);
        g.drawArc(x, y + h - 2 * r, 2 * r, 2 * r, 180, 90);
        g.drawArc(x + w - 2 * r, y + h - 2 * r, 2 * r, 2 * r, 270, 90);
    }

    /**
     * Unmissable focus treatment: pale halo fill behind the widget plus a
     * double-thick vivid outline. Call first in paint() when focused; the
     * widget then paints its own body over the middle, leaving a bright
     * frame visible around it.
     */
    protected void paintFocusHalo(Graphics g, Theme theme, int radius) {
        g.setColor(theme.focusHalo);
        fillRound(g, x - 3, y - 2, w + 6, h + 4, radius + 3);
        g.setColor(theme.focus);
        drawRound(g, x - 3, y - 2, w + 6, h + 4, radius + 3);
        drawRound(g, x - 2, y - 1, w + 4, h + 2, radius + 2);
    }

    protected static int lines(String s) {
        int n = 1;
        for (int i = 0; i < s.length(); i++) if (s.charAt(i) == '\n') n++;
        return n;
    }
}
