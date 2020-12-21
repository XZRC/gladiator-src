package com.client.glowclient.clickgui.buttons;

import com.client.glowclient.clickgui.BaseButton;
import com.client.glowclient.clickgui.ClickGUI;
import com.client.glowclient.clickgui.Window;
import com.client.glowclient.clickgui.utils.ColorUtils;
import com.client.glowclient.clickgui.utils.GuiUtils;
import com.client.glowclient.utils.render.Colors;
import com.client.glowclient.utils.render.SurfaceBuilder;
import com.client.glowclient.utils.render.SurfaceHelper;
import me.rigamortis.seppuku.api.module.Module;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Button extends BaseButton {
    private List subEntries = Collections.synchronizedList(new ArrayList());
    private boolean isOpen = false;
    private Module module;

    public Button(Window window, Module module) {
        super(window.getX() + 2, window.getY() + 2, window.getWidth() - 6, 14);
        this.window = window;
        this.module = module;
    }

    public void processMouseClick(int mouseX, int mouseY, int button) {
        this.updateIsMouseHovered(mouseX, mouseY);
        if (this.isMouseHovered()) {
            if (button == 0) {
                GuiUtils.toggleMod(this.module);
            }

            if (button == 1) {
                this.isOpen = !this.isOpen;
            }
        }

    }

    public void draw(int mouseX, int mouseY) {
        SurfaceBuilder builder = new SurfaceBuilder();
        this.y = this.window.getRenderYButton();
        this.x = this.window.getX() + 2;
        this.updateIsMouseHovered(mouseX, mouseY);
        if (ClickGUI.fontRenderer != null) {
           builder.reset();
           builder.task(SurfaceBuilder::enableBlend);
           builder.task(SurfaceBuilder::enableFontRendering);
           builder.fontRenderer(ClickGUI.fontRenderer);
           builder.color(this.getColor());
           builder.text(this.module.getDisplayName(), (double) (this.getX() + 2 + 1), (double) (this.getY() + 3 + 1), true);
           builder.color(this.getColor());
           builder.text(this.module.getDisplayName(), (double) (this.getX() + 2), (double) (this.getY() + 3));
        } else {
           builder.reset();
           builder.task(SurfaceBuilder::enableBlend);
           builder.task(SurfaceBuilder::enableFontRendering);
           builder.fontRenderer(ClickGUI.fontRenderer);
           builder.color(this.getColor());
           builder.text(this.module.getDisplayName(), (double) (this.getX() + 2), (double) (this.getY() + 3), true);
        }

        if (!this.isMouseHovered()) {
            SurfaceHelper.drawRect(this.getX(), this.getY(), this.getWidth(), this.height, Colors.toRGBA(0, 0, 0, 0));
        } else {
            SurfaceHelper.drawRect(this.getX(), this.getY(), this.getWidth(), this.height, Colors.toRGBA(100, 100, 100, 50));
            SurfaceHelper.drawOutlinedRectShaded(mouseX, mouseY + 3, (int) SurfaceHelper.getStringWidth(ClickGUI.fontRenderer, this.module.getDesc()) + 2, -12, Colors.toRGBA(10, 10, 10, 255), 175, 1.0F);
            builder.reset().fontRenderer(ClickGUI.fontRenderer).color(Colors.WHITE).text(this.module.getDesc(), (double) (mouseX + 1), (double) (mouseY - 7));
        }

    }

    public int getHeight() {
        int i = this.height;
        if (this.isOpen) {
            i += 15 * this.subEntries.size();
        }

        return i;
    }

    public int getColor() {
        return ColorUtils.getColorForGuiEntry(0, this.isMouseHovered(), this.module.isEnabled());
    }

    public String getName() {
        return this.module.getDisplayName();
    }

    public boolean isOpen() {
        return this.isOpen;
    }

    public void setOpen(boolean val) {
        this.isOpen = val;
    }

    public Module getModule() {
        return this.module;
    }

    public List getSubEntries() {
        return this.subEntries;
    }
}
