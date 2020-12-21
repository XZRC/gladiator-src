package com.client.glowclient.clickgui;

import com.client.glowclient.clickgui.buttons.*;
import com.client.glowclient.clickgui.buttons.submenu.SubMenu;
import com.client.glowclient.clickgui.utils.ColorUtils;
import com.client.glowclient.utils.extra.pepsimod.PUtils;
import com.client.glowclient.utils.render.Colors;
import com.client.glowclient.utils.render.SurfaceBuilder;
import com.client.glowclient.utils.render.SurfaceHelper;
import me.rigamortis.seppuku.Seppuku;
import me.rigamortis.seppuku.api.module.Module;
import me.rigamortis.seppuku.api.value.Value;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Window extends BaseButton {
    private final String text;
    private final Minecraft mc;
    public boolean isOpen = true;
    private List buttons = Collections.synchronizedList(new ArrayList());
    private int modulesCounted = 0;
    private int scroll = 0;
    private int renderYButton = 0;
    private boolean isDragging = false;
    private int dragX = 0;
    private int dragY = 0;
    private Module.ModuleType category;

    public Window(int x, int y, String name, Module.ModuleType category) {
        super(x, y, 100, 12);
        this.text = name;
        this.category = category;
        this.mc = Minecraft.getMinecraft();
    }

    public void processMouseClick(int mouseX, int mouseY, int button) {
        this.updateIsMouseHovered(mouseX, mouseY);
        if (this.isMouseHovered()) {
            if (button == 0) {
                this.isDragging = true;
                this.dragX = mouseX - this.getX();
                this.dragY = mouseY - this.getY();
            } else if (button == 1) {
                this.isOpen = !this.isOpen;
            }
        }

        Iterator var4 = this.buttons.iterator();

        while (var4.hasNext()) {
            BaseButton but = (BaseButton) var4.next();
            if (but.shouldRender()) {
                but.processMouseClick(mouseX, mouseY, button);
            }
        }

    }

    public void processMouseRelease(int mouseX, int mouseY, int button) {
        this.updateIsMouseHovered(mouseX, mouseY);
        if (this.isDragging) {
            this.isDragging = false;
        }

        Iterator var4 = this.buttons.iterator();

        while (var4.hasNext()) {
            BaseButton but = (BaseButton) var4.next();
            if (but.shouldRender()) {
                but.processMouseRelease(mouseX, mouseY, button);
            }
        }

    }

    public void processKeyPress(char character, int key) {
        Iterator var3 = this.buttons.iterator();

        while (var3.hasNext()) {
            BaseButton but = (BaseButton) var3.next();
            if (but.shouldRender()) {
                but.processKeyPress(character, key);
            }
        }

    }

    public void draw(int mouseX, int mouseY) {
        int red = 0x00;
        int green = 0x00;
        int blue = 0xFF;
        if (this.isDragging) {
            this.setX(mouseX - this.dragX);
            this.setY(mouseY - this.dragY);
        }

        GL11.glPushMatrix();
        GL11.glPushAttrib(1284);
        this.scroll = Math.max(0, this.scroll);
        this.scroll = Math.min(this.getDisplayableCount() - this.getModulesToDisplay(), this.scroll);
        this.updateIsMouseHovered(mouseX, mouseY);
        this.renderYButton = this.getY();
        SurfaceHelper.drawRect(this.getX(), this.getY(), this.getWidth(), 13, Colors.toRGBA(red, green, blue, 225));
        if (this.isOpen) {
            SurfaceHelper.drawRect(this.getX(), this.getY() + 13, this.getWidth(), this.getDisplayedHeight() + 4 - 13, Colors.toRGBA(0, 0, 0, 150));
        } else {
            SurfaceHelper.drawRect(this.getX(), this.getY() + 13, this.getWidth(), this.getDisplayedHeight() + 1 - 13, Colors.toRGBA(0, 0, 0, 150));
        }

        GL11.glColor3f(0.0F, 0.0F, 0.0F);
        SurfaceBuilder builder = new SurfaceBuilder();
        builder.reset().task(SurfaceBuilder::enableBlend).task(SurfaceBuilder::enableFontRendering).fontRenderer(ClickGUI.fontRenderer).color(Colors.WHITE).text(this.text, (double) (this.getX() + 2 + 1), (double) (this.getY() + 2 + 1), true).color(Colors.WHITE).text(this.text, (double) (this.getX() + 2), (double) (this.getY() + 2));
//      if (ClickGui.fontRenderer != null) {
//         builder.reset().task(SurfaceBuilder::enableBlend).task(SurfaceBuilder::enableFontRendering).fontRenderer(ClickGui.fontRenderer).color(Colors.WHITE).text(this.text, (double)(this.getX() + 2 + 1), (double)(this.getY() + 2 + 1), true).color(Colors.WHITE).text(this.text, (double)(this.getX() + 2), (double)(this.getY() + 2));
//      } else {
//         builder.reset().task(SurfaceBuilder::enableBlend).task(SurfaceBuilder::enableFontRendering).fontRenderer(ClickGui.fontRenderer).color(Colors.WHITE).text(this.text, (double)(this.getX() + 2), (double)(this.getY() + 2), true);
//      }

        if (this.isOpen) {
            int i;
            if (this.shouldScroll()) {
                i = this.getScrollbarHeight();
                int barY = this.getScrollbarY();
                barY = Math.min(barY, (int)((this.getScrollingModuleCount() * ClickGUI.fontRenderer.getHeight() * 2.05) - 1 - i));
                PUtils.drawRect((float) (this.getX() + 97), (float) (this.getY() + 13 + barY + 5), (float) (this.getX() + 99), (float) Math.min(this.getY() + 13 + barY + i, this.getY() + this.getDisplayedHeight()), Colors.toRGBA(red, green, blue, 255));
            }

            this.modulesCounted = 0;

            for (i = this.getScroll(); i < this.getModulesToDisplay() + this.getScroll(); ++i) {
                BaseButton but = this.getNextEntry();
                ++this.modulesCounted;
                but.draw(mouseX, mouseY);
            }
        }

        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

    public int getHeight() {
        int i = this.height;
        Iterator var2 = this.buttons.iterator();

        while (var2.hasNext()) {
            BaseButton but = (BaseButton) var2.next();
            if (but.shouldRender()) {
                i += ClickGUI.fontRenderer.getHeight() * 2.05;
            }
        }

        return i;
    }

    public void openGui() {
        Iterator var1 = this.buttons.iterator();

        while (var1.hasNext()) {
            BaseButton but = (BaseButton) var1.next();
            but.openGui();
        }

    }

    public boolean shouldRender() {
        return true;
    }

    public String getName() {
        return this.text;
    }

    public boolean isOpen() {
        return this.isOpen;
    }

    public void setOpen(boolean val) {
        this.isOpen = val;
    }

    public int getColor() {
        return ColorUtils.getColorForGuiEntry(1, this.isMouseHovered(), false);
    }

    public void init(Module.ModuleType category) {
        List<Module> modules = Seppuku.INSTANCE.getModuleManager().getModuleList(category);

        for (Module module : modules) {
            Button b = this.addButton(new Button(this, module));
            this.addSubBind(new SubBind(b));
            for (Value setting : module.getValueList()) {
                Object value = setting.getValue();
                if (value.getClass().isEnum()) {
                    this.addSubMode(new SubMode(b, setting));
                } else if (value instanceof Number) {
                    this.addSubSlider(new SubSlider(b, setting));
                } else if (value instanceof Boolean) {
                    this.addSubButton(new SubButton(b, setting));
                }
            }
        }
    }

    private Button addButton(Button b) {
        this.buttons.add(b);
        return b;
    }

    private SubButton addSubButton(SubButton b) {
        this.buttons.add(this.buttons.indexOf(b.getParent()) + 1, b);
        b.getParent().getSubEntries().add(b);
        return b;
    }

    private SubBind addSubBind(SubBind b) {
        this.buttons.add(this.buttons.indexOf(b.getParent()) + 1, b);
        b.getParent().getSubEntries().add(b);
        return b;
    }

    private SubMode addSubMode(SubMode b) {
        this.buttons.add(this.buttons.indexOf(b.getParent()) + 1, b);
        b.getParent().getSubEntries().add(b);
        return b;
    }

    private SubSlider addSubSlider(SubSlider slider) {
        this.buttons.add(this.buttons.indexOf(slider.getParent()) + 1, slider);
        slider.getParent().getSubEntries().add(slider);
        return slider;
    }

    private SubMenu addSubMenu(SubMenu b) {
        this.buttons.add(this.buttons.indexOf(b.getParent()) + 1, b);
        b.getParent().getSubEntries().add(b);
        return b;
    }

    private BaseButton getNextEntry() {
        int a = 0;
        int i = this.scroll;

        while (true) {
            BaseButton but = (BaseButton) this.buttons.get(i);
            if (but.shouldRender()) {
                if (this.modulesCounted == 0 || a >= this.modulesCounted) {
                    return but;
                }

                ++a;
            }

            ++i;
        }
    }

    public int getRenderYButton() {
        return this.renderYButton += ClickGUI.fontRenderer.getHeight() * 2.05;
    }

    private int getDisplayedHeight() {
        int max = this.maxDisplayHeight();
        int normal = this.getHeight();
        return Math.min(max + 14, normal);
    }

    private void updateIsMouseHoveredFull(int mouseX, int mouseY) {
        int x = this.getX();
        int y = this.getY();
        int maxX = x + this.width;
        int maxY = y + this.getDisplayedHeight();
        this.isHoveredCached = x <= mouseX && mouseX <= maxX && y <= mouseY && mouseY <= maxY;
    }

    private int getScrollbarHeight() {
        double maxHeight = (double) this.maxDisplayHeight();
        double maxAllowedModules = (double) this.getScrollingModuleCount();
        double displayable = (double) this.getDisplayableCount();
        return (int) Math.floor(maxHeight * (maxAllowedModules / displayable));
    }

    private int getScrollbarY() {
        int displayable = this.getDisplayableCount();
        int rest = displayable - this.scroll;
        int resultRaw = displayable - rest;
        return (int) (resultRaw * ClickGUI.fontRenderer.getHeight() * 2.05);
    }

    private int getScroll() {
        return this.shouldScroll() ? this.scroll : 1;
    }

    private boolean shouldScroll() {
        return this.getScrollingModuleCount() - 1 < this.getDisplayableCount();
    }

    public void handleScroll(int dWheel, int x, int y) {
        this.updateIsMouseHoveredFull(x, y);
        if (this.isMouseHovered() && this.shouldScroll()) {
            this.scroll += dWheel;
        }

    }

    private int maxDisplayHeight() {
        Minecraft MC = FMLClientHandler.instance().getClient();
        ScaledResolution scaledresolution = new ScaledResolution(MC);
        int height = scaledresolution.getScaledHeight();
        height = Math.floorDiv(height, (int) (ClickGUI.fontRenderer.getHeight() * 2.05));
        --height;
        height *= ClickGUI.fontRenderer.getHeight() * 2.05;
        return height;
    }

    private int getDisplayableCount() {
        int i = 0;
        Iterator var2 = this.buttons.iterator();

        while (var2.hasNext()) {
            BaseButton but = (BaseButton) var2.next();
            if (but.shouldRender()) {
                ++i;
            }
        }

        return i;
    }

    private int getScrollingModuleCount() {
        ScaledResolution scaledresolution = new ScaledResolution(Minecraft.getMinecraft());
        int height = scaledresolution.getScaledHeight();
        height = Math.floorDiv(height, (int) (ClickGUI.fontRenderer.getHeight() * 2.05));
        --height;
        return height;
    }

    private int getModulesToDisplay() {
        return this.shouldScroll() ? this.getScrollingModuleCount() : this.getDisplayableCount();
    }

    public Module.ModuleType getCategory() {
        return this.category;
    }
}
