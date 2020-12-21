package com.client.glowclient.clickgui.buttons;

import com.client.glowclient.clickgui.BaseButton;
import com.client.glowclient.clickgui.ClickGUI;
import com.client.glowclient.clickgui.utils.ColorUtils;
import com.client.glowclient.clickgui.utils.GuiUtils;
import com.client.glowclient.utils.render.Colors;
import com.client.glowclient.utils.render.SurfaceBuilder;
import com.client.glowclient.utils.render.SurfaceHelper;
import me.rigamortis.seppuku.api.value.Value;
import org.lwjgl.opengl.GL11;

import static com.client.glowclient.clickgui.ClickGUI.*;

public class SubButton extends BaseButton {
   private final Button parent;
   private Value<Boolean> option;

   public SubButton(Button parent, Value<Boolean> option) {
      super(parent.getWindow().getX() + 4, parent.getY() + 4, parent.getWindow().getWidth() - 8, 14);
      this.parent = parent;
      this.window = parent.getWindow();
      this.option = option;
   }

   public void processMouseClick(int mouseX, int mouseY, int button) {
      this.updateIsMouseHovered(mouseX, mouseY);
      if (this.isMouseHovered() && button == 0) {
         GuiUtils.toggleSetting(this.option);
      }

   }

   public void draw(int mouseX, int mouseY) {
      this.y = this.window.getRenderYButton();
      this.x = this.window.getX() + 4;
      this.updateIsMouseHovered(mouseX, mouseY);
      SurfaceHelper.drawRect(this.getX(), this.getY(), this.getWidth(), this.height, this.getColor());
      SurfaceHelper.drawRect(this.getX(), this.getY(), -1, this.height, Colors.toRGBA(red, green, blue, 255));
      GL11.glColor3f(0.0F, 0.0F, 0.0F);
      SurfaceBuilder builder = new SurfaceBuilder();
      if (ClickGUI.fontRenderer != null) {
         builder.reset();
         builder.task(SurfaceBuilder::enableBlend);
         builder.task(SurfaceBuilder::enableFontRendering);
         builder.fontRenderer(ClickGUI.fontRenderer);
         builder.color(Colors.WHITE);
         builder.text(this.option.getName(), (double) (this.getX() + 2 + 1), (double) (this.getY() + 2 + 1), true);
         builder.color(Colors.WHITE);
         builder.text(this.option.getName(), (double) (this.getX() + 2), (double) (this.getY() + 2));
      } else {
         builder.reset();
         builder.task(SurfaceBuilder::enableBlend);
         builder.task(SurfaceBuilder::enableFontRendering);
         builder.fontRenderer(ClickGUI.fontRenderer);
         builder.color(Colors.WHITE);
         builder.text(this.option.getName(), (double) (this.getX() + 2), (double) (this.getY() + 2), true);
      }

      if (this.isMouseHovered()) {
         SurfaceHelper.drawOutlinedRectShaded(mouseX, mouseY + 3, (int)SurfaceHelper.getStringWidth(ClickGUI.fontRenderer, this.option.getDesc()) + 2, -12, Colors.toRGBA(10, 10, 10, 255), 175, 1.0F);
         builder.reset().fontRenderer(ClickGUI.fontRenderer).color(Colors.WHITE).text(this.option.getDesc(), (double)(mouseX + 1), (double)(mouseY - 7));
      }

   }

   public int getColor() {
      return ColorUtils.getColorForGuiEntry(3, this.isMouseHovered(), this.option.getValue());
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
