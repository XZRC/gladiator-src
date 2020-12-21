package me.rigamortis.seppuku.impl.module.render;

import com.google.common.collect.Lists;
import com.mojang.realmsclient.gui.ChatFormatting;
import me.rigamortis.seppuku.Seppuku;
import me.rigamortis.seppuku.api.event.EventStageable;
import me.rigamortis.seppuku.api.event.network.EventReceivePacket;
import me.rigamortis.seppuku.api.event.render.EventRender2D;
import me.rigamortis.seppuku.api.event.render.EventRenderName;
import me.rigamortis.seppuku.api.friend.Friend;
import me.rigamortis.seppuku.api.module.Module;
import me.rigamortis.seppuku.api.util.*;
import me.rigamortis.seppuku.api.value.Value;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.*;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Author Seth
 * 4/20/2019 @ 10:07 AM.
 */
public final class WallHackModule extends Module {

    public final Value<Mode> mode = new Value<Mode>("Mode", new String[]{"Mode", "M"}, "The mode of the drawn esp/wallhack.", Mode.OPAQUE);

    private enum Mode {
        OPAQUE, BOX
    }

    public final Value<Boolean> players = new Value<Boolean>("Players", new String[]{"Player"}, "Choose to enable on players.", true);
    public final Value<Boolean> mobs = new Value<Boolean>("Mobs", new String[]{"Mob"}, "Choose to enable on mobs.", true);
    public final Value<Boolean> animals = new Value<Boolean>("Animals", new String[]{"Animal"}, "Choose to enable on animals.", true);
    public final Value<Boolean> vehicles = new Value<Boolean>("Vehicles", new String[]{"Vehic", "Vehicle"}, "Choose to enable on vehicles.", true);
    public final Value<Boolean> items = new Value<Boolean>("Items", new String[]{"Item"}, "Choose to enable on items.", true);
    public final Value<Boolean> local = new Value<Boolean>("Local", new String[]{"Self"}, "Choose to enable on self/local-player.", true);
    public final Value<Boolean> crystals = new Value<Boolean>("Crystals", new String[]{"crystal", "crystals", "endcrystal", "endcrystals"}, "Choose to enable on end crystals.", true);
    public final Value<Boolean> pearls = new Value<Boolean>("Pearls", new String[]{"Pearl"}, "Choose to enable on ender pearls.", true);
    public final Value<Boolean> armorStand = new Value<Boolean>("ArmorStands", new String[]{"ArmorStand", "ArmourStand", "ArmourStands", "ArmStand"}, "Choose to enable on armor-stands.", true);
    public final Value<Boolean> footsteps = new Value<Boolean>("FootSteps", new String[]{"FootStep", "Steps"}, "Choose to draw entity footsteps.", false);

    public final Value<Boolean> name = new Value<Boolean>("Name", new String[]{"Nam"}, "Draw the entity's name.", true);
    public final Value<Boolean> ping = new Value<Boolean>("Ping", new String[]{"Ms"}, "Draw the entity's ping (only works on players).", true);
    public final Value<Boolean> armor = new Value<Boolean>("Armor", new String[]{"Arm"}, "Draw the entity's equipped armor.", true);
    public final Value<Boolean> hearts = new Value<Boolean>("Hearts", new String[]{"Hrts"}, "Draw the entity's hearts in decimal format.", true);
    public final Value<Boolean> enchants = new Value<Boolean>("Enchants", new String[]{"Ench"}, "Draw enchant names above the entity's equipped armor. (requires Armor value to be enabled.", true);
    public final Value<PotionsMode> potions = new Value<PotionsMode>("Potions", new String[]{"Pot", "Pots", "PotsMode"}, "Rendering mode for active potion-effects on the entity.", PotionsMode.NONE);

    private enum PotionsMode {
        NONE, ICON, TEXT
    }

    public final Value<Boolean> background = new Value<Boolean>("Background", new String[]{"Bg"}, "Draw a transparent black background behind any text or icon drawn.", true);

    public final Value<HealthMode> hpMode = new Value<HealthMode>("Hp", new String[]{"Health", "HpMode"}, "Rendering mode for the health bar.", HealthMode.NONE);

    private enum HealthMode {
        NONE, BAR, BARTEXT
    }

