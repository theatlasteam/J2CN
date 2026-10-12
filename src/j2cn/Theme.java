package j2cn;

/** ShadCN-inspired design tokens for Java ME. Two built-in themes. */
public final class Theme {
    public final int background;
    public final int foreground;
    public final int card;
    public final int cardForeground;
    public final int primary;
    public final int primaryForeground;
    public final int secondary;
    public final int secondaryForeground;
    public final int muted;
    public final int mutedForeground;
    public final int accent;
    public final int accentForeground;
    public final int destructive;
    public final int destructiveForeground;
    public final int border;
    public final int input;
    public final int ring;
    /** Vivid focus outline, always high-contrast against widget fills. */
    public final int focus;
    /** Pale tint painted behind a focused widget as a halo. */
    public final int focusHalo;

    public Theme(int background, int foreground, int card, int cardForeground,
            int primary, int primaryForeground, int secondary, int secondaryForeground,
            int muted, int mutedForeground, int accent, int accentForeground,
            int destructive, int destructiveForeground, int border, int input, int ring,
            int focus, int focusHalo) {
        this.background = background; this.foreground = foreground;
        this.card = card; this.cardForeground = cardForeground;
        this.primary = primary; this.primaryForeground = primaryForeground;
        this.secondary = secondary; this.secondaryForeground = secondaryForeground;
        this.muted = muted; this.mutedForeground = mutedForeground;
        this.accent = accent; this.accentForeground = accentForeground;
        this.destructive = destructive; this.destructiveForeground = destructiveForeground;
        this.border = border; this.input = input; this.ring = ring;
        this.focus = focus; this.focusHalo = focusHalo;
    }

    public static Theme light() {
        return new Theme(
            0xffffff, 0x09090b, 0xffffff, 0x09090b,
            0x18181b, 0xfafafa, 0xf4f4f5, 0x18181b,
            0xf4f4f5, 0x71717a, 0xf4f4f5, 0x18181b,
            0xef4444, 0xfafafa, 0xe4e4e7, 0xe4e4e7, 0x18181b,
            0x2563eb, 0xdbeafe);
    }

    public static Theme dark() {
        return new Theme(
            0x09090b, 0xfafafa, 0x18181b, 0xfafafa,
            0xfafafa, 0x18181b, 0x27272a, 0xfafafa,
            0x27272a, 0xa1a1aa, 0x27272a, 0xfafafa,
            0x7f1d1d, 0xfafafa, 0x27272a, 0x27272a, 0xd4d4d8,
            0x60a5fa, 0x1e3a5f);
    }
}
