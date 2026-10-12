package j2cn;

import javax.microedition.lcdui.Graphics;

/** Horizontal row with weighted children. */
public class Row extends Widget {
    private final java.util.Vector children = new java.util.Vector();
    private final java.util.Vector weights = new java.util.Vector();
    public int gap = 6;

    public Row() {
        tag = "row";
    }

    public Row add(Widget c, int weight) {
        children.addElement(c);
        weights.addElement(new Integer(weight <= 0 ? 1 : weight));
        return this;
    }

    public Row add(Widget c) {
        return add(c, 1);
    }

    protected void resolveChildren(StyleSheet sheet) {
        Style base = new Style();
        for (int i = 0; i < children.size(); i++) {
            ((Widget) children.elementAt(i)).resolve(sheet, base);
        }
    }

    public int preferredHeight(int width) {
        int best = 0;
        int n = children.size();
        int[] ws = new int[n];
        for (int i = 0; i < n; i++) ws[i] = ((Integer) weights.elementAt(i)).intValue();
        int[][] cols = Layout.row(0, width, ws, gap);
        for (int i = 0; i < n; i++) {
            Widget c = (Widget) children.elementAt(i);
            if (!c.visible) continue;
            int ch = c.preferredHeight(cols[i][1]);
            if (ch > best) best = ch;
        }
        return best;
    }

    public void layout(int x, int y, int w) {
        this.x = x;
        this.y = y;
        this.w = w;
        int n = children.size();
        int[] ws = new int[n];
        for (int i = 0; i < n; i++) ws[i] = ((Integer) weights.elementAt(i)).intValue();
        int[][] cols = Layout.row(x, w, ws, gap);
        int best = preferredHeight(w);
        for (int i = 0; i < n; i++) {
            Widget c = (Widget) children.elementAt(i);
            if (!c.visible) continue;
            c.x = cols[i][0];
            c.y = y;
            c.w = cols[i][1];
            c.h = best;
            if (c instanceof Column) ((Column) c).layout(c.x, c.y, c.w);
            else if (c instanceof Row) ((Row) c).layout(c.x, c.y, c.w);
            else if (c instanceof Card) ((Card) c).layout(c.x, c.y, c.w);
            else if (c instanceof Tabs) ((Tabs) c).layout(c.x, c.y, c.w);
        }
        this.h = best;
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
