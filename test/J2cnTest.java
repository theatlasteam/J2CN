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
        check(light.focus == 0x2563eb && dark.focus == 0x60a5fa, "focus vivid");
        check(light.focus != light.primary && light.focusHalo != light.background, "focus distinct");

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

        // Animation math.
        check(Anim.lerp(0, 100, 512) == 50, "lerp half");
        check(Anim.lerp(10, 20, 0) == 10 && Anim.lerp(10, 20, 1024) == 20, "lerp ends");
        check(Anim.easeOut(0) == 0 && Anim.easeOut(1024) == 1024, "ease ends");
        check(Anim.easeOut(512) > 512, "ease fast start");
        boolean mono = true;
        int prev = -1;
        for (int t = 0; t <= 1024; t += 64) {
            int e = Anim.easeOut(t);
            if (e < prev) mono = false;
            prev = e;
        }
        check(mono, "ease monotonic");
        check(Anim.progress(1000, 200, 900) == 0, "progress before");
        check(Anim.progress(1000, 200, 1100) == 512, "progress half");
        check(Anim.progress(1000, 200, 1300) == 1024, "progress after");
        check(Anim.approach(5, 5) == 5, "approach rest");
        int v = 0;
        int steps = 0;
        while (v != 100 && steps < 500) {
            v = Anim.approach(v, 100);
            steps++;
        }
        check(v == 100 && steps < 100, "approach converges");
        v = 100;
        steps = 0;
        while (v != 0 && steps < 500) {
            v = Anim.approach(v, 0);
            steps++;
        }
        check(v == 0, "approach converges down");

        System.out.println("j2cn: " + passed + " passed, " + failed + " failed");
        if (failed > 0) System.exit(1);
    }
}
