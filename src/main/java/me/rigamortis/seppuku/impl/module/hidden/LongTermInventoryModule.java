package me.rigamortis.seppuku.impl.module.hidden;

import me.rigamortis.seppuku.api.event.EventStageable;
import me.rigamortis.seppuku.api.event.player.EventPlayerUpdate;
import me.rigamortis.seppuku.api.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

import java.util.ArrayDeque;
import java.util.Queue;

public class LongTermInventoryModule extends Module {

    private static Queue<Click> clickQueue = new ArrayDeque<>();

    public LongTermInventoryModule() {
        super("LTI", new String[]{"LTI"}, "Manages the inventory across ticks.", "NONE", -1, ModuleType.HIDDEN);
        this.setHidden(true);
        this.toggle();
    }

    @Listener
    public void onUpdate(EventPlayerUpdate event) {
        final Minecraft mc = Minecraft.getMinecraft();
        if (event.getStage() == EventStageable.EventStage.PRE && Minecraft.getMinecraft().player.ticksExisted % 4 == 0) {
            Click click = clickQueue.poll();
            if (click != null) {
                System.out.println(click);
                mc.playerController.windowClick(click.windowId, click.slotId, click.mouseButton, click.clickType, click.player);
                mc.playerController.updateController();
            }
        }
    }
    public static void moveSlot2Slot(int windowId, int fromSlot, int toSlot) {
        final Minecraft mc = Minecraft.getMinecraft();
        clickQueue.add(new Click(windowId, fromSlot, 0, ClickType.PICKUP, mc.player));
        clickQueue.add(new Click(windowId, toSlot, 0, ClickType.PICKUP, mc.player));
        clickQueue.add(new Click(windowId, fromSlot, 0, ClickType.PICKUP, mc.player));
    }

    public static void swapHotbarWithSlot(int windowId, int hotbarSlot, int slot) {
        final Minecraft mc = Minecraft.getMinecraft();
        clickQueue.add(new Click(windowId, slot, hotbarSlot, ClickType.SWAP, mc.player));
    }
    public static boolean isEmpty() {
        return clickQueue.isEmpty();
    }
    private static class Click {
        int windowId;
        int slotId;
        int mouseButton;
        ClickType clickType;
        EntityPlayer player;

        public Click(int windowId, int slotId, int mouseButton, ClickType clickType, EntityPlayer player) {
            this.windowId = windowId;
            this.slotId = slotId;
            this.mouseButton = mouseButton;
            this.clickType = clickType;
            this.player = player;
        }

        public int getWindowId() {
            return windowId;
        }

        public int getSlotId() {
            return slotId;
        }

        public int getMouseButton() {
            return mouseButton;
        }

        public ClickType getClickType() {
            return clickType;
        }

        public EntityPlayer getPlayer() {
            return player;
        }
    }
}
