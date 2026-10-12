package j2cn;

/** Desktop tests for pure-logic classes (no LCDUI). Run with plain javac/java. */
public final class J2cnTest {

    static int passed;
    static int failed;

    static void check(boolean cond, String name) {
        if (cond) {
            passed++;
        } else {
            failed++;
            System.out.println("FAIL " + name);
        }
    }

    public static void main(String[] args) {
        // Theme tokens differ between modes.
        Theme light = Theme.light();
        Theme dark = Theme.dark();
        check(light.background == 0xffffff, "light bg");
        check(dark.background == 0x09090b, "dark bg");
        check(light.primary != dark.primary, "primary differs");

        // Color parsing.
        check(StyleSheet.color("#18181b") == 0x18181b, "hex6");
        check(StyleSheet.color("#fff") == 0xffffff, "hex3");
        check(StyleSheet.color("red") == 0xff0000, "named");
        check(StyleSheet.color("rgb(10,20,30)") == (10 << 16 | 20 << 8 | 30), "rgb");
        check(StyleSheet.color("nope") == -1, "unknown");

        // Defaults give buttons a dark fill.
        StyleSheet sheet = new StyleSheet(StyleSheet.DEFAULT_CSS);
        check(sheet.ruleCount() > 5, "default rules");
        Style base = new Style();
        Style btn = sheet.styleFor("button", null, "btn", base);
        check(btn.bg == 0x18181b && btn.fg == 0xfafafa, "btn default");

        // Class overrides tag; id overrides class; source order breaks ties.
        StyleSheet s2 = new StyleSheet(
            "button { background: #111111; color: #222222; }\n" +
            ".btn-outline { background: #ffffff; }\n" +
            "#login { color: #333333; }\n");
        Style o = s2.styleFor("button", null, "btn-outline", new Style());
        check(o.bg == 0xffffff && o.fg == 0x222222, "class beats tag");
        Style o2 = s2.styleFor("button", "login", "btn-outline", new Style());
        check(o2.fg == 0x333333, "id beats class");

        // Radius/padding/bold/align properties.
        StyleSheet s3 = new StyleSheet("card { radius: 6; padding: 8; font-weight: bold; text-align: center; }");
        Style c = s3.styleFor("card", null, null, new Style());
        check(c.radius == 6 && c.padH == 8 && c.padV == 8 && c.bold == 1 && c.align == 1, "box props");
        StyleSheet s4 = new StyleSheet("input { border-width: 0; }");
        check(s4.styleFor("input", null, null, new Style()).borderWidth == 0, "border width");

        // Layout math.
        int[] ys = Layout.stack(10, new int[]{20, 30, 10}, 5);
        check(ys[0] == 10 && ys[1] == 35 && ys[2] == 70, "stack offsets");
        check(Layout.stackTotal(new int[]{20, 30, 10}, 5) == 70, "stack total");
        int[][] cols = Layout.row(0, 100, new int[]{1, 1}, 10);
        check(cols[0][0] == 0 && cols[0][1] == 45 && cols[1][0] == 55 && cols[1][1] == 45, "row halves");
        int[][] w = Layout.row(0, 100, new int[]{2, 1, 1}, 0);
        check(w[0][1] == 50 && w[1][1] == 25 && w[2][0] == 75, "row weights");

        System.out.println("j2cn: " + passed + " passed, " + failed + " failed");
        if (failed > 0) System.exit(1);
    }
}
