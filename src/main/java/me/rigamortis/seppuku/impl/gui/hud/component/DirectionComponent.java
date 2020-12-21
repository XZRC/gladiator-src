package me.rigamortis.seppuku.impl.gui.hud.component;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.rigamortis.seppuku.Seppuku;
import me.rigamortis.seppuku.api.gui.hud.component.DraggableHudComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;

/**
 * Author Seth
 * 8/7/2019 @ 12:54 PM.
 */
public final class DirectionComponent extends DraggableHudComponent {

    public DirectionComponent() {
        super("Direction");
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);

        final Minecraft mc = Minecraft.getMinecraft();
        final String direction = String.format("\u00A78%s" + " " + ChatFormatting.GRAY + "\u00A78%s", this.getFacing(), this.getTowards());
        this.setW(Seppuku.INSTANCE.getFontManager().getStringWidth(direction));
        this.setH(Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT);

        Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(direction, this.getX(), this.getY(), -1);
    }

    private String getFacing() {
        switch (MathHelper.floor((double) (Minecraft.getMinecraft().player.rotationYaw * 8.0F / 360.0F) + 0.5D) & 7) {
            case 0:
                return "\u00A78South";
            case 1:
                return "\u00A78South West";
            case 2:
                return "\u00A78West";
            case 3:
                return "\u00A78North West";
            case 4:
                return "\u00A78North";
            case 5:
                return "\u00A78North East";
            case 6:
                return "\u00A78East";
            case 7:
                return "\u00A78South East";
        }
        return "Invalid";
    }

    private String getTowards() {
        switch (MathHelper.floor((double) (Minecraft.getMinecraft().player.rotationYaw * 8.0F / 360.0F) + 0.5D) & 7) {
            case 0:
                return "\u00A78+Z";
            case 1:
                return "\u00A78-X +Z";
            case 2:
                return "\u00A78-X";
            case 3:
                return "\u00A78-X -Z";
            case 4:
                return "\u00A78-Z";
            case 5:
                return "\u00A78+X -Z";
            case 6:
                return "\u00A78+X";
            case 7:
                return "\u00A78+X +Z";
        }
        return "Invalid";
    }
}