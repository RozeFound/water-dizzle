package io.github.rozefound.waterdizzle.commands.zone;

import io.github.rozefound.waterdizzle.WaterDizzle;
import io.github.rozefound.waterdizzle.Zone;
import java.util.List;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;

/**
 * Command for listing all configured zones
 */
public class ZoneListCommand extends AbstractZoneSubCommand {

    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    public ZoneListCommand(WaterDizzle plugin) {
        super(
            plugin,
            "list",
            "waterdizzle.zone.list",
            "/zone list",
            "List all configured zones",
            false
        );
    }

    @Override
    protected boolean executeCommand(CommandSender sender, String[] args) {
        List<Zone> zones = plugin.getZoneManager().getZones();
        if (zones.isEmpty()) {
            String emptyMessage =
                "\n<gradient:#95A5A6:#7F8C8D>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>\n" +
                "<gradient:#95A5A6:#7F8C8D>        <bold>" +
                plugin
                    .getLanguageManager()
                    .getMessage("commands.zone.list.no-zones-title") +
                "</bold></gradient>\n" +
                "<gradient:#95A5A6:#7F8C8D>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>\n\n" +
                "<gray>" +
                plugin
                    .getLanguageManager()
                    .getMessage("commands.zone.list.empty") +
                "</gray>\n\n" +
                "<gradient:#9B59B6:#27AE60>" +
                plugin.getLanguageManager().getMessage("symbols.bulb") +
                " <bold>" +
                plugin
                    .getLanguageManager()
                    .getMessage("commands.zone.list.get-started") +
                "</bold></gradient>\n" +
                "<click:suggest_command:'/zone select new '><hover:show_text:'<gray>" +
                plugin
                    .getLanguageManager()
                    .getMessage("commands.zone.list.set-anchors-hint") +
                "</gray>'>" +
                "<aqua>" +
                plugin
                    .getLanguageManager()
                    .getMessage("commands.zone.list.step1") +
                "</aqua></hover></click>\n" +
                "<click:suggest_command:'/zone create '><hover:show_text:'<gray>" +
                plugin
                    .getLanguageManager()
                    .getMessage("commands.zone.list.create-zone-from-anchors") +
                "</gray>'>" +
                "<aqua>" +
                plugin
                    .getLanguageManager()
                    .getMessage("commands.zone.list.step2") +
                "</aqua></hover></click>\n";
            sender.sendMessage(miniMessage.deserialize(emptyMessage));
            return true;
        }

        // Send styled header
        String headerMessage =
            "\n<gradient:#3498DB:#2980B9>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>\n" +
            "<gradient:#3498DB:#2980B9>         <bold>" +
            plugin
                .getLanguageManager()
                .getMessage(
                    "commands.zone.list.title",
                    "count",
                    String.valueOf(zones.size())
                ) +
            "</bold></gradient>\n" +
            "<gradient:#3498DB:#2980B9>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>\n";

        sender.sendMessage(miniMessage.deserialize(headerMessage));

        for (int i = 0; i < zones.size(); i++) {
            Zone zone = zones.get(i);

            String worldName = zone.getWorld() != null
                ? zone.getWorld().getName()
                : "Unknown";
            String worldColor = zone.getWorld() != null ? "aqua" : "red";

            String zoneInfo =
                "\n<gradient:#E67E22:#D68910><bold>" +
                (i + 1) +
                ".</bold></gradient> " +
                "<click:suggest_command:'/zone info " +
                zone.getName() +
                "'>" +
                "<hover:show_text:'<gray>" +
                plugin
                    .getLanguageManager()
                    .getMessage("commands.zone.list.click-view-details") +
                "</gray>'>" +
                "<yellow><bold>" +
                zone.getName() +
                "</bold></yellow></hover></click> " +
                "<dark_gray>-</dark_gray> <gray>" +
                plugin
                    .getLanguageManager()
                    .getMessage("commands.zone.create.world-name") +
                "</gray> <" +
                worldColor +
                ">" +
                worldName +
                "</" +
                worldColor +
                ">";

            sender.sendMessage(miniMessage.deserialize(zoneInfo));

            String locationDetails =
                "   <gray>" +
                plugin
                    .getLanguageManager()
                    .getMessage(
                        "commands.zone.list.location-details",
                        "coords",
                        String.format(
                            "(%d, %d, %d) to (%d, %d, %d)",
                            (int) zone.getBounds().getMinX(),
                            (int) zone.getBounds().getMinY(),
                            (int) zone.getBounds().getMinZ(),
                            (int) zone.getBounds().getMaxX(),
                            (int) zone.getBounds().getMaxY(),
                            (int) zone.getBounds().getMaxZ()
                        )
                    ) +
                "</gray>";

            sender.sendMessage(miniMessage.deserialize(locationDetails));

            if (zone.getDamageType() != null) {
                String damageInfo =
                    "   <gray>" +
                    plugin
                        .getLanguageManager()
                        .getMessage(
                            "commands.zone.list.damage-type-details",
                            "type",
                            "<red>" +
                            zone.getDamageType().key().value() +
                            "</red>"
                        ) +
                    "</gray>";
                sender.sendMessage(miniMessage.deserialize(damageInfo));
            }

            if (zone.getDamageInterval() > 0) {
                String intervalFormatted = String.format(
                    "%d ticks (%.1f seconds)",
                    zone.getDamageInterval(),
                    zone.getDamageInterval() / 20.0
                );
                String ticksInfo =
                    "   <gray>" +
                    plugin
                        .getLanguageManager()
                        .getMessage(
                            "commands.zone.list.damage-interval-details",
                            "interval",
                            "<gold>" + intervalFormatted + "</gold>"
                        ) +
                    "</gray>";
                sender.sendMessage(miniMessage.deserialize(ticksInfo));
            }

            String statusInfo = zone.isEnabled()
                ? "   " +
                plugin
                    .getLanguageManager()
                    .getMessage("commands.zone.list.status-enabled")
                : "   " +
                plugin
                    .getLanguageManager()
                    .getMessage("commands.zone.list.status-disabled");
            sender.sendMessage(miniMessage.deserialize(statusInfo));
        }

        String footerMessage =
            "\n<gradient:#3498DB:#2980B9>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>\n\n" +
            "<gradient:#9B59B6:#27AE60>" +
            plugin.getLanguageManager().getMessage("symbols.bulb") +
            " <bold>" +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.list.quick-actions") +
            "</bold></gradient>\n" +
            "<click:suggest_command:'/zone info '><hover:show_text:'<gray>" +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.list.view-zone-details") +
            "</gray>'>" +
            "<aqua>• /zone info <name> - " +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.list.view-details") +
            "</aqua></hover></click>\n" +
            "<click:suggest_command:'/zone edit '><hover:show_text:'<gray>" +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.list.edit-zone-properties") +
            "</gray>'>" +
            "<aqua>• /zone edit <name> - " +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.list.edit-properties") +
            "</aqua></hover></click>\n" +
            "<click:suggest_command:'/zone show '><hover:show_text:'<gray>" +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.list.visualize-with-particles") +
            "</gray>'>" +
            "<aqua>• /zone show <name> - " +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.list.visualize-zone") +
            "</aqua></hover></click>\n";

        sender.sendMessage(miniMessage.deserialize(footerMessage));

        return true;
    }
}
