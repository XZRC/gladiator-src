package edu.gamayun.modules;

import me.rigamortis.seppuku.api.event.EventStageable;
import me.rigamortis.seppuku.api.event.player.EventPlayerUpdate;
import me.rigamortis.seppuku.api.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.world.GameType;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

public class BetterFakeCreative extends Module {

    public BetterFakeCreative(){
        super("BetterFakeCreative", new String[] { "creative", "fake" }, "NONE", -1, ModuleType.PLAYER);
    }

    Minecraft mc = Minecraft.getMinecraft();

    public void onEnable(){
        super.onEnable();
    }

    public void onDisable(){
        super.onDisable();
        mc.playerController.setGameType(GameType.SURVIVAL);
    }

    @Listener
    public void onUpdate(EventPlayerUpdate event){
        if(event.getStage() == EventStageable.EventStage.PRE){
            mc.playerController.setGameType(GameType.CREATIVE);
        }
    }
}
