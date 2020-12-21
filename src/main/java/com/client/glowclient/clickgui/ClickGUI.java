package com.client.glowclient.clickgui;

import com.client.glowclient.utils.render.Colors;
import com.client.glowclient.utils.render.SurfaceHelper;
import java.io.IOException;

import com.client.glowclient.utils.render.fonts.MinecraftFontRenderer;
import me.rigamortis.seppuku.Seppuku;
import me.rigamortis.seppuku.impl.management.FontManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Mouse;

public class ClickGUI extends GuiScreen {
   private static ClickGUI INSTANCE;
   private Window[] windows;
   private Minecraft mc;
   public static MinecraftFontRenderer fontRenderer = FontManager.Fonts.VERDANA;
   public static int red   = 0x00;
   public static int green = 0x00;
   public static int blue  = 0xFF;
   public ClickGUI() {
      INSTANCE = this;
      this.mc = Minecraft.getMinecraft();
   }

   public boolean doesGuiPauseGame() {
      return false;
   }

   public void drawScreen(int x, int y, float ticks) {
      SurfaceHelper.drawRect(0, 0, this.width, this.height, Colors.toRGBA(0, 0, 0, 150));
      Window[] var4 = this.windows;
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         Window window = var4[var6];
         window.draw(x, y);
      }

      SurfaceHelper.drawRect(-1, -1, 1, 1, Colors.toRGBA(0, 0, 0, 150));
   }

   public void mouseClicked(int x, int y, int b) throws IOException {
      Window[] var4 = this.windows;
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         Window window = var4[var6];
         window.processMouseClick(x, y, b);
      }

      super.mouseClicked(x, y, b);
   }

   public void mouseReleased(int x, int y, int state) {
      Window[] var4 = this.windows;
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         Window window = var4[var6];
         window.processMouseRelease(x, y, state);
      }

      super.mouseReleased(x, y, state);
   }

   public void handleMouseInput() throws IOException {
      super.handleMouseInput();
      int dWheel = MathHelper.clamp(Mouse.getEventDWheel(), -1, 1);
      if (dWheel != 0) {
         dWheel *= -1;
         int x = Mouse.getEventX() * this.width / mc.displayWidth;
         int y = this.height - Mouse.getEventY() * this.height / mc.displayHeight - 1;
         Window[] var4 = this.windows;
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            Window window = var4[var6];
            window.handleScroll(dWheel, x, y);
         }
      }

   }

   protected void keyTyped(char eventChar, int eventKey) {
      Window[] var3 = this.windows;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Window window = var3[var5];
         window.processKeyPress(eventChar, eventKey);
      }

      if (eventKey == 1) {
         mc.displayGuiScreen((GuiScreen)null);
         if (mc.currentScreen == null) {
            mc.setIngameFocus();
         }
      }

   }

   @Override
   public void onGuiClosed() {
      Seppuku.INSTANCE.getConfigManager().saveAll();
      super.onGuiClosed();
   }

   public void setWindows(Window... windows) {
      this.windows = windows;
   }

   public void initWindows() {
      Window[] var1 = this.windows;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         Window window = var1[var3];
         window.init(window.getCategory());
      }

   }

   public static ClickGUI getInstance() {
      return INSTANCE;
   }

   public Window[] getWindows() {
      return this.windows;
   }
}
