package io.github.famous1622.gladiator.commands;

import me.rigamortis.seppuku.Seppuku;
import me.rigamortis.seppuku.api.command.Command;
import me.rigamortis.seppuku.api.util.MathUtil;
import me.rigamortis.seppuku.api.util.StringUtil;
import net.minecraft.network.play.client.CPacketPlayer;

public class JJPlzCommand extends Command {
    public JJPlzCommand() {
        super("JJPlz", new String[]{"jjHClip"}, "Packets are cool and good", "ElytraHClip <Amount>");
    }

    @Override
    public void exec(String input) {
        if (!this.clamp(input, 2, 2)) {
            this.printUsage();
            return;
        }

        final String[] split = input.split(" ");

        if (StringUtil.isDouble(split[1])) {
            final double num = Double.parseDouble(split[1]);
            final double[] dir = MathUtil.directionSpeed(num);
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX + dir[0], 10000, mc.player.posZ + dir[1], mc.player.onGround));

            Seppuku.INSTANCE.logChat("Teleported you " + ((num > 0) ? "forward" : "backward") + " " + num);
        } else {
            Seppuku.INSTANCE.errorChat("Unknown number " + "\247f\"" + split[1] + "\"");
        }
    }

}
