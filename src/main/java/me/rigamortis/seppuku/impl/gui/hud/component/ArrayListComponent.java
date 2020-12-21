package me.rigamortis.seppuku.impl.gui.hud.component;

import com.client.glowclient.utils.render.fonts.MinecraftFontRenderer;
import com.mojang.realmsclient.gui.ChatFormatting;
import me.rigamortis.seppuku.Seppuku;
import me.rigamortis.seppuku.api.gui.hud.component.DraggableHudComponent;
import me.rigamortis.seppuku.api.module.Module;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Author Seth
 * 7/25/2019 @ 7:24 AM.
 */
public final class ArrayListComponent extends DraggableHudComponent {

    public ArrayListComponent() {
        super("ArrayList");
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);

        final List<Module> mods = new ArrayList<>();

        for (Module mod : Seppuku.INSTANCE.getModuleManager().getModuleList()) {
            if (mod != null && mod.getType() != Module.ModuleType.HIDDEN && mod.isEnabled() && !mod.isHidden()) {
                mods.add(mod);
            }
        }

        final Comparator<Module> comparator = (first, second) -> {
            final String firstName = first.getDisplayName() + (first.getMetaData() != null ? "\u00A78 " + ChatFormatting.DARK_GRAY + "\u00A78[" + ChatFormatting.WHITE + first.getMetaData().toLowerCase() + ChatFormatting.DARK_GRAY + "\u00A78]" : "\u00A78");
            final String secondName = second.getDisplayName() + (second.getMetaData() != null ? "\u00A78 " + ChatFormatting.DARK_GRAY + "\u00A78[" + ChatFormatting.WHITE + second.getMetaData().toLowerCase() + ChatFormatting.DARK_GRAY + "\u00A78]" : "\u00A78");
            final float dif = Seppuku.INSTANCE.getFontManager().getStringWidth(secondName) - Seppuku.INSTANCE.getFontManager().getStringWidth(firstName);
            return dif != 0 ? (int) dif : secondName.compareTo(firstName);
        };

        mods.sort(comparator);

        float xOffset = 0;
        float yOffset = 0;
        float maxWidth = 0;

        for (Module mod : mods) {
            if (mod != null && mod.getType() != Module.ModuleType.HIDDEN && mod.isEnabled() && !mod.isHidden()) {
                final String name = mod.getDisplayName() + (mod.getMetaData() != null ? "\u00A78 " + ChatFormatting.DARK_GRAY + "\u00A78[" + ChatFormatting.WHITE + mod.getMetaData().toLowerCase() + ChatFormatting.DARK_GRAY + "\u00A78]" : "\u00A78");

                final float width = Seppuku.INSTANCE.getFontManager().getStringWidth(name);

                if (width >= maxWidth) {
                    maxWidth = width;
                }

                if (this.getAnchorPoint() != null) {
                    switch (this.getAnchorPoint().getPoint()) {
                        case TOP_CENTER:
                            xOffset = (this.getW() - Seppuku.INSTANCE.getFontManager().getStringWidth(name)) / 2;
                            break;
                        case TOP_LEFT:
                        case BOTTOM_LEFT:
                            xOffset = 0;
                            break;
                        case TOP_RIGHT:
                        case BOTTOM_RIGHT:
                            xOffset = this.getW() - Seppuku.INSTANCE.getFontManager().getStringWidth(name);
                            break;
                    }
                }

                if (this.getAnchorPoint() != null) {
                    switch (this.getAnchorPoint().getPoint()) {
                        case TOP_CENTER:
                        case TOP_LEFT:
                        case TOP_RIGHT:
                            Seppuku.INSTANCE.getFontManager().drawString(name, this.getX() + xOffset, this.getY() + yOffset, mod.getColor(), true, false);
                            yOffset += (Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT + 1);
                            break;
                        case BOTTOM_LEFT:
                        case BOTTOM_RIGHT:
                            Seppuku.INSTANCE.getFontManager().drawString(name, this.getX() + xOffset, this.getY() + (this.getH() - Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT) + yOffset, mod.getColor(), true, false);
                            yOffset -= (Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT + 1);
                            break;
                    }
                } else {
                    Seppuku.INSTANCE.getFontManager().drawString(name, this.getX() + xOffset, this.getY() + yOffset, mod.getColor(), true, false);
                    yOffset += (Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT + 1);
                }
            }
        }

        this.setW(maxWidth);
        this.setH(Math.abs(yOffset));
    }

}
