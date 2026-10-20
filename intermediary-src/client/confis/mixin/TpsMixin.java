package client.confis.mixin;

import client.confis.TpsTracker;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * The server sends this packet every 20 ticks. Counting when it arrives gives the server TPS.
 * (The old version matched a Mojang method name that does not exist under Yarn, so it never ran.)
 */
@Mixin(WorldTimeUpdateS2CPacket.class)
public class TpsMixin {
    @Inject(method = "apply", at = @At("HEAD"), require = 0)
    private void confis$onTime(CallbackInfo ci) {
        TpsTracker.onTimePacket();
    }
}
