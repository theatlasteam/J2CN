package j2cn;

import javax.microedition.lcdui.Graphics;

/** Circle with initials, ShadCN avatar-fallback style. */
public class Avatar extends Widget {
    public String name;
    private static final int[] COLORS = {0xef4444, 0xf97316, 0x16a34a, 0x2563eb, 0x7c3aed, 0xdb2777};

    public Avatar(String name) {
        tag = "avatar";
        this.name = name == null ? "?" : name;
    }

    public String initials() {
        StringBuffer sb = new StringBuffer();
        int n = 0;
        for (int i = 0; i < name.length() && n < 2; i++) {
            char c = name.charAt(i);
            if (i == 0 || (name.charAt(i - 1) == ' ' && c != ' ')) {
                if (c != ' ') {
                    sb.append(Character.toUpperCase(c));
                    n++;
                }
            }
        }
        if (sb.length() == 0) sb.append('?');
        return sb.toString();
    }

    public int preferredHeight(int width) {
        return 36;
    }

    public void paint(Graphics g, UiFont font, Theme theme) {
        int hash = 0;
        for (int i = 0; i < name.length(); i++) hash = hash * 31 + name.charAt(i);
        if (hash < 0) hash = -hash;
        int bg = COLORS[hash % COLORS.length];
        int d = h - 4;
        if (d > 36) d = 36;
        int cx = x + style.padH;
        if (style.align == 1) cx = x + (w - d) / 2;
        else if (style.align == 2) cx = x + w - style.padH - d;
        int cy = y + (h - d) / 2;
        g.setColor(bg);
        g.fillArc(cx, cy, d, d, 0, 360);
        String in = initials();
        int tw = font.width(in);
        font.draw(g, in, cx + (d - tw) / 2, cy + (d - UiFont.LINE) / 2, true, 0xffffff);
        font.drawLine(g, name, cx + d + 8, y + (h - UiFont.LINE) / 2, w - (cx - x) - d - 8, 0, false, theme.foreground);
    }
}
