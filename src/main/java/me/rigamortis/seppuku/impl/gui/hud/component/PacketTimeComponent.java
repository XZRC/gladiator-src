package me.rigamortis.seppuku.impl.gui.hud.component;

import me.rigamortis.seppuku.Seppuku;
import me.rigamortis.seppuku.api.event.EventStageable;
import me.rigamortis.seppuku.api.event.network.EventReceivePacket;
import me.rigamortis.seppuku.api.gui.hud.component.DraggableHudComponent;
import me.rigamortis.seppuku.api.util.Timer;
import net.minecraft.client.Minecraft;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

import java.text.DecimalFormat;

/**
 * Author Seth
 * 8/31/2019 @ 3:07 AM.
 */
public final class PacketTimeComponent extends DraggableHudComponent {

    private Timer timer = new Timer();

    public PacketTimeComponent() {
        super("PacketTime");
        Seppuku.INSTANCE.getEventManager().addEventListener(this);
    }

    @Listener
    public void recievePacket(EventReceivePacket event) {
        if(event.getStage() == EventStageable.EventStage.PRE) {
            if(event.getPacket() != null) {
                this.timer.reset();
            }
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);

        final float seconds = ((System.currentTimeMillis() - this.timer.getTime()) / 1000.0f) % 60.0f;
        final String delay = "\u00A78PACKET:\u00A7f " + (seconds >= 3.0f ? "\2474" : "\247f") + new DecimalFormat("#.#").format(seconds);

        this.setW(Seppuku.INSTANCE.getFontManager().getStringWidth(delay));
        this.setH(Seppuku.INSTANCE.getFontManager().getHeight());

        Seppuku.INSTANCE.getFontManager().drawString(delay, this.getX(), this.getY(), -1, true, false);
    }

}
