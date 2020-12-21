package com.client.glowclient.clickgui.buttons;

import com.client.glowclient.clickgui.BaseButton;
import com.client.glowclient.clickgui.ClickGUI;
import com.client.glowclient.clickgui.Window;
import com.client.glowclient.clickgui.utils.ColorUtils;
import com.client.glowclient.utils.SimpleTimer;
import com.client.glowclient.utils.render.Colors;
import com.client.glowclient.utils.render.SurfaceBuilder;
import com.client.glowclient.utils.render.SurfaceHelper;
import me.rigamortis.seppuku.api.value.Value;
import org.lwjgl.opengl.GL11;

public class SubMode extends BaseButton {
   private final Button parent;
   private Window window;
   private Value<Enum> option;
   private boolean reset = false;
   private SimpleTimer timer = new SimpleTimer();

   public SubMode(Button parent, Value<Enum> option) {
      super(parent.getWindow().getX() + 4, parent.getY() + 4, parent.getWindow().getWidth() - 8, 14);
      this.parent = parent;
      this.window = parent.getWindow();
      this.option = option;
   }

   public void processMouseClick(int mouseX, int mouseY, int button) {
      this.updateIsMouseHovered(mouseX, mouseY);
      if (!this.reset) {
         this.timer.start();
         this.reset = true;
      }

      if (this.isMouseHovered() && this.parent.isOpen()) {
         int arrayNumber;
         if (button == 0) {
            arrayNumber = option.getValue().ordinal() + 1;

            if (arrayNumber != option.getValue().getClass().getEnumConstants().length) {
               this.option.setValue(option.getValue().getClass().getEnumConstants()[arrayNumber]);
            } else {
               this.option.setValue(option.getValue().getClass().getEnumConstants()[0]);
            }
         }

         if (button == 1 && this.timer.hasTimeElapsed(50.0D)) {
            arrayNumber = this.option.getValue().ordinal() - 1;
            if (arrayNumber != -1) {
               this.option.setValue(option.getValue().getClass().getEnumConstants()[arrayNumber]);
            } else {
               this.option.setValue(option.getValue().getClass().getEnumConstants()[option.getValue().getClass().getEnumConstants().length-1]);
            }
         }
      }

   }

   public void draw(int mouseX, int mouseY) {
      int red = 0x00;
      int green = 0x00;
      int blue = 0xFF;
      this.y = this.window.getRenderYButton();
      this.x = this.window.getX() + 4;
      this.updateIsMouseHovered(mouseX, mouseY);
      SurfaceHelper.drawRect(this.getX(), this.getY(), this.getWidth(), this.height, this.getColor());
      SurfaceHelper.drawRect(this.getX(), this.getY(), -1, this.height, Colors.toRGBA(red, green, blue, 255));
      GL11.glColor3f(0.0F, 0.0F, 0.0F);
      SurfaceBuilder builder = new SurfaceBuilder();
      if (ClickGUI.fontRenderer != null) {
         builder.reset()
                 .task(SurfaceBuilder::enableBlend)
                 .task(SurfaceBuilder::enableFontRendering)
                 .fontRenderer(ClickGUI.fontRenderer)
                 .color(Colors.WHITE)
                 .text(this.option.getName() + ": " + this.option.getValue(), (double)(this.getX() + 2 + 1), (double)(this.getY() + 2 + 1), true)
                 .color(Colors.WHITE)
                 .text(this.option.getName() + ": " + this.option.getValue(), (double)(this.getX() + 2), (double)(this.getY() + 2));
      } else {
         builder
                 .reset()
                 .task(SurfaceBuilder::enableBlend)
                 .task(SurfaceBuilder::enableFontRendering)
                 .fontRenderer(ClickGUI.fontRenderer)
                 .color(Colors.WHITE)
                 .text(this.option.getName() + ": " + this.option.getValue(), (double)(this.getX() + 2), (double)(this.getY() + 2), true);
      }

      if (this.isMouseHovered()) {
         SurfaceHelper.drawOutlinedRectShaded(mouseX, mouseY + 3, (int)SurfaceHelper.getStringWidth(ClickGUI.fontRenderer, this.option.getDesc()) + 2, -12, Colors.toRGBA(10, 10, 10, 255), 175, 1.0F);
         builder.reset().fontRenderer(ClickGUI.fontRenderer).color(Colors.WHITE).text(this.option.getDesc(), (double)(mouseX + 1), (double)(mouseY - 7));
      }

   }

   public int getColor() {
      return ColorUtils.getColorForGuiEntry(2, this.isMouseHovered(), false);
   }

   public boolean shouldRender() {
      return this.parent.isOpen() && this.parent.shouldRender();
   }

   public String getName() {
      return this.option.getName();
   }

   public Button getParent() {
      return this.parent;
   }
}
