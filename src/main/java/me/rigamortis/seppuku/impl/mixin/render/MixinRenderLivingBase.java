package me.rigamortis.seppuku.impl.mixin.render;

import me.rigamortis.seppuku.Seppuku;
import me.rigamortis.seppuku.api.event.render.EventRenderName;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("rawtypes") // To stop the compiler from complaining about bare Render.
@Mixin(RenderLivingBase.class)
public abstract class MixinRenderLivingBase extends Render {
    // Compiler bait
    protected MixinRenderLivingBase(RenderManager renderManager) {
        super(renderManager);
    }

    @Inject(method = "renderName", at = @At("HEAD"), cancellable = true)
    private void onRenderName(EntityLivingBase entity, double x, double y, double z, CallbackInfo ci) {
        final EventRenderName event = new EventRenderName(entity);
        Seppuku.dispatchEvent(event);
        if (event.isCanceled()) ci.cancel();
    }
}
