package client.confis;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.effect.StatusEffects;

/** Fullbright (raises the brightness setting while on, puts it back when off) and No Blindness. */
public final class Fullbright {
    private static final double LEVEL = 15.0;
    private static boolean applied;

    private Fullbright() {}

    private static boolean on(String id) {
        Module m = Modules.byId(id);
        return m != null && m.cfg().enabled;
    }

    /** Called every frame from Hud.render. */
    public static void poll(MinecraftClient mc) {
        if (mc.options == null) return;
        var gamma = mc.options.getGamma();
        Config cfg = Config.get();
        if (on("fullbright")) {
            if (!applied) {
                // after a crash the raised value may still be saved in options.txt: use the stored original
                cfg.savedGamma = (gamma.getValue() > 1.0 && cfg.savedGamma >= 0) ? cfg.savedGamma : gamma.getValue();
                Config.save();
                applied = true;
            }
            if (gamma.getValue() != LEVEL) gamma.setValue(LEVEL);
        } else if (applied) {
            restore(mc);
        }

        if (on("noblind") && mc.player != null && mc.player.hasStatusEffect(StatusEffects.BLINDNESS)) {
            mc.player.removeStatusEffect(StatusEffects.BLINDNESS);   // client side only
        }
    }

    /** Puts the normal brightness back. Also called when the game closes. */
    public static void restore(MinecraftClient mc) {
        Config cfg = Config.get();
        if (mc.options != null && (applied || mc.options.getGamma().getValue() > 1.0) && cfg.savedGamma >= 0) {
            mc.options.getGamma().setValue(Math.min(1.0, cfg.savedGamma));
        }
        applied = false;
    }
}
