package me.rigamortis.seppuku.impl.mixin.gui;

import me.rigamortis.seppuku.Seppuku;
import me.rigamortis.seppuku.api.event.gui.EventRenderTooltip;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiScreen.class)
public abstract class MixinGuiScreen extends Gui {
    @Inject(method = "renderToolTip", at = @At("HEAD"), cancellable = true)
    private void onRenderTooltip(ItemStack stack, int x, int y, CallbackInfo ci) {
        final EventRenderTooltip event = new EventRenderTooltip(stack, x, y);
        Seppuku.dispatchEvent(event);
        if (event.isCanceled()) ci.cancel();
    }
}
