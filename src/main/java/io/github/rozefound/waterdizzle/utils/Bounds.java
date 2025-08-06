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
}
