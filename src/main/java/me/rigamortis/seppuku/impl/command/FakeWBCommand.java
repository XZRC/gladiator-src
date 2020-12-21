package me.rigamortis.seppuku.impl.command;

import com.sun.jna.IntegerType;
import me.rigamortis.seppuku.Seppuku;
import me.rigamortis.seppuku.api.command.Command;
import me.rigamortis.seppuku.api.util.MathUtil;
import me.rigamortis.seppuku.api.util.StringUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.Vec3d;
import scala.Int;

public class FakeWBCommand extends Command {
    public FakeWBCommand() {
        super("FakeWorldBorder", new String[] {"fwb", "worldborder"}, "Allows you to fake the worldborder size", "fwb <Size>");
    }

    @Override
    public void exec(String input) {
        if (!this.clamp(input, 1)) {
            this.printUsage();
            return;
        }

        final String[] split = input.split(" ");

        if (StringUtil.isInt(split[1])) {
            final int num = Integer.parseInt(split[1]);

            Minecraft.getMinecraft().world.getWorldBorder().setSize(num);
            Seppuku.INSTANCE.logChat("Set World Border to " + num);

        } else {
            Seppuku.INSTANCE.errorChat("Unknown number " + "\247f\"" + split[1] + "\"");
        }
    }
}
