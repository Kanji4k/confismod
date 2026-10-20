package client.confis;

import net.minecraft.class_310;
import net.minecraft.class_332;
import org.lwjgl.glfw.GLFW;

public final class Hud {
    private static int frames, fps;
    private static long lastFpsTime = System.nanoTime();
    private static boolean shiftWasDown;

    private Hud() {}

    public static int fps() { return fps; }

    public static void render(class_332 g) {
        class_310 mc = class_310.method_1551();
        pollMenuKey(mc);
        Zoom.poll(mc);
        Freecam.poll(mc);
        Fullbright.poll(mc);
        if (mc.field_1724 == null || mc.field_1687 == null || mc.field_1690.field_1842) return;

        countFrame();
        Cps.poll(mc);

        boolean preview = mc.field_1755 instanceof ConfigScreen;
        int W = g.method_51421(), H = g.method_51443();
        for (Module m : Modules.ALL) {
            Config.ModuleCfg c = m.cfg();
            if (!c.enabled) continue;
            if (!m.movable()) {
                if (m instanceof Modules.WaypointsModule) Waypoints.render(g, mc, c);
                else m.drawFixed(g, mc, c, W, H);
                continue;
            }
            int hx = m.posX(mc, W), hy = m.posY(mc, H);
            float s = c.scale;
            g.method_51448().pushMatrix();
            g.method_51448().translate(hx, hy);
            g.method_51448().scale(s, s);
            m.draw(g, mc, c, 0, 0, preview);
            g.method_51448().popMatrix();
        }
    }

    /** Right Shift opens / closes the menu. Polled from the render thread's GL context. */
    private static void pollMenuKey(class_310 mc) {
        long window = GLFW.glfwGetCurrentContext();
        if (window == 0L) return;
        boolean down = GLFW.glfwGetKey(window, GLFW.GLFW_KEY_RIGHT_SHIFT) == GLFW.GLFW_PRESS;
        if (down && !shiftWasDown) {
            if (mc.field_1755 == null && mc.field_1724 != null) mc.method_1507(new ConfigScreen());
            else if (mc.field_1755 instanceof ConfigScreen) mc.method_1507(null);
        }
        shiftWasDown = down;
    }

    private static void countFrame() {
        frames++;
        long now = System.nanoTime();
        if (now - lastFpsTime >= 1_000_000_000L) {
            fps = frames;
            frames = 0;
            lastFpsTime = now;
        }
    }
}
