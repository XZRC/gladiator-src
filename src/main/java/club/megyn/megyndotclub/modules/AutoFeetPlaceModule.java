package club.megyn.megyndotclub.modules;

import me.rigamortis.seppuku.Seppuku;
import me.rigamortis.seppuku.api.event.EventStageable;
import me.rigamortis.seppuku.api.event.player.EventUpdateWalkingPlayer;
import me.rigamortis.seppuku.api.module.Module;
import me.rigamortis.seppuku.api.value.Value;
import me.rigamortis.seppuku.api.util.BlockUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.block.Block;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketEntityAction.Action;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayer.Rotation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;
import me.rigamortis.seppuku.api.event.player.EventUpdateWalkingPlayer;

import static me.rigamortis.seppuku.api.util.BlockUtil.*;

/**
 * Created 13 August 2019 by hub
 * Updated 29 November 2019 by hub
 */

public class AutoFeetPlaceModule extends Module {

    private final Vec3d[] surroundTargets = new Vec3d[]{new Vec3d(0.0D, 0.0D, 0.0D), new Vec3d(1.0D, 1.0D, 0.0D), new Vec3d(0.0D, 1.0D, 1.0D), new Vec3d(-1.0D, 1.0D, 0.0D), new Vec3d(0.0D, 1.0D, -1.0D), new Vec3d(1.0D, 0.0D, 0.0D), new Vec3d(0.0D, 0.0D, 1.0D), new Vec3d(-1.0D, 0.0D, 0.0D), new Vec3d(0.0D, 0.0D, -1.0D), new Vec3d(1.0D, 1.0D, 0.0D), new Vec3d(0.0D, 1.0D, 1.0D), new Vec3d(-1.0D, 1.0D, 0.0D), new Vec3d(0.0D, 1.0D, -1.0D)};
    private final Vec3d[] surroundTargetsCritical = new Vec3d[]{new Vec3d(0.0D, 0.0D, 0.0D), new Vec3d(1.0D, 1.0D, 0.0D), new Vec3d(0.0D, 1.0D, 1.0D), new Vec3d(-1.0D, 1.0D, 0.0D), new Vec3d(0.0D, 1.0D, -1.0D)};
    private Value<Boolean> toggleable = new Value<>("Toggleable", new String[]{"Toggleable"}, "guess", true);
    private Value<Boolean> spoofRotations = new Value<>("Rotate", new String[]{"Rotate"}, "GUESS WHAT THIS ONE DOES BUCKAROO", true);
    private Value<Boolean> autoCenter = new Value<>("AutoCenter", new String[]{"AutoCenter"}, "Teleports you to the center of a block.", true);

    private Value<Boolean> spoofHotbar = new Value<>("SpoofHotbar", new String[]{"SpoofHotbar"}, "Spoofs your hotbar", false);
    private Value<Integer> blockPerTick = new Value<>("BlocksPerTick", new String[]{"BlocksPerTick"}, "blocks per tick duh you retard", 4, 1, 9, 1);
    private Value<Boolean> debugMessages = new Value<>("DebugMessages", new String[]{"DebugMessages"}, "Sends debug messages.", false);
    private BlockPos basePos;
    private int offsetStep = 0;
    private int playerHotbarSlot = -1;
    private int lastHotbarSlot = -1;
    private Vec3d playerPos;

    private static final Minecraft mc = Minecraft.getMinecraft();

    public AutoFeetPlaceModule() {
        super("AutoFeetPlace", new String[]{"AutoFeetPlace"}, "Place blocks at your feet what did you think it does retard", "NONE", -1, ModuleType.COMBAT);
    }

