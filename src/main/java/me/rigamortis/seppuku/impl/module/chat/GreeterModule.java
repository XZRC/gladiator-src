package me.rigamortis.seppuku.impl.module.chat;

import me.rigamortis.seppuku.Seppuku;
import me.rigamortis.seppuku.api.event.player.EventPlayerJoin;
import me.rigamortis.seppuku.api.event.player.EventPlayerLeave;
import me.rigamortis.seppuku.api.module.Module;
import me.rigamortis.seppuku.api.value.Value;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

/**
 * Author Seth
 * 4/25/2019 @ 2:12 PM.
 */
public final class GreeterModule extends Module {

    public final Value<Mode> mode = new Value<Mode>("Mode", new String[]{"Mode", "M"}, "Change between greeter modes. Client mode will only appear for you, Server mode will broadcast the greeting message for everyone.", Mode.CLIENT);

    private enum Mode {
        CLIENT, SERVER
    }

    public GreeterModule() {
        super("Greeter", new String[]{"Greet"}, "Automatically announces when a player joins and leaves", "NONE", -1, ModuleType.CHAT);
    }

    @Override
    public String getMetaData() {
        return this.mode.getValue().name();
    }

    @Listener
    public void onPlayerJoin(EventPlayerJoin event) {
        switch (this.mode.getValue()) {
            case CLIENT:
                Seppuku.INSTANCE.logChat(event.getName() + " has joined the game");
                break;
            case SERVER:
                Seppuku.INSTANCE.getChatManager().add(event.getName() + " has joined the game");
                break;
        }
    }

    @Listener
    public void onPlayerLeave(EventPlayerLeave event) {
        switch (this.mode.getValue()) {
            case CLIENT:
                Seppuku.INSTANCE.logChat(event.getName() + " has left the game");
                break;
            case SERVER:
                Seppuku.INSTANCE.getChatManager().add(event.getName() + " has left the game");
                break;
        }
    }
}
