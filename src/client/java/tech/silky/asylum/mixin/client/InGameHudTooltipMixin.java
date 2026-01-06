package tech.silky.asylum.mixin.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.silky.asylum.client.misc.AsylumRuntimeData;

@Mixin(InGameHud.class)
public class InGameHudTooltipMixin {
    @Inject(
        method = "renderHeldItemTooltip",
        at = @At("HEAD"),
        cancellable = true
    )
    private void hideHeldItemTooltip(
            DrawContext context, CallbackInfo ci
    ) {
        if (!AsylumRuntimeData.INSTANCE.getDisplayFlags().get(
            AsylumRuntimeData.HOTBAR_TOOLTIP
        ))
            ci.cancel();
    }
}
