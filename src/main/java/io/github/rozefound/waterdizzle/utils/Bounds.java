package io.github.rozefound.waterdizzle.utils;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.util.BoundingBox;

public final class Bounds {

    private World world;

    private double minX;
    private double minY;
    private double minZ;
    private double maxX;
    private double maxY;
    private double maxZ;

    public Bounds(
        World world,
        double minX,
        double minY,
        double minZ,
        double maxX,
        double maxY,
        double maxZ
    ) {
        this.world = world;
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
    }

    public World getWorld() {
        return world;
    }

    public double getMinX() {
        return minX;
    }

    public double getMinY() {
        return minY;
    }

    public double getMinZ() {
        return minZ;
    }

    public double getMaxX() {
        return maxX;
    }

    public double getMaxY() {
        return maxY;
    }

    public double getMaxZ() {
        return maxZ;
    }

    public static Bounds fromAnchors(
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

    public boolean containsEntity(final Entity entity) {
        Location entityLocation = entity.getLocation();
        BoundingBox entityBoundingBox = entity.getBoundingBox();

        double entityMinX =
            entityLocation.getX() - (entityBoundingBox.getWidthX() / 2);
        double entityMaxX =
            entityLocation.getX() + (entityBoundingBox.getWidthX() / 2);
        double entityMinY = entityLocation.getY();
        double entityMaxY =
            entityLocation.getY() + entityBoundingBox.getHeight();
        double entityMinZ =
            entityLocation.getZ() - (entityBoundingBox.getWidthZ() / 2);
        double entityMaxZ =
            entityLocation.getZ() + (entityBoundingBox.getWidthZ() / 2);

        boolean xOverlap = entityMaxX > minX && entityMinX < maxX;
        boolean yOverlap = entityMaxY > minY && entityMinY < maxY;
        boolean zOverlap = entityMaxZ > minZ && entityMinZ < maxZ;

        return (
            entityLocation.getWorld().equals(world) &&
            xOverlap &&
            yOverlap &&
            zOverlap
        );
    }

    public boolean contains(Location location) {
        if (location == null) return false;
        if (!location.getWorld().equals(world)) return false;

        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();

        return (
            x >= minX &&
            x <= maxX &&
            y >= minY &&
            y <= maxY &&
            z >= minZ &&
            z <= maxZ
        );
    }

    public boolean containsBlock(Location location) {
        if (location == null) return false;
        if (!location.getWorld().equals(world)) return false;

        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();

        return (
            x >= minX &&
            x < maxX &&
            y >= minY &&
            y < maxY &&
            z >= minZ &&
            z < maxZ
        );
    }

    public Location getCenter() {
        double centerX = (minX + maxX) / 2.0;
        double centerY = (minY + maxY) / 2.0;
        double centerZ = (minZ + maxZ) / 2.0;
        return new Location(world, centerX, centerY, centerZ);
    }

    public int getVolume() {
        int width = (int) (maxX - minX);
        int height = (int) (maxY - minY);
        int length = (int) (maxZ - minZ);
        return width * height * length;
    }
}
