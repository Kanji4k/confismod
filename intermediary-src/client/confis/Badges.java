package client.confis;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/** Builds the nametag text: "C" in front of mod users, tier tag behind the name. */
public final class Badges {
    private Badges() {}

    public static Text decorate(PlayerEntity p, Text original) {
        Config cfg = Config.get();
        String name = p.getName().getString();
        MinecraftClient mc = MinecraftClient.getInstance();
        boolean self = mc.player != null && p == mc.player;
        Text out = original;
        if (cfg.nameBadge && (self || ModUsers.has(name)) && !original.getString().startsWith("C ")) {
            out = Text.empty()
                    .append(Text.literal("C").formatted(Formatting.BOLD, Formatting.AQUA))
                    .append(Text.literal(" "))
                    .append(out);
        }
        Module tm = Modules.byId("tiers");
        if (tm != null && tm.cfg().enabled) {
            String t = TierTagger.tag(name);
            if (!t.isEmpty()) out = Text.empty().append(out).append(Text.literal(" [" + t + "]").formatted(Formatting.GOLD));
        }
        return out;
    }
}
