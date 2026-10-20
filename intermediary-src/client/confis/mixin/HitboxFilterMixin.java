package client.confis.mixin;

import client.confis.HitboxRenderer;
import net.minecraft.client.render.debug.EntityHitboxDebugRenderer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Our copy of the hitbox renderer only draws the entity types picked in Hitboxes & Nametags. */
@Mixin(EntityHitboxDebugRenderer.class)
public class HitboxFilterMixin {
    @Inject(method = "drawHitbox", at = @At("HEAD"), cancellable = true, require = 0)
    private void confis$filter(Entity entity, float tickProgress, boolean inLocalServer, CallbackInfo ci) {
        if ((Object) this == HitboxRenderer.ours() && !HitboxRenderer.wants(entity)) ci.cancel();
    }
}
