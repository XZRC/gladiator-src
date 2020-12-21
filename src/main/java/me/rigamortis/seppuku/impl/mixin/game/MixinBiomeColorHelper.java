package me.rigamortis.seppuku.impl.mixin.game;

import me.rigamortis.seppuku.Seppuku;
import me.rigamortis.seppuku.api.event.world.EventFoliageColor;
import me.rigamortis.seppuku.api.event.world.EventGrassColor;
import me.rigamortis.seppuku.api.event.world.EventWaterColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.BiomeColorHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BiomeColorHelper.class)
public class MixinBiomeColorHelper {
    @Inject(at = @At("HEAD"), method = "getGrassColorAtPos", cancellable = true)
    private static void onGetGrassColorAtPos(IBlockAccess blockAccess, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        final EventGrassColor event = new EventGrassColor();
        Seppuku.dispatchEvent(event);
        if (event.isCanceled()) {
            cir.cancel();
            cir.setReturnValue(event.getColor());
        }
    }

    @Inject(at = @At("HEAD"), method = "getFoliageColorAtPos", cancellable = true)
    private static void onGetFoliageColorAtPos(IBlockAccess blockAccess, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        final EventFoliageColor event = new EventFoliageColor();
        Seppuku.dispatchEvent(event);
        if (event.isCanceled()) {
            cir.cancel();
            cir.setReturnValue(event.getColor());
        }
    }

    @Inject(at = @At("HEAD"), method = "getWaterColorAtPos", cancellable = true)
    private static void onGetWaterColorAtPos(IBlockAccess blockAccess, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        final EventWaterColor event = new EventWaterColor();
        Seppuku.dispatchEvent(event);
        if (event.isCanceled()) {
            cir.cancel();
            cir.setReturnValue(event.getColor());
        }
    }

}
