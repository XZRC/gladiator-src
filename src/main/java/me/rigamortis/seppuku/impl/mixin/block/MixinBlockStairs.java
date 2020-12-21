package me.rigamortis.seppuku.impl.mixin.block;

import me.rigamortis.seppuku.Seppuku;
import me.rigamortis.seppuku.api.event.world.EventAddCollisionBox;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(BlockStairs.class)
public class MixinBlockStairs {
    @Inject(at = @At("HEAD"), method = "addCollisionBoxToList", cancellable = true)
    public void onAddCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, Entity entityIn, boolean isActualState, CallbackInfo ci) {
        final EventAddCollisionBox event = new EventAddCollisionBox(pos, entityIn);
        Seppuku.dispatchEvent(event);
        if (event.isCanceled()) {
            ci.cancel();
        }
    }
}
