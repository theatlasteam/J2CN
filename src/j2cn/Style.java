package j2cn;

/** Resolved per-widget style. Integer fields keep CLDC allocation flat. */
public final class Style {
    public int bg = -1;
    public int fg = -1;
    public int border = -1;
    public int borderWidth = 1;
    public int radius = 4;
    public int padH = 8;
    public int padV = 6;
    public int bold = 0;
    /** 0 left, 1 center, 2 right. */
    public int align = 0;

    public Style copy() {
        Style s = new Style();
        s.bg = bg; s.fg = fg; s.border = border; s.borderWidth = borderWidth;
        s.radius = radius; s.padH = padH; s.padV = padV; s.bold = bold; s.align = align;
        return s;
    }
}
