package io.github.famous1622.gladiator.modules;

import me.rigamortis.seppuku.api.event.EventStageable;
import me.rigamortis.seppuku.api.event.player.EventPlayerUpdate;
import me.rigamortis.seppuku.api.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

public class AntiVaticanModule extends Module {
    public AntiVaticanModule() {
        super("AntiVatican", new String[]{}, "AntiVatican", "NONE", -1, ModuleType.COMBAT);
    }
    @Listener
    public void onUpdate(EventPlayerUpdate event) {
        final Minecraft mc = Minecraft.getMinecraft();
        if (event.getStage() == EventStageable.EventStage.PRE) {
            for (Entity entity : mc.world.loadedEntityList) {
                if (entity instanceof EntityOtherPlayerMP) {
                    EntityOtherPlayerMP player = (EntityOtherPlayerMP) entity;
                    if (player.getDistance(mc.player) < 6 && (player.getHeldItemMainhand().getItem() == Items.BONE || player.getHeldItemOffhand().getItem() == Items.BONE)) {
                        Minecraft.getMinecraft().player.connection.sendPacket(new CPacketHeldItemChange(420));
                        this.toggle();
                    }
                }
            }
        }
    }
}
