package j2cn;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;

/**
 * Canvas host: app bar, scrolling column, d-pad focus navigation.
 * Up/down moves focus, left/right + fire go to the focused widget.
 * 1/7 page scroll, * toggles dark mode when allowed.
 *
 * Motion: a single focus frame glides between widgets (exponential
 * approach, ~30fps ticker that runs only while something is moving),
 * scrolling eases toward its target, FIRE flashes the pressed widget,
 * and dialogs slide up on entry. All math is integer; the ticker
 * thread sleeps when idle so there is no battery drain.
 */
public class UiScreen extends Canvas implements CommandListener {

    private static final int TICK_MS = 33;
    private static final int PRESS_MS = 150;
    private static final int DIALOG_MS = 220;
    private static final int DIALOG_SLIDE = 40;

    private Column root;
    private Dialog dialog;
    private UiFont font;
    private Theme theme;
    private StyleSheet sheet;
    private String title;
    private java.util.Vector focusables = new java.util.Vector();
    private int focusIndex = -1;
    private int scrollTarget;
    private int scrollCur;
    private int maxScroll;
    private Display display;
    private javax.microedition.midlet.MIDlet midlet;
    private Input editing;
    private TextBox textBox;
    private Command okCmd = new Command("OK", Command.OK, 1);
    private Command cancelCmd = new Command("Back", Command.BACK, 2);
    public boolean allowThemeToggle;

    // animation state (written on UI thread, stepped on ticker thread)
    private int fx, fy, fw, fh;
    private boolean frameInit;
    private final int[] frameRect = new int[4];
    private Widget pressedWidget;
    private long pressUntil;
    private long dialogShownAt;
    private Thread animThread;
    private boolean animating;
    private int animReq;

    public UiScreen(javax.microedition.midlet.MIDlet midlet, Display display,
            UiFont font, String title, boolean dark) {
        this.midlet = midlet;
        this.display = display;
        this.font = font;
        this.title = title;
        this.theme = dark ? Theme.dark() : Theme.light();
        this.sheet = new StyleSheet(StyleSheet.DEFAULT_CSS);
        setFullScreenMode(true);
    }

    public Theme theme() {
        return theme;
    }

    public StyleSheet sheet() {
        return sheet;
    }

    public void setRoot(Column root) {
        this.root = root;
        relayout();
        scrollTarget = 0;
        scrollCur = 0;
        frameInit = false;
    }

    public void setDialog(Dialog d) {
        this.dialog = d;
        if (d != null) {
            d.w = getWidth();
            d.h = getHeight();
        }
        repaint();
    }

    public void relayout() {
        if (root == null) return;
        root.resolve(sheet, new Style());
        root.layout(0, 0, getWidth());
        maxScroll = root.h + 26 + 8 - getHeight();
        if (maxScroll < 0) maxScroll = 0;
        scrollTarget = clampScroll(scrollTarget);
        scrollCur = clampScroll(scrollCur);
        focusables.removeAllElements();
        root.collectFocusable(focusables);
        if (focusIndex >= focusables.size()) focusIndex = focusables.size() - 1;
        if (focusIndex < 0 && focusables.size() > 0) focusIndex = 0;
        updateFocus();
        repaint();
    }

    private int clampScroll(int s) {
        if (s < 0) return 0;
        if (s > maxScroll) return maxScroll;
        return s;
    }

    private void updateFocus() {
        for (int i = 0; i < focusables.size(); i++) {
            ((Widget) focusables.elementAt(i)).focused = (i == focusIndex);
        }
    }

    private Widget currentFocused() {
        if (focusIndex < 0 || focusIndex >= focusables.size()) return null;
        return (Widget) focusables.elementAt(focusIndex);
    }

    private void ensureVisible() {
        Widget w = currentFocused();
        if (w == null) return;
        // All in content coordinates: viewport is [scroll, scroll + viewH].
        // Margin 4 keeps the focus frame (extends ~3px past the widget)
        // fully on screen instead of hanging off the bottom edge.
        int viewH = getHeight() - 26;
        scrollTarget = clampScroll(Anim.follow(w.y, w.h, viewH, scrollTarget, 4));
        startAnim();
    }

