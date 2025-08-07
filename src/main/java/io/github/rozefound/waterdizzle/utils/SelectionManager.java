package io.github.rozefound.waterdizzle.utils;

import io.github.rozefound.waterdizzle.WaterDizzle;
import io.github.rozefound.waterdizzle.Zone;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class SelectionManager {

    private final WaterDizzle plugin;
    private final Map<UUID, SelectionData> playerSelections;
    private BukkitRunnable visualizationTask;

    // Particle settings
    private static final Color ANCHOR_COLOR = Color.LIME;
    private static final Color SINGLE_ANCHOR_COLOR = Color.YELLOW;
    private static final float PARTICLE_SIZE = 1.0f;
    private static final long VISUALIZATION_INTERVAL = 5L;

    public SelectionManager(WaterDizzle plugin) {
        this.plugin = plugin;
        this.playerSelections = new HashMap<>();
        startVisualizationTask();
    }

    private void startVisualizationTask() {
        visualizationTask = new BukkitRunnable() {
            @Override
            public void run() {
                visualizeAllSelections();
            }
        };
        visualizationTask.runTaskTimer(plugin, 0L, VISUALIZATION_INTERVAL);
    }

    private void visualizeAllSelections() {
        for (Map.Entry<
            UUID,
            SelectionData
        > entry : playerSelections.entrySet()) {
            Player player = plugin.getServer().getPlayer(entry.getKey());
            if (player == null || !player.isOnline()) {
                continue;
            }

            SelectionData data = entry.getValue();
            Location first = data.getFirstAnchor();
            Location second = data.getSecondAnchor();

            if (first != null && second != null) {
                if (first.getWorld().equals(second.getWorld())) {
                    drawZoneBorderExcludingAnchors(first, second);

                    highlightSingleBlock(first, ANCHOR_COLOR);
                    highlightSingleBlock(second, ANCHOR_COLOR);
                }
            } else if (first != null) {
                highlightSingleBlock(first, SINGLE_ANCHOR_COLOR);
            } else if (second != null) {
                highlightSingleBlock(second, SINGLE_ANCHOR_COLOR);
            }
        }
    }

    private void highlightSingleBlock(Location location, Color color) {
        World world = location.getWorld();
        if (world == null) return;

        double offset = 0.05; // Small offset to prevent particle overlap
        Location blockMin = location.getBlock().getLocation();
        double minX = blockMin.getX() + offset;
        double minY = blockMin.getY() + offset;
        double minZ = blockMin.getZ() + offset;
        double maxX = blockMin.getX() + 1 - offset;
        double maxY = blockMin.getY() + 1 - offset;
        double maxZ = blockMin.getZ() + 1 - offset;

        double step = 0.2;

        drawParticleLine(
            world,
            minX,
            minY,
            minZ,
            maxX,
            minY,
            minZ,
            step,
            color
        );
        drawParticleLine(
            world,
            minX,
            minY,
            maxZ,
            maxX,
            minY,
            maxZ,
            step,
            color
        );
        drawParticleLine(
            world,
            minX,
            minY,
            minZ,
            minX,
            minY,
            maxZ,
            step,
            color
        );
        drawParticleLine(
            world,
            maxX,
            minY,
            minZ,
            maxX,
            minY,
            maxZ,
            step,
            color
        );

        // Top edges
        drawParticleLine(
            world,
            minX,
            maxY,
            minZ,
            maxX,
            maxY,
            minZ,
            step,
            color
        );
        drawParticleLine(
            world,
            minX,
            maxY,
            maxZ,
            maxX,
            maxY,
            maxZ,
            step,
            color
        );
        drawParticleLine(
            world,
            minX,
            maxY,
            minZ,
            minX,
            maxY,
            maxZ,
            step,
            color
        );
        drawParticleLine(
            world,
            maxX,
            maxY,
            minZ,
            maxX,
            maxY,
            maxZ,
            step,
            color
        );

        // Vertical edges
        drawParticleLine(
            world,
            minX,
            minY,
            minZ,
            minX,
            maxY,
            minZ,
            step,
            color
        );
        drawParticleLine(
            world,
            maxX,
            minY,
            minZ,
            maxX,
            maxY,
            minZ,
            step,
            color
        );
        drawParticleLine(
            world,
            minX,
            minY,
            maxZ,
            minX,
            maxY,
            maxZ,
            step,
            color
        );
        drawParticleLine(
            world,
            maxX,
            minY,
            maxZ,
            maxX,
            maxY,
            maxZ,
            step,
            color
        );
    }

    private void drawParticleLine(
        World world,
        double x1,
        double y1,
        double z1,
        double x2,
        double y2,
        double z2,
        double step,
        Color color
    ) {
        double distance = Math.sqrt(
            Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2) + Math.pow(z2 - z1, 2)
        );
        int points = (int) Math.ceil(distance / step);

        for (int i = 0; i <= points; i++) {
            double ratio = (double) i / points;
            double x = x1 + (x2 - x1) * ratio;
            double y = y1 + (y2 - y1) * ratio;
            double z = z1 + (z2 - z1) * ratio;

            world.spawnParticle(
                Particle.DUST,
                x,
                y,
                z,
                1,
                0,
                0,
                0,
                0,
                new Particle.DustOptions(color, PARTICLE_SIZE)
            );
        }
    }

    private void drawZoneBorderExcludingAnchors(
        Location anchor1,
        Location anchor2
    ) {
        World world = anchor1.getWorld();
        if (world == null) return;

        double minX = Math.min(anchor1.getX(), anchor2.getX());
        double maxX = Math.max(anchor1.getX(), anchor2.getX()) + 1;
        double minY = Math.min(anchor1.getY(), anchor2.getY());
        double maxY = Math.max(anchor1.getY(), anchor2.getY()) + 1;
        double minZ = Math.min(anchor1.getZ(), anchor2.getZ());
        double maxZ = Math.max(anchor1.getZ(), anchor2.getZ()) + 1;

        double step = 0.25;
        Color zoneColor = Color.RED;
        double anchorExclusionRadius = 1.2;

        double corner1X = anchor1.getBlockX() ==
            Math.min(anchor1.getBlockX(), anchor2.getBlockX())
            ? minX
            : maxX;
        double corner1Y = anchor1.getBlockY() ==
            Math.min(anchor1.getBlockY(), anchor2.getBlockY())
            ? minY
            : maxY;
        double corner1Z = anchor1.getBlockZ() ==
            Math.min(anchor1.getBlockZ(), anchor2.getBlockZ())
            ? minZ
            : maxZ;

        double corner2X = anchor2.getBlockX() ==
            Math.min(anchor1.getBlockX(), anchor2.getBlockX())
            ? minX
            : maxX;
        double corner2Y = anchor2.getBlockY() ==
            Math.min(anchor1.getBlockY(), anchor2.getBlockY())
            ? minY
            : maxY;
        double corner2Z = anchor2.getBlockZ() ==
            Math.min(anchor1.getBlockZ(), anchor2.getBlockZ())
            ? minZ
            : maxZ;

        java.util.function.Predicate<double[]> isNearAnchor = coords -> {
            double x = coords[0];
            double y = coords[1];
            double z = coords[2];

            double dist1 = Math.sqrt(
                Math.pow(x - corner1X, 2) +
                Math.pow(y - corner1Y, 2) +
                Math.pow(z - corner1Z, 2)
            );

            double dist2 = Math.sqrt(
                Math.pow(x - corner2X, 2) +
                Math.pow(y - corner2Y, 2) +
                Math.pow(z - corner2Z, 2)
            );

            return (
                dist1 < anchorExclusionRadius || dist2 < anchorExclusionRadius
            );
        };

        // Bottom edges
        drawParticleLineWithAnchorExclusion(
            world,
            minX,
            minY,
            minZ,
            maxX,
            minY,
            minZ,
            step,
            zoneColor,
            isNearAnchor
        );
        drawParticleLineWithAnchorExclusion(
            world,
            minX,
            minY,
            maxZ,
            maxX,
            minY,
            maxZ,
            step,
            zoneColor,
            isNearAnchor
        );
        drawParticleLineWithAnchorExclusion(
            world,
            minX,
            minY,
            minZ,
            minX,
            minY,
            maxZ,
            step,
            zoneColor,
            isNearAnchor
        );
        drawParticleLineWithAnchorExclusion(
            world,
            maxX,
            minY,
            minZ,
            maxX,
            minY,
            maxZ,
            step,
            zoneColor,
            isNearAnchor
        );

        // Top edges
        drawParticleLineWithAnchorExclusion(
            world,
            minX,
            maxY,
            minZ,
            maxX,
            maxY,
            minZ,
            step,
            zoneColor,
            isNearAnchor
        );
        drawParticleLineWithAnchorExclusion(
            world,
            minX,
            maxY,
            maxZ,
            maxX,
            maxY,
            maxZ,
            step,
            zoneColor,
            isNearAnchor
        );
        drawParticleLineWithAnchorExclusion(
            world,
            minX,
            maxY,
            minZ,
            minX,
            maxY,
            maxZ,
            step,
            zoneColor,
            isNearAnchor
        );
        drawParticleLineWithAnchorExclusion(
            world,
            maxX,
            maxY,
            minZ,
            maxX,
            maxY,
            maxZ,
            step,
            zoneColor,
            isNearAnchor
        );

        // Vertical edges
        drawParticleLineWithAnchorExclusion(
            world,
            minX,
            minY,
            minZ,
            minX,
            maxY,
            minZ,
            step,
            zoneColor,
            isNearAnchor
        );
        drawParticleLineWithAnchorExclusion(
            world,
            maxX,
            minY,
            minZ,
            maxX,
            maxY,
            minZ,
            step,
            zoneColor,
            isNearAnchor
        );
        drawParticleLineWithAnchorExclusion(
            world,
            minX,
            minY,
            maxZ,
            minX,
            maxY,
            maxZ,
            step,
            zoneColor,
            isNearAnchor
        );
        drawParticleLineWithAnchorExclusion(
            world,
            maxX,
            minY,
            maxZ,
            maxX,
            maxY,
            maxZ,
            step,
            zoneColor,
            isNearAnchor
        );
    }

    private void drawParticleLineWithAnchorExclusion(
        World world,
        double x1,
        double y1,
        double z1,
        double x2,
        double y2,
        double z2,
        double step,
        Color color,
        java.util.function.Predicate<double[]> isNearAnchor
    ) {
        double distance = Math.sqrt(
            Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2) + Math.pow(z2 - z1, 2)
        );
        int points = (int) Math.ceil(distance / step);

        for (int i = 0; i <= points; i++) {
            double ratio = (double) i / points;
            double x = x1 + (x2 - x1) * ratio;
            double y = y1 + (y2 - y1) * ratio;
            double z = z1 + (z2 - z1) * ratio;

            // Skip particles that are near an anchor point
            if (!isNearAnchor.test(new double[] { x, y, z })) {
                world.spawnParticle(
                    Particle.DUST,
                    x,
                    y,
                    z,
                    1,
                    0,
                    0,
                    0,
                    0,
                    new Particle.DustOptions(color, PARTICLE_SIZE)
                );
            }
        }
    }

    public void enableSelection(Player player) {
        playerSelections.put(player.getUniqueId(), new SelectionData());
    }

    public void enableZoneEditingMode(Player player, Zone zone) {
        SelectionData data = new SelectionData();
        data.setEditingZone(zone);

        Location world = new Location(
            zone.getWorld(),
            zone.getBounds().getMinX(),
            zone.getBounds().getMinY(),
            zone.getBounds().getMinZ()
        );
        Location world2 = new Location(
            zone.getWorld(),
            zone.getBounds().getMaxX(),
            zone.getBounds().getMaxY(),
            zone.getBounds().getMaxZ()
        );

        data.setFirstAnchor(world);
        data.setSecondAnchor(world2);

        playerSelections.put(player.getUniqueId(), data);
    }

    public Zone getEditingZone(Player player) {
        SelectionData data = playerSelections.get(player.getUniqueId());
        return data != null ? data.getEditingZone() : null;
    }

    public boolean disableSelection(Player player) {
        return playerSelections.remove(player.getUniqueId()) != null;
    }

    public boolean isInSelectionMode(Player player) {
        return playerSelections.containsKey(player.getUniqueId());
    }

    public void setFirstAnchor(Player player, Location location) {
        SelectionData data = playerSelections.get(player.getUniqueId());
        if (data != null) {
            data.setFirstAnchor(location);

            if (data.getEditingZone() != null && data.hasCompleteSelection()) {
                applySelectionToZone(data);
            }
        }
    }

    public void setSecondAnchor(Player player, Location location) {
        SelectionData data = playerSelections.get(player.getUniqueId());
        if (data != null) {
            data.setSecondAnchor(location);

            if (data.getEditingZone() != null && data.hasCompleteSelection()) {
                applySelectionToZone(data);
            }
        }
    }

    private void applySelectionToZone(SelectionData data) {
        Zone zone = data.getEditingZone();
        if (zone != null && data.hasCompleteSelection()) {
            Location[] anchors = new Location[] {
                data.getFirstAnchor(),
                data.getSecondAnchor(),
            };
            zone.setAnchors(anchors);
            plugin.getZoneManager().saveZones();
        }
    }

    public Location getFirstAnchor(Player player) {
        SelectionData data = playerSelections.get(player.getUniqueId());
        return data != null ? data.getFirstAnchor() : null;
    }

    public Location getSecondAnchor(Player player) {
        SelectionData data = playerSelections.get(player.getUniqueId());
        return data != null ? data.getSecondAnchor() : null;
    }

    public Location[] getAnchors(Player player) {
        SelectionData data = playerSelections.get(player.getUniqueId());
        if (data != null) {
            return new Location[] {
                data.getFirstAnchor(),
                data.getSecondAnchor(),
            };
        }
        return null;
    }

    public boolean hasCompleteSelection(Player player) {
        SelectionData data = playerSelections.get(player.getUniqueId());
        return data != null && data.hasCompleteSelection();
    }

    public void handlePlayerQuit(Player player) {
        playerSelections.remove(player.getUniqueId());
    }

    public void clearAll() {
        playerSelections.clear();
    }

    public void stopVisualization() {
        if (visualizationTask != null && !visualizationTask.isCancelled()) {
            visualizationTask.cancel();
        }
    }

    private static class SelectionData {

        private Location firstAnchor;
        private Location secondAnchor;
        private Zone editingZone; // The zone being edited, if any

        public Location getFirstAnchor() {
            return firstAnchor;
        }

        public void setFirstAnchor(Location firstAnchor) {
            this.firstAnchor = firstAnchor;
        }

        public Location getSecondAnchor() {
            return secondAnchor;
        }

        public void setSecondAnchor(Location secondAnchor) {
            this.secondAnchor = secondAnchor;
        }

        public Zone getEditingZone() {
            return editingZone;
        }

        public void setEditingZone(Zone editingZone) {
            this.editingZone = editingZone;
        }

        public boolean hasCompleteSelection() {
            return firstAnchor != null && secondAnchor != null;
        }
    }
}
