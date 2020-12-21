package io.github.famous1622.gladiator.modules;

import me.rigamortis.seppuku.api.event.EventStageable;
import me.rigamortis.seppuku.api.event.network.EventReceivePacket;
import me.rigamortis.seppuku.api.event.player.EventPlayerUpdate;
import me.rigamortis.seppuku.api.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketEntityAttach;
import net.minecraft.network.play.server.SPacketEntityStatus;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

import java.util.Objects;

public class AutoRunModule extends Module {
    public AutoRunModule() {
        super("AutoRun", new String[]{}, "AntiVatican", "NONE", -1, ModuleType.COMBAT);
    }
    @Listener
    public void onUpdate(EventPlayerUpdate event) {
        final Minecraft mc = Minecraft.getMinecraft();
        if (event.getStage() == EventStageable.EventStage.PRE) {
            for (Entity entity : mc.world.loadedEntityList) {
                if (entity instanceof EntityEnderCrystal) {
                    EntityEnderCrystal crystal = (EntityEnderCrystal) entity;
                    if (crystal.getDistance(mc.player) < 4) {
                        warpToTheNetherRoof();
                    }
                }
            }
        }
    }

    private void warpToTheNetherRoof() {
        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX,10000, mc.player.posZ, mc.player.onGround));
        this.toggle();
    }

    @Listener
    public void onRecieve(EventReceivePacket event) {
        if (event.getStage() == EventStageable.EventStage.PRE) {
            if (event.getPacket() instanceof SPacketEntityStatus) {
                SPacketEntityStatus packet = (SPacketEntityStatus) event.getPacket();
                if (packet.getOpCode() == 35) {
                    if (Objects.equals(packet.getEntity(mc.world), mc.player)) {
                        warpToTheNetherRoof();
                    }
                }
            }
        }
    }
}
