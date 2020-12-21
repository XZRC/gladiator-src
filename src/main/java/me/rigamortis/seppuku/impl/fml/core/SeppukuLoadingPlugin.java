package me.rigamortis.seppuku.impl.fml.core;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * Author Seth
 * 4/5/2019 @ 1:24 AM.
 * @author cats
 * I fuckin redid this it ain't urs no more riga
 */
@IFMLLoadingPlugin.TransformerExclusions(value = "me.rigamortis.seppuku.impl.fml.core")
@IFMLLoadingPlugin.Name(value = "Gladiator")
@IFMLLoadingPlugin.MCVersion(value = "1.12.2")
public final class SeppukuLoadingPlugin implements IFMLLoadingPlugin {

    public SeppukuLoadingPlugin() {
        MixinBootstrap.init();
        Mixins.addConfiguration("mixins.gladiator.json");
        MixinEnvironment.getDefaultEnvironment().setSide(MixinEnvironment.Side.CLIENT);
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[0];
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Nullable
    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
    }

    @Override
    public String getAccessTransformerClass() {
        return SeppukuAccessTransformer.class.getName();
    }
}
