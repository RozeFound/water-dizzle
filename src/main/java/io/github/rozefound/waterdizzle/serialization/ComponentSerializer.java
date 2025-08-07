package io.github.rozefound.waterdizzle.serialization;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class ComponentSerializer implements JsonSerializer<Component> {

    private static final MiniMessage miniMessage = MiniMessage.miniMessage();

    @Override
    public JsonElement serialize(
        Component src,
        Type typeOfSrc,
        JsonSerializationContext context
    ) {
        String miniMessageFormat = miniMessage.serialize(src);
        return new JsonPrimitive(miniMessageFormat);
    }
}
