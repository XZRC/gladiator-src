package me.rigamortis.seppuku.impl.mixin.net;

import me.rigamortis.seppuku.Seppuku;
import me.rigamortis.seppuku.api.event.world.EventChunk;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.server.SPacketChunkData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetHandlerPlayClient.class)
public abstract class MixinNetHanderPlayClient {
    @Inject(method = "handleChunkData", at = @At("RETURN"))
    public void onHandleChunkData(SPacketChunkData packetIn, CallbackInfo ci) {
        if (packetIn != null) {
            final EventChunk event = new EventChunk(EventChunk.ChunkType.LOAD, Minecraft.getMinecraft().world.getChunk(packetIn.getChunkX(), packetIn.getChunkZ()));
            Seppuku.dispatchEvent(event);
        }
    }
}
