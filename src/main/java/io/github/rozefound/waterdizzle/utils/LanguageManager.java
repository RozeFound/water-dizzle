package io.github.rozefound.waterdizzle.utils;

import io.github.rozefound.waterdizzle.WaterDizzle;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * Manages language translations and message formatting for the plugin
 * Supports multiple language files in the /lang/ directory
 */
public class LanguageManager {

    private final WaterDizzle plugin;
    private final MiniMessage miniMessage;
    private FileConfiguration langConfig;
    private FileConfiguration defaultLangConfig;
    private File langFile;
    private String currentLanguage;

    private static final String DEFAULT_LANGUAGE = "en_us";
    private static final String LANG_FOLDER = "lang";

    private final Map<String, String> messageCache = new HashMap<>();

    public LanguageManager(WaterDizzle plugin) {
        this.plugin = plugin;
        this.miniMessage = MiniMessage.miniMessage();
        loadLanguageFile();
    }

    private void loadLanguageFile() {
        currentLanguage = plugin
            .getConfig()
            .getString("language", DEFAULT_LANGUAGE);

        File langFolder = new File(plugin.getDataFolder(), LANG_FOLDER);
        if (!langFolder.exists()) {
            langFolder.mkdirs();
        }

        loadDefaultLanguage();

        langFile = new File(langFolder, currentLanguage + ".yml");

        extractDefaultLanguageFiles();

        if (!langFile.exists()) {
            String resourcePath = LANG_FOLDER + "/" + currentLanguage + ".yml";
            if (plugin.getResource(resourcePath) != null) {
                plugin.saveResource(resourcePath, false);
                plugin
                    .getLogger()
                    .info("Created language file: " + currentLanguage + ".yml");
            } else {
                plugin
                    .getLogger()
                    .warning(
                        "Language file not found: " +
                        currentLanguage +
                        ".yml, falling back to " +
                        DEFAULT_LANGUAGE
                    );
                currentLanguage = DEFAULT_LANGUAGE;
                langFile = new File(langFolder, currentLanguage + ".yml");

                if (!langFile.exists()) {
                    plugin.saveResource(
                        LANG_FOLDER + "/" + DEFAULT_LANGUAGE + ".yml",
                        false
                    );
                }
            }
        }

        langConfig = YamlConfiguration.loadConfiguration(langFile);

        if (defaultLangConfig != null) {
            langConfig.setDefaults(defaultLangConfig);
        }

        updateLanguageFile();

        messageCache.clear();

        plugin.getLogger().info("Loaded language: " + currentLanguage);
    }

    private void loadDefaultLanguage() {
        String defaultResourcePath =
            LANG_FOLDER + "/" + DEFAULT_LANGUAGE + ".yml";
        InputStream defConfigStream = plugin.getResource(defaultResourcePath);

        if (defConfigStream != null) {
            defaultLangConfig = YamlConfiguration.loadConfiguration(
                new InputStreamReader(defConfigStream, StandardCharsets.UTF_8)
            );
        }
    }

    private void extractDefaultLanguageFiles() {
        File langFolder = new File(plugin.getDataFolder(), LANG_FOLDER);
        File langFile = new File(langFolder, DEFAULT_LANGUAGE + ".yml");
        String resourcePath = LANG_FOLDER + "/" + DEFAULT_LANGUAGE + ".yml";

        if (!langFile.exists() && plugin.getResource(resourcePath) != null) {
            plugin.saveResource(resourcePath, false);
            plugin
                .getLogger()
                .info(
                    "Extracted default language file: " +
                    DEFAULT_LANGUAGE +
                    ".yml"
                );
        }
    }

    private void updateLanguageFile() {
        if (defaultLangConfig == null) {
            return;
        }

        boolean needsUpdate = false;

        for (String key : defaultLangConfig.getKeys(true)) {
            if (!langConfig.contains(key)) {
                needsUpdate = true;
                langConfig.set(key, defaultLangConfig.get(key));
                plugin.getLogger().info("Added missing language key: " + key);
            }
        }

        if (needsUpdate) {
            try {
                langConfig.save(langFile);
                plugin
                    .getLogger()
                    .info(
                        "Updated " + currentLanguage + ".yml with missing keys"
                    );
            } catch (IOException e) {
                plugin
                    .getLogger()
                    .log(Level.SEVERE, "Could not update language file", e);
            }
        }
    }

    public String getCurrentLanguage() {
        return currentLanguage;
    }

    public String[] getAvailableLanguages() {
        File langFolder = new File(plugin.getDataFolder(), LANG_FOLDER);
        if (!langFolder.exists() || !langFolder.isDirectory()) {
            return new String[] { DEFAULT_LANGUAGE };
        }

        File[] files = langFolder.listFiles((dir, name) ->
            name.endsWith(".yml")
        );
        if (files == null || files.length == 0) {
            return new String[] { DEFAULT_LANGUAGE };
        }

        String[] languages = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            String filename = files[i].getName();
            languages[i] = filename.substring(0, filename.length() - 4);
        }

