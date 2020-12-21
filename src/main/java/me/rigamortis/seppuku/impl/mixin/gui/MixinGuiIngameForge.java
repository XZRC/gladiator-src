package me.rigamortis.seppuku.impl.mixin.gui;

import me.rigamortis.seppuku.Seppuku;
import me.rigamortis.seppuku.api.event.gui.EventRenderHelmet;
import me.rigamortis.seppuku.api.event.gui.EventRenderPortal;
import me.rigamortis.seppuku.api.event.gui.EventRenderPotions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.GuiIngameForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GuiIngameForge.class, remap = false)
public abstract class MixinGuiIngameForge extends GuiIngame {
    public MixinGuiIngameForge(Minecraft mcIn) {
        super(mcIn);
    }

    @Inject(method = "renderPortal", cancellable = true, at = @At("HEAD"))
    private void onRenderPortal(ScaledResolution res, float partialTicks, CallbackInfo ci) {
        final EventRenderPortal event = new EventRenderPortal();
        Seppuku.dispatchEvent(event);
        if (event.isCanceled()) ci.cancel();
    }

    @Inject(method = "renderPotionIcons", cancellable = true, at = @At("HEAD"))
    private void onRenderPotionIcons(ScaledResolution resolution, CallbackInfo ci) {
        final EventRenderPotions event = new EventRenderPotions();
        Seppuku.dispatchEvent(event);
        if (event.isCanceled()) ci.cancel();
    }

    @Inject(method = "renderHelmet", cancellable = true, at = @At("HEAD"))
    private void onRenderHelmet(ScaledResolution res, float partialTicks, CallbackInfo ci) {
        final EventRenderHelmet event = new EventRenderHelmet();
        Seppuku.dispatchEvent(event);
        if (event.isCanceled()) ci.cancel();
    }

}
