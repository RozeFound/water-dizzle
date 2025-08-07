package io.github.rozefound.waterdizzle.serialization;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import org.bukkit.damage.DamageType;

public class DamageTypeSerializer implements JsonSerializer<DamageType> {

    @Override
    public JsonElement serialize(
        DamageType src,
        Type typeOfSrc,
        JsonSerializationContext context
    ) {
        return context.serialize(src.key().toString());
    }
}
