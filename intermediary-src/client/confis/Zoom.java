package client.confis;

import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

/** Hold-to-zoom. The key is stored in the config and can be changed in the Zoom module options. */
public final class Zoom {
    private static boolean held;
    private static float current = 1f;

    private Zoom() {}

    public static void poll(MinecraftClient mc) {
        Module m = Modules.byId("zoom");
        long win = GLFW.glfwGetCurrentContext();
        held = m != null && m.cfg().enabled && win != 0L && mc.player != null && mc.currentScreen == null
                && GLFW.glfwGetKey(win, Config.get().zoomKey) == GLFW.GLFW_PRESS;
    }

    public static boolean active() { return held; }

    /** FOV divider, eased so zooming in and out is smooth. Called by ZoomMixin. */
    public static float mult() {
        float target = held ? Math.max(1.1f, Config.get().zoomFactor) : 1f;
        current += (target - current) * 0.35f;
        if (Math.abs(target - current) < 0.001f) current = target;
        return current;
    }

    public static String keyName(int key) {
        switch (key) {
            case GLFW.GLFW_KEY_SPACE: return "SPACE";
            case GLFW.GLFW_KEY_LEFT_SHIFT: return "L-SHIFT";
            case GLFW.GLFW_KEY_LEFT_CONTROL: return "L-CTRL";
            case GLFW.GLFW_KEY_RIGHT_CONTROL: return "R-CTRL";
            case GLFW.GLFW_KEY_LEFT_ALT: return "L-ALT";
            case GLFW.GLFW_KEY_RIGHT_ALT: return "R-ALT";
            case GLFW.GLFW_KEY_TAB: return "TAB";
            case GLFW.GLFW_KEY_ENTER: return "ENTER";
            case GLFW.GLFW_KEY_UP: return "UP";
            case GLFW.GLFW_KEY_DOWN: return "DOWN";
            case GLFW.GLFW_KEY_LEFT: return "LEFT";
            case GLFW.GLFW_KEY_RIGHT: return "RIGHT";
            default:
        }
        if (key >= GLFW.GLFW_KEY_F1 && key <= GLFW.GLFW_KEY_F12) return "F" + (key - GLFW.GLFW_KEY_F1 + 1);
        String n = GLFW.glfwGetKeyName(key, 0);
        return n == null ? "KEY " + key : n.toUpperCase();
    }
}
