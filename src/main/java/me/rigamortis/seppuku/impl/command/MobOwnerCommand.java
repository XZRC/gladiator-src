package me.rigamortis.seppuku.impl.command;

import me.rigamortis.seppuku.Seppuku;
import me.rigamortis.seppuku.api.command.Command;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.util.math.RayTraceResult;

/**
 * Author Seth
 * 5/23/2019 @ 8:14 AM.
 */
public final class MobOwnerCommand extends Command {

    public MobOwnerCommand() {
        super("MobOwner", new String[] {"Owner"}, "Allows you to find the owner of a mob", "Mobowner");
    }

    @Override
    public void exec(String input) {
        if (!this.clamp(input, 1, 1)) {
            this.printUsage();
            return;
        }

        final Minecraft mc = Minecraft.getMinecraft();

        final RayTraceResult ray = mc.objectMouseOver;
        if (ray.entityHit instanceof EntityTameable) {
            if (((EntityTameable) ray.entityHit).isTamed() && ((EntityTameable) ray.entityHit).getOwner() != null) {
                Seppuku.INSTANCE.logChat("Owner: " + ((EntityTameable) ray.entityHit).getOwner().getDisplayName().getFormattedText());
            }
        }
        if (ray.entityHit instanceof AbstractHorse) {
            if (((AbstractHorse) ray.entityHit).isTame() && ((AbstractHorse) ray.entityHit).getOwnerUniqueId() != null) {
                Seppuku.INSTANCE.logChat("Owner: " + Seppuku.INSTANCE.getApiManager().resolveName(String.valueOf(((AbstractHorse) ray.entityHit).getOwnerUniqueId())));
            }
        }
    }
}
