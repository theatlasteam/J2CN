package j2cn;

import javax.microedition.lcdui.Graphics;

/** ShadCN card: bordered surface wrapping one content widget. */
public class Card extends Widget {
    private Widget content;

    public Card(Widget content, String classes) {
        tag = "card";
        this.classes = classes;
        this.content = content;
    }

    protected void resolveChildren(StyleSheet sheet) {
        if (content != null) content.resolve(sheet, new Style());
    }

    public int preferredHeight(int width) {
        int inner = width - style.padH * 2;
        return content.preferredHeight(inner) + style.padV * 2;
    }

    public void layout(int x, int y, int w) {
        this.x = x;
        this.y = y;
        this.w = w;
        int inner = w - style.padH * 2;
        int ch = content.preferredHeight(inner);
        content.x = x + style.padH;
        content.y = y + style.padV;
        content.w = inner;
        content.h = ch;
        if (content instanceof Column) ((Column) content).layout(content.x, content.y, inner);
        else if (content instanceof Row) ((Row) content).layout(content.x, content.y, inner);
        else if (content instanceof Card) ((Card) content).layout(content.x, content.y, inner);
        else if (content instanceof Tabs) ((Tabs) content).layout(content.x, content.y, inner);
        this.h = ch + style.padV * 2;
    }

    public void paint(Graphics g, UiFont font, Theme theme) {
        int bg = style.bg >= 0 ? style.bg : theme.card;
        g.setColor(bg);
        fillRound(g, x, y, w, h, style.radius);
        if (style.borderWidth > 0) {
            g.setColor(style.border >= 0 ? style.border : theme.border);
            drawRound(g, x, y, w, h, style.radius);
        }
        content.paint(g, font, theme);
    }

    public void collectFocusable(java.util.Vector out) {
        content.collectFocusable(out);
    }
}
