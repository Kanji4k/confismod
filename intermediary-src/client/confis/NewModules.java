package client.confis;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

/** Inventory hub, totem/potion count, saturation, zoom, tier tagger, shulker tooltip, crosshair indicator, effect icons. */
public final class NewModules {
    private NewModules() {}

    private static String roman(int n) {
        String[] r = {"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"};
        return n >= 1 && n <= 10 ? r[n] : String.valueOf(n);
    }

    public static String saturation(MinecraftClient mc) {
        if (mc.player == null) return "Saturation: --";
        return "Saturation: " + String.format("%.1f", mc.player.getHungerManager().getSaturationLevel());
    }

    // ------------------------------------------------------------ inventory hub
    public static class InventoryModule extends Module {
        public InventoryModule() { super("inventory", "Inventory Hub", "HUD", 0.010, 0.560); }

        @Override public int width(MinecraftClient mc) { return 9 * 18 + 2; }
        @Override public int height(MinecraftClient mc) { return cfg().quickBar ? 4 * 18 + 3 + 2 : 3 * 18 + 2; }

        @Override
        public void draw(DrawContext g, MinecraftClient mc, Config.ModuleCfg c, int x, int y, boolean preview) {
            if (c.showBg) Glass.draw(g, x, y, width(mc), height(mc), c.bg);
            PlayerInventory inv = mc.player == null ? null : mc.player.getInventory();
            int rows = c.quickBar ? 4 : 3;          // row 3 = Quick Access Bar (hotbar)
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < 9; col++) {
                    int sx = x + 1 + col * 18;
                    int sy = y + 1 + row * 18 + (row == 3 ? 3 : 0);
                    Glass.slot(g, sx, sy, 17, 17);
                    if (inv == null) continue;
                    ItemStack s = inv.getStack(row == 3 ? col : 9 + row * 9 + col);
                    if (s.isEmpty()) continue;
                    g.drawItem(s, sx + 1, sy + 1);
                    g.drawStackOverlay(mc.textRenderer, s, sx + 1, sy + 1);
                }
            }
        }
    }

    // ------------------------------------------------------ totem / potion count
    public static class ItemCountModule extends Module {
        private final Item icon;
        private final Item[] items;
        private ItemStack iconStack;

        public ItemCountModule(String id, String name, double x, double y, Item icon, Item... items) {
            super(id, name, "HUD", x, y);
            this.icon = icon;
            this.items = items;
            defAccent = 0xFFFF5555;
            accentLabel = "Zero count color";
        }

        private int count(MinecraftClient mc) {
            if (mc.player == null) return 0;
            PlayerInventory inv = mc.player.getInventory();        // includes armor + offhand
            int n = 0;
            for (int i = 0; i < inv.size(); i++) {
                ItemStack s = inv.getStack(i);
                for (Item it : items) if (s.isOf(it)) { n += s.getCount(); break; }
            }
            return n;
        }

        @Override public int width(MinecraftClient mc) { return 24 + mc.textRenderer.getWidth("x" + count(mc)) + 6; }
        @Override public int height(MinecraftClient mc) { return 18; }

        @Override
        public void draw(DrawContext g, MinecraftClient mc, Config.ModuleCfg c, int x, int y, boolean preview) {
            if (iconStack == null) iconStack = new ItemStack(icon);
            int n = count(mc);
            if (c.showBg) Glass.draw(g, x, y, width(mc), 18, c.bg);
            g.drawItem(iconStack, x + 1, y + 1);
            g.drawText(mc.textRenderer, "x" + n, x + 22, y + 5, n == 0 ? c.accent : c.text, true);
        }
    }

    // ------------------------------------------------------------------- zoom
    public static class ZoomModule extends Module {
        public ZoomModule() {
            super("zoom", "Zoom", "HUD", 0.010, 0.520);
            defAccent = 0xFF55FF55;
            accentLabel = "Zooming color";
        }

        private String text() {
            return "Zoom [" + Zoom.keyName(Config.get().zoomKey) + "]  " + String.format("%.1fx", Config.get().zoomFactor);
        }

        @Override public int width(MinecraftClient mc) { return mc.textRenderer.getWidth(text()) + 8; }
        @Override public int height(MinecraftClient mc) { return 13; }

        @Override
        public void draw(DrawContext g, MinecraftClient mc, Config.ModuleCfg c, int x, int y, boolean preview) {
            if (c.showBg) Glass.draw(g, x, y, width(mc), 13, c.bg);
            g.drawText(mc.textRenderer, text(), x + 4, y + 3, Zoom.active() ? c.accent : c.text, true);
        }
    }

    // ------------------------------------------------- on/off only (Fullbright, No Blindness)
    /** No box on the HUD: just an on/off switch. The work is done in Fullbright.poll. Starts switched off. */
    public static class SwitchModule extends Module {
        public SwitchModule(String id, String name, String hint) {
            super(id, name, "HUD", 0, 0);
            defEnabled = false;
        }
        @Override public boolean movable() { return false; }
        @Override public int width(MinecraftClient mc) { return 0; }
        @Override public int height(MinecraftClient mc) { return 0; }
        @Override public void draw(DrawContext g, MinecraftClient mc, Config.ModuleCfg c, int x, int y, boolean preview) {}
    }

    // ---------------------------------------------------------------- freecam
    public static class FreecamModule extends Module {
        public FreecamModule() {
            super("freecam", "Freecam", "HUD", 0.010, 0.500);
            defAccent = 0xFF55FF55;
            accentLabel = "Freecam on color";
        }

        private String text() {
            return "Freecam [" + Zoom.keyName(Config.get().freecamKey) + "]  " + (Freecam.active() ? "ON" : "OFF");
        }

        @Override public int width(MinecraftClient mc) { return mc.textRenderer.getWidth(text()) + 8; }
        @Override public int height(MinecraftClient mc) { return 13; }

        @Override
        public void draw(DrawContext g, MinecraftClient mc, Config.ModuleCfg c, int x, int y, boolean preview) {
            if (c.showBg) Glass.draw(g, x, y, width(mc), 13, c.bg);
            g.drawText(mc.textRenderer, text(), x + 4, y + 3, Freecam.active() ? c.accent : c.text, true);
        }
    }

    // ------------------------------------------------------------ tier tagger
    public static class TierModule extends Module {
        public TierModule() { super("tiers", "Tier Tagger", "INFO", 0.010, 0.620); }

        private String text(MinecraftClient mc) {
            String t = mc.player == null ? "" : TierTagger.tag(mc.player.getName().getString());
            return "Tier: " + (t.isEmpty() ? "--" : t);
        }

        @Override public int width(MinecraftClient mc) { return mc.textRenderer.getWidth(text(mc)) + 8; }
        @Override public int height(MinecraftClient mc) { return 13; }

        @Override
        public void draw(DrawContext g, MinecraftClient mc, Config.ModuleCfg c, int x, int y, boolean preview) {
            if (c.showBg) Glass.draw(g, x, y, width(mc), 13, c.bg);
            g.drawText(mc.textRenderer, text(mc), x + 4, y + 3, c.text, true);
        }
    }

    // ----------------------------------------------------------- shulker tooltip
    /** No box on the HUD: just the on/off switch for the Shift-hover shulker preview. */
    public static class ShulkerModule extends Module {
        public ShulkerModule() { super("shulker", "Shulker Tooltip", "HUD", 0, 0); }
        @Override public boolean movable() { return false; }
        @Override public int width(MinecraftClient mc) { return 0; }
        @Override public int height(MinecraftClient mc) { return 0; }
        @Override public void draw(DrawContext g, MinecraftClient mc, Config.ModuleCfg c, int x, int y, boolean preview) {}
    }

    // ------------------------------------------------------- crosshair indicator
    /** Four ticks around the crosshair (accent color while aiming at an entity) + attack cooldown bar. */
    public static class CrosshairModule extends Module {
        public CrosshairModule() {
            super("crosshair", "Crosshair Indicator", "HUD", 0, 0);
            defAccent = 0xFFFF5555;
            accentLabel = "On-target color";
        }

        @Override public boolean movable() { return false; }
        @Override public int width(MinecraftClient mc) { return 0; }
        @Override public int height(MinecraftClient mc) { return 0; }
        @Override public void draw(DrawContext g, MinecraftClient mc, Config.ModuleCfg c, int x, int y, boolean preview) {}

        @Override
        public void drawFixed(DrawContext g, MinecraftClient mc, Config.ModuleCfg c, int W, int H) {
            if (mc.player == null) return;
            if (mc.currentScreen != null && !(mc.currentScreen instanceof ConfigScreen)) return;
            int cx = W / 2, cy = H / 2, gap = 4, len = 4;
            int col = mc.targetedEntity != null ? c.accent : c.text;
            g.fill(cx - gap - len, cy, cx - gap, cy + 1, col);
            g.fill(cx + gap + 1, cy, cx + gap + 1 + len, cy + 1, col);
            g.fill(cx, cy - gap - len, cx + 1, cy - gap, col);
            g.fill(cx, cy + gap + 1, cx + 1, cy + gap + 1 + len, col);
            float p = mc.player.getAttackCooldownProgress(0f);
            if (p < 1f) {
                int bw = 17, bx = cx - bw / 2, by = cy + gap + len + 5;
                g.fill(bx, by, bx + bw, by + 2, 0x80000000);
                g.fill(bx, by, bx + Math.round(bw * p), by + 2, col);
            }
        }
    }

    // ------------------------------------------------------ effect timer (icons)
    public static class EffectIconModule extends Module {
        private record Row(Identifier icon, String time, String lvl, boolean low) {}

        public EffectIconModule() {
            super("effects", "Effect Timer", "HUD", 0.010, 0.300);
            defAccent = 0xFFFF5555;
            accentLabel = "Expiring (under 10s) color";
        }

        private Row row(StatusEffectInstance e) {
            Identifier id = e.getEffectType().getKey().map(k -> k.getValue()).orElse(Identifier.of("minecraft", "speed"));
            int secs = Math.max(0, e.getDuration() / 20);
            boolean inf = e.isInfinite();
            String t = inf ? "inf" : (secs / 60) + ":" + String.format("%02d", secs % 60);
            return new Row(id.withPrefixedPath("mob_effect/"), t, e.getAmplifier() > 0 ? roman(e.getAmplifier() + 1) : "", !inf && secs < 10);
        }

        private List<Row> rows(MinecraftClient mc) {
            List<Row> out = new ArrayList<>();
            if (mc.player != null) {
                List<StatusEffectInstance> list = new ArrayList<>(mc.player.getStatusEffects());
                list.sort(Comparator.comparingInt(StatusEffectInstance::getDuration).reversed());
                for (StatusEffectInstance e : list) out.add(row(e));
            }
            if (out.isEmpty() && mc.currentScreen instanceof ConfigScreen) {      // placeholder while editing the HUD
                out.add(row(new StatusEffectInstance(StatusEffects.SPEED, 1800, 1)));
                out.add(row(new StatusEffectInstance(StatusEffects.REGENERATION, 160, 0)));
            }
            return out;
        }

        @Override
        public int width(MinecraftClient mc) {
            int w = 56;
            for (Row r : rows(mc))
                w = Math.max(w, 24 + mc.textRenderer.getWidth(r.time()) + (r.lvl().isEmpty() ? 0 : mc.textRenderer.getWidth(r.lvl()) + 5) + 6);
            return w;
        }

        @Override public int height(MinecraftClient mc) { return Math.max(1, rows(mc).size()) * 20; }

        @Override
        public void draw(DrawContext g, MinecraftClient mc, Config.ModuleCfg c, int x, int y, boolean preview) {
            List<Row> rows = rows(mc);
            int w = width(mc);
            boolean blink = (System.currentTimeMillis() / 400) % 2 == 0;
            for (int i = 0; i < rows.size(); i++) {
                Row r = rows.get(i);
                int ry = y + i * 20;
                if (c.showBg) Glass.draw(g, x, ry, w, 19, c.bg);
                g.drawGuiTexture(RenderPipelines.GUI_TEXTURED, r.icon(), x + 1, ry + 1, 18, 18);
                int col = r.low() && blink ? c.accent : c.text;
                g.drawText(mc.textRenderer, r.time(), x + 23, ry + 6, col, true);
                if (!r.lvl().isEmpty())
                    g.drawText(mc.textRenderer, r.lvl(), x + 23 + mc.textRenderer.getWidth(r.time()) + 5, ry + 6, 0xFFAAAAAA, true);
            }
        }
    }
}
