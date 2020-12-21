/* package me.rigamortis.seppuku.impl.command;

import me.rigamortis.seppuku.api.command.Command;
import net.minecraft.client.Minecraft;

import org.lwjgl.opengl.GL11;
import scala.util.parsing.combinator.token.StdTokens;

public class TacoCommand extends Command {

    public TacoCommand() {
        super("Taco", new String[]{""}, "Taco", "No arguments are needed for a taco!");
    }

    private boolean enabled;
    private int ticks = 0;

    @Override
    public void exec(String input) {
        if (!this.clamp(input, 1, 1)) {
            this.printUsage();
            return;
        }

        final Minecraft mc = Minecraft.getMinecraft();

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        // TODO: RainbowUI
        // if(WurstClient.INSTANCE.getHax().rainbowUiHack.isActive())
        // {
        // float[] acColor = WurstClient.INSTANCE.getGui().getAcColor();
        // GL11.glColor4f(acColor[0], acColor[1], acColor[2], 1);
        // }else
        GL11.glColor4f(1, 1, 1, 1);

        mc.getTextureManager().bindTexture(tacos[ticks / 8]);
        int x = sr.getScaledWidth() / 2 - 32 + 76;
        int y = sr.getScaledHeight() - 32 - 19;
        int w = 64;
        int h = 32;
        DrawableHelper.blit(x, y, 0, 0, w, h, w, h);

        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_BLEND);



    }
}
*/