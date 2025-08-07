package io.github.rozefound.waterdizzle.listeners;

import io.github.rozefound.waterdizzle.WaterDizzle;
import io.github.rozefound.waterdizzle.utils.LanguageManager;
import io.github.rozefound.waterdizzle.utils.SelectionManager;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Listener to handle block selection when players are in selection mode
 */
public class SelectionListener implements Listener {

    private final WaterDizzle plugin;
    private final SelectionManager selectionManager;

    public SelectionListener(
        WaterDizzle plugin,
        SelectionManager selectionManager
    ) {
        this.plugin = plugin;
        this.selectionManager = selectionManager;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (!selectionManager.isInSelectionMode(player)) {
            return;
        }

        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) {
            return;
        }

        event.setCancelled(true);

        Location blockLocation = clickedBlock.getLocation();

        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            handleFirstAnchorSelection(player, blockLocation);
        } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            handleSecondAnchorSelection(player, blockLocation);
        }
    }

    private void handleFirstAnchorSelection(Player player, Location location) {
        Location existingFirst = selectionManager.getFirstAnchor(player);
        if (existingFirst != null && existingFirst.equals(location)) {
            return;
        }

        selectionManager.setFirstAnchor(player, location);

        String message =
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.selection.first-corner-set") +
            " " +
            plugin
                .getLanguageManager()
                .getMessage(
                    "commands.zone.selection.position",
                    LanguageManager.placeholders(
                        "x",
                        String.valueOf(location.getBlockX()),
                        "y",
                        String.valueOf(location.getBlockY()),
                        "z",
                        String.valueOf(location.getBlockZ())
                    )
                );
        player.sendMessage(
            plugin.getLanguageManager().getSuccessMessage(message)
        );

        spawnSelectionParticles(location, Particle.HAPPY_VILLAGER);
        player.playSound(location, Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);

        if (selectionManager.hasCompleteSelection(player)) {
            player.sendMessage(
                plugin
                    .getLanguageManager()
                    .getComponent("commands.zone.selection.selection-complete")
            );
            displaySelectionBounds(player);
        } else {
            player.sendMessage(
                plugin
                    .getLanguageManager()
                    .getInfoMessage(
                        plugin
                            .getLanguageManager()
                            .getMessage(
                                "commands.zone.selection.set-second-corner"
                            )
                    )
            );
        }
    }

    private void handleSecondAnchorSelection(Player player, Location location) {
        Location existingSecond = selectionManager.getSecondAnchor(player);
        if (existingSecond != null && existingSecond.equals(location)) {
            return;
        }

        selectionManager.setSecondAnchor(player, location);

        String message =
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.selection.second-corner-set") +
            " " +
            plugin
                .getLanguageManager()
                .getMessage(
                    "commands.zone.selection.position",
                    LanguageManager.placeholders(
                        "x",
                        String.valueOf(location.getBlockX()),
                        "y",
                        String.valueOf(location.getBlockY()),
                        "z",
                        String.valueOf(location.getBlockZ())
                    )
                );
        player.sendMessage(
            plugin.getLanguageManager().getSuccessMessage(message)
        );

        spawnSelectionParticles(location, Particle.HAPPY_VILLAGER);
        player.playSound(location, Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.5f);

        if (selectionManager.hasCompleteSelection(player)) {
            player.sendMessage(
                plugin
                    .getLanguageManager()
                    .getComponent("commands.zone.selection.selection-complete")
            );
            displaySelectionBounds(player);
        } else if (selectionManager.getFirstAnchor(player) == null) {
            player.sendMessage(
                plugin
                    .getLanguageManager()
                    .getInfoMessage(
                        plugin
                            .getLanguageManager()
                            .getMessage(
                                "commands.zone.selection.set-first-corner"
                            )
                    )
            );
        }
    }

    private void displaySelectionBounds(Player player) {
        Location first = selectionManager.getFirstAnchor(player);
        Location second = selectionManager.getSecondAnchor(player);

        if (
            first == null ||
            second == null ||
            !first.getWorld().equals(second.getWorld())
        ) {
            return;
        }

        int width = Math.abs(first.getBlockX() - second.getBlockX()) + 1;
        int height = Math.abs(first.getBlockY() - second.getBlockY()) + 1;
        int length = Math.abs(first.getBlockZ() - second.getBlockZ()) + 1;
        int volume = width * height * length;

        String dimensionsMsg = plugin
            .getLanguageManager()
            .getMessage(
                "commands.zone.selection.dimensions",
                LanguageManager.placeholders(
                    "width",
                    String.valueOf(width),
                    "height",
                    String.valueOf(height),
                    "length",
                    String.valueOf(length)
                )
            );
        String volumeMsg = plugin
            .getLanguageManager()
            .getMessage(
                "commands.zone.selection.volume",
                "volume",
                String.format("%,d", volume)
            );

        player.sendMessage(
            plugin
                .getLanguageManager()
                .getInfoMessage(dimensionsMsg + " - " + volumeMsg)
        );
    }

    private void spawnSelectionParticles(Location location, Particle particle) {
        Location particleLoc = location.clone().add(0.5, 0.5, 0.5);
        location
            .getWorld()
            .spawnParticle(particle, particleLoc, 10, 0.2, 0.2, 0.2, 0.05);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (selectionManager.isInSelectionMode(player)) {
            selectionManager.handlePlayerQuit(player);
            plugin
                .getLogger()
                .info(
                    "Cleared selection mode for " +
                    player.getName() +
                    " (player quit)"
                );
        }
    }
}
