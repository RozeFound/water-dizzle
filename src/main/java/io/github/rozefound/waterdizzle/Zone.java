package io.github.rozefound.waterdizzle;

import com.google.gson.annotations.Expose;
import io.github.rozefound.waterdizzle.serialization.GsonFactory;
import io.github.rozefound.waterdizzle.utils.Bounds;
import io.github.rozefound.waterdizzle.utils.Condition;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Enemy;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public final class Zone {

    private final transient WaterDizzle plugin;

    @Expose
    private final String name;

    @Expose
    private Location[] anchors;

    private transient Bounds bounds;

    @Expose
    private double damageAmount;

    @Expose
    private long damageInterval;

    @Expose
    private DamageType damageType;

    @Expose
    private String deathMessage;

    @Expose
    private boolean damageAnimal;

    @Expose
    private boolean damageEntity;

    @Expose
    private boolean damagePlayer;

    @Expose
    private boolean destroyItem;

    @Expose
    private boolean enabled = true;

    private transient HashMap<Entity, ScheduledTask> tasks;

    @Expose
    private ArrayList<Condition> conditions;

    private transient HashSet<Player> damagedPlayers;

    public Zone(
        final WaterDizzle plugin,
        final String name,
        final Location firstAnchor,
        final Location secondAnchor
    ) {
        this.plugin = plugin;
        this.name = name;

        this.anchors = new Location[] { firstAnchor, secondAnchor };
        this.bounds = Bounds.fromAnchors(firstAnchor, secondAnchor);

        this.damageAmount = 0;
        this.damageInterval = 0;
        this.damageType = DamageType.MAGIC;

        this.deathMessage = plugin.getLanguageManager() != null
            ? plugin
                .getLanguageManager()
                .getMessage("zone.default-death-message")
            : "<gray>{player} has perished in the zone.</gray>";

        this.damageAnimal = false;
        this.damageEntity = false;
        this.damagePlayer = false;
        this.destroyItem = false;
        this.enabled = true;

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
        this.bounds = Bounds.fromAnchors(this.anchors[0], this.anchors[1]);
    }

    public void setAnchors(final Location[] anchors) {
        this.anchors = anchors;
        this.bounds = Bounds.fromAnchors(this.anchors[0], this.anchors[1]);
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

    public void setDeathMessage(final String deathMessage) {
        this.deathMessage = deathMessage;
    }

    public void setDeathMessageString(final String deathMessageString) {
        this.deathMessage = deathMessageString;
    }

    public void setDamageAnimal(final boolean damageAnimal) {
        this.damageAnimal = damageAnimal;
    }

    public void setDamageEntity(final boolean damageEntity) {
        this.damageEntity = damageEntity;
    }

    public void setDamagePlayer(final boolean damagePlayer) {
        this.damagePlayer = damagePlayer;
    }

    public void setDestroyItem(final boolean destroyItem) {
        this.destroyItem = destroyItem;
    }

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
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

    public World getWorld() {
        if (
            this.anchors != null &&
            this.anchors.length > 0 &&
            this.anchors[0] != null
        ) {
            return this.anchors[0].getWorld();
        }
        return null;
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

    public String getDeathMessage() {
        return this.deathMessage;
    }

    public Component getDeathMessageComponent() {
        return MiniMessage.miniMessage().deserialize(this.deathMessage);
    }

    public boolean damageAnimal() {
        return this.damageAnimal;
    }

    public boolean damageEntity() {
        return this.damageEntity;
    }

    public boolean damagePlayer() {
        return this.damagePlayer;
    }

    public boolean destroyItem() {
        return this.destroyItem;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public ArrayList<Condition> getConditions() {
        return this.conditions;
    }

    // #pragma endregion getters

    public boolean ensureWorldLoaded() {
        if (this.anchors == null || this.anchors.length == 0) {
            return false;
        }

        if (this.anchors[0] != null && this.anchors[0].getWorld() != null) {
            return true;
        }

        for (int i = 0; i < this.anchors.length; i++) {
            if (this.anchors[i] != null) {
                Location anchor = this.anchors[i];
                if (anchor.getWorld() == null) {
                    return false;
                }
            }
        }

        if (
            this.anchors[0].getWorld() != null &&
            this.anchors[1].getWorld() != null
        ) {
            this.bounds = Bounds.fromAnchors(this.anchors[0], this.anchors[1]);
            return true;
        }

        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Zone zone = (Zone) obj;
        return name.equals(zone.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public void dealDamage(Entity entity) {

        if (damageAmount == 0) return;

        DamageSource damageSource = DamageSource.builder(damageType).build();

        if (this.damagePlayer && entity instanceof Player) {
            var player = (Player) entity;
            player.damage(this.damageAmount, damageSource);
        } else if (this.damageAnimal && entity instanceof Animals) {
            ((Animals) entity).damage(this.damageAmount, damageSource);
        } else if (this.damageEntity && entity instanceof Enemy) {
            ((Enemy) entity).damage(this.damageAmount, damageSource);
        }
    }

    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!enabled) return;
        Player player = event.getPlayer();

        if (this.bounds.containsEntity(player)) {
            startDamageTaskForEntity(player, 20L);
        }
    }

    public void onEntityMove(Entity entity) {
        if (!enabled) return;

        if (this.bounds.containsEntity(entity)) {
            startDamageTaskForEntity(entity, 5L);
        }
    }

    public void onPlayerDeath(PlayerDeathEvent event) {
        if (!enabled) return;
        Player player = event.getPlayer();

        if (damagedPlayers.contains(player)) {
            MiniMessage mm = MiniMessage.miniMessage();

            Component formattedMessage = mm.deserialize(
                this.deathMessage,
                Placeholder.unparsed("player", player.getName())
            );
            event.deathMessage(formattedMessage);
        }
    }

    private boolean shouldApplyDamage(Entity entity) {
        if (!this.bounds.containsEntity(entity)) {
            return false;
        }

        if (!(entity instanceof Item) && entity.isDead()) {
            return false;
        }

        if (this.conditions.isEmpty()) {
            return true;
        }

        for (var condition : this.conditions) {
            if (condition.isMetForEntity(entity)) {
                return true;
            }
        }

        return false;
    }

    public void startDamageTaskForEntity(Entity entity, long taskDelay) {

        if (this.destroyItem && entity instanceof Item item && shouldApplyDamage(entity)) {
            item.remove(); return;
        }

        if (
            tasks.containsKey(entity) ||
            !shouldApplyDamage(entity) ||
            (taskDelay <= 0 || damageInterval <= 0)
        ) return;

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

    // #pragma region JSON serialization

    public String toJson() {
        return GsonFactory.createPrettyGson().toJson(this);
    }

    public static Zone fromJson(String json, WaterDizzle plugin) {
        Zone zone = GsonFactory.createGson().fromJson(json, Zone.class);

        try {
            var pluginField = Zone.class.getDeclaredField("plugin");
            pluginField.setAccessible(true);
            pluginField.set(zone, plugin);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set plugin field", e);
        }

        if (zone.anchors != null && zone.anchors.length >= 2) {
            zone.bounds = Bounds.fromAnchors(zone.anchors[0], zone.anchors[1]);
        }

        zone.tasks = new HashMap<>();
        zone.damagedPlayers = new HashSet<>();

        return zone;
    }

    // #pragma endregion JSON serialization
}
