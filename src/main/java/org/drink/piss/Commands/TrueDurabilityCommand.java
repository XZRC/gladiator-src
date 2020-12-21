package org.drink.piss.Commands;

import me.rigamortis.seppuku.Seppuku;
import me.rigamortis.seppuku.api.command.Command;
import net.minecraft.client.Minecraft;

/**
 * Author cats
 * 03/1/2020
 */

public class TrueDurabilityCommand extends Command {

    public TrueDurabilityCommand() {
        super("TrueDurability", new String[]{"Dura", "TrueDura", "TD"}, "Outputs the true durability of the item held in the player's mainhand.", "Requires no arguments.");
    }

    @Override
    public void exec(String input) {
        if (!this.clamp(input, 1, 1)) {
            this.printUsage();
            return;
        }

        Seppuku.INSTANCE.logChat(String.valueOf(Minecraft.getMinecraft().player.getHeldItemMainhand().getMaxDamage() - Minecraft.getMinecraft().player.getHeldItemMainhand().getItemDamage()));
    }
}
