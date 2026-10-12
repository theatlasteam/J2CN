package j2cn;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 * Proportional bitmap font. Atlas is 256x352 with 16x16 cells and
 * 351 advance bytes (ASCII U+0020..U+007E then Cyrillic U+0400..U+04FF).
 * Generate with tools/make_font.py. Looks like a clean grotesque
 * (DejaVu Sans) rather than the phone system font.
 */
public final class UiFont {

    public static final int GLYPHS = 351;
    public static final int CELL = 16;
    public static final int LINE = 18;

    private final Image atlas;
    private final byte[] advances;
    private final int[] tinted = new int[256];

    public UiFont(Image atlas, byte[] advances) {
        if (atlas.getWidth() != 256 || atlas.getHeight() != 352) {
            throw new IllegalArgumentException("font atlas must be 256x352");
        }
        if (advances == null || advances.length != GLYPHS) {
            throw new IllegalArgumentException("font metrics must be 351 bytes");
        }
        this.atlas = atlas;
        this.advances = advances;
    }

    private static int glyph(char c) {
        if (c >= 32 && c <= 126) return c - 32;
        if (c >= 1024 && c <= 1279) return 95 + c - 1024;
        if (c == '\u00a0') return 0;
        return '?' - 32;
    }

    public int width(String text) {
        int w = 0;
        for (int i = 0; i < text.length(); i++) w += advances[glyph(text.charAt(i))] & 255;
        return w;
    }

    /** Width of the longest line in a possibly multi-line string. */
    public int widthLines(String text) {
        int best = 0;
        int start = 0;
        for (int i = 0; i <= text.length(); i++) {
            if (i == text.length() || text.charAt(i) == '\n') {
                int w = 0;
                for (int k = start; k < i; k++) w += advances[glyph(text.charAt(k))] & 255;
                if (w > best) best = w;
                start = i + 1;
            }
        }
        return best;
    }

    public void draw(Graphics g, String s, int x, int y, boolean bold, int color) {
        if (color == 0) {
            drawFast(g, s, x, y, bold);
            return;
        }
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\n') continue;
            int id = glyph(c);
            int sx = (id % 16) * CELL;
            int sy = (id / 16) * CELL;
            atlas.getRGB(tinted, 0, CELL, sx, sy, CELL, CELL);
            for (int p = 0; p < CELL * CELL; p++) {
                tinted[p] = (tinted[p] & 0xff000000) | (color & 0xffffff);
            }
            g.drawRGB(tinted, 0, CELL, x, y, CELL, CELL, true);
            if (bold) g.drawRGB(tinted, 0, CELL, x + 1, y, CELL, CELL, true);
            x += advances[id] & 255;
        }
    }

    public void drawFast(Graphics g, String s, int x, int y, boolean bold) {
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\n') continue;
            int id = glyph(c);
            g.drawRegion(atlas, (id % 16) * CELL, (id / 16) * CELL, CELL, CELL, 0,
                    x, y, Graphics.TOP | Graphics.LEFT);
            if (bold) {
                g.drawRegion(atlas, (id % 16) * CELL, (id / 16) * CELL, CELL, CELL, 0,
                        x + 1, y, Graphics.TOP | Graphics.LEFT);
            }
            x += advances[id] & 255;
        }
    }

    /** Draw with alignment inside [x, x+width), clipped to one line with ellipsis. */
    public void drawLine(Graphics g, String s, int x, int y, int width, int align, boolean bold, int color) {
        int w = width(s);
        int dx = x;
        if (align == 1) dx = x + (width - w) / 2;
        else if (align == 2) dx = x + width - w;
        if (w > width) {
            String t = s;
            while (t.length() > 1 && width(t + "..") > width) t = t.substring(0, t.length() - 1);
            draw(g, t + "..", x, y, bold, color);
            return;
        }
        draw(g, s, dx, y, bold, color);
    }
}