    private ICamera camera = new Frustum();
    private final ResourceLocation inventory = new ResourceLocation("textures/gui/container/inventory.png");

    //i cba
    private List<FootstepData> footstepDataList = new CopyOnWriteArrayList<>();

    public WallHackModule() {
        super("WallHack", new String[]{"Esp"}, "Highlights entities", "NONE", -1, ModuleType.RENDER);
    }

    @Listener
    public void render2D(EventRender2D event) {
        final Minecraft mc = Minecraft.getMinecraft();

        if (this.footsteps.getValue()) {
            for (FootstepData data : this.footstepDataList) {
                final GLUProjection.Projection projection = GLUProjection.getInstance().project(data.x - mc.getRenderManager().viewerPosX, data.y - mc.getRenderManager().viewerPosY, data.z - mc.getRenderManager().viewerPosZ, GLUProjection.ClampMode.NONE, false);
                if (projection != null && projection.getType() == GLUProjection.Projection.Type.INSIDE) {
                    Seppuku.INSTANCE.getFontManager().drawString("*step*", (float) projection.getX() - Seppuku.INSTANCE.getFontManager().getStringWidth("*step*") / 2, (float) projection.getY(), -1, true, false);
                }

                if (Math.abs(System.currentTimeMillis() - data.getTime()) >= 3000) {
                    this.footstepDataList.remove(data);
                }
            }
        }

        for (Entity e : mc.world.loadedEntityList) {
            if (e != null) {
                if (this.checkFilter(e)) {
                    final float[] bounds = this.convertBounds(e, event.getPartialTicks(), event.getScaledResolution().getScaledWidth(), event.getScaledResolution().getScaledHeight());

                    if (bounds != null) {
                        if (this.mode.getValue() == Mode.BOX) {
                            RenderUtil.drawOutlineRect(bounds[0], bounds[1], bounds[2], bounds[3], 1.5f, 0xAA000000);
                            RenderUtil.drawOutlineRect(bounds[0] - 0.5f, bounds[1] - 0.5f, bounds[2] + 0.5f, bounds[3] + 0.5f, 0.5f, this.getColor(e));
                        }

                        String name = StringUtils.stripControlCodes(getNameForEntity(e));
                        String heartsFormatted = null;
                        String pingFormatted = null;

                        if (this.name.getValue()) {
                            int color = -1;

                            final Friend friend = Seppuku.INSTANCE.getFriendManager().isFriend(e);

                            if (friend != null) {
                                name = friend.getAlias();
                                color = 0xFF9900EE;
                            }

                            if (this.background.getValue()) {
                                RenderUtil.drawRect(bounds[0] + (bounds[2] - bounds[0]) / 2 - Seppuku.INSTANCE.getFontManager().getStringWidth(name) / 2 - 1, bounds[1] + (bounds[3] - bounds[1]) - Seppuku.INSTANCE.getFontManager().getHeight() - 2, bounds[0] + (bounds[2] - bounds[0]) / 2 + Seppuku.INSTANCE.getFontManager().getStringWidth(name) / 2 + 1, bounds[1] + (bounds[3] - bounds[1]) - 1, 0x75101010);
                            }

                            Seppuku.INSTANCE.getFontManager().drawString(name, bounds[0] + (bounds[2] - bounds[0]) / 2 - Seppuku.INSTANCE.getFontManager().getStringWidth(name) / 2, bounds[1] + (bounds[3] - bounds[1]) - Seppuku.INSTANCE.getFontManager().getHeight() - 1, color, true, false);
                        }

                        if (e instanceof EntityPlayer) {
                            final EntityPlayer player = (EntityPlayer) e;
                            if (this.ping.getValue()) {
                                int responseTime = -1;
                                try {
                                    responseTime = (int) MathUtil.clamp(mc.getConnection().getPlayerInfo(player.getUniqueID()).getResponseTime(), 0, 300);
                                } catch (NullPointerException np) {
                                }
                                pingFormatted = responseTime + "ms";

                                float startX = -Seppuku.INSTANCE.getFontManager().getStringWidth(pingFormatted) / 2.0f;
                                if (this.name.getValue())
                                    startX = (Seppuku.INSTANCE.getFontManager().getStringWidth(name) / 2.0f) + 2.0f;
                                else if (this.hearts.getValue())
                                    startX = (Seppuku.INSTANCE.getFontManager().getStringWidth(heartsFormatted) / 2.0f) + (Seppuku.INSTANCE.getFontManager().getStringWidth(heartsFormatted) / 2.0f);

                                int pingRounded = Math.round(255.0f - (responseTime * 255.0f / 300.0f)); // 300 = max response time (red, laggy)
                                int pingColor = 255 - pingRounded << 16 | pingRounded << 8;

                                if (this.background.getValue()) {
                                    RenderUtil.drawRect(bounds[0] + (bounds[2] - bounds[0]) / 2 + startX, bounds[1] + (bounds[3] - bounds[1]) - Seppuku.INSTANCE.getFontManager().getHeight() - 2, bounds[0] + (bounds[2] - bounds[0]) / 2 + startX + Seppuku.INSTANCE.getFontManager().getStringWidth(pingFormatted), bounds[1] + (bounds[3] - bounds[1]) - 1, 0x75101010);
                                }

                                Seppuku.INSTANCE.getFontManager().drawString(pingFormatted, bounds[0] + (bounds[2] - bounds[0]) / 2 + startX, bounds[1] + (bounds[3] - bounds[1]) - Seppuku.INSTANCE.getFontManager().getHeight() - 1, pingColor, true, false);
                            }
                        }

                        if (e instanceof EntityLivingBase) {
                            final EntityLivingBase entityLiving = (EntityLivingBase) e;
                            if (this.hearts.getValue()) {
                                float health = (entityLiving.getHealth() + entityLiving.getAbsorptionAmount());

                                if (health <= 0)
                                    heartsFormatted = "*DEAD*";
                                else if (health < 1)
                                    heartsFormatted = String.valueOf(1);
                                else
                                    heartsFormatted = String.valueOf((int) health);

                                if (e instanceof EntityPlayer) {
                                    if (((EntityPlayer) e).isCreative())
                                        heartsFormatted = ChatFormatting.YELLOW + "*";
                                }

                                float startX = -Seppuku.INSTANCE.getFontManager().getStringWidth(heartsFormatted) / 2.0f;
                                if (this.name.getValue())
                                    startX = -(Seppuku.INSTANCE.getFontManager().getStringWidth(name) / 2.0f) - 2.0f - Seppuku.INSTANCE.getFontManager().getStringWidth(heartsFormatted);
                                else if (this.ping.getValue() && entityLiving instanceof EntityPlayer)
                                    startX = -(Seppuku.INSTANCE.getFontManager().getStringWidth(pingFormatted) / 2.0f) - (Seppuku.INSTANCE.getFontManager().getStringWidth(heartsFormatted) / 2.0f);

                                int heartsRounded;

                                if (health > entityLiving.getMaxHealth())
                                    heartsRounded = 0;
                                else
                                    heartsRounded = Math.round(255.0f - (((int) health) * 255.0f / entityLiving.getMaxHealth()));

                                int heartsColor = 255 - heartsRounded << 8 | heartsRounded << 16;

                                if (this.background.getValue()) {
                                    RenderUtil.drawRect(bounds[0] + (bounds[2] - bounds[0]) / 2 + startX, bounds[1] + (bounds[3] - bounds[1]) - Seppuku.INSTANCE.getFontManager().getHeight() - 2, bounds[0] + (bounds[2] - bounds[0]) / 2 + startX + Seppuku.INSTANCE.getFontManager().getStringWidth(heartsFormatted), bounds[1] + (bounds[3] - bounds[1]) - 1, 0x75101010);
                                }

                                Seppuku.INSTANCE.getFontManager().drawString(heartsFormatted, bounds[0] + (bounds[2] - bounds[0]) / 2 + startX, bounds[1] + (bounds[3] - bounds[1]) - Seppuku.INSTANCE.getFontManager().getHeight() - 1, heartsColor, true, false);
                            }

                            if (this.hpMode.getValue() != HealthMode.NONE) {
                                RenderUtil.drawRect(bounds[2] - 0.5f, bounds[1], bounds[2] - 2, bounds[3], 0xAA000000);
                                final float hpHeight = ((((EntityLivingBase) e).getHealth() * (bounds[3] - bounds[1])) / ((EntityLivingBase) e).getMaxHealth());

                                RenderUtil.drawRect(bounds[2] - 1, bounds[1] - 0.5f, bounds[2] - 1.5f, (bounds[1] - bounds[3]) + bounds[3] + hpHeight + 0.5f, getHealthColor(e));

                                if (this.hpMode.getValue() == HealthMode.BARTEXT) {
                                    if (((EntityLivingBase) e).getHealth() < ((EntityLivingBase) e).getMaxHealth() && ((EntityLivingBase) e).getHealth() > 0) {
                                        final String hp = new DecimalFormat("#.#").format((int) ((EntityLivingBase) e).getHealth());
                                        Seppuku.INSTANCE.getFontManager().drawString(hp, (bounds[2] - 1 - Seppuku.INSTANCE.getFontManager().getStringWidth(hp) / 2), ((bounds[1] - bounds[3]) + bounds[3] + hpHeight + 0.5f - Seppuku.INSTANCE.getFontManager().getHeight() / 2), -1, true, false);
                                    }
                                }
                            }

                            if (this.potions.getValue() != PotionsMode.NONE) {
                                float scale = 0.5f;
                                int offsetX = 0;
                                int offsetY = 0;

                                for (PotionEffect effect : entityLiving.getActivePotionEffects()) {
                                    if (effect.getDuration() <= 0)
                                        continue;
                                    final Potion potion = effect.getPotion();
                                    if (this.potions.getValue() == PotionsMode.ICON) {
                                        if (potion.hasStatusIcon()) {
                                            GlStateManager.pushMatrix();
                                            mc.getTextureManager().bindTexture(this.inventory);// bind the inventory texture
                                            int iconIndex = potion.getStatusIconIndex();
                                            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                                            GlStateManager.translate(bounds[0] + 1, bounds[3], 0);
                                            GlStateManager.scale(scale, scale, 0);

                                            // scoot it over to the right
                                            if (offsetY > 16) {
                                                offsetY = 0;
                                                offsetX += 16;
                                            }

                                            // check to draw the transparent background behind the icon
                                            if (this.background.getValue()) {
                                                RenderUtil.drawRect(offsetX, offsetY, offsetX + 16, offsetY + 16, 0x75101010);
                                            }

                                            // draw the textured icon
                                            RenderUtil.drawTexture(offsetX, offsetY, iconIndex % 8 * 18, 198 + iconIndex / 8 * 18, 18, 18);
                                            GlStateManager.popMatrix();

                                            offsetY += 16;
                                        }
                                    } else if (this.potions.getValue() == PotionsMode.TEXT) {
                                        final List<String> potStringsToDraw = Lists.newArrayList();
                                        final String effectString = PotionUtil.getNameDurationString(effect);

                                        if (effectString != null) { // will return null if it doesn't exist as a valid formatted name
                                            potStringsToDraw.add(effectString);
                                        }

                                        for (String pString : potStringsToDraw) {
                                            GlStateManager.pushMatrix();
                                            GlStateManager.disableDepth();
                                            GlStateManager.translate(bounds[0] + 1, bounds[3] + offsetY, 0);

                                            GlStateManager.scale(0.5f, 0.5f, 0.5f);

                                            if (offsetY > 16) {
                                                offsetY = 0;
                                                offsetX += 16;
                                            }

                                            if (this.background.getValue()) {
                                                RenderUtil.drawRect(0, 0, Seppuku.INSTANCE.getFontManager().getStringWidth(pString), Seppuku.INSTANCE.getFontManager().getHeight() - 1, 0x75101010);
                                            }

                                            Seppuku.INSTANCE.getFontManager().drawString(pString, 0, 0, -1, true, false);

                                            GlStateManager.scale(2, 2, 2);
                                            GlStateManager.enableDepth();
                                            GlStateManager.popMatrix();

                                            offsetY += 4;
                                        }
                                    }
                                }
                            }
                        }

                        if (this.armor.getValue()) {
                            final Iterator<ItemStack> items = e.getEquipmentAndArmor().iterator();
                            final ArrayList<ItemStack> stacks = new ArrayList<>();

                            while (items.hasNext()) {
                                final ItemStack stack = items.next();
                                if (stack != null && stack.getItem() != Items.AIR) {
                                    stacks.add(stack);
                                }
                            }

                            Collections.reverse(stacks);

                            int x = 0;

                            for (ItemStack stack : stacks) {
                                if (stack != null) {
                                    final Item item = stack.getItem();
                                    if (item != Items.AIR) {
                                        GlStateManager.pushMatrix();
                                        GlStateManager.enableBlend();
                                        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                                        RenderHelper.enableGUIStandardItemLighting();
                                        GlStateManager.translate(bounds[0] + (bounds[2] - bounds[0]) / 2 + x - (16 * stacks.size() / 2), bounds[1] + (bounds[3] - bounds[1]) - Seppuku.INSTANCE.getFontManager().getHeight() - 19, 0);
                                        if (this.background.getValue()) {
                                            RenderUtil.drawRect(0, 0, 16, 16, 0x75101010);
                                        }
                                        mc.getRenderItem().renderItemAndEffectIntoGUI(stack, 0, 0);
                                        mc.getRenderItem().renderItemOverlays(mc.fontRenderer, stack, 0, 0);
                                        RenderHelper.disableStandardItemLighting();
                                        GlStateManager.disableBlend();
                                        GlStateManager.color(1, 1, 1, 1);
                                        GlStateManager.popMatrix();
                                        x += 16;

                                        if (this.enchants.getValue()) {
                                            final List<String> stringsToDraw = Lists.newArrayList();
                                            int y = 0;

                                            if (stack.getEnchantmentTagList() != null) {
                                                final NBTTagList tags = stack.getEnchantmentTagList();
                                                for (int i = 0; i < tags.tagCount(); i++) {
                                                    final NBTTagCompound tagCompound = tags.getCompoundTagAt(i);
                                                    if (tagCompound != null && Enchantment.getEnchantmentByID(tagCompound.getByte("id")) != null) {
                                                        final Enchantment enchantment = Enchantment.getEnchantmentByID(tagCompound.getShort("id"));
                                                        final short lvl = tagCompound.getShort("lvl");
                                                        if (enchantment != null) {
                                                            String ench = "";
                                                            if (enchantment.isCurse()) {
                                                                ench = ChatFormatting.RED + enchantment.getTranslatedName(lvl).substring(11).substring(0, 3) + ChatFormatting.GRAY + lvl;
                                                            } else if (ItemUtil.isIllegalEnchant(enchantment, lvl)) {
                                                                ench = ChatFormatting.AQUA + enchantment.getTranslatedName(lvl).substring(0, 3) + ChatFormatting.GRAY + lvl;
                                                            } else {
                                                                ench = enchantment.getTranslatedName(lvl).substring(0, 3) + ChatFormatting.GRAY + lvl;
                                                            }
                                                            stringsToDraw.add(ench);
                                                        }
                                                    }
                                                }
                                            }

                                            // Enchanted gapple
                                            if (item == Items.GOLDEN_APPLE) {
                                                if (stack.getItemDamage() == 1) {
                                                    stringsToDraw.add(ChatFormatting.YELLOW + "God");
                                                }
                                            }

                                            for (String string : stringsToDraw) {
                                                GlStateManager.pushMatrix();
                                                GlStateManager.disableDepth();
                                                GlStateManager.translate(bounds[0] + (bounds[2] - bounds[0]) / 2 + x - ((16.0f * stacks.size()) / 2.0f) - (16.0f / 2.0f) - (Seppuku.INSTANCE.getFontManager().getStringWidth(string) / 4.0f), bounds[1] + (bounds[3] - bounds[1]) - Seppuku.INSTANCE.getFontManager().getHeight() - 23 - y, 0);
                                                GlStateManager.scale(0.5f, 0.5f, 0.5f);
                                                if (this.background.getValue()) {
                                                    RenderUtil.drawRect(0, 0, Seppuku.INSTANCE.getFontManager().getStringWidth(string), Seppuku.INSTANCE.getFontManager().getHeight() - 1, 0x75101010);
                                                }
                                                Seppuku.INSTANCE.getFontManager().drawString(string, 0, 0, -1, true, false);
                                                GlStateManager.scale(2, 2, 2);
                                                GlStateManager.enableDepth();
                                                GlStateManager.popMatrix();
                                                y += 4;
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
    }

    @Listener
    public void receivePacket(EventReceivePacket event) {
        if (event.getStage() == EventStageable.EventStage.PRE) {
            if (event.getPacket() instanceof SPacketSoundEffect) {
                final SPacketSoundEffect packet = (SPacketSoundEffect) event.getPacket();

                if (packet.getCategory() == SoundCategory.NEUTRAL || packet.getCategory() == SoundCategory.PLAYERS) {
                    final String sound = packet.getSound().getSoundName().getPath();
                    if (sound.endsWith(".step") || sound.endsWith(".paddle_land") || sound.endsWith(".gallop")) {
                        this.footstepDataList.add(new FootstepData(packet.getX(), packet.getY(), packet.getZ(), System.currentTimeMillis()));
                    }
                }
            }
        }
    }

    private int getHealthColor(Entity entity) {
        int scale = (int) Math.round(255.0 - (double) ((EntityLivingBase) entity).getHealth() * 255.0 / (double) ((EntityLivingBase) entity).getMaxHealth());
        int damageColor = 255 - scale << 8 | scale << 16;

        return (255 << 24) | damageColor;
    }

    @Listener
    public void renderName(EventRenderName event) {
        if (event.getEntity() instanceof EntityPlayer) {
            event.setCanceled(true);
        }
    }

    private String getNameForEntity(Entity entity) {
        if (entity instanceof EntityItem) {
            final EntityItem item = (EntityItem) entity;
            String itemName = "";

            final int stackSize = item.getItem().getCount();
            if (stackSize > 1) {
                itemName = item.getItem().getDisplayName() + "(" + item.getItem().getCount() + ")";
            } else {
                itemName = item.getItem().getDisplayName();
            }
            return itemName;
        }
        if (entity instanceof EntityEnderCrystal) {
            return "End Crystal";
        }
        if (entity instanceof EntityEnderPearl) {
            final EntityEnderPearl pearl = (EntityEnderPearl) entity;
            //TODO resolve and draw username
//            return pearl.getCachedUniqueIdString();
            return "Ender Pearl";
        }
        if (entity instanceof EntityMinecart) {
            final EntityMinecart minecart = (EntityMinecart) entity;
            return minecart.getCartItem().getDisplayName();
        }
        return entity.getName();
    }

    private boolean checkFilter(Entity entity) {
        boolean ret = false;

        if (this.local.getValue() && (entity == Minecraft.getMinecraft().player) && (Minecraft.getMinecraft().gameSettings.thirdPersonView != 0)) {
            ret = true;
        }
        if (this.players.getValue() && entity instanceof EntityPlayer && entity != Minecraft.getMinecraft().player) {
            ret = true;
        }
        if (this.mobs.getValue() && entity instanceof IMob) {
            ret = true;
        }
        if (this.animals.getValue() && entity instanceof IAnimals && !(entity instanceof IMob)) {
            ret = true;
        }
        if (this.items.getValue() && entity instanceof EntityItem) {
            ret = true;
        }
        if (this.crystals.getValue() && entity instanceof EntityEnderCrystal) {
            ret = true;
        }
        if (this.vehicles.getValue() && (entity instanceof EntityBoat || entity instanceof EntityMinecart || entity instanceof EntityMinecartContainer)) {
            ret = true;
        }
        if (this.armorStand.getValue() && entity instanceof EntityArmorStand) {
            ret = true;
        }

        if (this.pearls.getValue() && entity instanceof EntityEnderPearl) {
            ret = true;
        }

        if (Minecraft.getMinecraft().player.getRidingEntity() != null && entity == Minecraft.getMinecraft().player.getRidingEntity()) {
            ret = false;
        }

        return ret;
    }

    private int getColor(Entity entity) {
        int ret = -1;

        if (entity instanceof IAnimals && !(entity instanceof IMob)) {
            ret = 0xFF00FF44;
        }
        if (entity instanceof IMob) {
            ret = 0xFFFFAA00;
        }
        if (entity instanceof EntityBoat || entity instanceof EntityMinecart || entity instanceof EntityMinecartContainer) {
            ret = 0xFF00FFAA;
        }
        if (entity instanceof EntityItem) {
            ret = 0xFF00FFAA;
        }
        if (entity instanceof EntityEnderCrystal) {
            ret = 0xFFCD00CD;
        }
        if (entity instanceof EntityEnderPearl) {
            ret = 0xFFCD00CD;
        }
        if (entity instanceof EntityPlayer) {
            ret = 0xFFFF4444;

            if (entity == Minecraft.getMinecraft().player) {
                ret = -1;
            }

            if (entity.isSneaking()) {
                ret = 0xFFEE9900;
            }

            if (Seppuku.INSTANCE.getFriendManager().isFriend(entity) != null) {
                ret = 0xFF9900EE;
            }
        }
        return ret;
    }

    private float[] convertBounds(Entity e, float partialTicks, int width, int height) {
        float x = -1;
        float y = -1;
        float w = width + 1;
        float h = height + 1;

        final Vec3d pos = MathUtil.interpolateEntity(e, partialTicks);

        if (pos == null) {
            return null;
        }

        AxisAlignedBB bb = e.getEntityBoundingBox();

        if (e instanceof EntityEnderCrystal) {
            bb = new AxisAlignedBB(bb.minX + 0.3f, bb.minY + 0.2f, bb.minZ + 0.3f, bb.maxX - 0.3f, bb.maxY, bb.maxZ - 0.3f);
        }

        if (e instanceof EntityItem) {
            bb = new AxisAlignedBB(bb.minX, bb.minY + 0.7f, bb.minZ, bb.maxX, bb.maxY, bb.maxZ);
        }

        bb = bb.expand(0.15f, 0.1f, 0.15f);

        camera.setPosition(Minecraft.getMinecraft().getRenderViewEntity().posX, Minecraft.getMinecraft().getRenderViewEntity().posY, Minecraft.getMinecraft().getRenderViewEntity().posZ);

        if (!camera.isBoundingBoxInFrustum(bb)) {
            return null;
        }

        final Vec3d corners[] = {
                new Vec3d(bb.minX - bb.maxX + e.width / 2, 0, bb.minZ - bb.maxZ + e.width / 2),
                new Vec3d(bb.maxX - bb.minX - e.width / 2, 0, bb.minZ - bb.maxZ + e.width / 2),
                new Vec3d(bb.minX - bb.maxX + e.width / 2, 0, bb.maxZ - bb.minZ - e.width / 2),
                new Vec3d(bb.maxX - bb.minX - e.width / 2, 0, bb.maxZ - bb.minZ - e.width / 2),

                new Vec3d(bb.minX - bb.maxX + e.width / 2, bb.maxY - bb.minY, bb.minZ - bb.maxZ + e.width / 2),
                new Vec3d(bb.maxX - bb.minX - e.width / 2, bb.maxY - bb.minY, bb.minZ - bb.maxZ + e.width / 2),
                new Vec3d(bb.minX - bb.maxX + e.width / 2, bb.maxY - bb.minY, bb.maxZ - bb.minZ - e.width / 2),
                new Vec3d(bb.maxX - bb.minX - e.width / 2, bb.maxY - bb.minY, bb.maxZ - bb.minZ - e.width / 2)
        };

        for (Vec3d vec : corners) {
            final GLUProjection.Projection projection = GLUProjection.getInstance().project(pos.x + vec.x - Minecraft.getMinecraft().getRenderManager().viewerPosX, pos.y + vec.y - Minecraft.getMinecraft().getRenderManager().viewerPosY, pos.z + vec.z - Minecraft.getMinecraft().getRenderManager().viewerPosZ, GLUProjection.ClampMode.NONE, false);

            if (projection == null) {
                return null;
            }

            x = Math.max(x, (float) projection.getX());
            y = Math.max(y, (float) projection.getY());

            w = Math.min(w, (float) projection.getX());
            h = Math.min(h, (float) projection.getY());
        }

        if (x != -1 && y != -1 && w != width + 1 && h != height + 1) {
            return new float[]{x, y, w, h};
        }

        return null;
    }

    public static class FootstepData {
        private double x;
        private double y;
        private double z;
        private long time;

        public FootstepData(double x, double y, double z, long time) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.time = time;
        }

        public double getX() {
            return x;
        }

        public void setX(double x) {
            this.x = x;
        }

        public double getY() {
            return y;
        }

        public void setY(double y) {
            this.y = y;
        }

        public double getZ() {
            return z;
        }

        public void setZ(double z) {
            this.z = z;
        }

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }
    }

}
