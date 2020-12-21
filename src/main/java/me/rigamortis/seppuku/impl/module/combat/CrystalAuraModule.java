package me.rigamortis.seppuku.impl.module.combat;

import me.rigamortis.seppuku.Seppuku;
import me.rigamortis.seppuku.api.event.EventStageable;
import me.rigamortis.seppuku.api.event.network.EventReceivePacket;
import me.rigamortis.seppuku.api.event.player.EventUpdateWalkingPlayer;
import me.rigamortis.seppuku.api.event.render.EventRender3D;
import me.rigamortis.seppuku.api.module.Module;
import me.rigamortis.seppuku.api.util.*;
import me.rigamortis.seppuku.api.value.Value;
import me.rigamortis.seppuku.impl.module.player.GodModeModule;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.ClickType;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.*;
import net.minecraft.world.Explosion;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static me.rigamortis.seppuku.api.util.InventoryUtil.findStackInventory;

/**
 * Author Seth
 * 4/29/2019 @ 1:19 AM.
 * Edited by cats
 * 12/12/2019
 */
public final class CrystalAuraModule extends Module {

    public final Value<Float> range = new Value("Range", new String[]{"Dist"}, "The minimum range to attack crystals.", 4.5f, 0.0f, 5.0f, 0.1f);
    public final Value<Float> delay = new Value("Attack_Delay", new String[]{"AttackDelay", "AttackDel", "Del"}, "The delay to attack in milliseconds.", 50.0f, 0.0f, 1000.0f, 1.0f);
    public final Value<Boolean> place = new Value("Place", new String[]{"AutoPlace"}, "Automatically place crystals.", true);
    public final Value<Boolean> antiWeakness = new Value("AntiWeakness", new String[]{"Weakness"}, "Switches a sword to mainhand to break crystals while affected by weakness.", true);
    public final Value<Boolean> sync = new Value("Sync", new String[]{"snc"}, "Sync your hits with the server's estimated TPS for AntiWeakness.", true);
    public final Value<Float> minCoolDown = new Value("Min_CoolDown", new String[]{"CoolDown", "Cool"}, "The minimum cooldown to hit a crystal for antiweakness because I couldn't get it to figure it out itself.", 50.0f, 0.0f, 100.0f, 1.0f);
    public final Value<Float> placeDelay = new Value("Place_Delay", new String[]{"PlaceDelay", "PlaceDel"}, "The delay to place crystals.", 50.0f, 0.0f, 1000.0f, 1.0f);
    public final Value<Float> minDamage = new Value("Min_Damage", new String[]{"MinDamage", "Min", "MinDmg"}, "The minimum explosion damage calculated to place down a crystal.", 1.0f, 0.0f, 20.0f, 0.5f);
    public final Value<Boolean> ignore = new Value("Ignore", new String[]{"Ig"}, "Ignore self damage checks.", false);
    public final Value<Boolean> friendIgnore = new Value("FriendIgnore", new String[]{"Fig"}, "Ignore friend damage checks.", false);
    public final Value<Boolean> render = new Value("Render", new String[]{"R"}, "Draws information about recently placed crystals from your player.", true);
    public final Value<Boolean> renderDamage = new Value("Render_Damage", new String[]{"RD", "RenderDamage", "ShowDamage"}, "Draws calculated explosion damage on recently placed crystals from your player.", true);

    public final Value<Boolean> refill = new Value<Boolean>("Refill", new String[]{"ref"}, "Automatically refills your hotbar with end crystals", true);
    public final Value<Float> absorptionFactor = new Value<Float>("Absorption_Percent", new String[] {"ap"}, "Percent of absorption to be factored into damage calculations", 50f);
    private Timer attackTimer = new Timer();
    private Timer placeTimer = new Timer();

    private List<PlaceLocation> placeLocations = new CopyOnWriteArrayList<>();
    private boolean placed;

    public CrystalAuraModule() {
        super("CrystalAura", new String[]{"AutoCrystal", "Crystal"}, "Automatically places crystals near enemies and detonates them", "NONE", -1, ModuleType.COMBAT);
    }

