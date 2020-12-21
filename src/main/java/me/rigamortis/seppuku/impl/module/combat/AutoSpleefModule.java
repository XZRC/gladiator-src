package me.rigamortis.seppuku.impl.module.combat;

import me.rigamortis.seppuku.Seppuku;
import me.rigamortis.seppuku.api.event.EventStageable;
import me.rigamortis.seppuku.api.event.player.EventUpdateWalkingPlayer;
import me.rigamortis.seppuku.api.module.Module;
import me.rigamortis.seppuku.api.util.MathUtil;
import me.rigamortis.seppuku.api.value.Value;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

import java.util.ArrayList;
import java.util.List;

/**
 * Author cats
 * 18/12/2019
 */
public final class AutoSpleefModule extends Module {

    public final Value<Mode> mode = new Value("Mode", new String[]{"Mode", "M"}, "The Spleef mode.", Mode.SINGLE);

    public final Value<Float> distance = new Value<Float>("Distance", new String[]{"Dist", "D"}, "Maximum distance in blocks the nuker will reach.", 4.5f, 0.0f, 10.0f, 0.1f);

    public AutoSpleefModule() {
        super("AutoSpleef", new String[]{"Spleef"}, "Automatically mines blocks within reach", "NONE", -1, ModuleType.WORLD);
    }

    @Override
    public String getMetaData() {
        return this.mode.getValue().name();
    }

    @Listener
    public void onWalkingUpdate(EventUpdateWalkingPlayer event) {
        if (event.getStage() == EventStageable.EventStage.PRE) {
            final Minecraft mc = Minecraft.getMinecraft();

            for (Entity player : mc.world.getLoadedEntityList()) {
                if (player instanceof EntityPlayer && player != mc.player && Seppuku.INSTANCE.getFriendManager().isFriend(player) == null && mc.player.getDistance(player) <= this.distance.getValue()) {

                    switch (this.mode.getValue()) {
                        case MULTI:
                            List<BlockPos> pos = new ArrayList<>();
                            pos.add(new BlockPos(player.posX, player.posY - 1, player.posZ));
                            pos.add(new BlockPos(player.posX + 1, player.posY - 1, player.posZ));
                            pos.add(new BlockPos(player.posX - 1, player.posY - 1, player.posZ));
                            pos.add(new BlockPos(player.posX, player.posY - 1, player.posZ + 1));
                            pos.add(new BlockPos(player.posX, player.posY - 1, player.posZ - 1));

                            for (BlockPos spleef : pos) {
                                final float[] angle = MathUtil.calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), new Vec3d(spleef.getX() + 0.5f, spleef.getY() + 0.5f, spleef.getZ() + 0.5f));
                                Seppuku.INSTANCE.getRotationManager().setPlayerRotations(angle[0], angle[1]);

                                if (canBreak(spleef)) {
                                    mc.playerController.onPlayerDamageBlock(spleef, mc.player.getHorizontalFacing());
                                    mc.player.swingArm(EnumHand.MAIN_HAND);
                                }
                            }

                        case SINGLE:
                            final BlockPos block = new BlockPos(player.posX, player.posY - 1, player.posZ);

                            final float[] angle = MathUtil.calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), new Vec3d(block.getX() + 0.5f, block.getY() + 0.5f, block.getZ() + 0.5f));
                            Seppuku.INSTANCE.getRotationManager().setPlayerRotations(angle[0], angle[1]);

                            if (canBreak(block)) {
                                mc.playerController.onPlayerDamageBlock(block, mc.player.getHorizontalFacing());
                                mc.player.swingArm(EnumHand.MAIN_HAND);
                            }
                    }
                }
            }
        }
    }

    private boolean canBreak(BlockPos pos) {
        final IBlockState blockState = Minecraft.getMinecraft().world.getBlockState(pos);
        final Block block = blockState.getBlock();

        return block.getBlockHardness(blockState, Minecraft.getMinecraft().world, pos) != -1;
    }

    private enum Mode {
        SINGLE, MULTI
    }
}
