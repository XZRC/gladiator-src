package club.megyn.megyndotclub.modules;

import me.rigamortis.seppuku.api.event.EventStageable.EventStage;
import me.rigamortis.seppuku.api.event.player.EventPlayerUpdate;
import me.rigamortis.seppuku.api.module.Module;
import me.rigamortis.seppuku.api.module.Module.ModuleType;
import me.rigamortis.seppuku.api.util.Timer;
import me.rigamortis.seppuku.api.value.Value;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketEntityAction.Action;
import net.minecraft.network.play.client.CPacketPlayer.Rotation;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

public class AnvilAuraModule extends Module {
    public final Value<Float> range = new Value("Range", new String[]{"Dist"}, "The minimum range to attack crystals.", 4.5F, 0.0F, 5.0F, 0.1F);
    public final Value<Float> delay = new Value("Delay", new String[]{"Del"}, "The delay to place in milliseconds.", 50.0F, 0.0F, 1000.0F, 1.0F);
    public final Value<Boolean> floorOnly = new Value("FloorOnly", new String[]{"fo"}, "If the module should only place anvils on top of blocks.", true);
    public final Value<Boolean> look = new Value("look", new String[]{"l"}, "If the module should look where it's placing.", true);
    private Timer placeTimer = new Timer();

    public AnvilAuraModule() {
        super("AnvilAura", new String[]{"Anvil", "aa", "aaura"}, "NONE", -1, ModuleType.COMBAT);
    }

    @Listener
    public void onUpdate(EventPlayerUpdate e) {
        if (e.getStage() == EventStage.PRE) {
            Minecraft mc = Minecraft.getMinecraft();
            EntityPlayerSP player = Minecraft.getMinecraft().player;
            if (((ItemStack)player.inventory.mainInventory.get(player.inventory.currentItem)).getItem() == Item.REGISTRY.getObject(new ResourceLocation("anvil")) && this.placeTimer.passed((double)(Float)this.delay.getValue())) {
                float radius = (Float)this.range.getValue();
                boolean hasPlaced = false;

                for(float x = radius; x >= -radius && !hasPlaced; --x) {
                    for(float y = radius; y >= -radius && !hasPlaced; --y) {
                        for(float z = radius; z >= -radius && !hasPlaced; --z) {
                            BlockPos blockPos = new BlockPos(mc.player.posX + (double)x, mc.player.posY + (double)y, mc.player.posZ + (double)z);
                            if (this.valid(blockPos)) {
                                this.placeBlock(blockPos);
                                hasPlaced = true;
                            }
                        }
                    }
                }

                this.placeTimer.reset();
            }
        }

    }

    private void placeBlock(BlockPos pos) {
        Minecraft mc = Minecraft.getMinecraft();
        float yaw = mc.player.rotationYaw;
        float pitch = mc.player.rotationPitch;
        if ((Boolean)this.look.getValue()) {
            lookAt(pos);
        }

        Block north = mc.world.getBlockState(pos.add(0, 0, -1)).getBlock();
        Block south = mc.world.getBlockState(pos.add(0, 0, 1)).getBlock();
        Block east = mc.world.getBlockState(pos.add(1, 0, 0)).getBlock();
        Block west = mc.world.getBlockState(pos.add(-1, 0, 0)).getBlock();
        Block up = mc.world.getBlockState(pos.add(0, 1, 0)).getBlock();
        Block down = mc.world.getBlockState(pos.add(0, -1, 0)).getBlock();
        boolean activated;
        if (up != null && up != Blocks.AIR && !(up instanceof BlockLiquid)) {
            activated = up.onBlockActivated(mc.world, pos, mc.world.getBlockState(pos), mc.player, EnumHand.MAIN_HAND, EnumFacing.DOWN, 0.0F, 0.0F, 0.0F);
            if (activated) {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, Action.START_SNEAKING));
            }

            if (mc.playerController.processRightClickBlock(mc.player, mc.world, pos.add(0, 1, 0), EnumFacing.DOWN, new Vec3d(0.0D, 0.0D, 0.0D), EnumHand.MAIN_HAND) != EnumActionResult.FAIL) {
                mc.player.swingArm(EnumHand.MAIN_HAND);
            }

