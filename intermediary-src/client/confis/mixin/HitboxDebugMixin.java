package client.confis.mixin;

import client.confis.HitboxRenderer;
import java.util.List;
import net.minecraft.client.render.debug.DebugRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * In 1.21.11 hitboxes are drawn by a debug renderer that only exists in this list while F3+B is on.
 * While any hitbox switch is on, our own copy of it is kept in the list (see HitboxRenderer.sync).
 */
@Mixin(DebugRenderer.class)
public abstract class HitboxDebugMixin {
    @Shadow @Final private List<DebugRenderer.Renderer> renderers;

    @Inject(method = "render", at = @At("HEAD"), require = 0)
    private void confis$sync(CallbackInfo ci) {
        HitboxRenderer.sync(renderers);
    }

    @Inject(method = "initRenderers", at = @At("TAIL"), require = 0)
    private void confis$syncAfterInit(CallbackInfo ci) {
        HitboxRenderer.sync(renderers);
    }
}
