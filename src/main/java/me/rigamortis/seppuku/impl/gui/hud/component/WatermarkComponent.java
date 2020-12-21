package me.rigamortis.seppuku.impl.gui.hud.component;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.rigamortis.seppuku.Seppuku;
import me.rigamortis.seppuku.api.gui.hud.component.DraggableHudComponent;
import me.rigamortis.seppuku.impl.fml.SeppukuMod;
import net.minecraft.client.Minecraft;

/**
 * Author Seth
 * 7/25/2019 @ 4:55 AM.
 */
public final class WatermarkComponent extends DraggableHudComponent {

    private final String WATERMARK = ChatFormatting.ITALIC + "\u00A78Gladiator " + ChatFormatting.GRAY + "\u00A7f" + SeppukuMod.VERSION;

    public WatermarkComponent() {
        super("Watermark");
        this.setW((Seppuku.INSTANCE.getFontManager().getStringWidth(WATERMARK)));
        this.setH(Seppuku.INSTANCE.getFontManager().getHeight());
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);
        //RenderUtil.drawRect(this.getX(), this.getY(), this.getX() + this.getW(), this.getY() + this.getH(), 0x90222222);
        Seppuku.INSTANCE.getFontManager().drawString(WATERMARK, this.getX(), this.getY(), -1, true, false);
    }

}
