package me.rigamortis.seppuku.impl.mixin.game;

import me.rigamortis.seppuku.Seppuku;
import me.rigamortis.seppuku.api.event.EventStageable;
import me.rigamortis.seppuku.api.event.minecraft.EventDisplayGui;
import me.rigamortis.seppuku.api.event.minecraft.EventKeyPress;
import me.rigamortis.seppuku.api.event.minecraft.EventRunTick;
import me.rigamortis.seppuku.api.event.minecraft.EventUpdateFramebufferSize;
import me.rigamortis.seppuku.api.event.world.EventLoadWorld;
import me.rigamortis.seppuku.impl.duck.MixinMinecraftInterface;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.Timer;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

/**
 * @author cats
 */
@Mixin(Minecraft.class)
public abstract class MixinMinecraft implements MixinMinecraftInterface {
    @Accessor(value = "timer")
    public abstract Timer getTimer();

    @Inject(method = "updateFramebufferSize", at = @At("HEAD"))
    private void onUpdateFramebufferSize(CallbackInfo ci) {
        Seppuku.dispatchEvent(new EventUpdateFramebufferSize());
    }

    @Inject(method = "runTick", at = @At("HEAD"))
    private void onRunTickPre(CallbackInfo ci) {
        Seppuku.dispatchEvent(new EventRunTick(EventStageable.EventStage.PRE));
    }

    @Inject(method = "runTick", at = @At("RETURN"))
    private void onRunTickPost(CallbackInfo ci) {
        Seppuku.dispatchEvent(new EventRunTick(EventStageable.EventStage.POST));
    }

    @Inject(method = "runTickKeyboard", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;currentScreen:Lnet/minecraft/client/gui/GuiScreen;", ordinal = 0), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void onRunTickKeyboard(CallbackInfo ci, int i) {
        if (Keyboard.getEventKeyState()) {
            Seppuku.dispatchEvent(new EventKeyPress(i));
        }
    }

    @Inject(method = "displayGuiScreen", at = @At("HEAD"), cancellable = true)
    private void onDisplayGuiScreen(GuiScreen guiScreenIn, CallbackInfo ci) {
        final EventDisplayGui event = new EventDisplayGui(guiScreenIn);
        Seppuku.dispatchEvent(event);
        if (event.isCanceled()) ci.cancel();
    }

    @Inject(method = "loadWorld(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V", cancellable = true, at = @At("HEAD"))
    private void onLoadWorld(WorldClient worldClientIn, String loadingMessage, CallbackInfo ci) {
        final EventLoadWorld event = new EventLoadWorld(worldClientIn);
        Seppuku.dispatchEvent(event);
        if (event.isCanceled()) ci.cancel();
    }
}
