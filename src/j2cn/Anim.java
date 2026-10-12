package j2cn;

/**
 * Integer-only animation math (no LCDUI, no allocation).
 * Progress and easing use 0..1024 fixed point; time in milliseconds
 * from System.currentTimeMillis(). Frame stepping uses exponential
 * approach so motion eases out without any time tracking.
 */
public final class Anim {

    private Anim() {}

    /** Linear interpolation a..b at t (0..1024). */
    public static int lerp(int a, int b, int t) {
        return a + (b - a) * t / 1024;
    }

    /** Ease-out cubic at t (0..1024). easeOut(0)=0, easeOut(1024)=1024. */
    public static int easeOut(int t) {
        if (t <= 0) return 0;
        if (t >= 1024) return 1024;
        int u = 1024 - t;
        return 1024 - (u * u / 1024) * u / 1024;
    }

    /** 0..1024 progress of now between start and start+durMs. */
    public static int progress(long startMs, int durMs, long nowMs) {
        if (durMs <= 0) return 1024;
        long d = nowMs - startMs;
        if (d <= 0) return 0;
        long p = d * 1024 / durMs;
        return p >= 1024 ? 1024 : (int) p;
    }

    /**
     * Scroll offset so [y, y+h] is visible in a viewport of height viewH,
     * keeping margin px clear for focus frames. Pure; unit-tested.
     */
    public static int follow(int y, int h, int viewH, int scroll, int margin) {
        if (y - margin < scroll) return y - margin;
        if (y + h + margin > scroll + viewH) return y + h + margin - viewH;
        return scroll;
    }

    /**
     * One frame of exponential approach: moves cur a quarter of the way
     * to target, minimum 1px so it always converges. Returns the new value.
     */
    public static int approach(int cur, int target) {
        int d = target - cur;
        if (d == 0) return cur;
        int s = d / 4;
        if (s == 0) s = d > 0 ? 1 : -1;
        return cur + s;
    }
}
