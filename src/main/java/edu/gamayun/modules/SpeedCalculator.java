package edu.gamayun.modules;

import me.rigamortis.seppuku.Seppuku;
import me.rigamortis.seppuku.api.event.render.EventRender2D;
import me.rigamortis.seppuku.api.module.Module;
import me.rigamortis.seppuku.impl.module.render.GoodHudModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

import java.text.DecimalFormat;

public class SpeedCalculator extends Module {
    public SpeedCalculator() { super("SpeedCalculator", new String[] { "SpeedCalc" }, "NONE", -1, Module.ModuleType.RENDER); }


    @Listener
    public void render(EventRender2D event) {
        Minecraft mc = Minecraft.getMinecraft();

        if (mc.gameSettings.showDebugInfo) {
            return;
        }

        GoodHudModule hud = (GoodHudModule)Seppuku.INSTANCE.getModuleManager().find(GoodHudModule.class);

        if (hud != null && hud.isEnabled()) {
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();

            double deltaX = (Minecraft.getMinecraft()).player.posX - (Minecraft.getMinecraft()).player.prevPosX;
            double deltaZ = (Minecraft.getMinecraft()).player.posZ - (Minecraft.getMinecraft()).player.prevPosZ;
            float tickRate = (Minecraft.getMinecraft()).timer.tickLength / 1000.0F;

            double bps = (MathHelper.sqrt(deltaX * deltaX + deltaZ * deltaZ) / tickRate);
            DecimalFormat format = new DecimalFormat("###.##");
            mc.fontRenderer.drawStringWithShadow(format.format(bps) + " m/s", 2.0F, (2 + (hud.watermark.getValue() ? 20 : 10)), -1);

            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }
}