    private void snapFrameTo(Widget w) {
        w.focusRect(frameRect);
        fx = frameRect[0] - 3;
        fy = frameRect[1] - 2;
        fw = frameRect[2] + 6;
        fh = frameRect[3] + 4;
        frameInit = true;
    }

    private void paintHaloBehind(Graphics g) {
        Widget w = currentFocused();
        if (w == null) return;
        if (!frameInit) snapFrameTo(w);
        int rad = 7;
        if (w.style() != null) rad = w.style().radius + 3;
        g.setColor(theme.focusHalo);
        Widget.fillRound(g, fx, fy, fw, fh, rad);
    }

    private void paintFocusOutlines(Graphics g) {
        Widget w = currentFocused();
        if (w == null) return;
        if (!frameInit) snapFrameTo(w);
        int rad = 7;
        if (w.style() != null) rad = w.style().radius + 3;
        g.setColor(theme.focus);
        Widget.drawRound(g, fx, fy, fw, fh, rad);
        Widget.drawRound(g, fx + 1, fy + 1, fw - 2, fh - 2, rad > 0 ? rad - 1 : 0);
    }

    private void paintPressFlash(Graphics g, long now) {
        if (pressedWidget == null || now >= pressUntil || !pressedWidget.visible) return;
        Widget w = pressedWidget;
        int rad = 4;
        if (w.style() != null) rad = w.style().radius;
        g.setColor(theme.focus);
        Widget.drawRound(g, w.x + 1, w.y + 1, w.w - 2, w.h - 2, rad);
        Widget.drawRound(g, w.x + 2, w.y + 2, w.w - 4, w.h - 4, rad > 0 ? rad - 1 : 0);
    }

    protected void paint(Graphics g) {
        int sw = getWidth();
        int sh = getHeight();
        long now = System.currentTimeMillis();
        g.setColor(theme.background);
        g.fillRect(0, 0, sw, sh);
        // app bar
        g.setColor(theme.card);
        g.fillRect(0, 0, sw, 24);
        g.setColor(theme.border);
        g.drawLine(0, 24, sw - 1, 24);
        font.drawLine(g, title, 8, 4, sw - 16, 0, true, theme.foreground);
        if (root != null) {
            int topBar = 26;
            g.translate(0, topBar - scrollCur);
            g.setClip(0, scrollCur, sw, sh - topBar);
            paintHaloBehind(g);
            root.paint(g, font, theme);
            paintFocusOutlines(g);
            paintPressFlash(g, now);
            g.translate(0, scrollCur - topBar);
            g.setClip(0, 0, sw, sh);
        }
        if (dialog != null && dialog.open) {
            if (dialogShownAt == 0) {
                dialogShownAt = now;
                startAnim();
            }
            int p = Anim.progress(dialogShownAt, DIALOG_MS, now);
            int off = (1024 - Anim.easeOut(p)) * DIALOG_SLIDE / 1024;
            dialog.x = 0;
            dialog.y = 0;
            dialog.w = sw;
            dialog.h = sh;
            g.translate(0, off);
            dialog.paint(g, font, theme);
            g.translate(0, -off);
        } else {
            dialogShownAt = 0;
        }
    }

    /** One ticker step. Returns true if another frame is needed. */
    private boolean step() {
        boolean more = false;
        long now = System.currentTimeMillis();
        Widget w = currentFocused();
        if (w != null) {
            w.focusRect(frameRect);
            int tx = frameRect[0] - 3;
            int ty = frameRect[1] - 2;
            int tw = frameRect[2] + 6;
            int th = frameRect[3] + 4;
            if (!frameInit) {
                fx = tx;
                fy = ty;
                fw = tw;
                fh = th;
                frameInit = true;
            }
            int nx = Anim.approach(fx, tx);
            int ny = Anim.approach(fy, ty);
            int nw = Anim.approach(fw, tw);
            int nh = Anim.approach(fh, th);
            if (nx != tx || ny != ty || nw != tw || nh != th) more = true;
            fx = nx;
            fy = ny;
            fw = nw;
            fh = nh;
        }
        if (scrollCur != scrollTarget) {
            scrollCur = Anim.approach(scrollCur, scrollTarget);
            if (scrollCur != scrollTarget) more = true;
        }
        if (pressedWidget != null) {
            if (now < pressUntil) more = true;
            else pressedWidget = null;
        }
        if (dialog != null && dialog.open && dialogShownAt != 0) {
            if (Anim.progress(dialogShownAt, DIALOG_MS, now) < 1024) more = true;
        }
        return more;
    }

