package io.github.rozefound.waterdizzle.commands.zone;

import io.github.rozefound.waterdizzle.WaterDizzle;
import io.github.rozefound.waterdizzle.utils.LanguageManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Command for reloading plugin configuration and language files
 * Usage: /zone reload
 */
public class ZoneReloadCommand extends AbstractZoneSubCommand {

    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    public ZoneReloadCommand(WaterDizzle plugin) {
        super(
            plugin,
            "reload",
            "waterdizzle.zone.reload",
            "Reload configuration and language files",
            "/zone reload",
            false // Can be used from console
        );
    }

    @Override
    protected boolean executeCommand(CommandSender sender, String[] args) {
        LanguageManager languageManager = plugin.getLanguageManager();
        String oldLanguage = languageManager.getCurrentLanguage();

        String reloadingMessage = languageManager.getMessage(
            "commands.zone.reload.reloading"
        );
        sender.sendMessage(miniMessage.deserialize(reloadingMessage));

        try {
            int oldZoneCount = plugin.getZoneManager().getZoneCount();

            plugin.reload();

            String newLanguage = languageManager.getCurrentLanguage();
            int newZoneCount = plugin.getZoneManager().getZoneCount();

            String border = languageManager.getMessage(
                "commands.zone.reload.complete-border"
            );
            String header = languageManager.getMessage(
                "commands.zone.reload.complete-header"
            );
            String successMessage =
                "\n" +
                border +
                "\n" +
                "<gradient:#2ECC71:#27AE60>     <bold>" +
                header +
                "</bold></gradient>\n" +
                border +
                "\n\n" +
                languageManager.getMessage(
                    "commands.zone.reload.config-success"
                ) +
                "\n" +
                languageManager.getMessage(
                    "commands.zone.reload.language-loaded",
                    "language",
                    newLanguage
                ) +
                "\n" +
                languageManager.getMessage(
                    "commands.zone.reload.zones-loaded",
                    "count",
                    String.valueOf(newZoneCount)
                ) +
                "\n";

            if (!oldLanguage.equals(newLanguage)) {
                Map<String, String> langPlaceholders = new HashMap<>();
                langPlaceholders.put("old", oldLanguage);
                langPlaceholders.put("new", newLanguage);
                successMessage +=
                    languageManager.getMessage(
                        "commands.zone.reload.language-changed",
                        langPlaceholders
                    ) +
                    "\n";
            }

            if (oldZoneCount != newZoneCount) {
                int diff = newZoneCount - oldZoneCount;
                if (diff > 0) {
                    successMessage +=
                        languageManager.getMessage(
                            "commands.zone.reload.zones-added",
                            "count",
                            String.valueOf(diff)
                        ) +
                        "\n";
                } else if (diff < 0) {
                    successMessage +=
                        languageManager.getMessage(
                            "commands.zone.reload.zones-removed",
                            "count",
                            String.valueOf(-diff)
                        ) +
                        "\n";
                }
            }

            sender.sendMessage(miniMessage.deserialize(successMessage));

            Component localizedMessage = languageManager.getComponent(
                "general.config-reloaded"
            );
            sender.sendMessage(localizedMessage);

            if (sender instanceof Player) {
                plugin
                    .getLogger()
                    .info(
                        languageManager.getMessage(
                            "commands.zone.reload.console-log-success",
                            "player",
                            sender.getName()
                        )
                    );
            }

            return true;
        } catch (Exception e) {
            String errorBorder = languageManager.getMessage(
                "commands.zone.reload.failed-border"
            );
            String errorHeader = languageManager.getMessage(
                "commands.zone.reload.failed-header"
            );
            String errorMessage =
                "\n" +
                errorBorder +
                "\n" +
                "<gradient:#E74C3C:#C0392B>     <bold>" +
                errorHeader +
                "</bold></gradient>\n" +
                errorBorder +
                "\n\n" +
                languageManager.getMessage(
                    "commands.zone.reload.error-occurred"
                ) +
                "\n" +
                languageManager.getMessage(
                    "commands.zone.reload.error-details",
                    "error",
                    e.getMessage()
                ) +
                "\n\n" +
                languageManager.getMessage(
                    "commands.zone.reload.check-console"
                );

            sender.sendMessage(miniMessage.deserialize(errorMessage));

            plugin
                .getLogger()
                .severe(
                    languageManager.getMessage(
                        "commands.zone.reload.console-log-error",
                        "error",
                        e.getMessage()
                    )
                );
            e.printStackTrace();

            return false;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}
