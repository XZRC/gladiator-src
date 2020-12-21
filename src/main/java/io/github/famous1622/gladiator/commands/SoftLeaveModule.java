package io.github.famous1622.gladiator.commands;

import me.rigamortis.seppuku.api.command.Command;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;

public class SoftLeaveModule extends Command {

    public SoftLeaveModule() {
        super("SoftLeave", new String[]{"sLeave"}, "leaves the server, kinda", "softLeave <unload>");
    }

    @Override
    public void exec(String input) {
        if (!this.clamp(input, 2, 2)) {
            this.printUsage();
            return;
        }

        final String[] split = input.split(" ");

        if (split[1].equalsIgnoreCase("unload")) {
            mc.loadWorld(null);
        }

        mc.currentScreen = new GuiMultiplayer(new GuiMainMenu());
    }
}
