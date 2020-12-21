package io.github.famous1622.gladiator.modules;

import me.rigamortis.seppuku.Seppuku;
import me.rigamortis.seppuku.api.event.world.EventAddEntity;
import me.rigamortis.seppuku.api.module.Module;
import net.minecraft.entity.player.EntityPlayer;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

public class NewPlayerAlertModule extends Module {
    public NewPlayerAlertModule() {
        super("NewPlayerAlert", new String[]{"PlayerAlert"}, "Alerts when new players are loaded", "NONE", -1,  ModuleType.EXPLOIT);
    }

    @Listener
    public void onUpdate(EventAddEntity event) {
        if (event.getEntity() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntity();
            Seppuku.INSTANCE.logfChat(
                    "The player %s spawned at position %d, %d, %d",
                    player.getDisplayNameString(),
                    player.getPosition().getX(),
                    player.getPosition().getY(),
                    player.getPosition().getZ());
        }
    }
}
