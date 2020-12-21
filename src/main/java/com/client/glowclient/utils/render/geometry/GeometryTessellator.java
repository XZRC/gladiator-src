package com.client.glowclient.utils.render.geometry;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.BlockPos;

public class GeometryTessellator extends Tessellator {
   private static GeometryTessellator instance = null;
   private static double deltaS = 0.0D;
   private double delta;

   public GeometryTessellator() {
      this(2097152);
   }

   public GeometryTessellator(int size) {
      super(size);
      this.delta = 0.0D;
   }

   public static GeometryTessellator getInstance() {
      if (instance == null) {
         instance = new GeometryTessellator();
      }

      return instance;
   }

   public void setTranslation(double x, double y, double z) {
      this.getBuffer().setTranslation(x, y, z);
   }

   public void beginQuads() {
      this.begin(7);
   }

   public void beginLines() {
      this.begin(1);
   }

   public void begin(int mode) {
      this.getBuffer().begin(mode, DefaultVertexFormats.POSITION_COLOR);
   }

   public void draw() {
      super.draw();
   }

   public void setDelta(double delta) {
      this.delta = delta;
   }

   public static void setstaticDelta(double delta) {
      deltaS = delta;
   }

   public void drawCuboid(BlockPos pos, int sides, int argb) {
      this.drawCuboid(pos, pos, sides, argb);
   }

   public void drawCuboid(BlockPos begin, BlockPos end, int sides, int argb) {
      drawCuboid(this.getBuffer(), begin, end, sides, argb, this.delta);
   }

   public static void drawCuboid(BufferBuilder buffer, BlockPos pos, int sides, int argb) {
      drawCuboid(buffer, pos, pos, sides, argb);
   }

   public static void drawCuboid(BufferBuilder buffer, BlockPos begin, BlockPos end, int sides, int argb) {
      drawCuboid(buffer, begin, end, sides, argb, deltaS);
   }

   private static void drawCuboid(BufferBuilder buffer, BlockPos begin, BlockPos end, int sides, int argb, double delta) {
      if (buffer.getDrawMode() != -1 && sides != 0) {
         double x0 = (double)begin.getX() - delta;
         double y0 = (double)begin.getY() - delta;
         double z0 = (double)begin.getZ() - delta;
         double x1 = (double)(end.getX() + 1) + delta;
         double y1 = (double)(end.getY() + 1) + delta;
         double z1 = (double)(end.getZ() + 1) + delta;
         switch(buffer.getDrawMode()) {
         case 1:
            drawLines(buffer, x0, y0, z0, x1, y1, z1, sides, argb);
            break;
         case 7:
            drawQuads(buffer, x0, y0, z0, x1, y1, z1, sides, argb);
            break;
         default:
            throw new IllegalStateException("Unsupported mode!");
         }

      }
   }

   public static void drawQuads(BufferBuilder buffer, double x0, double y0, double z0, double x1, double y1, double z1, int sides, int argb) {
      int a = argb >>> 24 & 255;
      int r = argb >>> 16 & 255;
      int g = argb >>> 8 & 255;
      int b = argb & 255;
      drawQuads(buffer, x0, y0, z0, x1, y1, z1, sides, a, r, g, b);
   }

   public static void drawQuads(BufferBuilder buffer, double x0, double y0, double z0, double x1, double y1, double z1, int sides, int a, int r, int g, int b) {
      if ((sides & 1) != 0) {
         buffer.pos(x1, y0, z0).color(r, g, b, a).endVertex();
         buffer.pos(x1, y0, z1).color(r, g, b, a).endVertex();
         buffer.pos(x0, y0, z1).color(r, g, b, a).endVertex();
         buffer.pos(x0, y0, z0).color(r, g, b, a).endVertex();
      }

      if ((sides & 2) != 0) {
         buffer.pos(x1, y1, z0).color(r, g, b, a).endVertex();
         buffer.pos(x0, y1, z0).color(r, g, b, a).endVertex();
         buffer.pos(x0, y1, z1).color(r, g, b, a).endVertex();
         buffer.pos(x1, y1, z1).color(r, g, b, a).endVertex();
      }

      if ((sides & 4) != 0) {
         buffer.pos(x1, y0, z0).color(r, g, b, a).endVertex();
         buffer.pos(x0, y0, z0).color(r, g, b, a).endVertex();
         buffer.pos(x0, y1, z0).color(r, g, b, a).endVertex();
         buffer.pos(x1, y1, z0).color(r, g, b, a).endVertex();
      }

      if ((sides & 8) != 0) {
         buffer.pos(x0, y0, z1).color(r, g, b, a).endVertex();
         buffer.pos(x1, y0, z1).color(r, g, b, a).endVertex();
         buffer.pos(x1, y1, z1).color(r, g, b, a).endVertex();
         buffer.pos(x0, y1, z1).color(r, g, b, a).endVertex();
      }

      if ((sides & 16) != 0) {
         buffer.pos(x0, y0, z0).color(r, g, b, a).endVertex();
         buffer.pos(x0, y0, z1).color(r, g, b, a).endVertex();
         buffer.pos(x0, y1, z1).color(r, g, b, a).endVertex();
         buffer.pos(x0, y1, z0).color(r, g, b, a).endVertex();
      }

      if ((sides & 32) != 0) {
         buffer.pos(x1, y0, z1).color(r, g, b, a).endVertex();
         buffer.pos(x1, y0, z0).color(r, g, b, a).endVertex();
         buffer.pos(x1, y1, z0).color(r, g, b, a).endVertex();
         buffer.pos(x1, y1, z1).color(r, g, b, a).endVertex();
      }

   }

