package com.client.glowclient.utils.extra.pepsimod;

import java.awt.Color;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.PrimitiveIterator;
import java.util.Random;
import java.util.Timer;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.Vector3d;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemEgg;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemLingeringPotion;
import net.minecraft.item.ItemSnowball;
import net.minecraft.item.ItemSplashPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.lwjgl.opengl.GL11;

public class PUtils {
   private static final Minecraft MC = Minecraft.getMinecraft();
   public static final Timer timer = new Timer();
   public static final String[] CLIENT_CREATORS = new String[0];

   public static String buttonPrefix = "§c";
   public static Color RAINBOW_COLOR = new Color(0, 0, 0);
   public static Field block_id = null;
   private static Random random = new Random(System.currentTimeMillis());

   public static int rand(int min, int max) {
      return min == max ? max : min + random.nextInt(max - min);
   }

   public static boolean rand() {
      return random.nextBoolean();
   }

   public static int ceilDiv(int x, int y) {
      return Math.floorDiv(x, y) + (x % y == 0 ? 0 : 1);
   }

   public static int ensureRange(int value, int min, int max) {
      return Math.min(Math.max(value, min), max);
   }

   public static boolean canEntityBeSeen(Entity entityIn, EntityPlayer player, TargettingTranslator.TargetBone bone) {
      return entityIn.world.rayTraceBlocks(new Vec3d(player.posX, player.posY + (double)player.getEyeHeight(), player.posZ), new Vec3d(entityIn.posX, getTargetHeight(entityIn, bone), entityIn.posZ), false, true, false) == null;
   }

   public static double getTargetHeight(Entity entity, TargettingTranslator.TargetBone bone) {
      double targetHeight = entity.posY;
      if (bone == TargettingTranslator.TargetBone.HEAD) {
         targetHeight = (double)entity.getEyeHeight();
      } else if (bone == TargettingTranslator.TargetBone.MIDDLE) {
         targetHeight = (double)(entity.getEyeHeight() / 2.0F);
      }

      return targetHeight;
   }

   public static void setBlockIdFields() {
      try {
         if (block_id != null) {
            return;
         }

         Class clazz = Block.class;
         block_id = clazz.getDeclaredField("block id");
         Method setPepsimod_id = clazz.getDeclaredMethod("setBlock_id");
         Block.REGISTRY.forEach((block) -> {
            try {
               setPepsimod_id.invoke(block);
            } catch (Throwable var3) {
               var3.printStackTrace();
               FMLCommonHandler.instance().exitJava(8742043, true);
            }

         });
      } catch (Throwable var2) {
         var2.printStackTrace();
         FMLCommonHandler.instance().exitJava(2349573, true);
      }

   }

   public static int getBlockId(Block block) {
      try {
         return (Integer)block_id.get(block);
      } catch (Throwable var2) {
         var2.printStackTrace();
         FMLCommonHandler.instance().exitJava(97348562, true);
         return -1;
      }
   }

   public static AxisAlignedBB cloneBB(AxisAlignedBB bb) {
      return new AxisAlignedBB(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ);
   }

   public static Vector3d vector3d(BlockPos pos) {
      Vector3d v3d = new Vector3d();
      v3d.x = (double)pos.getX();
      v3d.y = (double)pos.getY();
      v3d.z = (double)pos.getZ();
      return v3d;
   }

   public static Vector3d sub(Vector3d in, Vector3d with) {
      in.x -= with.x;
      in.y -= with.y;
      in.z -= with.z;
      return in;
   }

   public static int toRGBA(int r, int g, int b, int a) {
      return (r << 16) + (g << 8) + (b << 0) + (a << 24);
   }

   public static Vec3d getInterpolatedPos(Entity entity, float ticks) {
      return (new Vec3d(entity.lastTickPosX, entity.lastTickPosY, entity.lastTickPosZ)).add(getInterpolatedAmount(entity, (double)ticks));
   }

   public static Vec3d getInterpolatedAmount(Entity entity, double x, double y, double z) {
      return new Vec3d((entity.posX - entity.lastTickPosX) * x, (entity.posY - entity.lastTickPosY) * y, (entity.posZ - entity.lastTickPosZ) * z);
   }

