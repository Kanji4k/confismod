package client.confis;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import net.minecraft.class_1304;
import net.minecraft.class_1799;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_640;

public final class Modules {
    public static final List<Module> ALL = new ArrayList<>();

    static {
        ALL.add(new TextModule("fps", "FPS", "INFO", 0.010, 0.015, mc -> "FPS: " + Hud.fps()));
        ALL.add(new TextModule("ping", "Ping", "INFO", 0.010, 0.055, mc -> {
            int ping = -1;
            if (mc.method_1562() != null && mc.field_1724 != null) {
                class_640 info = mc.method_1562().method_2871(mc.field_1724.method_5667());
                if (info != null) ping = info.method_2959();
            }
            return "Ping: " + (ping < 0 ? "--" : ping + " ms");
        }));
        ALL.add(new TextModule("tps", "Server TPS", "INFO", 0.010, 0.095, mc -> {
            double t = TpsTracker.get();
            return "TPS: " + (t < 0 ? "--" : String.format("%.1f", t));
        }));
        ALL.add(new TextModule("cps", "CPS", "INFO", 0.010, 0.135,
                mc -> "CPS: " + Cps.left() + " | " + Cps.right()));
        ALL.add(new TextModule("clock", "Clock", "INFO", 0.010, 0.175, mc -> {
            DateTimeFormatter f = DateTimeFormatter.ofPattern(Config.get().clock24h ? "HH:mm" : "hh:mm a");
            return "Time: " + LocalTime.now().format(f);
        }));
        ALL.add(new TextModule("day", "Day", "INFO", 0.010, 0.215,
                mc -> "Day: " + (mc.field_1687 == null ? 0 : mc.field_1687.method_75260() / 24000L)));
        ALL.add(new ArmorModule());
        ALL.add(new KeystrokesModule());
        ALL.add(new WaypointsModule());
        ALL.add(new NewModules.EffectIconModule());
        ALL.add(new ExtraModules.ScoreboardModule());
        ALL.add(new ExtraModules.ReachModule());
        ALL.add(new ExtraModules.RangeModule());
        ALL.add(new TextModule("saturation", "Saturation", "INFO", 0.010, 0.335, NewModules::saturation));
        ALL.add(new NewModules.InventoryModule());
        ALL.add(new NewModules.ItemCountModule("totems", "Totem Count", 0.010, 0.470,
                net.minecraft.item.Items.TOTEM_OF_UNDYING, net.minecraft.item.Items.TOTEM_OF_UNDYING));
        ALL.add(new NewModules.ItemCountModule("potions", "Potion Count", 0.100, 0.470,
                net.minecraft.item.Items.SPLASH_POTION, net.minecraft.item.Items.POTION,
                net.minecraft.item.Items.SPLASH_POTION, net.minecraft.item.Items.LINGERING_POTION));
        ALL.add(new NewModules.ZoomModule());
        ALL.add(new NewModules.FreecamModule());
        ALL.add(new NewModules.SwitchModule("fullbright", "Fullbright", "See clearly in the dark."));
        ALL.add(new NewModules.SwitchModule("noblind", "No Blindness", "Removes the Blindness effect view."));
        ALL.add(new NewModules.TierModule());
        ALL.add(new NewModules.ShulkerModule());
        ALL.add(new NewModules.CrosshairModule());
    }

    private Modules() {}

    public static Module byId(String id) {
        for (Module m : ALL) if (m.id.equals(id)) return m;
        return null;
    }

    // ---------------------------------------------------------------- text box
    public static class TextModule extends Module {
        private final Function<class_310, String> text;

        TextModule(String id, String name, String cat, double x, double y, Function<class_310, String> text) {
            super(id, name, cat, x, y);
            this.text = text;
        }

        @Override public int width(class_310 mc) { return mc.field_1772.method_1727(text.apply(mc)) + 8; }
        @Override public int height(class_310 mc) { return 13; }

        @Override
        public void draw(class_332 g, class_310 mc, Config.ModuleCfg c, int x, int y, boolean preview) {
            String s = text.apply(mc);
            int w = mc.field_1772.method_1727(s) + 8;
            if (c.showBg) Glass.draw(g, x, y, w, 13, c.bg);
            g.method_51433(mc.field_1772, s, x + 4, y + 3, c.text, true);
        }
    }

    // ------------------------------------------------------------------- armor
    public static class ArmorModule extends Module {
        private static final class_1304[] SLOTS = {class_1304.field_6169, class_1304.field_6174,
                class_1304.field_6172, class_1304.field_6166, class_1304.field_6173};

        ArmorModule() { super("armor", "Armor Status", "HUD", 0.010, 0.400); }

        private static String mode(Config.ModuleCfg c) { return c.armorMode == null ? "NUMBER" : c.armorMode; }
        private static boolean left(Config.ModuleCfg c) { return "LEFT".equals(c.armorSide); }

