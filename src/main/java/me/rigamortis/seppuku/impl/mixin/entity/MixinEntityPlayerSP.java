package me.rigamortis.seppuku.impl.mixin.entity;

import com.mojang.authlib.GameProfile;
import me.rigamortis.seppuku.Seppuku;
import me.rigamortis.seppuku.api.event.EventStageable;
import me.rigamortis.seppuku.api.event.player.*;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.MoverType;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author cats
 */
@Mixin(EntityPlayerSP.class)
public abstract class MixinEntityPlayerSP extends AbstractClientPlayer {
    // Compiler bait
    public MixinEntityPlayerSP(World worldIn, GameProfile playerProfile) {
        super(worldIn, playerProfile);
    }

    @Shadow
    protected abstract void updateAutoJump(float p_189810_1_, float p_189810_2_);

    @Inject(method = "onUpdate", at = @At("HEAD"), cancellable = true)
    private void onUpdatePre(CallbackInfo ci) {
        EventPlayerUpdate event = new EventPlayerUpdate(EventStageable.EventStage.PRE);
        Seppuku.INSTANCE.getCameraManager().update();
        Seppuku.dispatchEvent(event);
        if (event.isCanceled()) ci.cancel();
    }

    @Inject(method = "onUpdate", at = @At("RETURN"))
    private void onUpdatePost(CallbackInfo ci) {
        EventPlayerUpdate event = new EventPlayerUpdate(EventStageable.EventStage.POST);
        Seppuku.dispatchEvent(event);
    }


    @Inject(method = "onUpdateWalkingPlayer", at = @At("HEAD"), cancellable = true)
    private void onUpdateWalkingPlayerPre(CallbackInfo ci) {
        Seppuku.INSTANCE.getRotationManager().updateRotations();
        Seppuku.INSTANCE.getPositionManager().updatePosition();
        EventUpdateWalkingPlayer event = new EventUpdateWalkingPlayer(EventStageable.EventStage.PRE);
        Seppuku.INSTANCE.getEventManager().dispatchEvent(event);
        if (event.isCanceled()) ci.cancel();
    }

    @Inject(method = "onUpdateWalkingPlayer", at = @At("RETURN"))
    private void onUpdateWalkingPlayerPost(CallbackInfo ci) {
        EventUpdateWalkingPlayer event = new EventUpdateWalkingPlayer(EventStageable.EventStage.POST);
        Seppuku.INSTANCE.getEventManager().dispatchEvent(event);

        Seppuku.INSTANCE.getPositionManager().restorePosition();
        Seppuku.INSTANCE.getRotationManager().restoreRotations();
    }

    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    private void onSendChatMessage(String message, CallbackInfo ci) {
        EventSendChatMessage event = new EventSendChatMessage(message);
        Seppuku.dispatchEvent(event);
        if (event.isCanceled()) ci.cancel();
    }

    @Inject(method = "swingArm", at = @At("HEAD"), cancellable = true)
    private void onSwingArm(EnumHand hand, CallbackInfo ci) {
        final EventSwingArm event = new EventSwingArm(hand);
        Seppuku.dispatchEvent(event);
        if (event.isCanceled()) ci.cancel();
    }

    @Inject(method = "closeScreen", at = @At("HEAD"), cancellable = true)
    private void onCloseScreen(CallbackInfo ci) {
        final EventCloseScreen event = new EventCloseScreen();
        Seppuku.dispatchEvent(event);
        if (event.isCanceled()) ci.cancel();
    }

    @Inject(method = "pushOutOfBlocks", at = @At("HEAD"), cancellable = true)
    private void onPushOutOfBlocks(double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
        final EventPushOutOfBlocks event = new EventPushOutOfBlocks();
        Seppuku.dispatchEvent(event);
        if (event.isCanceled()) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }

    @Inject(method = "onLivingUpdate", at = @At(remap = false, value = "INVOKE", target = "Lnet/minecraftforge/client/ForgeHooksClient;onInputUpdate(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/util/MovementInput;)V"))
    private void onUpdateInput(CallbackInfo ci) {
        Seppuku.dispatchEvent(new EventUpdateInput());
    }

    @Inject(method = "move", at = @At("HEAD"), cancellable = true)
    private void onMove(MoverType type, double x, double y, double z, CallbackInfo ci) {
        ci.cancel();
        double d0 = posX;
        double d1 = posZ;
        final EventMove event = new EventMove(type, x, y, z);
        Seppuku.dispatchEvent(event);
        if (event.isCanceled()) return;
        super.move(type, event.getX(), event.getY(), event.getZ());
        updateAutoJump((float) (posX - d0), (float) (posZ - d1));
    }
}
