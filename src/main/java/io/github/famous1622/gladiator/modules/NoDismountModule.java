package io.github.famous1622.gladiator.modules;

import me.rigamortis.seppuku.api.event.EventStageable;
import me.rigamortis.seppuku.api.event.network.EventSendPacket;
import me.rigamortis.seppuku.api.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

public final class NoDismountModule extends Module {
    public NoDismountModule() {
        super("NoDismount", new String[]{"AntiDismount"}, "Prevents jj's plugin from dismounting you while riding", "NONE", -1, Module.ModuleType.PLAYER);
    }

    @Listener
    public void sendPacket(final EventSendPacket event) {
        if (event.getStage() == EventStageable.EventStage.PRE) {
            if (event.getPacket() instanceof CPacketPlayer.Position) {
                event.setCanceled(true);
                final CPacketPlayer.Position packet = (CPacketPlayer.Position) event.getPacket();
                Minecraft.getMinecraft().player.connection.sendPacket((Packet) new CPacketPlayer.PositionRotation(packet.x, packet.y, packet.z, packet.yaw, packet.pitch, packet.onGround));
            }
            if (event.getPacket() instanceof CPacketPlayer && !(event.getPacket() instanceof CPacketPlayer.PositionRotation)) {
                event.setCanceled(true);
            }
        }
    }
}
