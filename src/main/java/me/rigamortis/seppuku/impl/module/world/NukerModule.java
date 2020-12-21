package me.rigamortis.seppuku.impl.module.world;

import me.rigamortis.seppuku.Seppuku;
import me.rigamortis.seppuku.api.event.EventStageable;
import me.rigamortis.seppuku.api.event.network.EventSendPacket;
import me.rigamortis.seppuku.api.event.player.EventPlayerDamageBlock;
import me.rigamortis.seppuku.api.event.player.EventRightClickBlock;
import me.rigamortis.seppuku.api.event.player.EventUpdateWalkingPlayer;
import me.rigamortis.seppuku.api.module.Module;
import me.rigamortis.seppuku.api.util.MathUtil;
import me.rigamortis.seppuku.api.value.Value;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

/**
 * Author Seth
 * 6/10/2019 @ 2:31 PM.
 */
public final class NukerModule extends Module {

    public final Value<Mode> mode = new Value<Mode>("Mode", new String[]{"Mode", "M"}, "The nuker mode to use.", Mode.SELECTION);
    public final Value<Boolean> autotool = new Value("Players", new String[]{"Player"}, "AutoTool Mode.", true);

    private enum Mode {
        SELECTION, ALL
    }

    public final Value<Float> distance = new Value<Float>("Distance", new String[]{"Dist", "D"}, "Maximum distance in blocks the nuker will reach.", 4.5f, 0.0f, 5.0f, 0.1f);

    private Block selected;
    private boolean send;

    public BlockPos position;
    public EnumFacing facing;
    public NukerModule() {
        super("Nuker", new String[]{"Nuke"}, "Automatically mines blocks within reach", "NONE", -1, ModuleType.WORLD);
    }

    @Override
    public void onToggle() {
        super.onToggle();
        this.selected = null;
    }

    @Override
    public String getMetaData() {
        return this.mode.getValue().name();
    }

