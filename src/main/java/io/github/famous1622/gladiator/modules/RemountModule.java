package io.github.famous1622.gladiator.modules;

import me.rigamortis.seppuku.Seppuku;
import me.rigamortis.seppuku.api.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityDonkey;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;

public class RemountModule extends  Module{
    public RemountModule() {
        super("Remount", new String[]{}, "BetterThanFamousLol", "NONE", -1, ModuleType.PLAYER);
    }

    private Entity mount = null;

    @Override
    public void onEnable() {
        super.onEnable();
        runThread();
    }

    @Override
    public void onDisable() {
        mount = null;
    }

    private void runThread(){
        (new Thread(() -> {
            try{
                if(findEntity() != null && !mc.player.isRiding()){
                    mc.player.setPositionAndRotation(mc.player.getPosition().getX(), mc.player.getPosition().getY(), mc.player.getPosition().getZ(), findEntity().rotationYaw, findEntity().rotationPitch);
                    Thread.sleep(50);
                    mc.getConnection().sendPacket(new CPacketPlayer.PositionRotation(
                            mc.player.getPosition().getX(),
                            mc.player.getPosition().getY(),
                            mc.player.getPosition().getZ(),
                            findEntity().rotationYaw,
                            findEntity().rotationPitch,
                            mc.player.onGround
                    ));
                    Thread.sleep(125);
                    Minecraft.getMinecraft().player.connection.sendPacket(new CPacketUseEntity(findEntity(), EnumHand.MAIN_HAND));
                    toggle();
                }else{
                    if(mc.player.isRiding()){
                        Seppuku.INSTANCE.logChat("You are currently riding an entity.");
                    } else {
                        Seppuku.INSTANCE.logChat("No entity nearby!");
                    }
                    toggle();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        })
        ).start();
    }

    private Entity findEntity() {
        Entity ent = null;

        final Minecraft mc = Minecraft.getMinecraft();

        float maxDist = 4f;

        for (Entity e : mc.world.loadedEntityList) {
            if (e != null) {
                float currentDist = mc.player.getDistance(e);
                if(e instanceof EntityDonkey || e instanceof EntityHorse || e instanceof EntityPig){
                    if (currentDist <= maxDist) {
                        maxDist = currentDist;
                        ent = e;
                    }
                }
            }
        }

        return ent;
    }
}
