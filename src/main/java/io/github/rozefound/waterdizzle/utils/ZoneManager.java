package io.github.rozefound.waterdizzle.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.github.rozefound.waterdizzle.WaterDizzle;
import io.github.rozefound.waterdizzle.Zone;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * Manager class for handling zone operations
 */
public class ZoneManager {

    private final WaterDizzle plugin;
    private final Map<String, Zone> zones;
    private final File zonesFile;

    public ZoneManager(WaterDizzle plugin) {
        this.plugin = plugin;
        this.zones = new HashMap<>();
        this.zonesFile = new File(plugin.getDataFolder(), "zones.json");

        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        loadZones();
    }

    public List<Zone> getZones() {
        return new ArrayList<>(zones.values());
    }

    public Zone getZone(String name) {
        return zones.get(name);
    }

    public Set<String> getZoneNames() {
        return zones.keySet();
    }

    public void addZone(Zone zone) {
        zones.put(zone.getName(), zone);
    }

    public Zone removeZone(String name) {
        return zones.remove(name);
    }

    public boolean hasZone(String name) {
        return zones.containsKey(name);
    }

    public void clearZones() {
        zones.clear();
    }

    public int getZoneCount() {
        return zones.size();
    }

    public void saveZones() {
        try {
            JsonArray zonesArray = new JsonArray();

            for (Zone zone : zones.values()) {
                String zoneJson = zone.toJson();
                JsonElement zoneElement = JsonParser.parseString(zoneJson);
                zonesArray.add(zoneElement);
            }

            try (FileWriter writer = new FileWriter(zonesFile)) {
                writer.write(zonesArray.toString());
            }

            plugin
                .getLogger()
                .info("Saved " + zones.size() + " zones to file.");
        } catch (IOException e) {
            plugin
                .getLogger()
                .severe("Failed to save zones: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void loadZones() {
        if (!zonesFile.exists()) {
            plugin
                .getLogger()
                .info("No zones file found, starting with empty zones list.");
            return;
        }

        try {
            JsonElement element = JsonParser.parseReader(
                new FileReader(zonesFile)
            );

            if (!element.isJsonArray()) {
                plugin
                    .getLogger()
                    .warning("Invalid zones file format, expected JSON array.");
                return;
            }

            JsonArray zonesArray = element.getAsJsonArray();
            zones.clear();

            for (JsonElement zoneElement : zonesArray) {
                try {
                    String zoneJson = zoneElement.toString();
                    Zone zone = Zone.fromJson(zoneJson, plugin);
                    zones.put(zone.getName(), zone);
                } catch (Exception e) {
                    plugin
                        .getLogger()
                        .warning("Failed to load a zone: " + e.getMessage());
                }
            }

            plugin
                .getLogger()
                .info("Loaded " + zones.size() + " zones from file.");
        } catch (Exception e) {
            plugin
                .getLogger()
                .severe("Failed to load zones: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public int reloadZones() {
        zones.clear();

        loadZones();

        return zones.size();
    }

    public List<Zone> getZonesAt(Location location) {
        List<Zone> containingZones = new ArrayList<>();
        for (Zone zone : zones.values()) {
            if (zone.getBounds().contains(location)) {
                containingZones.add(zone);
            }
        }
        return containingZones;
    }

    public List<Zone> getEnabledZones() {
        List<Zone> enabledZones = new ArrayList<>();
        for (Zone zone : zones.values()) {
            if (zone.isEnabled()) {
                enabledZones.add(zone);
            }
        }
        return enabledZones;
    }

    public List<Zone> getZonesInWorld(World world) {
        List<Zone> worldZones = new ArrayList<>();
        for (Zone zone : zones.values()) {
            if (zone.getWorld() != null && zone.getWorld().equals(world)) {
                worldZones.add(zone);
            }
        }
        return worldZones;
    }
}
