package io.github.famous1622.gladiator.modules;

import me.rigamortis.seppuku.api.event.EventStageable;
import me.rigamortis.seppuku.api.event.player.EventPlayerUpdate;
import me.rigamortis.seppuku.api.event.player.EventUpdateInput;
import me.rigamortis.seppuku.api.event.player.EventUpdateWalkingPlayer;
import me.rigamortis.seppuku.api.module.Module;
import me.rigamortis.seppuku.api.value.Value;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

public final class BoatStepModule extends Module {

    public final Value<Mode> mode = new Value<Mode>("Mode", new String[]{"Mode", "M"}, "The step block-height mode to use.", Mode.ONE);

    private enum Mode {
        ONE, TWO
    }


    public BoatStepModule() {
        super("BoatStep", new String[]{"stp"}, "Allows the player to step/teleport up blocks when horizontally colliding with one", "NONE", -1, ModuleType.MOVEMENT);
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Listener
    public void onWalkingUpdate(EventPlayerUpdate event) {
        if (event.getStage() == EventStageable.EventStage.PRE) {
            final Minecraft mc = Minecraft.getMinecraft();

            switch (this.mode.getValue()) {
                case ONE:
                case TWO:
                    break;
            }

            Entity ridingEntity = mc.player.getRidingEntity();
            if (ridingEntity == null) {
                return;
            }

            if (ridingEntity.onGround && !ridingEntity.isInsideOfMaterial(Material.WATER) && !ridingEntity.isInsideOfMaterial(Material.LAVA) && !ridingEntity.isInWeb && ridingEntity.collidedVertically && !mc.gameSettings.keyBindJump.pressed && ridingEntity.collidedHorizontally) {
                ridingEntity.setPosition(ridingEntity.posX, ridingEntity.posY + (this.mode.getValue() == Mode.ONE ? 1.5 : 2.5), ridingEntity.posZ);
            }
        }
    }

}

