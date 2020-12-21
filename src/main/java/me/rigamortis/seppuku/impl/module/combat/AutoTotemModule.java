package me.rigamortis.seppuku.impl.module.combat;

import java.awt.Color;
import me.rigamortis.seppuku.api.event.EventStageable.EventStage;
import me.rigamortis.seppuku.api.event.player.EventUpdateWalkingPlayer;
import me.rigamortis.seppuku.api.module.Module;
import me.rigamortis.seppuku.api.module.Module.ModuleType;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

public class AutoTotemModule extends Module {
    public AutoTotemModule() {
        super("BetterTotem", new String[]{"bt", "btotem", "bettert"}, "oHare's Better Auto Totem", "none", (new Color(255, 178, 231)).getRGB(), ModuleType.COMBAT);
    }

    @Listener
    public void onUpdate(EventUpdateWalkingPlayer eventUpdateWalkingPlayer) {
        if (eventUpdateWalkingPlayer.getStage() == EventStage.PRE) {
            for(int i = 0; i < Minecraft.getMinecraft().player.inventory.mainInventory.size(); ++i) {
                if (Minecraft.getMinecraft().player.inventory.mainInventory.get(i) != ItemStack.EMPTY && ((ItemStack)Minecraft.getMinecraft().player.inventory.mainInventory.get(i)).getItem() == Items.TOTEM_OF_UNDYING && Minecraft.getMinecraft().player.getItemStackFromSlot(EntityEquipmentSlot.OFFHAND).getItem() != Items.TOTEM_OF_UNDYING) {
                    this.replaceTotem(i);
                    break;
                }
            }
        }

    }

    private void replaceTotem(int inventoryIndex) {
        if (Minecraft.getMinecraft().player.openContainer instanceof ContainerPlayer) {
            Minecraft.getMinecraft().playerController.windowClick(0, inventoryIndex < 9 ? inventoryIndex + 36 : inventoryIndex, 0, ClickType.PICKUP, Minecraft.getMinecraft().player);
            Minecraft.getMinecraft().playerController.windowClick(0, 45, 0, ClickType.PICKUP, Minecraft.getMinecraft().player);
            Minecraft.getMinecraft().playerController.windowClick(0, inventoryIndex < 9 ? inventoryIndex + 36 : inventoryIndex, 0, ClickType.PICKUP, Minecraft.getMinecraft().player);
        }

    }
}
