package client.confis.mixin;

import client.confis.Zoom;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Narrows the field of view while the zoom key is held. Matched by name, require = 0. */
@Mixin(GameRenderer.class)
public class ZoomMixin {
    @Inject(method = "getFov", at = @At("RETURN"), cancellable = true, require = 0)
    private void confis$zoom(CallbackInfoReturnable<Float> cir) {
        float m = Zoom.mult();
        if (m != 1f) cir.setReturnValue(cir.getReturnValue() / m);
    }
}
