package me.rigamortis.seppuku.impl.mixin.world;

import me.rigamortis.seppuku.Seppuku;
import me.rigamortis.seppuku.api.event.world.EventChunk;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Chunk.class)
public class MixinChunk {
    @Inject(method = "onUnload", at = @At("RETURN"))
    private void onUnload(CallbackInfo ci) {
        Seppuku.dispatchEvent(new EventChunk(EventChunk.ChunkType.UNLOAD, (Chunk) (Object) this));
    }
}
