package me.rigamortis.seppuku.impl.mixin.world;

import me.rigamortis.seppuku.Seppuku;
import me.rigamortis.seppuku.api.event.world.EventAddEntity;
import me.rigamortis.seppuku.api.event.world.EventLightUpdate;
import me.rigamortis.seppuku.api.event.world.EventRainStrength;
import me.rigamortis.seppuku.api.event.world.EventRemoveEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class)
public abstract class MixinWorld {
    @Inject(method = "checkLightFor", at = @At("HEAD"), cancellable = true)
    private void onCheckLightFor(EnumSkyBlock lightType, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        final EventLightUpdate event = new EventLightUpdate();
        Seppuku.dispatchEvent(event);
        if (!Minecraft.getMinecraft().isSingleplayer() && event.isCanceled()) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }

    @Inject(method = "getRainStrength", at = @At("HEAD"), cancellable = true)
    private void onGetRainStrength(float delta, CallbackInfoReturnable<Float> cir) {
        final EventRainStrength event = new EventRainStrength();
        Seppuku.dispatchEvent(event);
        if (event.isCanceled()) {
            cir.setReturnValue(0f);
            cir.cancel();
        }
    }

    @Inject(method = "onEntityAdded", at = @At("HEAD"))
    private void onEntityAdded(Entity entityIn, CallbackInfo ci) {
        Seppuku.dispatchEvent(new EventAddEntity(entityIn));
    }

    @Inject(method = "onEntityRemoved", at = @At("HEAD"))
    private void onEntityRemoved(Entity entityIn, CallbackInfo ci) {
        Seppuku.dispatchEvent(new EventRemoveEntity(entityIn));
    }
}
