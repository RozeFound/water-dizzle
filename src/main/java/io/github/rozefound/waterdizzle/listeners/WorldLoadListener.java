package io.github.rozefound.waterdizzle.listeners;

import io.github.rozefound.waterdizzle.WaterDizzle;
import io.github.rozefound.waterdizzle.Zone;
import io.github.rozefound.waterdizzle.utils.ZoneManager;
import java.util.logging.Level;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

/**
 * Listener to handle world load/unload events and maintain zone world references.
 * This is critical for zones that are loaded from storage before their worlds are available.
 */
public class WorldLoadListener implements Listener {

    private final WaterDizzle plugin;
    private final ZoneManager zoneManager;

    public WorldLoadListener(WaterDizzle plugin) {
        this.plugin = plugin;
        this.zoneManager = plugin.getZoneManager();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWorldLoad(WorldLoadEvent event) {
        World loadedWorld = event.getWorld();
        String worldName = loadedWorld.getName();

        plugin
            .getLogger()
            .log(
                Level.INFO,
                "World '" +
                worldName +
                "' loaded. Checking zones for world reference restoration..."
            );

        int restoredCount = 0;

        for (Zone zone : zoneManager.getZones()) {
            if (zone.getWorld() == null && zone.getAnchors() != null) {
                boolean restored = tryRestoreZoneWorld(zone, loadedWorld);

                if (restored) {
                    restoredCount++;
                    plugin
                        .getLogger()
                        .log(
                            Level.INFO,
                            "Restored world reference for zone '" +
                            zone.getName() +
                            "' to world '" +
                            worldName +
                            "'"
                        );
                }
            }
        }

        if (restoredCount > 0) {
            plugin
                .getLogger()
                .log(
                    Level.INFO,
                    "Restored world references for " +
                    restoredCount +
                    " zone(s) in world '" +
                    worldName +
                    "'"
                );

            zoneManager.saveZones();
        }
    }

    /**
     * Handle world unload events to prevent memory leaks and invalid references
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onWorldUnload(WorldUnloadEvent event) {
        World unloadedWorld = event.getWorld();
        String worldName = unloadedWorld.getName();

        plugin
            .getLogger()
            .log(
                Level.INFO,
                "World '" + worldName + "' is being unloaded. Checking zones..."
            );

        int affectedCount = 0;

        for (Zone zone : zoneManager.getZones()) {
            if (
                zone.getWorld() != null && zone.getWorld().equals(unloadedWorld)
            ) {
                affectedCount++;
                plugin
                    .getLogger()
                    .log(
                        Level.WARNING,
                        "Zone '" +
                        zone.getName() +
                        "' is in unloading world '" +
                        worldName +
                        "'. Zone will be inactive until world is reloaded."
                    );
            }
        }

        if (affectedCount > 0) {
            plugin
                .getLogger()
                .log(
                    Level.WARNING,
                    affectedCount +
                    " zone(s) are affected by unloading world '" +
                    worldName +
                    "'"
                );
        }
    }

    private boolean tryRestoreZoneWorld(Zone zone, World world) {
        if (zone.getAnchors() == null || zone.getAnchors().length < 2) {
            return false;
        }

        boolean restored = zone.ensureWorldLoaded();

        if (!restored) {
            return false;
        }

        return restored;
    }
}