    private synchronized void startAnim() {
        animating = true;
        animReq++;
        if (animThread == null) {
            animThread = new Thread(new Runnable() {
                public void run() {
                    animLoop();
                }
            });
            animThread.start();
        }
        notify();
    }

    private int currentReq() {
        synchronized (this) {
            return animReq;
        }
    }

    private void animLoop() {
        while (true) {
            int seen;
            synchronized (this) {
                while (!animating) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                    }
                }
                seen = animReq;
            }
            boolean more;
            do {
                more = step();
                repaint();
                try {
                    Thread.sleep(TICK_MS);
                } catch (InterruptedException e) {
                }
                synchronized (this) {
                    if (animReq != seen) {
                        seen = animReq;
                        more = true;
                    }
                }
            } while (more);
            synchronized (this) {
                if (animReq == seen) animating = false;
            }
        }
    }

    protected void keyPressed(int keyCode) {
        if (dialog != null && dialog.open) {
            dialog.handleKey(keyCode, getGameAction(keyCode));
            repaint();
            return;
        }
        int action = getGameAction(keyCode);
        if (keyCode == Canvas.KEY_STAR && allowThemeToggle) {
            boolean isDark = theme.background == Theme.dark().background;
            theme = isDark ? Theme.light() : Theme.dark();
            relayout();
            return;
        }
        if (keyCode == Canvas.KEY_NUM1) {
            scrollTarget = clampScroll(scrollTarget - getHeight() / 2);
            startAnim();
            repaint();
            return;
        }
        if (keyCode == Canvas.KEY_NUM7) {
            scrollTarget = clampScroll(scrollTarget + getHeight() / 2);
            startAnim();
            repaint();
            return;
        }
        if ((action == UP || action == DOWN) && focusables.size() > 0) {
            if (focusIndex < 0) focusIndex = 0;
            else focusIndex = (focusIndex + (action == DOWN ? 1 : focusables.size() - 1)) % focusables.size();
            updateFocus();
            // tabs page switch changes layout: relayout keeps focus sane
            relayout();
            ensureVisible();
            repaint();
            return;
        }
        if (focusIndex >= 0 && focusIndex < focusables.size()) {
            Widget w = (Widget) focusables.elementAt(focusIndex);
            if (w instanceof Tabs && (action == LEFT || action == RIGHT)) {
                w.handleKey(keyCode, action);
                relayout();
                ensureVisible();
                repaint();
                return;
            }
            if (w.handleKey(keyCode, action)) {
                if (w instanceof Input && ((Input) w).editRequested) {
                    ((Input) w).editRequested = false;
                    openEditor((Input) w);
                    return;
                }
                pressedWidget = w;
                pressUntil = System.currentTimeMillis() + PRESS_MS;
                startAnim();
                repaint();
            }
        }
    }

    private void openEditor(Input in) {
        editing = in;
        textBox = new TextBox(in.label, in.getText(), 64, TextField.ANY);
        textBox.addCommand(okCmd);
        textBox.addCommand(cancelCmd);
        textBox.setCommandListener(this);
        display.setCurrent(textBox);
    }

    public void commandAction(Command c, Displayable d) {
        if (d == textBox && editing != null) {
            if (c == okCmd) editing.setText(textBox.getString());
            editing = null;
            display.setCurrent(this);
            repaint();
        }
    }
}
