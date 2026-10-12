package j2cn;

/**
 * CSS-like stylesheet for the UI engine. Pure Java, no LCDUI imports,
 * so it runs in desktop unit tests.
 *
 * Supported selectors: tag, .class, #id, tag.class. One selector per
 * rule (comma groups are split). Specificity: tag=1, class=10, id=100;
 * ties break by source order.
 *
 * Supported properties: color, background, border-color, border-width,
 * radius, padding, font-weight, text-align.
 */
public final class StyleSheet {

    /** ShadCN-like defaults; users append their own CSS after it. */
    public static final String DEFAULT_CSS =
        "label { color: #09090b; padding: 2; }\n" +
        ".muted { color: #71717a; }\n" +
        "card { background: #ffffff; border-color: #e4e4e7; border-width: 1; radius: 6; padding: 8; }\n" +
        "button { background: #18181b; color: #fafafa; border-width: 0; radius: 6; padding: 8; font-weight: bold; text-align: center; }\n" +
        ".btn-secondary { background: #f4f4f5; color: #18181b; }\n" +
        ".btn-outline { background: #ffffff; color: #09090b; border-color: #e4e4e7; border-width: 1; }\n" +
        ".btn-ghost { background: #ffffff; color: #09090b; border-width: 0; }\n" +
        ".btn-destructive { background: #ef4444; color: #fafafa; }\n" +
        "input { background: #ffffff; color: #09090b; border-color: #e4e4e7; border-width: 1; radius: 6; padding: 6; }\n" +
        ".badge { background: #f4f4f5; color: #18181b; radius: 8; padding: 4; }\n" +
        ".badge-primary { background: #18181b; color: #fafafa; }\n" +
        ".badge-destructive { background: #ef4444; color: #fafafa; }\n";

    private final String[] selectors = new String[128];
    private final String[] declarations = new String[128];
    private final int[] weights = new int[128];
    private int count;

    public StyleSheet(String css) {
        add(css);
    }

    public void add(String css) {
        if (css == null) return;
        StringBuffer clean = new StringBuffer();
        for (int i = 0; i < css.length(); i++) {
            if (css.startsWith("/*", i)) {
                int end = css.indexOf("*/", i + 2);
                if (end < 0) break;
                i = end + 1;
            } else {
                clean.append(css.charAt(i));
            }
        }
        String src = clean.toString();
        int at = 0;
        while (count < 128) {
            int open = src.indexOf('{', at);
            if (open < 0) break;
            int close = src.indexOf('}', open);
            if (close < 0) break;
            String group = src.substring(at, open).trim();
            String decl = src.substring(open + 1, close);
            at = close + 1;
            if (group.length() > 160 || decl.length() > 2048) continue;
            int start = 0;
            while (start < group.length() && count < 128) {
                int comma = group.indexOf(',', start);
                if (comma < 0) comma = group.length();
                String selector = group.substring(start, comma).trim();
                start = comma + 1;
                if (selector.length() == 0) continue;
                if (selector.indexOf(' ') >= 0 || selector.indexOf('>') >= 0
                        || selector.indexOf(':') >= 0 || selector.indexOf('[') >= 0) continue;
                selectors[count] = selector;
                declarations[count] = decl;
                weights[count] = specificity(selector);
                count++;
            }
        }
    }

    static int specificity(String selector) {
        int hash = selector.indexOf('#');
        int dot = selector.indexOf('.');
        if (hash >= 0) return 100;
        if (dot >= 0) return 10;
        return 1;
    }

    static boolean matches(String selector, String tag, String id, String classes) {
        int hash = selector.indexOf('#');
        int dot = selector.indexOf('.');
        int split = hash >= 0 ? hash : dot;
        String base = split >= 0 ? selector.substring(0, split) : selector;
        if (base.length() > 0 && !base.equals("*") && !base.equalsIgnoreCase(tag)) return false;
        if (hash >= 0) {
            String want = selector.substring(hash + 1);
            int end = want.indexOf('.');
            if (end >= 0) want = want.substring(0, end);
            return id != null && want.equals(id);
        }
        if (dot >= 0) {
            String want = selector.substring(dot + 1);
            return classes != null && (" " + classes + " ").indexOf(" " + want + " ") >= 0;
        }
        return true;
    }

