package io.github.famous1622.gladiator.modules;

import me.rigamortis.seppuku.api.event.EventStageable;
import me.rigamortis.seppuku.api.event.player.EventPlayerUpdate;
import me.rigamortis.seppuku.api.module.Module;
import me.rigamortis.seppuku.api.value.Value;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.CPacketPlayer;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

import static java.lang.Double.NaN;

public class BoundsPacketSpamModule extends Module {
    public final Value<Integer> delay = new Value<Integer>("EveryNTicks", new String[]{"dly", "d"}, "Delay between Messages.", 2, 1, 20, 1);
    public final Value<Integer> howmany = new Value<Integer>("NEvery", new String[]{"dly", "d"}, "Delay between Messages.", 2, 1, 20, 1);

    public BoundsPacketSpamModule() {
        super("BoundsPacketSpam", new String[]{"bounds"}, "Like chunkborders for maps", "NONE", -1, ModuleType.EXPLOIT);
    }


    @Listener
    public void onUpdate(EventPlayerUpdate eventPlayerUpdate) {
        final Minecraft mc = Minecraft.getMinecraft();
        if (eventPlayerUpdate.getStage() == EventStageable.EventStage.POST) {
            if (mc.player.ticksExisted % delay.getValue() == 0) {
                for (int i = 0; i < howmany.getValue(); i++) {
                    final CPacketPlayer bounds = new CPacketPlayer.Position(mc.player.posX + mc.player.motionX, 0, mc.player.posZ + mc.player.motionZ, mc.player.onGround);
                    //CPacketPlayer bounds = new CPacketPlayer.Position(NaN, NaN, NaN, Boolean.getBoolean("true"));
                    mc.player.connection.sendPacket(bounds);
                }
            }
        }
    }

}
