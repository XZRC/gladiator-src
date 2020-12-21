package me.rigamortis.seppuku.impl.mixin.entity;

import me.rigamortis.seppuku.Seppuku;
import me.rigamortis.seppuku.api.event.player.EventApplyCollision;
import me.rigamortis.seppuku.api.event.player.EventPushedByWater;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityPlayer.class)
public abstract class MixinEntityPlayer extends EntityLivingBase {

    // Makes the compiler happy
    public MixinEntityPlayer(World worldIn) {
        super(worldIn);
    }

    @Inject(at = @At("HEAD"), method = "isPushedByWater", cancellable = true)
    private void onIsPushedByWater(CallbackInfoReturnable<Boolean> cir) {
        final EventPushedByWater event = new EventPushedByWater();
        Seppuku.dispatchEvent(event);
        if (event.isCanceled()) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
}
