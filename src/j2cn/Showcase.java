package j2cn;

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Image;
import javax.microedition.midlet.MIDlet;

/** Showcase MIDlet: exercises every widget with ShadCN-like styling. */
public class Showcase extends MIDlet {

    private UiScreen screen;

    protected void startApp() {
        try {
            Image atlas = Image.createImage("/font.png");
            byte[] metrics = readMetrics("/font.metrics");
            final UiFont font = new UiFont(atlas, metrics);
            Display display = Display.getDisplay(this);
            screen = new UiScreen(this, display, font, "J2CN", false);
            screen.allowThemeToggle = true;
            screen.sheet().add(
                "button { radius: 6; padding: 8; }\n" +
                "#login { background: #18181b; color: #fafafa; }\n" +
                ".hero { color: #18181b; text-align: left; }\n");
            final Dialog dialog = new Dialog("Delete item?", "This cannot be undone.\nThe row will be removed.");
            dialog.okText = "Delete";
            dialog.cancelText = "Cancel";

            Column page = new Column();
            page.gap = 8;
            Column hero = new Column();
            hero.add(new Avatar("Ada Lovelace"));
            hero.add(new Label("Settings", "hero"));
            hero.add(new Label("Manage your account and preferences.", "muted"));
            page.add(new Card(hero, null));

            page.add(new Label("Profile", null));
            final Input name = new Input("Name", "Ada");
            page.add(name);
            Row row = new Row();
            row.add(new Badge("PRO", "badge badge-primary"), 1);
            row.add(new Badge("Beta", "badge"), 1);
            row.add(new Badge("Off", "badge badge-destructive"), 1);
            page.add(row);
            page.add(new Separator());

            page.add(new Label("Controls", null));
            final CheckBox mail = new CheckBox("Email notifications", true);
            final Switch wifi = new Switch("Wi-Fi sync", false);
            final Slider bright = new Slider("Brightness", 60);
            final Progress prog = new Progress(35);
            page.add(mail);
            page.add(wifi);
            page.add(bright);
            page.add(prog);

            Column t1 = new Column();
            t1.add(new Label("General options live here.", "muted"));
            t1.add(new CheckBox("Auto-update", false));
            Column t2 = new Column();
            t2.add(new Label("Advanced knobs live here.", "muted"));
            t2.add(new Slider("Cache", 20));
            Tabs tabs = new Tabs(new String[]{"General", "Advanced"}, new Widget[]{t1, t2});
            page.add(tabs);

            page.add(new Button("Save changes", "btn", new Runnable() {
                public void run() {
                    prog.set(prog.value >= 100 ? 0 : prog.value + 15);
                    screen.repaint();
                }
            }));
            page.add(new Button("Outline action", "btn btn-outline", null));
            page.add(new Button("Delete", "btn btn-destructive", new Runnable() {
                public void run() {
                    dialog.open = true;
                    screen.setDialog(dialog);
                }
            }));

            screen.setRoot(page);
            screen.setDialog(dialog);
            display.setCurrent(screen);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static byte[] readMetrics(String path) throws Exception {
        java.io.InputStream in = "x".getClass().getResourceAsStream(path);
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        byte[] buf = new byte[256];
        int n;
        while ((n = in.read(buf)) > 0) out.write(buf, 0, n);
        in.close();
        return out.toByteArray();
    }

    protected void pauseApp() {}

    protected void destroyApp(boolean unconditional) {}
}
