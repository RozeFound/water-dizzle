package io.github.rozefound.waterdizzle.serialization;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class LocationDeserializer implements JsonDeserializer<Location> {

    @Override
    public Location deserialize(
        JsonElement json,
        Type typeOfT,
        JsonDeserializationContext context
    ) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        String worldName = null;
        World world = null;

        if (jsonObject.has("world")) {
            worldName = jsonObject.get("world").getAsString();
            world = Bukkit.getWorld(worldName);

            if (world == null) {
                Bukkit.getLogger().warning(
                    "Could not find world '" +
                    worldName +
                    "' when deserializing Location. World may not be loaded yet."
                );
            }
        }

        double x = jsonObject.get("x").getAsDouble();
        double y = jsonObject.get("y").getAsDouble();
        double z = jsonObject.get("z").getAsDouble();

        float yaw = 0.0f;
        float pitch = 0.0f;

        if (jsonObject.has("yaw")) {
            yaw = jsonObject.get("yaw").getAsFloat();
        }

        if (jsonObject.has("pitch")) {
            pitch = jsonObject.get("pitch").getAsFloat();
        }

        return new Location(world, x, y, z, yaw, pitch);
    }
}
