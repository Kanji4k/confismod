package client.confis.mixin;

import client.confis.Config;
import client.confis.DropScale;
import net.minecraft.class_10039;
import net.minecraft.class_1542;
import net.minecraft.class_4587;
import net.minecraft.class_7923;
import net.minecraft.class_916;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Scales dropped items. Everything is matched by method NAME with require = 0, so if a Minecraft
 * update changes the signature the game still starts and drops just keep their normal size.
 */
@Mixin(class_916.class)
public class ItemDropScaleMixin {
    @Inject(method = {"updateRenderState(Lnet/minecraft/class_1542;Lnet/minecraft/class_10039;F)V", "extractRenderState"}, at = @At("TAIL"), require = 0)
    private void confis$remember(class_1542 entity, class_10039 state, float partial, CallbackInfo ci) {
        String id = class_7923.field_41178.method_10221(entity.method_6983().method_7909()).method_12832();
        DropScale.BY_STATE.put(state, DropScale.forId(id));
    }

    @Inject(method = {"render(Lnet/minecraft/class_10039;Lnet/minecraft/class_4587;Lnet/minecraft/class_11659;Lnet/minecraft/class_12075;)V", "submit"}, at = @At("HEAD"), require = 0)
    private void confis$push(class_10039 state, class_4587 pose, net.minecraft.class_11659 c,
                             net.minecraft.class_12075 cam, CallbackInfo ci) {
        float s = DropScale.BY_STATE.getOrDefault(state, 1f);
        pose.method_22903();
        pose.method_22905(s, s, s);
    }

    @Inject(method = {"render(Lnet/minecraft/class_10039;Lnet/minecraft/class_4587;Lnet/minecraft/class_11659;Lnet/minecraft/class_12075;)V", "submit"}, at = @At("RETURN"), require = 0)
    private void confis$pop(class_10039 state, class_4587 pose, net.minecraft.class_11659 c,
                            net.minecraft.class_12075 cam, CallbackInfo ci) {
        pose.method_22909();
    }
}
