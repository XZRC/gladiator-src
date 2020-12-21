package org.drink.piss.Modules;

import me.rigamortis.seppuku.api.event.EventStageable;
import me.rigamortis.seppuku.api.event.player.EventUpdateWalkingPlayer;
import me.rigamortis.seppuku.api.module.Module;
import me.rigamortis.seppuku.api.util.MathUtil;
import me.rigamortis.seppuku.api.value.Value;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.CPacketPlayer;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

/**
 * Author: auto or one of those gamers
 * Person who put it here: cats
 */

public class TeleportSpeedModule extends Module {

    public final Value<Float> speed = new Value<Float>("Speed", new String[]{"Spd"}, "Speed multiplier for flight, higher values equals more speed.", 1f, 0f, 10f, 0.1f);

    public TeleportSpeedModule() {
        super("TeleportSpeed", new String[]{"TpSpeed"}, "Sends teleport packets in a weird way that allow for high speed travel. Credit to auto.", "NONE", -1, ModuleType.EXPLOIT);
    }

    @Listener
    public void onWalkingUpdate(EventUpdateWalkingPlayer event) {
        if (event.getStage() == EventStageable.EventStage.PRE) {
            final Minecraft mc = Minecraft.getMinecraft();
            if (mc.player.moveForward != 0.0f || (mc.player.moveStrafing != 0.0f && mc.player.onGround)) {
                for (double x = 0.0625; x < this.speed.getValue(); x += 0.262) {
                    final double[] dir = MathUtil.directionSpeed(x);
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX + dir[0], mc.player.posY, mc.player.posZ + dir[1], mc.player.onGround));
                }
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX + mc.player.motionX, 0.0, mc.player.posZ + mc.player.motionZ, mc.player.onGround));
            }
        }
    }
}
