package me.rigamortis.seppuku.impl.module.movement;

import me.rigamortis.seppuku.api.event.EventStageable;
import me.rigamortis.seppuku.api.event.player.EventPlayerUpdate;
import me.rigamortis.seppuku.api.module.Module;
import me.rigamortis.seppuku.api.util.MathUtil;
import me.rigamortis.seppuku.api.value.Value;
import net.minecraft.client.Minecraft;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

public class BoatFlyModule extends Module {
    public BoatFlyModule() {
        super("BoatFly", new String[]{"Boat"}, "boat", "NONE", -1, ModuleType.MOVEMENT);
    }

    public final Value<Mode> mode = new Value<Mode>("Mode", new String[]{"Mode", "M"}, "The flight mode to use.", Mode.NORMAL);

    private enum Mode {
        NORMAL, CLIP, PACKET
    }

    public final Value<Float> speed = new Value<Float>("Speed", new String[]{"Spd"}, "Speed multiplier for boatfly, higher values equals more speed.", 1.0f);

    public final Value<Boolean> noKick = new Value<Boolean>("NoKick", new String[]{"AntiKick", "Kick"}, "Bypass the server kicking you for flying while in flight.", true);


    @Listener
    public void onUpdate(EventPlayerUpdate event) {
        if (event.getStage() == EventStageable.EventStage.PRE) {
            final Minecraft mc = Minecraft.getMinecraft();
            if (mc.player != null && mc.player.getRidingEntity() != null) {
                switch (this.mode.getValue()) {
                    case NORMAL:
                        mc.player.getRidingEntity().setNoGravity(true);
                        mc.player.getRidingEntity().motionY = 0;
                        mc.player.getRidingEntity().rotationYaw = mc.player.rotationYaw;
                        if (mc.gameSettings.keyBindJump.isKeyDown()) {
                            mc.player.getRidingEntity().onGround = false;
                            mc.player.getRidingEntity().motionY = (this.speed.getValue()/10);
                        }

                        if (mc.gameSettings.keyBindSprint.isKeyDown()) {
                            mc.player.getRidingEntity().onGround = false;
                            mc.player.getRidingEntity().motionY = -(this.speed.getValue()/10);
                        }

                        final double[] normalDir = MathUtil.directionSpeed(this.speed.getValue()/2);

                        if (mc.player.movementInput.moveStrafe != 0 || mc.player.movementInput.moveForward != 0) {
                            mc.player.getRidingEntity().motionX = normalDir[0];
                            mc.player.getRidingEntity().motionZ = normalDir[1];
                        } else {
                            mc.player.getRidingEntity().motionX = 0;
                            mc.player.getRidingEntity().motionZ = 0;
                        }

                        if (this.noKick.getValue()) {
                            if (mc.gameSettings.keyBindJump.isKeyDown()) {
                                if (mc.player.ticksExisted % 8 < 2) {
                                    mc.player.getRidingEntity().motionY = -0.04f;
                                }
                            } else {
                                if (mc.player.ticksExisted % 8 < 4) {
                                    mc.player.getRidingEntity().motionY = -0.08f;
                                }
                            }
                        }

                    case CLIP:
                        mc.player.getRidingEntity().setNoGravity(true);
                        mc.player.getRidingEntity().motionY = 0;
                        mc.player.getRidingEntity().rotationYaw = mc.player.rotationYaw;
                        if (mc.gameSettings.keyBindJump.isKeyDown()) {
                            mc.player.getRidingEntity().onGround = false;
                            mc.player.getRidingEntity().setPosition(mc.player.getRidingEntity().posX, mc.player.getRidingEntity().posY + (this.speed.getValue()/10), mc.player.getRidingEntity().posZ);
                        }

                        if (mc.gameSettings.keyBindSprint.isKeyDown()) {
                            mc.player.getRidingEntity().onGround = false;
                            mc.player.getRidingEntity().setPosition(mc.player.getRidingEntity().posX, mc.player.getRidingEntity().posY - (this.speed.getValue()/10), mc.player.getRidingEntity().posZ);
                        }

                        final double[] dir = MathUtil.directionSpeed(this.speed.getValue()/2);

                        if (mc.player.movementInput.moveStrafe != 0 || mc.player.movementInput.moveForward != 0) {
                            mc.player.getRidingEntity().setPosition(mc.player.getRidingEntity().posX + dir[0], mc.player.getRidingEntity().posY, mc.player.getRidingEntity().posZ + dir[1]);
                        } else {
                            mc.player.getRidingEntity().setPosition(mc.player.getRidingEntity().posX, mc.player.getRidingEntity().posY, mc.player.getRidingEntity().posZ);
                        }

                        if (this.noKick.getValue()) {
                            if (mc.gameSettings.keyBindJump.isKeyDown()) {
                                if (mc.player.ticksExisted % 8 < 2) {
                                    mc.player.getRidingEntity().motionY = -0.04f;
                                }
                            } else {
                                if (mc.player.ticksExisted % 8 < 4) {
                                    mc.player.getRidingEntity().motionY = -0.08f;
                                }
                            }
                        }
                    case PACKET:
                }
            }
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        final Minecraft mc = Minecraft.getMinecraft();
        if (mc.player.getRidingEntity() != null) {
            mc.player.getRidingEntity().setNoGravity(false);
        }
    }
}
