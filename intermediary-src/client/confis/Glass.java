package client.confis;

import net.minecraft.client.gui.DrawContext;

/**
 * iOS-style "liquid glass" drawing for the HUD modules and the menu.
 * Frosted translucent body, a bright specular sheen on top, a glowing rim that fades towards the
 * bottom and an optional soft shadow. Built only from fill() calls so it works anywhere.
 */
public final class Glass {
    private Glass() {}

    private static int lerp(int a, int b, float t) { return Math.round(a + (b - a) * t); }
    private static int clamp(int v, int lo, int hi) { return Math.max(lo, Math.min(hi, v)); }
    private static int white(int a) { return (a & 255) << 24 | 0xFFFFFF; }
    private static int rgba(int a, int rgb) { return (a & 255) << 24 | (rgb & 0xFFFFFF); }
    private static int argb(int a, int r, int g, int b) {
        return (a & 255) << 24 | (r & 255) << 16 | (g & 255) << 8 | (b & 255);
    }
    private static int lum(int c) {
        return (((c >> 16) & 255) * 299 + ((c >> 8) & 255) * 587 + (c & 255) * 114) / 1000;
    }

    /** Horizontal inset of corner row i (0 = outermost row) for corner radius r. */
    private static int inset(int i, int r) {
        if (i >= r) return 0;
        double dy = r - i - 0.5;
        return r - (int) Math.round(Math.sqrt(Math.max(0, r * r - dy * dy)));
    }

    /** Inset of row i of a shape that is h rows tall (same shape top and bottom). */
    private static int d(int i, int h, int r) {
        i = Math.max(0, Math.min(h - 1, i));
        return inset(Math.min(i, h - 1 - i), r);
    }

    private static void span(DrawContext g, int x0, int x1, int y, int color) {
        if (x1 > x0) g.fill(x0, y, x1, y + 1, color);
    }

    // ---- run merging: consecutive rows with the same x-range and color become ONE fill() call.
    // Every fill() is a separate GUI element in 1.21.x, so this is what keeps the FPS up.
    private static boolean runOpen;
    private static int runX0, runX1, runY0, runY1, runCol;

    private static void run(DrawContext g, int x0, int x1, int y, int color) {
        if (x1 <= x0) return;
        if (runOpen && x0 == runX0 && x1 == runX1 && y == runY1 && color == runCol) {
            runY1 = y + 1;
            return;
        }
        flush(g);
        runOpen = true;
        runX0 = x0; runX1 = x1; runY0 = y; runY1 = y + 1; runCol = color;
    }

    private static void flush(DrawContext g) {
        if (runOpen) g.fill(runX0, runY0, runX1, runY1, runCol);
        runOpen = false;
    }

    /** Rounds an alpha value to a step so neighbouring rows share a color and can be merged. */
    private static int q(int a, int step) { return (a / step) * step; }

    // ------------------------------------------------------------------ core
    private static void render(DrawContext g, int x, int y, int w, int h, int r,
                               int bodyA, int cr, int cg, int cb,
                               boolean shadow, int sheenMax, int rimTop, int rimBottom, int rimRgb) {
        r = Math.max(1, Math.min(r, Math.min(h / 2, (w + 1) / 2)));

        if (shadow) {
            g.fill(x + 3, y + h, x + w - 3, y + h + 1, 0x26000000);
            g.fill(x + 5, y + h + 1, x + w - 5, y + h + 2, 0x12000000);
        }

        int sheenRows = Math.max(2, h * 45 / 100);

        // pass 1: body (a touch more opaque at the top, lighter at the bottom)
        for (int i = 0; i < h; i++) {
            int ins = d(i, h, r);
            float t = h <= 1 ? 0f : (float) i / (h - 1);
            int a = q(clamp(lerp(bodyA + 0x10, bodyA - 0x10, t), 0x18, 0xFF), 6);
            run(g, x + ins, x + w - ins, y + i, argb(a, cr, cg, cb));
        }
        flush(g);

        // pass 2: specular sheen across the top half
        if (sheenMax > 0) {
            for (int i = 0; i < Math.min(sheenRows, h); i++) {
                int ins = d(i, h, r);
                int sa = q(Math.round(sheenMax * (1f - (float) i / sheenRows)), 6);
                if (sa > 0) run(g, x + ins, x + w - ins, y + i, white(sa));
            }
            flush(g);
        }

        // pass 3: glowing rim (bright at the top, fading towards the bottom): top/bottom edge rows
        for (int i = 0; i < h; i += Math.max(1, h - 1)) {
            int ins = d(i, h, r);
            float t = h <= 1 ? 0f : (float) i / (h - 1);
            span(g, x + ins, x + w - ins, y + i, rgba(lerp(rimTop, rimBottom, t), rimRgb));
            if (h <= 1) break;
        }
        // left side, then right side
        for (int side = 0; side < 2; side++) {
            for (int i = 1; i < h - 1; i++) {
                int ins = d(i, h, r);
                int x0 = x + ins, x1 = x + w - ins;
                float t = h <= 1 ? 0f : (float) i / (h - 1);
                int rw = i < h / 2
                        ? Math.max(1, d(i - 1, h, r) - ins)
                        : Math.max(1, d(i + 1, h, r) - ins);
                int col = rgba(q(lerp(rimTop, rimBottom, t), 12), rimRgb);
                if (side == 0) run(g, x0, Math.min(x0 + rw, x1), y + i, col);
                else run(g, Math.max(x1 - rw, x0), x1, y + i, col);
            }
            flush(g);
        }

        // thin inner highlight just under the top edge, and a faint glow above the bottom edge
        if (h >= 9 && sheenMax > 0) {
            int tx = d(1, h, r) + 3;
            span(g, x + tx, x + w - tx, y + 1, white(0x3C));
            int bx = d(h - 2, h, r) + 3;
            span(g, x + bx, x + w - bx, y + h - 2, white(0x16));
        }
    }

