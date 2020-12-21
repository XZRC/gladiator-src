package org.drink.piss.Modules;

import me.rigamortis.seppuku.Seppuku;
import me.rigamortis.seppuku.api.event.player.EventSendChatMessage;
import me.rigamortis.seppuku.api.module.Module;
import me.rigamortis.seppuku.impl.module.hidden.CommandsModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.util.StringUtils;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

import java.util.ArrayList;

/**
 * Author cats
 */

public class MassMessageModule extends Module {

    private ArrayList<String> players = new ArrayList<>();

    public MassMessageModule() {
        super("MassMessage", new String[]{"ServerMessage", "SMsg"}, "Modify your outgoing chat messages", "NONE", -1, ModuleType.EXPLOIT);
    }

    @Listener
    public void onSendChatMessage(EventSendChatMessage event) {
        final CommandsModule cmds = (CommandsModule) Seppuku.INSTANCE.getModuleManager().find(CommandsModule.class);
        if (cmds == null) {
            return;
        }

        if (event.getMessage().startsWith("/") || event.getMessage().startsWith(cmds.prefix.getValue())) {
            return;
        }

        event.setCanceled(true);

        final Minecraft mc = Minecraft.getMinecraft();
        for(NetworkPlayerInfo info : mc.getConnection().getPlayerInfoMap()) {
            String name = info.getDisplayName().getFormattedText();
            name = StringUtils.stripControlCodes(name);

            if(name.equals(mc.player.getName()))
                continue;

            this.players.add(name);
        }

        for(String playerName : players) {
            if (playerName != null) {
                mc.getConnection().sendPacket(new CPacketChatMessage("/whisper " + playerName + " " + event.getMessage()));
            }
        }

        this.players.clear();
    }
}