package client.confis.mixin;

import client.confis.Config;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Players draw their label (and score under it) in their own method. */
@Mixin(PlayerEntityRenderer.class)
public class PlayerNametagMixin {
    @Inject(method = "renderLabelIfPresent", at = @At("HEAD"), cancellable = true, require = 0)
    private void confis$skipLabel(CallbackInfo ci) {
        if (!Config.get().nametags) ci.cancel();
    }
}
