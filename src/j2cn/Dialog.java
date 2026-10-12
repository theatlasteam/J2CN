package j2cn;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;

/** Modal dialog overlay with title, message and up to 2 buttons. */
public class Dialog extends Widget {
    public String title;
    public String message;
    public String okText = "OK";
    public String cancelText;
    public boolean open;
    public boolean confirmed;
    private boolean focusOk = true;

    public Dialog(String title, String message) {
        tag = "dialog";
        this.title = title;
        this.message = message;
    }

    public int preferredHeight(int width) {
        return lines(message) * UiFont.LINE + UiFont.LINE + 60;
    }

    public void paint(Graphics g, UiFont font, Theme theme) {
        if (!open) return;
        int sw = w > 0 ? w : 220;
        int sx = x;
        int dw = sw - 40;
        if (dw < 140) dw = 140;
        int dx = sx + (sw - dw) / 2;
        int dy = y + 60;
        int dh = preferredHeight(dw);
        g.setColor(0x000000);
        // dim: sparse dither lines, cheap on device
        for (int yy = y; yy < y + h; yy += 3) g.drawLine(sx, yy, sx + sw - 1, yy);
        g.setColor(theme.card);
        fillRound(g, dx, dy, dw, dh, 6);
        g.setColor(theme.border);
        drawRound(g, dx, dy, dw, dh, 6);
        font.drawLine(g, title, dx + 10, dy + 8, dw - 20, 0, true, theme.foreground);
        int my = dy + 8 + UiFont.LINE + 4;
        int start = 0;
        String m = message;
        for (int i = 0; i <= m.length(); i++) {
            if (i == m.length() || m.charAt(i) == '\n') {
                font.drawLine(g, m.substring(start, i), dx + 10, my, dw - 20, 0, false, theme.mutedForeground);
                my += UiFont.LINE;
                start = i + 1;
            }
        }
        int bw = (dw - 30) / (cancelText != null ? 2 : 1);
        int by = dy + dh - UiFont.LINE - 16;
        g.setColor(theme.primary);
        int okx = cancelText != null ? dx + 10 + bw + 10 : dx + 10;
        if (focusOk) {
            g.setColor(theme.focus);
            fillRound(g, okx - 2, by - 2, bw + 4, UiFont.LINE + 14, 6);
            g.setColor(theme.primary);
            fillRound(g, okx, by, bw, UiFont.LINE + 10, 4);
        } else {
            fillRound(g, okx, by, bw, UiFont.LINE + 10, 4);
        }
        font.drawLine(g, okText, okx, by + 5, bw, 1, true, theme.primaryForeground);
        if (cancelText != null) {
            g.setColor(theme.muted);
            int cx = dx + 10;
            if (!focusOk) {
                g.setColor(theme.focus);
                fillRound(g, cx - 2, by - 2, bw + 4, UiFont.LINE + 14, 6);
                g.setColor(theme.muted);
                fillRound(g, cx, by, bw, UiFont.LINE + 10, 4);
            } else {
                fillRound(g, cx, by, bw, UiFont.LINE + 10, 4);
            }
            font.drawLine(g, cancelText, cx, by + 5, bw, 1, false, theme.mutedForeground);
        }
    }

    public boolean handleKey(int keyCode, int gameAction) {
        if (!open) return false;
        if (gameAction == Canvas.LEFT || gameAction == Canvas.RIGHT) {
            if (cancelText != null) focusOk = !focusOk;
            return true;
        }
        if (gameAction == Canvas.FIRE || keyCode == Canvas.KEY_NUM5) {
            confirmed = focusOk;
            open = false;
            return true;
        }
        return true;
    }
}
