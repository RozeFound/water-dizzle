package io.github.rozefound.waterdizzle.listeners;

import io.github.rozefound.waterdizzle.WaterDizzle;
import io.github.rozefound.waterdizzle.utils.ZoneUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class WaterDizzleListener implements Listener {

    private final WaterDizzle plugin;

    public WaterDizzleListener(WaterDizzle plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        boolean isInZone = ZoneUtils.isPlayerInZone(
            player,
            plugin.getDamageZone()
        );

        if (isInZone) plugin.addPlayerToDamageZone(player);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onMoveEvent(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        boolean isInZone = ZoneUtils.isPlayerInZone(
            player,
            plugin.getDamageZone()
        );
        boolean wasInZone = plugin.isPlayerInDamageZone(player);

        if (isInZone && !wasInZone) plugin.addPlayerToDamageZone(player);
        else if (!isInZone && wasInZone) plugin.removePlayerFromDamageZone(
            player
        );
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (plugin.isPlayerGotDamagedByWater(event.getPlayer())) {
            event.deathMessage(
                Component.text("You stayed in the water for too long!")
            );
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.removePlayerFromDamageZone(event.getPlayer());
    }
}
