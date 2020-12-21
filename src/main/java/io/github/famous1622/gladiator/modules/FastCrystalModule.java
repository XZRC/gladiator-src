package io.github.famous1622.gladiator.modules;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import me.rigamortis.seppuku.Seppuku;
import me.rigamortis.seppuku.api.event.EventStageable;
import me.rigamortis.seppuku.api.event.network.EventSendPacket;
import me.rigamortis.seppuku.api.event.player.EventUpdateWalkingPlayer;
import me.rigamortis.seppuku.api.event.render.EventRender3D;
import me.rigamortis.seppuku.api.module.Module;
import me.rigamortis.seppuku.api.util.MathUtil;
import me.rigamortis.seppuku.api.value.Value;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.potion.Potion;
import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import team.stiff.pomelo.handler.ListenerPriority;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_LINE_SMOOTH;

/**
 * Author Memeszz_
 */

public class FastCrystalModule extends Module {
    private Value<Boolean> autoSwitch =  new Value<Boolean>("Auto Switch", new String[]{"switch"}, "ChatAlert", true);
    private Value<Boolean> players = new Value<Boolean>("Players", new String[]{"play"}, "ChatAlert", true);
    private Value<Boolean> place =  new Value<Boolean>("Place", new String[]{"place"}, "ChatAlert", true);
    private Value<Boolean> raytrace =  new Value<Boolean>("Raytrace", new String[]{"ray"}, "ChatAlert", true);
    private Value<Boolean> explode = new Value<Boolean>("Explode", new String[]{"exp"}, "ChatAlert", true);
    private Value<Boolean> thing = new Value<Boolean>("MultiPlace", new String[]{"mp"}, "ChatAlert", true);
    public Value<Double> range = new Value<>("Range", new String[]{"range"}, "ChatAlert", 6.0, 0.0, 6.0, 0.1);
    public Value<Boolean> antiWeakness = new Value<Boolean>("AntiWeakness", new String[]{"weak"}, "Use sharp thing hit crystal", true);
    public Value<Double> Pdelay = new Value<>("PlaceDelay", new String[]{"pdelay"}, "ChatAlert", 1.0, 0.0, 6.0, 0.1);
    public Value<Double> Bdelay = new Value<>("BreakDelay", new String[]{"bdelay"}, "ChatAlert", 1.0, 0.0, 6.0, 0.1);
    public Value<Double> distance = new Value<>("EnemyDistance", new String[]{"edist"}, "ChatAlert", 6.0, 0.0, 6.0, 0.1);
    public Value<Boolean> alert = new Value<Boolean>("ChatAlert", new String[]{"Alert"}, "ChatAlert", false);
    public Value<Boolean> verbose = new Value<Boolean>("Verbose", new String[]{"v"}, "ChatAlert", true);

    public final Value<Float> minDamage = new Value<>("Min_Damage", new String[]{"MinDamage", "Min", "MinDmg"}, "The minimum explosion damage calculated to place down a crystal.", 1.0f, 0.0f, 20.0f, 0.5f);
    private BlockPos render;
    private Entity renderEnt;
    private long systemTime = -1L;
    private static boolean togglePitch = false;
    private boolean switchCooldown = false;
    private boolean isAttacking = false;
    private int oldSlot = -1;
    private int placements;
    private static boolean isSpoofingAngles;
    private static double yaw;
    private static double pitch;
    private static Minecraft mc = Minecraft.getMinecraft();

    public FastCrystalModule() {
        super("TestCrystalAura", new String[]{"gloom"}, "TestAura ", "NONE", -1, ModuleType.COMBAT);
    }

    @Listener(priority = ListenerPriority.LOWEST)
    public void onSend(EventSendPacket event) {
        if (event.getStage() == EventStageable.EventStage.PRE) {
            Packet packet = event.getPacket();
            if (packet instanceof CPacketPlayer && isSpoofingAngles) {
                ((CPacketPlayer)packet).yaw = (float)yaw;
                ((CPacketPlayer)packet).pitch = (float)pitch;
            }
        }
    }

