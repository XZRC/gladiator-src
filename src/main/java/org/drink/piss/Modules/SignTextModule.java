package org.drink.piss.Modules;

import me.rigamortis.seppuku.Seppuku;
import me.rigamortis.seppuku.api.event.render.EventRender3D;
import me.rigamortis.seppuku.api.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.text.ITextComponent;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

import java.util.ArrayList;

/**
 * Author cats
 * 03/1/2020
 */

public class SignTextModule extends Module {

    private ArrayList<TileEntity> signs = new ArrayList<>();

    public SignTextModule() {
        super("SignText", new String[]{"Signs"}, "Print the text from all signs into chat.", "NONE", -1, ModuleType.MISC);
    }

    @Override
    public void onToggle() {
        super.onToggle();
        signs.clear();
    }

    @Listener
    public void onRenderWorld(EventRender3D event) {
        final Minecraft mc = Minecraft.getMinecraft();
        for (TileEntity tileEntity : mc.world.loadedTileEntityList) {
            if (tileEntity instanceof TileEntitySign && !signs.contains(tileEntity)) {
                final TileEntitySign sign = (TileEntitySign) tileEntity;

                Seppuku.INSTANCE.logChat("Sign " + (signs.size() + 1) + ":");

                for (ITextComponent textComponent : sign.signText) {
                    final String fullText = textComponent.getFormattedText();
                    Seppuku.INSTANCE.logChat(fullText);
                }

                signs.add(tileEntity);
            }
        }
    }
}