   public static void drawLines(BufferBuilder buffer, double x0, double y0, double z0, double x1, double y1, double z1, int sides, int argb) {
      int a = argb >>> 24 & 255;
      int r = argb >>> 16 & 255;
      int g = argb >>> 8 & 255;
      int b = argb & 255;
      drawLines(buffer, x0, y0, z0, x1, y1, z1, sides, a, r, g, b);
   }

   public static void drawLines(BufferBuilder buffer, double x0, double y0, double z0, double x1, double y1, double z1, int sides, int a, int r, int g, int b) {
      if ((sides & 17) != 0) {
         buffer.pos(x0, y0, z0).color(r, g, b, a).endVertex();
         buffer.pos(x0, y0, z1).color(r, g, b, a).endVertex();
      }

      if ((sides & 18) != 0) {
         buffer.pos(x0, y1, z0).color(r, g, b, a).endVertex();
         buffer.pos(x0, y1, z1).color(r, g, b, a).endVertex();
      }

      if ((sides & 33) != 0) {
         buffer.pos(x1, y0, z0).color(r, g, b, a).endVertex();
         buffer.pos(x1, y0, z1).color(r, g, b, a).endVertex();
      }

      if ((sides & 34) != 0) {
         buffer.pos(x1, y1, z0).color(r, g, b, a).endVertex();
         buffer.pos(x1, y1, z1).color(r, g, b, a).endVertex();
      }

      if ((sides & 5) != 0) {
         buffer.pos(x0, y0, z0).color(r, g, b, a).endVertex();
         buffer.pos(x1, y0, z0).color(r, g, b, a).endVertex();
      }

      if ((sides & 6) != 0) {
         buffer.pos(x0, y1, z0).color(r, g, b, a).endVertex();
         buffer.pos(x1, y1, z0).color(r, g, b, a).endVertex();
      }

      if ((sides & 9) != 0) {
         buffer.pos(x0, y0, z1).color(r, g, b, a).endVertex();
         buffer.pos(x1, y0, z1).color(r, g, b, a).endVertex();
      }

      if ((sides & 10) != 0) {
         buffer.pos(x0, y1, z1).color(r, g, b, a).endVertex();
         buffer.pos(x1, y1, z1).color(r, g, b, a).endVertex();
      }

      if ((sides & 20) != 0) {
         buffer.pos(x0, y0, z0).color(r, g, b, a).endVertex();
         buffer.pos(x0, y1, z0).color(r, g, b, a).endVertex();
      }

      if ((sides & 36) != 0) {
         buffer.pos(x1, y0, z0).color(r, g, b, a).endVertex();
         buffer.pos(x1, y1, z0).color(r, g, b, a).endVertex();
      }

      if ((sides & 24) != 0) {
         buffer.pos(x0, y0, z1).color(r, g, b, a).endVertex();
         buffer.pos(x0, y1, z1).color(r, g, b, a).endVertex();
      }

      if ((sides & 40) != 0) {
         buffer.pos(x1, y0, z1).color(r, g, b, a).endVertex();
         buffer.pos(x1, y1, z1).color(r, g, b, a).endVertex();
      }

   }

   public static final class Line {
      public static final int DOWN_WEST = 17;
      public static final int UP_WEST = 18;
      public static final int DOWN_EAST = 33;
      public static final int UP_EAST = 34;
      public static final int DOWN_NORTH = 5;
      public static final int UP_NORTH = 6;
      public static final int DOWN_SOUTH = 9;
      public static final int UP_SOUTH = 10;
      public static final int NORTH_WEST = 20;
      public static final int NORTH_EAST = 36;
      public static final int SOUTH_WEST = 24;
      public static final int SOUTH_EAST = 40;
      public static final int ALL = 63;
   }

   public static final class Quad {
      public static final int DOWN = 1;
      public static final int UP = 2;
      public static final int NORTH = 4;
      public static final int SOUTH = 8;
      public static final int WEST = 16;
      public static final int EAST = 32;
      public static final int ALL = 63;
   }
}
