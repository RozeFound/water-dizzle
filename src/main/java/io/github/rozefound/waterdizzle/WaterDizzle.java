package io.github.rozefound.waterdizzle;

import io.github.rozefound.waterdizzle.commands.ZoneCommand;
import io.github.rozefound.waterdizzle.listeners.WaterDizzleListener;
import io.github.rozefound.waterdizzle.utils.Condition;
import io.github.rozefound.waterdizzle.utils.Condition.Direction;
import io.github.rozefound.waterdizzle.utils.ZoneUtils;
import io.papermc.lib.PaperLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.damage.DamageType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class WaterDizzle extends JavaPlugin {

    private WaterDizzleListener waterDizzleListener;
    public ZoneManager zoneManager;

    private static final double DAMAGE_AMOUNT = 0.5;
    private static final long DAMAGE_INTERVAL_TICKS = 10L; // half a second

    private boolean particlesEnabled = false;

    @Override
    public void onEnable() {
        PaperLib.suggestPaper(this);

        waterDizzleListener = new WaterDizzleListener(this);
        getServer()
            .getPluginManager()
            .registerEvents(waterDizzleListener, this);

        getCommand("zone").setExecutor(
            new ZoneCommand(this, waterDizzleListener)
        );

        saveDefaultConfig();

        Location firstAnchor = new Location(
            this.getServer().getWorlds().get(0),
            1,
            1,
            1
        );
        Location secondAnchor = new Location(
            this.getServer().getWorlds().get(0),
            1,
            1,
            1
        );

        zoneManager = new ZoneManager(
            this,
            "WaterDamageZone",
            firstAnchor,
            secondAnchor
        );

        zoneManager.setDamageAmount(DAMAGE_AMOUNT);
        zoneManager.setDamageInterval(DAMAGE_INTERVAL_TICKS);

        zoneManager.setCanDamagePlayer(true);
        zoneManager.setCanDamageEntity(true);

        zoneManager.setDamageType(DamageType.IN_FIRE);

        zoneManager.addCondition(
            new Condition(Direction.Inside, Material.WATER)
        );

        startParticleTask();
    }

    public Location[] getZoneAnchors() {
        return zoneManager.getAnchors();
    }

    public boolean toggleParticles() {
        var old_value = particlesEnabled;
        particlesEnabled = !particlesEnabled;
        return old_value;
    }

    public void setZoneAnchor(int anchorIndex, Location location) {
        zoneManager.setAnchor(anchorIndex, location);

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
                ZoneUtils.spawnZoneBorderParticles(getZoneAnchors());
            }
        }
            .runTaskTimer(this, 0L, 10L); // Run every half a second
    }
}
