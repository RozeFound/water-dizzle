package io.github.rozefound.waterdizzle.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.util.BoundingBox;

public final class Condition {

    public enum Direction {
        Inside,
        StandingOn,
    }

    private Direction direction;
    private Material material;

    public Condition(Direction direction, Material material) {
        this.direction = direction;
        this.material = material;
    }

    public Direction getDirection() {
        return direction;
    }

    public Material getMaterial() {
        return material;
    }

    public boolean isMetForEntity(Entity entity) {
        Location entityLocation = entity.getLocation();

        if (direction == Direction.Inside) {
            // Check if entity's bounding box overlaps with any block of the specified type
            BoundingBox entityBoundingBox = entity.getBoundingBox();

            // Get the block coordinates the entity's bounding box spans
            int minBlockX = (int) Math.floor(entityBoundingBox.getMinX());
            int maxBlockX = (int) Math.floor(entityBoundingBox.getMaxX());
            int minBlockY = (int) Math.floor(entityBoundingBox.getMinY());
            int maxBlockY = (int) Math.floor(entityBoundingBox.getMaxY());
            int minBlockZ = (int) Math.floor(entityBoundingBox.getMinZ());
            int maxBlockZ = (int) Math.floor(entityBoundingBox.getMaxZ());

            // Check all blocks the entity's bounding box touches
            for (int x = minBlockX; x <= maxBlockX; x++) {
                for (int y = minBlockY; y <= maxBlockY; y++) {
                    for (int z = minBlockZ; z <= maxBlockZ; z++) {
                        Location blockLocation = new Location(
                            entityLocation.getWorld(),
                            x,
                            y,
                            z
                        );
                        if (
                            blockLocation.getBlock().getType().equals(material)
                        ) {
                            return true;
                        }
                    }
                }
            }
        } else if (direction == Direction.StandingOn) {
            // Check if entity is standing on a block of the specified type
            BoundingBox entityBoundingBox = entity.getBoundingBox();

            // Get the block coordinates at the entity's feet level (bottom of bounding box)
            int minBlockX = (int) Math.floor(entityBoundingBox.getMinX());
            int maxBlockX = (int) Math.floor(entityBoundingBox.getMaxX());
            int blockY = (int) Math.floor(entityBoundingBox.getMinY() - 0.1); // Slightly below feet
            int minBlockZ = (int) Math.floor(entityBoundingBox.getMinZ());
            int maxBlockZ = (int) Math.floor(entityBoundingBox.getMaxZ());

            // Check blocks below the entity
            for (int x = minBlockX; x <= maxBlockX; x++) {
                for (int z = minBlockZ; z <= maxBlockZ; z++) {
                    Location blockLocation = new Location(
                        entityLocation.getWorld(),
                        x,
                        blockY,
                        z
                    );
                    if (blockLocation.getBlock().getType().equals(material)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
