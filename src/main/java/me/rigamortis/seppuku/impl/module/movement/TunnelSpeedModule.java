package me.rigamortis.seppuku.impl.module.movement;

import me.rigamortis.seppuku.api.event.EventStageable;
import me.rigamortis.seppuku.api.event.player.EventUpdateWalkingPlayer;
import me.rigamortis.seppuku.api.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

public class TunnelSpeedModule extends Module {
    public TunnelSpeedModule() {
        super("TunnelSpeed", new String[]{"TunSpeed","Tunnel"}, "Increases speed while moving in tunnels by jumping.", "NONE", -1, ModuleType.MOVEMENT);
    }

    @Listener
    public void OnWalkingUpdate(EventUpdateWalkingPlayer event) {
        if (event.getStage() == EventStageable.EventStage.PRE) {
            final Minecraft mc = Minecraft.getMinecraft();
            BlockPos pos = new BlockPos(mc.player.posX, mc.player.posY + 2.0D, mc.player.posZ);
            BlockPos pos2 = new BlockPos(mc.player.posX, mc.player.posY - 1.0D, mc.player.posZ);
            if (mc.world.getBlockState(pos).getBlock() != Blocks.AIR && mc.world.getBlockState(pos).getBlock() != Blocks.PORTAL && mc.world.getBlockState(pos).getBlock() != Blocks.END_PORTAL && mc.world.getBlockState(pos).getBlock() != Blocks.WATER && mc.world.getBlockState(pos).getBlock() != Blocks.FLOWING_WATER && mc.world.getBlockState(pos).getBlock() != Blocks.LAVA && mc.world.getBlockState(pos).getBlock() != Blocks.FLOWING_LAVA && mc.world.getBlockState(pos2).getBlock() != Blocks.ICE && mc.world.getBlockState(pos2).getBlock() != Blocks.FROSTED_ICE && mc.world.getBlockState(pos2).getBlock() != Blocks.PACKED_ICE && !mc.player.isInWater()) {
                float yaw = (float)Math.toRadians(mc.player.rotationYaw);
                if (mc.gameSettings.keyBindForward.isKeyDown() && !mc.gameSettings.keyBindSneak.isKeyDown() && mc.player.onGround) {
                    mc.player.motionX -= Math.sin(yaw) * 0.15D;
                    mc.player.motionZ += Math.cos(yaw) * 0.15D;
                }
            }

        }
    }

}
