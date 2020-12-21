package me.rigamortis.seppuku.impl.module.ui;

import com.client.glowclient.clickgui.ClickGUI;
import com.client.glowclient.clickgui.Window;
import me.rigamortis.seppuku.api.module.Module;
import net.minecraft.client.Minecraft;

public class ClickGuiModule extends Module {
    public ClickGuiModule() {
        super("ClickGui", new String[]{"click", "gui"}, "Displays a menu", "RSHIFT", -1, ModuleType.UI);
        this.setHidden(false);
    }

    @Override
    public void onEnable() {
        for (Window window: ClickGUI.getInstance().getWindows()) {
            window.openGui();
        }
        Minecraft.getMinecraft().displayGuiScreen(ClickGUI.getInstance());
        this.toggle();
    }
}