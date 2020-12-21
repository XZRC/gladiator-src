package me.rigamortis.seppuku.impl.module.render;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.rigamortis.seppuku.Seppuku;
import me.rigamortis.seppuku.api.event.gui.EventRenderPotions;
import me.rigamortis.seppuku.api.event.render.EventRender2D;
import me.rigamortis.seppuku.api.module.Module;
import me.rigamortis.seppuku.api.value.Value;
import me.rigamortis.seppuku.impl.fml.SeppukuMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Author Seth
 * 4/7/2019 @ 10:17 PM.
 */
public final class GoodHudModule extends Module {

    public final Value<Boolean> watermark = new Value<>("Watermark", new String[]{"Water", "Wm"}, "", true);
    public final Value<Boolean> tps = new Value<>("TPS", new String[]{"tickrate"}, "", true);
    public final Value<Boolean> coords = new Value<>("Coordinates", new String[]{"Coordinate", "Coord", "Coords"}, "", true);
    public final Value<Boolean> potions = new Value<>("Potions", new String[]{"Potions", "Potion", "PotionEffects", "PotionEffect", "Pe"}, "", true);
    public final Value<Boolean> modules = new Value<>("Modules", new String[]{"Mods", "Mod"}, "", true);
    public final Value<Boolean> armor = new Value<>("Armor", new String[]{"Arm"}, "", true);

    private Map<String, Float> stringWidthLookup = new HashMap<>();

    public GoodHudModule() {
        super("Hud", new String[]{"Overlay"}, "Shows lots of useful info", "NONE", -1, ModuleType.RENDER);
    }

    public static String formatPotionEffect(PotionEffect first) {
        String firstEffectName = I18n.format(first.getPotion().getName());

        if (first.getAmplifier() == 1) {
            firstEffectName = firstEffectName + " " + I18n.format("enchantment.level.2");
        } else if (first.getAmplifier() == 2) {
            firstEffectName = firstEffectName + " " + I18n.format("enchantment.level.3");
        } else if (first.getAmplifier() == 3) {
            firstEffectName = firstEffectName + " " + I18n.format("enchantment.level.4");
        }

        return firstEffectName + " " + ChatFormatting.GRAY + Potion.getPotionDurationString(first, 1.0F);
    }

