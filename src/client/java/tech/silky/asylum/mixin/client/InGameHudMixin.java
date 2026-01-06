package tech.silky.asylum.mixin.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.silky.asylum.client.misc.AsylumRuntimeData;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Inject(
        method = "renderHotbar",
        at = @At("HEAD"),
        cancellable = true
    )
    private void hideHotbar(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (!AsylumRuntimeData.INSTANCE.getDisplayFlags().get(
            AsylumRuntimeData.HOTBAR
        )) {
            ci.cancel();
        }
    }
}
