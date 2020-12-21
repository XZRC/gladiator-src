package io.github.famous1622.gladiator.modules;

import me.rigamortis.seppuku.Seppuku;
import me.rigamortis.seppuku.api.event.EventStageable;
import me.rigamortis.seppuku.api.event.network.EventReceivePacket;
import me.rigamortis.seppuku.api.event.player.EventUpdateWalkingPlayer;
import me.rigamortis.seppuku.api.module.Module;
import me.rigamortis.seppuku.api.util.MathUtil;
import me.rigamortis.seppuku.api.value.Value;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketEntityMetadata;
import net.minecraft.util.EnumHand;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Author Seth
 * 4/30/2019 @ 4:04 PM.
 * Updated by Memeszz
 */
public final class WolfAuraModule extends Module {

    public final Value<Float> range = new Value<>("Range", new String[]{"Dist"}, "The minimum range to attack.", 4.5f, 0.0f, 5.0f, 0.1f);
    public final Value<Boolean> coolDown = new Value("CoolDown", new String[]{"CoolD"}, "Delay your hits to gain damage.", true);


    private Set<EntityWolf> alreadyTamed = new HashSet<>();
    private Set<EntityWolf> alreadyUnsat = new HashSet<>();

    public WolfAuraModule() {
        super("WolfAura", new String[]{"Aura"}, "Automatically tames and unsits wolves", "NONE", -1, ModuleType.COMBAT);
    }

    @Listener
    public void onWalkingUpdate(EventUpdateWalkingPlayer event) {
        if (event.getStage() == EventStageable.EventStage.POST) {
            final Minecraft mc = Minecraft.getMinecraft();

            if (mc.player.ticksExisted % 20 == 0) {
                alreadyUnsat.clear();
                alreadyTamed.clear();
            }

            final EntityWolf tamingTarget = findTamingTarget();
            final EntityWolf unsittingTarget = findUnsittingTarget();

            final ItemStack stack = mc.player.getHeldItem(EnumHand.OFF_HAND);
            Item item = mc.player.getHeldItemMainhand().getItem();

            if (tamingTarget != null && stack.getItem() == Items.BONE) {
                final float[] angle = MathUtil.calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), tamingTarget.getPositionEyes(mc.getRenderPartialTicks()));
                Seppuku.INSTANCE.getRotationManager().setPlayerRotations(angle[0], angle[1]);
                mc.player.connection.sendPacket(new CPacketUseEntity(tamingTarget, EnumHand.OFF_HAND));
                alreadyTamed.add(tamingTarget);
            } else if (unsittingTarget != null) {
                final float[] angle = MathUtil.calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), unsittingTarget.getPositionEyes(mc.getRenderPartialTicks()));
                Seppuku.INSTANCE.getRotationManager().setPlayerRotations(angle[0], angle[1]);
                boolean useBoneHandToTame = (item instanceof ItemFood) && ((ItemFood)item).isWolfsFavoriteMeat();
                mc.player.connection.sendPacket(new CPacketUseEntity(unsittingTarget, useBoneHandToTame ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND));
                alreadyUnsat.add(unsittingTarget);
            }
        }
    }

    private EntityWolf findUnsittingTarget() {
        final Minecraft mc = Minecraft.getMinecraft();

        float maxDist = this.range.getValue();

        for (Entity e : mc.world.loadedEntityList) {
            if (mc.player.getDistance(e) < maxDist && e instanceof EntityWolf) {
                EntityWolf wolf = (EntityWolf) e;
                if (wolf.isTamed() && !alreadyUnsat.contains(wolf) && wolf.isSitting() && Objects.equals(wolf.getOwnerId(), mc.player.getUniqueID())) {
                    return wolf;
                }
            }
        }
        return null;
    }


    private EntityWolf findTamingTarget() {
        final Minecraft mc = Minecraft.getMinecraft();

        float maxDist = this.range.getValue();

        for (Entity e : mc.world.loadedEntityList) {
            if (mc.player.getDistance(e) < maxDist && e instanceof EntityWolf) {
                EntityWolf wolf = (EntityWolf) e;
                if (!wolf.isTamed() && !alreadyTamed.contains(wolf)) {
                    return wolf;
                }
            }
        }
        return null;
    }

    @Listener
    public void onPacket(EventReceivePacket event) {
        if (event.getStage()== EventStageable.EventStage.POST){
            if (event.getPacket() instanceof SPacketEntityMetadata) {
                SPacketEntityMetadata packet = (SPacketEntityMetadata) event.getPacket();
                Entity entityByID = mc.world.getEntityByID(packet.getEntityId());
                if (entityByID instanceof EntityWolf) {
                    alreadyUnsat.remove(entityByID);
                    alreadyTamed.remove(entityByID);
                }
            }
        }
    }
}

