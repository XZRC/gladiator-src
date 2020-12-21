package me.rigamortis.seppuku.impl.module.movement;

import me.rigamortis.seppuku.Seppuku;
import me.rigamortis.seppuku.api.event.player.EventMove;
import me.rigamortis.seppuku.api.module.Module;
import me.rigamortis.seppuku.api.util.BlockUtil;
import me.rigamortis.seppuku.api.util.InventoryUtil;
import me.rigamortis.seppuku.api.value.Value;
import me.rigamortis.seppuku.impl.module.player.FreeCamModule;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.ClickType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

/**
 * Author Seth
 * 4/25/2019 @ 6:29 PM.
 */
public final class SafeScaffoldModule extends Module {

    public final Value<Integer> height = new Value<Integer>("Height", new String[]{"Hei", "H"}, "Height to start scaffolding at", 0);

    public final Value<Boolean> refill = new Value<Boolean>("Refill", new String[]{"ref"}, "Automatically refills hotbar while using scaffold", true);

    public SafeScaffoldModule() {
        super("SafeScaffold", new String[]{"SScaffold"}, "Scaffold, but not rage mode", "NONE", -1, ModuleType.MOVEMENT);
    }

    @Listener
    public void onMove(EventMove event) {
        final Minecraft mc = Minecraft.getMinecraft();
        double x = event.getX();
        double y = event.getY();
        double z = event.getZ();
        double origx = x;
        double origz = z;
        final FreeCamModule freeCam = (FreeCamModule)Seppuku.INSTANCE.getModuleManager().find(FreeCamModule.class);

        if(freeCam != null && freeCam.isEnabled()) {
          return;
        }

        if (mc.player.onGround && !mc.player.noClip) {
            double increment = 0.05D;
            boolean saved = false;
            for (;x != 0.0D && isOffsetBBEmpty(x, -height.getValue(), 0.0D); ) {
                saved = true;
                if (x < increment && x >= -increment) {
                    x = 0.0D;
                } else if (x > 0.0D) {
                    x -= increment;
                } else {
                    x += increment;
                }
            }
            for (; z != 0.0D && isOffsetBBEmpty(0.0D, -height.getValue(), z); ) {
                saved = true;
                if (z < increment && z >= -increment) {
                    z = 0.0D;
                } else if (z > 0.0D) {
                    z -= increment;
                } else {
                    z += increment;
                }
            }
            for (; x != 0.0D && z != 0.0D && isOffsetBBEmpty(x, -height.getValue(), z); ) {
                saved = true;
                if (x < increment && x >= -increment) {
                    x = 0.0D;
                } else if (x > 0.0D) {
                    x -= increment;
                } else {
                    x += increment;
                }
                if (z < increment && z >= -increment) {
                    z = 0.0D;
                } else if (z > 0.0D) {
                    z -= increment;
                } else {
                    z += increment;
                }
            }

            if (saved) {
                final int blockSlot = ScaffoldModule.findStackHotbar();
                if (blockSlot != -1) {
                    int oldSlot = mc.player.inventory.currentItem;
                    switchToBlockSlot();
                    Vec3d newPos = mc.player.getPositionVector().add(Vec3d.fromPitchYaw(0.0f, mc.player.rotationYaw).scale(0.1));
                    BlockUtil.placeBlockWithScaffold(new BlockPos(newPos.x,newPos.y-1,newPos.z));
                    mc.player.inventory.currentItem = oldSlot;
                    mc.playerController.updateController();
                }
            }
        }
        event.setX(x);
        event.setY(y);
        event.setZ(z);
    }

    private boolean isOffsetBBEmpty(double x, double y, double z) {
        return Minecraft.getMinecraft().world
                .getCollisionBoxes(
                        Minecraft.getMinecraft().player,
                        Minecraft.getMinecraft().player
                                .getEntityBoundingBox()
                                .offset(x,y,z)).isEmpty();
    }

    private void switchToBlockSlot() {
        final Minecraft mc = Minecraft.getMinecraft();
        if (refill.getValue()) {
            final int slot = ScaffoldModule.findStackHotbar();
            if (slot != -1) {
                mc.player.inventory.currentItem = slot;
                mc.playerController.updateController();
            } else {
                final int invSlot = ScaffoldModule.findStackInventory();
                if (invSlot != -1) {
                    final int empty = InventoryUtil.findEmptyHotbar();
                    mc.playerController.windowClick(mc.player.inventoryContainer.windowId, invSlot, empty == -1 ? mc.player.inventory.currentItem : empty, ClickType.SWAP, mc.player);
                    mc.player.inventory.currentItem = empty;
                    mc.playerController.updateController();
                    mc.player.setVelocity(0, 0, 0);
                }
            }
        }

    }

}