        /** Durability text: NUMBER = "312", PERCENT = "89%", BOTH = "312 (89%)". */
        private static String label(Config.ModuleCfg c, int left, int max) {
            int pct = max <= 0 ? 0 : Math.round(left * 100f / max);
            return switch (mode(c)) {
                case "PERCENT" -> pct + "%";
                case "BOTH" -> left + " (" + pct + "%)";
                default -> String.valueOf(left);
            };
        }

        /** Widest text this box can show (full durability), so the box does not jump around. */
        private int textWidth(class_310 mc) {
            Config.ModuleCfg c = cfg();
            int w = mc.field_1772.method_1727(label(c, 592, 592));
            if (mc.field_1724 != null) {
                for (class_1304 slot : SLOTS) {
                    class_1799 st = mc.field_1724.method_6118(slot);
                    if (!st.method_7960() && st.method_7963())
                        w = Math.max(w, mc.field_1772.method_1727(label(c, st.method_7936(), st.method_7936())));
                }
            }
            return w;
        }

        @Override public int width(class_310 mc) { return 22 + textWidth(mc) + 6; }
        @Override public int height(class_310 mc) { return SLOTS.length * 19 - 2; }

        @Override
        public void draw(class_332 g, class_310 mc, Config.ModuleCfg c, int x, int y, boolean preview) {
            if (mc.field_1724 == null) return;
            int w = width(mc);
            boolean onLeft = left(c);
            int row = 0;
            for (class_1304 slot : SLOTS) {
                int ry = y + row * 19;
                row++;
                class_1799 stack = mc.field_1724.method_6118(slot);
                String s;
                if (stack.method_7960()) {
                    if (!preview) continue;
                    s = "-";
                } else if (stack.method_7963()) {
                    int max = stack.method_7936();
                    s = label(c, max - stack.method_7919(), max);
                } else if (stack.method_7947() > 1) {
                    s = "x" + stack.method_7947();
                } else {
                    s = "";
                }
                if (c.showBg) Glass.draw(g, x, ry, w, 17, c.bg);
                int tw = mc.field_1772.method_1727(s);
                if (!stack.method_7960()) g.method_51427(stack, onLeft ? x + w - 17 : x + 1, ry);
                if (!s.isEmpty()) {
                    int tx = onLeft ? x + w - 21 - tw : (stack.method_7960() ? x + 6 : x + 22);
                    g.method_51433(mc.field_1772, s, tx, ry + 5, c.text, true);
                }
            }
        }
    }

    // -------------------------------------------------------------- keystrokes
    public static class KeystrokesModule extends Module {
        KeystrokesModule() {
            super("keystrokes", "Keystrokes", "HUD", 0.010, 0.690);
            defAccent = 0xB0FFFFFF;
            accentLabel = "Pressed key color";
        }

        @Override public int width(class_310 mc) { return 70; }
        @Override public int height(class_310 mc) { return 84; }

        @Override
        public void draw(class_332 g, class_310 mc, Config.ModuleCfg c, int x, int y, boolean preview) {
            int box = 22, gap = 2;
            var o = mc.field_1690;
            key(g, mc, c, "W", x + box + gap, y, box, box, o.field_1894.method_1434());
            key(g, mc, c, "A", x, y + box + gap, box, box, o.field_1913.method_1434());
            key(g, mc, c, "S", x + box + gap, y + box + gap, box, box, o.field_1881.method_1434());
            key(g, mc, c, "D", x + 2 * (box + gap), y + box + gap, box, box, o.field_1849.method_1434());
            int my = y + 2 * (box + gap);
            int mw = (box * 3 + gap) / 2;
            key(g, mc, c, "LMB " + Cps.left(), x, my, mw, box, o.field_1886.method_1434());
            key(g, mc, c, "RMB " + Cps.right(), x + mw + gap, my, mw, box, o.field_1904.method_1434());
            key(g, mc, c, "", x, my + box + gap, box * 3 + gap * 2, 12, o.field_1903.method_1434());
        }

        private static void key(class_332 g, class_310 mc, Config.ModuleCfg c, String label,
                                int x, int y, int w, int h, boolean down) {
            if (down) Glass.draw(g, x, y, w, h, c.accent);
            else if (c.showBg) Glass.draw(g, x, y, w, h, c.bg);
            if (!label.isEmpty()) {
                g.method_25300(mc.field_1772, label, x + w / 2, y + (h - 8) / 2, down ? 0xFF000000 : c.text);
            }
        }
    }

    // --------------------------------------------------------------- waypoints
    /** Not a box on the HUD: draws markers in the world view. Only has colors + on/off. */
    public static class WaypointsModule extends Module {
        WaypointsModule() { super("waypoints", "Waypoints", "MAP", 0, 0); defBg = 0x80000000; }

        @Override public boolean movable() { return false; }
        @Override public int width(class_310 mc) { return 0; }
        @Override public int height(class_310 mc) { return 0; }
        @Override public void draw(class_332 g, class_310 mc, Config.ModuleCfg c, int x, int y, boolean preview) {}
    }
}
