package io.github.rozefound.waterdizzle;

import io.github.rozefound.waterdizzle.utils.Bounds;
import io.github.rozefound.waterdizzle.utils.Condition;
import io.github.rozefound.waterdizzle.utils.ZoneUtils;
import io.papermc.paper.event.entity.EntityMoveEvent;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public final class ZoneManager {

    private final WaterDizzle plugin;

    private final String name;

    private Location[] anchors;
    private Bounds bounds;

    private double damageAmount;
    private long damageInterval;
    private DamageType damageType;

    private Component deathMessage;

    private boolean canDamageAnimal;
    private boolean canDamageEntity;
    private boolean canDamagePlayer;
    private boolean canDestroyItem;

    private HashMap<Entity, ScheduledTask> tasks;
    private ArrayList<Condition> conditions;
    private HashSet<Player> damagedPlayers;

    public ZoneManager(
        final WaterDizzle plugin,
        final String name,
        final Location firstAnchor,
        final Location secondAnchor
    ) {
        this.plugin = plugin;
        this.name = name;

        this.anchors = new Location[] { firstAnchor, secondAnchor };
        this.bounds = ZoneUtils.getZoneBounds(firstAnchor, secondAnchor);

        this.damageAmount = 0;
        this.damageInterval = 0;
        this.damageType = DamageType.MAGIC;

        this.deathMessage = Component.text(
            "A new soul has perished in the zone."
        );

        this.canDamageAnimal = false;
        this.canDamageEntity = false;
        this.canDamagePlayer = false;
        this.canDestroyItem = false;

        this.tasks = new HashMap<>();
        this.conditions = new ArrayList<>();
        this.damagedPlayers = new HashSet<>();
    }

    // #pragma region setters

    public void setAnchor(final int anchorIndex, final Location location) {
        if (anchorIndex < 0 || anchorIndex >= 2) {
            throw new IllegalArgumentException(
                "Invalid anchor index: " + anchorIndex
            );
        }

        if (
            location.getWorld() !=
            this.anchors[(anchorIndex + 1) % 2].getWorld()
        ) {
            throw new IllegalArgumentException(
                "Anchors must be in the same world"
            );
        }

        this.anchors[anchorIndex] = location;
        this.bounds = ZoneUtils.getZoneBounds(this.anchors[0], this.anchors[1]);
    }

    public void setDamageAmount(final double damageAmount) {
        this.damageAmount = damageAmount;
    }

    public void setDamageInterval(final long damageInterval) {
        this.damageInterval = damageInterval;
    }

    public void setDamageType(final DamageType damageType) {
        this.damageType = damageType;
    }

    public void setDeathMessage(final Component deathMessage) {
        this.deathMessage = deathMessage;
    }

    public void setCanDamageAnimal(final boolean canDamageAnimal) {
        this.canDamageAnimal = canDamageAnimal;
    }

    public void setCanDamageEntity(final boolean canDamageMob) {
        this.canDamageEntity = canDamageMob;
    }

    public void setCanDamagePlayer(final boolean canDamagePlayer) {
        this.canDamagePlayer = canDamagePlayer;
    }

    public void setCanDestroyItem(final boolean canDestroyItem) {
        this.canDestroyItem = canDestroyItem;
    }

    public void addCondition(final Condition condition) {
        this.conditions.add(condition);
    }

    public void removeCondition(final Condition condition) {
        this.conditions.remove(condition);
    }

    // #pragma endregion setters

    // #pragma region getters

    public String getName() {
        return this.name;
    }

    public Location[] getAnchors() {
        return this.anchors.clone();
    }

    public Bounds getBounds() {
        return this.bounds;
    }

    public double getDamageAmount() {
        return this.damageAmount;
    }

    public long getDamageInterval() {
        return this.damageInterval;
    }

    public DamageType getDamageType() {
        return this.damageType;
    }

    public Component getDeathMessage() {
        return this.deathMessage;
    }

    public boolean canDamageAnimal() {
        return this.canDamageAnimal;
    }

    public boolean canDamageMob() {
        return this.canDamageEntity;
    }

    public boolean canDamagePlayer() {
        return this.canDamagePlayer;
    }

    public boolean canDestroyItem() {
        return this.canDestroyItem;
    }

    public ArrayList<Condition> getConditions() {
        return this.conditions;
    }

    // #pragma endregion getters

    public void dealDamage(Entity entity) {
        if (
            this.canDestroyItem && entity instanceof Item
        ) ((Item) entity).remove();

        if (damageAmount == 0) return;

        DamageSource damageSource = DamageSource.builder(damageType).build();

        if (this.canDamagePlayer && entity instanceof Player) {
            var player = (Player) entity;
            player.damage(this.damageAmount, damageSource);
        } else if (this.canDamageEntity && entity instanceof LivingEntity) {
            ((LivingEntity) entity).damage(this.damageAmount, damageSource);
        } else if (this.canDamageAnimal && entity instanceof Animals) {
            ((Animals) entity).damage(this.damageAmount, damageSource);
        }
    }

    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (this.bounds.containsEntity(player)) {
            startDamageTaskForEntity(player, 20L);
        }
    }

    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (this.bounds.containsEntity(player)) {
            startDamageTaskForEntity(player, 5L);
        }
    }

    public void onEntityMove(EntityMoveEvent event) {
        Entity entity = event.getEntity();

        if (this.bounds.containsEntity(entity)) {
            startDamageTaskForEntity(entity, 5L);
        }
    }

    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getPlayer();

        if (damagedPlayers.contains(player)) {
            event.deathMessage(deathMessage);
        }
    }

    private boolean shouldApplyDamage(Entity entity) {
        boolean metAllConditions = true;

        for (var condition : this.conditions) {
            if (!condition.isMetForEntity(entity)) {
                metAllConditions = false;
                break;
            }
        }

        if (
            !entity.isDead() &&
            this.bounds.containsEntity(entity) &&
            metAllConditions
        ) return true;

        return false;
    }

    public void startDamageTaskForEntity(Entity entity, long taskDelay) {
        if (taskDelay <= 0) {
            throw new IllegalArgumentException(
                "Task delay must be greater than 0"
            );
        }

        if (tasks.containsKey(entity) || !shouldApplyDamage(entity)) return;

        tasks.put(
            entity,
            entity
                .getScheduler()
                .runAtFixedRate(
                    this.plugin,
                    task -> {
                        if (shouldApplyDamage(entity)) {
                            boolean isPlayer = entity instanceof Player;
                            if (isPlayer) damagedPlayers.add((Player) entity);
                            dealDamage(entity);
                            if (isPlayer) damagedPlayers.remove(
                                (Player) entity
                            );
                        } else {
                            task.cancel();
                            tasks.remove(entity);
                        }
                    },
                    () -> {},
                    taskDelay,
                    damageInterval
                )
        );
    }
}
