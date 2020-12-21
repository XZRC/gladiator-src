package me.rigamortis.seppuku.impl.module.movement;

import me.rigamortis.seppuku.api.event.EventStageable;
import me.rigamortis.seppuku.api.event.player.EventUpdateWalkingPlayer;
import me.rigamortis.seppuku.api.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

public class HighJumpModule extends Module {

    public HighJumpModule() {
        super("HighJump", new String[]{"Jump"}, "jump", "NONE", -1, ModuleType.MOVEMENT);
    }

    @Listener
    public void onWalkingUpdate(EventUpdateWalkingPlayer event) {
        if (event.getStage() == EventStageable.EventStage.PRE) {
            Minecraft.getMinecraft().player.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, 25, 4));
        }
    }

    public void onDisable() {
        super.onDisable();
        Minecraft.getMinecraft().player.removePotionEffect(MobEffects.JUMP_BOOST);
    }
}