package org.drink.piss.Hud;

import me.rigamortis.seppuku.Seppuku;
import me.rigamortis.seppuku.api.gui.hud.component.DraggableHudComponent;

public class LocalDifficultyComponent extends DraggableHudComponent {

    public LocalDifficultyComponent() {
        super("LocalDifficulty");
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);

        final String difficulty = "Difficulty: " + mc.world.getDifficultyForLocation(mc.player.getPosition()).getAdditionalDifficulty();

        this.setW(Seppuku.INSTANCE.getFontManager().getStringWidth(difficulty));
        this.setH(Seppuku.INSTANCE.getFontManager().getHeight());

        Seppuku.INSTANCE.getFontManager().drawString(difficulty, this.getX(), this.getY(), -1, true, false);
    }
}
