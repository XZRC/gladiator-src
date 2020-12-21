package com.client.glowclient.utils.render;

import me.rigamortis.seppuku.impl.management.FontManager;
import com.client.glowclient.utils.render.fonts.MinecraftFontRenderer;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;
import org.lwjgl.opengl.GL11;

public class SurfaceHelper {
   public static void drawString(@Nullable MinecraftFontRenderer fontRenderer, String text, double x, double y, int color, boolean shadow) {
      if (fontRenderer == null) {
         Minecraft.getMinecraft().fontRenderer.drawString(text, (float)Math.round(x), (float)Math.round(y), color, shadow);
      } else {
         fontRenderer.drawString(text, x, y, color, shadow);
      }

   }

   public static double getStringWidth(@Nullable MinecraftFontRenderer fontRenderer, String text) {
      return fontRenderer == null ? (double)Minecraft.getMinecraft().fontRenderer.getStringWidth(text) : (double)fontRenderer.getStringWidth(text);
   }

   public static double getStringHeight(@Nullable MinecraftFontRenderer fontRenderer) {
      return fontRenderer == null ? (double)Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT : (double)fontRenderer.getHeight();
   }

   public static void drawRect(int x, int y, int w, int h, int color) {
      GL11.glLineWidth(1.0F);
      Gui.drawRect(x, y, x + w, y + h, color);
   }

   public static void drawOutlinedRect(double x, double y, double w, double h, int color) {
      drawOutlinedRect(x, y, w, h, color, 1.0F);
   }

   public static void drawOutlinedRectShaded(int x, int y, int w, int h, int colorOutline, int shade, float width) {
      int shaded = 16777215 & colorOutline | (shade & 255) << 24;
      drawRect(x, y, w, h, shaded);
      drawOutlinedRect((double)x, (double)y, (double)w, (double)h, colorOutline, width);
   }

   public static void drawOutlinedRect(double x, double y, double w, double h, int color, float width) {
      float r = (float)(color >> 16 & 255) / 255.0F;
      float g = (float)(color >> 8 & 255) / 255.0F;
      float b = (float)(color & 255) / 255.0F;
      float a = (float)(color >> 24 & 255) / 255.0F;
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder BufferBuilder = tessellator.getBuffer();
      GlStateManager.enableBlend();
      GlStateManager.disableTexture2D();
      GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
      GlStateManager.color(r, g, b, a);
      GL11.glLineWidth(width);
      BufferBuilder.begin(2, DefaultVertexFormats.POSITION);
      BufferBuilder.pos(x, y, 0.0D).endVertex();
      BufferBuilder.pos(x, y + h, 0.0D).endVertex();
      BufferBuilder.pos(x + w, y + h, 0.0D).endVertex();
      BufferBuilder.pos(x + w, y, 0.0D).endVertex();
      tessellator.draw();
      GlStateManager.color(1.0F, 1.0F, 1.0F);
      GlStateManager.enableTexture2D();
      GlStateManager.disableBlend();
   }

   public static void drawTexturedRect(int x, int y, int textureX, int textureY, int width, int height, int zLevel) {
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder BufferBuilder = tessellator.getBuffer();
      BufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
      BufferBuilder.pos((double)(x + 0), (double)(y + height), (double)zLevel).tex((double)((float)(textureX + 0) * 0.00390625F), (double)((float)(textureY + height) * 0.00390625F)).endVertex();
      BufferBuilder.pos((double)(x + width), (double)(y + height), (double)zLevel).tex((double)((float)(textureX + width) * 0.00390625F), (double)((float)(textureY + height) * 0.00390625F)).endVertex();
      BufferBuilder.pos((double)(x + width), (double)(y + 0), (double)zLevel).tex((double)((float)(textureX + width) * 0.00390625F), (double)((float)(textureY + 0) * 0.00390625F)).endVertex();
      BufferBuilder.pos((double)(x + 0), (double)(y + 0), (double)zLevel).tex((double)((float)(textureX + 0) * 0.00390625F), (double)((float)(textureY + 0) * 0.00390625F)).endVertex();
      tessellator.draw();
   }

   public static void drawText(String msg, int x, int y, int color) {
      Minecraft.getMinecraft().fontRenderer.drawString(msg, x, y, color);
   }

   public static void drawTextShadow(String msg, int x, int y, int color) {
      Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(msg, (float)x, (float)y, color);
   }

   public static void drawTextShadowCentered(String msg, float x, float y, int color) {
      float offsetX = (float)getTextWidth(msg) / 2.0F;
      float offsetY = (float)getTextHeight() / 2.0F;
      Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(msg, x - offsetX, y - offsetY, color);
   }

   public static void drawText(String msg, double x, double y, int color, double scale, boolean shadow) {
      GlStateManager.pushMatrix();
      GlStateManager.disableDepth();
      GlStateManager.scale(scale, scale, scale);
      Minecraft.getMinecraft().fontRenderer.drawString(msg, (float)(x * (1.0D / scale)), (float)(y * (1.0D / scale)), color, shadow);
      GlStateManager.enableDepth();
      GlStateManager.popMatrix();
   }

