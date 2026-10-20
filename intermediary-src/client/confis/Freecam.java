package client.confis;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

/**
 * Detached camera. Your real player stays still (all game keys are released while it is on) and the
 * camera flies around with W A S D / Space / Shift (Ctrl = faster). The toggle key is changeable in the
 * Freecam module options. The camera itself is moved by FreecamMixin (client.confis.mixin).
 */
public final class Freecam {
    private static boolean on, keyWas, cameraSeen;
    private static double x, y, z;
    private static float yaw, pitch;
    private static float savedYaw, savedPitch, frozenYaw;
    private static long lastNs, startedNs;

    private Freecam() {}

    public static boolean active() { return on; }
    public static double x() { return x; }
    public static double y() { return y; }
    public static double z() { return z; }
    public static float yaw() { return yaw; }
    public static float pitch() { return pitch; }

    /** Called every frame from Hud.render: key toggle, safety checks, releasing the game keys. */
    public static void poll(MinecraftClient mc) {
        Module m = Modules.byId("freecam");
        boolean enabled = m != null && m.cfg().enabled;
        long win = GLFW.glfwGetCurrentContext();
        boolean down = win != 0L && GLFW.glfwGetKey(win, Config.get().freecamKey) == GLFW.GLFW_PRESS;
        boolean edge = down && !keyWas;
        keyWas = down;

        if (on && (!enabled || mc.player == null || mc.world == null)) { stop(mc, mc.player != null); return; }
        if (edge && enabled && mc.currentScreen == null && mc.player != null && mc.world != null) {
            if (on) stop(mc, true); else start(mc);
        }
        if (!on) return;

        // safety net: if the camera hook never ran (mixin did not apply), give control back
        if (!cameraSeen && System.nanoTime() - startedNs > 1_500_000_000L) {
            stop(mc, true);
            mc.inGameHud.setOverlayMessage(Text.literal("Freecam is not supported on this version"), false);
            return;
        }
        releaseKeys(mc.options);
    }

    private static void start(MinecraftClient mc) {
        ClientPlayerEntity p = mc.player;
        Vec3d c = p.getEyePos();
        x = c.x; y = c.y; z = c.z;
        yaw = p.getYaw();
        pitch = p.getPitch();
        savedYaw = yaw;
        savedPitch = pitch;
        frozenYaw = yaw;
        p.setPitch(0f);                       // mouse movement is read as the difference from this
        on = true;
        cameraSeen = false;
        startedNs = lastNs = System.nanoTime();
        mc.inGameHud.setOverlayMessage(Text.literal("Freecam ON  [" + Zoom.keyName(Config.get().freecamKey) + "]"), false);
    }

    private static void stop(MinecraftClient mc, boolean message) {
        on = false;
        if (mc.player != null) {
            mc.player.setYaw(savedYaw);
            mc.player.setPitch(savedPitch);
        }
        if (message && mc.inGameHud != null) mc.inGameHud.setOverlayMessage(Text.literal("Freecam OFF"), false);
    }

    private static void releaseKeys(GameOptions o) {
        KeyBinding[] keys = {o.forwardKey, o.backKey, o.leftKey, o.rightKey, o.jumpKey, o.sneakKey, o.sprintKey,
                o.attackKey, o.useKey};
        for (KeyBinding k : keys) {
            k.setPressed(false);
            while (k.wasPressed()) { /* drop queued presses */ }
        }
    }

    /** Called by FreecamMixin once per rendered frame, right before the camera is placed. */
    public static void step(MinecraftClient mc) {
        cameraSeen = true;
        ClientPlayerEntity p = mc.player;
        long now = System.nanoTime();
        float dt = Math.min(0.1f, (now - lastNs) / 1_000_000_000f);
        lastNs = now;
        if (p == null) return;

        // mouse look: take what the mouse added to the player's rotation, then put the player back
        yaw += p.getYaw() - frozenYaw;
        pitch = MathHelper.clamp(pitch + p.getPitch(), -90f, 90f);
        p.setYaw(frozenYaw);
        p.setPitch(0f);

        long win = GLFW.glfwGetCurrentContext();
        if (win == 0L || mc.currentScreen != null) return;
        double fwd = axis(win, GLFW.GLFW_KEY_W, GLFW.GLFW_KEY_S);
        double side = axis(win, GLFW.GLFW_KEY_A, GLFW.GLFW_KEY_D);     // +1 = left
        double up = axis(win, GLFW.GLFW_KEY_SPACE, GLFW.GLFW_KEY_LEFT_SHIFT);
        double speed = 10.0 * Config.get().freecamSpeed * dt
                * (GLFW.glfwGetKey(win, GLFW.GLFW_KEY_LEFT_CONTROL) == GLFW.GLFW_PRESS ? 2.0 : 1.0);

        double yr = Math.toRadians(yaw), pr = Math.toRadians(pitch);
        double fx = -Math.sin(yr) * Math.cos(pr), fy = -Math.sin(pr), fz = Math.cos(yr) * Math.cos(pr);
        double lx = Math.cos(yr), lz = Math.sin(yr);                   // left of the facing direction
        x += (fx * fwd + lx * side) * speed;
        y += (fy * fwd + up) * speed;
        z += (fz * fwd + lz * side) * speed;
    }

    private static double axis(long win, int plus, int minus) {
        return (GLFW.glfwGetKey(win, plus) == GLFW.GLFW_PRESS ? 1 : 0) - (GLFW.glfwGetKey(win, minus) == GLFW.GLFW_PRESS ? 1 : 0);
    }
}
