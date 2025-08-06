package io.github.rozefound.waterdizzle.listeners;

import io.github.rozefound.waterdizzle.WaterDizzle;
import io.papermc.paper.event.entity.EntityMoveEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class WaterDizzleListener implements Listener {

    private final WaterDizzle plugin;

    public WaterDizzleListener(WaterDizzle plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        plugin.zoneManager.onPlayerJoin(event);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void OnPlayerMoveEvent(PlayerMoveEvent event) {
        plugin.zoneManager.onPlayerMove(event);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityMoveEvent(EntityMoveEvent event) {
        plugin.zoneManager.onEntityMove(event);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerDeath(PlayerDeathEvent event) {
        plugin.zoneManager.onPlayerDeath(event);
    }
}
