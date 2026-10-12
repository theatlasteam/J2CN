package j2cn;

import javax.microedition.lcdui.Graphics;

/** Single or multi-line text. */
public class Label extends Widget {
    public String text;

    public Label(String text) {
        this(text, null);
    }

    public Label(String text, String classes) {
        tag = "label";
        this.classes = classes;
        this.text = text;
    }

    public int preferredHeight(int width) {
        return lines(text) * UiFont.LINE + style.padV * 2;
    }

    public void paint(Graphics g, UiFont font, Theme theme) {
        int fg = style.fg >= 0 ? style.fg : theme.foreground;
        int y = this.y + style.padV;
        int start = 0;
        for (int i = 0; i <= text.length(); i++) {
            if (i == text.length() || text.charAt(i) == '\n') {
                font.drawLine(g, text.substring(start, i), x + style.padH, y,
                        w - style.padH * 2, style.align, style.bold != 0, fg);
                y += UiFont.LINE;
                start = i + 1;
            }
        }
    }
}
