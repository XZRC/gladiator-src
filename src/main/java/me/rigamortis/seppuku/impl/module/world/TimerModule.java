package me.rigamortis.seppuku.impl.module.world;

import me.rigamortis.seppuku.api.event.EventStageable;
import me.rigamortis.seppuku.api.event.network.EventReceivePacket;
import me.rigamortis.seppuku.api.event.player.EventPlayerUpdate;
import me.rigamortis.seppuku.api.module.Module;
import me.rigamortis.seppuku.api.util.MathUtil;
import me.rigamortis.seppuku.api.util.Timer;
import me.rigamortis.seppuku.api.value.Value;
import me.rigamortis.seppuku.impl.duck.MixinMinecraftInterface;
import me.rigamortis.seppuku.impl.duck.MixinTimerInterface;
import net.minecraft.network.play.server.SPacketParticles;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

/**
 * Author Seth
 * 4/19/2019 @ 10:06 PM.
 */
public final class TimerModule extends Module {

    public final Value<Mode> mode = new Value<Mode>("Mode", new String[]{"Mode", "M"}, "Mode to use for Timer.", Mode.NORMAL);
    public final Value<Float> speed = new Value<Float>("Speed", new String[]{"Spd"}, "Tick-rate multiplier. [(20tps/second) * (this value)]", 4.0f, 0.1f, 10.0f, 0.1f);

    public final Value<Float> allout = new Value<Float>("AO", new String[]{"AllOut"}, "AO", 1.0f, 1.0f, 50.0f, 0.1f);
    public final Value<Float> offTick = new Value<Float>("OT", new String[]{"OffTick"}, "Tick-rate multiplier. [(20tps/second) * (this value)]", 100.0f, 1.0f, 200.0f, 0.1f);
    public final Value<Float> onTick = new Value<Float>("NT", new String[]{"OnTick"}, "Tick-rate multiplier. [(20tps/second) * (this value)]", 20.0f, 1.0f, 200.0f, 0.1f);

    private enum Mode {
        NORMAL, WIGGLY, IamSpeed
    }

    private long nextHitTime = 0;
    private Timer iamspeedTimer = new Timer();
    private int iamspeedInt = 0;
    public TimerModule() {
        super("Timer", new String[] {"Time", "Tmr"}, "Speeds up the client tick rate", "NONE", -1, ModuleType.WORLD);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        iamspeedTimer.reset();
        nextHitTime = System.currentTimeMillis() + 500;
    }

    @Override
    public void onDisable() {
        mc.timer.tickLength = 50.0f;
        super.onDisable();
    }

    @Override
    public String getMetaData() {
        return "" + this.speed.getValue();
    }

    @Listener
    public void onUpdate(EventPlayerUpdate event) {
        if(event.getStage() == EventStageable.EventStage.PRE) {
            if (this.mode.getValue() == Mode.NORMAL) {
                ((MixinTimerInterface) ((MixinMinecraftInterface) mc).getTimer()).setTickLength(50.0f / speed.getValue());
            } else if (this.mode.getValue() == Mode.WIGGLY){
                if (System.currentTimeMillis() >= nextHitTime + 500) {
                    nextHitTime = nextHitTime + 500 + 5000;
                    ((MixinTimerInterface) ((MixinMinecraftInterface) mc).getTimer()).setTickLength(speed.getValue() - 0.5f);
                } else if (System.currentTimeMillis() >= nextHitTime) {
                    ((MixinTimerInterface) ((MixinMinecraftInterface) mc).getTimer()).setTickLength(50.0f / 6.0f);
                } else {
                    ((MixinTimerInterface) ((MixinMinecraftInterface) mc).getTimer()).setTickLength(50.0f / speed.getValue());
                }
            } else if (this.mode.getValue() == Mode.IamSpeed) {
                iamspeedInt++;
                switch (iamspeedInt % 5) {
                    case 0:
                        ((MixinTimerInterface) ((MixinMinecraftInterface) mc).getTimer()).setTickLength(allout.getValue());
                        break;
                    case 1:
                    case 3:
                        ((MixinTimerInterface) ((MixinMinecraftInterface) mc).getTimer()).setTickLength(offTick.getValue());
                        break;
                    case 2:
                    case 4:
                        ((MixinTimerInterface) ((MixinMinecraftInterface) mc).getTimer()).setTickLength(onTick.getValue());
                        break;
                }
//                if (iamspeedTimer.passed(125)) {
//                    mc.timer.tickLength = 100.0f;
//                    iamspeedTimer.reset();
//                } else if (iamspeedTimer.passed(100)) {
//                    mc.timer.tickLength = 1f;
//                } else {
//                    mc.timer.tickLength = 30.0f;
//                }
            }
        }
    }

    @Listener
    public void receivePacket(EventReceivePacket event) {
        if (event.getStage() == EventStageable.EventStage.PRE) {
            if (this.mode.getValue() == Mode.WIGGLY || this.mode.getValue() == Mode.IamSpeed) {
                if (event.getPacket() instanceof SPacketPlayerPosLook) {
                    final SPacketPlayerPosLook packet = (SPacketPlayerPosLook) event.getPacket();
                    final double dist = MathUtil.getDistance(mc.player.getPositionVector(), packet.getX(), packet.getY(), packet.getZ());
                    if (dist > 2.0 && System.currentTimeMillis() >= nextHitTime + 500) {
                        ((MixinTimerInterface) ((MixinMinecraftInterface) mc).getTimer()).setTickLength(50.0f);
                        if (System.currentTimeMillis() - nextHitTime < 250) {
                            nextHitTime = nextHitTime + 500 + 5000 - 25;
                        } else {
                            nextHitTime = nextHitTime + 500 + 5000 + 25;
                        }
                        iamspeedTimer.setTime(iamspeedTimer.getTime()+100);
                    }
                }
            }
            if (event.getPacket() instanceof SPacketParticles) {
                event.setCanceled(true);
            }
        }
    }
}
