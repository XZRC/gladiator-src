package me.rigamortis.seppuku.impl.mixin.render;

import me.rigamortis.seppuku.Seppuku;
import me.rigamortis.seppuku.api.event.world.EventSetOpaqueCube;
import net.minecraft.client.renderer.chunk.VisGraph;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VisGraph.class)
public class MixinVisGraph {
    @Inject(method = "setOpaqueCube", at = @At("HEAD"), cancellable = true)
    private void onSetOpaqueCube(BlockPos pos, CallbackInfo ci) {
        final EventSetOpaqueCube event = new EventSetOpaqueCube();
        Seppuku.dispatchEvent(event);
        if (event.isCanceled()) ci.cancel();
    }
}
