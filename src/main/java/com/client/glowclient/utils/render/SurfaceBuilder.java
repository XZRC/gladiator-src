package com.client.glowclient.utils.render;

import com.client.glowclient.clickgui.ClickGUI;
import com.client.glowclient.utils.render.fonts.MinecraftFontRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import java.util.Stack;

public class SurfaceBuilder {
    public static final int COLOR = 1;
    public static final int SCALE = 2;
    public static final int TRANSLATION = 4;
    public static final int ROTATION = 8;
    public static final int ALL = 15;
    private static final SurfaceBuilder INSTANCE = new SurfaceBuilder();
    private final Stack settings = new Stack();
    private final SurfaceBuilder.RenderSettings DEFAULT_SETTINGS = new SurfaceBuilder.RenderSettings();

    public static SurfaceBuilder getBuilder() {
        return INSTANCE;
    }

    public static void disableTexture2D() {
        GlStateManager.disableTexture2D();
    }

    public static void enableTexture2D() {
        GlStateManager.enableTexture2D();
    }

    public static void enableBlend() {
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
    }

    public static void disableBlend() {
        GlStateManager.disableBlend();
    }

    public static void enableFontRendering() {
        GlStateManager.disableDepth();
    }

    public static void disableFontRendering() {
        GlStateManager.enableDepth();
    }

    public static void enableItemRendering() {
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.disableLighting();
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableColorMaterial();
        GlStateManager.enableLighting();
    }

    public static void disableItemRendering() {
        GlStateManager.disableLighting();
        GlStateManager.enableDepth();
    }

