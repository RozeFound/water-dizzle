package io.github.rozefound.waterdizzle.commands.zone;

import io.github.rozefound.waterdizzle.WaterDizzle;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;

/**
 * Command for displaying plugin version information
 */
public class ZoneVersionCommand extends AbstractZoneSubCommand {

    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    public ZoneVersionCommand(WaterDizzle plugin) {
        super(
            plugin,
            "version",
            null,
            "/zone version",
            "Display plugin version information",
            false
        );
    }

    @Override
    protected boolean executeCommand(CommandSender sender, String[] args) {
        PluginDescriptionFile description = plugin.getDescription();
        String pluginName = description.getName();
        String version = description.getVersion();
        String apiVersion = description.getAPIVersion();

        String versionMessage =
            "\n<gradient:#3498DB:#2980B9>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>\n" +
            "<gradient:#3498DB:#2980B9>       <bold>" +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.version.title") +
            "</bold></gradient>\n" +
            "<gradient:#3498DB:#2980B9>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>\n\n" +
            "<gradient:#9B59B6:#27AE60>" +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.version.plugin-name") +
            ":</gradient> <yellow>" +
            pluginName +
            "</yellow>\n" +
            "<gradient:#9B59B6:#27AE60>" +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.version.version") +
            ":</gradient> <aqua>" +
            version +
            "</aqua>\n" +
            "<gradient:#9B59B6:#27AE60>" +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.version.api-version") +
            ":</gradient> <green>" +
            (apiVersion != null ? apiVersion : "1.21") +
            "</green>\n\n" +
            "<gradient:#3498DB:#2980B9>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>\n";

        sender.sendMessage(miniMessage.deserialize(versionMessage));
        return true;
    }
}
