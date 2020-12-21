

package pw.seppuku.rpc;

import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.Minecraft;
import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRichPresence;
import club.minnced.discord.rpc.DiscordRPC;
import me.rigamortis.seppuku.api.module.Module;
/**
 * created by Seth 
 */
public final class DiscordRPCModule extends Module
{
    private static DiscordRPC LIB;
    private DiscordRichPresence presence;
    private String details;
    private int index;
    private long lastTime;
    
    public DiscordRPCModule() {
        super("DiscordRPC", new String[] { "DiscordRichPresence", "DRPC" }, "Changes your discord status", "NONE", -1, ModuleType.MISC);
        this.details = "SPEEEEED";
        this.setEnabled(true);
        this.setHidden(true);
        final String applicationId = "666175648527810570";
        final DiscordEventHandlers handlers = new DiscordEventHandlers();
        DiscordRPCModule.LIB.Discord_Initialize("666175648527810570", handlers, true, "");
        final ServerData[] serverData = new ServerData[1];
        new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                this.presence = new DiscordRichPresence();
                this.presence.startTimestamp = this.lastTime;
                this.presence.details = this.marquee();
                this.presence.largeImageKey = "ez";
                serverData[0] = Minecraft.getMinecraft().getCurrentServerData();
                if (serverData[0] != null && Minecraft.getMinecraft().getConnection() != null) {
                    this.presence.state = "Multi player: " + serverData[0].serverIP;
                }
                else if (Minecraft.getMinecraft().isSingleplayer()) {
                    this.presence.state = "Single player";
                }
                else if (Minecraft.getMinecraft().currentScreen != null) {
                    if (Minecraft.getMinecraft().currentScreen instanceof GuiMainMenu) {
                        this.presence.state = "Main menu";
                    }
                    if (Minecraft.getMinecraft().currentScreen instanceof GuiMultiplayer) {
                        this.presence.state = "Multi player";
                    }
                }
                DiscordRPCModule.LIB.Discord_UpdatePresence(this.presence);
                DiscordRPCModule.LIB.Discord_RunCallbacks();
                try {
                    Thread.sleep(2000L);
                }
                catch (InterruptedException ignored) {
                    ignored.printStackTrace();
                }
            }
        }, "RPC-Callback-Handler").start();
    }
    
    private String marquee() {
        ++this.index;
        if (this.index >= this.details.length()) {
            this.index = 0;
        }
        return this.details.substring(0, this.index);
    }
    
    static {
        DiscordRPCModule.LIB = DiscordRPC.INSTANCE;
    }
}