    private void debug(String message) {
        if (verbose.getValue()) {
            Seppuku.INSTANCE.logChat(message);
        }
    }
    @Listener
    public void onUpdate(EventUpdateWalkingPlayer event) {
        if (event.getStage() == EventStageable.EventStage.PRE) {
            debug("onUpdate");
            EntityEnderCrystal crystal = mc.world.loadedEntityList.stream().filter(entity -> entity instanceof EntityEnderCrystal).map(entity -> (EntityEnderCrystal) entity).min(Comparator.comparing(c -> mc.player.getDistance(c))).orElse(null);
            if (this.explode.getValue() && crystal != null && (double) mc.player.getDistance(crystal) <= this.range.getValue()) {

                debug("Crystal is valid");
                if ((double) (System.nanoTime() / 1000000L - this.systemTime) >= this.Bdelay.getValue()) {
                    debug("Break");
                    if (this.antiWeakness.getValue() && mc.player.isPotionActive(MobEffects.WEAKNESS)) {
                        debug("AntiWeakness");
                        if (!this.isAttacking) {
                            this.oldSlot = mc.player.inventory.currentItem;
                            this.isAttacking = true;
                        }
                        int newSlot = -1;
                        for (int i = 0; i < 9; ++i) {
                            ItemStack stack = mc.player.inventory.getStackInSlot(i);
                            if (stack == ItemStack.EMPTY) continue;
                            if (stack.getItem() instanceof ItemSword) {
                                newSlot = i;
                                break;
                            }
                            if (!(stack.getItem() instanceof ItemTool)) continue;
                            newSlot = i;
                            break;
                        }
                        if (newSlot != -1) {
                            mc.player.inventory.currentItem = newSlot;
                            this.switchCooldown = true;
                        }
                    }
                    this.lookAtPacket(crystal.posX, crystal.posY, crystal.posZ, (EntityPlayer) mc.player);
                    mc.playerController.attackEntity((EntityPlayer) mc.player, (Entity) crystal);
                    mc.player.swingArm(EnumHand.MAIN_HAND);
                    this.systemTime = System.nanoTime() / 1000000L;
                }
                if (!this.thing.getValue()) {
                    debug("Not placing");
                    return;
                }
                if (this.placements == 3) {
                    this.placements = 0;
                    return;
                }
            } else {
                resetRotation();
                if (this.oldSlot != -1) {
                    mc.player.inventory.currentItem = this.oldSlot;
                    this.oldSlot = -1;
                }
                this.isAttacking = false;
            }
            debug("Placing");
            int crystalSlot = mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL ? mc.player.inventory.currentItem : -1;
            if (crystalSlot == -1) {
                debug("Switching to crystals");
                for (int l = 0; l < 9; ++l) {
                    if (mc.player.inventory.getStackInSlot(l).getItem() != Items.END_CRYSTAL) continue;
                    crystalSlot = l;
                    break;
                }
            }
            boolean offhand = false;
            if (mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) {
                offhand = true;
                debug("In offhand");
            } else if (crystalSlot == -1) {
                debug("Can't find crystals");
                return;
            }
            List<BlockPos> blocks = this.findCrystalBlocks();
            ArrayList<Entity> entities = new ArrayList<>();
                entities.addAll(mc.world.playerEntities.stream()
                        .filter(entityPlayer -> Seppuku.INSTANCE.getFriendManager().isFriend(entityPlayer) != null)
                        .collect(Collectors.toList()));


            BlockPos q = null;
            double damage = 0.5;
            for (Entity entity2 : entities) {
                if (entity2 == mc.player || ((EntityLivingBase) entity2).getHealth() <= 0.0f) continue;
                for (BlockPos blockPos : blocks) {
                    double self;
                    double d;
                    double b = entity2.getDistanceSq(blockPos);
                    if (b >= this.distance.getValue() * this.distance.getValue() ||
                            !((d = calculateDamage((double) blockPos.getX() + 0.5, blockPos.getY() + 1, (double) blockPos.getZ() + 0.5, entity2)) > damage) ||
                            (self = calculateDamage((double) blockPos.getX() + 0.5, blockPos.getY() + 1, (double) blockPos.getZ() + 0.5, mc.player)) > d && !(d < (double) ((EntityLivingBase) entity2).getHealth()) || self - 0.5 > (double) mc.player.getHealth() || !(d >= (double) this.minDamage.getValue().intValue()))
                        continue;
                    damage = d;
                    q = blockPos;
                    this.renderEnt = entity2;
                }
            }
            if (damage == 0.5) {
                debug("damage == 0.5");
                this.render = null;
                this.renderEnt = null;
                resetRotation();
                return;
            }
            this.render = q;
            if (this.place.getValue()) {
                debug("Placing");
                RayTraceResult result;
                if (!offhand && mc.player.inventory.currentItem != crystalSlot) {
                    if (this.autoSwitch.getValue()) {
                        mc.player.inventory.currentItem = crystalSlot;
                        resetRotation();
                        this.switchCooldown = true;
                    }
                    return;
                }
                this.lookAtPacket((double) q.getX() + 0.5, (double) q.getY() - 0.5, (double) q.getZ() + 0.5, (EntityPlayer) mc.player);
                EnumFacing f = this.raytrace.getValue() ? ((result = mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + (double) mc.player.getEyeHeight(), mc.player.posZ), new Vec3d((double) q.getX() + 0.5, (double) q.getY() - 0.5, (double) q.getZ() + 0.5))) == null || result.sideHit == null ? EnumFacing.UP : result.sideHit) : EnumFacing.DOWN;
                mc.playerController.updateController();
                if (this.switchCooldown) {
                    debug("Switch cooldown");
                    this.switchCooldown = false;
                    return;
                }
                if ((double) (System.nanoTime() / 1000000L - this.systemTime) >= this.Pdelay.getValue()) {
                    debug("Placing packet");
                    mc.player.connection.sendPacket((Packet) new CPacketPlayerTryUseItemOnBlock(q, f, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.0f, 0.0f, 0.0f));
                    ++this.placements;
                    this.systemTime = System.nanoTime() / 1000000L;
                }
            }
            if (isSpoofingAngles) {
                if (togglePitch) {
                    mc.player.rotationPitch = (float) ((double) mc.player.rotationPitch + 4.0E-4);
                    togglePitch = false;
                } else {
                    mc.player.rotationPitch = (float) ((double) mc.player.rotationPitch - 4.0E-4);
                    togglePitch = true;
                }
            }
        }
    }

