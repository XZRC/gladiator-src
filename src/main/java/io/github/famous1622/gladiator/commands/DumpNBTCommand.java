package io.github.famous1622.gladiator.commands;

import me.rigamortis.seppuku.Seppuku;
import me.rigamortis.seppuku.api.command.Command;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;

import java.io.IOException;

public class DumpNBTCommand extends Command {
    public DumpNBTCommand() {
        super("DumpNBT", new String[]{"dnbt"}, "Mojang please.", "DumpNBT");
    }

    @Override
    public void exec(String input) throws IOException {
        Seppuku.INSTANCE.logChat(String.valueOf(mc.player.inventory.getCurrentItem().getTagCompound()));
    }
}
