package j2cn;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;

/** Checkbox with label. FIRE toggles. */
public class CheckBox extends Widget {
    public String text;
    public boolean checked;

    public CheckBox(String text, boolean checked) {
        tag = "checkbox";
        this.text = text;
        this.checked = checked;
        focusable = true;
    }

    public int preferredHeight(int width) {
        return UiFont.LINE + style.padV * 2;
    }

    public void paint(Graphics g, UiFont font, Theme theme) {
        int fg = style.fg >= 0 ? style.fg : theme.foreground;
        int box = UiFont.LINE - 2;
        int by = y + (h - box) / 2;
        if (checked) {
            g.setColor(theme.primary);
            g.fillRect(x + style.padH, by, box, box);
            g.setColor(theme.primaryForeground);
            g.drawLine(x + style.padH + 3, by + box / 2, x + style.padH + box / 2, by + box - 3);
            g.drawLine(x + style.padH + box / 2, by + box - 3, x + style.padH + box - 2, by + 2);
        } else {
            g.setColor(theme.card);
            g.fillRect(x + style.padH, by, box, box);
            g.setColor(focused ? theme.ring : theme.border);
            g.drawRect(x + style.padH, by, box - 1, box - 1);
        }
        font.drawLine(g, text, x + style.padH + box + 6, y + (h - UiFont.LINE) / 2,
                w - style.padH - box - 6, 0, false, fg);
    }

    public boolean handleKey(int keyCode, int gameAction) {
        if (gameAction == Canvas.FIRE || keyCode == Canvas.KEY_NUM5) {
            checked = !checked;
            return true;
        }
        return false;
    }
}
