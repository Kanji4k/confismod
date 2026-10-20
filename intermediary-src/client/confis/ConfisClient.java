package client.confis;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.class_2960;

public class ConfisClient implements ClientModInitializer {
    public static final String MOD_ID = "confis0oooooo";
    public static final String NAME = "Confis0oooooo";

    @Override
    public void onInitializeClient() {
        Config.load();
        HudElementRegistry.attachElementAfter(
                VanillaHudElements.MISC_OVERLAYS,
                class_2960.method_60655(MOD_ID, "hud"),
                (graphics, deltaTracker) -> Hud.render(graphics));
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> { TpsTracker.reset(); ModUsers.refresh(); });
        MessengerLog.register();
        ReachTracker.register();
        HitboxRenderer.register();
        // hide the vanilla sidebar while the custom Scoreboard mod is on
        HudElementRegistry.replaceElement(VanillaHudElements.SCOREBOARD,
                original -> (graphics, deltaTracker) -> {
                    if (!Modules.byId("scoreboard").cfg().enabled) original.render(graphics, deltaTracker);
                });
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> { Fullbright.restore(client); Config.save(); });
    }
}
