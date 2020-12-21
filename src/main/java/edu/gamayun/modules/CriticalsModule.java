package edu.gamayun.modules;
import me.rigamortis.seppuku.Seppuku;
import me.rigamortis.seppuku.api.event.EventStageable;
import me.rigamortis.seppuku.api.event.network.EventSendPacket;
import me.rigamortis.seppuku.api.event.player.EventPreAttack;
import me.rigamortis.seppuku.api.module.Module;
import me.rigamortis.seppuku.api.value.Value;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumParticleTypes;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

/**
 * Author Seth
 * 5/1/2019 @ 12:41 AM.
 *
 * Edited By: Zim
 * 2/24/20
 */
public final class CriticalsModule extends Module {

    public final Value<Mode> mode = new Value<>("Mode", new String[]{"Mode", "M"}, "The criticals mode to use.", Mode.PACKET);

    public CriticalsModule() {
        super("Criticals", new String[]{"Crits"}, "Attempts to preform a critical while hitting entities", "NONE", -1, ModuleType.COMBAT);
    }

    public void onEnable(){
        super.onEnable();
    }

    public void onDisable(){
        super.onDisable();
    }

    @Override
    public String getMetaData() {
        return this.mode.getValue().name();
    }

    @Listener
    public void preAttack(EventPreAttack event){
        if(mc.player.onGround){
            switch (mode.getValue()){
                case JUMP:
                    mc.player.jump();
                case PACKET:
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.1625, mc.player.posZ, false));
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 4.0E-6, mc.player.posZ, false));
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.0E-6, mc.player.posZ, false));
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                    mc.player.connection.sendPacket(new CPacketPlayer());
                    mc.player.onCriticalHit(event.getTarget());
            }
            mc.effectRenderer.emitParticleAtEntity(event.getTarget(), EnumParticleTypes.DRAGON_BREATH);
        }
    }

    private enum Mode {
        JUMP, PACKET
    }
}