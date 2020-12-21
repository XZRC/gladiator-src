package com.client.glowclient.utils.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.LogicOp;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Sphere;

import java.awt.*;

public class RenderUtils {
    public static void drawEntityESP(Entity entity, int red, int green, int blue) {
        try {
            double renderPosX = Minecraft.getMinecraft().getRenderManager().renderPosX;
            double renderPosY = Minecraft.getMinecraft().getRenderManager().renderPosY;
            double renderPosZ = Minecraft.getMinecraft().getRenderManager().renderPosZ;
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            GL11.glEnable(2848);
            GL11.glLineWidth(1.0F);
            GL11.glDisable(3553);
            GL11.glDisable(2929);
            GL11.glPushMatrix();
            GL11.glColor4d((float) red / 255.0F, (float) green / 255.0F, (float) blue / 255.0F, 0.05000000074505806D);
            GL11.glPushMatrix();
            GL11.glTranslated(entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) Minecraft.getMinecraft().getRenderPartialTicks() - renderPosX, entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) Minecraft.getMinecraft().getRenderPartialTicks() - renderPosY, entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) Minecraft.getMinecraft().getRenderPartialTicks() - renderPosZ);
            drawColorBox(new AxisAlignedBB(entity.getEntityBoundingBox().minX - 0.05D - entity.posX, entity.getEntityBoundingBox().minY - 0.0D - entity.posY, entity.getEntityBoundingBox().minZ - 0.05D - entity.posZ, entity.getEntityBoundingBox().maxX + 0.05D - entity.posX, entity.getEntityBoundingBox().maxY + 0.1D - entity.posY, entity.getEntityBoundingBox().maxZ + 0.05D - entity.posZ), 0.0F, 0.0F, 0.0F, 0.0F);
            GL11.glColor4d((float) red / 255.0F, (float) green / 255.0F, (float) blue / 255.0F, 255.0D);
            drawSelectionBoundingBox(new AxisAlignedBB(entity.getEntityBoundingBox().minX - 0.05D - entity.posX, entity.getEntityBoundingBox().minY - 0.0D - entity.posY, entity.getEntityBoundingBox().minZ - 0.05D - entity.posZ, entity.getEntityBoundingBox().maxX + 0.05D - entity.posX, entity.getEntityBoundingBox().maxY + 0.1D - entity.posY, entity.getEntityBoundingBox().maxZ + 0.05D - entity.posZ));
            GL11.glPopMatrix();
            GL11.glPopMatrix();
            GL11.glEnable(2929);
            GL11.glEnable(3553);
            GL11.glDisable(3042);
            GL11.glDisable(2848);
        } catch (Exception var10) {
        }

    }

    public static void drawTargetESP(Entity entity, int red, int green, int blue) {
        try {
            double renderPosX = Minecraft.getMinecraft().getRenderManager().renderPosX;
            double renderPosY = Minecraft.getMinecraft().getRenderManager().renderPosY;
            double renderPosZ = Minecraft.getMinecraft().getRenderManager().renderPosZ;
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            GL11.glEnable(2848);
            GL11.glLineWidth(1.0F);
            GL11.glDisable(3553);
            GL11.glDisable(2929);
            GL11.glPushMatrix();
            GL11.glColor4d((float) red / 255.0F, (float) green / 255.0F, (float) blue / 255.0F, 0.05000000074505806D);
            GL11.glPushMatrix();
            GL11.glTranslated(entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) Minecraft.getMinecraft().getRenderPartialTicks() - renderPosX, entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) Minecraft.getMinecraft().getRenderPartialTicks() - renderPosY, entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) Minecraft.getMinecraft().getRenderPartialTicks() - renderPosZ);
            GL11.glColor4d((float) red / 255.0F, (float) green / 255.0F, (float) blue / 255.0F, 255.0D);
            drawSelectionBoundingBox(new AxisAlignedBB(entity.getEntityBoundingBox().minX - 0.05D - entity.posX, entity.getEntityBoundingBox().minY - 0.0D - entity.posY, entity.getEntityBoundingBox().minZ - 0.05D - entity.posZ, entity.getEntityBoundingBox().maxX + 0.05D - entity.posX, entity.getEntityBoundingBox().maxY + 0.1D - entity.posY, entity.getEntityBoundingBox().maxZ + 0.05D - entity.posZ));
            GL11.glPopMatrix();
            GL11.glPopMatrix();
            GL11.glEnable(2929);
            GL11.glEnable(3553);
            GL11.glDisable(3042);
            GL11.glDisable(2848);
        } catch (Exception var10) {
        }

    }

    public static void drawFreecamESP(double x1, double y1, double z1, int red, int green, int blue) {
        try {
            double renderPosX = Minecraft.getMinecraft().getRenderManager().renderPosX;
            double renderPosY = Minecraft.getMinecraft().getRenderManager().renderPosY;
            double renderPosZ = Minecraft.getMinecraft().getRenderManager().renderPosZ;
            GL11.glPushMatrix();
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            GL11.glLineWidth(2.0F);
            GL11.glDisable(2896);
            GL11.glDisable(3553);
            GL11.glLineWidth(2.0F);
            GL11.glEnable(2848);
            GL11.glDisable(2929);
            GL11.glDepthMask(false);
            GL11.glColor4d(red, green, blue, 0.18250000476837158D);
            double x = x1 - renderPosX;
            double y = y1 - renderPosY;
            double z = z1 - renderPosZ;
            drawSelectionBoundingBox(new AxisAlignedBB(x, y, z, x + 0.6D, y + 1.8D, z + 0.6D));
            GL11.glColor4d(red, green, blue, 1.0D);
            drawSelectionBoundingBox(new AxisAlignedBB(x, y, z, x + 0.6D, y + 1.8D, z + 0.6D));
            GL11.glLineWidth(2.0F);
            GL11.glDisable(2848);
            GL11.glEnable(3553);
            GL11.glEnable(2896);
            GL11.glDisable(2896);
            GL11.glEnable(2929);
            GL11.glDepthMask(true);
            GL11.glDisable(3042);
            GL11.glPopMatrix();
        } catch (Exception var21) {
        }

    }

    public static void drawLogoutESP(double d, double d1, double d2, double r, double g, double b) {
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glLineWidth(1.5F);
        GL11.glDisable(2896);
        GL11.glDisable(3553);
        GL11.glLineWidth(1.0F);
        GL11.glEnable(2848);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        GL11.glColor4d(r, g, b, 0.18250000476837158D);
        drawSelectionBoundingBox(new AxisAlignedBB(d, d1, d2, d + 0.6D, d1 + 1.8D, d2 + 0.6D));
        GL11.glColor4d(r, g, b, 1.0D);
        drawSelectionBoundingBox(new AxisAlignedBB(d, d1, d2, d + 0.6D, d1 + 1.8D, d2 + 0.6D));
        GL11.glLineWidth(2.0F);
        GL11.glDisable(2848);
        GL11.glEnable(3553);
        GL11.glEnable(2896);
        GL11.glDisable(2896);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }

    public static void chunkEspTall(ChunkPos blockPos, int red, int green, int blue, int alpha) {
        int length2 = 16;
        byte length = 16;

        try {
            double renderPosX = Minecraft.getMinecraft().getRenderManager().renderPosX;
            double renderPosY = Minecraft.getMinecraft().getRenderManager().renderPosY;
            double renderPosZ = Minecraft.getMinecraft().getRenderManager().renderPosZ;
            double x = (double) (blockPos.x * 16) - renderPosX;
            double z = (double) (blockPos.z * 16) - renderPosZ;
            GL11.glPushMatrix();
            GL11.glBlendFunc(770, 771);
            GL11.glEnable(3042);
            GL11.glLineWidth(2.0F);
            GL11.glDisable(3553);
            GL11.glDisable(2929);
            GL11.glDepthMask(false);
            drawColorBox(new AxisAlignedBB(x, 0.0D - renderPosY, z, x + (double) length2, 256.0D - renderPosY, z + (double) length), (float) red, (float) green, (float) blue, (float) alpha);
            GL11.glColor4d((float) red / 255.0F, (float) green / 255.0F, (float) blue / 255.0F, (float) alpha / 255.0F);
            drawSelectionBoundingBox(new AxisAlignedBB(x, 0.0D - renderPosY, z, x + (double) length2, 256.0D - renderPosY, z + (double) length));
            GL11.glLineWidth(2.0F);
            GL11.glEnable(3553);
            GL11.glEnable(2929);
            GL11.glDepthMask(true);
            GL11.glDisable(3042);
            GL11.glPopMatrix();
        } catch (Exception var17) {
        }

    }

    public static void chunkEspFlat(ChunkPos blockPos, int red, int green, int blue, int alpha) {
        int length2 = 16;
        byte length = 16;

        try {
            double renderPosX = Minecraft.getMinecraft().getRenderManager().renderPosX;
            double renderPosY = Minecraft.getMinecraft().getRenderManager().renderPosY;
            double renderPosZ = Minecraft.getMinecraft().getRenderManager().renderPosZ;
            double x = (double) (blockPos.x * 16) - renderPosX;
            double z = (double) (blockPos.z * 16) - renderPosZ;
            GL11.glPushMatrix();
            GL11.glBlendFunc(770, 771);
            GL11.glEnable(3042);
            GL11.glLineWidth(2.0F);
            GL11.glDisable(3553);
            GL11.glDisable(2929);
            GL11.glDepthMask(false);
            GL11.glColor4d((float) red / 255.0F, (float) green / 255.0F, (float) blue / 255.0F, (float) alpha / 255.0F);
            drawSelectionBoundingBox(new AxisAlignedBB(x, 0.0D - renderPosY, z, x + (double) length2, 0.0D - renderPosY, z + (double) length));
            GL11.glLineWidth(2.0F);
            GL11.glEnable(3553);
            GL11.glEnable(2929);
            GL11.glDepthMask(true);
            GL11.glDisable(3042);
            GL11.glPopMatrix();
        } catch (Exception var17) {
        }

    }

    public static void drawBlockESP(BlockPos blockPos, int red, int green, int blue, int alpha, int outlineR, int outlineG, int outlineB, int outlineA, int outlineWidth) {
        try {
            double renderPosX = Minecraft.getMinecraft().getRenderManager().renderPosX;
            double renderPosY = Minecraft.getMinecraft().getRenderManager().renderPosY;
            double renderPosZ = Minecraft.getMinecraft().getRenderManager().renderPosZ;
            double x = (double) blockPos.getX() - renderPosX;
            double y = (double) blockPos.getY() - renderPosY;
            double z = (double) blockPos.getZ() - renderPosZ;
            GL11.glPushMatrix();
            GL11.glBlendFunc(770, 771);
            GL11.glEnable(3042);
            GL11.glLineWidth((float) outlineWidth);
            GL11.glDisable(3553);
            GL11.glDisable(2929);
            GL11.glDepthMask(false);
            GL11.glColor4d((float) red / 255.0F, (float) green / 255.0F, (float) blue / 255.0F, (float) alpha / 255.0F);
            drawColorBox(new AxisAlignedBB(x, y, z, x + 1.0D, y + 1.0D, z + 1.0D), 0.0F, 0.0F, 0.0F, 0.0F);
            GL11.glColor4d((float) outlineR / 255.0F, (float) outlineG / 255.0F, (float) outlineB / 255.0F, (float) outlineA / 255.0F);
            drawSelectionBoundingBox(new AxisAlignedBB(x, y, z, x + 1.0D, y + 1.0D, z + 1.0D));
            GL11.glLineWidth(2.0F);
            GL11.glEnable(3553);
            GL11.glEnable(2929);
            GL11.glDepthMask(true);
            GL11.glDisable(3042);
            GL11.glPopMatrix();
        } catch (Exception var22) {
        }

    }

    public static void blockEspFrame(BlockPos blockPos, double red, double green, double blue) {
        try {
            double renderPosX = Minecraft.getMinecraft().getRenderManager().renderPosX;
            double renderPosY = Minecraft.getMinecraft().getRenderManager().renderPosY;
            double renderPosZ = Minecraft.getMinecraft().getRenderManager().renderPosZ;
            double x = (double) blockPos.getX() - renderPosX;
            double y = (double) blockPos.getY() - renderPosY;
            double z = (double) blockPos.getZ() - renderPosZ;
            GL11.glBlendFunc(770, 771);
            GL11.glEnable(3042);
            GL11.glLineWidth(1.0F);
            GL11.glDisable(3553);
            GL11.glDisable(2929);
            GL11.glDepthMask(false);
            GL11.glColor4d(red, green, blue, 1.0D);
            drawSelectionBoundingBox(new AxisAlignedBB(x, y, z, x + 1.0D, y + 1.0D, z + 1.0D));
            GL11.glEnable(3553);
            GL11.glEnable(2929);
            GL11.glDepthMask(true);
            GL11.glDisable(3042);
        } catch (Exception var19) {
        }

    }

    public static void blockEspBox(BlockPos blockPos, double red, double green, double blue) {
        try {
            double renderPosX = Minecraft.getMinecraft().getRenderManager().renderPosX;
            double renderPosY = Minecraft.getMinecraft().getRenderManager().renderPosY;
            double renderPosZ = Minecraft.getMinecraft().getRenderManager().renderPosZ;
            double x = (double) blockPos.getX() - renderPosX;
            double y = (double) blockPos.getY() - renderPosY;
            double z = (double) blockPos.getZ() - renderPosZ;
            GL11.glBlendFunc(770, 771);
            GL11.glEnable(3042);
            GL11.glLineWidth(2.0F);
            GL11.glDisable(3553);
            GL11.glDisable(2929);
            GL11.glDepthMask(false);
            GL11.glColor4d(red, green, blue, 0.15000000596046448D);
            drawColorBox(new AxisAlignedBB(x, y, z, x + 1.0D, y + 1.0D, z + 1.0D), 0.0F, 0.0F, 0.0F, 0.0F);
            GL11.glEnable(3553);
            GL11.glEnable(2929);
            GL11.glDepthMask(true);
            GL11.glDisable(3042);
        } catch (Exception var19) {
        }

    }

    public static void box(double x, double y, double z, double x2, double y2, double z2, float red, float green, float blue, float alpha) {
        try {
            double renderPosX = Minecraft.getMinecraft().getRenderManager().renderPosX;
            double renderPosY = Minecraft.getMinecraft().getRenderManager().renderPosY;
            double renderPosZ = Minecraft.getMinecraft().getRenderManager().renderPosZ;
            x -= renderPosX;
            y -= renderPosY;
            z -= renderPosZ;
            x2 -= renderPosX;
            y2 -= renderPosY;
            z2 -= renderPosZ;
            GL11.glBlendFunc(770, 771);
            GL11.glEnable(3042);
            GL11.glLineWidth(2.0F);
            GL11.glDisable(3553);
            GL11.glDisable(2929);
            GL11.glDepthMask(false);
            GL11.glDepthMask(false);
            GL11.glColor4f(red, green, blue, alpha);
            drawColorBox(new AxisAlignedBB(x, y, z, x2, y2, z2), red, green, blue, alpha);
            GL11.glColor4d(0.0D, 0.0D, 0.0D, 0.5D);
            drawSelectionBoundingBox(new AxisAlignedBB(x, y, z, x2, y2, z2));
            GL11.glEnable(3553);
            GL11.glEnable(2929);
            GL11.glDepthMask(true);
            GL11.glDisable(3042);
        } catch (Exception var22) {
        }

    }

    public static void frame(double x, double y, double z, double x2, double y2, double z2, Color color) {
        try {
            double renderPosX = Minecraft.getMinecraft().getRenderManager().renderPosX;
            double renderPosY = Minecraft.getMinecraft().getRenderManager().renderPosY;
            double renderPosZ = Minecraft.getMinecraft().getRenderManager().renderPosZ;
            x -= renderPosX;
            y -= renderPosY;
            z -= renderPosZ;
            x2 -= renderPosX;
            y2 -= renderPosY;
            z2 -= renderPosZ;
            GL11.glBlendFunc(770, 771);
            GL11.glEnable(3042);
            GL11.glLineWidth(2.0F);
            GL11.glDisable(3553);
            GL11.glDisable(2929);
            GL11.glDepthMask(false);
            drawSelectionBoundingBox(new AxisAlignedBB(x, y, z, x2, y2, z2));
            GL11.glEnable(3553);
            GL11.glEnable(2929);
            GL11.glDepthMask(true);
            GL11.glDisable(3042);
        } catch (Exception var19) {
        }

    }

    public static void drawColorBox(AxisAlignedBB axisalignedbb, float red, float green, float blue, float alpha) {
        Tessellator ts = Tessellator.getInstance();
        BufferBuilder vb = ts.getBuffer();
        vb.begin(7, DefaultVertexFormats.POSITION_TEX);
        vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        ts.draw();
        vb.begin(7, DefaultVertexFormats.POSITION_TEX);
        vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        ts.draw();
        vb.begin(7, DefaultVertexFormats.POSITION_TEX);
        vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        ts.draw();
        vb.begin(7, DefaultVertexFormats.POSITION_TEX);
        vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        ts.draw();
        vb.begin(7, DefaultVertexFormats.POSITION_TEX);
        vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        ts.draw();
        vb.begin(7, DefaultVertexFormats.POSITION_TEX);
        vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        vb.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ).color(red, green, blue, alpha).endVertex();
        ts.draw();
    }

    public static void drawSelectionBoundingBox(AxisAlignedBB boundingBox) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexbuffer = tessellator.getBuffer();
        vertexbuffer.begin(3, DefaultVertexFormats.POSITION);
        vertexbuffer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).endVertex();
        vertexbuffer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).endVertex();
        vertexbuffer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).endVertex();
        vertexbuffer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).endVertex();
        vertexbuffer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).endVertex();
        tessellator.draw();
        vertexbuffer.begin(3, DefaultVertexFormats.POSITION);
        vertexbuffer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).endVertex();
        vertexbuffer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).endVertex();
        vertexbuffer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).endVertex();
        vertexbuffer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).endVertex();
        vertexbuffer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).endVertex();
        tessellator.draw();
        vertexbuffer.begin(1, DefaultVertexFormats.POSITION);
        vertexbuffer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).endVertex();
        vertexbuffer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).endVertex();
        vertexbuffer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).endVertex();
        vertexbuffer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).endVertex();
        vertexbuffer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).endVertex();
        vertexbuffer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).endVertex();
        vertexbuffer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).endVertex();
        vertexbuffer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).endVertex();
        tessellator.draw();
    }

    public static void drawBoundingBox(AxisAlignedBB par1AxisAlignedBB) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexbuffer = tessellator.getBuffer();
        GlStateManager.color(0.0F, 0.0F, 255.0F, 255.0F);
        GlStateManager.disableTexture2D();
        GlStateManager.enableColorLogic();
        GlStateManager.colorLogicOp(LogicOp.OR_REVERSE);
        vertexbuffer.begin(3, DefaultVertexFormats.POSITION);
        vertexbuffer.pos(par1AxisAlignedBB.minX, par1AxisAlignedBB.minY, par1AxisAlignedBB.minZ);
        vertexbuffer.pos(par1AxisAlignedBB.minX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.minZ);
        vertexbuffer.pos(par1AxisAlignedBB.maxX, par1AxisAlignedBB.minY, par1AxisAlignedBB.minZ);
        vertexbuffer.pos(par1AxisAlignedBB.maxX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.minZ);
        vertexbuffer.pos(par1AxisAlignedBB.maxX, par1AxisAlignedBB.minY, par1AxisAlignedBB.maxZ);
        vertexbuffer.pos(par1AxisAlignedBB.maxX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.maxZ);
        vertexbuffer.pos(par1AxisAlignedBB.minX, par1AxisAlignedBB.minY, par1AxisAlignedBB.maxZ);
        vertexbuffer.pos(par1AxisAlignedBB.minX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.maxZ);
        tessellator.draw();
        vertexbuffer.pos(par1AxisAlignedBB.maxX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.minZ);
        vertexbuffer.pos(par1AxisAlignedBB.maxX, par1AxisAlignedBB.minY, par1AxisAlignedBB.minZ);
        vertexbuffer.pos(par1AxisAlignedBB.minX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.minZ);
        vertexbuffer.pos(par1AxisAlignedBB.minX, par1AxisAlignedBB.minY, par1AxisAlignedBB.minZ);
        vertexbuffer.pos(par1AxisAlignedBB.minX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.maxZ);
        vertexbuffer.pos(par1AxisAlignedBB.minX, par1AxisAlignedBB.minY, par1AxisAlignedBB.maxZ);
        vertexbuffer.pos(par1AxisAlignedBB.maxX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.maxZ);
        vertexbuffer.pos(par1AxisAlignedBB.maxX, par1AxisAlignedBB.minY, par1AxisAlignedBB.maxZ);
        tessellator.draw();
        vertexbuffer.pos(par1AxisAlignedBB.minX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.minZ);
        vertexbuffer.pos(par1AxisAlignedBB.maxX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.minZ);
        vertexbuffer.pos(par1AxisAlignedBB.maxX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.maxZ);
        vertexbuffer.pos(par1AxisAlignedBB.minX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.maxZ);
        vertexbuffer.pos(par1AxisAlignedBB.minX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.minZ);
        vertexbuffer.pos(par1AxisAlignedBB.minX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.maxZ);
        vertexbuffer.pos(par1AxisAlignedBB.maxX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.maxZ);
        vertexbuffer.pos(par1AxisAlignedBB.maxX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.minZ);
        tessellator.draw();
        vertexbuffer.pos(par1AxisAlignedBB.minX, par1AxisAlignedBB.minY, par1AxisAlignedBB.minZ);
        vertexbuffer.pos(par1AxisAlignedBB.maxX, par1AxisAlignedBB.minY, par1AxisAlignedBB.minZ);
        vertexbuffer.pos(par1AxisAlignedBB.maxX, par1AxisAlignedBB.minY, par1AxisAlignedBB.maxZ);
        vertexbuffer.pos(par1AxisAlignedBB.minX, par1AxisAlignedBB.minY, par1AxisAlignedBB.maxZ);
        vertexbuffer.pos(par1AxisAlignedBB.minX, par1AxisAlignedBB.minY, par1AxisAlignedBB.minZ);
        vertexbuffer.pos(par1AxisAlignedBB.minX, par1AxisAlignedBB.minY, par1AxisAlignedBB.maxZ);
        vertexbuffer.pos(par1AxisAlignedBB.maxX, par1AxisAlignedBB.minY, par1AxisAlignedBB.maxZ);
        vertexbuffer.pos(par1AxisAlignedBB.maxX, par1AxisAlignedBB.minY, par1AxisAlignedBB.minZ);
        tessellator.draw();
        vertexbuffer.pos(par1AxisAlignedBB.minX, par1AxisAlignedBB.minY, par1AxisAlignedBB.minZ);
        vertexbuffer.pos(par1AxisAlignedBB.minX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.minZ);
        vertexbuffer.pos(par1AxisAlignedBB.minX, par1AxisAlignedBB.minY, par1AxisAlignedBB.maxZ);
        vertexbuffer.pos(par1AxisAlignedBB.minX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.maxZ);
        vertexbuffer.pos(par1AxisAlignedBB.maxX, par1AxisAlignedBB.minY, par1AxisAlignedBB.maxZ);
        vertexbuffer.pos(par1AxisAlignedBB.maxX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.maxZ);
        vertexbuffer.pos(par1AxisAlignedBB.maxX, par1AxisAlignedBB.minY, par1AxisAlignedBB.minZ);
        vertexbuffer.pos(par1AxisAlignedBB.maxX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.minZ);
        tessellator.draw();
        vertexbuffer.pos(par1AxisAlignedBB.minX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.maxZ);
        vertexbuffer.pos(par1AxisAlignedBB.minX, par1AxisAlignedBB.minY, par1AxisAlignedBB.maxZ);
        vertexbuffer.pos(par1AxisAlignedBB.minX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.minZ);
        vertexbuffer.pos(par1AxisAlignedBB.minX, par1AxisAlignedBB.minY, par1AxisAlignedBB.minZ);
        vertexbuffer.pos(par1AxisAlignedBB.maxX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.minZ);
        vertexbuffer.pos(par1AxisAlignedBB.maxX, par1AxisAlignedBB.minY, par1AxisAlignedBB.minZ);
        vertexbuffer.pos(par1AxisAlignedBB.maxX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.maxZ);
        vertexbuffer.pos(par1AxisAlignedBB.maxX, par1AxisAlignedBB.minY, par1AxisAlignedBB.maxZ);
        tessellator.draw();
        GlStateManager.disableColorLogic();
        GlStateManager.enableTexture2D();
    }

    public static void drawSphere(double x, double y, double z, float size, int slices, int stacks) {
        Sphere s = new Sphere();

        try {
            double renderPosX = Minecraft.getMinecraft().getRenderManager().renderPosX;
            double renderPosY = Minecraft.getMinecraft().getRenderManager().renderPosY;
            double renderPosZ = Minecraft.getMinecraft().getRenderManager().renderPosZ;
            GL11.glPushMatrix();
            GL11.glBlendFunc(770, 771);
            GL11.glEnable(3042);
            GL11.glLineWidth(10.0F);
            GL11.glDisable(3553);
            GL11.glDisable(2929);
            GL11.glDepthMask(false);
            s.setDrawStyle(100013);
            GL11.glTranslated(x - renderPosX, y - renderPosY, z - renderPosZ);
            s.draw(size, slices, stacks);
            GL11.glEnable(3553);
            GL11.glEnable(2929);
            GL11.glDepthMask(true);
            GL11.glDisable(3042);
            GL11.glPopMatrix();
        } catch (Exception var16) {
        }

    }

    public static void drawRoundedRect(float x, float y, float x1, float y1, int borderC, int insideC) {
        x *= 2.0F;
        y *= 2.0F;
        x1 *= 2.0F;
        y1 *= 2.0F;
        GL11.glScalef(0.5F, 0.5F, 0.5F);
        drawVLine(x, y + 1.0F, y1 - 2.0F, borderC);
        drawVLine(x1 - 1.0F, y + 1.0F, y1 - 2.0F, borderC);
        drawHLine(x + 2.0F, x1 - 3.0F, y, borderC);
        drawHLine(x + 2.0F, x1 - 3.0F, y1 - 1.0F, borderC);
        drawHLine(x + 1.0F, x + 1.0F, y + 1.0F, borderC);
        drawHLine(x1 - 2.0F, x1 - 2.0F, y + 1.0F, borderC);
        drawHLine(x1 - 2.0F, x1 - 2.0F, y1 - 2.0F, borderC);
        drawHLine(x + 1.0F, x + 1.0F, y1 - 2.0F, borderC);
        drawRect(x + 1.0F, y + 1.0F, x1 - 1.0F, y1 - 1.0F, insideC);
        GL11.glScalef(2.0F, 2.0F, 2.0F);
    }

    public static void drawBetterBorderedRect(int x, float y, int x1, int y1, float size, int borderC, int insideC) {
        drawRect((float) x, y, (float) x1, (float) y1, insideC);
        drawRect((float) x, y, (float) x1, y + size, borderC);
        drawRect((float) x, (float) y1, (float) x1, (float) y1 + size, borderC);
        drawRect((float) x1, y, (float) x1 + size, (float) y1 + size, borderC);
        drawRect((float) x, y, (float) x + size, (float) y1 + size, borderC);
    }

    public static void drawBorderedRect(double x, double y, double x2, double y2, float l1, int col1, int col2) {
        drawRect((float) x, (float) y, (float) x2, (float) y2, col2);
        float f = (float) (col1 >> 24 & 255) / 255.0F;
        float f1 = (float) (col1 >> 16 & 255) / 255.0F;
        float f2 = (float) (col1 >> 8 & 255) / 255.0F;
        float f3 = (float) (col1 & 255) / 255.0F;
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glColor4f(f1, f2, f3, f);
        GL11.glLineWidth(l1);
        GL11.glBegin(1);
        GL11.glVertex2d(x, y);
        GL11.glVertex2d(x, y2);
        GL11.glVertex2d(x2, y2);
        GL11.glVertex2d(x2, y);
        GL11.glVertex2d(x, y);
        GL11.glVertex2d(x2, y);
        GL11.glVertex2d(x, y2);
        GL11.glVertex2d(x2, y2);
        GL11.glEnd();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
        GL11.glPopMatrix();
    }

    public static void drawHLine(float par1, float par2, float par3, int par4) {
        if (par2 < par1) {
            float var5 = par1;
            par1 = par2;
            par2 = var5;
        }

        drawRect(par1, par3, par2 + 1.0F, par3 + 1.0F, par4);
    }

    public static void drawVLine(float par1, float par2, float par3, int par4) {
        if (par3 < par2) {
            float var5 = par2;
            par2 = par3;
            par3 = var5;
        }

        drawRect(par1, par2 + 1.0F, par1 + 1.0F, par3, par4);
    }

    public static void drawRect(float paramXStart, float paramYStart, float paramXEnd, float paramYEnd, int paramColor) {
        float alpha = (float) (paramColor >> 24 & 255) / 255.0F;
        float red = (float) (paramColor >> 16 & 255) / 255.0F;
        float green = (float) (paramColor >> 8 & 255) / 255.0F;
        float blue = (float) (paramColor & 255) / 255.0F;
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glColor4f(red, green, blue, alpha);
        GL11.glBegin(7);
        GL11.glVertex2d(paramXEnd, paramYStart);
        GL11.glVertex2d(paramXStart, paramYStart);
        GL11.glVertex2d(paramXStart, paramYEnd);
        GL11.glVertex2d(paramXEnd, paramYEnd);
        GL11.glEnd();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
        GL11.glPopMatrix();
    }

    public static void drawGradientRect(double x, double y, double x2, double y2, int col1, int col2) {
        float f = (float) (col1 >> 24 & 255) / 255.0F;
        float f1 = (float) (col1 >> 16 & 255) / 255.0F;
        float f2 = (float) (col1 >> 8 & 255) / 255.0F;
        float f3 = (float) (col1 & 255) / 255.0F;
        float f4 = (float) (col2 >> 24 & 255) / 255.0F;
        float f5 = (float) (col2 >> 16 & 255) / 255.0F;
        float f6 = (float) (col2 >> 8 & 255) / 255.0F;
        float f7 = (float) (col2 & 255) / 255.0F;
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glShadeModel(7425);
        GL11.glPushMatrix();
        GL11.glBegin(7);
        GL11.glColor4f(f1, f2, f3, f);
        GL11.glVertex2d(x2, y);
        GL11.glVertex2d(x, y);
        GL11.glColor4f(f5, f6, f7, f4);
        GL11.glVertex2d(x, y2);
        GL11.glVertex2d(x2, y2);
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
        GL11.glShadeModel(7424);
    }

    public static void drawGradientBorderedRect(double x, double y, double x2, double y2, float l1, int col1, int col2, int col3) {
        float f = (float) (col1 >> 24 & 255) / 255.0F;
        float f1 = (float) (col1 >> 16 & 255) / 255.0F;
        float f2 = (float) (col1 >> 8 & 255) / 255.0F;
        float f3 = (float) (col1 & 255) / 255.0F;
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glDisable(3042);
        GL11.glPushMatrix();
        GL11.glColor4f(f1, f2, f3, f);
        GL11.glLineWidth(1.0F);
        GL11.glBegin(1);
        GL11.glVertex2d(x, y);
        GL11.glVertex2d(x, y2);
        GL11.glVertex2d(x2, y2);
        GL11.glVertex2d(x2, y);
        GL11.glVertex2d(x, y);
        GL11.glVertex2d(x2, y);
        GL11.glVertex2d(x, y2);
        GL11.glVertex2d(x2, y2);
        GL11.glEnd();
        GL11.glPopMatrix();
        drawGradientRect(x, y, x2, y2, col2, col3);
        GL11.glEnable(3042);
        GL11.glEnable(3553);
        GL11.glDisable(2848);
    }

    public static void drawStrip(int x, int y, float width, double angle, float points, float radius, int color) {
        GL11.glPushMatrix();
        float f1 = (float) (color >> 24 & 255) / 255.0F;
        float f2 = (float) (color >> 16 & 255) / 255.0F;
        float f3 = (float) (color >> 8 & 255) / 255.0F;
        float f4 = (float) (color & 255) / 255.0F;
        GL11.glTranslatef((float) x, (float) y, 0.0F);
        GL11.glColor4f(f2, f3, f4, f1);
        GL11.glLineWidth(width);
        GL11.glEnable(3042);
        GL11.glDisable(2929);
        GL11.glEnable(2848);
        GL11.glDisable(3553);
        GL11.glDisable(3008);
        GL11.glBlendFunc(770, 771);
        GL11.glHint(3154, 4354);
        GL11.glEnable(32925);
        int i;
        float a;
        float xc;
        float yc;
        if (angle > 0.0D) {
            GL11.glBegin(3);

            for (i = 0; (double) i < angle; ++i) {
                a = (float) ((double) i * (angle * 3.141592653589793D / (double) points));
                xc = (float) (Math.cos(a) * (double) radius);
                yc = (float) (Math.sin(a) * (double) radius);
                GL11.glVertex2f(xc, yc);
            }

            GL11.glEnd();
        }

        if (angle < 0.0D) {
            GL11.glBegin(3);

            for (i = 0; (double) i > angle; --i) {
                a = (float) ((double) i * (angle * 3.141592653589793D / (double) points));
                xc = (float) (Math.cos(a) * (double) (-radius));
                yc = (float) (Math.sin(a) * (double) (-radius));
                GL11.glVertex2f(xc, yc);
            }

            GL11.glEnd();
        }

        GL11.glDisable(3042);
        GL11.glEnable(3553);
        GL11.glDisable(2848);
        GL11.glEnable(3008);
        GL11.glEnable(2929);
        GL11.glDisable(32925);
        GL11.glDisable(3479);
        GL11.glPopMatrix();
    }

    public static void drawCircle(float cx, float cy, float r, int num_segments, int c) {
        GL11.glScalef(0.5F, 0.5F, 0.5F);
        r *= 2.0F;
        cx *= 2.0F;
        cy *= 2.0F;
        float f = (float) (c >> 24 & 255) / 255.0F;
        float f1 = (float) (c >> 16 & 255) / 255.0F;
        float f2 = (float) (c >> 8 & 255) / 255.0F;
        float f3 = (float) (c & 255) / 255.0F;
        float theta = (float) (6.2831852D / (double) num_segments);
        float p = (float) Math.cos(theta);
        float s = (float) Math.sin(theta);
        GL11.glColor4f(f1, f2, f3, f);
        float x = r;
        float y = 0.0F;
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glBlendFunc(770, 771);
        GL11.glBegin(2);

        for (int ii = 0; ii < num_segments; ++ii) {
            GL11.glVertex2f(x + cx, y + cy);
            float t = x;
            x = p * x - s * y;
            y = s * t + p * y;
        }

        GL11.glEnd();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
        GL11.glScalef(2.0F, 2.0F, 2.0F);
    }

    public static void drawFullCircle(int cx, int cy, double r, int c) {
        GL11.glScalef(0.5F, 0.5F, 0.5F);
        r *= 2.0D;
        cx *= 2;
        cy *= 2;
        float f = (float) (c >> 24 & 255) / 255.0F;
        float f1 = (float) (c >> 16 & 255) / 255.0F;
        float f2 = (float) (c >> 8 & 255) / 255.0F;
        float f3 = (float) (c & 255) / 255.0F;
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glBlendFunc(770, 771);
        GL11.glColor4f(f1, f2, f3, f);
        GL11.glBegin(6);

        for (int i = 0; i <= 360; ++i) {
            double x = Math.sin((double) i * 3.141592653589793D / 180.0D) * r;
            double y = Math.cos((double) i * 3.141592653589793D / 180.0D) * r;
            GL11.glVertex2d((double) cx + x, (double) cy + y);
        }

        GL11.glEnd();
        GL11.glDisable(2848);
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glScalef(2.0F, 2.0F, 2.0F);
    }
}
