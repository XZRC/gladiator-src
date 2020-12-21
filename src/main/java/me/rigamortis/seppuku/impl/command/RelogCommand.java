package me.rigamortis.seppuku.impl.command;

import me.rigamortis.seppuku.Seppuku;
import me.rigamortis.seppuku.api.command.Command;
import net.minecraft.client.Minecraft;

public class RelogCommand extends Command
{
    Minecraft mc;

    public RelogCommand() {
        super("quickrelog", new String[] { "relog" }, "9b9t only Quickly relogs", "reconnect");
        this.mc = Minecraft.getMinecraft();
    }
    
    public void exec(final String input) {
        if (!this.clamp(input, 1, 1)) {
            this.printUsage();
        }
        else {
            Seppuku.INSTANCE.logChat("Reconnecting..");
            Seppuku.INSTANCE.logChat("Connecting to queue..");
            this.mc.player.sendChatMessage("/server queue");
            try {
                Seppuku.INSTANCE.logChat("Connecting to 9b9t..");
                Thread.sleep(850L);
                this.mc.player.sendChatMessage("/server 9b9t");
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
