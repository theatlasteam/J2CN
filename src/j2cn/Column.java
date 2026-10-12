package j2cn;

import javax.microedition.lcdui.Graphics;

/** Vertical stack container. */
public class Column extends Widget {
    private final java.util.Vector children = new java.util.Vector();
    public int gap = 6;

    public Column() {
        tag = "column";
    }

    public Column add(Widget c) {
        children.addElement(c);
        return this;
    }

    public int size() {
        return children.size();
    }

    public Widget child(int i) {
        return (Widget) children.elementAt(i);
    }

    protected void resolveChildren(StyleSheet sheet) {
        Style base = new Style();
        for (int i = 0; i < children.size(); i++) {
            ((Widget) children.elementAt(i)).resolve(sheet, base);
        }
    }

    public int preferredHeight(int width) {
        int total = 0;
        for (int i = 0; i < children.size(); i++) {
            Widget c = (Widget) children.elementAt(i);
            if (c.visible) total += c.preferredHeight(width);
        }
        if (children.size() > 1) total += gap * (children.size() - 1);
        return total + style.padV * 2;
    }

    public void layout(int x, int y, int w) {
        this.x = x;
        this.y = y;
        this.w = w;
        int cy = y + style.padV;
        int cw = w - style.padH * 2;
        for (int i = 0; i < children.size(); i++) {
            Widget c = (Widget) children.elementAt(i);
            if (!c.visible) continue;
            int ch = c.preferredHeight(cw);
            c.x = x + style.padH;
            c.y = cy;
            c.w = cw;
            c.h = ch;
            if (c instanceof Column) ((Column) c).layout(c.x, c.y, cw);
            else if (c instanceof Row) ((Row) c).layout(c.x, c.y, cw);
            else if (c instanceof Card) ((Card) c).layout(c.x, c.y, cw);
            else if (c instanceof Tabs) ((Tabs) c).layout(c.x, c.y, cw);
            cy += ch + gap;
        }
        this.h = cy - y - (children.size() > 0 ? gap : 0) + style.padV;
    }

    public void paint(Graphics g, UiFont font, Theme theme) {
        for (int i = 0; i < children.size(); i++) {
            Widget c = (Widget) children.elementAt(i);
            if (c.visible) c.paint(g, font, theme);
        }
    }

    public void collectFocusable(java.util.Vector out) {
        for (int i = 0; i < children.size(); i++) {
            ((Widget) children.elementAt(i)).collectFocusable(out);
        }
    }
}
