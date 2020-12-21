package me.rigamortis.seppuku.impl.module.misc;

import me.rigamortis.seppuku.Seppuku;
import me.rigamortis.seppuku.api.event.world.EventAddEntity;
import me.rigamortis.seppuku.api.module.Module;
import me.rigamortis.seppuku.api.value.Value;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.projectile.*;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

/**
 * Author cats
 * 21/11/19 @ 4:55 PM.
 */

public class PlayerNotificationsModule extends Module {

    public final Value<Boolean> notification = new Value<Boolean>("Notification", new String[]{"notif", "notify"}, "Send notification using the notification manager.", true);
    public final Value<Boolean> chat = new Value<Boolean>("Chat Message", new String[]{"chat", "chatmessage", "chatmsg", "msg"}, "Send notificaiton in chat.", true);
    public final Value<Boolean> enderpearl = new Value<Boolean>("Enderpearl", new String[]{"EndPearl", "Pearl", "epearl"}, "Send notification when a player throws an Enderpearl.", true);
    public final Value<Boolean> arrows = new Value<Boolean>("Arrow", new String[]{"arrows"}, "Send notification when a player fires an arrow", true);
    public final Value<Boolean> expbottle = new Value<Boolean>("Exp Bottle", new String[]{"Exp", "Experience", "Xp"}, "Send notification when a player throws an EXP Bottle", true);
    public final Value<Boolean> snowball = new Value<Boolean>("Snowball", new String[]{"snow"}, "Send notification when a player throws a snowball.", true);
    public final Value<Boolean> egg = new Value<Boolean>("Egg", new String[]{"eggs"}, "Send notification when a player throws an Egg", true);
    public final Value<Boolean> other = new Value<Boolean>("Other", new String[]{"Other"}, "Send notification when a player throws anything.", true);



    public PlayerNotificationsModule() {
        super("PlayerNotifications", new String[]{"Notifications", "PlayerNotifs", "Notifs", "Notif"}, "Notifies you of some of the actions of other players", "NONE", -1, ModuleType.MISC);
    }

    @Listener
    public void onEntityAdded(EventAddEntity event) {
        String msg = null;
        if (event.getEntity() instanceof EntityEnderPearl && enderpearl.getValue()) {
                msg = ((event.getEntity().getEntityWorld().getClosestPlayerToEntity(event.getEntity(), 3)).getName() +" has thrown an Ender Pearl");

        } else if (event.getEntity() instanceof EntityArrow && arrows.getValue()) {
                msg = ((event.getEntity().getEntityWorld().getClosestPlayerToEntity(event.getEntity(), 3)).getName() + " has shot an Arrow");

        } else if (event.getEntity() instanceof EntitySpectralArrow && arrows.getValue()) {
                msg = ((event.getEntity().getEntityWorld().getClosestPlayerToEntity(event.getEntity(), 3)).getName() + " has shot a Spectral Arrow");

        } else if (event.getEntity() instanceof EntityExpBottle && expbottle.getValue()) {
                msg = ((event.getEntity().getEntityWorld().getClosestPlayerToEntity(event.getEntity(), 3)).getName() +" has thrown an XP Bottle");

        } else if (event.getEntity() instanceof EntitySnowball && snowball.getValue()) {
                msg = ((event.getEntity().getEntityWorld().getClosestPlayerToEntity(event.getEntity(), 3)).getName() +" has thrown a snowball");

        } else if (event.getEntity() instanceof EntityEgg && egg.getValue()) {
                msg = ((event.getEntity().getEntityWorld().getClosestPlayerToEntity(event.getEntity(), 3)).getName() +" has thrown an Egg");

        } else if (event.getEntity() instanceof EntityThrowable && other.getValue()) {
                msg = ((event.getEntity().getEntityWorld().getClosestPlayerToEntity(event.getEntity(), 3)).getName() +" has thrown " + event.getEntity().getName());

        }

        if (msg != null) {
            if (notification.getValue())
                Seppuku.INSTANCE.getNotificationManager().addNotification("", msg);
            if (chat.getValue())
                Seppuku.INSTANCE.logChat(msg);
        }
    }
}
