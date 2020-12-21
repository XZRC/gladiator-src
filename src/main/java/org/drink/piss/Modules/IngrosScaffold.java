package org.drink.piss.Modules;

import me.rigamortis.seppuku.api.event.EventStageable;
import me.rigamortis.seppuku.api.event.player.EventUpdateWalkingPlayer;
import me.rigamortis.seppuku.api.module.Module;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

import java.util.Objects;

public class IngrosScaffold extends Module {

    public IngrosScaffold() {
        super("IngrosScaffold", new String[]{"iScaffold"}, "Ingrosware's scaffold", "NONE", -1, ModuleType.WORLD);
    }

    @Listener
    public void onUpdate(EventUpdateWalkingPlayer event) {
        if (event.getStage() != EventStageable.EventStage.PRE) return;
        BlockPos pos = new BlockPos(mc.player.posX, mc.player.posY - 1.0, mc.player.posZ);
        int slot = mc.player.inventory.currentItem;
        int i = 8;
        while (i >= 0) {
            if (mc.player.inventory.getStackInSlot(i).getItem() instanceof ItemBlock && this.isBlockAbleToBeWalkedOn(((ItemBlock)mc.player.inventory.getStackInSlot(i).getItem()).getBlock().getDefaultState())) {
                for (EnumFacing facing : EnumFacing.values()) {
                    if (facing == EnumFacing.UP || !this.isBlockAbleToBeWalkedOn(mc.world.getBlockState(pos.offset(facing)))) continue;
                    Objects.requireNonNull(mc.getConnection()).sendPacket(new CPacketHeldItemChange(i));
                    mc.player.inventory.currentItem = i;
                    this.lookAtBlockSide(pos.offset(facing), facing);
                    this.place(pos.offset(facing), facing);
                    mc.getConnection().sendPacket(new CPacketHeldItemChange(slot));
                    mc.player.inventory.currentItem = slot;
                    return;
                }
            }
            --i;
        }
    }

    private void lookAtBlockSide(BlockPos pos, EnumFacing facing) {
        if (mc.world.getBlockState(new BlockPos(mc.player.posX, mc.player.posY - 1.0, mc.player.posZ)).getMaterial() != Material.AIR) return;
        double d0 = (double)pos.getX() + 0.5 + (double)((float)facing.getOpposite().getXOffset() / 2.0f) - mc.player.posX;
        double d1 = (double)pos.getY() + 0.5 + (double)((float)facing.getOpposite().getYOffset() / 2.0f) - (mc.player.posY + (double)mc.player.getEyeHeight());
        double d2 = (double)pos.getZ() + 0.5 + (double)((float)facing.getOpposite().getZOffset() / 2.0f) - mc.player.posZ;
        float[] rots = getRotations(d0, d2, d1);
        Objects.requireNonNull(mc.getConnection()).sendPacket(new CPacketPlayer.Rotation(rots[0], rots[1], true));
    }

    private static float[] getRotations(double xDiff, double zDiff, double yDiff) {
        double dist = MathHelper.sqrt(xDiff * xDiff + zDiff * zDiff);
        float yaw = (float)MathHelper.wrapDegrees(MathHelper.atan2(zDiff, xDiff) * 57.29577951308232 - 90.0);
        float pitch = (float)MathHelper.wrapDegrees(-(MathHelper.atan2(yDiff, dist) * 57.29577951308232));
        return new float[]{yaw, pitch};
    }

    private boolean isBlockAbleToBeWalkedOn(IBlockState state) {
        AxisAlignedBB bb = state.getCollisionBoundingBox(mc.world, new BlockPos(0, 0, 0));
        if (bb == null) return false;
        return bb.equals(Block.FULL_BLOCK_AABB);
    }

    private void place(BlockPos pos, EnumFacing facing) {
        if (mc.world.getBlockState(new BlockPos(mc.player.posX, mc.player.posY - 1.0, mc.player.posZ)).getMaterial() != Material.AIR) return;
        if (mc.world.getBlockState(pos).getMaterial() == Material.AIR) return;
        ItemStack stack = mc.player.getHeldItem(EnumHand.MAIN_HAND);
        int i = stack.getCount();
        EnumActionResult enumactionresult = mc.playerController.processRightClickBlock(mc.player, mc.world, pos, facing.getOpposite(), new Vec3d(0.0, 0.0, 0.0), EnumHand.MAIN_HAND);
        if (enumactionresult != EnumActionResult.SUCCESS) return;
        mc.player.swingArm(EnumHand.MAIN_HAND);
        if (stack.isEmpty()) return;
        if (stack.getCount() == i) {
            if (!mc.playerController.isInCreativeMode()) return;
        }
        mc.entityRenderer.itemRenderer.resetEquippedProgress(EnumHand.MAIN_HAND);
    }
}
