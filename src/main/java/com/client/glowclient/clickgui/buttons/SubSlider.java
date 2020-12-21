package com.client.glowclient.clickgui.buttons;

import com.client.glowclient.clickgui.BaseButton;
import com.client.glowclient.clickgui.ClickGUI;
import com.client.glowclient.clickgui.utils.ColorUtils;
import com.client.glowclient.clickgui.utils.GuiUtils;
import com.client.glowclient.utils.render.Colors;
import com.client.glowclient.utils.render.SurfaceBuilder;
import com.client.glowclient.utils.render.SurfaceHelper;
import me.rigamortis.seppuku.api.value.Value;
import static com.client.glowclient.clickgui.ClickGUI.red;
import static com.client.glowclient.clickgui.ClickGUI.green;
import static com.client.glowclient.clickgui.ClickGUI.blue;
import org.lwjgl.opengl.GL11;

public class SubSlider extends BaseButton {
   private final Button parent;
   private Value<Number> option;
   private float value;
   private int currentWidth;
   private boolean dragging = false;

   public SubSlider(Button parent, Value<Number> option) {
      super(parent.getWindow().getX() + 4, parent.getY() + 4, parent.getWindow().getWidth() - 8, 14);
      this.parent = parent;
      this.window = parent.getWindow();
      if (option != null) {
         this.value = option.getValue().floatValue();
      }

      this.option = option;
   }

   public void processMouseClick(int mouseX, int mouseY, int button) {
      this.updateIsMouseHovered(mouseX, mouseY);
      if (this.isMouseHovered() && button == 0) {
         this.dragging = true;
      }
   }

   public void processMouseRelease(int mouseX, int mouseY, int button) {
      this.updateIsMouseHovered(mouseX, mouseY);
      if (this.dragging && button == 0) {
         this.dragging = false;
         this.getWidthFromValue();
      }

   }

   public void draw(int mouseX, int mouseY) {
      if (this.dragging) {
         this.currentWidth = mouseX - this.getX();
         if (this.currentWidth < 0) {
            this.currentWidth = 0;
         } else if (this.currentWidth > 92) {
            this.currentWidth = 92;
         }

         this.updateValueFromWidth();
      }

      this.y = this.window.getRenderYButton();
      this.x = this.window.getX() + 4;
      this.updateIsMouseHovered(mouseX, mouseY);
      SurfaceHelper.drawRect(this.getX(), this.getY(), -1, this.height, Colors.toRGBA(red, green, blue, 255));
      SurfaceHelper.drawRect(this.getX(), this.getY(), this.getWidth(), this.height, Colors.toRGBA(0, 0, 0, 0));
      SurfaceHelper.drawRect(this.getX(), this.getY(), this.currentWidth, this.height, this.getColor());
      GL11.glColor3f(0.0F, 0.0F, 0.0F);
      SurfaceBuilder builder = new SurfaceBuilder();
      if (ClickGUI.fontRenderer != null) {
         builder.reset();
         builder.task(SurfaceBuilder::enableBlend);
         builder.task(SurfaceBuilder::enableFontRendering);
         builder.fontRenderer(ClickGUI.fontRenderer);
         builder.color(Colors.WHITE);
         builder.text(this.option.getName() + ": " + GuiUtils.roundSlider(this.value), (double) (this.getX() + 2), (double) (this.getY() + 2), true);
         builder.color(Colors.WHITE);
         builder.text(this.option.getName() + ": " + GuiUtils.roundSlider(this.value), (double) (this.getX() + 2), (double) (this.getY() + 2));
      } else {
         builder.reset();
         builder.task(SurfaceBuilder::enableBlend);
         builder.task(SurfaceBuilder::enableFontRendering);
         builder.fontRenderer(ClickGUI.fontRenderer);
         builder.color(Colors.WHITE);
         builder.text(this.option.getName() + ": " + GuiUtils.roundSlider(this.value), (double) (this.getX() + 2), (double) (this.getY() + 2), true);
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

   public void openGui() {
      if (this.option != null) {
         this.value = this.option.getValue().floatValue();
      }

      this.getWidthFromValue();
   }

   public String getName() {
      return this.option.getName();
   }

   protected int getWidthFromValue() {
      float val = this.value;
      val -= this.getMin();
      val /= this.getMax() - this.getMin();
      val *= 92.0F;
      return this.currentWidth = (int)GuiUtils.reCheckSliderRange(val, 0.0F, 92.0F);
   }

   protected void updateValueFromWidth() {
      float val = (float)this.currentWidth / 92.0F;
      val *= this.getMax() - this.getMin();
      val += this.getMin();
      val = GuiUtils.roundSliderStep(val, this.getStep());
      val = GuiUtils.reCheckSliderRange(val, this.getMin(), this.getMax());
      this.value = val;
      Double roundedValue = GuiUtils.roundSliderForConfig(val);
      if (this.option.getValue() instanceof Long) {
         this.option.setValue(roundedValue.longValue());
      } else if (this.option.getValue() instanceof Integer) {
         this.option.setValue(roundedValue.intValue());
      } else if (this.option.getValue() instanceof Float) {
         this.option.setValue(roundedValue.floatValue());
      } else if (this.option.getValue() instanceof Double) {
         this.option.setValue(roundedValue);
      }
   }

   public Button getParent() {
      return this.parent;
   }

   public float getMax() {
      Number inc = this.option.getMax();
      return inc == null ? 100.0F : inc.floatValue();
   }

   public float getMin() {
      Number inc = this.option.getMin();
      return inc == null ? 0.0F : inc.floatValue();
   }

   public float getStep() {
      Number inc = this.option.getInc();
      return inc == null ? 1.0F : inc.floatValue();
   }
}