            if (activated) {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, Action.STOP_SNEAKING));
            }
        }

        if (down != null && down != Blocks.AIR && !(down instanceof BlockLiquid)) {
            activated = down.onBlockActivated(mc.world, pos, mc.world.getBlockState(pos), mc.player, EnumHand.MAIN_HAND, EnumFacing.UP, 0.0F, 0.0F, 0.0F);
            if (activated) {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, Action.START_SNEAKING));
            }

            if (mc.playerController.processRightClickBlock(mc.player, mc.world, pos.add(0, -1, 0), EnumFacing.UP, new Vec3d(0.0D, 0.0D, 0.0D), EnumHand.MAIN_HAND) != EnumActionResult.FAIL) {
                mc.player.swingArm(EnumHand.MAIN_HAND);
            }

            if (activated) {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, Action.STOP_SNEAKING));
            }
        }

        if (north != null && north != Blocks.AIR && !(north instanceof BlockLiquid)) {
            activated = north.onBlockActivated(mc.world, pos, mc.world.getBlockState(pos), mc.player, EnumHand.MAIN_HAND, EnumFacing.UP, 0.0F, 0.0F, 0.0F);
            if (activated) {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, Action.START_SNEAKING));
            }

            if (mc.playerController.processRightClickBlock(mc.player, mc.world, pos.add(0, 0, -1), EnumFacing.SOUTH, new Vec3d(0.0D, 0.0D, 0.0D), EnumHand.MAIN_HAND) != EnumActionResult.FAIL) {
                mc.player.swingArm(EnumHand.MAIN_HAND);
            }

            if (activated) {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, Action.STOP_SNEAKING));
            }
        }

        if (south != null && south != Blocks.AIR && !(south instanceof BlockLiquid)) {
            activated = south.onBlockActivated(mc.world, pos, mc.world.getBlockState(pos), mc.player, EnumHand.MAIN_HAND, EnumFacing.UP, 0.0F, 0.0F, 0.0F);
            if (activated) {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, Action.START_SNEAKING));
            }

            if (mc.playerController.processRightClickBlock(mc.player, mc.world, pos.add(0, 0, 1), EnumFacing.NORTH, new Vec3d(0.0D, 0.0D, 0.0D), EnumHand.MAIN_HAND) != EnumActionResult.FAIL) {
                mc.player.swingArm(EnumHand.MAIN_HAND);
            }

            if (activated) {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, Action.STOP_SNEAKING));
            }
        }

        if (east != null && east != Blocks.AIR && !(east instanceof BlockLiquid)) {
            activated = east.onBlockActivated(mc.world, pos, mc.world.getBlockState(pos), mc.player, EnumHand.MAIN_HAND, EnumFacing.UP, 0.0F, 0.0F, 0.0F);
            if (activated) {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, Action.START_SNEAKING));
            }

            if (mc.playerController.processRightClickBlock(mc.player, mc.world, pos.add(1, 0, 0), EnumFacing.WEST, new Vec3d(0.0D, 0.0D, 0.0D), EnumHand.MAIN_HAND) != EnumActionResult.FAIL) {
                mc.player.swingArm(EnumHand.MAIN_HAND);
            }

            if (activated) {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, Action.STOP_SNEAKING));
            }
        }

        if (west != null && west != Blocks.AIR && !(west instanceof BlockLiquid)) {
            activated = west.onBlockActivated(mc.world, pos, mc.world.getBlockState(pos), mc.player, EnumHand.MAIN_HAND, EnumFacing.UP, 0.0F, 0.0F, 0.0F);
            if (activated) {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, Action.START_SNEAKING));
            }

            if (mc.playerController.processRightClickBlock(mc.player, mc.world, pos.add(-1, 0, 0), EnumFacing.EAST, new Vec3d(0.0D, 0.0D, 0.0D), EnumHand.MAIN_HAND) != EnumActionResult.FAIL) {
                mc.player.swingArm(EnumHand.MAIN_HAND);
            }

            if (activated) {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, Action.STOP_SNEAKING));
            }
        }

        if ((Boolean)this.look.getValue()) {
            mc.player.connection.sendPacket(new Rotation(yaw, pitch, mc.player.onGround));
        }

    }

    private boolean valid(BlockPos pos) {
        if (!(Boolean)this.floorOnly.getValue() || Minecraft.getMinecraft().world.getBlockState(pos.down()).getBlock() != Blocks.AIR && Minecraft.getMinecraft().world.getBlockState(pos.down()).getBlock() != Blocks.ANVIL) {
            return !Minecraft.getMinecraft().world.checkNoEntityCollision(new AxisAlignedBB(pos)) ? false : Minecraft.getMinecraft().world.getBlockState(pos).getBlock().isReplaceable(Minecraft.getMinecraft().world, pos);
        } else {
            return false;
        }
    }

    public static void lookAt(BlockPos pos) {
        pos = pos.down();
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        double dirx = player.posX - (double)pos.getX();
        double diry = player.posY - (double)pos.getY();
        double dirz = player.posZ - (double)pos.getZ();
        double len = Math.sqrt(dirx * dirx + diry * diry + dirz * dirz);
        dirx /= len;
        diry /= len;
        dirz /= len;
        double pitch = Math.asin(diry);
        double yaw = Math.atan2(dirz, dirx);
        pitch = pitch * 180.0D / 3.141592653589793D;
        yaw = yaw * 180.0D / 3.141592653589793D;
        yaw += 90.0D;
        player.connection.sendPacket(new Rotation((float)yaw, (float)pitch, player.onGround));
    }
}