   public static void drawText(String msg, double x, double y, int color, double scale) {
      drawText(msg, x, y, color, scale, false);
   }

   public static void drawTextShadow(String msg, double x, double y, int color, double scale) {
      drawText(msg, x, y, color, scale, true);
   }

   public static int getTextWidth(String text, double scale) {
      return (int)((double)Minecraft.getMinecraft().fontRenderer.getStringWidth(text) * scale);
   }

   public static int getTextWidth(String text) {
      return getTextWidth(text, 1.0D);
   }

   public static int getTextWidthFont(String text, double scale) {
      return (int)((double)FontManager.Fonts.VERDANA.getStringWidth(text) * scale);
   }

   public static int getTextWidthFont(String text) {
      return getTextWidthFont(text, 1.0D);
   }

   public static int getTextHeight() {
      return Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT;
   }

   public static int getTextHeight(double scale) {
      return (int)((double)Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT * scale);
   }

   public static void drawItem(ItemStack item, double x, double y) {
      GlStateManager.pushMatrix();
      RenderHelper.enableGUIStandardItemLighting();
      GlStateManager.disableLighting();
      GlStateManager.enableRescaleNormal();
      GlStateManager.enableColorMaterial();
      GlStateManager.enableLighting();
      Minecraft.getMinecraft().getRenderItem().zLevel = 100.0F;
      renderItemAndEffectIntoGUI(Minecraft.getMinecraft().player, item, x, y, 16.0D);
      Minecraft.getMinecraft().getRenderItem().zLevel = 0.0F;
      GlStateManager.popMatrix();
      GlStateManager.disableLighting();
      GlStateManager.enableDepth();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
   }

   public static void drawItemWithOverlay(ItemStack item, double x, double y, double scale) {
      GlStateManager.pushMatrix();
      RenderHelper.enableGUIStandardItemLighting();
      GlStateManager.disableLighting();
      GlStateManager.enableRescaleNormal();
      GlStateManager.enableColorMaterial();
      GlStateManager.enableLighting();
      Minecraft.getMinecraft().getRenderItem().zLevel = 100.0F;
      renderItemAndEffectIntoGUI(Minecraft.getMinecraft().player, item, x, y, 16.0D);
      renderItemOverlayIntoGUI(Minecraft.getMinecraft().fontRenderer, item, x, y, (String)null, scale);
      Minecraft.getMinecraft().getRenderItem().zLevel = 0.0F;
      GlStateManager.popMatrix();
      GlStateManager.disableLighting();
      GlStateManager.enableDepth();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
   }

   public static void drawPotionEffect(PotionEffect potion, int x, int y) {
      int index = potion.getPotion().getStatusIconIndex();
      GlStateManager.pushMatrix();
      RenderHelper.enableGUIStandardItemLighting();
      GlStateManager.disableLighting();
      GlStateManager.enableRescaleNormal();
      GlStateManager.enableColorMaterial();
      GlStateManager.enableLighting();
      GlStateManager.enableTexture2D();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      Minecraft.getMinecraft().getTextureManager().bindTexture(GuiContainer.INVENTORY_BACKGROUND);
      drawTexturedRect(x, y, index % 8 * 18, 198 + index / 8 * 18, 18, 18, 100);
      potion.getPotion().renderHUDEffect(x, y, potion, Minecraft.getMinecraft(), 255.0F);
      GlStateManager.disableLighting();
      GlStateManager.enableDepth();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.popMatrix();
   }

   public static void drawHead(ResourceLocation skinResource, double x, double y, float scale) {
      GlStateManager.pushMatrix();
      Minecraft.getMinecraft().renderEngine.bindTexture(skinResource);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.scale(scale, scale, scale);
      drawScaledCustomSizeModalRect(x * (double)(1.0F / scale), y * (double)(1.0F / scale), 8.0F, 8.0F, 8.0D, 8.0D, 12.0D, 12.0D, 64.0D, 64.0D);
      drawScaledCustomSizeModalRect(x * (double)(1.0F / scale), y * (double)(1.0F / scale), 40.0F, 8.0F, 8.0D, 8.0D, 12.0D, 12.0D, 64.0D, 64.0D);
      GlStateManager.popMatrix();
   }

   protected static void renderItemAndEffectIntoGUI(@Nullable EntityLivingBase living, ItemStack stack, double x, double y, double scale) {
      if (!stack.isEmpty()) {
         RenderItem var10000 = Minecraft.getMinecraft().getRenderItem();
         var10000.zLevel += 50.0F;

         try {
            renderItemModelIntoGUI(stack, x, y, Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(stack, (World)null, living), scale);
         } catch (Throwable var12) {
            var12.printStackTrace();
         } finally {
            var10000 = Minecraft.getMinecraft().getRenderItem();
            var10000.zLevel -= 50.0F;
         }
      }

   }