    @Listener
    public void onWalkingUpdate(EventUpdateWalkingPlayer event) {
        if (event.getStage() == EventStageable.EventStage.PRE) {
            BlockPos pos = null;

            switch (this.mode.getValue()) {
                case SELECTION:
                    pos = this.getClosestBlockSelection();
                    break;
                case ALL:
                    pos = this.getClosestBlockAll();
                    break;
            }

            if (pos != null) {
                final Minecraft mc = Minecraft.getMinecraft();

                final float[] angle = MathUtil.calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), new Vec3d(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f));
                Seppuku.INSTANCE.getRotationManager().setPlayerRotations(angle[0], angle[1]);

                if (canBreak(pos)) {
                    mc.playerController.onPlayerDamageBlock(pos, mc.player.getHorizontalFacing());
                    mc.player.swingArm(EnumHand.MAIN_HAND);
                }
            }
        }
    }

    @Listener
    public void clickBlock(EventRightClickBlock event) {
        if (this.mode.getValue() == Mode.SELECTION) {
            final Block block = Minecraft.getMinecraft().world.getBlockState(event.getPos()).getBlock();
            if (block != null && block != this.selected) {
                this.selected = block;
                Seppuku.INSTANCE.logChat("Nuker block set to " + block.getLocalizedName());
                event.setCanceled(true);
            }
        }
    }

    private boolean canBreak(BlockPos pos) {
        final IBlockState blockState = Minecraft.getMinecraft().world.getBlockState(pos);
        final Block block = blockState.getBlock();

        return block.getBlockHardness(blockState, Minecraft.getMinecraft().world, pos) != -1;
    }

    private BlockPos getClosestBlockAll() {
        final Minecraft mc = Minecraft.getMinecraft();
        float maxDist = this.distance.getValue();

        BlockPos ret = null;

        for (float x = maxDist; x >= -maxDist; x--) {
            for (float y = maxDist; y >= -maxDist; y--) {
                for (float z = maxDist; z >= -maxDist; z--) {
                    final BlockPos pos = new BlockPos(mc.player.posX + x, mc.player.posY + y, mc.player.posZ + z);
                    final double dist = mc.player.getDistance(pos.getX(), pos.getY(), pos.getZ());
                    if (dist <= maxDist && (mc.world.getBlockState(pos).getBlock() != Blocks.AIR && !(mc.world.getBlockState(pos).getBlock() instanceof BlockLiquid)) && canBreak(pos)) {
                        if (pos.getY() >= mc.player.posY) {
                            maxDist = (float) dist;
                            ret = pos;
                        }
                    }
                }
            }
        }

        return ret;
    }

    private BlockPos getClosestBlockSelection() {
        final Minecraft mc = Minecraft.getMinecraft();
        float maxDist = this.distance.getValue();

        BlockPos ret = null;

        for (float x = maxDist; x >= -maxDist; x--) {
            for (float y = maxDist; y >= -maxDist; y--) {
                for (float z = maxDist; z >= -maxDist; z--) {
                    final BlockPos pos = new BlockPos(mc.player.posX + x, mc.player.posY + y, mc.player.posZ + z);
                    final double dist = mc.player.getDistance(pos.getX(), pos.getY(), pos.getZ());
                    if (dist <= maxDist && (mc.world.getBlockState(pos).getBlock() != Blocks.AIR && !(mc.world.getBlockState(pos).getBlock() instanceof BlockLiquid)) && mc.world.getBlockState(pos).getBlock() == this.selected && canBreak(pos)) {
                        if (pos.getY() >= mc.player.posY) {
                            maxDist = (float) dist;
                            ret = pos;
                        }
                    }
                }
            }
        }

        return ret;
    }
    private float blockStrength(BlockPos pos, ItemStack stack) {
        final Minecraft mc = Minecraft.getMinecraft();

        float hardness = mc.world.getBlockState(pos).getBlockHardness(mc.world, pos);

        if (hardness < 0.0F) {
            return 0.0F;
        }

        if (!canHarvestBlock(mc.world.getBlockState(pos).getBlock(), pos, stack)) {
            return getDigSpeed(mc.world.getBlockState(pos), pos, stack) / hardness / 100F;
        } else {
            return getDigSpeed(mc.world.getBlockState(pos), pos, stack) / hardness / 30F;
        }
    }

    private boolean canHarvestBlock(Block block, BlockPos pos, ItemStack stack) {
        final Minecraft mc = Minecraft.getMinecraft();
        IBlockState state = mc.world.getBlockState(pos);
        state = state.getBlock().getActualState(state, mc.world, pos);

        if (state.getMaterial().isToolNotRequired()) {
            return true;
        }

        String tool = block.getHarvestTool(state);

        if (stack.isEmpty() || tool == null) {
            return mc.player.canHarvestBlock(state);
        }

        final int toolLevel = stack.getItem().getHarvestLevel(stack, tool, mc.player, state);

        if (toolLevel < 0) {
            return mc.player.canHarvestBlock(state);
        }

        return toolLevel >= block.getHarvestLevel(state);
    }

    private float getDestroySpeed(IBlockState state, ItemStack stack) {
        float f = 1.0F;

        f *= stack.getDestroySpeed(state);

        return f;
    }

    private float getDigSpeed(IBlockState state, BlockPos pos, ItemStack stack) {
        final Minecraft mc = Minecraft.getMinecraft();
        float f = getDestroySpeed(state, stack);

        if (f > 1.0F) {
            int i = EnchantmentHelper.getEfficiencyModifier(mc.player);

            if (i > 0 && !stack.isEmpty()) {
                f += (float) (i * i + 1);
            }
        }

        if (mc.player.isPotionActive(MobEffects.HASTE)) {
            f *= 1.0F + (float) (mc.player.getActivePotionEffect(MobEffects.HASTE).getAmplifier() + 1) * 0.2F;
        }

        if (mc.player.isPotionActive(MobEffects.MINING_FATIGUE)) {
            float f1;

            switch (mc.player.getActivePotionEffect(MobEffects.MINING_FATIGUE).getAmplifier()) {
                case 0:
                    f1 = 0.3F;
                    break;
                case 1:
                    f1 = 0.09F;
                    break;
                case 2:
                    f1 = 0.0027F;
                    break;
                case 3:
                default:
                    f1 = 8.1E-4F;
            }

            f *= f1;
        }

        if (mc.player.isInsideOfMaterial(Material.WATER) && !EnchantmentHelper.getAquaAffinityModifier(mc.player)) {
            f /= 5.0F;
        }

        if (!mc.player.onGround) {
            f /= 5.0F;
        }

        f = net.minecraftforge.event.ForgeEventFactory.getBreakSpeed(mc.player, state, f, pos);
        return (f < 0 ? 0 : f);
    }

    @Listener
    public void damageBlock(EventPlayerDamageBlock event) {
        final Minecraft mc = Minecraft.getMinecraft();
        {
            final int slot = getToolInventory(event.getPos());
            if (slot != -1) {
                mc.playerController.curBlockDamageMP += blockStrength(event.getPos(), mc.player.inventoryContainer.getSlot(slot).getStack());
            } else {
                final int hotbar = getToolHotbar(event.getPos());
                if (hotbar != -1) {
                    mc.playerController.curBlockDamageMP += blockStrength(event.getPos(), mc.player.inventory.getStackInSlot(hotbar));
                }
            }
        } {
            final int slot = getToolHotbar(event.getPos());
            if (slot != -1) {
                mc.player.inventory.currentItem = slot;
                mc.playerController.updateController();
            }
        }
    }

    @Listener
    public void sendPacket(EventSendPacket event) {
        if (event.getStage() == EventStageable.EventStage.PRE) {
            if (this.send) {
                this.send = false;
                return;
            }
            {
                final CPacketPlayerDigging packet = (CPacketPlayerDigging) event.getPacket();
                if (packet.getAction() == CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK) {
                    this.position = packet.getPosition();
                    this.facing = packet.getFacing();

                    if (this.position != null && this.facing != null) {
                        final int slot = getToolInventory(packet.getPosition());
                        if (slot != -1) {
                            event.setCanceled(true);
                            Minecraft.getMinecraft().playerController.windowClick(Minecraft.getMinecraft().player.inventoryContainer.windowId, slot, Minecraft.getMinecraft().player.inventory.currentItem, ClickType.SWAP, Minecraft.getMinecraft().player);
                            send = true;
                            Minecraft.getMinecraft().player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, this.position, this.facing));
                            Minecraft.getMinecraft().playerController.windowClick(Minecraft.getMinecraft().player.inventoryContainer.windowId, slot, Minecraft.getMinecraft().player.inventory.currentItem, ClickType.SWAP, Minecraft.getMinecraft().player);
                        } else {
                            final int hotbar = getToolHotbar(packet.getPosition());
                            if (hotbar != -1) {
                                event.setCanceled(true);
                                Minecraft.getMinecraft().playerController.windowClick(Minecraft.getMinecraft().player.inventoryContainer.windowId, hotbar, Minecraft.getMinecraft().player.inventory.currentItem, ClickType.SWAP, Minecraft.getMinecraft().player);
                                send = true;
                                Minecraft.getMinecraft().player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, this.position, this.facing));
                                Minecraft.getMinecraft().playerController.windowClick(Minecraft.getMinecraft().player.inventoryContainer.windowId, hotbar, Minecraft.getMinecraft().player.inventory.currentItem, ClickType.SWAP, Minecraft.getMinecraft().player);
                            }
                        }
                    }
                }
            }
        }
    }

    private int getToolInventory(BlockPos pos) {
        int index = -1;

        float speed = 1.0f;

        for (int i = 9; i < 36; i++) {
            final ItemStack stack = Minecraft.getMinecraft().player.inventoryContainer.getSlot(i).getStack();
            if (stack != null && stack != ItemStack.EMPTY) {
                final float digSpeed = EnchantmentHelper.getEnchantmentLevel(Enchantments.EFFICIENCY, stack);
                final float destroySpeed = stack.getDestroySpeed(Minecraft.getMinecraft().world.getBlockState(pos));

                if ((digSpeed + destroySpeed) > speed) {
                    speed = (digSpeed + destroySpeed);
                    index = i;
                }
            }
        }

        return index;
    }

    private int getToolHotbar(BlockPos pos) {
        int index = -1;

        float speed = 1.0f;

        for (int i = 0; i <= 9; i++) {
            final ItemStack stack = Minecraft.getMinecraft().player.inventory.getStackInSlot(i);
            if (stack != null && stack != ItemStack.EMPTY) {
                final float digSpeed = EnchantmentHelper.getEnchantmentLevel(Enchantments.EFFICIENCY, stack);
                final float destroySpeed = stack.getDestroySpeed(Minecraft.getMinecraft().world.getBlockState(pos));

                if ((digSpeed + destroySpeed) > speed) {
                    speed = (digSpeed + destroySpeed);
                    index = i;
                }
            }
        }

        return index;
    }

}


