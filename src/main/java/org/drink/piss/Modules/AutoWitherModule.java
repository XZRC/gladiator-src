package org.drink.piss.Modules;

import me.rigamortis.seppuku.Seppuku;
import me.rigamortis.seppuku.api.module.Module;
import me.rigamortis.seppuku.api.value.Value;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Author: it's bella ¯\_(ツ)_/¯
 * Seppuku-ified by cats
 */

public class AutoWitherModule extends Module {
    public final Value<Double> placeRange = new Value<Double>("Range", new String[]{"Dist"}, "Distance to place at" ,4.5d, 0.0d, 10.0d, 0.1d);
    public final Value<Boolean> placeCloseToEnemy = new Value<Boolean>("PlaceCloseToEnemy", new String[]{"Enemy"}, "Enables or disables the placing near enemies feature.", true);
    public final Value<Boolean> debugMessages = new Value<Boolean>("Debug", new String[]{"Bug"}, "Shows debug info", false);
    public final Value<Boolean> fastMode = new Value<Boolean>("Disable", new String[]{"Dis", "Toggle"}, "Toggles the module after placement", true);


    public AutoWitherModule() {
        super("AutoWither", new String[]{"Wither"}, "Spawns a wither", "NONE", -1, ModuleType.WORLD);
    }

    private static final List<Block> blackList = Arrays.asList(
            Blocks.ENDER_CHEST,
            Blocks.CHEST,
            Blocks.TRAPPED_CHEST,
            Blocks.CRAFTING_TABLE,
            Blocks.ANVIL,
            Blocks.BREWING_STAND,
            Blocks.HOPPER,
            Blocks.DROPPER,
            Blocks.DISPENSER,
            Blocks.TRAPDOOR
    );

    private static final List<Block> shulkerList = Arrays.asList(
            Blocks.WHITE_SHULKER_BOX,
            Blocks.ORANGE_SHULKER_BOX,
            Blocks.MAGENTA_SHULKER_BOX,
            Blocks.LIGHT_BLUE_SHULKER_BOX,
            Blocks.YELLOW_SHULKER_BOX,
            Blocks.LIME_SHULKER_BOX,
            Blocks.PINK_SHULKER_BOX,
            Blocks.GRAY_SHULKER_BOX,
            Blocks.SILVER_SHULKER_BOX,
            Blocks.CYAN_SHULKER_BOX,
            Blocks.PURPLE_SHULKER_BOX,
            Blocks.BLUE_SHULKER_BOX,
            Blocks.BROWN_SHULKER_BOX,
            Blocks.GREEN_SHULKER_BOX,
            Blocks.RED_SHULKER_BOX,
            Blocks.BLACK_SHULKER_BOX
    );

    private static final DecimalFormat df = new DecimalFormat("#.#");

    private int swordSlot;
    private static boolean isSneaking;

    @Override
    public void onEnable() {

        final Minecraft mc = Minecraft.getMinecraft();

        df.setRoundingMode(RoundingMode.CEILING);

        int skullSlot = -1;
        int soulSandSlot = -1;
        swordSlot = mc.player.inventory.currentItem;

        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);

            if (stack == ItemStack.EMPTY) {
                continue;
            }

            if (stack.getItem() instanceof ItemBlock) {
                Block block = ((ItemBlock) stack.getItem()).getBlock();
                if (block == Blocks.SOUL_SAND) {
                    soulSandSlot = i;
                    continue;
                }
            }

            Item item = stack.getItem();
            
