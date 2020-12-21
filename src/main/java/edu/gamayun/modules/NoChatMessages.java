package edu.gamayun.modules;

import me.rigamortis.seppuku.api.event.EventStageable;
import me.rigamortis.seppuku.api.event.network.EventReceivePacket;
import me.rigamortis.seppuku.api.module.Module;
import net.minecraft.network.play.server.SPacketChat;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

public class NoChatMessages extends Module {

    public NoChatMessages() {
        super("NoChat", new String[]{"antichat", "chatbegone"},
                "Removes all chat messages that are not whispers",
                "", 16777215, false, false, ModuleType.EXPLOIT);
    }

    public void onEnable() {
        super.onEnable();
    }

    public void onDisable() {
        super.onDisable();
    }

    @Listener
    public void onPacketReceive(EventReceivePacket event) {
        if (event.getPacket() instanceof SPacketChat && event.getStage() == EventStageable.EventStage.PRE) {
            SPacketChat packet = ((SPacketChat) event.getPacket());
            if (!packet.getChatComponent().toString().contains("\247d")) {
                event.setCanceled(true);
            }
        }
    }
}
