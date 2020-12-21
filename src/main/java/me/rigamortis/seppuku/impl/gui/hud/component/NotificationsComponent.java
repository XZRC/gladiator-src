package me.rigamortis.seppuku.impl.gui.hud.component;

import me.rigamortis.seppuku.Seppuku;
import me.rigamortis.seppuku.api.gui.hud.component.DraggableHudComponent;
import me.rigamortis.seppuku.api.notification.Notification;
import me.rigamortis.seppuku.api.util.RenderUtil;
import me.rigamortis.seppuku.impl.gui.hud.GuiHudEditor;
import net.minecraft.client.Minecraft;

/**
 * created by noil on 8/17/2019 at 4:39 PM
 */
public final class NotificationsComponent extends DraggableHudComponent {

    public NotificationsComponent() {
        super("Notifications");
        this.setVisible(true);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);

        if (Minecraft.getMinecraft().currentScreen instanceof GuiHudEditor) {
            if (Seppuku.INSTANCE.getNotificationManager().getNotifications().isEmpty()) {
                final String placeholder = "Notification Tray";
                this.setW(Seppuku.INSTANCE.getFontManager().getStringWidth(placeholder));
                this.setH(Seppuku.INSTANCE.getFontManager().getHeight());
                Seppuku.INSTANCE.getFontManager().drawString(placeholder, this.getX(), this.getY(), 0xFFFFFFFF, true, false);
                return;
            }
        }

        int offsetY = 0;
        float maxWidth = 0;

        for (Notification notification : Seppuku.INSTANCE.getNotificationManager().getNotifications()) {

            float offsetX = 0;

            if (this.getAnchorPoint() != null) {
                switch (this.getAnchorPoint().getPoint()) {
                    case TOP_CENTER:
                        offsetX = (this.getW() - Seppuku.INSTANCE.getFontManager().getStringWidth(notification.getText())) / 2;
                        break;
                    case TOP_LEFT:
                    case BOTTOM_LEFT:
                        offsetX = 0;
                        break;
                    case TOP_RIGHT:
                    case BOTTOM_RIGHT:
                        offsetX = this.getW() - Seppuku.INSTANCE.getFontManager().getStringWidth(notification.getText());
                        break;
                }
            }

            notification.setX(this.getX() + offsetX);
            notification.setY(this.getY() + offsetY);
            notification.setWidth(Seppuku.INSTANCE.getFontManager().getStringWidth(notification.getText()) + 4);
            notification.setHeight(Seppuku.INSTANCE.getFontManager().getHeight() + 5);

            RenderUtil.drawRect(notification.getTransitionX(), notification.getTransitionY(), notification.getTransitionX() + notification.getWidth(), notification.getTransitionY() + notification.getHeight(), 0x75101010);
            RenderUtil.drawRect(notification.getTransitionX(), notification.getTransitionY(), notification.getTransitionX() + notification.getWidth(), (notification.getTransitionY() + 1), notification.getType().getColor());
            Seppuku.INSTANCE.getFontManager().drawString(notification.getText(), notification.getTransitionX() + 2.0F, notification.getTransitionY() + 4.0F, 0xFFFFFFFF, true, false);

            final float width = notification.getWidth();
            if (width >= maxWidth) {
                maxWidth = width;
            }

            offsetY += notification.getHeight();
        }

        this.setW(maxWidth);
        this.setH(offsetY);
    }

}
