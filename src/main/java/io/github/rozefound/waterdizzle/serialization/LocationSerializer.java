package io.github.rozefound.waterdizzle.serialization;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import org.bukkit.Location;
import org.bukkit.World;

public class LocationSerializer implements JsonSerializer<Location> {

    @Override
    public JsonElement serialize(
        Location location,
        Type typeOfSrc,
        JsonSerializationContext context
    ) {
        JsonObject json = new JsonObject();

        World world = location.getWorld();
        if (world != null) {
            json.addProperty("world", world.getName());
        }

        json.addProperty("x", location.getX());
        json.addProperty("y", location.getY());
        json.addProperty("z", location.getZ());

        float yaw = location.getYaw();
        float pitch = location.getPitch();

        if (yaw != 0.0f) {
            json.addProperty("yaw", yaw);
        }

        if (pitch != 0.0f) {
            json.addProperty("pitch", pitch);
        }

        return json;
    }
}
