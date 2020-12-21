package me.rigamortis.seppuku.impl.command;

import me.rigamortis.seppuku.api.command.Command;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.ClickType;

/**
 * Author Seth
 * 5/12/2019 @ 8:05 PM.
 */
public final class DropAllCommand extends Command {

    public DropAllCommand() {
        super("dropall", new String[] {"drop"}, "Drops your inv", "drop");
    }

    @Override
    public void exec(String input) {
        if (!this.clamp(input, 1, 1)) {
            this.printUsage();
            return;
        }

        final Minecraft mc = Minecraft.getMinecraft();
        (new Thread(() -> {
            try{
                for (int i = 0; i <= 45; i++) {
                    mc.playerController.windowClick(mc.player.inventoryContainer.windowId, i, -1, ClickType.THROW, mc.player);
                    Thread.sleep(150);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        })
        ).start();

    }
}
