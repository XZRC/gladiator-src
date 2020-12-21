package org.drink.piss.Modules;

import me.rigamortis.seppuku.api.event.EventStageable;
import me.rigamortis.seppuku.api.event.player.EventUpdateWalkingPlayer;
import me.rigamortis.seppuku.api.module.Module;
import me.rigamortis.seppuku.api.util.MathUtil;
import me.rigamortis.seppuku.api.value.Value;
import net.minecraft.client.Minecraft;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

/**
 * @author cats
 */

public class TwoBeeElytraFlyModule extends Module {

    public final Value<Float> hSpeed = new Value<Float>("Speed", new String[]{"spd","S"}, "The speed to fly at.", 1f, 0f, 100f, 0.1f);

    public final Value<Float> vSpeed = new Value<Float>("VerticalSpeed", new String[]{"vspeed","vspd","vS"}, "The speed you move vertically", 1f, 0f, 100f, 0.1f);

    public final Value<Float> glide = new Value<>("Glide", new String[]{"g"}, "Glide speed.", 0.0001f);

    public Value<Mode> mode = new Value<>("Mode", new String[]{"Mode", "M"}, "Which elytraflight mode to use", Mode.NORMAL);

    private enum Mode {
        NORMAL, HALFCLIP, FULLCLIP
    }

    private Value<Boolean> noKick = new Value<>("NoKick", new String[]{"nk", "AntiKick", "Kick"}, "Automatically descends to avoid being kicked by anticheat", true);

    private Value<Boolean> allowUp = new Value<>("AllowUp", new String[]{"up"}, "Determines if you will be allowed to go up, to prevent accidentally kicking yourself out of flight", true);

    private Double posX;
    private Double flyHeight;
    private Double posZ;

    public TwoBeeElytraFlyModule() {
        super("2b2tElytraFly", new String[]{"2b2tElytra", "2be", "BypassElytra"}, "Lets you elytrafly on 2b2t.", "NONE", -1, ModuleType.MOVEMENT);
    }

    @Listener
    public void onWalkingUpdate(EventUpdateWalkingPlayer event) {
        if (event.getStage() != EventStageable.EventStage.PRE) return;
        final Minecraft mc = Minecraft.getMinecraft();

        switch (this.mode.getValue()) {
            case NORMAL: {
                if (mc.player.isElytraFlying()) {
                    if (flyHeight == null) {
                        flyHeight = mc.player.posY;
                    }
                } else {
                    flyHeight = null;
                    return;
                }

                if (this.noKick.getValue()) {
                    //if (mc.player.ticksExisted % 8 < 2) {
                        flyHeight -= this.glide.getValue();
                    //}
                }

                mc.player.setPosition(mc.player.posX, flyHeight, mc.player.posZ);
                mc.player.setVelocity(0, 0, 0);

                //mc.player.jumpMovementFactor = this.speed.getValue();

                final double[] dir = MathUtil.directionSpeed(this.hSpeed.getValue());

                if (mc.player.movementInput.moveStrafe != 0 || mc.player.movementInput.moveForward != 0) {
                    mc.player.motionX = dir[0];
                    mc.player.motionZ = dir[1];
                } else {
                    mc.player.motionX = 0;
                    mc.player.motionZ = 0;
                }

                if (mc.gameSettings.keyBindJump.isKeyDown()) {
                    mc.player.motionY += this.vSpeed.getValue();
                    //flyHeight = mc.player.posY + this.speed.getValue;
                }

                if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                    mc.player.motionY -= this.vSpeed.getValue();
                    flyHeight = mc.player.posY + mc.player.motionY;
                }
                break;
            }

            case HALFCLIP: {

                if (mc.player.isElytraFlying()) {
                    if (flyHeight == null) {
                        flyHeight = mc.player.posY;
                    }
                } else {
                    flyHeight = null;
                    return;
                }

                if (this.noKick.getValue()) {
                    //if (mc.player.ticksExisted % 8 < 2) {
                        flyHeight -= this.glide.getValue();
                    //}
                }

                posX = 0d;
                posZ = 0d;

                //mc.player.jumpMovementFactor = (float) this.speed.getValue();

                final double[] dir = MathUtil.directionSpeed(this.hSpeed.getValue());

                if (mc.player.movementInput.moveStrafe != 0 || mc.player.movementInput.moveForward != 0) {
                    posX = dir[0];
                    posZ = dir[1];
                }

                if (mc.gameSettings.keyBindJump.isKeyDown()) {
                    flyHeight = mc.player.posY + this.vSpeed.getValue();
                }

                if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                    flyHeight = mc.player.posY - this.vSpeed.getValue();
                }

                mc.player.setPosition(mc.player.posX + posX, flyHeight, mc.player.posZ + posZ);
                mc.player.setVelocity(0, 0, 0);
                break;
            }


            case FULLCLIP: {

                if (mc.player.isElytraFlying()) {
                    if (flyHeight == null
                            || (posX == null || posX == 0)
                            || (posZ == null || posZ == 0)) {
                        flyHeight = mc.player.posY;
                        posX = mc.player.posX;
                        posZ = mc.player.posZ;
                        // if any of these are null, or 0, we will want to get them again
                    }
                } else {
                    flyHeight = null;
                    posX = null;
                    posZ = null;
                    return;
                }

                if (this.noKick.getValue()) {
                    //if (mc.player.ticksExisted % 8 < 2) {
                    flyHeight -= glide.getValue();
                    //}
                }

                final double[] dir = MathUtil.directionSpeed(this.hSpeed.getValue());

                if (mc.player.movementInput.moveStrafe != 0 || mc.player.movementInput.moveForward != 0) {
                    posX += dir[0];
                    posZ += dir[1];
                }

                if (allowUp.getValue() && mc.gameSettings.keyBindJump.isKeyDown()) {
                    flyHeight = mc.player.posY + this.vSpeed.getValue() / 10;
                }

                if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                    flyHeight = mc.player.posY - this.vSpeed.getValue() / 10;
                }

                mc.player.setPosition(posX, flyHeight, posZ);
                mc.player.setVelocity(0, 0, 0);
                // move this to the bottom so the changes we make with the posX and posZ make a difference
                break;
            }
        }
    }

    /*
    @Override
    public void onEnable() {
        //Get height to fly at, so we don't drift down because of the elytra
        flyHeight = mc.player.posY;
    }

    @Override
    public void onDisable() {
        flyHeight = null;
    }
    */

    @Listener
    public void onToggle() {
        super.onToggle();
        flyHeight = null;
        posX = null;
        posZ = null;
    }
}