package io.github.rozefound.waterdizzle.serialization;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import java.lang.reflect.Type;
import org.bukkit.NamespacedKey;
import org.bukkit.damage.DamageType;

public class DamageTypeDeserializer implements JsonDeserializer<DamageType> {

    @Override
    public DamageType deserialize(
        JsonElement json,
        Type typeOfT,
        JsonDeserializationContext context
    ) throws JsonParseException {
        String key = json.getAsString();
        try {
            if (key != null) {
                DamageType damageType = getDamageType(key);
                if (damageType != null) {
                    return damageType;
                }
            }
        } catch (Exception e) {
            // Fall through to default
        }
        return DamageType.MAGIC; // fallback
    }

    private DamageType getDamageType(String name) {
        try {
            NamespacedKey key = NamespacedKey.minecraft(name);
            return RegistryAccess.registryAccess()
                .getRegistry(RegistryKey.DAMAGE_TYPE)
                .get(key);
        } catch (Exception e) {
            return null;
        }
    }
}
