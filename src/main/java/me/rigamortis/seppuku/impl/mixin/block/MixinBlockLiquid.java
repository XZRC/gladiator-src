package me.rigamortis.seppuku.impl.mixin.block;

import me.rigamortis.seppuku.Seppuku;
import me.rigamortis.seppuku.api.event.render.EventRenderBlockSide;
import me.rigamortis.seppuku.api.event.world.EventCanCollide;
import me.rigamortis.seppuku.api.event.world.EventLiquidCollisionBB;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockLiquid.class)
public abstract class MixinBlockLiquid extends Block {
    // To make the compiler happy
    public MixinBlockLiquid(Material materialIn) {
        super(materialIn);
    }

    @Inject(at = @At("HEAD"), method = "getCollisionBoundingBox", cancellable = true)
    public void onGetCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos, CallbackInfoReturnable<AxisAlignedBB> cir) {
        final EventLiquidCollisionBB event = new EventLiquidCollisionBB(pos);
        Seppuku.dispatchEvent(event);
        if (event.isCanceled()) {
            cir.cancel();
            cir.setReturnValue(event.getBoundingBox());
        }
    }

    @Inject(at = @At("HEAD"), method = "shouldSideBeRendered", cancellable = true)
    private void onShouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side, CallbackInfoReturnable<Boolean> cir) {
        final EventRenderBlockSide event = new EventRenderBlockSide(this);
        Seppuku.dispatchEvent(event);
        if (event.isCanceled()) {
            cir.cancel();
            cir.setReturnValue(event.isRenderable());
        }
    }

    @Inject(at = @At("HEAD"), method = "canCollideCheck", cancellable = true)
    private void onCanCollideCheck(IBlockState state, boolean hitIfLiquid, CallbackInfoReturnable<Boolean> cir) {
        final EventCanCollide event = new EventCanCollide();
        Seppuku.dispatchEvent(event);
        if (event.isCanceled()) {
            cir.cancel();
            cir.setReturnValue(true);
        }
    }


}
