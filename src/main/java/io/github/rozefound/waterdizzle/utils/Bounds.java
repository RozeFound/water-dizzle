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

        double playerMinX =
            entityLocation.getX() - (entityBoundingBox.getWidthX() / 2);
        double playerMaxX =
            entityLocation.getX() + (entityBoundingBox.getWidthX() / 2);
        double playerMinY = entityLocation.getY();
        double playerMaxY =
            entityLocation.getY() + entityBoundingBox.getHeight();
        double playerMinZ =
            entityLocation.getZ() - (entityBoundingBox.getWidthZ() / 2);
        double playerMaxZ =
            entityLocation.getZ() + (entityBoundingBox.getWidthZ() / 2);

        boolean xOverlap = playerMaxX > minX && playerMinX < maxX;
        boolean yOverlap = playerMaxY > minY && playerMinY < maxY;
        boolean zOverlap = playerMaxZ > minZ && playerMinZ < maxZ;

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
