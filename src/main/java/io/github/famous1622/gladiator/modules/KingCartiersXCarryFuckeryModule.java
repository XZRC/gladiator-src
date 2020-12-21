package io.github.famous1622.gladiator.modules;

import io.github.famous1622.gladiator.KeyBinds;
import me.rigamortis.seppuku.api.event.player.EventPlayerUpdate;
import me.rigamortis.seppuku.api.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.network.play.client.CPacketClickWindow;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

public class KingCartiersXCarryFuckeryModule extends Module {
    public KingCartiersXCarryFuckeryModule() {
        super("IllegalCarry", new String[]{}, "Does some carrying magic on 2b2tPVP or smthng", "NONE", -1, ModuleType.EXPLOIT);
    }

    @Listener
    public void onUpdate(EventPlayerUpdate event) {
        final Minecraft mc = Minecraft.getMinecraft();
        if (mc.currentScreen != null && mc.currentScreen instanceof GuiContainer) {
            GuiContainer container = (GuiContainer) mc.currentScreen;
            Slot slot = container.getSlotUnderMouse();
            if (slot != null) {
                if (KeyBinds.dragToXCarry.isPressed()) {
                    mc.playerController.windowClick(container.inventorySlots.windowId, slot.getSlotIndex(), 0, ClickType.PICKUP, mc.player);
                    mc.playerController.windowClick(container.inventorySlots.windowId, -999, 0, ClickType.QUICK_CRAFT, mc.player);
                    mc.playerController.windowClick(0, 1, 1, ClickType.QUICK_CRAFT, mc.player);
                    mc.playerController.windowClick(container.inventorySlots.windowId, -999, 2, ClickType.QUICK_CRAFT, mc.player);
                    //     public CPacketClickWindow(int windowIdIn, int slotIdIn, int usedButtonIn, ClickType modeIn, ItemStack clickedItemIn, short actionNumberIn)
                } else if (KeyBinds.dragFromXCarry.isPressed()) {
                    mc.playerController.windowClick(0, 1, 0, ClickType.PICKUP, mc.player);
                    mc.playerController.windowClick(0, -999, 0, ClickType.QUICK_CRAFT, mc.player);
                    mc.playerController.windowClick(container.inventorySlots.windowId, slot.getSlotIndex(), 1, ClickType.QUICK_CRAFT, mc.player);
                    mc.playerController.windowClick(0, -999, 2, ClickType.QUICK_CRAFT, mc.player);
                    //     public CPacketClickWindow(int windowIdIn, int slotIdIn, int usedButtonIn, ClickType modeIn, ItemStack clickedItemIn, short actionNumberIn)
                }
            }
        }
    }
}
