package io.github.rozefound.waterdizzle.serialization;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import org.bukkit.Material;

public class MaterialSerializer implements JsonSerializer<Material> {

    @Override
    public JsonElement serialize(
        Material material,
        Type typeOfSrc,
        JsonSerializationContext context
    ) {
        if (material == null) {
            return null;
        }

        return new JsonPrimitive(material.name());
    }
}
