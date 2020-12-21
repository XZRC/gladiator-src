package me.rigamortis.seppuku.impl.mixin.entity;

import me.rigamortis.seppuku.Seppuku;
import me.rigamortis.seppuku.api.event.entity.EventHorseSaddled;
import me.rigamortis.seppuku.api.event.entity.EventSteerEntity;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractHorse.class)
public abstract class MixinAbstractHorse extends EntityAnimal {

    // Makes the compiler happy
    public MixinAbstractHorse(World worldIn) {
        super(worldIn);
    }

    // Allows steering without a saddle
    @Inject(at = @At("HEAD"), method = "canBeSteered", cancellable = true)
    public void onCanBeSteered(CallbackInfoReturnable<Boolean> cir) {
        final EventSteerEntity event = new EventSteerEntity();
        Seppuku.INSTANCE.getEventManager().dispatchEvent(event);
        if (event.isCanceled()) {
            cir.setReturnValue(true);
            cir.cancel();
        }
    }

    // Allows controlling and riding without a saddle.
    @Inject(at = @At("HEAD"), method = "isHorseSaddled", cancellable = true)
    public void onIsHorseSaddled(CallbackInfoReturnable<Boolean> cir) {
        final EventHorseSaddled event = new EventHorseSaddled();
        Seppuku.INSTANCE.getEventManager().dispatchEvent(event);
        if (event.isCanceled()) {
            cir.setReturnValue(true);
            cir.cancel();
        }
    }
}
