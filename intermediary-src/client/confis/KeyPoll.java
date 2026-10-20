package client.confis;

import org.lwjgl.glfw.GLFW;

/** Edge-detected key polling, same trick the existing menu uses (independent of Screen input signatures). */
public final class KeyPoll {
    private boolean was;
    private final int key, key2;

    public KeyPoll(int key, int key2) { this.key = key; this.key2 = key2; }

    public boolean pressed() {
        long win = GLFW.glfwGetCurrentContext();
        if (win == 0L) return false;
        boolean d = GLFW.glfwGetKey(win, key) == GLFW.GLFW_PRESS || GLFW.glfwGetKey(win, key2) == GLFW.GLFW_PRESS;
        boolean r = d && !was;
        was = d;
        return r;
    }

    public static KeyPoll enter() { return new KeyPoll(GLFW.GLFW_KEY_ENTER, GLFW.GLFW_KEY_KP_ENTER); }
}
