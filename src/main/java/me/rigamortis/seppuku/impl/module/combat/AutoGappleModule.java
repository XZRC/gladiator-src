package me.rigamortis.seppuku.impl.module.combat;

import me.rigamortis.seppuku.api.event.EventStageable;
import me.rigamortis.seppuku.api.event.player.EventPlayerUpdate;
import me.rigamortis.seppuku.api.module.Module;
import me.rigamortis.seppuku.api.util.InventoryUtil;
import me.rigamortis.seppuku.api.value.Value;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

/**
 * Author Seth
 * 4/30/2019 @ 3:37 AM.
 */
public final class AutoGappleModule extends Module {

    public final Value<Float> health = new Value("Health", new String[]{"Hp"}, "The amount of health needed to acquire a Gapple.", 20.0f, 0.0f, 20.0f, 0.5f);

    public AutoGappleModule() {
        super("AutoGapple", new String[] {"Gapple"}, "Automatically places a Gapple in your offhand", "NONE", -1, ModuleType.COMBAT);
    }

    @Override
    public String getMetaData() {
        return "\u00A7e" + this.getItemCount(Items.GOLDEN_APPLE);
    }

    @Listener
    public void onUpdate(EventPlayerUpdate event) {
        if (event.getStage() == EventStageable.EventStage.PRE) {
            final Minecraft mc = Minecraft.getMinecraft();

            if(mc.currentScreen == null || mc.currentScreen instanceof GuiInventory) {
                if(mc.player.getHealth() <= this.health.getValue()) {
                    final ItemStack offHand = mc.player.getHeldItemOffhand();

                    if (offHand.getItem() == Items.GOLDEN_APPLE) {
                        return;
                    }

                    final int slot = this.getItemSlot(Items.GOLDEN_APPLE);

                    if(slot != -1) {
                        InventoryUtil.refillSlotFromSlot(slot, 45);
                    }
                }
            }
        }
    }

    private int getItemSlot(Item input) {
        for(int i = 0; i < 36; i++) {
            final Item item = Minecraft.getMinecraft().player.inventory.getStackInSlot(i).getItem();
            if(item == input) {
                if (i < 9) {
                    i += 36;
                }
                return i;
            }
        }
        return -1;
    }

    private int getItemCount(Item input) {
        int items = 0;

        for(int i = 0; i < 45; i++) {
            final ItemStack stack = Minecraft.getMinecraft().player.inventory.getStackInSlot(i);
            if(stack.getItem() == input) {
                items += stack.getCount();
            }
        }

        return items;
    }

}