    @Listener
    public void onWalkingUpdate(EventUpdateWalkingPlayer event)  {
        if (event.getStage() == EventStageable.EventStage.PRE) {
            if (this.offsetStep == 0) {
                this.basePos = (new BlockPos(mc.player.getPositionVector())).down();
                this.playerHotbarSlot = mc.player.inventory.currentItem;
                if (this.debugMessages.getValue() && mc.world != null) {
                    Seppuku.INSTANCE.logChat("[AutoFeetPlace] Starting Loop, current Player Slot: " + this.playerHotbarSlot);
                }

                if (!(Boolean)this.spoofHotbar.getValue()) {
                    this.lastHotbarSlot = mc.player.inventory.currentItem;
                }
            }

            for(int i = 0; i < (int)Math.floor(this.blockPerTick.getValue()); ++i) {
                if (this.debugMessages.getValue() && mc.world != null) {
                    Seppuku.INSTANCE.logChat("[AutoFeetPlace] Loop iteration: " + this.offsetStep);
                }

                if (this.offsetStep >= this.surroundTargets.length) {
                    this.endLoop();
                    return;
                }

                Vec3d offset = this.surroundTargets[this.offsetStep];
                this.placeBlock(new BlockPos(this.basePos.add(offset.x, offset.y, offset.z)));
                ++this.offsetStep;
            }

        }
    }
    private void centerPlayer(double x, double y, double z) {
         {

        }


        mc.player.connection.sendPacket(new CPacketPlayer.Position(x, y, z, true));
        mc.player.setPosition(x, y, z);
    }
    double getDst(Vec3d vec) {
        return playerPos.distanceTo(vec);
    }
    public void onEnable() {
        super.onEnable();

        if (mc.player == null) return;
        /* Autocenter */
        BlockPos centerPos = mc.player.getPosition();
        playerPos = mc.player.getPositionVector();
        double y = centerPos.getY();
        double x = centerPos.getX();
        double z = centerPos.getZ();

        final Vec3d plusPlus = new Vec3d(x + 0.5, y, z + 0.5);
        final Vec3d plusMinus = new Vec3d(x + 0.5, y, z - 0.5);
        final Vec3d minusMinus = new Vec3d(x - 0.5, y, z - 0.5);
        final Vec3d minusPlus = new Vec3d(x - 0.5, y, z + 0.5);

        if (autoCenter.getValue()) {
            if (getDst(plusPlus) < getDst(plusMinus) && getDst(plusPlus) < getDst(minusMinus) && getDst(plusPlus) < getDst(minusPlus)) {
                x = centerPos.getX() + 0.5;
                z = centerPos.getZ() + 0.5;
                centerPlayer(x, y, z);
            } if (getDst(plusMinus) < getDst(plusPlus) && getDst(plusMinus) < getDst(minusMinus) && getDst(plusMinus) < getDst(minusPlus)) {
                x = centerPos.getX() + 0.5;
                z = centerPos.getZ() - 0.5;
                centerPlayer(x, y, z);
            } if (getDst(minusMinus) < getDst(plusPlus) && getDst(minusMinus) < getDst(plusMinus) && getDst(minusMinus) < getDst(minusPlus)) {
                x = centerPos.getX() - 0.5;
                z = centerPos.getZ() - 0.5;
                centerPlayer(x, y, z);
            } if (getDst(minusPlus) < getDst(plusPlus) && getDst(minusPlus) < getDst(plusMinus) && getDst(minusPlus) < getDst(minusMinus)) {
                x = centerPos.getX() - 0.5;
                z = centerPos.getZ() + 0.5;
                centerPlayer(x, y, z);
            }
        }
        /* End of Autocenter*/

        if (mc.player == null) {
            this.onDisable();
        } else {
            if (this.debugMessages.getValue() && mc.world != null) {
                Seppuku.INSTANCE.logChat("[AutoFeetPlace] Enabling");
            }

            this.playerHotbarSlot = mc.player.inventory.currentItem;
            this.lastHotbarSlot = -1;
            if (this.debugMessages.getValue() && mc.world != null) {
                Seppuku.INSTANCE.logChat("[AutoFeetPlace] Saving initial Slot  = " + this.playerHotbarSlot);
            }

        }
    }

    public void onDisable() {
        super.onDisable();
        if (mc.player != null) {
            if (this.debugMessages.getValue() && mc.world != null) {
                Seppuku.INSTANCE.logChat("[AutoFeetPlace] Disabling");
            }

            if (this.lastHotbarSlot != this.playerHotbarSlot && this.playerHotbarSlot != -1) {
                if (this.debugMessages.getValue() && mc.world != null) {
                    Seppuku.INSTANCE.logChat("[AutoFeetPlace] Setting Slot to  = " + this.playerHotbarSlot);
                }

                if ((Boolean)this.spoofHotbar.getValue()) {
                    mc.player.connection.sendPacket(new CPacketHeldItemChange(this.playerHotbarSlot));
                } else {
                    mc.player.inventory.currentItem = this.playerHotbarSlot;
                }
            }

            this.playerHotbarSlot = -1;
            this.lastHotbarSlot = -1;
        }
    }

    private void endLoop() {
        this.offsetStep = 0;
        if (this.debugMessages.getValue() && mc.world != null) {
            Seppuku.INSTANCE.logChat("[AutoFeetPlace] Ending Loop");
        }

        if (this.lastHotbarSlot != this.playerHotbarSlot && this.playerHotbarSlot != -1) {
            if (this.debugMessages.getValue() && mc.world != null) {
                Seppuku.INSTANCE.logChat("[AutoFeetPlace] Setting Slot back to  = " + this.playerHotbarSlot);
            }

            if ((Boolean)this.spoofHotbar.getValue()) {
                mc.player.connection.sendPacket(new CPacketHeldItemChange(this.playerHotbarSlot));
            } else {
                mc.player.inventory.currentItem = this.playerHotbarSlot;
            }

            this.lastHotbarSlot = this.playerHotbarSlot;
        }

        if (!(Boolean)this.toggleable.getValue()) {
            this.onDisable();
        }

    }

    private void placeBlock(BlockPos blockPos) {
        if (!mc.world.getBlockState(blockPos).getMaterial().isReplaceable()) {
            if (this.debugMessages.getValue() && mc.world != null) {
                Seppuku.INSTANCE.logChat("[AutoFeetPlace] Block is already placed, skipping");
            }

        } else if (!BlockUtil.checkForNeighbours(blockPos)) {
            if (this.debugMessages.getValue() && mc.world != null) {
                Seppuku.INSTANCE.logChat("[AutoFeetPlace] !checkForNeighbours(blockPos), disabling! ");
            }

        } else {
            this.placeBlockExecute(blockPos);
        }
    }

