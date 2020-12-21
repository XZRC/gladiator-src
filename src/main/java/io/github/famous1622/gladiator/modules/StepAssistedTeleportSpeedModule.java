package io.github.famous1622.gladiator.modules;

import me.rigamortis.seppuku.api.event.EventStageable;
import me.rigamortis.seppuku.api.event.player.EventUpdateWalkingPlayer;
import me.rigamortis.seppuku.api.module.Module;
import me.rigamortis.seppuku.api.util.MathUtil;
import me.rigamortis.seppuku.api.value.Value;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Author: auto or one of those gamers
 * Person who put it here: cats
 */

public class StepAssistedTeleportSpeedModule extends Module {

    public final Value<Float> speed = new Value<Float>("Speed", new String[]{"Spd"}, "Speed multiplier for flight, higher values equals more speed.", 1f, 0f, 10f, 0.1f);
    private double[] selectedPositions = {0.42D, 0.75D, 1.0D};
    public final Value<Double> fallspeed = new Value<Double>("FallSpeed", new String[]{"FSpd"}, "Speed multiplier for flight, higher values equals more speed.", 0.1d, 0.01d, 1d, 0.01d);

    private List<Vec3d> points = new LinkedList<>();
    public StepAssistedTeleportSpeedModule() {
        super("HighJumpTeleportSpeed", new String[]{"HTpSpeed"}, "Sends teleport packets in a weird way that allow for high speed travel. Credit to auto.", "NONE", -1, ModuleType.EXPLOIT);
    }

    @Listener
    public void onWalkingUpdate(EventUpdateWalkingPlayer event) {
        if (event.getStage() == EventStageable.EventStage.PRE) {
            final Minecraft mc = Minecraft.getMinecraft();
            if ((mc.player.moveForward != 0.0f || mc.player.moveStrafing != 0.0f) && mc.player.onGround) {
                double pawnY = 0;
                final double[] lastStep = MathUtil.directionSpeed(.262);

                for (double x = 0.0625; x < this.speed.getValue(); x += 0.262) {
                    final double[] dir = MathUtil.directionSpeed(x);
                    AxisAlignedBB bb = Objects.requireNonNull(mc.player.getEntityBoundingBox()).offset(dir[0],pawnY,dir[1]);;

                    while (collidesHorizontally(bb)) {
                        for (double position : this.selectedPositions) {
                            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX + dir[0] - lastStep[0], mc.player.posY + pawnY + position, mc.player.posZ + dir[1] - lastStep[1], true));
                        }
                        pawnY = pawnY + 1;
                        bb = Objects.requireNonNull(mc.player.getEntityBoundingBox()).offset(dir[0],pawnY,dir[1]);
                    }

                    if (!mc.world.checkBlockCollision(bb.grow(0.0125, 0, 0.0125).offset(0,-1,0))) {
                        for (double i = 0.0d; i <= 1.0d; i+=fallspeed.getValue()) {
                            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX + dir[0], mc.player.posY + pawnY - i, mc.player.posZ + dir[1], true));
                        }
                        pawnY -= 1.0;
                    }
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX + dir[0], mc.player.posY + pawnY, mc.player.posZ + dir[1], mc.player.onGround));
                }
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX + mc.player.motionX, 0.0, mc.player.posZ + mc.player.motionZ, mc.player.onGround));
            }
        }

    }

    private static boolean collidesHorizontally(AxisAlignedBB bb) {
        final Minecraft mc = Minecraft.getMinecraft();
        if (mc.world.collidesWithAnyBlock(bb)) {
            Vec3d center = bb.getCenter();
            BlockPos blockpos = new BlockPos(center.x, bb.minY, center.z);
            return mc.world.isBlockFullCube(blockpos.west()) || mc.world.isBlockFullCube(blockpos.east()) || mc.world.isBlockFullCube(blockpos.north()) || mc.world.isBlockFullCube(blockpos.south()) || mc.world.isBlockFullCube(blockpos);
        } else {
            return false;
        }
    }
}
