package io.github.rozefound.waterdizzle.listeners;

import io.github.rozefound.waterdizzle.WaterDizzle;
import io.papermc.paper.event.entity.EntityMoveEvent;
import java.util.HashMap;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class WaterDizzleListener implements Listener {

    private final WaterDizzle plugin;
    private final HashMap<UUID, Location> lastKnown = new HashMap<>();

    public WaterDizzleListener(WaterDizzle plugin) {
        this.plugin = plugin;

        new BukkitRunnable() {
            @Override
            public void run() {
                for (var entry : lastKnown.entrySet()) {
                    Item item = (Item) Bukkit.getEntity(entry.getKey());
                    if (item == null) continue;

                    // For performance reasons we only track items in zones
                    // As soon as it leaves the zone, it's none of our concern

                    Location current = item.getLocation();
                    if (!current.equals(entry.getValue())) {
                        if (!anyZoneContainsEntity(item)) {
                            lastKnown.remove(entry.getKey());
                        }
                        entry.setValue(current);
                        onItemMove(item);
                    }
                }
            }
        }
            .runTaskTimer(plugin, 0L, 1L);
    }

    private boolean anyZoneContainsEntity(Entity entity) {
        for (var zone : plugin.getZoneManager().getZones()) {
            if (zone.getBounds().containsEntity(entity)) {
                return true;
            }
        }
        return false;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onItemSpawn(ItemSpawnEvent event) {
        Item item = event.getEntity();
        if (anyZoneContainsEntity(item)) {
            lastKnown.put(item.getUniqueId(), item.getLocation());
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onItemDespawn(ItemDespawnEvent event) {
        lastKnown.remove(event.getEntity().getUniqueId());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        for (var zone : plugin.getZoneManager().getZones()) {
            zone.onPlayerJoin(event);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void OnPlayerMoveEvent(PlayerMoveEvent event) {
        for (var zone : plugin.getZoneManager().getZones()) {
            zone.onEntityMove(event.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityMoveEvent(EntityMoveEvent event) {
        for (var zone : plugin.getZoneManager().getZones()) {
            zone.onEntityMove(event.getEntity());
        }
    }

    public void onItemMove(Item item) {
        for (var zone : plugin.getZoneManager().getZones()) {
            zone.onEntityMove(item);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerDeath(PlayerDeathEvent event) {
        for (var zone : plugin.getZoneManager().getZones()) {
            zone.onPlayerDeath(event);
        }
    }
}
