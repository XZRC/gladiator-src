package me.rigamortis.seppuku.impl.module.render;

import me.rigamortis.seppuku.Seppuku;
import me.rigamortis.seppuku.api.event.EventStageable;
import me.rigamortis.seppuku.api.event.player.EventPlayerUpdate;
import me.rigamortis.seppuku.api.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityTameable;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

public class MobOwnerModule extends Module {

    public MobOwnerModule() {
        super("MobOwner", new String[]{"MobOwners"}, "Shows the owner of a mob.", "NONE", -1, ModuleType.RENDER);
    }

    @Listener
    public void onUpdate(EventPlayerUpdate event) {
        if (event.getStage() == EventStageable.EventStage.PRE) {
            for (Entity entity : Minecraft.getMinecraft().world.getLoadedEntityList()) {
                if (!entity.getCustomNameTag().contains("Owner:")) {
                    if (entity instanceof EntityTameable) {
                        final EntityTameable entityTameable = (EntityTameable) entity;
                        if (entityTameable.isTamed() && entityTameable.getOwner() != null) {
                            entityTameable.setAlwaysRenderNameTag(true);
                            entityTameable.setCustomNameTag(entityTameable.getCustomNameTag() + "[Owner: " + entityTameable.getOwner().getDisplayName().getFormattedText() + "]");
                        }
                    }
                    if (entity instanceof AbstractHorse) {
                        final AbstractHorse horse = (AbstractHorse) entity;
                        if (horse.isTame() && horse.getOwnerUniqueId() != null) {
                            horse.setAlwaysRenderNameTag(true);
                            horse.setCustomNameTag(horse.getCustomNameTag() + "[Owner: " + Seppuku.INSTANCE.getApiManager().resolveName(String.valueOf(horse.getOwnerUniqueId())) + "]");
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        for (final Entity entity : Minecraft.getMinecraft().world.loadedEntityList) {
            if (entity instanceof EntityTameable || entity instanceof AbstractHorse) {
                try {
                    entity.setAlwaysRenderNameTag(false);
                } catch (Exception ignored) {}
            }
        }
    }
}
