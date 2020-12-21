package me.rigamortis.seppuku.impl.mixin.block;

import me.rigamortis.seppuku.Seppuku;
import me.rigamortis.seppuku.api.event.world.EventCollideSoulSand;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSoulSand;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockSoulSand.class)
public abstract class MixinBlockSoulSand extends Block {

    // Compiler bait
    public MixinBlockSoulSand(Material blockMaterialIn, MapColor blockMapColorIn) {
        super(blockMaterialIn, blockMapColorIn);
    }

    @Inject(at = @At("HEAD"), method = "onEntityCollision", cancellable = true)
    private void onEntityWalk(World worldIn, BlockPos pos, IBlockState state, Entity entityIn, CallbackInfo ci) {
        final EventCollideSoulSand event = new EventCollideSoulSand();
        Seppuku.dispatchEvent(event);
        if (event.isCanceled()) ci.cancel();
    }
}
