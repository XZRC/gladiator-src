package edu.gamayun.modules;

import me.rigamortis.seppuku.Seppuku;
import me.rigamortis.seppuku.api.event.EventStageable;
import me.rigamortis.seppuku.api.event.network.EventSendPacket;
import me.rigamortis.seppuku.api.event.player.EventUpdateWalkingPlayer;
import me.rigamortis.seppuku.api.module.Module;
import me.rigamortis.seppuku.api.value.Value;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketInput;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketVehicleMove;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

import java.util.Objects;

/**
 * BetterGodmode was created to bypass packet related patches for Godmode/Donkey duping
 *
 * It works by creating, sending, and blocking all expected and non-expected packets to the server.
 * This variant of Godmode has the unique ability to bypass all checks for Godmode via Packet checks.
 *
 * Originally created for the Gamayun Seppuku module, it later saw light in Gamayun client, and now we're here.
 *
 * @author ZimTheDestroyer
 */

public class BetterGodmode extends Module {
    public Minecraft mc = Minecraft.getMinecraft();
    public Entity entity;

    private final Value<Boolean> remount = new Value<Boolean>("Remount", new String[]{"remnt", "ride"}, "Remount the entity whilst in godmode", false);

    public BetterGodmode(){
        super("BetterGodmode", new String[] {"bettergmode", "gmodebypass", "bypasser", "godmoder"},
                "Improvements to the original godmode module - created by Zim The Destroyer",
                "", 16777215, false, false, ModuleType.EXPLOIT);
    }

    public void onEnable(){
        super.onEnable();
        if(mc.world != null && mc.player.getRidingEntity() != null){
            entity = mc.player.getRidingEntity();
            mc.renderGlobal.loadRenderers();
            hideEntity();
            mc.player.setPosition(Minecraft.getMinecraft().player.getPosition().getX(),
                    Minecraft.getMinecraft().player.getPosition().getY() -1,
                    Minecraft.getMinecraft().player.getPosition().getZ());
        }
        if(mc.world != null && remount.getValue()){
            remount.setValue(false);
        }
    }

    public void onDisable(){
        super.onDisable();
        if(remount.getValue()){
            remount.setValue(false);
        }
        mc.player.dismountRidingEntity();
        mc.getConnection().sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
        mc.getConnection().sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
    }

    @Listener
    public void onPacketSend(EventSendPacket event){
        if(event.getStage() == EventStageable.EventStage.PRE){
            if(event.getPacket() instanceof CPacketPlayer.Position || event.getPacket() instanceof CPacketPlayer.PositionRotation){
                event.setCanceled(true);
            }
        }
    }

    private void hideEntity() {
        if (mc.player.getRidingEntity() != null) {
            mc.player.dismountRidingEntity();
            mc.world.removeEntity(entity);
        }
    }

    private void showEntity(Entity entity2) {
        entity2.isDead = false;
        mc.world.loadedEntityList.add(entity2);
        mc.player.startRiding(entity2, true);
    }

    @Listener
    public void onPlayerWalkingUpdate(EventUpdateWalkingPlayer event){
        if(event.getStage() == EventStageable.EventStage.PRE){
            if(remount.getValue() && Objects.requireNonNull(Seppuku.INSTANCE.getModuleManager().find(BetterGodmode.class)).isEnabled()){
                showEntity(entity);
            }
            entity.setPositionAndRotation(
                    Minecraft.getMinecraft().player.posX,
                    Minecraft.getMinecraft().player.posY,
                    Minecraft.getMinecraft().player.posZ,
                    Minecraft.getMinecraft().player.rotationYaw,
                    Minecraft.getMinecraft().player.rotationPitch
            );
            mc.player.connection.sendPacket(new CPacketPlayer.Rotation(mc.player.rotationYaw, mc.player.rotationPitch, true));
            mc.player.connection.sendPacket(new CPacketInput(mc.player.movementInput.moveForward, mc.player.movementInput.moveStrafe, false, false));
            mc.player.connection.sendPacket(new CPacketVehicleMove(entity));
        }
    }
}
