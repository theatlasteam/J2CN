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
 */
public class UiScreen extends Canvas implements CommandListener {

    private Column root;
    private Dialog dialog;
    private UiFont font;
    private Theme theme;
    private StyleSheet sheet;
    private String title;
    private java.util.Vector focusables = new java.util.Vector();
    private int focusIndex = -1;
    private int scroll;
    private Display display;
    private javax.microedition.midlet.MIDlet midlet;
    private Input editing;
    private TextBox textBox;
    private Command okCmd = new Command("OK", Command.OK, 1);
    private Command cancelCmd = new Command("Back", Command.BACK, 2);
    public boolean allowThemeToggle;

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
        focusables.removeAllElements();
        root.collectFocusable(focusables);
        if (focusIndex >= focusables.size()) focusIndex = focusables.size() - 1;
        if (focusIndex < 0 && focusables.size() > 0) focusIndex = 0;
        updateFocus();
        repaint();
    }

    private void updateFocus() {
        for (int i = 0; i < focusables.size(); i++) {
            ((Widget) focusables.elementAt(i)).focused = (i == focusIndex);
        }
    }

    private void ensureVisible() {
        if (focusIndex < 0 || focusIndex >= focusables.size()) return;
        Widget w = (Widget) focusables.elementAt(focusIndex);
        int topBar = 26;
        int viewH = getHeight() - topBar;
        if (w.y < scroll + topBar) scroll = w.y - topBar;
        if (w.y + w.h > scroll + topBar + viewH - 8) scroll = w.y + w.h - (topBar + viewH - 8);
        if (scroll < 0) scroll = 0;
    }

    protected void paint(Graphics g) {
        int sw = getWidth();
        int sh = getHeight();
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
            g.translate(0, topBar - scroll);
            g.setClip(0, scroll - topBar, sw, sh - topBar);
            root.paint(g, font, theme);
            g.translate(0, scroll - topBar);
            g.setClip(0, 0, sw, sh);
        }
        if (dialog != null && dialog.open) {
            dialog.x = 0;
            dialog.y = 0;
            dialog.w = sw;
            dialog.h = sh;
            dialog.paint(g, font, theme);
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
            scroll -= getHeight() / 2;
            if (scroll < 0) scroll = 0;
            repaint();
            return;
        }
        if (keyCode == Canvas.KEY_NUM7) {
            scroll += getHeight() / 2;
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
