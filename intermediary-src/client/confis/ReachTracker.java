package client.confis;

import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.class_1269;
import net.minecraft.class_238;
import net.minecraft.class_243;

/** Remembers the distance of your last hit (eye to the closest point of the target's hitbox). */
public final class ReachTracker {
    private static double last = -1;
    private static long lastTime;

    private ReachTracker() {}

    public static void register() {
        AttackEntityCallback.EVENT.register((player, level, hand, entity, hit) -> {
            if (level.method_8608()) {
                class_243 eye = player.method_33571();
                class_238 b = entity.method_5829();
                class_243 p = new class_243(clamp(eye.field_1352, b.field_1323, b.field_1320), clamp(eye.field_1351, b.field_1322, b.field_1325), clamp(eye.field_1350, b.field_1321, b.field_1324));
                last = eye.method_1022(p);
                lastTime = System.currentTimeMillis();
            }
            return class_1269.field_5811;
        });
    }

    private static double clamp(double v, double lo, double hi) { return Math.max(lo, Math.min(hi, v)); }

    /** Distance of the last hit, or -1 if none in the last 5 seconds. */
    public static double last() { return System.currentTimeMillis() - lastTime > 5000 ? -1 : last; }
}
