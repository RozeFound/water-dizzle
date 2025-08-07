package io.github.rozefound.waterdizzle;

import io.github.rozefound.waterdizzle.commands.ZoneCommand;
import io.github.rozefound.waterdizzle.listeners.SelectionListener;
import io.github.rozefound.waterdizzle.listeners.WaterDizzleListener;
import io.github.rozefound.waterdizzle.listeners.WorldLoadListener;
import io.github.rozefound.waterdizzle.utils.LanguageManager;
import io.github.rozefound.waterdizzle.utils.SelectionManager;
import io.github.rozefound.waterdizzle.utils.ZoneManager;
import io.papermc.lib.PaperLib;
import java.io.File;
import org.bukkit.plugin.java.JavaPlugin;

public class WaterDizzle extends JavaPlugin {

    private WaterDizzleListener waterDizzleListener;
    private ZoneManager zoneManager;
    private SelectionManager selectionManager;
    private LanguageManager languageManager;

    @Override
    public void onEnable() {
        PaperLib.suggestPaper(this);

        saveDefaultConfig();

        File langFolder = new File(getDataFolder(), "lang");
        if (!langFolder.exists()) {
            langFolder.mkdirs();
        }

        File englishLangFile = new File(langFolder, "en_us.yml");
        if (!englishLangFile.exists()) {
            saveResource("lang/en_us.yml", false);
        }

        languageManager = new LanguageManager(this);
        zoneManager = new ZoneManager(this);
        selectionManager = new SelectionManager(this);

        waterDizzleListener = new WaterDizzleListener(this);
        getServer()
            .getPluginManager()
            .registerEvents(waterDizzleListener, this);

        WorldLoadListener worldLoadListener = new WorldLoadListener(this);
        getServer().getPluginManager().registerEvents(worldLoadListener, this);

        SelectionListener selectionListener = new SelectionListener(
            this,
            selectionManager
        );
        getServer().getPluginManager().registerEvents(selectionListener, this);

        ZoneCommand zoneCommand = new ZoneCommand(this, waterDizzleListener);
        getCommand("zone").setExecutor(zoneCommand);
        getCommand("zone").setTabCompleter(zoneCommand);

        getLogger().info(languageManager.getMessage("general.plugin-enabled"));
        getLogger().info(
            languageManager.getMessage(
                "general.plugin-loaded-zones",
                "count",
                String.valueOf(zoneManager.getZoneCount())
            )
        );
    }

    @Override
    public void onDisable() {
        if (zoneManager != null) {
            zoneManager.saveZones();
        }

        if (selectionManager != null) {
            selectionManager.stopVisualization();
        }

        getLogger().info(languageManager.getMessage("general.plugin-disabled"));
    }

    public ZoneManager getZoneManager() {
        return zoneManager;
    }

    public SelectionManager getSelectionManager() {
        return selectionManager;
    }

    public LanguageManager getLanguageManager() {
        return languageManager;
    }

    public void reload() {
        reloadConfig();
        languageManager.reload();
        zoneManager.loadZones();
        getLogger().info(languageManager.getMessage("general.config-reloaded"));
    }
}
