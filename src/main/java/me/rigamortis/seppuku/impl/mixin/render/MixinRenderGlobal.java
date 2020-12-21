package me.rigamortis.seppuku.impl.mixin.render;

import me.rigamortis.seppuku.Seppuku;
import me.rigamortis.seppuku.api.event.render.EventRenderBlockDamage;
import me.rigamortis.seppuku.api.event.render.EventRenderEntityOutlines;
import me.rigamortis.seppuku.api.event.render.EventRenderSky;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderGlobal.class)
public class MixinRenderGlobal {
    @Inject(method = "isRenderEntityOutlines", at = @At("HEAD"), cancellable = true)
    private void onIsRenderEntityOutlines(CallbackInfoReturnable<Boolean> cir) {
        final EventRenderEntityOutlines event = new EventRenderEntityOutlines();
        Seppuku.dispatchEvent(event);
        if (event.isCanceled()) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }

    @Inject(method = "renderSky(FI)V", at = @At("HEAD"), cancellable = true)
    private void onRenderSky(float partialTicks, int pass, CallbackInfo ci) {
        final EventRenderSky event = new EventRenderSky();
        Seppuku.dispatchEvent(event);
        if (event.isCanceled()) ci.cancel();
    }

    @Inject(method = "drawBlockDamageTexture", at = @At("HEAD"), cancellable = true)
    private void onDrawBlockDamageTexture(Tessellator tessellatorIn, BufferBuilder bufferBuilderIn, Entity entityIn, float partialTicks, CallbackInfo ci) {
        final EventRenderBlockDamage event = new EventRenderBlockDamage();
        Seppuku.dispatchEvent(event);
        if (event.isCanceled()) ci.cancel();
    }
}
