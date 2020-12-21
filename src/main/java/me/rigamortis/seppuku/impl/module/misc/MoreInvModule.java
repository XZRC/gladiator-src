package me.rigamortis.seppuku.impl.module.misc;

import me.rigamortis.seppuku.api.event.EventStageable;
import me.rigamortis.seppuku.api.event.network.EventReceivePacket;
import me.rigamortis.seppuku.api.event.network.EventSendPacket;
import me.rigamortis.seppuku.api.event.player.EventPlayerUpdate;
import me.rigamortis.seppuku.api.module.Module;
import me.rigamortis.seppuku.api.value.Value;
import me.rigamortis.seppuku.impl.module.hidden.LongTermInventoryModule;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketCloseWindow;
import net.minecraft.network.play.server.SPacketCollectItem;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

/**
 * Author Seth
 * 4/16/2019 @ 6:19 PM.
 */
public final class MoreInvModule extends Module {

    public final Value<Mode> mode = new Value<Mode>("Mode", new String[]{"Mode", "M"}, "Choose which mode MoreInv should run in.", Mode.NORMAL);
    private int xcarryingslot = -1;
    private boolean waitForLTIEmpty;
    private boolean collectedItem;

    private enum Mode {
        NORMAL, AUTO
    }

    public MoreInvModule() {
        super("MoreInv", new String[]{"XCarry", "MoreInventory"}, "Allows you to carry items in your crafting and dragging slot", "NONE", -1, ModuleType.MISC);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (Minecraft.getMinecraft().world != null) {
            Minecraft.getMinecraft().player.connection.sendPacket(new CPacketCloseWindow(Minecraft.getMinecraft().player.inventoryContainer.windowId));
        }
    }

    @Listener
    public void sendPacket(EventSendPacket event) {
        if (event.getStage() == EventStageable.EventStage.PRE) {
            if (event.getPacket() instanceof CPacketCloseWindow) {
                final CPacketCloseWindow packet = (CPacketCloseWindow) event.getPacket();
                if (packet.windowId == Minecraft.getMinecraft().player.inventoryContainer.windowId) {
                    System.out.println("Event canceled.");
                    event.setCanceled(true);
                }
            }
        }
    }

    @Listener
    public void receivePacket(EventReceivePacket event) {
        if (event.getStage() == EventStageable.EventStage.PRE && mode.getValue() == Mode.AUTO) {
            if (event.getPacket() instanceof SPacketCollectItem) {
                collectedItem = true;
            }
        }
    }

    @Listener
    public void onPlayerUpdate(EventPlayerUpdate event) {
        final Minecraft mc = Minecraft.getMinecraft();
        if (event.getStage() == EventStageable.EventStage.PRE) {
            if (mode.getValue() == Mode.AUTO) {
                int emptyslots = 0;
                for (ItemStack stack : mc.player.inventory.mainInventory) if (stack.isEmpty()) emptyslots++;
                int fxcarryslot = getFirstXcarrySlot();
                if (waitForLTIEmpty) {
                    waitForLTIEmpty = !LongTermInventoryModule.isEmpty();
                } else if (fxcarryslot != -1 && emptyslots > 1 && getEmptyNonHotbar() != -1) {
                    LongTermInventoryModule.moveSlot2Slot(mc.player.inventoryContainer.windowId, fxcarryslot, getEmptyNonHotbar());
                    waitForLTIEmpty = true;
                } else if (emptyslots == 0 && xcarryingslot != -1) {
                    final Slot xcarryslot = mc.player.inventoryContainer.inventorySlots.get(xcarryingslot);
                    ItemStack stack = xcarryslot.getStack();

                    for (int i = 1; i <= 4; i++) {
                        Slot craftingSlot = mc.player.inventoryContainer.inventorySlots.get(i);
                        ItemStack craftingStack = craftingSlot.getStack();
                        if (doesStackFitInStack(stack, craftingStack)) {
                            if (moveSlot2Slot(xcarryslot, craftingSlot, i) <= 0) {
                                break;
                            }
                        }
                    }
                    waitForLTIEmpty = true;
                }

                xcarryingslot = getEmptyNonHotbar();


            }
        }
    }

    private int getEmptyNonHotbar() {
        final Minecraft mc = Minecraft.getMinecraft();
        for (int i = 10; i < mc.player.inventory.mainInventory.size(); ++i) {
            if (mc.player.inventory.mainInventory.get(i).isEmpty()) {
                return i;
            }
        }
        return -1;
    }

    private int getFirstXcarrySlot() {
        final Minecraft mc = Minecraft.getMinecraft();
        for (int i = 1; i <= 4; i++) {
            Slot craftingSlot = mc.player.inventoryContainer.inventorySlots.get(i);
            ItemStack craftingStack = craftingSlot.getStack();
            if (!craftingStack.isEmpty()) {
                return i;
            }
        }
        return -1;
    }

    private boolean doesStackFitInStack(ItemStack stack, ItemStack into) {
        if (into.isEmpty()) return true;
        else if (into.getItem() != stack.getItem()) return false;
        else if (into.getItemDamage() != stack.getItemDamage()) return false;
        else if (!into.isStackable()) return false;
        else return into.getCount() < into.getMaxStackSize();
    }

    private int moveSlot2Slot(Slot src, Slot dest, int destnum) {
        System.out.println("Moving slot " + src + " 2 slot " + dest);
        final Minecraft mc = Minecraft.getMinecraft();
        int ret = Math.max((src.getStack().getCount() + dest.getStack().getCount()) - dest.getStack().getMaxStackSize(), 0);
        LongTermInventoryModule.moveSlot2Slot(mc.player.inventoryContainer.windowId, src.getSlotIndex(), destnum);
        return ret;
    }


}
