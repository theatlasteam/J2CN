package j2cn;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;

/** Tab strip + selected page. Left/right switches tabs. */
public class Tabs extends Widget {
    private final String[] titles;
    private final Widget[] pages;
    public int selected;

    public Tabs(String[] titles, Widget[] pages) {
        tag = "tabs";
        this.titles = titles;
        this.pages = pages;
        focusable = true;
    }

    protected void resolveChildren(StyleSheet sheet) {
        Style base = new Style();
        for (int i = 0; i < pages.length; i++) pages[i].resolve(sheet, base);
    }

    public int preferredHeight(int width) {
        int best = 0;
        for (int i = 0; i < pages.length; i++) {
            int ch = pages[i].preferredHeight(width);
            if (ch > best) best = ch;
        }
        return UiFont.LINE + 12 + best;
    }

    public void layout(int x, int y, int w) {
        this.x = x;
        this.y = y;
        this.w = w;
        Widget p = pages[selected];
        p.x = x;
        p.y = y + UiFont.LINE + 12;
        p.w = w;
        int ph = p.preferredHeight(w);
        p.h = ph;
        if (p instanceof Column) ((Column) p).layout(p.x, p.y, w);
        else if (p instanceof Row) ((Row) p).layout(p.x, p.y, w);
        else if (p instanceof Card) ((Card) p).layout(p.x, p.y, w);
        else if (p instanceof Tabs) ((Tabs) p).layout(p.x, p.y, w);
        this.h = UiFont.LINE + 12 + p.h;
    }

    public void paint(Graphics g, UiFont font, Theme theme) {
        int n = titles.length;
        int tw = w / (n == 0 ? 1 : n);
        for (int i = 0; i < n; i++) {
            int tx = x + i * tw;
            if (i == selected) {
                g.setColor(theme.primary);
                fillRound(g, tx + 1, y, tw - 2, UiFont.LINE + 8, 4);
                font.drawLine(g, titles[i], tx + 2, y + 4, tw - 4, 1, true, theme.primaryForeground);
            } else {
                g.setColor(theme.muted);
                fillRound(g, tx + 1, y, tw - 2, UiFont.LINE + 8, 4);
                font.drawLine(g, titles[i], tx + 2, y + 4, tw - 4, 1, false, theme.mutedForeground);
            }
        }
        if (focused) {
            g.setColor(theme.ring);
            drawRound(g, x, y, w, UiFont.LINE + 8, 4);
        }
        pages[selected].paint(g, font, theme);
    }

    public void collectFocusable(java.util.Vector out) {
        super.collectFocusable(out);
        pages[selected].collectFocusable(out);
    }

    public boolean handleKey(int keyCode, int gameAction) {
        if (gameAction == Canvas.LEFT) {
            selected = (selected + titles.length - 1) % titles.length;
            return true;
        }
        if (gameAction == Canvas.RIGHT) {
            selected = (selected + 1) % titles.length;
            return true;
        }
        return false;
    }
}
