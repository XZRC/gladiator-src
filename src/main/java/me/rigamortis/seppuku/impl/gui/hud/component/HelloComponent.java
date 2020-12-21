package me.rigamortis.seppuku.impl.gui.hud.component;

import me.rigamortis.seppuku.Seppuku;
import me.rigamortis.seppuku.api.gui.hud.component.DraggableHudComponent;
import net.minecraft.client.Minecraft;

/**
 * created by noil on 10/6/2019 at 8:53 PM
 */
public final class HelloComponent extends DraggableHudComponent {

    public HelloComponent() {
        // this will be the recognized name of our component
        super("Hello");
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);

        // store the minecraft instance in a local variable
        final Minecraft mc = Minecraft.getMinecraft();

        // this will be the string we display on screen
        final String helloString = String.format("\u00A78Hello \u00A7f%s :)", mc.getSession().getUsername());
        final int stringWidth = Seppuku.INSTANCE.getFontManager().getStringWidth(helloString);

        // draw the string
        Seppuku.INSTANCE.getFontManager().drawString(helloString, this.getX(), this.getY(), 0xFFFFFFFF, true, false);

        this.setW(stringWidth); // set the width of the component to the width of the displayed string
        this.setH(mc.fontRenderer.FONT_HEIGHT); // same with the component's height, set it to the string height
    }
}
