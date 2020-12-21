package io.github.famous1622.gladiator;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.input.Keyboard;

public class KeyBinds {
    public static KeyBinding dragToXCarry;
    public static KeyBinding dragFromXCarry;

    public static void register() {
        dragFromXCarry = new KeyBinding("key.dragFromXCarry", Keyboard.KEY_NONE, "key.categories.gladiator");
        ClientRegistry.registerKeyBinding(dragFromXCarry);
        dragToXCarry = new KeyBinding("key.dragToXCarry", Keyboard.KEY_NONE, "key.categories.gladiator");
        ClientRegistry.registerKeyBinding(dragToXCarry);
    }
}
