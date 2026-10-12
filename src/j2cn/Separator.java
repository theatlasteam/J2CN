package j2cn;

import javax.microedition.lcdui.Graphics;

/** Thin divider line. */
public class Separator extends Widget {
    public Separator() {
        tag = "separator";
    }

    public int preferredHeight(int width) {
        return 8;
    }

    public void paint(Graphics g, UiFont font, Theme theme) {
        g.setColor(theme.border);
        g.drawLine(x + style.padH, y + 3, x + w - style.padH, y + 3);
    }
}
