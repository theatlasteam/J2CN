package j2cn;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;

/**
 * Text input. FIRE requests native editing; UiScreen opens a TextBox
 * and writes the result back via setText.
 */
public class Input extends Widget {
    public String label;
    private StringBuffer value = new StringBuffer();
    public boolean editRequested;

    public Input(String label, String initial) {
        tag = "input";
        this.label = label;
        if (initial != null) value.append(initial);
        focusable = true;
    }

    public String getText() {
        return value.toString();
    }

    public void setText(String s) {
        value.setLength(0);
        if (s != null) value.append(s);
    }

    public int preferredHeight(int width) {
        return UiFont.LINE * 2 + style.padV * 2 + 4;
    }

    public void paint(Graphics g, UiFont font, Theme theme) {
        int bg = style.bg >= 0 ? style.bg : theme.card;
        int fg = style.fg >= 0 ? style.fg : theme.foreground;
        if (focused) paintFocusHalo(g, theme, style.radius);
        g.setColor(bg);
        fillRound(g, x, y, w, h, style.radius);
        int b = style.border >= 0 ? style.border : (focused ? theme.focus : theme.input);
        g.setColor(b);
        drawRound(g, x, y, w, h, style.radius);
        if (focused) {
            drawRound(g, x + 1, y + 1, w - 2, h - 2, style.radius > 0 ? style.radius - 1 : 0);
        }
        font.drawLine(g, label, x + style.padH, y + style.padV, w - style.padH * 2, 0, false, theme.mutedForeground);
        String v = value.length() == 0 ? "..." : value.toString();
        int vc = value.length() == 0 ? theme.mutedForeground : fg;
        font.drawLine(g, v, x + style.padH, y + style.padV + UiFont.LINE, w - style.padH * 2, 0, false, vc);
        if (focused && value.length() > 0) {
            int tw = font.width(v);
            g.setColor(theme.focus);
            g.fillRect(x + style.padH + tw + 1, y + style.padV + UiFont.LINE, 2, UiFont.LINE - 2);
        }
    }

    public boolean handleKey(int keyCode, int gameAction) {
        if (gameAction == Canvas.FIRE || keyCode == Canvas.KEY_NUM5) {
            editRequested = true;
            return true;
        }
        return false;
    }
}
