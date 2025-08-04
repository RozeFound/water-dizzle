package io.github.rozefound.waterdizzle.utils;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;

public final class ZoneUtils {

    private ZoneUtils() {}

    public static Bounds getZoneBounds(Location[] zone) {
        double zoneMinX = Math.min(zone[0].getX(), zone[1].getX());
        double zoneMaxX = Math.max(zone[0].getX(), zone[1].getX()) + 1.0;
        double zoneMinY = Math.min(zone[0].getY(), zone[1].getY());
        double zoneMaxY = Math.max(zone[0].getY(), zone[1].getY()) + 1.0;
        double zoneMinZ = Math.min(zone[0].getZ(), zone[1].getZ());
        double zoneMaxZ = Math.max(zone[0].getZ(), zone[1].getZ()) + 1.0;

        return new Bounds(
            zoneMinX,
            zoneMinY,
            zoneMinZ,
            zoneMaxX,
            zoneMaxY,
            zoneMaxZ
        );
    }

    public static boolean isPlayerInZone(Player player, Location[] zone) {
        Location playerLocation = player.getLocation();

        final double PLAYER_WIDTH = 0.6;
        final double PLAYER_HEIGHT = 1.8;

        var zoneBounds = getZoneBounds(zone);

        double playerMinX = playerLocation.getX() - (PLAYER_WIDTH / 2);
        double playerMaxX = playerLocation.getX() + (PLAYER_WIDTH / 2);
        double playerMinY = playerLocation.getY();
        double playerMaxY = playerLocation.getY() + PLAYER_HEIGHT;
        double playerMinZ = playerLocation.getZ() - (PLAYER_WIDTH / 2);
        double playerMaxZ = playerLocation.getZ() + (PLAYER_WIDTH / 2);

        boolean xOverlap =
            playerMaxX > zoneBounds.getMinX() &&
            playerMinX < zoneBounds.getMaxX();
        boolean yOverlap =
            playerMaxY > zoneBounds.getMinY() &&
            playerMinY < zoneBounds.getMaxY();
        boolean zOverlap =
            playerMaxZ > zoneBounds.getMinZ() &&
            playerMinZ < zoneBounds.getMaxZ();

        return (
            playerLocation.getWorld().equals(zone[0].getWorld()) &&
            xOverlap &&
            yOverlap &&
            zOverlap
        );
    }

    public static void spawnZoneBorderParticles(Location[] zone) {
        World world = zone[0].getWorld();
        if (world == null) return;

        var zoneBounds = getZoneBounds(zone);
        double step = 0.2; // Distance between particles

        // Bottom face edges
        drawParticleLine(
            world,
            zoneBounds.getMinX(),
            zoneBounds.getMinY(),
            zoneBounds.getMinZ(),
            zoneBounds.getMaxX(),
            zoneBounds.getMinY(),
            zoneBounds.getMinZ(),
            step
        ); // Bottom front
        drawParticleLine(
            world,
            zoneBounds.getMinX(),
            zoneBounds.getMinY(),
            zoneBounds.getMaxZ(),
            zoneBounds.getMaxX(),
            zoneBounds.getMinY(),
            zoneBounds.getMaxZ(),
            step
        ); // Bottom back
        drawParticleLine(
            world,
            zoneBounds.getMinX(),
            zoneBounds.getMinY(),
            zoneBounds.getMinZ(),
            zoneBounds.getMinX(),
            zoneBounds.getMinY(),
            zoneBounds.getMaxZ(),
            step
        ); // Bottom left
        drawParticleLine(
            world,
            zoneBounds.getMaxX(),
            zoneBounds.getMinY(),
            zoneBounds.getMinZ(),
            zoneBounds.getMaxX(),
            zoneBounds.getMinY(),
            zoneBounds.getMaxZ(),
            step
        ); // Bottom right

        // Top face edges
        drawParticleLine(
            world,
            zoneBounds.getMinX(),
            zoneBounds.getMaxY(),
            zoneBounds.getMinZ(),
            zoneBounds.getMaxX(),
            zoneBounds.getMaxY(),
            zoneBounds.getMinZ(),
            step
        ); // Top front
        drawParticleLine(
            world,
            zoneBounds.getMinX(),
            zoneBounds.getMaxY(),
            zoneBounds.getMaxZ(),
            zoneBounds.getMaxX(),
            zoneBounds.getMaxY(),
            zoneBounds.getMaxZ(),
            step
        ); // Top back
        drawParticleLine(
            world,
            zoneBounds.getMinX(),
            zoneBounds.getMaxY(),
            zoneBounds.getMinZ(),
            zoneBounds.getMinX(),
            zoneBounds.getMaxY(),
            zoneBounds.getMaxZ(),
            step
        ); // Top left
        drawParticleLine(
            world,
            zoneBounds.getMaxX(),
            zoneBounds.getMaxY(),
            zoneBounds.getMinZ(),
            zoneBounds.getMaxX(),
            zoneBounds.getMaxY(),
            zoneBounds.getMaxZ(),
            step
        ); // Top right

        // Vertical edges
        drawParticleLine(
            world,
            zoneBounds.getMinX(),
            zoneBounds.getMinY(),
            zoneBounds.getMinZ(),
            zoneBounds.getMinX(),
            zoneBounds.getMaxY(),
            zoneBounds.getMinZ(),
            step
        ); // Front left
        drawParticleLine(
            world,
            zoneBounds.getMaxX(),
            zoneBounds.getMinY(),
            zoneBounds.getMinZ(),
            zoneBounds.getMaxX(),
            zoneBounds.getMaxY(),
            zoneBounds.getMinZ(),
            step
        ); // Front right
        drawParticleLine(
            world,
            zoneBounds.getMinX(),
            zoneBounds.getMinY(),
            zoneBounds.getMaxZ(),
            zoneBounds.getMinX(),
            zoneBounds.getMaxY(),
            zoneBounds.getMaxZ(),
            step
        ); // Back left
        drawParticleLine(
            world,
            zoneBounds.getMaxX(),
            zoneBounds.getMinY(),
            zoneBounds.getMaxZ(),
            zoneBounds.getMaxX(),
            zoneBounds.getMaxY(),
            zoneBounds.getMaxZ(),
            step
        ); // Back right
    }

    private static void drawParticleLine(
        World world,
        double x1,
        double y1,
        double z1,
        double x2,
        double y2,
        double z2,
        double step
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
                new Particle.DustOptions(org.bukkit.Color.RED, 1.0f)
            );
        }
    }
}
