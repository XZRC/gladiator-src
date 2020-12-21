package io.github.famous1622.gladiator.modules;

import me.rigamortis.seppuku.api.event.EventStageable;
import me.rigamortis.seppuku.api.event.network.EventReceivePacket;
import me.rigamortis.seppuku.api.event.network.EventSendPacket;
import me.rigamortis.seppuku.api.event.player.EventPlayerUpdate;
import me.rigamortis.seppuku.api.module.Module;
import me.rigamortis.seppuku.api.util.InventoryUtil;
import me.rigamortis.seppuku.api.value.Value;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.network.play.server.SPacketWindowItems;
import net.minecraft.util.math.RayTraceResult;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

public class ElytraNoFallModule extends Module {
    public final Value<Boolean> glide = new Value<Boolean>("Glide", new String[]{"glide"}, "Glide with ElytraNoFall in Overworld",false);
    public final Value<Boolean> silent = new Value<Boolean>("Silent", new String[]{"equip", "requip"},"Equip silently, server side only.", true);
    private final Minecraft mc = Minecraft.getMinecraft();

    private boolean equipped = false;
    private boolean gotElytra = false;
    private int ogslot = -1;

    public ElytraNoFallModule() {
        super("AntiFallDMG", new String[]{"ENF"}, "NONE", -1, Module.ModuleType.MOVEMENT);
    }

    @Listener
    public void onSend(EventSendPacket e) {
        RayTraceResult result = this.mc.world.rayTraceBlocks(this.mc.player.getPositionVector(), this.mc.player.getPositionVector().add(0.0, -3.0, 0.0), true, true, false);
        if (!equipped && e.getStage() == EventStageable.EventStage.PRE && e.getPacket() instanceof CPacketPlayer && this.mc.player.fallDistance >= 3.0f && (glide.getValue() || (result != null && result.typeOfHit == RayTraceResult.Type.BLOCK))) {

            if (mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem().equals(Items.ELYTRA)) {
                this.mc.player.connection.sendPacket(new CPacketEntityAction(this.mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
            } else if (silent.getValue()) {
                int slot = InventoryUtil.getItemHotbar(Items.ELYTRA);
                if (slot != -1) {
                    mc.playerController.windowClick(mc.player.inventoryContainer.windowId, 6, slot, ClickType.SWAP, mc.player);
                    this.mc.player.connection.sendPacket(new CPacketEntityAction(this.mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
                }
                ogslot = slot;
                equipped = true;
            }
        }
    }

    @Listener
    public void onUpdate(EventPlayerUpdate e) {

        if (e.getStage() == EventStageable.EventStage.PRE) {
            if (silent.getValue() && equipped && gotElytra) {
                mc.playerController.windowClick(mc.player.inventoryContainer.windowId, 6, ogslot, ClickType.SWAP, mc.player);
                mc.playerController.updateController();
                equipped = false;
                gotElytra = false;
            } else if (silent.getValue() && InventoryUtil.getItemHotbar(Items.ELYTRA) == -1) {
                int slot = InventoryUtil.findStackInventory(Items.ELYTRA);
                if (slot != -1 && ogslot != -1){
                    System.out.println(String.format("Moving %d to hotbar %d", slot, ogslot));
                    mc.playerController.windowClick(mc.player.inventoryContainer.windowId, slot, ogslot, ClickType.SWAP, mc.player);
                    mc.playerController.updateController();
                }
            }
        }
    }

    @Listener
    public void onReceive(EventReceivePacket e) {
        if (equipped && e.getStage() == EventStageable.EventStage.POST && (e.getPacket() instanceof SPacketWindowItems || e.getPacket() instanceof SPacketSetSlot)) {
            gotElytra = true;
        }
    }
}

