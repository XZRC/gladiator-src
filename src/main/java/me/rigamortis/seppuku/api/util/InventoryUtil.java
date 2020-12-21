package me.rigamortis.seppuku.api.util;

import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

/* Created by famous1622, 2019-11-17*/
public class InventoryUtil {

    private static final Minecraft mc = Minecraft.getMinecraft();

    private static boolean moving = false;
    private static boolean returnI = false;

    public static int findStackInventory(Item input) {
        return findStackInventory(input, false);
    }

    public static int findStackInventory(Item input, boolean withHotbar) {
        for (int i = withHotbar ? 0 : 9; i < 36; i++) {
            final Item item = Minecraft.getMinecraft().player.inventory.getStackInSlot(i).getItem();
            if (Item.getIdFromItem(input) == Item.getIdFromItem(item)) {
                return i + (i < 9 ? 36 : 0);
            }
        }
        return -1;
    }


    public static int getItemHotbar(Item input) {
        for (int i = 0; i < 9; i++) {
            final Item item = Minecraft.getMinecraft().player.inventory.getStackInSlot(i).getItem();
            if (Item.getIdFromItem(item) == Item.getIdFromItem(input)) {
                return i;
            }
        }
        return -1;
    }

    public static int findEmptyHotbar() {
        for (int i = 0; i < 9; i++) {
            final ItemStack stack = Minecraft.getMinecraft().player.inventory.getStackInSlot(i);

            if (stack.getItem() == Items.AIR) {
                return i;
            }
        }
        return -1;
    }

    public static void refillSlotFromSlot(int fromSlot, int toSlot) {
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, fromSlot, 0, ClickType.PICKUP, mc.player);
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, toSlot, 0, ClickType.PICKUP, mc.player);
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, fromSlot, 0, ClickType.PICKUP, mc.player);
        mc.playerController.updateController();
    }

    public static void OffhandCrystal() {
        int crystals;
        int t;
        if (returnI) {
            crystals = -1;

            for (t = 0; t < 45; ++t) {
                if (mc.player.inventory.getStackInSlot(t).isEmpty()) {
                    crystals = t;
                    break;
                }
            }

            if (crystals == -1) {
                return;
            }

            mc.playerController.windowClick(0, crystals < 9 ? crystals + 36 : crystals, 0, ClickType.PICKUP, mc.player);
            returnI = false;
        }

        crystals = mc.player.inventory.mainInventory.stream().filter((itemStack) -> {
            return itemStack.getItem() == Items.END_CRYSTAL;
        }).mapToInt(ItemStack::getCount).sum();
        if (mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) {
            ++crystals;
        }

        if (moving) {
            mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, mc.player);
            moving = false;
            if (!mc.player.inventory.getItemStack().isEmpty()) {
                returnI = true;
            }

            return;
        }

        int i;
        if (mc.player.inventory.getItemStack().isEmpty()) {
            if (crystals == 0) {
                return;
            }

            t = -1;

            for (i = 0; i < 45; ++i) {
                if (mc.player.inventory.getStackInSlot(i).getItem() == Items.END_CRYSTAL) {
                    t = i;
                    break;
                }
            }

            if (t == -1) {
                return;
            }

            mc.playerController.windowClick(0, t < 9 ? t + 36 : t, 0, ClickType.PICKUP, mc.player);
            moving = true;
        } else {
            t = -1;

            for (i = 0; i < 45; ++i) {
                if (mc.player.inventory.getStackInSlot(i).isEmpty()) {
                    t = i;
                    break;
                }
            }

            if (t == -1) {
                return;
            }

            mc.playerController.windowClick(0, t < 9 ? t + 36 : t, 0, ClickType.PICKUP, mc.player);
        }
    }




    public static void OffhandCrystalReset() {
        int crystals = mc.player.inventory.mainInventory.stream().filter((itemStack) -> {
            return itemStack.getItem() == Items.TOTEM_OF_UNDYING;
        }).mapToInt(ItemStack::getCount).sum();
        if (mc.player.getHeldItemOffhand().getItem() != Items.TOTEM_OF_UNDYING) {
            if (crystals == 0) {
                return;
            }

            int t = -1;

            for(int i = 0; i < 45; ++i) {
                if (mc.player.inventory.getStackInSlot(i).getItem() == Items.TOTEM_OF_UNDYING) {
                    t = i;
                    break;
                }
            }

            if (t == -1) {
                return;
            }

            mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, mc.player);
            mc.playerController.windowClick(0, t < 9 ? t + 36 : t, 0, ClickType.PICKUP, mc.player);
            mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, mc.player);
        }

    }

}
