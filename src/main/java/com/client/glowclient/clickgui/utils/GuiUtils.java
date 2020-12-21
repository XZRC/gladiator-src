package com.client.glowclient.clickgui.utils;

import com.client.glowclient.clickgui.ClickGUI;
import com.client.glowclient.utils.render.Colors;
import com.client.glowclient.utils.render.SurfaceBuilder;
import com.client.glowclient.utils.render.SurfaceHelper;
import me.rigamortis.seppuku.api.module.Module;
import me.rigamortis.seppuku.api.value.Value;
import me.rigamortis.seppuku.impl.management.FontManager;

public class GuiUtils {
   private static int red = 0x00;
   private static int green = 0x00;
   private static int blue = 0xFF;
   public static void drawButtonMenu(String name, int x, int y, int w, int h, int mouseX, int mouseY) {
      SurfaceBuilder builder = new SurfaceBuilder();
      int Color;
      if (mouseX > x && mouseX < x + w && mouseY > y && mouseY < y + h) {
         Color = Colors.toRGBA(150, 150, 150, 50);
      } else {
         Color = Colors.toRGBA(0, 0, 0, 0);
      }

      SurfaceHelper.drawRect(x, y, w, h, Colors.toRGBA(0, 0, 0, 150));
      SurfaceHelper.drawRect(x, y, w, h, Color);
      SurfaceHelper.drawRect(x, y, w, 1, Colors.toRGBA(red, green, blue, 255));
      SurfaceHelper.drawRect(x, y + h, w, 1, Colors.toRGBA(red, green, blue, 255));
      if (ClickGUI.fontRenderer != null) {
         builder.reset().fontRenderer(ClickGUI.fontRenderer).color(Colors.SHADOW).textcentered(name, (double)(x + w / 2 + 1), (double)(y + h / 2) - SurfaceHelper.getStringHeight(ClickGUI.fontRenderer) / 2.0D + 1.0D, false).color(Colors.WHITE).textcentered(name, (double)(x + w / 2), (double)(y + h / 2) - SurfaceHelper.getStringHeight(ClickGUI.fontRenderer) / 2.0D, false);
      } else {
         SurfaceHelper.drawTextShadowCentered(name, (float)(x + w / 2), (float)(y + h / 2), Colors.WHITE);
      }

   }

   public static void drawButtonMenuDesc(String name, int x, int y, int w, int h, int mouseX, int mouseY, String desc) {
      SurfaceBuilder builder = new SurfaceBuilder();
      int Color;
      if (mouseX > x && mouseX < x + w && mouseY > y && mouseY < y + h) {
         Color = Colors.toRGBA(150, 150, 150, 50);
      } else {
         Color = Colors.toRGBA(0, 0, 0, 0);
      }

      SurfaceHelper.drawRect(x, y, w, h, Colors.toRGBA(0, 0, 0, 150));
      SurfaceHelper.drawRect(x, y, w, h, Color);
      SurfaceHelper.drawRect(x, y, w, 1, Colors.toRGBA(red, green, blue, 255));
      SurfaceHelper.drawRect(x, y + h, w, 1, Colors.toRGBA(red, green, blue, 255));
      if (ClickGUI.fontRenderer != null) {
         builder.reset().fontRenderer(ClickGUI.fontRenderer).color(Colors.SHADOW).textcentered(name, (double)(x + w / 2 + 1), (double)(y + h / 2) - SurfaceHelper.getStringHeight(ClickGUI.fontRenderer) / 2.0D + 1.0D, false).color(Colors.WHITE).textcentered(name, (double)(x + w / 2), (double)(y + h / 2) - SurfaceHelper.getStringHeight(ClickGUI.fontRenderer) / 2.0D, false);
      } else {
         SurfaceHelper.drawTextShadowCentered(name, (float)(x + w / 2), (float)(y + h / 2), Colors.WHITE);
      }

      if (mouseX > x && mouseX < x + w && mouseY > y && mouseY < y + h && desc != null) {
         SurfaceHelper.drawOutlinedRectShaded(mouseX, mouseY + 2, (int)SurfaceHelper.getStringWidth(ClickGUI.fontRenderer, desc) + 2, -12, Colors.toRGBA(10, 10, 10, 255), 175, 1.0F);
         builder.reset();
         builder.fontRenderer(ClickGUI.fontRenderer);
         builder.color(Colors.WHITE);
         builder.text(desc, (double) (mouseX + 1), (double) (mouseY - 7));
      }

   }

