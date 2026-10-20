package client.confis.mixin;

import client.confis.ShulkerPreview;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreen.class)
public abstract class ShulkerTooltipMixin {
    @Shadow protected Slot focusedSlot;

    private boolean confis$show() {
        if (focusedSlot == null) return false;
        HandledScreen<?> self = (HandledScreen<?>) (Object) this;
        return self.getScreenHandler().getCursorStack().isEmpty() && ShulkerPreview.wanted(focusedSlot.getStack());
    }

    @Inject(method = "drawMouseoverTooltip", at = @At("HEAD"), cancellable = true, require = 0)
    private void confis$hideVanilla(DrawContext ctx, int mx, int my, CallbackInfo ci) {
        if (confis$show()) ci.cancel();
    }

    @Inject(method = "render", at = @At("TAIL"), require = 0)
    private void confis$draw(DrawContext ctx, int mx, int my, float delta, CallbackInfo ci) {
        if (confis$show()) ShulkerPreview.draw(ctx, MinecraftClient.getInstance(), focusedSlot.getStack(), mx, my);
    }
}