   public static Vec3d getInterpolatedAmount(Entity entity, Vec3d vec) {
      return getInterpolatedAmount(entity, vec.x, vec.y, vec.z);
   }

   public static Vec3d getInterpolatedAmount(Entity entity, double ticks) {
      return getInterpolatedAmount(entity, ticks, ticks, ticks);
   }

   public static boolean isThrowable(ItemStack stack) {
      Item item = stack.getItem();
      return item instanceof ItemBow || item instanceof ItemSnowball || item instanceof ItemEgg || item instanceof ItemEnderPearl || item instanceof ItemSplashPotion || item instanceof ItemLingeringPotion || item instanceof ItemFishingRod;
   }

   public static String roundCoords(double d) {
      return String.format("%.2f", d);
   }

   public static String getFacing() {
      Entity entity = MC.getRenderViewEntity();
      EnumFacing enumfacing = entity.getHorizontalFacing();
      String s = "Invalid";
      switch(enumfacing) {
      case NORTH:
         s = "-Z";
         break;
      case SOUTH:
         s = "+Z";
         break;
      case WEST:
         s = "-X";
         break;
      case EAST:
         s = "+X";
      }

      return s;
   }

   public static boolean isClientCreator(String uuid) {
      String[] var1 = CLIENT_CREATORS;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         String s = var1[var3];
         if (s.equals(uuid)) {
            return true;
         }
      }

