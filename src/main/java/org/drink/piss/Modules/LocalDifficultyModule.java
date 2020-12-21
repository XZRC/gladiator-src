package org.drink.piss.Modules;

import me.rigamortis.seppuku.Seppuku;
import me.rigamortis.seppuku.api.event.EventStageable;
import me.rigamortis.seppuku.api.event.network.EventReceivePacket;
import me.rigamortis.seppuku.api.event.render.EventRender3D;
import me.rigamortis.seppuku.api.module.Module;
import me.rigamortis.seppuku.api.util.RenderUtil;
import me.rigamortis.seppuku.api.value.Value;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author cats
 */
public class LocalDifficultyModule extends Module {

    public final Value<Float> scale = new Value<Float>("Scale", new String[]{"S", "TextScale"}, "Scale of the text size on the drawn information.", 1.0f, 0.0f, 3.0f, 0.25f);

    private ICamera frustum = new Frustum();

    private CopyOnWriteArrayList<ChunkData> chunkList = new CopyOnWriteArrayList<>();

    public LocalDifficultyModule() {
        super("ChunkDifficulty", new String[]{"ChunkD", "CDifficulty", "cDiff"}, "Shows the difficulty of a chunk below it.", "NONE", -1, ModuleType.WORLD);
    }

    @Listener
    public void onToggle() {
        super.onToggle();
        this.chunkList.clear();
    }

    @Listener
    public void receivePacket(EventReceivePacket event) {
        if(event.getStage() == EventStageable.EventStage.PRE) {
            if(event.getPacket() instanceof SPacketChunkData) {
                final SPacketChunkData packet = (SPacketChunkData) event.getPacket();
                ChunkData chunk = new ChunkData(packet.getChunkX() * 16, packet.getChunkZ() * 16);

                if(!this.chunkList.contains(chunk)) {
                    this.chunkList.add(chunk);
                }
            }
        }
    }

    @Listener
    public void render3D(EventRender3D event) {
        for (ChunkData chunkData : this.chunkList) {
            if(chunkData != null) {
                this.frustum.setPosition(Minecraft.getMinecraft().getRenderViewEntity().posX, Minecraft.getMinecraft().getRenderViewEntity().posY, Minecraft.getMinecraft().getRenderViewEntity().posZ);

                final AxisAlignedBB bb = new AxisAlignedBB(chunkData.x, 0, chunkData.z, chunkData.x + 16, 1, chunkData.z + 16);

                if (frustum.isBoundingBoxInFrustum(bb)) {
                    GlStateManager.pushMatrix();
                    RenderUtil.glBillboardDistanceScaled((float) chunkData.x, (float) 0, (float) chunkData.z, mc.player, this.scale.getValue());
                    GlStateManager.disableDepth();
                    drawDifficulty(chunkData);
                    GlStateManager.enableDepth();
                    GlStateManager.popMatrix();
                }
            }
        }
    }

    private void drawDifficulty(ChunkData chunk) {

        final double roundedDifficulty = Math.round(mc.world.getDifficultyForLocation(new BlockPos(chunk.x, 0, chunk.z)).getAdditionalDifficulty() * 1000d) / 1000d;

        Seppuku.INSTANCE.getFontManager().drawString(Double.toString(roundedDifficulty), 0, 0, 0xFFAAAAAA, true, false);

    }

    public static class ChunkData {
        private int x;
        private int z;

        public ChunkData(int x, int z) {
            this.x = x;
            this.z = z;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getZ() {
            return z;
        }

        public void setZ(int z) {
            this.z = z;
        }
    }
}
