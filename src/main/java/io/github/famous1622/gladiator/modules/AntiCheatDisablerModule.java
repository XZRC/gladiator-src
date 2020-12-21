package io.github.famous1622.gladiator.modules;

import me.rigamortis.seppuku.Seppuku;
import me.rigamortis.seppuku.api.event.EventStageable;
import me.rigamortis.seppuku.api.event.network.EventReceivePacket;
import me.rigamortis.seppuku.api.event.network.EventSendPacket;
import me.rigamortis.seppuku.api.event.player.EventPlayerUpdate;
import me.rigamortis.seppuku.api.module.Module;
import me.rigamortis.seppuku.api.value.Value;
import net.minecraft.network.Packet;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

import java.util.ArrayList;

import static me.rigamortis.seppuku.api.event.EventStageable.EventStage.PRE;

public class AntiCheatDisablerModule extends Module {
    public final Value<Integer> delay = new Value<Integer>("EveryNTicks", new String[]{"dly", "d"}, "Delay between Messages.", 2, 1, 20, 1);
    public final Value<Mode> mode = new Value<>("Mode", new String[]{"Mode", "M"}, "Change between modes", Mode.SIGMA_HYPIXEL);

    public boolean watchdogOrSmthng = false;
    private ArrayList<Packet<?>> packets = new ArrayList<>();
    public AntiCheatDisablerModule() {
        super("AntiCheatDisabler", new String[]{"acd"}, "Disables anti-cheat", "NONE", -1, ModuleType.EXPLOIT);
    }



    @Override
    public void onEnable() {
        switch (mode.getValue()) {
            case SIGMA_HYPIXEL:
                if (mc.player.onGround && mc.player.collidedVertically) {
                    double x = mc.player.posX;
                    double y = mc.player.posY;
                    double z = mc.player.posZ;
                    Packet<INetHandlerPlayServer> a = new CPacketPlayer.Position(x, y + 0.16, z, true);
                    Packet<INetHandlerPlayServer> b = new CPacketPlayer.Position(x, y + 0.07, z, true);
                    packets.add(a);
                    packets.add(b);
                    mc.player.connection.sendPacket(a);
                    mc.player.connection.sendPacket(b);
                    Seppuku.INSTANCE.logChat("Wait 5 seconds...");
                    watchdogOrSmthng = true;
                } else {
                    watchdogOrSmthng = false;
                }
            case JIRACHI:
                break;
        }
    }

    @Listener
    public void onUpdate(EventPlayerUpdate eventPlayerUpdate) {
        if(eventPlayerUpdate.getStage()== PRE){
            switch (mode.getValue()) {
                case SIGMA_HYPIXEL:
                    if (!watchdogOrSmthng) {
                        if (mc.player.onGround && mc.player.collidedVertically) {
                            double x = mc.player.posX;
                            double y = mc.player.posY;
                            double z = mc.player.posZ;
                            Packet<INetHandlerPlayServer> c = new CPacketPlayer.Position(x, y , z, true);
                            Packet<INetHandlerPlayServer> a = new CPacketPlayer.Position(x, y + 0.16, z, true);
                            Packet<INetHandlerPlayServer> b = new CPacketPlayer.Position(x, y + 0.07, z, true);
                            packets.add(a);
                            packets.add(b);
                            packets.add(c);
                            mc.player.connection.sendPacket(c);
                            mc.player.connection.sendPacket(a);
                            mc.player.connection.sendPacket(b);
                            watchdogOrSmthng = true;
                            Seppuku.INSTANCE.logChat("Wait 5 seconds...");
                        }
                    }
                case JIRACHI:
                    break;
            }
        }
    }

    @Listener
    public void onPacketSend(EventSendPacket event) {
        if (event.getStage() == PRE && event.getPacket() instanceof CPacketPlayer && watchdogOrSmthng && mode.getValue() == Mode.SIGMA_HYPIXEL) {
            event.setCanceled(true);
        }
    }

    @Listener
    public void onPacketRecv(EventReceivePacket event) {
        if (event.getPacket() instanceof SPacketPlayerPosLook) {
            SPacketPlayerPosLook packet = (SPacketPlayerPosLook) event.getPacket();
            packet.yaw = mc.player.rotationYaw;
            packet.pitch = mc.player.rotationPitch;
            if (watchdogOrSmthng && mode.getValue() == Mode.SIGMA_HYPIXEL) {
                Seppuku.INSTANCE.logChat("Do whatever lol");
                this.toggle();
            }
        }
    }

    public enum Mode {
        SIGMA_HYPIXEL,
        JIRACHI
    }
}