    // ------------------------------------------------------------ HUD modules
    /** Liquid-glass panel for a HUD module. bg is the module's background color (its alpha sets how frosted it is). */
    public static void draw(DrawContext g, int x, int y, int w, int h, int bg) {
        if (w < 6 || h < 6) {
            g.fill(x, y, x + w, y + h, bg);
            return;
        }
        int bodyA = clamp((int) ((bg >>> 24) * 0.85f), 0x40, 0xC8);
        render(g, x, y, w, h, 7, bodyA,
                lerp((bg >> 16) & 255, 255, 0.14f), lerp((bg >> 8) & 255, 255, 0.14f), lerp(bg & 255, 255, 0.14f),
                true, 0x34, 0xB4, 0x34, 0xFFFFFF);
    }

    /** Small rounded "well" used for inventory slots. */
    public static void slot(DrawContext g, int x, int y, int w, int h) {
        fillRound(g, x, y, w, h, 3, 0x26FFFFFF);
    }

    // -------------------------------------------------------------------- menu
    // Blue "neon glass" look: translucent blue body, bright cyan-white rim, white sheen.
    // Only flat fills + static banded gradients: no blur, no shaders, no refraction.
    private static final int RIM = 0xDFF2FF;

    /** The big menu window. argb alpha = how see-through the glass is. */
    public static void panel(DrawContext g, int x, int y, int w, int h, int argb) {
        int a = clamp((int) ((argb >>> 24) * 0.42f), 0x34, 0x70);
        int glow = 0x30A0E0FF;                         // soft outer glow (4 thin fills)
        g.fill(x + 10, y - 1, x + w - 10, y, glow);
        g.fill(x + 10, y + h, x + w - 10, y + h + 1, glow);
        g.fill(x - 1, y + 10, x, y + h - 10, glow);
        g.fill(x + w, y + 10, x + w + 1, y + h - 10, glow);
        render(g, x, y, w, h, 12, a, 38, 128, 224, false, 0x26, 0xF0, 0x70, RIM);
    }

    /** A module card in the menu grid. */
    public static void card(DrawContext g, int x, int y, int w, int h, boolean hover, boolean on) {
        int a = on ? 0x5C : 0x44;
        int cr = on ? 40 : 28, cg = on ? 132 : 98, cb = on ? 218 : 175;
        if (hover) { a += 0x1C; cr += 14; cg += 22; cb += 18; }
        render(g, x, y, w, h, 8, a, cr, cg, cb, false, 0x30,
                hover ? 0xFF : 0xD8, hover ? 0x98 : 0x58, RIM);
    }

    /** A pill button. Bright fills (active tab, EDIT HUD) become cyan glass, dark ones blue glass. */
    public static void button(DrawContext g, int x, int y, int w, int h, int fill, boolean hover) {
        boolean light = lum(fill) > 140;
        int a = light ? 0xD0 : 0x70;
        int cr = light ? 150 : 34, cg = light ? 225 : 104, cb = light ? 255 : 190;
        if (hover) { a = Math.min(0xF0, a + 0x28); cr = lerp(cr, 255, 0.15f); cg = lerp(cg, 255, 0.15f); cb = lerp(cb, 255, 0.15f); }
        render(g, x, y, w, h, 7, a, cr, cg, cb, false, light ? 0x40 : 0x30,
                hover ? 0xFF : 0xE0, hover ? 0xA0 : 0x70, RIM);
    }

    /** Enabled / Disabled pill: green glass when on, blue glass when off. */
    public static void toggle(DrawContext g, int x, int y, int w, int h, boolean on, boolean hover) {
        int a = on ? 0xC0 : 0x70;
        int cr = on ? 36 : 34, cg = on ? 196 : 104, cb = on ? 112 : 190;
        if (hover) { a = Math.min(0xF0, a + 0x28); cr = lerp(cr, 255, 0.15f); cg = lerp(cg, 255, 0.15f); cb = lerp(cb, 255, 0.15f); }
        render(g, x, y, w, h, 7, a, cr, cg, cb, false, 0x34,
                hover ? 0xFF : 0xE0, hover ? 0xA0 : 0x70, RIM);
    }

    /** Inset text field (search box). Focused = rim in the accent color. */
    public static void field(DrawContext g, int x, int y, int w, int h, boolean focus, int accent) {
        render(g, x, y, w, h, 7, 0x70, 10, 50, 110, false, 0,
                focus ? 0xFF : 0xB4, focus ? 0xD0 : 0x60, focus ? (accent & 0xFFFFFF) : RIM);
    }

    /** Plain rounded fill in one color (sliders, scrollbars, slots). */
    public static void fillRound(DrawContext g, int x, int y, int w, int h, int r, int argb) {
        r = Math.max(1, Math.min(r, Math.min(h / 2, (w + 1) / 2)));
        int i = 0;
        while (i < h) {
            int ins = d(i, h, r);
            int j = i + 1;
            while (j < h && d(j, h, r) == ins) j++;      // rows with the same inset = one fill
            if (w - 2 * ins > 0) g.fill(x + ins, y + i, x + w - ins, y + j, argb);
            i = j;
        }
    }
}