   private static void renderItemModelIntoGUI(ItemStack stack, double x, double y, IBakedModel bakedmodel, double scale) {
      GlStateManager.pushMatrix();
      Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
      Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
      GlStateManager.enableRescaleNormal();
      GlStateManager.enableAlpha();
      GlStateManager.alphaFunc(516, 0.1F);
      GlStateManager.enableBlend();
      GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.translate(x, y, 1.0D);
      GlStateManager.translate(8.0F, 8.0F, 0.0F);
      GlStateManager.scale(1.0F, -1.0F, 1.0F);
      GlStateManager.scale(scale, scale, scale);
      if (bakedmodel.isGui3d()) {
         GlStateManager.enableLighting();
      } else {
         GlStateManager.disableLighting();
      }

      bakedmodel = ForgeHooksClient.handleCameraTransforms(bakedmodel, ItemCameraTransforms.TransformType.GUI, false);
      Minecraft.getMinecraft().getRenderItem().renderItem(stack, bakedmodel);
      GlStateManager.disableAlpha();
      GlStateManager.disableRescaleNormal();
      GlStateManager.disableLighting();
      GlStateManager.popMatrix();
      Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
      Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
   }

   protected static void renderItemOverlayIntoGUI(FontRenderer fr, ItemStack stack, double xPosition, double yPosition, @Nullable String text, double scale) {
      double SCALE_RATIO = 1.23076923077D;
      if (!stack.isEmpty()) {
         if (stack.getCount() != 1 || text != null) {
            String s = text == null ? String.valueOf(stack.getCount()) : text;
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            GlStateManager.disableBlend();
            fr.drawStringWithShadow(s, (float)(xPosition + 19.0D - 2.0D - (double)fr.getStringWidth(s)), (float)(yPosition + 6.0D + 3.0D), 16777215);
            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
            GlStateManager.enableBlend();
         }

         if (stack.getItem().showDurabilityBar(stack)) {
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            GlStateManager.disableTexture2D();
            GlStateManager.disableAlpha();
            GlStateManager.disableBlend();
            double health = stack.getItem().getDurabilityForDisplay(stack);
            int rgbfordisplay = stack.getItem().getRGBDurabilityForDisplay(stack);
            int i = Math.round(13.0F - (float)health * 13.0F);
            draw(xPosition + scale / 8.0D, yPosition + scale / 1.23076923077D, 13.0D, 2.0D, 0, 0, 0, 255);
            draw(xPosition + scale / 8.0D, yPosition + scale / 1.23076923077D, (double)i, 1.0D, rgbfordisplay >> 16 & 255, rgbfordisplay >> 8 & 255, rgbfordisplay & 255, 255);
            GlStateManager.enableBlend();
            GlStateManager.enableAlpha();
            GlStateManager.enableTexture2D();
            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
         }

         EntityPlayerSP entityplayersp = Minecraft.getMinecraft().player;
         float f3 = entityplayersp == null ? 0.0F : entityplayersp.getCooldownTracker().getCooldown(stack.getItem(), Minecraft.getMinecraft().getRenderPartialTicks());
         if (f3 > 0.0F) {
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            GlStateManager.disableTexture2D();
            draw(xPosition, yPosition + scale * (double)(1.0F - f3), 16.0D, scale * (double)f3, 255, 255, 255, 127);
            GlStateManager.enableTexture2D();
            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
         }
      }

   }

   private static void draw(double x, double y, double width, double height, int red, int green, int blue, int alpha) {
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder renderer = tessellator.getBuffer();
      renderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
      renderer.pos(x + 0.0D, y + 0.0D, 0.0D).color(red, green, blue, alpha).endVertex();
      renderer.pos(x + 0.0D, y + height, 0.0D).color(red, green, blue, alpha).endVertex();
      renderer.pos(x + width, y + height, 0.0D).color(red, green, blue, alpha).endVertex();
      renderer.pos(x + width, y + 0.0D, 0.0D).color(red, green, blue, alpha).endVertex();
      Tessellator.getInstance().draw();
   }

   protected static void drawScaledCustomSizeModalRect(double x, double y, float u, float v, double uWidth, double vHeight, double width, double height, double tileWidth, double tileHeight) {
      double f = 1.0D / tileWidth;
      double f1 = 1.0D / tileHeight;
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
      bufferbuilder.pos(x, y + height, 0.0D).tex((double)u * f, (double)(v + (float)vHeight) * f1).endVertex();
      bufferbuilder.pos(x + width, y + height, 0.0D).tex((double)(u + (float)uWidth) * f, (double)(v + (float)vHeight) * f1).endVertex();
      bufferbuilder.pos(x + width, y, 0.0D).tex((double)(u + (float)uWidth) * f, (double)v * f1).endVertex();
      bufferbuilder.pos(x, y, 0.0D).tex((double)u * f, (double)v * f1).endVertex();
      tessellator.draw();
   }

   public static int getHeadWidth(float scale) {
      return (int)(scale * 12.0F);
   }

   public static int getHeadWidth() {
      return getHeadWidth(1.0F);
   }

   public static int getHeadHeight(float scale) {
      return (int)(scale * 12.0F);
   }

   public static int getHeadHeight() {
      return getHeadWidth(1.0F);
   }
}
