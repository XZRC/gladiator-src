package com.client.glowclient.clickgui.buttons;

import com.client.glowclient.clickgui.BaseButton;
import com.client.glowclient.clickgui.ClickGUI;
import com.client.glowclient.clickgui.Window;
import com.client.glowclient.utils.render.Colors;
import com.client.glowclient.utils.render.SurfaceBuilder;
import com.client.glowclient.utils.render.SurfaceHelper;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import static com.client.glowclient.clickgui.ClickGUI.red;
import static com.client.glowclient.clickgui.ClickGUI.green;
import static com.client.glowclient.clickgui.ClickGUI.blue;
public class SubBind extends BaseButton {
   private final Button parent;
   private Window window;
   private boolean accepting = false;

   public SubBind(Button parent) {
      super(parent.getWindow().getX() + 4, parent.getY() + 4, parent.getWindow().getWidth() - 8, 14);
      this.parent = parent;
      this.window = parent.getWindow();
   }

   public void processMouseClick(int mouseX, int mouseY, int button) {
      this.updateIsMouseHovered(mouseX, mouseY);
      if (this.isMouseHovered() && button == 0) {
         this.accepting = true;
      }

   }

   public void processKeyPress(char character, int key) {
      if (this.accepting) {
         if (key != 211 && key != 14 && key != 1) {
            this.parent.getModule().setKey(Keyboard.getKeyName(key));
            this.accepting = false;
         } else {
            this.parent.getModule().setKey("NONE");
            this.accepting = false;
         }
      }

   }

   public void draw(int mouseX, int mouseY) {
      String keyname;
      if (!this.accepting) {
         keyname = "Bind: " + this.parent.getModule().getKey();
      } else {
         keyname = "Press a key...";
      }

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
         builder.text(keyname, (double) (this.getX() + 2 + 1), (double) (this.getY() + 2 + 1), true);
         builder.color(Colors.WHITE);
         builder.text(keyname, (double) (this.getX() + 2), (double) (this.getY() + 2));
      } else {
         builder.reset();
         builder.task(SurfaceBuilder::enableBlend);
         builder.task(SurfaceBuilder::enableFontRendering);
         builder.fontRenderer(ClickGUI.fontRenderer);
         builder.color(Colors.WHITE);
         builder.text(keyname, (double) (this.getX() + 2), (double) (this.getY() + 2), true);
      }

      if (this.isMouseHovered()) {
         SurfaceHelper.drawOutlinedRectShaded(mouseX, mouseY + 3, (int)SurfaceHelper.getStringWidth(ClickGUI.fontRenderer, "Keybind of mod") + 2, -12, Colors.toRGBA(10, 10, 10, 255), 175, 1.0F);
         builder.reset();
         builder.fontRenderer(ClickGUI.fontRenderer);
         builder.color(Colors.WHITE);
         builder.text("Keybind of mod", (double) (mouseX + 1), (double) (mouseY - 7));
      }

   }

   public int getColor() {
      return this.isMouseHovered() ? Colors.toRGBA(150, 150, 150, 50) : Colors.toRGBA(0, 0, 0, 50);
   }

   public boolean shouldRender() {
      return this.parent.isOpen() && this.parent.shouldRender();
   }

   public String getName() {
      return "Bind: " + this.parent.getModule().getKey();
   }

   public Button getParent() {
      return this.parent;
   }
}