    @Listener
    public void render(EventRender2D event) {
        final Minecraft mc = Minecraft.getMinecraft();

        if (mc.gameSettings.showDebugInfo) {
            return;
        }

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();

        if (this.watermark.getValue()) {
            this.drawWatermark();
        }

        if (this.tps.getValue()) {
            this.drawTPS();
        }

        if (this.coords.getValue()) {
            this.drawCoords(event.getScaledResolution().getScaledWidth(), event.getScaledResolution().getScaledHeight());
        }

        if (this.potions.getValue()) {
            this.drawPotions(event.getScaledResolution().getScaledWidth(), event.getScaledResolution().getScaledHeight());
        }

        if (this.armor.getValue()) {
            this.drawArmor(event.getScaledResolution().getScaledWidth(), event.getScaledResolution().getScaledHeight());
        }

        if (this.modules.getValue()) {
            this.drawModules(event.getScaledResolution().getScaledWidth(), event.getScaledResolution().getScaledHeight());
        }

        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public void drawWatermark() {
        Seppuku.INSTANCE.getFontManager().drawStringWithShadow(ChatFormatting.ITALIC + "Gladiator " + ChatFormatting.GRAY + SeppukuMod.VERSION, 2, 2, -1);
    }

    public void drawTPS() {
        int startY = this.watermark.getValue() ? 2 + 10 : 2;
        Seppuku.INSTANCE.getFontManager().drawStringWithShadow(String.format(ChatFormatting.WHITE + "TPS: %.2f", Seppuku.INSTANCE.getTickRateManager().getTickRate()), 2, startY, -1);
    }

    private float getStringWidth(String string) {
        return stringWidthLookup.computeIfAbsent(string, s -> Float.valueOf(Seppuku.INSTANCE.getFontManager().getStringWidth(s)));
    }
    public void drawModules(int width, int height) {

        final List<Module> mods = Seppuku.INSTANCE.getModuleManager().getModuleList().stream()
                .filter(Objects::nonNull)
                .filter(Module::isEnabled)
                .filter(mod -> mod.getType() != ModuleType.HIDDEN)
                .filter(mod -> !mod.isHidden())
                .sorted((first, second) -> {
                    final String firstName = first.getDisplayName() + (first.getMetaData() != null ? " " + ChatFormatting.GRAY + first.getMetaData() : "");
                    final String secondName = second.getDisplayName() + (second.getMetaData() != null ? " " + ChatFormatting.GRAY + second.getMetaData() : "");
                    final float dif = getStringWidth(secondName) - getStringWidth(firstName);
                    return dif != 0 ? (int) dif : secondName.compareTo(firstName);
                })
                .collect(Collectors.toList());

    //    mods.sort((first, second) -> {
       //     final String firstName = first.getDisplayName() + (first.getMetaData() != null ? " " + ChatFormatting.GRAY + first.getMetaData() : "");
        //    final String secondName = second.getDisplayName() + (second.getMetaData() != null ? " " + ChatFormatting.GRAY + second.getMetaData() : "");
        //    final float dif = Seppuku.INSTANCE.getFontManager().getStringWidth(secondName) - Seppuku.INSTANCE.getFontManager().getStringWidth(firstName);
        //    return dif != 0 ? (int) dif : secondName.compareTo(firstName);
      //  });

        int space = 0;

        for (Module mod : mods) {
            final String name = mod.getDisplayName() + (mod.getMetaData() != null ? " " + ChatFormatting.GRAY + mod.getMetaData() : "");
            Seppuku.INSTANCE.getFontManager().drawStringWithShadow(name, width - Seppuku.INSTANCE.getFontManager().getStringWidth(name) - 2, 2 + space, mod.getColor());
            space += 10;
        }
    }

    public void drawCoords(int width, int height) {
        final DecimalFormat df = new DecimalFormat("#.#");

        final String current = ChatFormatting.GRAY + "x " + ChatFormatting.RESET +
                df.format(Minecraft.getMinecraft().player.posX) + ChatFormatting.RESET + "," +
                ChatFormatting.GRAY + " y " + ChatFormatting.RESET + df.format(Minecraft.getMinecraft().player.posY) + ChatFormatting.RESET + "," +
                ChatFormatting.GRAY + " z " + ChatFormatting.RESET + df.format(Minecraft.getMinecraft().player.posZ) + ChatFormatting.RESET;

        final String nether = ChatFormatting.GRAY + "x " + ChatFormatting.RED +
                df.format(Minecraft.getMinecraft().player.posX / 8) + ChatFormatting.RED + "," +
                ChatFormatting.GRAY + " y " + ChatFormatting.RED + df.format(Minecraft.getMinecraft().player.posY) + ChatFormatting.RED + "," +
                ChatFormatting.GRAY + " z " + ChatFormatting.RED + df.format(Minecraft.getMinecraft().player.posZ / 8) + ChatFormatting.RESET;

        final String overworld = ChatFormatting.GRAY + "x " + ChatFormatting.RED +
                df.format(Minecraft.getMinecraft().player.posX * 8) + ChatFormatting.RED + "," +
                ChatFormatting.GRAY + " y " + ChatFormatting.RED + df.format(Minecraft.getMinecraft().player.posY) + ChatFormatting.RED + "," +
                ChatFormatting.GRAY + " z " + ChatFormatting.RED + df.format(Minecraft.getMinecraft().player.posZ * 8) + ChatFormatting.RESET;

        if (Minecraft.getMinecraft().player.dimension == -1) {
            Seppuku.INSTANCE.getFontManager().drawStringWithShadow(overworld, 2, height - Seppuku.INSTANCE.getFontManager().getHeight() - (Minecraft.getMinecraft().currentScreen instanceof GuiChat ? 15 : 2) - 10, -1);
        } else {
            Seppuku.INSTANCE.getFontManager().drawStringWithShadow(nether, 2, height - Seppuku.INSTANCE.getFontManager().getHeight() - (Minecraft.getMinecraft().currentScreen instanceof GuiChat ? 15 : 2) - 10, -1);
        }

        Seppuku.INSTANCE.getFontManager().drawStringWithShadow(current, 2, height - Seppuku.INSTANCE.getFontManager().getHeight() - (Minecraft.getMinecraft().currentScreen instanceof GuiChat ? 15 : 2), -1);
    }

    public void drawPotions(int width, int height) {

        final List<PotionEffect> effects = new ArrayList<>(Minecraft.getMinecraft().player.getActivePotionEffects());

        effects.sort((first, second) -> {
            String firstEffect = formatPotionEffect(first);
            String secondEffect = formatPotionEffect(second);

            final float dif = Seppuku.INSTANCE.getFontManager().getStringWidth(secondEffect) - Seppuku.INSTANCE.getFontManager().getStringWidth(firstEffect);
            return dif != 0 ? (int) dif : secondEffect.compareTo(firstEffect);
        });

        int space = 0;

        for (PotionEffect potionEffect : effects) {
            if (potionEffect != null) {
                final String effect = formatPotionEffect(potionEffect);
                Seppuku.INSTANCE.getFontManager().drawStringWithShadow(effect, width - Seppuku.INSTANCE.getFontManager().getStringWidth(effect) - 2, height - Seppuku.INSTANCE.getFontManager().getHeight() - (Minecraft.getMinecraft().currentScreen instanceof GuiChat ? 15 : 2) - space, potionEffect.getPotion().getLiquidColor());
                space += 10;
            }
        }

    }

    public void drawArmor(int width, int height) {
        final Minecraft mc = Minecraft.getMinecraft();

        int space = 0;

        int x = width / 2 + 72;
        int y = height - (mc.player.getAir() < 300 ? 65 : 58);

        for (int i = 0; i <= 3; i++) {
            final ItemStack stack = mc.player.inventoryContainer.getSlot(8 - i).getStack();
            if (stack != ItemStack.EMPTY) {
                GlStateManager.pushMatrix();
                RenderHelper.enableGUIStandardItemLighting();
                mc.getRenderItem().renderItemAndEffectIntoGUI(stack, x - space, y);
                mc.getRenderItem().renderItemOverlays(mc.fontRenderer, stack, x - space, y);
                RenderHelper.disableStandardItemLighting();
                GlStateManager.popMatrix();
                space += 18;
            }
        }
    }

    @Listener
    public void renderPotionIcons(EventRenderPotions event) {
        event.setCanceled(true);
    }

    private float getFrameTime() {
        final int fps = Minecraft.getDebugFPS();
        return 1.0f / fps == 0 ? 1 : fps;
    }
}
