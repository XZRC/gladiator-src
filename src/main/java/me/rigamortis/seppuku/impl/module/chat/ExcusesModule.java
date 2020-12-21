package me.rigamortis.seppuku.impl.module.chat;

import me.rigamortis.seppuku.api.event.minecraft.EventDisplayGui;
import me.rigamortis.seppuku.api.module.Module;
import net.minecraft.client.gui.GuiGameOver;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

/**
 * Memeszz
 */
public final class ExcusesModule extends Module {
    public ExcusesModule() {
        super("Excuses", new String[]{"Excuses"}, "Makes a excuse when you die.", "NONE", -1, ModuleType.CHAT);
    }


    @Listener
    public void displayGuiScreen(EventDisplayGui event) {
        if(event.getScreen() != null && event.getScreen() instanceof GuiGameOver) {
{
            mc.displayGuiScreen(null);
            mc.player.sendChatMessage("Fuck bro i was desynced!");
        }
    }
}}