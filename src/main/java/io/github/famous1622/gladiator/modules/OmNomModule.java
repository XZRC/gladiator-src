package io.github.famous1622.gladiator.modules;

import me.rigamortis.seppuku.api.event.EventStageable;
import me.rigamortis.seppuku.api.event.network.EventSendPacket;
import me.rigamortis.seppuku.api.event.player.EventPlayerUpdate;
import me.rigamortis.seppuku.api.module.Module;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

import java.util.ArrayList;
import java.util.List;

public class OmNomModule extends Module {
    public OmNomModule() {
        super("ForceFeed", new String[]{"FastEat"}, "NONE", -1, ModuleType.EXPLOIT);
    }

    private List<Packet> packets = new ArrayList<>();
    @Listener
    public void onUpdate(EventPlayerUpdate event) throws InterruptedException {
        if (event.getStage() == EventStageable.EventStage.PRE) {
            if (mc.player.isHandActive() && mc.player.getHeldItemMainhand().getItem() instanceof ItemFood) {
                mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
            }
        }
    }

    @Listener
    public void onPacket(EventSendPacket event) throws InterruptedException {
        if (event.getStage() == EventStageable.EventStage.PRE) {
            if (event.getPacket() instanceof CPacketPlayerTryUseItem) {
                CPacketPlayerTryUseItem packet = (CPacketPlayerTryUseItem) event.getPacket();
                ItemStack heldItem = mc.player.getHeldItem(packet.getHand());
                if (heldItem.getItem() instanceof ItemFood) {
                    mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                }
            }
        }
    }
}
