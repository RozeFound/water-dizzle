package io.github.rozefound.waterdizzle.serialization;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.rozefound.waterdizzle.utils.Condition;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.damage.DamageType;

public final class GsonFactory {

    private GsonFactory() {
        // Utility class - prevent instantiation
    }

    public static Gson createGson() {
        return createGsonBuilder().create();
    }

    public static Gson createPrettyGson() {
        return createGsonBuilder().setPrettyPrinting().create();
    }

    public static GsonBuilder createGsonBuilder() {
        return new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .registerTypeAdapter(Component.class, new ComponentSerializer())
            .registerTypeAdapter(Component.class, new ComponentDeserializer())
            .registerTypeAdapter(DamageType.class, new DamageTypeSerializer())
            .registerTypeAdapter(DamageType.class, new DamageTypeDeserializer())
            .registerTypeAdapter(Location.class, new LocationSerializer())
            .registerTypeAdapter(Location.class, new LocationDeserializer())
            .registerTypeAdapter(Material.class, new MaterialSerializer())
            .registerTypeAdapter(Material.class, new MaterialDeserializer())
            .registerTypeAdapter(Condition.class, new ConditionSerializer())
            .registerTypeAdapter(Condition.class, new ConditionDeserializer())
            .serializeNulls();
    }

    public static Gson createCompactGson() {
        return new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .registerTypeAdapter(Component.class, new ComponentSerializer())
            .registerTypeAdapter(Component.class, new ComponentDeserializer())
            .registerTypeAdapter(DamageType.class, new DamageTypeSerializer())
            .registerTypeAdapter(DamageType.class, new DamageTypeDeserializer())
            .registerTypeAdapter(Location.class, new LocationSerializer())
            .registerTypeAdapter(Location.class, new LocationDeserializer())
            .registerTypeAdapter(Material.class, new MaterialSerializer())
            .registerTypeAdapter(Material.class, new MaterialDeserializer())
            .registerTypeAdapter(Condition.class, new ConditionSerializer())
            .registerTypeAdapter(Condition.class, new ConditionDeserializer())
            .create();
    }
}
