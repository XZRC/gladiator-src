package me.rigamortis.seppuku.impl.mixin.block;

import me.rigamortis.seppuku.Seppuku;
import me.rigamortis.seppuku.api.event.world.EventLandOnSlime;
import me.rigamortis.seppuku.api.event.world.EventWalkOnSlime;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlime;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockSlime.class)
public abstract class MixinBlockSlime extends Block {

    // Compiler bait
    public MixinBlockSlime(Material blockMaterialIn, MapColor blockMapColorIn) {
        super(blockMaterialIn, blockMapColorIn);
    }

    @Inject(at = @At("HEAD"), method = "onEntityWalk", cancellable = true)
    private void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn, CallbackInfo ci) {
        final EventWalkOnSlime event = new EventWalkOnSlime();
        Seppuku.dispatchEvent(event);
        if (event.isCanceled()) ci.cancel();
    }

    @Inject(at = @At("HEAD"), method = "onLanded", cancellable = true)
    private void onEntityWalk(World worldIn, Entity entityIn, CallbackInfo ci) {
        final EventLandOnSlime event = new EventLandOnSlime();
        Seppuku.dispatchEvent(event);
        if (event.isCanceled()) ci.cancel();
    }
}
