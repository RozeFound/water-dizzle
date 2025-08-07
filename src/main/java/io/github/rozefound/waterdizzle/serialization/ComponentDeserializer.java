package io.github.rozefound.waterdizzle.serialization;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class ComponentDeserializer implements JsonDeserializer<Component> {

    private static final MiniMessage miniMessage = MiniMessage.miniMessage();

    @Override
    public Component deserialize(
        JsonElement json,
        Type typeOfT,
        JsonDeserializationContext context
    ) throws JsonParseException {
        return miniMessage.deserialize(json.getAsString());
    }
}
