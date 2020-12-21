package io.github.famous1622.gladiator.modules;

import me.rigamortis.seppuku.Seppuku;
import me.rigamortis.seppuku.api.event.player.EventUpdateWalkingPlayer;
import me.rigamortis.seppuku.api.module.Module;
import me.rigamortis.seppuku.api.util.BlockUtil;
import me.rigamortis.seppuku.api.util.InventoryUtil;
import me.rigamortis.seppuku.api.value.Value;
import me.rigamortis.seppuku.impl.management.FriendManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

import java.util.*;

public class AutoWebModule extends Module {
    public final Value<Cage> mode = new Value<>("Mode", new String[]{"Mode", "M"}, "The brightness mode to use.", Cage.Web);
    public final Value<Integer> tickDelay = new Value<Integer>("TickDelay", new String[]{"dly", "d"}, "Delay between Messages.", 2, 1, 10, 1);
    private boolean firstRun = false;
    private int playerHotbarSlot = -1;
    private int lastHotbarSlot = -1;
    private boolean isSneaking = false;
    private int delayStep = 0;
    public final Value<Integer> blocksPerTick =  new Value<Integer>("BlocksPerTick", new String[]{"bpt", "tick"}, "Delay between Messages.", 2, 1, 23, 1);
    private int offsetStep=0;
    public final Value<Float> range = new Value("Range", new String[]{"Dist"}, "The minimum range to attack crystals.", 4.5f, 0.0f, 5.0f, 0.1f);

    public AutoWebModule() {
        super("AutoWeb", new String[]{"AW"},"Spider Man", "NONE", -1, ModuleType.COMBAT);
    }

    @Override
    public void onEnable() {
        firstRun = true;
        playerHotbarSlot = mc.player.inventory.currentItem;
        lastHotbarSlot = -1;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        if (lastHotbarSlot != playerHotbarSlot && playerHotbarSlot != -1) {
            mc.player.inventory.currentItem = playerHotbarSlot;
            mc.playerController.updateController();
        }

        if (isSneaking) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            isSneaking = false;
        }

        playerHotbarSlot = -1;
        lastHotbarSlot = -1;

        super.onDisable();
    }

    @Listener
    public void onUpdate(EventUpdateWalkingPlayer event) {
        if (!firstRun) {
            if (delayStep < tickDelay.getValue()) {
                delayStep++;
                return;
            } else {
                delayStep = 0;
            }
        }

        EntityPlayer closestTarget = findClosestTarget();

        if (closestTarget == null) {
            if (firstRun) {
                firstRun = false;
            }
            return;
        }

        List<Vec3d> placeTargets = new ArrayList<>();

        Collections.addAll(placeTargets, mode.getValue().offsets);

        int blocksPlaced = 0;
        while (blocksPlaced < blocksPerTick.getValue()) {
            if (offsetStep >= placeTargets.size()) {
                offsetStep = 0;
                break;
            }
            Vec3d vec3d = placeTargets.get(offsetStep);
            BlockPos targetPos = new BlockPos(closestTarget.getPositionVector()).down().add(vec3d.x, vec3d.y, vec3d.z);

            if (placeBlockInRange(targetPos, range.getValue())) blocksPlaced++;
            offsetStep++;
        }

        if (mc.player.ticksExisted % 20 == 0) {
            mc.player.connection.sendPacket(new CPacketClickWindow(0, 0, 0, ClickType.PICKUP, new ItemStack(Blocks.BEDROCK), (short) 1337));
        }
    }

    private boolean placeBlockInRange(BlockPos pos, double range) {
        IBlockState block = mc.world.getBlockState(pos);
        if (!block.getMaterial().isReplaceable()) {
            return false;
        }

        EnumFacing placeableSide = BlockUtil.getPlaceableSide(pos);
        if (placeableSide == null) {
            return false;
        }

        BlockPos neighbour = pos.offset(placeableSide);
        EnumFacing opposite = placeableSide.getOpposite();
        Vec3d hitVec = new Vec3d(neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));

        if (mc.player.getPositionVector().distanceTo(hitVec) > range) {
            return false;
        }

        int obiSlot = InventoryUtil.getItemHotbar(Item.getItemFromBlock(Blocks.WEB));

        if (obiSlot == -1) {
            this.toggle();
            return false;
        }

        if (lastHotbarSlot != obiSlot) {
            mc.player.inventory.currentItem = obiSlot;
            lastHotbarSlot = obiSlot;
            mc.playerController.updateController();
        }


        return BlockUtil.placeBlock(pos);
    }

    private EntityPlayer findClosestTarget() {
        List<EntityPlayer> playerList = mc.world.playerEntities;
        EntityPlayer closestTarget = null;
        for (EntityPlayer target : playerList) {
            if (target == mc.player) {
                continue;
            }

            if (Seppuku.INSTANCE.getFriendManager().isFriend(target) != null) {
                continue;
            }

            if (!target.isEntityAlive()) {
                continue;
            }

            if (closestTarget == null) {
                closestTarget = target;
                continue;
            }

            if (mc.player.getDistance(target) < mc.player.getDistance(closestTarget)) {
                closestTarget = target;
            }
        }
        return closestTarget;
    }

    private static enum Cage {
        Web(new Vec3d[]{
                new Vec3d(0, 1, 0),
        });

        public final Vec3d[] offsets;

        Cage(Vec3d[] offsets) {
            this.offsets = offsets;
        }
    }
}
