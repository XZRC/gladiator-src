package me.rigamortis.seppuku.impl.gui.hud.component;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.rigamortis.seppuku.Seppuku;
import me.rigamortis.seppuku.api.gui.hud.component.DraggableHudComponent;
import net.minecraft.client.Minecraft;

import java.text.DecimalFormat;

/**
 * Author Seth
 * 7/27/2019 @ 7:44 PM.
 */
public final class CoordsComponent extends DraggableHudComponent {

    public CoordsComponent() {
        super("Coords");
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);
        final DecimalFormat df = new DecimalFormat("#.#");

        final String coords = ChatFormatting.DARK_GRAY + "x " + ChatFormatting.RESET +
                df.format(Minecraft.getMinecraft().player.posX) + ChatFormatting.RESET + "," +
                ChatFormatting.DARK_GRAY + "\u00A78 y " + ChatFormatting.RESET + df.format(Minecraft.getMinecraft().player.posY) + ChatFormatting.RESET + "\u00A78," +
                ChatFormatting.DARK_GRAY + "\u00A78 z " + ChatFormatting.RESET + df.format(Minecraft.getMinecraft().player.posZ) + ChatFormatting.RESET;

        this.setW(Seppuku.INSTANCE.getFontManager().getStringWidth(coords));
        this.setH(Seppuku.INSTANCE.getFontManager().getHeight());

        //RenderUtil.drawRect(this.getX(), this.getY(), this.getX() + this.getW(), this.getY() + this.getH(), 0xAA202020);
        Seppuku.INSTANCE.getFontManager().drawString(coords, this.getX(), this.getY(), -1, true, false);
    }

}