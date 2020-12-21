package org.drink.piss.Modules;

import me.rigamortis.seppuku.api.event.EventStageable;
import me.rigamortis.seppuku.api.event.network.EventSendPacket;
import me.rigamortis.seppuku.api.module.Module;
import me.rigamortis.seppuku.api.value.Value;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

import java.util.Random;

public class HitEffectsModule extends Module {

    public final Value<Boolean> lightning = new Value<Boolean>("Lightning", new String[]{"Lightning"}, "Spawn lightning effects", true);
    public final Value<Boolean> explosion = new Value<Boolean>("Explosion", new String[]{"Explosion"}, "Boom", false);
    public final Value<Boolean> totem = new Value<Boolean>("Totem", new String[]{"Totem"}, "Totem effects for godpvpers", false);
    public final Value<Boolean> sounds = new Value<Boolean>("Sounds", new String[]{"Sounds"}, "sounds for you!", true);
    protected static final Minecraft mc = Minecraft.getMinecraft();


    public HitEffectsModule() {
        super("HitEffects", new String[]{"Hit"}, "pretty colors", "NONE", -1, ModuleType.RENDER);
    }

    @Listener
    public void sendPacket(EventSendPacket event) {
        if (event.getStage() == EventStageable.EventStage.PRE && Minecraft.getMinecraft().world != null && event.getPacket() instanceof CPacketUseEntity) {
            CPacketUseEntity packet = (CPacketUseEntity)event.getPacket();
            if (packet.getAction().equals(CPacketUseEntity.Action.ATTACK)) {
                Entity entity = packet.getEntityFromWorld(mc.world);
                ResourceLocation explodelocal = new ResourceLocation("minecraft", "entity.generic.explode");
                SoundEvent explosionsound = new SoundEvent(explodelocal);
                ResourceLocation thunderlocal = new ResourceLocation("minecraft", "entity.lightning.thunder");
                SoundEvent thundersound = new SoundEvent(thunderlocal);
                ResourceLocation lightningimpactlocal = new ResourceLocation("minecraft", "entity.lightning.impact");
                SoundEvent lightningimpactsound = new SoundEvent(lightningimpactlocal);
                ResourceLocation totemlocal = new ResourceLocation("minecraft", "item.totem.use");
                SoundEvent totemsound = new SoundEvent(totemlocal);
                if (this.lightning.getValue()) {
                    EntityLightningBolt lightning = new EntityLightningBolt(mc.world, entity.posX, entity.posY + 1.0D, entity.posZ, true);
                    mc.world.spawnEntity(lightning);
                    if (this.sounds.getValue()) {
                        mc.world.playSound(mc.player, new BlockPos(entity.posX, entity.posY, entity.posZ), thundersound, SoundCategory.WEATHER, 1.0F, 1.0F);
                        mc.world.playSound(mc.player, new BlockPos(entity.posX, entity.posY, entity.posZ), lightningimpactsound, SoundCategory.WEATHER, 1.0F, 1.0F);
                    }
                }

                if (this.explosion.getValue()) {
                    mc.world.spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, entity.posX, entity.posY, entity.posZ, 0.0D, 0.0D, 0.0D, new int[0]);
                    mc.world.spawnParticle(EnumParticleTypes.CLOUD, entity.posX, entity.posY, entity.posZ, 0.0D, 0.0D, 0.0D, new int[0]);
                    mc.world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, entity.posX, entity.posY, entity.posZ, 0.0D, 0.0D, 0.0D, new int[0]);
                    if (this.sounds.getValue()) {
                        mc.world.playSound(mc.player, new BlockPos(entity.posX, entity.posY, entity.posZ), explosionsound, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    }
                }

                if (this.totem.getValue()) {
                    Random r = new Random();

                    for(int i = 0; i < 300; ++i) {
                        mc.world.spawnParticle(EnumParticleTypes.TOTEM, entity.posX, entity.posY, entity.posZ, (double)(r.nextInt(2) - 1), (double)(r.nextInt(4) - 2), (double)(r.nextInt(2) - 1), new int[0]);
                    }

                    if (this.sounds.getValue()) {
                        mc.world.playSound(mc.player, new BlockPos(entity.posX, entity.posY, entity.posZ), totemsound, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    }
                }
            }
        }

    }
}
