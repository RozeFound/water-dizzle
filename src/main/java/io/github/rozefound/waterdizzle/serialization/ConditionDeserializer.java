package io.github.rozefound.waterdizzle.serialization;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import io.github.rozefound.waterdizzle.utils.Condition;
import java.lang.reflect.Type;
import org.bukkit.Bukkit;
import org.bukkit.Material;

public class ConditionDeserializer implements JsonDeserializer<Condition> {

    @Override
    public Condition deserialize(
        JsonElement json,
        Type typeOfT,
        JsonDeserializationContext context
    ) throws JsonParseException {
        if (!json.isJsonObject()) {
            throw new JsonParseException(
                "Expected Condition to be a JSON object"
            );
        }

        JsonObject jsonObject = json.getAsJsonObject();

        Condition.Direction direction = null;
        if (jsonObject.has("direction")) {
            String directionStr = jsonObject.get("direction").getAsString();
            try {
                direction = Condition.Direction.valueOf(directionStr);
            } catch (IllegalArgumentException e) {
                throw new JsonParseException(
                    "Invalid Condition.Direction: " + directionStr,
                    e
                );
            }
        }

        if (jsonObject.has("blockDataString")) {
            String blockDataString = jsonObject
                .get("blockDataString")
                .getAsString();
            return new Condition(direction, blockDataString);
        }

        if (jsonObject.has("material")) {
            String materialName = jsonObject.get("material").getAsString();

            try {
                Material material = Material.valueOf(materialName);

                String blockDataString = material.name().toLowerCase();

                Bukkit.getLogger().info(
                    "Migrating condition from Material '" +
                    materialName +
                    "' to block data '" +
                    blockDataString +
                    "'"
                );

                return new Condition(direction, blockDataString);
            } catch (IllegalArgumentException e) {
                Bukkit.getLogger().warning(
                    "Could not migrate Material '" +
                    materialName +
                    "' to block data. Using AIR as fallback."
                );
                return new Condition(direction, "air");
            }
        }

        throw new JsonParseException(
            "Condition JSON must contain either 'blockDataString' or 'material' field"
        );
    }
}
