package edu.gamayun.modules;

import me.rigamortis.seppuku.api.event.EventStageable;
import me.rigamortis.seppuku.api.event.gui.EventRenderHelmet;
import me.rigamortis.seppuku.api.event.network.EventReceivePacket;
import me.rigamortis.seppuku.api.event.network.EventSendPacket;
import me.rigamortis.seppuku.api.event.player.*;
import me.rigamortis.seppuku.api.event.render.EventRenderOverlay;
import me.rigamortis.seppuku.api.event.world.EventAddCollisionBox;
import me.rigamortis.seppuku.api.event.world.EventSetOpaqueCube;
import me.rigamortis.seppuku.api.module.Module;
import me.rigamortis.seppuku.api.util.MathUtil;
import me.rigamortis.seppuku.api.value.Value;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.BlockPos;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

import java.util.ArrayList;
import java.util.List;

public class BetterPacketFly extends Module {
    public final Value<Float> speed = new Value<Float>("Speed", new String[]{"Spd"},"Speed", 0.06f, 0.0f, 10.0f, 0.01f);

    private final Value<Boolean> noKick = new Value<Boolean>("NoKick", new String[]{"AntiKick", "Kick"}, "Bypass the server kicking you for flying while in flight.", true);
    public final Value<Boolean> phase = new Value<Boolean>("Phase", new String[]{"Phase", "P"}, "To phase or to fly?", false);
    public final Value<Mode> mode = new Value<Mode>("Mode", new String[]{"Mode", "M"}, "What type of fuckery would you like today?", Mode.Y);

    private int teleportId;
    private List<CPacketPlayer> packets = new ArrayList<>();

    public BetterPacketFly() {
        super("BetterPacketfly", new String[]{ "packetfly", "bpfly", "bypassfly"}, "Packet fly bypass for 9b9t", "NONE", -1, ModuleType.MOVEMENT);
    }

    private enum Mode {
        Up, Down, Zero, Y, X, Z, XZ
    }

    @Listener
    public void setOpaqueCube(EventSetOpaqueCube event) {
        event.setCanceled(true);
    }

    @Listener
    public void renderOverlay(EventRenderOverlay event) {
        event.setCanceled(true);
    }

    @Listener
    public void renderHelmet(EventRenderHelmet event) {
        event.setCanceled(true);
    }

    @Listener
    public void pushOutOfBlocks(EventPushOutOfBlocks event) {
        event.setCanceled(true);
    }

    @Listener
    public void pushedByWater(EventPushedByWater event) {
        event.setCanceled(true);
    }

