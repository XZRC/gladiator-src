package me.rigamortis.seppuku.impl.mixin.entity;

import me.rigamortis.seppuku.Seppuku;
import me.rigamortis.seppuku.api.event.player.EventPreAttack;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PlayerControllerMP.class, priority = 9998)
public class MixinPlayerControllerMP {
    @Inject(method = "attackEntity", at = @At("HEAD"), cancellable = true)
    public void attackEntity(EntityPlayer playerIn, Entity targetEntity, CallbackInfo callbackInfo) {
        EventPreAttack preEvent = new EventPreAttack(playerIn, targetEntity);
        Seppuku.dispatchEvent(preEvent);
    }
}
