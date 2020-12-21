package edu.gamayun.modules;

import me.rigamortis.seppuku.api.event.EventStageable;
import me.rigamortis.seppuku.api.event.network.EventSendPacket;
import me.rigamortis.seppuku.api.event.player.EventUpdateWalkingPlayer;
import me.rigamortis.seppuku.api.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketInput;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketVehicleMove;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

public class DevGMode extends Module {
    public Minecraft mc = Minecraft.getMinecraft();
    public Entity entity;

    public DevGMode(){
        super("DevGodmode", new String[] {},
                "Testing with automated duping using donkey's",
                "", 16777215, false, false, ModuleType.EXPLOIT);
    }

    public void onEnable(){
        super.onEnable();
        if(mc.player.getRidingEntity() != null){
            entity = mc.player.getRidingEntity();
            mc.renderGlobal.loadRenderers();
            mc.world.removeEntity(entity);
            mc.player.setPosition(Minecraft.getMinecraft().player.getPosition().getX(),
                    Minecraft.getMinecraft().player.getPosition().getY() -1,
                    Minecraft.getMinecraft().player.getPosition().getZ());
        }
    }

    public void onDisable(){
        super.onDisable();
        if(entity != null){
            entity.isDead = false;
            mc.world.loadedEntityList.add(entity);
        }
    }

    @Listener
    public void onPacketSend(EventSendPacket event){
        if(event.getPacket() instanceof CPacketPlayer.PositionRotation || event.getPacket() instanceof CPacketPlayer.Position){
            if(event.getStage() == EventStageable.EventStage.PRE){
                event.setCanceled(true);
            }
        }
    }

    @Listener
    public void onPlayerWalkingUpdate(EventUpdateWalkingPlayer event){
        if(event.getStage() == EventStageable.EventStage.PRE){
            if(mc.player.isRiding()){
                entity.setPositionAndRotation(mc.player.posX, mc.player.posY, mc.player.posZ, mc.player.rotationYaw, mc.player.rotationPitch);
                mc.getConnection().sendPacket(new CPacketVehicleMove(entity));
                mc.getConnection().sendPacket(new CPacketPlayer.Rotation(mc.player.rotationYaw, mc.player.rotationPitch, true));
                mc.getConnection().sendPacket(new CPacketInput(mc.player.movementInput.moveStrafe, mc.player.movementInput.moveForward, false, false));
            }
        }
    }
}