    /** Resolve a style for a widget. Base supplies theme defaults. */
    public Style styleFor(String tag, String id, String classes, Style base) {
        Style out = base.copy();
        for (int tier = 1; tier <= 100; tier = tier == 1 ? 10 : tier == 10 ? 100 : 101) {
            for (int i = 0; i < count; i++) {
                if (weights[i] == tier && matches(selectors[i], tag, id, classes)) {
                    apply(out, declarations[i]);
                }
            }
        }
        return out;
    }

    static void apply(Style out, String decl) {
        int start = 0;
        while (start < decl.length()) {
            int end = decl.indexOf(';', start);
            if (end < 0) end = decl.length();
            String property = decl.substring(start, end).trim();
            start = end + 1;
            int colon = property.indexOf(':');
            if (colon < 0) continue;
            String name = property.substring(0, colon).trim().toLowerCase();
            String value = property.substring(colon + 1).trim().toLowerCase();
            int important = value.indexOf("!important");
            if (important >= 0) value = value.substring(0, important).trim();
            if (name.equals("color")) {
                int c = color(value);
                if (c >= 0) out.fg = c;
            } else if (name.equals("background") || name.equals("background-color")) {
                int c = color(value);
                if (c >= 0) out.bg = c;
            } else if (name.equals("border-color")) {
                int c = color(value);
                if (c >= 0) out.border = c;
            } else if (name.equals("border-width")) {
                out.borderWidth = parseInt(value, 1);
            } else if (name.equals("radius") || name.equals("border-radius")) {
                int r = parseInt(value, 4);
                if (r < 0) r = 0;
                if (r > 12) r = 12;
                out.radius = r;
            } else if (name.equals("padding")) {
                int p = parseInt(value, 6);
                if (p < 0) p = 0;
                if (p > 16) p = 16;
                out.padH = p;
                out.padV = p;
            } else if (name.equals("font-weight")) {
                out.bold = (value.equals("bold") || value.equals("700") || value.equals("800")) ? 1 : 0;
            } else if (name.equals("text-align")) {
                out.align = value.equals("center") ? 1 : value.equals("right") ? 2 : 0;
            }
        }
    }

    static int parseInt(String value, int fallback) {
        try {
            StringBuffer digits = new StringBuffer();
            for (int i = 0; i < value.length(); i++) {
                char c = value.charAt(i);
                if ((c >= '0' && c <= '9') || (c == '-' && digits.length() == 0)) digits.append(c);
                else if (digits.length() > 0) break;
            }
            if (digits.length() == 0) return fallback;
            return Integer.parseInt(digits.toString());
        } catch (Exception e) {
            return fallback;
        }
    }

    /** Parse #rgb, #rrggbb, a few named colors, rgb(r,g,b). -1 if unknown. */
    public static int color(String value) {
        try {
            if (value.startsWith("#")) {
                String h = value.substring(1);
                if (h.length() == 3) {
                    h = "" + h.charAt(0) + h.charAt(0) + h.charAt(1) + h.charAt(1) + h.charAt(2) + h.charAt(2);
                }
                if (h.length() == 6) return Integer.parseInt(h, 16);
            }
            if (value.equals("black")) return 0;
            if (value.equals("white")) return 0xffffff;
            if (value.equals("red")) return 0xff0000;
            if (value.equals("green")) return 0x16a34a;
            if (value.equals("blue")) return 0x2563eb;
            if (value.equals("yellow")) return 0xfacc15;
            if (value.equals("gray") || value.equals("grey")) return 0x808080;
            if (value.equals("transparent")) return -2;
            if (value.startsWith("rgb(") && value.endsWith(")")) {
                String s = value.substring(4, value.length() - 1);
                int a = s.indexOf(',');
                int b = s.indexOf(',', a + 1);
                int r = Integer.parseInt(s.substring(0, a).trim());
                int g = Integer.parseInt(s.substring(a + 1, b).trim());
                int bl = Integer.parseInt(s.substring(b + 1).trim());
                if (r < 0 || r > 255 || g < 0 || g > 255 || bl < 0 || bl > 255) return -1;
                return (r << 16) | (g << 8) | bl;
            }
        } catch (Exception e) {
        }
        return -1;
    }

    public int ruleCount() {
        return count;
    }
}