        return languages;
    }

    public void reload() {
        loadLanguageFile();
    }

    public String getRawMessage(String key) {
        if (messageCache.containsKey(key)) {
            return messageCache.get(key);
        }

        String message = langConfig.getString(key);

        if (message == null) {
            if (langConfig.getDefaults() != null) {
                message = langConfig.getDefaults().getString(key);
            }

            if (message == null) {
                plugin
                    .getLogger()
                    .warning(
                        "Missing language key in " +
                        currentLanguage +
                        ": " +
                        key
                    );
                message = key;
            }
        }

        messageCache.put(key, message);

        return message;
    }

    public String getMessage(String key, Map<String, String> placeholders) {
        String message = getRawMessage(key);

        if (placeholders != null) {
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                message = message.replace(
                    "{" + entry.getKey() + "}",
                    entry.getValue()
                );
            }
        }

        return message;
    }

    public String getMessage(String key, String placeholder, String value) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put(placeholder, value);
        return getMessage(key, placeholders);
    }

    public String getMessage(String key) {
        return getMessage(key, null);
    }

    public Component getComponent(
        String key,
        Map<String, String> placeholders
    ) {
        String message = getMessage(key, placeholders);

        if (placeholders != null && !placeholders.isEmpty()) {
            TagResolver.Builder builder = TagResolver.builder();
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                builder.resolver(
                    Placeholder.unparsed(entry.getKey(), entry.getValue())
                );
            }
            return miniMessage.deserialize(message, builder.build());
        }

        return miniMessage.deserialize(message);
    }

    public Component getComponent(
        String key,
        String placeholder,
        String value
    ) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put(placeholder, value);
        return getComponent(key, placeholders);
    }

    public Component getComponent(String key) {
        return getComponent(key, null);
    }

    public Component parseMiniMessage(
        String miniMessageString,
        Map<String, String> placeholders
    ) {
        if (placeholders != null && !placeholders.isEmpty()) {
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                miniMessageString = miniMessageString.replace(
                    "{" + entry.getKey() + "}",
                    entry.getValue()
                );
            }

            TagResolver.Builder builder = TagResolver.builder();
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                builder.resolver(
                    Placeholder.unparsed(entry.getKey(), entry.getValue())
                );
            }
            return miniMessage.deserialize(miniMessageString, builder.build());
        }

        return miniMessage.deserialize(miniMessageString);
    }

    public Component parseMiniMessage(String miniMessageString) {
        return parseMiniMessage(miniMessageString, null);
    }

    public Component getErrorMessage(String message) {
        String errorSymbol = getRawMessage("symbols.error");
        String formatted =
            "<gradient:#E74C3C:#C0392B>" +
            errorSymbol +
            "</gradient> <red>" +
            message +
            "</red>";
        return miniMessage.deserialize(formatted);
    }

    public Component getSuccessMessage(String message) {
        String successSymbol = getRawMessage("symbols.success");
        String formatted =
            "<gradient:#2ECC71:#27AE60>" +
            successSymbol +
            "</gradient> <green>" +
            message +
            "</green>";
        return miniMessage.deserialize(formatted);
    }

    public Component getInfoMessage(String message) {
        String infoSymbol = getRawMessage("symbols.info");
        String formatted =
            "<gradient:#F1C40F:#F39C12>" +
            infoSymbol +
            "</gradient> <yellow>" +
            message +
            "</yellow>";
        return miniMessage.deserialize(formatted);
    }

    public Component getWarningMessage(String message) {
        String warningSymbol = getRawMessage("symbols.warning");
        String formatted =
            "<gradient:#E67E22:#D68910>" +
            warningSymbol +
            "</gradient> <gold>" +
            message +
            "</gold>";
        return miniMessage.deserialize(formatted);
    }

    public static Map<String, String> placeholders(String... keysAndValues) {
        if (keysAndValues.length % 2 != 0) {
            throw new IllegalArgumentException(
                "Must provide an even number of arguments (key-value pairs)"
            );
        }

        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < keysAndValues.length; i += 2) {
            map.put(keysAndValues[i], keysAndValues[i + 1]);
        }
        return map;
    }

    public void save() {
        try {
            langConfig.save(langFile);
        } catch (IOException e) {
            plugin
                .getLogger()
                .log(Level.SEVERE, "Could not save language file", e);
        }
    }

    public boolean hasMessage(String key) {
        return langConfig.contains(key);
    }

    public MiniMessage getMiniMessage() {
        return miniMessage;
    }
}
