package io.github.famous1622.gladiator.modules;

import io.github.famous1622.gladiator.util.KamiTessellator;
import me.rigamortis.seppuku.api.event.render.EventRender3D;
import me.rigamortis.seppuku.api.module.Module;
import me.rigamortis.seppuku.api.util.ColorUtil;
import me.rigamortis.seppuku.api.util.RenderUtil;
import me.rigamortis.seppuku.api.value.Value;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

public class MapBoundariesModule extends Module {


    public MapBoundariesModule() {
        super("MapBoundaries", new String[]{"mapbounds"}, "Like chunkborders for maps", "NONE", -1, ModuleType.WORLD);
    }

    @Listener
    public void onRender(EventRender3D event) {
        final Minecraft mc = Minecraft.getMinecraft();
        final float partialTicks = event.getPartialTicks();
        final EntityPlayer player = mc.player;


        double d0 = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double) partialTicks;
        double d1 = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double) partialTicks;
        double d2 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double) partialTicks;


        int xCenter = MathHelper.floor((d0 + 64.0D) / 128) * 128;
        int zCenter = MathHelper.floor((d2 + 64.0D) / 128) * 128;
        double x = xCenter - 64 - mc.getRenderManager().viewerPosX;
        double z = zCenter - 64 - mc.getRenderManager().viewerPosZ;

        final AxisAlignedBB bb = new AxisAlignedBB(x, -mc.getRenderManager().viewerPosY, z, x+128, 256-mc.getRenderManager().viewerPosY, z+128);



//        RenderUtil.drawBoundingBox(bb, 1.5f, 0xffff00ff);
        KamiTessellator.prepare(GL11.GL_LINES);
        KamiTessellator.drawLines(KamiTessellator.getBufferBuilder(), (float) x, (float) -mc.getRenderManager().viewerPosY, (float) z, 128, 255, 128, 255, 0, 255, 255, KamiTessellator.GeometryMasks.Line.ALL);
        KamiTessellator.release();
//        RenderUtil.drawBoundingBox(mc.player.getCollisionBoundingBox(), 4, 0xFFff00ff);
//        RenderUtil.drawBoundingBox(bb, 20, 0xffff00ff);
//        RenderUtil.drawPlane(bb, 0x00ff00ff | (0x1000000 * opacity.getValue()));
//        RenderUtil.drawFilledBox(bb, 0x00ff00ff | (0x1000000 * opacity.getValue()));

    }


}
