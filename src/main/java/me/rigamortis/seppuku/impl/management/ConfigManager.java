package me.rigamortis.seppuku.impl.management;

import me.rigamortis.seppuku.Seppuku;
import me.rigamortis.seppuku.api.config.Configurable;
import me.rigamortis.seppuku.api.gui.hud.component.DraggableHudComponent;
import me.rigamortis.seppuku.impl.config.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author noil
 */
public final class ConfigManager {

    public static final String CONFIG_PATH = "Seppuku/Config/";
    private File configDir;
    private File moduleConfigDir;
    private File hudComponentConfigDir;
    private boolean firstLaunch;
    private List<Configurable> configurableList = new ArrayList<>();
    private Thread saverThread;
    private BlockingQueue<SaverCommands> saverQueue = new LinkedBlockingQueue<>();

    public ConfigManager() {
        this.generateDirectories();
    }

    private void generateDirectories() {
        this.configDir = new File(CONFIG_PATH);
        if (!this.configDir.exists()) {
            this.firstLaunch = true;
            this.configDir.mkdirs();
        }

        this.moduleConfigDir = new File(CONFIG_PATH + "Modules" + "/");
        if (!this.moduleConfigDir.exists()) {
            this.moduleConfigDir.mkdirs();
        }

        this.hudComponentConfigDir = new File(CONFIG_PATH + "HudComponents" + "/");
        if (!this.hudComponentConfigDir.exists()) {
            this.hudComponentConfigDir.mkdirs();
        }
    }

    public void init() {
        Seppuku.INSTANCE.getModuleManager().getModuleList().forEach(module -> {
            this.configurableList.add(new ModuleConfig(this.moduleConfigDir, module));
        });

        Seppuku.INSTANCE.getHudManager().getComponentList().stream().filter(hudComponent -> hudComponent instanceof DraggableHudComponent).forEach(hudComponent -> {
            this.configurableList.add(new HudConfig(this.hudComponentConfigDir, (DraggableHudComponent) hudComponent));
        });

        this.configurableList.add(new FriendConfig(configDir));
        this.configurableList.add(new XrayConfig(configDir));
        this.configurableList.add(new MacroConfig(configDir));
        this.configurableList.add(new WaypointsConfig(configDir));
        this.configurableList.add(new WorldConfig(configDir));
        this.configurableList.add(new IgnoreConfig(configDir));
        this.configurableList.add(new AutoIgnoreConfig(configDir));

        this.saverThread = new Thread(() -> {
            while (true) {
                try {
                    SaverCommands take = saverQueue.take();
                    if (take.equals(SaverCommands.SAVE)) {
                        for (Configurable cfg : configurableList) {
                            cfg.onSave();
                        }
                        System.out.println("Saved config.");
                    } else {
                        for (Configurable cfg : configurableList) {
                            cfg.onLoad();
                        }
                        System.out.println("Loaded config.");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        this.saverThread.setDaemon(true);
        this.saverThread.setName("Gladiator Saver Thread");
        this.saverThread.start();

        if (this.firstLaunch) {
            this.saveAll();
        } else {
            this.loadAll();
        }
    }

    public void saveAll() {
        this.saverQueue.offer(SaverCommands.SAVE);
    }

    public void loadAll() {
        this.saverQueue.offer(SaverCommands.LOAD);
    }

    public File getConfigDir() {
        return configDir;
    }

    public File getModuleConfigDir() {
        return moduleConfigDir;
    }

    public File getHudComponentConfigDir() {
        return hudComponentConfigDir;
    }

    public boolean isFirstLaunch() {
        return firstLaunch;
    }

    public void setFirstLaunch(boolean firstLaunch) {
        this.firstLaunch = firstLaunch;
    }

    public void addConfigurable(Configurable configurable) {
        this.configurableList.add(configurable);
    }

    public List<Configurable> getConfigurableList() {
        return configurableList;
    }

    public void setConfigurableList(List<Configurable> configurableList) {
        this.configurableList = configurableList;
    }

    private enum SaverCommands {
        SAVE, LOAD;
    }
}
