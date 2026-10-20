package client.confis.mixin;

import client.confis.Config;
import net.minecraft.client.render.entity.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Nametags OFF: no entity shows a name above its head (Yarn names, checked against 1.21.11). */
@Mixin(EntityRenderer.class)
public class NametagMixin {
    @Inject(method = "hasLabel", at = @At("RETURN"), cancellable = true, require = 0)
    private void confis$hideNames(CallbackInfoReturnable<Boolean> cir) {
        if (!Config.get().nametags) cir.setReturnValue(false);
    }

    @Inject(method = "renderLabelIfPresent", at = @At("HEAD"), cancellable = true, require = 0)
    private void confis$skipLabel(CallbackInfo ci) {
        if (!Config.get().nametags) ci.cancel();
    }
}
