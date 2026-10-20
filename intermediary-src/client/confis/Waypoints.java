package client.confis;

import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_332;

/** Draws waypoint markers projected onto the screen. */
public final class Waypoints {
    public static final int[] PALETTE = {0xFF5555, 0x55FF55, 0x5555FF, 0xFFFF55, 0xFF55FF, 0x55FFFF, 0xFFAA00};

    private Waypoints() {}

    public static int nextColor(Config c) {
        return PALETTE[c.waypointList.size() % PALETTE.length];
    }

    public static void render(class_332 g, class_310 mc, Config.ModuleCfg mod) {
        Config c = Config.get();
        if (c.waypointList.isEmpty() || mc.field_1687 == null || mc.field_1724 == null) return;
        String dim = mc.field_1687.method_27983().toString();

        class_243 eye = mc.field_1724.method_33571();
        class_243 f = mc.field_1724.method_5828(1.0F);
        double rx = -f.field_1350, rz = f.field_1352;
        double rl = Math.sqrt(rx * rx + rz * rz);
        if (rl < 1e-6) return;
        rx /= rl; rz /= rl;
        double ux = -rz * f.field_1351;
        double uy = rz * f.field_1352 - rx * f.field_1350;
        double uz = rx * f.field_1351;

        int W = g.method_51421(), H = g.method_51443();
        double tanHalf = Math.tan(Math.toRadians(mc.field_1690.method_41808().method_41753()) / 2.0);
        double aspect = (double) W / H;

        for (Waypoint w : c.waypointList) {
            if (!dim.equals(w.dim)) continue;
            double dx = w.x + 0.5 - eye.field_1352, dy = w.y + 0.5 - eye.field_1351, dz = w.z + 0.5 - eye.field_1350;
            double cz = dx * f.field_1352 + dy * f.field_1351 + dz * f.field_1350;
            if (cz < 0.1) continue;
            double cx = dx * rx + dz * rz;
            double cy = dx * ux + dy * uy + dz * uz;
            int sx = (int) (W / 2.0 + (cx / cz) / (tanHalf * aspect) * (W / 2.0));
            int sy = (int) (H / 2.0 - (cy / cz) / tanHalf * (H / 2.0));
            if (sx < 4 || sx > W - 4 || sy < 12 || sy > H - 12) continue;

            double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
            String label = w.name + " [" + (int) dist + "m]";
            int tw = mc.field_1772.method_1727(label);
            if (mod.showBg) g.method_25294(sx - tw / 2 - 2, sy - 16, sx + tw / 2 + 2, sy - 5, mod.bg);
            g.method_25300(mc.field_1772, label, sx, sy - 14, mod.text);
            g.method_25294(sx - 3, sy - 3, sx + 4, sy + 4, 0xFF000000);
            g.method_25294(sx - 2, sy - 2, sx + 3, sy + 3, 0xFF000000 | w.color);
        }
    }
}
