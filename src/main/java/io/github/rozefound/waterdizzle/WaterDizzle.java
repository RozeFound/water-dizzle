package io.github.rozefound.waterdizzle;

import io.github.rozefound.waterdizzle.commands.ZoneCommand;
import io.github.rozefound.waterdizzle.listeners.WaterDizzleListener;
import io.github.rozefound.waterdizzle.utils.ZoneUtils;
import io.papermc.lib.PaperLib;
import java.util.HashSet;
import org.bukkit.Location;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class WaterDizzle extends JavaPlugin {

    private WaterDizzleListener waterDizzleListener;

    private Location[] damageZone;
    private HashSet<Player> playersInDamageZone;
    private static final double DAMAGE_AMOUNT = 0.5;
    private static final long DAMAGE_INTERVAL_TICKS = 10L; // half a second
    private boolean particlesEnabled = false;

    private HashSet<Player> wasDamagedByWater;

    @Override
    public void onEnable() {
        PaperLib.suggestPaper(this);

        // Create and register the example listener
        waterDizzleListener = new WaterDizzleListener(this);
        getServer()
            .getPluginManager()
            .registerEvents(waterDizzleListener, this);

        // Register the zone command
        getCommand("zone").setExecutor(
            new ZoneCommand(this, waterDizzleListener)
        );

        saveDefaultConfig();

        damageZone = new Location[2];
        playersInDamageZone = new HashSet<>();
        wasDamagedByWater = new HashSet<>();

        damageZone[0] = new Location(
            this.getServer().getWorlds().get(0),
            1,
            1,
            1
        );
        damageZone[1] = new Location(
            this.getServer().getWorlds().get(0),
            1,
            1,
            1
        );

        startDamageTask();
        startParticleTask();
    }

    public boolean isPlayerGotDamagedByWater(Player player) {
        return wasDamagedByWater.contains(player);
    }

    public boolean isPlayerInDamageZone(Player player) {
        return playersInDamageZone.contains(player);
    }

    public void addPlayerToDamageZone(Player player) {
        playersInDamageZone.add(player);
    }

    public void removePlayerFromDamageZone(Player player) {
        playersInDamageZone.remove(player);
    }

    public Location[] getDamageZone() {
        return damageZone;
    }

    public Location[] getZoneAnchors() {
        return new Location[] { damageZone[0].clone(), damageZone[1].clone() };
    }

    public boolean toggleParticles() {
        var old_value = particlesEnabled;
        particlesEnabled = !particlesEnabled;
        return old_value;
    }

    public void setZoneAnchor(int anchorIndex, Location location) {
        if (anchorIndex < 0 || anchorIndex >= damageZone.length) {
            throw new IllegalArgumentException(
                "Invalid anchor index: " + anchorIndex
            );
        }

        damageZone[anchorIndex] = location.clone();

        this.getLogger().info(
            String.format(
                "Zone anchor %d set to: %.0f, %.0f, %.0f in world %s",
                anchorIndex + 1,
                location.getX(),
                location.getY(),
                location.getZ(),
                location.getWorld().getName()
            )
        );
    }

    private void startParticleTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!particlesEnabled) return;
                ZoneUtils.spawnZoneBorderParticles(damageZone);
            }
        }
            .runTaskTimer(this, 0L, 10L); // Run every half a second
    }

    private void startDamageTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : playersInDamageZone) {
                    if (
                        player.isOnline() &&
                        ZoneUtils.isPlayerInZone(player, damageZone) &&
                        player.isInWater()
                    ) {
                        wasDamagedByWater.add(player);
                        player.damage(
                            DAMAGE_AMOUNT,
                            DamageSource.builder(DamageType.MAGIC).build()
                        );
                        wasDamagedByWater.remove(player);
                    }
                }
            }
        }
            .runTaskTimer(this, 0L, DAMAGE_INTERVAL_TICKS);
    }
}
