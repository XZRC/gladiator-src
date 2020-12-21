package me.rigamortis.seppuku.impl.mixin.render;

import me.rigamortis.seppuku.Seppuku;
import me.rigamortis.seppuku.api.event.render.EventRenderFluid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BlockFluidRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockFluidRenderer.class)
public abstract class MixinBlockFluidRenderer {
    @Inject(at = @At("HEAD"), method = "renderFluid", cancellable = true)
    public void onRenderFluid(IBlockAccess blockAccess, IBlockState blockStateIn, BlockPos blockPosIn, BufferBuilder bufferBuilderIn, CallbackInfoReturnable<Boolean> cir) {
        EventRenderFluid event = new EventRenderFluid(blockAccess, blockStateIn, blockPosIn, bufferBuilderIn);
        Seppuku.dispatchEvent(event);
        if (event.isCanceled()) {
            cir.cancel();
            cir.setReturnValue(event.isRenderable());
        }
    }
}
