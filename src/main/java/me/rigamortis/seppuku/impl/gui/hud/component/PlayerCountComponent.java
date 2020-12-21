package me.rigamortis.seppuku.impl.gui.hud.component;

import me.rigamortis.seppuku.Seppuku;
import me.rigamortis.seppuku.api.gui.hud.component.DraggableHudComponent;
import net.minecraft.client.Minecraft;

/**
 * Author Seth
 * 12/5/2019 @ 3:00 PM.
 */
public final class PlayerCountComponent extends DraggableHudComponent {

    public PlayerCountComponent() {
        super("PlayerCount");
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);
        final String playerCount = "\u00A78ONLINE:\u00A7f " + Minecraft.getMinecraft().player.connection.getPlayerInfoMap().size();

        this.setW(Seppuku.INSTANCE.getFontManager().getStringWidth(playerCount));
        this.setH(Seppuku.INSTANCE.getFontManager().getHeight());

        Seppuku.INSTANCE.getFontManager().drawString(playerCount, this.getX(), this.getY(), -1, true, false);
    }

}
