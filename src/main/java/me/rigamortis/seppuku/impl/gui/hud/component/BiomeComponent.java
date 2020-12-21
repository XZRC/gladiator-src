package me.rigamortis.seppuku.impl.gui.hud.component;

import me.rigamortis.seppuku.Seppuku;
import me.rigamortis.seppuku.api.gui.hud.component.DraggableHudComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;

/**
 * Author Seth
 * 8/7/2019 @ 12:48 PM.
 */
public final class BiomeComponent extends DraggableHudComponent {

    public BiomeComponent() {
        super("Biome");
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);

        final Minecraft mc = Minecraft.getMinecraft();
        final BlockPos pos = mc.player.getPosition();
        final Chunk chunk = mc.world.getChunk(pos);
        final Biome biome = chunk.getBiome(pos, mc.world.getBiomeProvider());

        this.setW(Seppuku.INSTANCE.getFontManager().getStringWidth(biome.getBiomeName()));
        this.setH(Seppuku.INSTANCE.getFontManager().getHeight());

        //RenderUtil.drawRect(this.getX(), this.getY(), this.getX() + this.getW(), this.getY() + this.getH(), 0x90222222);
        Seppuku.INSTANCE.getFontManager().drawString(biome.getBiomeName(), this.getX(), this.getY(), -1, true, false);
    }

}