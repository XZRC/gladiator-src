package edu.gamayun.modules;

import me.rigamortis.seppuku.api.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;

public class DonkeyDrop extends Module {

    Minecraft mc = Minecraft.getMinecraft();
    public DonkeyDrop(){
        super("DonkeyDrop", new String[]{}, "Drops the inventory of a donkey", "NONE", -1, ModuleType.PLAYER);
    }

    public void onEnable() {
        super.onEnable();
        runThread();
    }

    public void onDisable() {
        super.onDisable();
    }

    private void runThread(){
        (new Thread(() -> {
            try{
                if (mc.player.getRidingEntity() instanceof AbstractHorse) {
                    mc.getConnection().sendPacket(new CPacketEntityAction(mc.player.getRidingEntity(), CPacketEntityAction.Action.OPEN_INVENTORY));
                    Thread.sleep(175);
                    for (int i = 1; i < 17; ++i) {
                        Thread.sleep(75);
                        final ItemStack itemStack = mc.player.openContainer.getInventory().get(i);
                        if (!itemStack.isEmpty() && itemStack.getItem() != Items.AIR) {
                            mc.playerController.windowClick(mc.player.openContainer.windowId, i, 0, ClickType.PICKUP, mc.player);
                            mc.playerController.windowClick(mc.player.openContainer.windowId, -999, 0, ClickType.PICKUP, mc.player);
                        }
                    }
                }
                toggle();
            } catch (Exception e) {
                e.printStackTrace();
            }
        })
        ).start();
    }
}