    @Listener
    public void onWalkingUpdate(EventUpdateWalkingPlayer event) {
        final Minecraft mc = Minecraft.getMinecraft();
        if (event.getStage() == EventStageable.EventStage.PRE) {

            if (mc.player.getHeldItemMainhand().getItem() == Items.GOLDEN_APPLE
                    || mc.player.getHeldItemMainhand().getItem() == Items.DIAMOND_PICKAXE
                    || mc.player.getHeldItemMainhand().getItem() == Items.CHORUS_FRUIT
                    || mc.player.getHeldItemMainhand().getItem() == Items.EXPERIENCE_BOTTLE
                    || mc.player.getHeldItemMainhand().getItem() == Items.POTIONITEM)
            {
                return;
            }

            if (placed && mc.player.inventory.getCurrentItem().getItem() == Items.AIR && this.refill.getValue()) {
                final int invSlot = findStackInventory(Items.END_CRYSTAL);
                if (invSlot != -1 && invSlot != mc.player.inventory.currentItem) {
                    mc.playerController.windowClick(mc.player.inventoryContainer.windowId, invSlot, mc.player.inventory.currentItem, ClickType.SWAP, mc.player);
                    mc.playerController.updateController();
                }
            }

            if (this.place.getValue()) {
                if (this.placeTimer.passed(this.placeDelay.getValue())) {
                    final float radius = this.range.getValue();

                    float friendDamage = 0;
                    float damage = 0;
                    double maxDist = 6.0f;
                    BlockPos pos = null;
                    EntityPlayer target = null;
                    EntityPlayer friend = null;

                    for (float x = radius; x >= -radius; x--) {
                        for (float y = radius; y >= -radius; y--) {
                            for (float z = radius; z >= -radius; z--) {
                                final BlockPos blockPos = new BlockPos(mc.player.posX + x, mc.player.posY + y, mc.player.posZ + z);

                                if (canPlaceCrystal(blockPos)) {
                                    for (Entity entity : mc.world.loadedEntityList) {
                                        if (entity != null && entity instanceof EntityPlayer) {
                                            final EntityPlayer player = (EntityPlayer) entity;
                                            if (player != mc.player && !player.getName().equals(mc.player.getName()) && player.getHealth() > 0 && Seppuku.INSTANCE.getFriendManager().isFriend(player) == null) {
                                                final double distToBlock = entity.getDistance(blockPos.getX() + 0.5f, blockPos.getY() + 1, blockPos.getZ() + 0.5f);
                                                final double distToLocal = entity.getDistance(mc.player.posX, mc.player.posY, mc.player.posZ);
                                                if (distToBlock <= 14 && distToLocal <= maxDist) {
                                                    target = player;
                                                    maxDist = distToLocal;
                                                }
                                            }
                                            if (!this.friendIgnore.getValue()) {
                                                if (player != mc.player && !player.getName().equals(mc.player.getName()) && player.getHealth() > 0 && Seppuku.INSTANCE.getFriendManager().isFriend(player) != null) {
                                                    final double distToBlock = entity.getDistance(blockPos.getX() + 0.5f, blockPos.getY() + 1, blockPos.getZ() + 0.5f);
                                                    if (distToBlock <= 14) {
                                                        friend = player;
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    if (target != null) {

                                        final float currentDamage = calculateExplosionDamage(target, 6.0f, blockPos.getX() + 0.5f, blockPos.getY() + 1.0f, blockPos.getZ() + 0.5f) / 2.0f;

                                        float localDamage = calculateExplosionDamage(mc.player, 6.0f, blockPos.getX() + 0.5f, blockPos.getY() + 1.0f, blockPos.getZ() + 0.5f) / 2.0f;

                                        if (!this.friendIgnore.getValue() && friend != null) {
                                            friendDamage = calculateExplosionDamage(friend, 6.0f, blockPos.getX() + 0.5f, blockPos.getY() + 1.0f, blockPos.getZ() + 0.5f) / 2.0f;
                                        }

                                        if (!canTakeDamage()) {
                                            localDamage = -1;
                                        }

                                        if (currentDamage > damage && currentDamage >= this.minDamage.getValue() && localDamage <= currentDamage) {
                                            if (this.friendIgnore.getValue() || friend == null) {
                                                damage = currentDamage;
                                                pos = blockPos;
                                            } else if (friendDamage <= currentDamage) {
                                                damage = currentDamage;
                                                pos = blockPos;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (pos != null && damage > 0) {
                        mc.player.inventory.currentItem = InventoryUtil.getItemHotbar(Items.END_CRYSTAL);
                        final float[] angle = MathUtil.calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), new Vec3d(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f));
                        Seppuku.INSTANCE.getRotationManager().setPlayerRotations(angle[0], angle[1]);
                        mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, EnumFacing.UP, EnumHand.MAIN_HAND, 0, 0, 0));
                        this.placeLocations.add(new PlaceLocation(pos.getX(), pos.getY(), pos.getZ(), damage));
                    }

                    this.placeTimer.reset();

                    placed = true;
                }
            }
            for (Entity entity : mc.world.loadedEntityList) {
                if (entity != null && entity instanceof EntityEnderCrystal) {
                    if (mc.player.getDistance(entity) <= this.range.getValue()) {
                        for (Entity ent : mc.world.loadedEntityList) {
                            if (ent != null && ent != mc.player && (ent.getDistance(entity) <= 14.0f) && ent != entity && ent instanceof EntityPlayer && Seppuku.INSTANCE.getFriendManager().isFriend(ent) == null) {
                                final EntityPlayer player = (EntityPlayer) ent;
                                float currentDamage = calculateExplosionDamage(player, 6.0f, (float) entity.posX, (float) entity.posY, (float) entity.posZ) / 2.0f;
                                float localDamage = calculateExplosionDamage(mc.player, 6.0f, (float) entity.posX, (float) entity.posY, (float) entity.posZ) / 2.0f;

                                if (!canTakeDamage()) {
                                    localDamage = -1;
                                }

                                if (localDamage <= currentDamage && currentDamage >= this.minDamage.getValue()) {
                                    final float[] angle = MathUtil.calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), entity.getPositionVector());
                                    Seppuku.INSTANCE.getRotationManager().setPlayerRotations(angle[0], angle[1]);

                                    if (this.antiWeakness.getValue() && mc.player.isPotionActive(MobEffects.WEAKNESS)) {
                                        final float ticks = 20.0f - Seppuku.INSTANCE.getTickRateManager().getTickRate();
                                        if (mc.player.getCooledAttackStrength(this.sync.getValue() ? -ticks : 0.0f) >= (this.minCoolDown.getValue()/100) && this.attackTimer.passed(this.delay.getValue())) {
                                            mc.player.inventory.currentItem = InventoryUtil.getItemHotbar(Items.DIAMOND_SWORD);
                                            mc.player.swingArm(EnumHand.MAIN_HAND);
                                            mc.playerController.attackEntity(mc.player, entity);
                                            this.attackTimer.reset();
                                        }

                                    } else {
                                        if (this.attackTimer.passed(this.delay.getValue())) {
                                            mc.player.swingArm(EnumHand.MAIN_HAND);
                                            mc.playerController.attackEntity(mc.player, entity);
                                            this.attackTimer.reset();
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Listener
    public void onReceivePacket(EventReceivePacket event) {
        if (event.getStage() == EventStageable.EventStage.POST) {
            if (event.getPacket() instanceof SPacketSpawnObject) {
                final SPacketSpawnObject packetSpawnObject = (SPacketSpawnObject) event.getPacket();
                if (packetSpawnObject.getType() == 51) {
                    for (PlaceLocation placeLocation : this.placeLocations) {
                        if (placeLocation.getDistance((int) packetSpawnObject.getX(), (int) packetSpawnObject.getY() - 1, (int) packetSpawnObject.getZ()) <= 1) {
                            placeLocation.placed = true;
                        }
                    }
                }
            }
        }
    }

    @Listener
    public void onRender(EventRender3D event) {
        if (!this.render.getValue())
            return;

        final Minecraft mc = Minecraft.getMinecraft();

        for (PlaceLocation placeLocation : this.placeLocations) {
            if (placeLocation.alpha <= 0) {
                this.placeLocations.remove(placeLocation);
                continue;
            }

            placeLocation.update();

            if (placeLocation.placed) {
                final AxisAlignedBB bb = new AxisAlignedBB(
                        placeLocation.getX() - mc.getRenderManager().viewerPosX,
                        placeLocation.getY() - mc.getRenderManager().viewerPosY,
                        placeLocation.getZ() - mc.getRenderManager().viewerPosZ,
                        placeLocation.getX() + 1 - mc.getRenderManager().viewerPosX,
                        placeLocation.getY() + 1 - mc.getRenderManager().viewerPosY,
                        placeLocation.getZ() + 1 - mc.getRenderManager().viewerPosZ);

                RenderUtil.drawFilledBox(bb, ColorUtil.changeAlpha(0xAA9900EE, placeLocation.alpha / 2));
                RenderUtil.drawBoundingBox(bb, 1, ColorUtil.changeAlpha(0xAAAAAAAA, placeLocation.alpha));

                if (this.renderDamage.getValue()) {
                    GlStateManager.pushMatrix();
                    RenderUtil.glBillboardDistanceScaled((float) placeLocation.getX() + 0.5f, (float) placeLocation.getY() + 0.5f, (float) placeLocation.getZ() + 0.5f, mc.player, 1);
                    final float damage = placeLocation.damage;
                    final String damageText = (Math.floor(damage) == damage ? (int) damage : String.format("%.1f", damage)) + "";
                    GlStateManager.disableDepth();
                    GlStateManager.translate(-(mc.fontRenderer.getStringWidth(damageText) / 2.0d), 0, 0);
                    mc.fontRenderer.drawStringWithShadow(damageText, 0, 0, 0xFFAAAAAA);
                    GlStateManager.popMatrix();
                }
            }
        }
    }

    private boolean canTakeDamage() {
        final Minecraft mc = Minecraft.getMinecraft();

        if (mc.player.capabilities.isCreativeMode) {
            return false;
        }

        final GodModeModule mod = (GodModeModule) Seppuku.INSTANCE.getModuleManager().find(GodModeModule.class);

        if (mod != null && mod.isEnabled()) {
            return false;
        }

        if (this.ignore.getValue()) {
            return false;
        }

        return true;
    }

    private boolean canPlaceCrystal(BlockPos pos) {
        final Minecraft mc = Minecraft.getMinecraft();

        final Block block = mc.world.getBlockState(pos).getBlock();

        if (block == Blocks.OBSIDIAN || block == Blocks.BEDROCK) {
            final Block floor = mc.world.getBlockState(pos.add(0, 1, 0)).getBlock();
            final Block ceil = mc.world.getBlockState(pos.add(0, 2, 0)).getBlock();

            if (floor == Blocks.AIR && ceil == Blocks.AIR) {
                if (mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos.add(0, 1, 0))).isEmpty()) {
                    if (mc.player.getDistance(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f) <= this.range.getValue()) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private float calculateExplosionDamage(EntityLivingBase entity, float size, float x, float y, float z) {
        final Minecraft mc = Minecraft.getMinecraft();
        final float scale = size * 2.0F;
        final Vec3d pos = MathUtil.interpolateEntity(entity, mc.getRenderPartialTicks());
        final double dist = MathUtil.getDistance(pos, x, y, z) / (double) scale;
        //final double dist = entity.getDistance(x, y, z) / (double) scale;
        final Vec3d vec3d = new Vec3d(x, y, z);
        final double density = (double) entity.world.getBlockDensity(vec3d, entity.getEntityBoundingBox());
        final double densityScale = (1.0D - dist) * density;

        float unscaledDamage = (float) ((int) ((densityScale * densityScale + densityScale) / 2.0d * 7.0d * (double) scale + 1.0d));

        unscaledDamage *= 0.5f * mc.world.getDifficulty().getId();

        return scaleExplosionDamage(entity, new Explosion(mc.world, null, x, y, z, size, false, true), unscaledDamage);
    }

    private float scaleExplosionDamage(EntityLivingBase entity, Explosion explosion, float damage) {
        damage = CombatRules.getDamageAfterAbsorb(damage, (float) entity.getTotalArmorValue(), (float) entity.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());

        damage *= (1.0F - MathHelper.clamp(EnchantmentHelper.getEnchantmentModifierDamage(entity.getArmorInventoryList(), DamageSource.causeExplosionDamage(explosion)), 0.0F, 20.0F) / 25.0F);

        damage = Math.max(damage - (entity.getAbsorptionAmount() * (absorptionFactor.getValue())/100f), 0.0F);
        return damage;
    }

    private final class PlaceLocation extends Vec3i {

        private int alpha = 0xAA;
        private boolean placed = false;
        private float damage;

        private PlaceLocation(int xIn, int yIn, int zIn, float damage) {
            super(xIn, yIn, zIn);
            this.damage = damage;
        }

        private void update() {
            if (this.alpha > 0)
                this.alpha -= 1;
        }
    }

}
