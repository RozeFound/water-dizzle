package io.github.rozefound.waterdizzle.utils;

import com.google.gson.annotations.Expose;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.util.BoundingBox;

public final class Condition {

    public enum Direction {
        Inside,
        StandingOn,
    }

    @Expose
    private Direction direction;

    @Expose
    private String blockDataString;

    private transient BlockData blockData;

    public Condition(Direction direction, String blockDataString) {
        this.direction = direction;
        this.blockDataString = blockDataString;
        this.blockData = parseBlockData(blockDataString);
    }

    public Condition(Direction direction, BlockData blockData) {
        this.direction = direction;
        this.blockData = blockData;
        this.blockDataString = blockData.getAsString();
    }

    private BlockData parseBlockData(String blockDataString) {
        try {
            String materialOnly = blockDataString.split("\\[")[0];
            return Bukkit.createBlockData(materialOnly);
        } catch (IllegalArgumentException e) {
            try {
                return Bukkit.createBlockData(blockDataString.toUpperCase());
            } catch (IllegalArgumentException e2) {
                throw new IllegalArgumentException(
                    "Invalid block data: " + blockDataString,
                    e
                );
            }
        }
    }

    public Direction getDirection() {
        return direction;
    }

    public BlockData getBlockData() {
        if (blockData == null && blockDataString != null) {
            blockData = parseBlockData(blockDataString);
        }
        return blockData;
    }

    public String getBlockDataString() {
        return blockDataString.split("\\[")[0];
    }

    public boolean isMetForEntity(Entity entity) {
        Location entityLocation = entity.getLocation();
        BlockData targetBlockData = getBlockData();

        if (targetBlockData == null) {
            return false;
        }

        if (direction == Direction.Inside) {
            BoundingBox entityBoundingBox = entity.getBoundingBox();

            int minBlockX = (int) Math.floor(entityBoundingBox.getMinX());
            int maxBlockX = (int) Math.floor(entityBoundingBox.getMaxX());
            int minBlockY = (int) Math.floor(entityBoundingBox.getMinY());
            int maxBlockY = (int) Math.floor(entityBoundingBox.getMaxY());
            int minBlockZ = (int) Math.floor(entityBoundingBox.getMinZ());
            int maxBlockZ = (int) Math.floor(entityBoundingBox.getMaxZ());

            for (int x = minBlockX; x <= maxBlockX; x++) {
                for (int y = minBlockY; y <= maxBlockY; y++) {
                    for (int z = minBlockZ; z <= maxBlockZ; z++) {
                        Location blockLocation = new Location(
                            entityLocation.getWorld(),
                            x,
                            y,
                            z
                        );
                        BlockData currentBlockData = blockLocation
                            .getBlock()
                            .getBlockData();
                        if (
                            matchesBlockData(currentBlockData, targetBlockData)
                        ) {
                            return true;
                        }
                    }
                }
            }
        } else if (direction == Direction.StandingOn) {
            BoundingBox entityBoundingBox = entity.getBoundingBox();

            int minBlockX = (int) Math.floor(entityBoundingBox.getMinX());
            int maxBlockX = (int) Math.floor(entityBoundingBox.getMaxX());
            int blockY = (int) Math.floor(entityBoundingBox.getMinY() - 0.1); // Slightly below feet
            int minBlockZ = (int) Math.floor(entityBoundingBox.getMinZ());
            int maxBlockZ = (int) Math.floor(entityBoundingBox.getMaxZ());

            for (int x = minBlockX; x <= maxBlockX; x++) {
                for (int z = minBlockZ; z <= maxBlockZ; z++) {
                    Location blockLocation = new Location(
                        entityLocation.getWorld(),
                        x,
                        blockY,
                        z
                    );
                    BlockData currentBlockData = blockLocation
                        .getBlock()
                        .getBlockData();
                    if (matchesBlockData(currentBlockData, targetBlockData)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private boolean matchesBlockData(BlockData current, BlockData target) {
        return current.getMaterial().equals(target.getMaterial());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Condition condition = (Condition) obj;

        if (direction != condition.direction) return false;
        if (blockDataString != null) {
            return blockDataString.equals(condition.blockDataString);
        }
        return condition.blockDataString == null;
    }

    @Override
    public int hashCode() {
        int result = direction != null ? direction.hashCode() : 0;
        result =
            31 * result +
            (blockDataString != null ? blockDataString.hashCode() : 0);
        return result;
    }
}
