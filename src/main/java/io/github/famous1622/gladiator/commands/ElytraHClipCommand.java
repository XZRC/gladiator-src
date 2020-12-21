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
public final class ElytraHClipCommand extends Command {

    public ElytraHClipCommand() {
        super("ElytraHClip", new String[]{"EHC", "ElytraHorizontalClip", "EHClip"}, "Allows you to teleport horizontally", "ElytraHClip <Amount>");
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

            final double[] dir = MathUtil.directionSpeed(1.0d);

            if (dir != null) {
//                if (Minecraft.getMinecraft().player.getRidingEntity() != null) {
//                    Minecraft.getMinecraft().player.getRidingEntity().setPosition(Minecraft.getMinecraft().player.getRidingEntity().posX + dir.x * num, Minecraft.getMinecraft().player.getRidingEntity().posY, Minecraft.getMinecraft().player.getRidingEntity().posZ + dir.z * num);
//                } else {
//                    Minecraft.getMinecraft().player.setPosition(Minecraft.getMinecraft().player.posX + dir.x * num, Minecraft.getMinecraft().player.posY, Minecraft.getMinecraft().player.posZ + dir.z * num);
//                }
                Minecraft.getMinecraft().player.connection.sendPacket(new CPacketEntityAction(Minecraft.getMinecraft().player, CPacketEntityAction.Action.START_FALL_FLYING));
                Minecraft.getMinecraft().player.setVelocity(dir[0], 0, dir[1]);
                Seppuku.INSTANCE.logChat("Teleported you " + ((num > 0) ? "forward" : "backward") + " " + num);
            }
        } else {
            Seppuku.INSTANCE.errorChat("Unknown number " + "\247f\"" + split[1] + "\"");
        }
    }
}
