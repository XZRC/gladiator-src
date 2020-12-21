package me.rigamortis.seppuku.impl.module.chat;

import me.rigamortis.seppuku.api.event.EventStageable;
import me.rigamortis.seppuku.api.event.network.EventReceivePacket;
import me.rigamortis.seppuku.api.module.Module;
import me.rigamortis.seppuku.api.util.StringUtil;
import me.rigamortis.seppuku.api.value.Value;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.util.text.TextComponentString;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

import java.util.ArrayList;
import java.util.List;

/**
 * Author Seth
 * 5/25/2019 @ 4:34 AM.
 */
public final class ChatFilterModule extends Module {

    public final Value<Boolean> unicode = new Value("Unicode", new String[]{"uc"}, "Reverts \"Fancy Chat\" characters back into normal ones. ", true);
    public final Value<Boolean> broadcasts = new Value("Broadcasts", new String[]{"broad", "bc"}, "Prevents displaying chat messages that begin with [SERVER].", true);
    public final Value<Boolean> spam = new Value("Spam", new String[]{"sp", "s"}, "Attempts to prevent spam by checking recent chat messages for duplicates.", true);

    private List<String> cache = new ArrayList<>();

    public ChatFilterModule() {
        super("ChatFilter", new String[]{"CFilter"}, "Filters out annoying chat messages", "NONE", -1, ModuleType.CHAT);
    }

    @Listener
    public void onDisable() {
        super.onDisable();
        this.cache.clear();
    }

    @Listener
    public void receivePacket(EventReceivePacket event) {
        if (event.getStage() == EventStageable.EventStage.PRE) {
            if (event.getPacket() instanceof SPacketChat) {
                final SPacketChat packet = (SPacketChat) event.getPacket();

                if (this.broadcasts.getValue()) {
                    if (packet.getChatComponent().getFormattedText().startsWith("\2475[SERVER]")) {
                        event.setCanceled(true);
                    }
                }

                if (this.spam.getValue()) {
                    final String chat = packet.getChatComponent().getUnformattedText();

                    if (this.cache.size() > 0) {
                        for (String s : this.cache) {
                            final double diff = StringUtil.levenshteinDistance(s, chat);

                            if (diff >= 0.75f) {
                                event.setCanceled(true);
                            }
                        }
                    }

                    this.cache.add(chat);

                    if (this.cache.size() >= 10) {
                        this.cache.remove(0);
                    }
                }

                if (this.unicode.getValue()) {
                    if (packet.getChatComponent() instanceof TextComponentString) {
                        final TextComponentString component = (TextComponentString) packet.getChatComponent();

                        final StringBuilder sb = new StringBuilder();

                        boolean containsUnicode = false;

                        for (String s : component.getFormattedText().split(" ")) {

                            String line = "";

                            for (char c : s.toCharArray()) {
                                if (c >= 0xFEE0) {
                                    c -= 0xFEE0;
                                    containsUnicode = true;
                                }

                                line += c;
                            }

                            sb.append(line + " ");
                        }

                        if (containsUnicode) {
                            packet.chatComponent = new TextComponentString(sb.toString());
                        }
                    }
                }
            }
        }
    }

}