      return false;
   }

   public static void renderItem(int x, int y, float partialTicks, EntityPlayer player, ItemStack stack) {
      if (!stack.isEmpty()) {
         GlStateManager.pushMatrix();
         RenderHelper.enableGUIStandardItemLighting();
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

         try {
            GlStateManager.translate(0.0F, 0.0F, 32.0F);
            MC.getRenderItem().zLevel = 200.0F;
            MC.getRenderItem().renderItemAndEffectIntoGUI(stack, x, y);
            MC.getRenderItem().renderItemOverlayIntoGUI(MC.fontRenderer, stack, x, y, "");
            MC.getRenderItem().zLevel = 0.0F;
         } catch (Exception var6) {
            var6.printStackTrace();
         }

         RenderHelper.disableStandardItemLighting();
         GlStateManager.popMatrix();
      }

   }

   public static ItemStack getWearingArmor(int armorType) {
      return MC.player.inventoryContainer.getSlot(5 + armorType).getStack();
   }

   public static void drawNameplateNoScale(FontRenderer fontRendererIn, String str, float x, float y, float z, int verticalShift, float viewerYaw, float viewerPitch, boolean isThirdPersonFrontal, boolean isSneaking) {
      GlStateManager.pushMatrix();
      GlStateManager.translate(x, y, z);
      GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(-viewerYaw, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate((float)(isThirdPersonFrontal ? -1 : 1) * viewerPitch, 1.0F, 0.0F, 0.0F);
      isSneaking = false;
      double distance = Math.max(1.6D, (double)(MC.getRenderViewEntity().getDistance(MC.player) / 4.0F));
      distance /= 100.0D;
      GlStateManager.scale(-distance, -distance, distance);
      GlStateManager.disableLighting();
      GlStateManager.depthMask(false);
      GlStateManager.disableDepth();
      GlStateManager.enableBlend();
      GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
      int i = fontRendererIn.getStringWidth(str) / 2;
      GlStateManager.disableTexture2D();
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
      bufferbuilder.pos((double)(-i - 1), (double)(-1 + verticalShift), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
      bufferbuilder.pos((double)(-i - 1), (double)(8 + verticalShift), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
      bufferbuilder.pos((double)(i + 1), (double)(8 + verticalShift), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
      bufferbuilder.pos((double)(i + 1), (double)(-1 + verticalShift), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
      tessellator.draw();
      GlStateManager.enableTexture2D();
      fontRendererIn.drawString(str, -fontRendererIn.getStringWidth(str) / 2, verticalShift, 553648127);
      GlStateManager.enableDepth();
      GlStateManager.depthMask(true);
      fontRendererIn.drawString(str, -fontRendererIn.getStringWidth(str) / 2, verticalShift, isSneaking ? 553648127 : -1);
      GlStateManager.enableLighting();
      GlStateManager.disableBlend();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.popMatrix();
   }

   public static void renderFloatingText(String text, float x, float y, float z, int color, boolean renderBlackBackground, float partialTickTime) {
      RenderManager renderManager = MC.getRenderManager();
      float playerX = (float)(MC.player.lastTickPosX + (MC.player.posX - MC.player.lastTickPosX) * (double)partialTickTime);
      float playerY = (float)(MC.player.lastTickPosY + (MC.player.posY - MC.player.lastTickPosY) * (double)partialTickTime);
      float playerZ = (float)(MC.player.lastTickPosZ + (MC.player.posZ - MC.player.lastTickPosZ) * (double)partialTickTime);
      float dx = x - playerX;
      float dy = y - playerY;
      float dz = z - playerZ;
      float distanceRatio = (float)(5.0D / MC.player.getDistance((double)x, (double)y, (double)z));
      dx *= distanceRatio;
      dy *= distanceRatio;
      dz *= distanceRatio;
      float scale = 0.03F;
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.5F);
      GL11.glPushMatrix();
      GL11.glTranslatef(dx, dy, dz);
      GL11.glRotatef(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
      GL11.glRotatef(renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
      GL11.glScalef(-scale, -scale, scale);
      GL11.glDisable(2896);
      GL11.glDepthMask(false);
      GL11.glDisable(2929);
      GL11.glEnable(3042);
      GL11.glBlendFunc(770, 771);
      int textWidth = MC.fontRenderer.getStringWidth(text);
      int lineHeight = 10;
      if (renderBlackBackground) {
         int stringMiddle = textWidth / 2;
         Tessellator tessellator = Tessellator.getInstance();
         BufferBuilder buffer = tessellator.getBuffer();
         GlStateManager.disableTexture2D();
         buffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
         buffer.pos((double)(-stringMiddle - 1), -1.0D, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
         buffer.pos((double)(-stringMiddle - 1), (double)(8 + lineHeight - lineHeight), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
         buffer.pos((double)(stringMiddle + 1), (double)(8 + lineHeight - lineHeight), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
         buffer.pos((double)(stringMiddle + 1), -1.0D, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
         tessellator.draw();
         GlStateManager.enableTexture2D();
      }

      MC.fontRenderer.drawString(text, -textWidth / 2, 0, color);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glDepthMask(true);
      GL11.glEnable(2929);
      GL11.glPopMatrix();
   }

   public static void renderFloatingItemIcon(float x, float y, float z, Item item, float partialTickTime) {
      RenderManager renderManager = MC.getRenderManager();
      float playerX = (float)(MC.player.lastTickPosX + (MC.player.posX - MC.player.lastTickPosX) * (double)partialTickTime);
      float playerY = (float)(MC.player.lastTickPosY + (MC.player.posY - MC.player.lastTickPosY) * (double)partialTickTime);
      float playerZ = (float)(MC.player.lastTickPosZ + (MC.player.posZ - MC.player.lastTickPosZ) * (double)partialTickTime);
      float dx = x - playerX;
      float dy = y - playerY;
      float dz = z - playerZ;
      float scale = 0.025F;
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.75F);
      GL11.glPushMatrix();
      GL11.glTranslatef(dx, dy, dz);
      GL11.glRotatef(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
      GL11.glRotatef(renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
      GL11.glScalef(-scale, -scale, scale);
      GL11.glDisable(2896);
      GL11.glDepthMask(false);
      GL11.glDisable(2929);
      GL11.glEnable(3042);
      GL11.glBlendFunc(770, 771);
      renderItemTexture(-8.0D, -8.0D, item, 16, 16);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glDepthMask(true);
      GL11.glEnable(2929);
      GL11.glPopMatrix();
   }

   public static void renderItemTexture(double x, double y, Item item, int width, int height) {
      IBakedModel iBakedModel = MC.getRenderItem().getItemModelMesher().getItemModel(new ItemStack(item));
      TextureAtlasSprite textureAtlasSprite = MC.getTextureMapBlocks().getAtlasSprite(iBakedModel.getParticleTexture().getIconName());
      MC.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
      renderTexture(x, y, textureAtlasSprite, width, height, 0.0D);
   }

   private static void renderTexture(double x, double y, TextureAtlasSprite textureAtlasSprite, int width, int height, double zLevel) {
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder worldrenderer = tessellator.getBuffer();
      worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
      worldrenderer.pos(x, y + (double)height, zLevel).tex((double)textureAtlasSprite.getMaxU(), (double)textureAtlasSprite.getMaxV()).endVertex();
      worldrenderer.pos(x + (double)width, y + (double)height, zLevel).tex((double)textureAtlasSprite.getMinU(), (double)textureAtlasSprite.getMaxV()).endVertex();
      worldrenderer.pos(x + (double)width, y, zLevel).tex((double)textureAtlasSprite.getMinU(), (double)textureAtlasSprite.getMinV()).endVertex();
      worldrenderer.pos(x, y, zLevel).tex((double)textureAtlasSprite.getMaxU(), (double)textureAtlasSprite.getMinV()).endVertex();
      tessellator.draw();
   }

   public static int getBestTool(Block block) {
      float best = -1.0F;
      int index = -1;

      for(int i = 0; i < 9; ++i) {
         ItemStack itemStack = MC.player.inventory.getStackInSlot(i);
         if (itemStack != null) {
            float str = itemStack.getItem().getDestroySpeed(itemStack, block.getDefaultState());
            if (str > best) {
               best = str;
               index = i;
            }
         }
      }

      return index;
   }

   public static int getArmorType(ItemArmor armor) {
      return armor.armorType.ordinal() - 2;
   }

   public static boolean isAttackable(EntityLivingBase entity) {
      return entity != null && entity != MC.player && entity.isEntityAlive();
   }

   public static EntityLivingBase getClosestEntityWithoutReachFactor() {
      EntityLivingBase closestEntity = null;
      double distance = 9999.0D;
      Iterator var3 = MC.world.loadedEntityList.iterator();

      while(var3.hasNext()) {
         Object object = var3.next();
         if (object instanceof EntityLivingBase) {
            EntityLivingBase entity = (EntityLivingBase)object;
            if (isAttackable(entity)) {
               double newDistance = MC.player.getDistanceSq(entity);
               if (closestEntity != null) {
                  if (distance > newDistance) {
                     closestEntity = entity;
                     distance = newDistance;
                  }
               } else {
                  closestEntity = entity;
                  distance = newDistance;
               }
            }
         }
      }

      return closestEntity;
   }

   public static void drawRect(float paramXStart, float paramYStart, float paramXEnd, float paramYEnd, int paramColor) {
      float alpha = (float)(paramColor >> 24 & 255) / 255.0F;
      float red = (float)(paramColor >> 16 & 255) / 255.0F;
      float green = (float)(paramColor >> 8 & 255) / 255.0F;
      float blue = (float)(paramColor & 255) / 255.0F;
      GL11.glPushMatrix();
      GL11.glEnable(3042);
      GL11.glDisable(3553);
      GL11.glBlendFunc(770, 771);
      GL11.glEnable(2848);
      GL11.glColor4f(red, green, blue, alpha);
      GL11.glBegin(7);
      GL11.glVertex2d((double)paramXEnd, (double)paramYStart);
      GL11.glVertex2d((double)paramXStart, (double)paramYStart);
      GL11.glVertex2d((double)paramXStart, (double)paramYEnd);
      GL11.glVertex2d((double)paramXEnd, (double)paramYEnd);
      GL11.glEnd();
      GL11.glEnable(3553);
      GL11.glDisable(2848);
      GL11.glPopMatrix();
   }

   public static Vec3d adjustVectorForBone(Vec3d vec3d, Entity entity, TargettingTranslator.TargetBone bone) {
      return vec3d;
   }

}
