package j2cn;

/**
 * Pure layout math (no LCDUI) so desktop tests can verify it.
 * Vertical stack and weighted horizontal row.
 */
public final class Layout {

    private Layout() {}

    /** y offsets for n children with given heights and a gap. */
    public static int[] stack(int top, int[] heights, int gap) {
        int[] ys = new int[heights.length];
        int y = top;
        for (int i = 0; i < heights.length; i++) {
            ys[i] = y;
            y += heights[i] + gap;
        }
        return ys;
    }

    public static int stackTotal(int[] heights, int gap) {
        if (heights.length == 0) return 0;
        int total = 0;
        for (int i = 0; i < heights.length; i++) total += heights[i];
        return total + gap * (heights.length - 1);
    }

    /** x offsets/widths for a weighted row inside [left, left+width). */
    public static int[][] row(int left, int width, int[] weights, int gap) {
        int n = weights.length;
        int[][] out = new int[n][2];
        int sum = 0;
        for (int i = 0; i < n; i++) sum += weights[i];
        if (sum <= 0) sum = n;
        int gaps = gap * (n - 1);
        int x = left;
        int used = 0;
        for (int i = 0; i < n; i++) {
            int w = (i == n - 1) ? (width - gaps - used) : (width - gaps) * weights[i] / sum;
            out[i][0] = x;
            out[i][1] = w;
            x += w + gap;
            used += w;
        }
        return out;
    }
}