    @Listener
    public void applyCollision(EventApplyCollision event) {
        event.setCanceled(true);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        final Minecraft mc = Minecraft.getMinecraft();

        if (mc.world != null) {
            this.teleportId = 0;
            this.packets.clear();
            final CPacketPlayer bounds = new CPacketPlayer.Position(mc.player.posX, 10000, mc.player.posZ, mc.player.onGround);
            this.packets.add(bounds);
            mc.player.connection.sendPacket(bounds);
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }


    @Listener
    public void onWalkingUpdate(EventUpdateWalkingPlayer event) {
        if (event.getStage() == EventStageable.EventStage.PRE) {
            final Minecraft mc = Minecraft.getMinecraft();

            if (this.teleportId <= 0) {
                final CPacketPlayer bounds = new CPacketPlayer.Position(Minecraft.getMinecraft().player.posX, 10000, Minecraft.getMinecraft().player.posZ, Minecraft.getMinecraft().player.onGround);
                this.packets.add(bounds);
                Minecraft.getMinecraft().player.connection.sendPacket(bounds);
                return;
            }

            mc.player.setVelocity(0, 0, 0);

            if (mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().expand(-0.0625d, 0, -0.0625d)).isEmpty()) {
                double ySpeed = 0;

                if (mc.gameSettings.keyBindJump.isKeyDown()) {

                    if (this.noKick.getValue()) {
                        ySpeed = mc.player.ticksExisted % 20 == 0 ? -0.04f : 0.062f;
                    } else {
                        ySpeed = 0.062f;
                    }
                } else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                    ySpeed = -0.062d;
                } else {
                    ySpeed = mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().expand(-0.0625d, -0.0625d, -0.0625d)).isEmpty() ? (mc.player.ticksExisted % 4 == 0) ? (this.noKick.getValue() ? -0.04f : 0.0f) : 0.0f : 0.0f;
                }

                final double[] directionalSpeed = MathUtil.directionSpeed(this.speed.getValue());

                if (mc.gameSettings.keyBindJump.isKeyDown() || mc.gameSettings.keyBindSneak.isKeyDown() || mc.gameSettings.keyBindForward.isKeyDown() || mc.gameSettings.keyBindBack.isKeyDown() || mc.gameSettings.keyBindRight.isKeyDown() || mc.gameSettings.keyBindLeft.isKeyDown()) {
                    if (directionalSpeed[0] != 0.0d || ySpeed != 0.0d || directionalSpeed[1] != 0.0d) {
                        if (mc.player.movementInput.jump && (mc.player.moveStrafing != 0 || mc.player.moveForward != 0)) {
                            mc.player.setVelocity(0, 0, 0);
                            move(0, 0, 0);
                            for (int i = 0; i <= 3; i++) {
                                mc.player.setVelocity(0, ySpeed * i, 0);
                                move(0, ySpeed * i, 0);
                            }
                        } else {
                            if (mc.player.movementInput.jump) {
                                mc.player.setVelocity(0, 0, 0);
                                move(0, 0, 0);
                                for (int i = 0; i <= 3; i++) {
                                    mc.player.setVelocity(0, ySpeed * i, 0);
                                    move(0, ySpeed * i, 0);
                                }
                            } else {
                                for (int i = 0; i <= 2; i++) {
                                    mc.player.setVelocity(directionalSpeed[0] * i, ySpeed * i, directionalSpeed[1] * i);
                                    move(directionalSpeed[0] * i, ySpeed * i, directionalSpeed[1] * i);
                                }
                            }
                        }
                    }
                } else {
                    if (this.noKick.getValue()) {
                        if (mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().expand(-0.0625d, -0.0625d, -0.0625d)).isEmpty()) {
                            mc.player.setVelocity(0, (mc.player.ticksExisted % 2 == 0) ? 0.04f : -0.04f, 0);
                            move(0, (mc.player.ticksExisted % 2 == 0) ? 0.04f : -0.04f, 0);
                        }
                    }
                }
            }
        }
    }

    @Listener
    public void collideWithBlock(EventAddCollisionBox event) {
        final Minecraft mc = Minecraft.getMinecraft();

        if (event.getEntity() == mc.player || mc.player.getRidingEntity() != null && event.getEntity() == mc.player.getRidingEntity()) {
            if(phase.getValue()){
                event.setCanceled(true);
            }
        }
    }

    private CPacketPlayer createBoundsPacket(double x, double y, double z) {
        switch (mode.getValue()) {
            case Up: return new CPacketPlayer.Position(mc.player.posX + x, 10000, mc.player.posZ + z, mc.player.onGround);
            case Down: return new CPacketPlayer.Position(mc.player.posX + x, -10000, mc.player.posZ + z, mc.player.onGround);
            case Zero: return new CPacketPlayer.Position(mc.player.posX + x, 0, mc.player.posZ + z, mc.player.onGround);
            case Y: return new CPacketPlayer.Position(mc.player.posX + x, mc.player.posY + y <= 10 ? 255 : 1, mc.player.posZ + z, mc.player.onGround);
            case X: return new CPacketPlayer.Position(mc.player.posX + x + 75, mc.player.posY + y, mc.player.posZ + z, mc.player.onGround);
            case Z: return new CPacketPlayer.Position(mc.player.posX + x, mc.player.posY + y, mc.player.posZ + z + 75, mc.player.onGround);
            case XZ: return new CPacketPlayer.Position(mc.player.posX + x + 75, mc.player.posY + y, mc.player.posZ + z + 75, mc.player.onGround);
        }
        throw new RuntimeException("WTF");
    }

    private void move(double x, double y, double z) {
        final Minecraft mc = Minecraft.getMinecraft();
        final CPacketPlayer pos = new CPacketPlayer.Position(mc.player.posX + x, mc.player.posY + y, mc.player.posZ + z, mc.player.onGround);
        this.packets.add(pos);
        mc.player.connection.sendPacket(pos);

        final CPacketPlayer bounds = createBoundsPacket(x, y, z);
        this.packets.add(bounds);
        mc.player.connection.sendPacket(bounds);

        this.teleportId++;
        mc.player.connection.sendPacket(new CPacketConfirmTeleport(this.teleportId - 1));
        mc.player.connection.sendPacket(new CPacketConfirmTeleport(this.teleportId));
        mc.player.connection.sendPacket(new CPacketConfirmTeleport(this.teleportId + 1));
    }

    @Listener
    public void onPacketSend(EventSendPacket event) {
        if (event.getStage() == EventStageable.EventStage.PRE) {

            if (event.getPacket() instanceof CPacketPlayer && !(event.getPacket() instanceof CPacketPlayer.Position)) {
                event.setCanceled(true);
            }
            if (event.getPacket() instanceof CPacketPlayer) {
                final CPacketPlayer packet = (CPacketPlayer) event.getPacket();
                if (packets.contains(packet)) {
                    packets.remove(packet);
                    return;
                }
                event.setCanceled(true);
            }
        }
    }

    @Listener
    public void onPacketReceive(EventReceivePacket event) {
        if (event.getStage() == EventStageable.EventStage.PRE) {

            if (event.getPacket() instanceof SPacketPlayerPosLook) {
                final SPacketPlayerPosLook packet = (SPacketPlayerPosLook) event.getPacket();
                if (Minecraft.getMinecraft().player.isEntityAlive() && Minecraft.getMinecraft().world.isBlockLoaded(new BlockPos(Minecraft.getMinecraft().player.posX, Minecraft.getMinecraft().player.posY, Minecraft.getMinecraft().player.posZ)) && !(Minecraft.getMinecraft().currentScreen instanceof GuiDownloadTerrain)) {
                    if (this.teleportId <= 0) {
                        this.teleportId = packet.getTeleportId();
                    } else {
                        event.setCanceled(true);
                    }
                }
            }
        }
    }
}
