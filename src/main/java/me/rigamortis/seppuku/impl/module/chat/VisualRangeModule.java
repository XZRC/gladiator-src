package me.rigamortis.seppuku.impl.module.chat;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.rigamortis.seppuku.Seppuku;
import me.rigamortis.seppuku.api.event.world.EventAddEntity;
import me.rigamortis.seppuku.api.event.world.EventRemoveEntity;
import me.rigamortis.seppuku.api.friend.Friend;
import me.rigamortis.seppuku.api.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

/**
 * Author Seth
 * 11/10/2019 @ 4:20 AM.
 */
public final class VisualRangeModule extends Module {
    private enum Mode {
        PUBLIC
    }
    private int prevPlayer = -1;

    public VisualRangeModule() {
        super("VisualRange", new String[]{"VisRange", "VRange", "VR"}, "Notifies you when players enter and leave your visual range", "NONE", -1, ModuleType.CHAT);
    }

    @Listener
    public void onEntityAdded(EventAddEntity event) {
        if (Minecraft.getMinecraft().world != null && !Minecraft.getMinecraft().player.isDead && event.getEntity() instanceof EntityPlayer && !event.getEntity().getName().equalsIgnoreCase(Minecraft.getMinecraft().player.getName())) {
            final Friend friend = Seppuku.INSTANCE.getFriendManager().isFriend(event.getEntity());
            final String msg = (friend != null ? ChatFormatting.DARK_PURPLE : ChatFormatting.RED) + (friend != null ? friend.getAlias() : event.getEntity().getName()) + ChatFormatting.WHITE + " has entered your visual range.";
            Seppuku.INSTANCE.getNotificationManager().addNotification("", msg);
            Seppuku.INSTANCE.logChat(msg);
            if (event.getEntity().getEntityId() == this.prevPlayer) {
                this.prevPlayer = -1;
            }
        }
    }

    @Listener
    public void onEntityRemove(EventRemoveEntity event) {
        if (Minecraft.getMinecraft().world != null && !Minecraft.getMinecraft().player.isDead && event.getEntity() instanceof EntityPlayer && !event.getEntity().getName().equalsIgnoreCase(Minecraft.getMinecraft().player.getName())) {
            if (this.prevPlayer != event.getEntity().getEntityId()) {
                this.prevPlayer = event.getEntity().getEntityId();
                final Friend friend = Seppuku.INSTANCE.getFriendManager().isFriend(event.getEntity());
                final String msg = (friend != null ? ChatFormatting.DARK_PURPLE : ChatFormatting.RED) + (friend != null ? friend.getAlias() : event.getEntity().getName()) + ChatFormatting.WHITE + " has left your visual range.";
                Seppuku.INSTANCE.getNotificationManager().addNotification("", msg);
                Seppuku.INSTANCE.logChat(msg);
            }
        }
    }


}