    public static void clearColor() {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public SurfaceBuilder.RenderSettings current() {
        return !this.settings.isEmpty() ? (SurfaceBuilder.RenderSettings) this.settings.peek() : this.DEFAULT_SETTINGS;
    }

    public SurfaceBuilder begin(int mode) {
        GL11.glBegin(mode);
        return this;
    }

    public SurfaceBuilder beginLines() {
        return this.begin(1);
    }

    public SurfaceBuilder beginLineLoop() {
        return this.begin(2);
    }

    public SurfaceBuilder beginQuads() {
        return this.begin(7);
    }

    public SurfaceBuilder beginPolygon() {
        return this.begin(9);
    }

    public SurfaceBuilder end() {
        GL11.glEnd();
        return this;
    }

    public SurfaceBuilder autoApply(boolean enabled) {
        this.current().setAutoApply(enabled);
        return this;
    }

    public SurfaceBuilder apply() {
        return this.apply(15);
    }

    public SurfaceBuilder apply(int flags) {
        SurfaceBuilder.RenderSettings current = this.current();
        if ((flags & 1) == 1) {
            current.applyColor();
        }

        if ((flags & 2) == 2) {
            current.applyScale();
        }

        if ((flags & 4) == 4) {
            current.applyTranslation();
        }

        if ((flags & 8) == 8) {
            current.applyRotation();
        }

        return this;
    }

    public SurfaceBuilder reset() {
        return this.reset(15);
    }

    public SurfaceBuilder reset(int flags) {
        SurfaceBuilder.RenderSettings current = this.current();
        if ((flags & 1) == 1) {
            current.resetColor();
        }

        if ((flags & 2) == 2) {
            current.resetScale();
        }

        if ((flags & 4) == 4) {
            current.resetTranslation();
        }

        if ((flags & 8) == 8) {
            current.resetRotation();
        }

        return this;
    }

    public SurfaceBuilder push() {
        GlStateManager.pushMatrix();
        this.settings.push(new SurfaceBuilder.RenderSettings());
        return this;
    }

    public SurfaceBuilder pop() {
        if (!this.settings.isEmpty()) {
            this.settings.pop();
        }

        GlStateManager.popMatrix();
        return this;
    }

    public SurfaceBuilder color(double r, double g, double b, double a) {
        this.current().setColor4d(new double[]{MathHelper.clamp(r, 0.0D, 1.0D), MathHelper.clamp(g, 0.0D, 1.0D), MathHelper.clamp(b, 0.0D, 1.0D), MathHelper.clamp(a, 0.0D, 1.0D)});
        return this;
    }

    public SurfaceBuilder color(int buffer) {
        return this.color((double) (buffer >> 16 & 255) / 255.0D, (double) (buffer >> 8 & 255) / 255.0D, (double) (buffer & 255) / 255.0D, (double) (buffer >> 24 & 255) / 255.0D);
    }

    public SurfaceBuilder color(int r, int g, int b, int a) {
        return this.color((double) r / 255.0D, (double) g / 255.0D, (double) b / 255.0D, (double) a / 255.0D);
    }

    public SurfaceBuilder scale(double x, double y, double z) {
        this.current().setScale3d(new double[]{x, y, z});
        return this;
    }

    public SurfaceBuilder scale(double s) {
        return this.scale(s, s, s);
    }

    public SurfaceBuilder scale() {
        return this.scale(0.0D);
    }

    public SurfaceBuilder translate(double x, double y, double z) {
        this.current().setTranslate3d(new double[]{x, y, z});
        return this;
    }

    public SurfaceBuilder translate(double x, double y) {
        return this.translate(x, y, 0.0D);
    }

    public SurfaceBuilder rotate(double angle, double x, double y, double z) {
        this.current().setRotated4d(new double[]{angle, x, y, z});
        return this;
    }

    public SurfaceBuilder width(double width) {
        GlStateManager.glLineWidth((float) width);
        return this;
    }

    public SurfaceBuilder vertex(double x, double y, double z) {
        GL11.glVertex3d(x, y, z);
        return this;
    }

    public SurfaceBuilder vertex(double x, double y) {
        GL11.glVertex2d(x, y);
        return this;
    }

    public SurfaceBuilder line(double startX, double startY, double endX, double endY) {
        return this.vertex(startX, startY).vertex(endX, endY);
    }

    public SurfaceBuilder rectangle(double x, double y, double w, double h) {
        return this.vertex(x, y).vertex(x, y + h).vertex(x + w, y + h).vertex(x + w, y);
    }

    public SurfaceBuilder fontRenderer(MinecraftFontRenderer fontRenderer) {
        this.current().setFontRenderer(fontRenderer);
        return this;
    }

    public SurfaceBuilder text(String text, double x, double y, boolean shadow) {
        if (this.current().hasFontRenderer()) {
            this.current().getFontRenderer().drawString(text, x, y + 1.0D, Colors.toRGBA(this.current().getColor4d()), shadow);
        } else {
            GlStateManager.pushMatrix();
            GlStateManager.translate(x, y + 1.0D, 0.0D);
            Minecraft.getMinecraft().fontRenderer.drawString(text, 0.0F, 0.0F, Colors.toRGBA(this.current().getColor4d()), shadow);
            GlStateManager.popMatrix();
        }

        return this;
    }

    public SurfaceBuilder text(String text, double x, double y) {
        return this.text(text, x, y, false);
    }

    public SurfaceBuilder textcentered(String text, double x, double y, boolean shadow) {
        double offsetX = SurfaceHelper.getStringWidth(ClickGUI.fontRenderer, text) / 2.0D;
        double offsetY = SurfaceHelper.getStringHeight(ClickGUI.fontRenderer) / 2.0D;
        if (this.current().hasFontRenderer()) {
            this.current().getFontRenderer().drawString(text, x - SurfaceHelper.getStringWidth(ClickGUI.fontRenderer, text) / 2.0D, y, Colors.toRGBA(this.current().getColor4d()), shadow);
        } else {
            GlStateManager.pushMatrix();
            GlStateManager.translate(x, y + 1.0D, 0.0D);
            Minecraft.getMinecraft().fontRenderer.drawString(text, 0.0F, 0.0F, Colors.toRGBA(this.current().getColor4d()), shadow);
            GlStateManager.popMatrix();
        }

        return this;
    }

    public SurfaceBuilder task(Runnable task) {
        task.run();
        return this;
    }

    public SurfaceBuilder item(ItemStack stack, double x, double y) {
        Minecraft.getMinecraft().getRenderItem().zLevel = 100.0F;
        SurfaceHelper.renderItemAndEffectIntoGUI(Minecraft.getMinecraft().player, stack, x, y, this.current().hasScale() ? this.current().getScale3d()[0] : 16.0D);
        Minecraft.getMinecraft().getRenderItem().zLevel = 0.0F;
        return this;
    }

    public SurfaceBuilder itemOverlay(ItemStack stack, double x, double y) {
        SurfaceHelper.renderItemOverlayIntoGUI(Minecraft.getMinecraft().fontRenderer, stack, x, y, null, this.current().hasScale() ? this.current().getScale3d()[0] : 16.0D);
        return this;
    }

    public SurfaceBuilder head(ResourceLocation resource, double x, double y) {
        Minecraft.getMinecraft().renderEngine.bindTexture(resource);
        double scale = this.current().hasScale() ? this.current().getScale3d()[0] : 12.0D;
        SurfaceHelper.drawScaledCustomSizeModalRect(x * (1.0D / scale), y * (1.0D / scale), 8.0F, 8.0F, 8.0D, 8.0D, 12.0D, 12.0D, 64.0D, 64.0D);
        SurfaceHelper.drawScaledCustomSizeModalRect(x * (1.0D / scale), y * (1.0D / scale), 40.0F, 8.0F, 8.0D, 8.0D, 12.0D, 12.0D, 64.0D, 64.0D);
        return this;
    }

    public int getFontWidth(String text) {
        return this.current().hasFontRenderer() ? this.current().getFontRenderer().getStringWidth(text) : Minecraft.getMinecraft().fontRenderer.getStringWidth(text);
    }

    public int getFontHeight() {
        return this.current().hasFontRenderer() ? this.current().getFontRenderer().getHeight() : Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT;
    }

    public int getFontHeight(String text) {
        return this.getFontHeight();
    }

    private double _getScaled(int index, double p) {
        return p * (1.0D / this.current().getScale3d()[index]);
    }

    public double getScaledX(double x) {
        return this._getScaled(0, x);
    }

    public double getScaledY(double y) {
        return this._getScaled(1, y);
    }

    public double getScaledZ(double z) {
        return this._getScaled(2, z);
    }

    public double getScaled(double p) {
        return this.getScaledX(p);
    }

    public double getItemSize() {
        return 16.0D;
    }

    private static class RenderSettings {
        private static final double[] EMPTY_VECTOR3D = new double[]{0.0D, 0.0D, 0.0D};
        private static final double[] EMPTY_VECTOR4D = new double[]{0.0D, 0.0D, 0.0D, 0.0D};
        private double[] color4d;
        private double[] scale3d;
        private double[] translate3d;
        private double[] rotated4d;
        private boolean autoApply;
        private MinecraftFontRenderer fontRenderer;

        private RenderSettings() {
            this.color4d = EMPTY_VECTOR4D;
            this.scale3d = EMPTY_VECTOR3D;
            this.translate3d = EMPTY_VECTOR3D;
            this.rotated4d = EMPTY_VECTOR4D;
            this.autoApply = true;
            this.fontRenderer = null;
        }

        // $FF: synthetic method
        RenderSettings(Object x0) {
            this();
        }

        public double[] getColor4d() {
            return this.color4d;
        }

        public void setColor4d(double[] color4d) {
            this.color4d = color4d;
            if (this.autoApply) {
                this.applyColor();
            }

        }

        public double[] getScale3d() {
            return this.scale3d;
        }

        public void setScale3d(double[] scale3d) {
            this.scale3d = scale3d;
            if (this.autoApply) {
                this.applyScale();
            }

        }

        public double[] getTranslate3d() {
            return this.translate3d;
        }

        public void setTranslate3d(double[] translate3d) {
            this.translate3d = translate3d;
            if (this.autoApply) {
                this.applyTranslation();
            }

        }

        public double[] getRotated4d() {
            return this.rotated4d;
        }

        public void setRotated4d(double[] rotated4d) {
            this.rotated4d = rotated4d;
            if (this.autoApply) {
                this.applyRotation();
            }

        }

        public MinecraftFontRenderer getFontRenderer() {
            return this.fontRenderer;
        }

        public void setFontRenderer(MinecraftFontRenderer fontRenderer) {
            this.fontRenderer = fontRenderer;
        }

        public void setAutoApply(boolean autoApply) {
            this.autoApply = autoApply;
        }

        public boolean hasColor() {
            return this.color4d != EMPTY_VECTOR4D;
        }

        public boolean hasScale() {
            return this.scale3d != EMPTY_VECTOR3D;
        }

        public boolean hasTranslation() {
            return this.translate3d != EMPTY_VECTOR3D;
        }

        public boolean hasRotation() {
            return this.rotated4d != EMPTY_VECTOR4D;
        }

        public boolean hasFontRenderer() {
            return this.fontRenderer != null;
        }

        public void applyColor() {
            if (this.hasColor()) {
                GL11.glColor4d(this.color4d[0], this.color4d[1], this.color4d[2], this.color4d[3]);
            }

        }

        public void applyScale() {
            if (this.hasScale()) {
                GL11.glScaled(this.scale3d[0], this.scale3d[1], this.scale3d[2]);
            }

        }

        public void applyTranslation() {
            if (this.hasTranslation()) {
                GL11.glTranslated(this.translate3d[0], this.translate3d[1], this.translate3d[2]);
            }

        }

        public void applyRotation() {
            if (this.hasRotation()) {
                GL11.glRotated(this.rotated4d[0], this.rotated4d[1], this.rotated4d[2], this.rotated4d[3]);
            }

        }

        public void clearColor() {
            this.color4d = EMPTY_VECTOR4D;
        }

        public void clearScale() {
            this.scale3d = EMPTY_VECTOR3D;
        }

        public void clearTranslation() {
            this.translate3d = EMPTY_VECTOR3D;
        }

        public void clearRotation() {
            this.rotated4d = EMPTY_VECTOR4D;
        }

        public void clearFontRenderer() {
            this.fontRenderer = null;
        }

        public void resetColor() {
            if (this.hasColor()) {
                this.clearColor();
                GL11.glColor4d(1.0D, 1.0D, 1.0D, 1.0D);
            }

        }

        public void resetScale() {
            if (this.hasScale()) {
                this.clearScale();
                GL11.glScaled(1.0D, 1.0D, 1.0D);
            }

        }

        public void resetTranslation() {
            if (this.hasTranslation()) {
                this.clearTranslation();
                GL11.glTranslated(0.0D, 0.0D, 0.0D);
            }

        }

        public void resetRotation() {
            if (this.hasRotation()) {
                this.clearRotation();
                GL11.glRotated(0.0D, 0.0D, 0.0D, 0.0D);
            }

        }
    }
}
