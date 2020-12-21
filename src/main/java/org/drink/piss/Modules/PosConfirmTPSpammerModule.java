package org.drink.piss.Modules;

import me.rigamortis.seppuku.api.event.EventStageable;
import me.rigamortis.seppuku.api.event.player.EventPlayerUpdate;
import me.rigamortis.seppuku.api.module.Module;
import me.rigamortis.seppuku.api.value.Value;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

/**
 * @author cats
 * @since 10 mar 2020
 */
public class PosConfirmTPSpammerModule extends Module {

    public final Value<Float> rate = new Value<>("Rate", new String[]{"r"}, "Rate to send packets at", 1.0f);


    public PosConfirmTPSpammerModule() {
        super("TPSpam", new String[]{}, "Spams setPos and CPacketConfirmTeleport packets", "NONE", -1, ModuleType.EXPLOIT);
    }

    @Listener
    public void onPlayerUpdate(EventPlayerUpdate event) {
        if (event.getStage() == EventStageable.EventStage.PRE) {
            for (int n = 0; n <= this.rate.getValue(); n++) {
                mc.player.setPosition(999, 65, 999);
                mc.getConnection().sendPacket(new CPacketConfirmTeleport(-n));
            }
        }
    }
}
