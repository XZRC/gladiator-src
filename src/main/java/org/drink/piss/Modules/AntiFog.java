package org.drink.piss.Modules;

import me.rigamortis.seppuku.api.module.Module;
import org.drink.piss.Events.EventSetupFog;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

public class AntiFog extends Module {

    public AntiFog() {
        super("AntiFog", new String[]{"af", "nofog"}, "Stops fog from rendering", "NONE", -1, ModuleType.RENDER);
    }

    @Listener
    public void on(EventSetupFog event) {
        event.setCanceled(true);
    }
}
