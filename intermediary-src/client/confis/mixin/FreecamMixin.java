package client.confis.mixin;

import client.confis.Freecam;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Moves the camera away from the player while Freecam is on. Lives in its own mixin config. */
@Mixin(Camera.class)
public abstract class FreecamMixin {
    @Shadow protected abstract void setPos(double x, double y, double z);
    @Shadow protected abstract void setRotation(float yaw, float pitch);

    @Inject(method = "update", at = @At("TAIL"), require = 0)
    private void confis$freecam(CallbackInfo ci) {
        if (!Freecam.active()) return;
        Freecam.step(MinecraftClient.getInstance());
        setPos(Freecam.x(), Freecam.y(), Freecam.z());
        setRotation(Freecam.yaw(), Freecam.pitch());
    }

    /** Draw your own player model while the camera is detached. */
    @Inject(method = "isThirdPerson", at = @At("RETURN"), cancellable = true, require = 0)
    private void confis$thirdPerson(CallbackInfoReturnable<Boolean> cir) {
        if (Freecam.active()) cir.setReturnValue(true);
    }
}
