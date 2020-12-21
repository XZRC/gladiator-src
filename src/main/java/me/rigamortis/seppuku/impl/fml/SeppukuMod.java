package me.rigamortis.seppuku.impl.fml;

import io.github.famous1622.gladiator.KeyBinds;
import me.rigamortis.seppuku.Seppuku;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 * Author Seth
 * 4/4/2019 @ 10:48 PM.
 */
@Mod(modid = "gladiatormod", name = "Gladiator", version = SeppukuMod.VERSION, certificateFingerprint = "7979b1d0446af2675fcb5e888851a7f32637fdb9")
public final class SeppukuMod {

    public static final String VERSION = "6.0";

    @Mod.EventHandler
    public void preinit(FMLPreInitializationEvent event) {
        KeyBinds.register();
    }

    /**
     * Our mods entry point
     * @param event
     */
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        //initialize the client
        Seppuku.INSTANCE.init();
    }

}
