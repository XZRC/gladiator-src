package org.drink.piss.Modules;

import me.rigamortis.seppuku.api.event.render.EventRender3D;
import me.rigamortis.seppuku.api.event.render.EventRenderBlockModel;
import me.rigamortis.seppuku.api.event.world.EventChunk;
import me.rigamortis.seppuku.api.module.Module;
import me.rigamortis.seppuku.api.util.ColorUtil;
import me.rigamortis.seppuku.api.util.MathUtil;
import me.rigamortis.seppuku.api.util.RenderUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockTripWire;
import net.minecraft.block.BlockTripWireHook;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class TripWireESPModule extends Module {

    private final List<Vec3d> wires = new CopyOnWriteArrayList<>();

    public TripWireESPModule() {
        super("TripWireESP", new String[]{"TripWire", "TripESP"}, "Highlights tripwires so you don't get caught on them", "NONE", -1,  ModuleType.RENDER);
    }

    @Listener
    public void onChunkLoad(EventChunk event) {
        final Minecraft mc = Minecraft.getMinecraft();
        switch (event.getType()) {
            case LOAD:
                final Chunk chunk = event.getChunk();
                final ExtendedBlockStorage[] blockStoragesLoad = chunk.getBlockStorageArray();
                for (int i = 0; i < blockStoragesLoad.length; i++) {
                    final ExtendedBlockStorage extendedBlockStorage = blockStoragesLoad[i];
                    if (extendedBlockStorage == null) {
                        continue;
                    }

                    for (int x = 0; x < 16; ++x) {
                        for (int y = 0; y < 16; ++y) {
                            for (int z = 0; z < 16; ++z) {
                                final IBlockState blockState = extendedBlockStorage.get(x, y, z);
                                final int worldY = y + extendedBlockStorage.getYLocation();
                                if (blockState.getBlock().equals(Blocks.TRIPWIRE)) {
                                    BlockPos position = new BlockPos(event.getChunk().getPos().getXStart() + x, worldY, event.getChunk().getPos().getZStart() + z);
                                    if (!isWireCached(position.getX(), position.getY(), position.getZ(), 0)) {
                                        final Vec3d portal = new Vec3d(position.getX(), position.getY(), position.getZ());
                                        this.wires.add(portal);
                                        return;
                                    }
                                }
                                if (blockState.getBlock().equals(Blocks.TRIPWIRE_HOOK)) {
                                    BlockPos position = new BlockPos(event.getChunk().getPos().getXStart() + x, worldY, event.getChunk().getPos().getZStart() + z);
                                    if (!isWireCached(position.getX(), position.getY(), position.getZ(), 3)) {
                                        final Vec3d portal = new Vec3d(position.getX(), position.getY(), position.getZ());
                                        this.wires.add(portal);
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }
                break;
            case UNLOAD:
                    for (Vec3d wire : this.wires) {
                        if (mc.player.getDistance(wire.x, wire.y, wire.z) > 100) {
                            this.wires.remove(wire);
                        }
                    }
                break;
        }
    }

    @Listener
    public void onRender(EventRender3D event) {
        for (Vec3d wire : this.wires) {
            final Minecraft mc = Minecraft.getMinecraft();
            final Vec3d interp = MathUtil.interpolateEntity(mc.player, mc.getRenderPartialTicks());
//                RenderUtil.drawBoundingBox(iblockstate.getSelectedBoundingBox(mc.world, blockpos).grow(0.0020000000949949026D).offset(-interp.x, -interp.y, -interp.z), 1.5f, 0xFF9900EE);
            RenderUtil.drawFilledBox(new AxisAlignedBB(wire.x, wire.y, wire.z, wire.x + 1, wire.y + 1, wire.z +1).grow(0.0020000000949949026D).offset(-interp.x, -interp.y, -interp.z), 0xFF9900EE);
        }
    }

    private boolean isWireCached(int x, int y, int z, float dist) {
        for (int i = this.wires.size() - 1; i >= 0; i--) {
            Vec3d searchPortal = this.wires.get(i);

            if (searchPortal.distanceTo(new Vec3d(x, y, z)) <= dist)
                return true;

            if (searchPortal.x == x && searchPortal.y == y && searchPortal.z == z)
                return true;
        }
        return false;
    }
}
