package io.github.famous1622.gladiator.commands;

import me.rigamortis.seppuku.Seppuku;
import me.rigamortis.seppuku.api.command.Command;
import me.rigamortis.seppuku.api.util.MathUtil;
import me.rigamortis.seppuku.api.util.StringUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.math.Vec3d;
import scala.collection.parallel.ParIterableLike;

/**
 * Author Seth
 * 4/16/2019 @ 9:11 PM.
 */
public final class PacketHClipCommand extends Command {

    public PacketHClipCommand() {
        super("PacketHClip", new String[]{"PHC", "ElytraHorizontalClip", "pHClip"}, "Allows you to teleport horizontally", "ElytraHClip <Amount>");
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


            for (double x = 0.0625; x < num; x += 0.262) {
                final double[] dir = MathUtil.directionSpeed(x);
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX + dir[0], mc.player.posY, mc.player.posZ + dir[1], false));
            }
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX + mc.player.motionX, 0.0, mc.player.posZ + mc.player.motionZ, mc.player.onGround));

                Seppuku.INSTANCE.logChat("Teleported you " + ((num > 0) ? "forward" : "backward") + " " + num);
        } else {
            Seppuku.INSTANCE.errorChat("Unknown number " + "\247f\"" + split[1] + "\"");
        }
    }
}
