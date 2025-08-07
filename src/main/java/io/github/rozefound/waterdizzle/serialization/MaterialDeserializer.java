package io.github.rozefound.waterdizzle.serialization;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import org.bukkit.Bukkit;
import org.bukkit.Material;

public class MaterialDeserializer implements JsonDeserializer<Material> {

    @Override
    public Material deserialize(
        JsonElement json,
        Type typeOfT,
        JsonDeserializationContext context
    ) throws JsonParseException {
        if (json == null || json.isJsonNull()) {
            return null;
        }

        String materialName = json.getAsString();

        try {
            return Material.valueOf(materialName);
        } catch (IllegalArgumentException e) {
            Bukkit.getLogger().warning(
                "Could not deserialize Material: '" +
                materialName +
                "'. Material may not exist in this version of Minecraft."
            );
            return null;
        }
    }
}