//    @Override
//    public void onWorldRender(RenderEvent event) {
//        if (this.render != null) {
//            KamiTessellator.prepare(7);
//            KamiTessellator.drawBox(this.render, this.Red.getValue(), this.Green.getValue(), this.Blue.getValue(), this.Alpha.getValue(), 63);
//            KamiTessellator.release();
//        }
//    }

    private ICamera camera = new Frustum();

//    @Listener
//    public void onRender(EventRender3D event) {
//        final Minecraft mc = Minecraft.getMinecraft();
//
//        final AxisAlignedBB bb = new AxisAlignedBB(
//                this.render.getX() - mc.getRenderManager().viewerPosX,
//                this.render.getY() - mc.getRenderManager().viewerPosY,
//                this.render.getZ() - mc.getRenderManager().viewerPosZ,
//                this.render.getX() + 1 - mc.getRenderManager().viewerPosX,
//                this.render.getY() + 1 - mc.getRenderManager().viewerPosY,
//                this.render.getZ() + 1 - mc.getRenderManager().viewerPosZ);
//
//        camera.setPosition(mc.getRenderViewEntity().posX, mc.getRenderViewEntity().posY, mc.getRenderViewEntity().posZ);
//
//        if (camera.isBoundingBoxInFrustum(new AxisAlignedBB(bb.minX + mc.getRenderManager().viewerPosX,
//                bb.minY + mc.getRenderManager().viewerPosY,
//                bb.minZ + mc.getRenderManager().viewerPosZ,
//                bb.maxX + mc.getRenderManager().viewerPosX,
//                bb.maxY + mc.getRenderManager().viewerPosY,
//                bb.maxZ + mc.getRenderManager().viewerPosZ))) {
//            GlStateManager.pushMatrix();
//            GlStateManager.enableBlend();
//            GlStateManager.disableDepth();
//            GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
//            GlStateManager.disableTexture2D();
//            GlStateManager.depthMask(false);
//            glEnable(GL_LINE_SMOOTH);
//            glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
//            glLineWidth(1.5f);
//            RenderGlobal.renderFilledBox(bb, 0, 1, 0, 0.25f);
//            RenderGlobal.drawBoundingBox(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ, 1, 0, 1, 0.25f);
//            glDisable(GL_LINE_SMOOTH);
//            GlStateManager.depthMask(true);
//            GlStateManager.enableDepth();
//            GlStateManager.enableTexture2D();
//            GlStateManager.disableBlend();
//            GlStateManager.popMatrix();
//
//        }
//    }

    private void lookAtPacket(double px, double py, double pz, EntityPlayer me) {
        debug(String.format("Looking at %.2f, %.2f, %.2f", px, py, pz));
        float[] v = MathUtil.calcAngle(me.getPositionVector().add(0, me.getEyeHeight(), 0), new Vec3d(px,py,pz));
        setYawAndPitch(v[0], v[1]);
    }

    private boolean canPlaceCrystal(BlockPos blockPos) {
        BlockPos boost = blockPos.add(0, 1, 0);
        BlockPos boost2 = blockPos.add(0, 2, 0);
        return (mc.world.getBlockState(blockPos).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(blockPos).getBlock() == Blocks.OBSIDIAN) && mc.world.getBlockState(boost).getBlock() == Blocks.AIR && mc.world.getBlockState(boost2).getBlock() == Blocks.AIR && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost)).isEmpty() && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost2)).isEmpty();
    }

    private static BlockPos getPlayerPos() {
        return new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ));
    }

    private List<BlockPos> findCrystalBlocks() {
        NonNullList positions = NonNullList.create();
        positions.addAll(this.getSphere(getPlayerPos(), this.range.getValue().floatValue(), this.range.getValue().intValue(), false, true, 0)
                .stream()
                .filter(this::canPlaceCrystal)
                .collect(Collectors.toList()));
        return positions;
    }

    public List<BlockPos> getSphere(BlockPos loc, float r, int h, boolean hollow, boolean sphere, int plus_y) {
        ArrayList<BlockPos> circleblocks = new ArrayList<>();
        int cx = loc.getX();
        int cy = loc.getY();
        int cz = loc.getZ();
        int x = cx - (int)r;
        while ((float)x <= (float)cx + r) {
            int z = cz - (int)r;
            while ((float)z <= (float)cz + r) {
                int y = sphere ? cy - (int)r : cy;
                do {
                    float f = sphere ? (float)cy + r : (float)(cy + h);
                    if (!((float)y < f)) break;
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
                    if (!(!(dist < (double)(r * r)) || hollow && dist < (double)((r - 1.0f) * (r - 1.0f)))) {
                        BlockPos l = new BlockPos(x, y + plus_y, z);
                        circleblocks.add(l);
                    }
                    ++y;
                } while (true);
                ++z;
            }
            ++x;
        }
        return circleblocks;
    }

    public static float calculateDamage(double posX, double posY, double posZ, Entity entity) {
        float doubleExplosionSize = 12.0f;
        double distancedsize = entity.getDistance(posX, posY, posZ) / (double)doubleExplosionSize;
        Vec3d vec3d = new Vec3d(posX, posY, posZ);
        double blockDensity = entity.world.getBlockDensity(vec3d, entity.getEntityBoundingBox());
        double v = (1.0 - distancedsize) * blockDensity;
        float damage = (int)((v * v + v) / 2.0 * 7.0 * (double)doubleExplosionSize + 1.0);
        double finald = 1.0;
        if (entity instanceof EntityLivingBase) {
            finald = getBlastReduction((EntityLivingBase)entity, getDamageMultiplied(damage), new Explosion((World)mc.world, null, posX, posY, posZ, 6.0f, false, true));
        }
        return (float)finald;
    }

    public static float getBlastReduction(EntityLivingBase entity, float damage, Explosion explosion) {
        if (entity instanceof EntityPlayer) {
            EntityPlayer ep = (EntityPlayer)entity;
            DamageSource ds = DamageSource.causeExplosionDamage((Explosion)explosion);
            damage = CombatRules.getDamageAfterAbsorb((float)damage, (float)ep.getTotalArmorValue(), (float)((float)ep.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue()));
            int k = EnchantmentHelper.getEnchantmentModifierDamage((Iterable<ItemStack>)ep.getArmorInventoryList(), (DamageSource)ds);
            float f = MathHelper.clamp((float)k, (float)0.0f, (float)20.0f);
            damage *= 1.0f - f / 25.0f;
            if (entity.isPotionActive(Potion.getPotionById((int)11))) {
                damage -= damage / 4.0f;
            }
            damage = Math.max(damage, 0.0f);
            return damage;
        }
        damage = CombatRules.getDamageAfterAbsorb((float)damage, (float)entity.getTotalArmorValue(), (float)((float)entity.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue()));
        return damage;
    }

    private static float getDamageMultiplied(float damage) {
        int diff = mc.world.getDifficulty().getId();
        return damage * (diff == 0 ? 0.0f : (diff == 2 ? 1.0f : (diff == 1 ? 0.5f : 1.5f)));
    }

    public static float calculateDamage(EntityEnderCrystal crystal, Entity entity) {
        return calculateDamage(crystal.posX, crystal.posY, crystal.posZ, entity);
    }

    private static void setYawAndPitch(float yaw1, float pitch1) {
        yaw = yaw1;
        pitch = pitch1;
        isSpoofingAngles = true;
    }

    private static void resetRotation() {
        if (isSpoofingAngles) {
            yaw = mc.player.rotationYaw;
            pitch = mc.player.rotationPitch;
            isSpoofingAngles = false;
        }
    }

    @Override
    public void onEnable() {
        if (this.alert.getValue()) {
            Seppuku.INSTANCE.logChat(" \u00a7eAutoCrystal \u00a7aON");
        }
        super.onEnable();
    }

    @Override
    public void onDisable() {
        if (this.alert.getValue()) {
            Seppuku.INSTANCE.logChat(" \u00a7eAutoCrystal \u00a74OFF");
        }
        this.render = null;
        this.renderEnt = null;
        resetRotation();
        super.onDisable();
    }
}

