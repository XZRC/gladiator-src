package me.rigamortis.seppuku.impl.mixin.entity;

import me.rigamortis.seppuku.Seppuku;
import me.rigamortis.seppuku.api.event.player.EventApplyCollision;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(Entity.class)
public abstract class MixinEntity {

    @Inject(method = "applyEntityCollision", cancellable = true, at =@At("HEAD"))
    private void onApplyEntityCollision(Entity entityIn, CallbackInfo ci) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        // Inspection is disabled here because these types will be convertible at mixin runtime.
        //noinspection EqualsBetweenInconvertibleTypes
        if ((player != null) && (player.equals(this) || player.equals(entityIn))) {
            final EventApplyCollision event = new EventApplyCollision();
            Seppuku.dispatchEvent(event);
            if (event.isCanceled()) {
                ci.cancel();
            }
        }
    }
}
