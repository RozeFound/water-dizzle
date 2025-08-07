package io.github.rozefound.waterdizzle.serialization;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import io.github.rozefound.waterdizzle.utils.Condition;
import java.lang.reflect.Type;

public class ConditionSerializer implements JsonSerializer<Condition> {

    @Override
    public JsonElement serialize(
        Condition condition,
        Type typeOfSrc,
        JsonSerializationContext context
    ) {
        if (condition == null) {
            return null;
        }

        JsonObject jsonObject = new JsonObject();

        if (condition.getDirection() != null) {
            jsonObject.addProperty(
                "direction",
                condition.getDirection().name()
            );
        }

        if (condition.getBlockDataString() != null) {
            jsonObject.addProperty(
                "blockDataString",
                condition.getBlockDataString()
            );
        }

        return jsonObject;
    }
}
