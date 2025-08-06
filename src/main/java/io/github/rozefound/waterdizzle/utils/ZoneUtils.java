package io.github.rozefound.waterdizzle.utils;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;

public final class ZoneUtils {

    private ZoneUtils() {}

    public static Bounds getZoneBounds(
        Location firstAnchor,
        Location secondAnchor
    ) {
        if (firstAnchor.getWorld() != secondAnchor.getWorld()) {
            throw new IllegalArgumentException(
                "Anchors must be in the same world"
            );
        }

        double zoneMinX = Math.min(firstAnchor.getX(), secondAnchor.getX());
        double zoneMaxX =
            Math.max(firstAnchor.getX(), secondAnchor.getX()) + 1.0;
        double zoneMinY = Math.min(firstAnchor.getY(), secondAnchor.getY());
        double zoneMaxY =
            Math.max(firstAnchor.getY(), secondAnchor.getY()) + 1.0;
        double zoneMinZ = Math.min(firstAnchor.getZ(), secondAnchor.getZ());
        double zoneMaxZ =
            Math.max(firstAnchor.getZ(), secondAnchor.getZ()) + 1.0;

        return new Bounds(
            firstAnchor.getWorld(),
            zoneMinX,
            zoneMinY,
            zoneMinZ,
            zoneMaxX,
            zoneMaxY,
            zoneMaxZ
        );
    }

    public static void spawnZoneBorderParticles(Location[] zone) {
        World world = zone[0].getWorld();
        if (world == null) return;

        var zoneBounds = getZoneBounds(zone[0], zone[1]);
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
