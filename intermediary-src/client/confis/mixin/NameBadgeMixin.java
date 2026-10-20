package client.confis.mixin;

import client.confis.Badges;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** "C" in front of mod users and the tier tag behind a player's name. */
@Mixin(Entity.class)
public class NameBadgeMixin {
    @Inject(method = "getDisplayName", at = @At("RETURN"), cancellable = true, require = 0)
    private void confis$badge(CallbackInfoReturnable<Text> cir) {
        if (!((Object) this instanceof PlayerEntity p)) return;
        Text original = cir.getReturnValue();
        if (original == null) return;
        Text out = Badges.decorate(p, original);
        if (out != original) cir.setReturnValue(out);
    }
}
