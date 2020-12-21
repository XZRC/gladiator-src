package me.rigamortis.seppuku.impl.mixin.render;

import me.rigamortis.seppuku.Seppuku;
import me.rigamortis.seppuku.api.event.EventStageable;
import me.rigamortis.seppuku.api.event.render.EventRenderEntity;
import me.rigamortis.seppuku.impl.duck.MixinRenderManagerInterface;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author cats
 */
@Mixin(RenderManager.class)
public abstract class MixinRenderManager implements MixinRenderManagerInterface {
    @Accessor(value = "renderPosX")
    public abstract double getRenderPosX();

    @Accessor(value = "renderPosY")
    public abstract double getRenderPosY();

    @Accessor(value = "renderPosZ")
    public abstract double getRenderPosZ();

    @Inject(method = "renderEntity", at = @At("HEAD"), cancellable = true)
    private void onRenderEntityPre(Entity entityIn, double x, double y, double z, float yaw, float partialTicks, boolean p_188391_10_, CallbackInfo ci) {
        final EventRenderEntity event = new EventRenderEntity(EventStageable.EventStage.PRE, entityIn, x, y, z, yaw, partialTicks);
        Seppuku.dispatchEvent(event);
        if (event.isCanceled()) ci.cancel();
    }

    @Inject(method = "renderEntity", at = @At("RETURN"))
    private void onRenderEntity(Entity entityIn, double x, double y, double z, float yaw, float partialTicks, boolean p_188391_10_, CallbackInfo ci) {
        final EventRenderEntity event = new EventRenderEntity(EventStageable.EventStage.POST, entityIn, x, y, z, yaw, partialTicks);
        Seppuku.dispatchEvent(event);
        if (event.isCanceled()) ci.cancel();
    }
}
