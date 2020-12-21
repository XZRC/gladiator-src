package org.drink.piss.Hud;

import me.rigamortis.seppuku.Seppuku;
import me.rigamortis.seppuku.api.gui.hud.component.DraggableHudComponent;
import net.minecraft.client.Minecraft;

public class DurabilityComponent extends DraggableHudComponent {

    public DurabilityComponent() {
        super("Durability");
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);
        final String durability = "Durability: " + (Minecraft.getMinecraft().player.getHeldItemMainhand().getMaxDamage() - Minecraft.getMinecraft().player.getHeldItemMainhand().getItemDamage());

        this.setW(Seppuku.INSTANCE.getFontManager().getStringWidth(durability));
        this.setH(Seppuku.INSTANCE.getFontManager().getHeight());
        Seppuku.INSTANCE.getFontManager().drawString(durability, this.getX(), this.getY(), -1, true, false);
    }

}