    private int findObiInHotbar() {
        int slot = -1;

        for(int i = 0; i < 9; ++i) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack != ItemStack.EMPTY && stack.getItem() instanceof ItemBlock) {
                Block block = ((ItemBlock)stack.getItem()).getBlock();
                if (block instanceof BlockObsidian) {
                    slot = i;
                    break;
                }
            }
        }

        return slot;
    }

    public void placeBlockExecute(BlockPos pos) {
        Vec3d eyesPos = new Vec3d(mc.player.posX, mc.player.posY + (double)mc.player.getEyeHeight(), mc.player.posZ);
        EnumFacing[] var3 = EnumFacing.values();
        int var4 = var3.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            EnumFacing side = var3[var5];
            BlockPos neighbor = pos.offset(side);
            EnumFacing side2 = side.getOpposite();
            if (!canBeClicked(neighbor)) {
                if (this.debugMessages.getValue() && mc.world != null) {
                    Seppuku.INSTANCE.logChat("[AutoFeetPlace] No neighbor to click at!");
                }
            } else {
                Vec3d hitVec = (new Vec3d(neighbor)).add(0.5D, 0.5D, 0.5D).add((new Vec3d(side2.getDirectionVec())).scale(0.5D));
                if (eyesPos.squareDistanceTo(hitVec) <= 18.0625D) {
                    if ((Boolean)this.spoofRotations.getValue()) {
                        faceVectorPacketInstant(hitVec);
                    }

                    boolean needSneak = false;
                    Block blockBelow = mc.world.getBlockState(neighbor).getBlock();
                    if (BlockUtil.shiftAgainst.contains(blockBelow)) {
                        if (this.debugMessages.getValue() && mc.world != null) {
                            Seppuku.INSTANCE.logChat("[AutoFeetPlace] Sneak enabled!");
                        }

                        needSneak = true;
                    }

                    if (needSneak) {
                        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, Action.START_SNEAKING));
                    }

                    int obiSlot = this.findObiInHotbar();
                    if (obiSlot == -1) {
                        if (this.debugMessages.getValue() && mc.world != null) {
                            Seppuku.INSTANCE.logChat("[AutoFeetPlace] No Obi in Hotbar, disabling!");
                        }

                        this.onDisable();
                        return;
                    }

                    if (this.lastHotbarSlot != obiSlot) {
                        if (this.debugMessages.getValue() && mc.world != null) {
                            Seppuku.INSTANCE.logChat("[AutoFeetPlace] Setting Slot to Obi at  = " + obiSlot);
                        }

                        if ((Boolean)this.spoofHotbar.getValue()) {
                            mc.player.connection.sendPacket(new CPacketHeldItemChange(obiSlot));
                        } else {
                            mc.player.inventory.currentItem = obiSlot;
                        }

                        this.lastHotbarSlot = obiSlot;
                    }

                    mc.playerController.processRightClickBlock(mc.player, mc.world, neighbor, side2, hitVec, EnumHand.MAIN_HAND);
                    mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
                    if (needSneak) {
                        if (this.debugMessages.getValue() && mc.world != null) {
                            Seppuku.INSTANCE.logChat("[AutoFeetPlace] Sneak disabled!");
                        }

                        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, Action.STOP_SNEAKING));
                    }

                    return;
                }

                if (this.debugMessages.getValue() && mc.world != null) {
                    Seppuku.INSTANCE.logChat("[AutoFeetPlace] Distance > 4.25 blocks!");
                }
            }
        }

    }

    private static boolean canBeClicked(BlockPos pos) {
        return getBlock(pos).canCollideCheck(getState(pos), false);
    }

    private static Block getBlock(BlockPos pos) {
        return getState(pos).getBlock();
    }

    private static IBlockState getState(BlockPos pos) {
        return mc.world.getBlockState(pos);
    }

    private static void faceVectorPacketInstant(Vec3d vec) {
        float[] rotations = getLegitRotations(vec);
        mc.player.connection.sendPacket(new Rotation(rotations[0], rotations[1], mc.player.onGround));
    }

    private static float[] getLegitRotations(Vec3d vec) {
        Vec3d eyesPos = getEyesPos();
        double diffX = vec.x - eyesPos.x;
        double diffY = vec.y - eyesPos.y;
        double diffZ = vec.z - eyesPos.z;
        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0F;
        float pitch = (float)(-Math.toDegrees(Math.atan2(diffY, diffXZ)));
        return new float[]{mc.player.rotationYaw + MathHelper.wrapDegrees(yaw - mc.player.rotationYaw), mc.player.rotationPitch + MathHelper.wrapDegrees(pitch - mc.player.rotationPitch)};
    }

    private static Vec3d getEyesPos() {
        return new Vec3d(mc.player.posX, mc.player.posY + (double)mc.player.getEyeHeight(), mc.player.posZ);
    }
}
