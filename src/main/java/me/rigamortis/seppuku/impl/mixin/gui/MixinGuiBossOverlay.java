package me.rigamortis.seppuku.impl.mixin.gui;

import me.rigamortis.seppuku.Seppuku;
import me.rigamortis.seppuku.api.event.render.EventRenderBossHealth;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiBossOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiBossOverlay.class)
public abstract class MixinGuiBossOverlay extends Gui {
    @Inject(method = "renderBossHealth", at = @At("HEAD"), cancellable = true)
    private void onRenderBossHealth(CallbackInfo ci) {
        final EventRenderBossHealth event = new EventRenderBossHealth();
        Seppuku.dispatchEvent(event);
        if (event.isCanceled()) ci.cancel();
    }
}
