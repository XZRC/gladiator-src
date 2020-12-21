package me.rigamortis.seppuku.impl.gui.hud.component;

import me.rigamortis.seppuku.Seppuku;
import me.rigamortis.seppuku.api.gui.hud.component.DraggableHudComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

/**
 * Author: Memeszz
 * 2019-10-21
 */
public final class GappleCountComponent extends DraggableHudComponent {

    public GappleCountComponent() {
        super("GapCount");
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);
        // RenderUtil.drawRect(this.getX(), this.getY(), this.getX() + this.getW(), this.getY() + this.getH(), 0x90222222);
        final Minecraft mc = Minecraft.getMinecraft();
        final String GAPCount = "\u00A78GAPS = \u00A7f" + Integer.toString(getGAPCount());

        this.setW(Seppuku.INSTANCE.getFontManager().getStringWidth(GAPCount));
        this.setH(Seppuku.INSTANCE.getFontManager().getHeight());
        Seppuku.INSTANCE.getFontManager().drawString(GAPCount, this.getX(), this.getY(), -1, true, false);
    }

        private int getGAPCount() {
            int GAP = 0;
            for(int i = 0; i < 45; i++) {
                final ItemStack stack = Minecraft.getMinecraft().player.inventory.getStackInSlot(i);
                if(stack.getItem() == Items.GOLDEN_APPLE){
                    GAP += stack.getCount();
                }
            }
            return GAP;
        }
    }