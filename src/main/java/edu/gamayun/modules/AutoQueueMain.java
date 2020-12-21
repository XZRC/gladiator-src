package edu.gamayun.modules;

import me.rigamortis.seppuku.api.event.player.EventPlayerUpdate;
import me.rigamortis.seppuku.api.module.Module;
import me.rigamortis.seppuku.api.util.Timer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3i;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

public class AutoQueueMain extends Module {
    private final Timer timer = new Timer();

    public AutoQueueMain() {
        super("AutoQueueMain", new String[]{"AutoQueueMain", "Auto_Queue_Main", "AutoQueue", "QueueMain", "QMain"}, "Sends the chat message '/queue main' automatically.", "NONE", -1, ModuleType.MISC);
    }

    @Listener
    public void onPlayerUpdate(EventPlayerUpdate event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (this.shouldSendMessage(mc.player)) {
            mc.player.sendChatMessage("/queue main");
            this.timer.reset();
        }
    }

    private boolean shouldSendMessage(EntityPlayer player) {
        boolean inEnd = player.dimension == 1;
        boolean atCorrectPosition = player.getPosition().equals(new Vec3i(0, 240, 0));
        boolean timePassed = this.timer.passed(9000.0D);
        return inEnd && atCorrectPosition && timePassed;
    }
}
