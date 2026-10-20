package client.confis;

import net.minecraft.class_124;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_4185;
import net.minecraft.class_437;

/** Shared look for all new Confis screens: dark panel, accent outline, title, X button. */
public abstract class ConfisScreen extends class_437 {
    protected final class_437 parent;
    protected final class_310 mc = class_310.method_1551();
    protected int px, py, pw, ph;
    private boolean dirty;

    protected ConfisScreen(String title, class_437 parent) {
        super(class_2561.method_43470(title));
        this.parent = parent;
    }

    protected int panelW() { return 440; }
    protected int panelH() { return 260; }
    /** Add widgets here. Called on every (re)layout. */
    protected abstract void build();
    /** Draw custom things on top of the widgets. */
    protected abstract void draw(class_332 g, int mx, int my);

    protected void markDirty() { dirty = true; }
    protected int accent() { return 0xFF000000 | Config.get().accent; }

    @Override
    protected void method_25426() {
        pw = Math.min(panelW(), field_22789 - 16);
        ph = Math.min(panelH(), field_22790 - 16);
        px = (field_22789 - pw) / 2;
        py = (field_22790 - ph) / 2;
        btn("X", px + pw - 20, py + 5, 14, 14, this::method_25419);
        build();
    }

    @Override
    public void method_25393() {
        if (dirty) {
            dirty = false;
            method_41843();
        }
    }

    protected class_4185 btn(String label, int x, int y, int w, int h, Runnable action) {
        class_4185 b = class_4185.method_46430(class_2561.method_43470(label), bt -> action.run()).method_46434(x, y, w, h).method_46431();
        method_37063(b);
        return b;
    }

    @Override public void method_25420(class_332 g, int mx, int my, float d) {}
    @Override public boolean method_25421() { return false; }
    @Override public void method_25419() { mc.method_1507(parent); }

    @Override
    public void method_25394(class_332 g, int mx, int my, float d) {
        g.method_25294(0, 0, field_22789, field_22790, 0x90000000);
        g.method_25294(px - 1, py - 1, px + pw + 1, py + ph + 1, accent());
        g.method_25294(px, py, px + pw, py + ph, Config.get().panelBg);
        g.method_25294(px, py + 24, px + pw, py + 25, 0xFF3A3A3A);
        g.method_51439(field_22793, class_2561.method_43470(method_25440().getString()).method_27692(class_124.field_1067), px + 8, py + 8, accent(), true);
        super.method_25394(g, mx, my, d);
        draw(g, mx, my);
    }
}
