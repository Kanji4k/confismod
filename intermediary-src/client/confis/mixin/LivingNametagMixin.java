package client.confis.mixin;

import client.confis.Config;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Mobs and players have their own copy of the name check, so it is hidden here too. */
@Mixin(LivingEntityRenderer.class)
public class LivingNametagMixin {
    @Inject(method = "hasLabel", at = @At("RETURN"), cancellable = true, require = 0)
    private void confis$hideNames(CallbackInfoReturnable<Boolean> cir) {
        if (!Config.get().nametags) cir.setReturnValue(false);
    }
}
