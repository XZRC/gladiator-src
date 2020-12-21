package org.drink.piss.Modules;


import org.drink.piss.util.ChatHelper;
import org.drink.piss.util.FileHelper;
import me.rigamortis.seppuku.api.event.EventStageable;
import me.rigamortis.seppuku.api.event.player.EventPlayerUpdate;
import me.rigamortis.seppuku.api.module.Module;
import me.rigamortis.seppuku.api.util.Timer;
import me.rigamortis.seppuku.api.value.Value;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.CPacketChatMessage;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;


public class SpammerModule extends Module {

    public final Value<Boolean> random = new Value<>("Random", new String[]{"r"}, "Randomizes what is spammed.", false);
    public final Value<Boolean> randomSuffix = new Value<>("AntiSpam", new String[]{"AS", "Anti"}, "Adds a random value at the end to avoid flagging spam filters.", false);
    public final Value<Boolean> readFile = new Value<>("File", new String[]{"Custom"}, "Spams the contents of a file.", false);public final Value<Integer> packets = new Value<Integer>("Packets", new String[]{"pckts", "packet"}, "Amount of packets to send each tick while running the chosen lag mode.", 500, 0, 5000, 1);
    public final Value<Integer> delay = new Value<Integer>("Delay", new String[]{"dly", "d"}, "Delay between Messages.", 500);

    private static final String fileName = "Gladiator_Spammer.txt";
    private static final String defaultMessage = "Join 0b0t.org - Worlds oldest Minecraft Server!";
    private static List<String> spamMessages;
    private static Random rnd;
    private static Timer timer = new Timer();

    public SpammerModule() {
        super("Spammer", new String[]{"spam", "Spammsg"}, "Modify your outgoing chat messages", "NONE", -1, ModuleType.EXPLOIT);

    }

    @Override
    public void onEnable() {
        timer.reset();
        this.readSpamFile();
        if (Minecraft.getMinecraft().world == null) {
            this.toggle();
            return;
        }
    }

    @Override
    public void onDisable() {
        spamMessages.clear();
    }

    @Listener
    public void onPlayerUpdate(EventPlayerUpdate event) {
        if (event.getStage() == EventStageable.EventStage.PRE) {
            if (timer.passed(delay.getValue())) {
                this.runCycle();
                timer.reset();
            }
        }
    }

    private void runCycle() {
        String messageOut;
        if (Minecraft.getMinecraft().world == null) {
            return;
        }
        if (this.readFile.getValue()) {
            this.readSpamFile();
            this.readFile.setValue(false);
        }
        if (spamMessages.size() <= 0) return;
        if (this.random.getValue()) {
            int index = rnd.nextInt(spamMessages.size());
            messageOut = spamMessages.get(index);
            spamMessages.remove(index);
        } else {
            messageOut = spamMessages.get(0);
            spamMessages.remove(0);
        }
        spamMessages.add(messageOut);
        int reserved = 0;
        ArrayList<String> messageAppendix = new ArrayList<>();
        if (this.randomSuffix.getValue()) {
            messageAppendix.add(ChatHelper.generateRandomHexSuffix(2));
        }
        if (messageAppendix.size() > 0) {
            StringBuilder sb = new StringBuilder();
            sb.append(" ");
            for (String msg : messageAppendix) {
                sb.append(msg);
            }
            messageOut = ChatHelper.cropMaxLengthMessage(messageOut, sb.toString().length() + reserved);
            messageOut = messageOut + sb.toString();
        }
        Minecraft.getMinecraft().player.connection.sendPacket(new CPacketChatMessage(messageOut.replaceAll("\u00a7", "")));
    }

    private void readSpamFile() {
        List<String> fileInput = FileHelper.readTextFileAllLines((String)fileName);
        Iterator<String> i = fileInput.iterator();
        spamMessages.clear();
        do {
            if (!i.hasNext()) {
                if (spamMessages.size() != 0) return;
                spamMessages.add(defaultMessage);
                return;
            }
            String s = i.next();
            if (s.replaceAll("\\s", "").isEmpty()) continue;
            spamMessages.add(s);
        } while (true);
    }

    static {
        spamMessages = new ArrayList<>();
        rnd = new Random();
    }
}