   public static void drawButtonAntiPacket(Value<Boolean> sname, int x, int y, int w, int h, int mouseX, int mouseY) {
      SurfaceBuilder builder = new SurfaceBuilder();
      String desc = sname.getDesc();
      int color;
      if (mouseX > x && mouseX < x + w && mouseY > y && mouseY < y + h) {
         color = Colors.toRGBA(150, 150, 150, 50);
      } else {
         color = Colors.toRGBA(0, 0, 0, 0);
      }

      SurfaceHelper.drawRect(x, y, w, h, Colors.toRGBA(0, 0, 0, 150));
      SurfaceHelper.drawRect(x, y, w, h, color);
      SurfaceHelper.drawRect(x, y, w, 1, Colors.toRGBA(red, green, blue, 255));
      SurfaceHelper.drawRect(x, y + h, w, 1, Colors.toRGBA(red, green, blue, 255));
      if (SurfaceHelper.getStringWidth(ClickGUI.fontRenderer, sname.getName()) < 100.0D) {
         if (!sname.getValue()) {
            if (ClickGUI.fontRenderer != null) {
               builder.reset();
               builder.fontRenderer(ClickGUI.fontRenderer);
               builder.color(Colors.SHADOW);
               builder.textcentered(sname.getName(), (double) (x + w / 2 + 1), (double) (y + h / 2) - SurfaceHelper.getStringHeight(ClickGUI.fontRenderer) / 2.0D + 1.0D, false);
               builder.color(Colors.GRAY);
               builder.textcentered(sname.getName(), (double) (x + w / 2), (double) (y + h / 2) - SurfaceHelper.getStringHeight(ClickGUI.fontRenderer) / 2.0D, false);
            } else {
               SurfaceHelper.drawTextShadowCentered(sname.getName(), (float)(x + w / 2), (float)(y + h / 2), Colors.GRAY);
            }
         } else if (ClickGUI.fontRenderer != null) {
            builder.reset();
            builder.fontRenderer(ClickGUI.fontRenderer);
            builder.color(Colors.SHADOW);
            builder.textcentered(sname.getName(), (double) (x + w / 2 + 1), (double) (y + h / 2) - SurfaceHelper.getStringHeight(ClickGUI.fontRenderer) / 2.0D + 1.0D, false);
            builder.color(Colors.WHITE);
            builder.textcentered(sname.getName(), (double) (x + w / 2), (double) (y + h / 2) - SurfaceHelper.getStringHeight(ClickGUI.fontRenderer) / 2.0D, false);
         } else {
            SurfaceHelper.drawTextShadowCentered(sname.getName(), (float)(x + w / 2), (float)(y + h / 2), Colors.WHITE);
         }
      } else if (!sname.getValue()) {
         if (ClickGUI.fontRenderer != null) {
            builder.reset().fontRenderer(FontManager.Fonts.VERDANA75).color(Colors.SHADOW).textcentered(sname.getName(), (double)(x + 15 + w / 2 + 1), (double)(y + h / 2) - SurfaceHelper.getStringHeight(FontManager.Fonts.VERDANA75) / 2.0D + 1.0D, false).color(Colors.GRAY).textcentered(sname.getName(), (double)(x + 15 + w / 2), (double)(y + h / 2) - SurfaceHelper.getStringHeight(FontManager.Fonts.VERDANA75) / 2.0D, false);
         } else {
            SurfaceHelper.drawText(sname.getName(), (double)(x - 2 + w / 2) - SurfaceHelper.getStringWidth(FontManager.Fonts.VERDANA75, sname.getName()) / 2.0D, (double)(y - 2 + h / 2), Colors.GRAY, 0.75D);
         }
      } else if (ClickGUI.fontRenderer != null) {
         builder.reset().fontRenderer(FontManager.Fonts.VERDANA75).color(Colors.SHADOW).textcentered(sname.getName(), (double)(x + 15 + w / 2 + 1), (double)(y + h / 2) - SurfaceHelper.getStringHeight(FontManager.Fonts.VERDANA75) / 2.0D + 1.0D, false).color(Colors.WHITE).textcentered(sname.getName(), (double)(x + 15 + w / 2), (double)(y + h / 2) - SurfaceHelper.getStringHeight(FontManager.Fonts.VERDANA75) / 2.0D, false);
      } else {
         SurfaceHelper.drawText(sname.getName(), (double)(x - 2 + w / 2) - SurfaceHelper.getStringWidth(FontManager.Fonts.VERDANA75, sname.getName()) / 2.0D, (double)(y - 2 + h / 2), Colors.WHITE, 0.75D);
      }

   }

   public static void toggleMod(Module module) {
      module.toggle();
   }

   public static void toggleSetting(Value<Boolean> setting) {
      setting.setValue(!setting.getValue());
   }

   public static double roundSliderForConfig(double val) {
      return Double.parseDouble(String.format("%.2f", val));
   }

   public static String roundSlider(float f) {
      return String.format("%.2f", f);
   }

   public static float roundSliderStep(float input, float step) {
      return (float)Math.round(input / step) * step;
   }

   public static float reCheckSliderRange(float value, float min, float max) {
      return Math.min(Math.max(value, min), max);
   }
}
