package edu.gamayun.modules;

import io.github.famous1622.gladiator.event.EventElytraFlyCheck;
import me.rigamortis.seppuku.api.event.EventStageable;
import me.rigamortis.seppuku.api.event.player.EventUpdateWalkingPlayer;
import me.rigamortis.seppuku.api.module.Module;
import me.rigamortis.seppuku.api.util.MathUtil;
import me.rigamortis.seppuku.api.value.Value;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.CPacketEntityAction;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

/**
 *
 * @author ZimTheDestroyer
 * @information This Elytrafly was designed to bypass JJ's mouth. He told me I could not go faster than him, I proved him wrong.
 *
 * Basically spoofs vanilla fly whilst using an Elytra.
 *
 **/


public class BetterElytrafly extends Module {

    Minecraft mc = Minecraft.getMinecraft();

    public final Value<Float> speed = new Value<>("Speed", new String[]{"Spd"},"", 0.06f, 0.0f, 10.0f, 0.01f);

    public BetterElytrafly(){
        super("BetterElytrafly", new String[]{"bfly"}, "ZOOM with Elytra's", "NONE", -1, ModuleType.MOVEMENT);
    }

    public void onEnable() {
        super.onEnable();
        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
    }

    public void onDisable() {
        super.onDisable();
    }

    @Listener
    public void onPlayerWalkingUpdate(EventUpdateWalkingPlayer event){
        if(event.getStage() == EventStageable.EventStage.PRE){
            final double[] dir = MathUtil.directionSpeed(speed.getValue());

            mc.player.setVelocity(0, 0, 0);
            mc.player.jumpMovementFactor = speed.getValue();

            if (mc.gameSettings.keyBindJump.isKeyDown()) {
                mc.player.motionY += speed.getValue();
            }

            if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                mc.player.motionY -= speed.getValue();
            }

            if (mc.player.movementInput.moveStrafe != 0 || mc.player.movementInput.moveForward != 0) {
                mc.player.motionX = dir[0];
                mc.player.motionZ = dir[1];
            } else {
                mc.player.motionX = 0;
                mc.player.motionZ = 0;
            }
        }
    }

    private enum Mode {
        Bounds, Legit
    }

    @Listener
    public void elytraEvent(EventElytraFlyCheck event){
        event.setCanceled(true);
    }
}