            if (item == Items.SKULL) {
                skullSlot = i;
            }

        }

        if (skullSlot == -1) {
            if (debugMessages.getValue()) {
                Seppuku.INSTANCE.logChat("[AutoWither] Wither Skull missing, disabling.");
            }
            this.toggle();
            return;
        }

        if (soulSandSlot == -1) {
            Seppuku.INSTANCE.logChat("no sand");
            return;
        }

        int range = (int) Math.ceil(this.placeRange.getValue());

        List<BlockPos> placeTargetList = getSphere(mc.player.getPosition(), range, range, false, true, 0);
        Map<BlockPos, Double> placeTargetMap = new HashMap<>();

        BlockPos placeTarget = null;
        boolean useRangeSorting = false;

        for (BlockPos placeTargetTest : placeTargetList) {
            for (Entity entity : mc.world.loadedEntityList) {

                if (!(entity instanceof EntityPlayer)) {
                    continue;
                }

                if (entity == mc.player) {
                    continue;
                }

                if (Seppuku.INSTANCE.getFriendManager().isFriend(entity) != null) {
                    continue;
                }

                if (isAreaPlaceable(placeTargetTest)) {
                    useRangeSorting = true;
                    double distanceToEntity = entity.getDistance(placeTargetTest.getX(), placeTargetTest.getY(), placeTargetTest.getZ());
                    // Add distance to Map Value of placeTarget Key
                    placeTargetMap.put(placeTargetTest, placeTargetMap.containsKey(placeTargetTest) ? placeTargetMap.get(placeTargetTest) + distanceToEntity : distanceToEntity);
                    useRangeSorting = true;
                }

            }
        }

        if (placeTargetMap.size() > 0) {

            placeTargetMap.forEach((k, v) -> {
                if (!isAreaPlaceable(k)) {
                    placeTargetMap.remove(k);
                }
            });

            if (placeTargetMap.size() == 0) {
                useRangeSorting = false;
            }

        }

        if (useRangeSorting) {

            if (placeCloseToEnemy.getValue()) {
                if (debugMessages.getValue()) {
                    Seppuku.INSTANCE.logChat("[AutoWither] Placing close to Enemy");
                }
                // Get Key with lowest Value (closest to enemies)
                placeTarget = Collections.min(placeTargetMap.entrySet(), Map.Entry.comparingByValue()).getKey();
            } else {
                if (debugMessages.getValue()) {
                    Seppuku.INSTANCE.logChat("[AutoWither] Placing far from Enemy");
                }
                // Get Key with highest Value (furthest away from enemies)
                placeTarget = Collections.max(placeTargetMap.entrySet(), Map.Entry.comparingByValue()).getKey();
            }

        } else {

            if (debugMessages.getValue()) {
                Seppuku.INSTANCE.logChat("[AutoWither] No enemy nearby, placing at first valid position.");
            }

            // Use any place target position if no enemies are around
            for (BlockPos pos : placeTargetList) {
                if (isAreaPlaceable(pos)) {
                    placeTarget = pos;
                    break;
                }
            }

        }

        if (placeTarget == null) {
            if (debugMessages.getValue()) {
                Seppuku.INSTANCE.logChat("[AutoWither] No valid position in range to place!");
            }
            this.toggle();
            return;
        }

        if (debugMessages.getValue()) {
            Seppuku.INSTANCE.logChat("[AutoWither] Place Target: " + placeTarget.getX() + " " + placeTarget.getY() + " " + placeTarget.getZ() + " Distance: " + df.format(mc.player.getPositionVector().distanceTo(new Vec3d(placeTarget))));
        }

        mc.player.inventory.currentItem = soulSandSlot;
        placeBlock(new BlockPos(placeTarget.add(0, 0, 0)));
        placeBlock(new BlockPos(placeTarget.add(0, 1, 0)));
        placeBlock(new BlockPos(placeTarget.add(-1, 1, 0)));
        placeBlock(new BlockPos(placeTarget.add(1, 1, 0)));

        mc.player.inventory.currentItem = skullSlot;
        placeBlock(new BlockPos(placeTarget.add(0, 2, 0)));
        placeBlock(new BlockPos(placeTarget.add(-1, 2, 0)));
        placeBlock(new BlockPos(placeTarget.add(1, 2, 0)));

        if (isSneaking) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            isSneaking = false;
        }
        mc.player.inventory.currentItem = swordSlot;

        if (fastMode.getValue()) {
            this.toggle();
            return;
        }
    }

    @Listener
    public void onUpdate() {
        final Minecraft mc = Minecraft.getMinecraft();
        if (mc.player == null) {
            return;
        }

        if (!(mc.currentScreen instanceof GuiContainer)) {
            return;
        }

        if (swordSlot == -1) {
            return;
        }
    }

    private boolean isAreaPlaceable(BlockPos blockPos) {
        final Minecraft mc = Minecraft.getMinecraft();
        for (Entity entity : mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(blockPos))) {
            if (entity instanceof EntityLivingBase) {
                return false; // entity on block
            }
        }

        if (!mc.world.getBlockState(blockPos).getMaterial().isReplaceable()) {
            return false; // no space for hopper
        }

        if (!mc.world.getBlockState(blockPos.add(0, 1, 0)).getMaterial().isReplaceable()) {
            return false; // no space for shulker
        }

        if (mc.world.getBlockState(blockPos.add(0, -1, 0)).getBlock() instanceof BlockAir) {
            return false; // air below hopper
        }

        if (mc.world.getBlockState(blockPos.add(0, -1, 0)).getBlock() instanceof BlockLiquid) {
            return false; // liquid below hopper
        }

        if (mc.player.getPositionVector().distanceTo(new Vec3d(blockPos)) > (double) placeRange.getValue()) {
            return false; // out of range
        }

        Block block = mc.world.getBlockState(blockPos.add(0, -1, 0)).getBlock();
        if (blackList.contains(block) || shulkerList.contains(block)) {
            return false; // would need sneak
        }

        return !(mc.player.getPositionVector().distanceTo(new Vec3d(blockPos).add(0, 1, 0)) > (double) placeRange.getValue()); // out of range

    }

    private static void placeBlock(BlockPos pos) {
        final Minecraft mc = Minecraft.getMinecraft();

        if (!mc.world.getBlockState(pos).getMaterial().isReplaceable()) {
            return;
        }

        // check if we have a block adjacent to blockpos to click at
        if (!checkForNeighbours(pos)) {
            return;
        }

        for (EnumFacing side : EnumFacing.values()) {

            BlockPos neighbor = pos.offset(side);
            EnumFacing side2 = side.getOpposite();

            if (!mc.world.getBlockState(neighbor).getBlock().canCollideCheck(mc.world.getBlockState(neighbor), false)) {
                continue;
            }

            Vec3d hitVec = new Vec3d(neighbor).add(0.5, 0.5, 0.5).add(new Vec3d(side2.getDirectionVec()).scale(0.5));

            Block neighborPos = mc.world.getBlockState(neighbor).getBlock();
            if (blackList.contains(neighborPos) || shulkerList.contains(neighborPos)) {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
                isSneaking = true;
            }

            mc.playerController.processRightClickBlock(mc.player, mc.world, neighbor, side2, hitVec, EnumHand.MAIN_HAND);
            mc.player.swingArm(EnumHand.MAIN_HAND);
            mc.rightClickDelayTimer = 4;

            return;

        }

    }

    public List<BlockPos> getSphere(BlockPos loc, float r, int h, boolean hollow, boolean sphere, int plus_y) {
        List<BlockPos> circleblocks = new ArrayList<>();
        int cx = loc.getX();
        int cy = loc.getY();
        int cz = loc.getZ();
        for (int x = cx - (int) r; x <= cx + r; x++) {
            for (int z = cz - (int) r; z <= cz + r; z++) {
                for (int y = (sphere ? cy - (int) r : cy); y < (sphere ? cy + r : cy + h); y++) {
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
                    if (dist < r * r && !(hollow && dist < (r - 1) * (r - 1))) {
                        BlockPos l = new BlockPos(x, y + plus_y, z);
                        circleblocks.add(l);
                    }
                }
            }
        }
        return circleblocks;
    }

    private static boolean checkForNeighbours(BlockPos blockPos) {
        if (!hasNeighbour(blockPos)) {
            for (EnumFacing side : EnumFacing.values()) {
                BlockPos neighbour = blockPos.offset(side);
                if (hasNeighbour(neighbour)) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    private static boolean hasNeighbour(BlockPos blockPos) {
        final Minecraft mc = Minecraft.getMinecraft();
        for (EnumFacing side : EnumFacing.values()) {
            BlockPos neighbour = blockPos.offset(side);
            if (!mc.world.getBlockState(neighbour).getMaterial().isReplaceable()) {
                return true;
            }
        }
        return false;
    }
}

