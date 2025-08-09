package io.github.rozefound.waterdizzle.commands.zone;

import io.github.rozefound.waterdizzle.WaterDizzle;
import io.github.rozefound.waterdizzle.Zone;
import io.github.rozefound.waterdizzle.utils.Condition;
import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;

/**
 * Command for displaying detailed information about a zone
 */
public class ZoneInfoCommand extends AbstractZoneSubCommand {

    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    public ZoneInfoCommand(WaterDizzle plugin) {
        super(
            plugin,
            "info",
            "waterdizzle.zone.info",
            "/zone info <name>",
            "Display detailed information about a zone",
            false
        );
    }

    @Override
    protected boolean executeCommand(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sendError(sender, "Usage: " + getUsage());
            sendInfo(
                sender,
                plugin
                    .getLanguageManager()
                    .getMessage("commands.zone.info.specify-zone")
            );
            return true;
        }

        String zoneName = args[0];
        Zone zone = plugin.getZoneManager().getZone(zoneName);

        if (zone == null) {
            sendError(
                sender,
                plugin
                    .getLanguageManager()
                    .getMessage(
                        "commands.zone.info.not-found",
                        "name",
                        zoneName
                    )
            );
            sendInfo(
                sender,
                plugin
                    .getLanguageManager()
                    .getMessage("commands.zone.edit.see-all-zones")
            );
            return true;
        }

        displayZoneInfo(sender, zone);

        return true;
    }

    private void displayZoneInfo(CommandSender sender, Zone zone) {
        String header =
            "\n<gradient:#3498DB:#2980B9>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>\n" +
            "<gradient:#3498DB:#2980B9>        <bold>" +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.info.zone-info-title") +
            "</bold></gradient>\n" +
            "<gradient:#3498DB:#2980B9>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>\n";
        sender.sendMessage(miniMessage.deserialize(header));

        String basicInfo =
            "<gradient:#9B59B6:#27AE60>" +
            plugin.getLanguageManager().getMessage("symbols.zap") +
            " <bold>" +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.info.zone-name") +
            "</bold></gradient> <yellow><bold>" +
            zone.getName() +
            "</bold></yellow>\n" +
            "<gradient:#9B59B6:#27AE60>" +
            plugin.getLanguageManager().getMessage("symbols.zap") +
            " <bold>" +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.info.status") +
            "</bold></gradient> " +
            (zone.isEnabled()
                    ? "<green>" +
                    plugin
                        .getLanguageManager()
                        .getMessage("commands.zone.info.enabled") +
                    "</green>"
                    : "<red>" +
                    plugin
                        .getLanguageManager()
                        .getMessage("commands.zone.info.disabled") +
                    "</red>") +
            "\n" +
            "<gradient:#9B59B6:#27AE60>" +
            plugin.getLanguageManager().getMessage("symbols.zap") +
            " <bold>" +
            plugin.getLanguageManager().getMessage("commands.zone.info.world") +
            "</bold></gradient> <aqua>" +
            (zone.getWorld() != null
                    ? zone.getWorld().getName()
                    : "<red>" +
                    plugin
                        .getLanguageManager()
                        .getMessage("commands.zone.info.unloaded") +
                    "</red>") +
            "</aqua>\n";
        sender.sendMessage(miniMessage.deserialize(basicInfo));

        String locationInfo =
            "<gradient:#9B59B6:#27AE60>" +
            plugin.getLanguageManager().getMessage("symbols.zap") +
            " <bold>" +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.info.boundaries") +
            "</bold></gradient>\n" +
            "  <gray>" +
            plugin.getLanguageManager().getMessage("commands.zone.info.min") +
            "</gray> <white>(" +
            (int) zone.getBounds().getMinX() +
            ", " +
            (int) zone.getBounds().getMinY() +
            ", " +
            (int) zone.getBounds().getMinZ() +
            ")</white>\n" +
            "  <gray>" +
            plugin.getLanguageManager().getMessage("commands.zone.info.max") +
            "</gray> <white>(" +
            (int) zone.getBounds().getMaxX() +
            ", " +
            (int) zone.getBounds().getMaxY() +
            ", " +
            (int) zone.getBounds().getMaxZ() +
            ")</white>\n";
        sender.sendMessage(miniMessage.deserialize(locationInfo));

        int width =
            (int) (zone.getBounds().getMaxX() - zone.getBounds().getMinX()) + 1;
        int height =
            (int) (zone.getBounds().getMaxY() - zone.getBounds().getMinY()) + 1;
        int length =
            (int) (zone.getBounds().getMaxZ() - zone.getBounds().getMinZ()) + 1;
        int volume = width * height * length;

        String dimensionInfo =
            "<gradient:#9B59B6:#27AE60>" +
            plugin.getLanguageManager().getMessage("symbols.zap") +
            " <bold>" +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.info.dimensions") +
            "</bold></gradient> <gold>" +
            plugin
                .getLanguageManager()
                .getMessage(
                    "commands.zone.info.volume-format",
                    io.github.rozefound.waterdizzle.utils.LanguageManager.placeholders(
                        "width",
                        String.valueOf(width),
                        "height",
                        String.valueOf(height),
                        "length",
                        String.valueOf(length),
                        "volume",
                        String.format("%,d", volume)
                    )
                ) +
            "</gold>\n";
        sender.sendMessage(miniMessage.deserialize(dimensionInfo));

        String damageInfo =
            "<gradient:#9B59B6:#27AE60>" +
            plugin.getLanguageManager().getMessage("symbols.zap") +
            " <bold>" +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.info.damage-settings") +
            "</bold></gradient>\n" +
            "  <gray>" +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.info.damage-type") +
            "</gray> <yellow>" +
            zone.getDamageType().key().value() +
            "</yellow>\n" +
            "  <gray>" +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.info.damage-amount") +
            "</gray> <red>" +
            zone.getDamageAmount() +
            " " +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.info.hearts") +
            "</red>\n" +
            "  <gray>" +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.info.damage-interval") +
            "</gray> <aqua>" +
            zone.getDamageInterval() +
            " ticks" +
            (zone.getDamageInterval() > 0
                    ? " (" +
                    String.format("%.1f", zone.getDamageInterval() / 20.0) +
                    " seconds)"
                    : "") +
            "</aqua>\n";
        sender.sendMessage(miniMessage.deserialize(damageInfo));

        String targetInfo =
            "<gradient:#9B59B6:#27AE60>" +
            plugin.getLanguageManager().getMessage("symbols.zap") +
            " <bold>" +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.info.target-settings") +
            "</bold></gradient>\n" +
            "  <gray>" +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.info.can-damage-players") +
            "</gray> " +
            formatBoolean(zone.damagePlayer()) +
            "\n" +
            "  <gray>" +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.info.can-damage-animals") +
            "</gray> " +
            formatBoolean(zone.damageAnimal()) +
            "\n" +
            "  <gray>" +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.info.can-damage-entities") +
            "</gray> " +
            formatBoolean(zone.damageEntity()) +
            "\n" +
            "  <gray>" +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.info.can-destroy-items") +
            "</gray> " +
            formatBoolean(zone.destroyItem()) +
            "\n";
        sender.sendMessage(miniMessage.deserialize(targetInfo));

        if (zone.getDeathMessage() != null) {
            String deathMessageInfo =
                "<gradient:#9B59B6:#27AE60>" +
                plugin.getLanguageManager().getMessage("symbols.zap") +
                " <bold>" +
                plugin
                    .getLanguageManager()
                    .getMessage("commands.zone.info.death-message") +
                "</bold></gradient> <gray>\"" +
                net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText().serialize(
                    miniMessage.deserialize(
                        zone.getDeathMessage().replace("{player}", "<player>")
                    )
                ) +
                "\"</gray>\n";
            sender.sendMessage(miniMessage.deserialize(deathMessageInfo));
        }

        if (!zone.getConditions().isEmpty()) {
            String conditionsHeader =
                "<gradient:#9B59B6:#27AE60>" +
                plugin.getLanguageManager().getMessage("symbols.zap") +
                " <bold>" +
                plugin
                    .getLanguageManager()
                    .getMessage("commands.zone.info.conditions") +
                "</bold></gradient> <yellow>" +
                plugin
                    .getLanguageManager()
                    .getMessage(
                        "commands.zone.info.conditions-active",
                        "count",
                        String.valueOf(zone.getConditions().size())
                    ) +
                "</yellow>\n";
            sender.sendMessage(miniMessage.deserialize(conditionsHeader));

            int index = 1;
            for (Condition condition : zone.getConditions()) {
                String conditionInfo =
                    "  <gray>" +
                    index +
                    ".</gray> <aqua>" +
                    formatCondition(condition) +
                    "</aqua>\n";
                sender.sendMessage(miniMessage.deserialize(conditionInfo));
                index++;
            }
        } else {
            String noConditions =
                "<gradient:#9B59B6:#27AE60>" +
                plugin.getLanguageManager().getMessage("symbols.zap") +
                " <bold>" +
                plugin
                    .getLanguageManager()
                    .getMessage("commands.zone.info.conditions") +
                "</bold></gradient> <gray>" +
                plugin
                    .getLanguageManager()
                    .getMessage("commands.zone.info.no-conditions") +
                "</gray>\n";
            sender.sendMessage(miniMessage.deserialize(noConditions));
        }

        String actions =
            "\n<gradient:#9B59B6:#27AE60>" +
            plugin.getLanguageManager().getMessage("symbols.bulb") +
            " <bold>" +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.info.quick-actions") +
            "</bold></gradient>\n" +
            "<click:suggest_command:'/zone edit " +
            zone.getName() +
            " '>" +
            "<hover:show_text:'<gray>" +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.info.click-edit") +
            "</gray>'>" +
            "<aqua>" +
            plugin.getLanguageManager().getMessage("symbols.arrow-right") +
            " " +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.info.edit-properties") +
            "</aqua></hover></click>\n" +
            "<click:suggest_command:'/zone select " +
            zone.getName() +
            "'>" +
            "<hover:show_text:'<gray>" +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.info.click-visualize") +
            "</gray>'>" +
            "<aqua>" +
            plugin.getLanguageManager().getMessage("symbols.arrow-right") +
            " " +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.info.visualize-particles") +
            "</aqua></hover></click>\n" +
            "<click:suggest_command:'/zone condition " +
            zone.getName() +
            " '>" +
            "<hover:show_text:'<gray>" +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.info.click-conditions") +
            "</gray>'>" +
            "<aqua>" +
            plugin.getLanguageManager().getMessage("symbols.arrow-right") +
            " " +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.info.manage-conditions") +
            "</aqua></hover></click>\n" +
            "<click:suggest_command:'/zone remove " +
            zone.getName() +
            "'>" +
            "<hover:show_text:'<gray>" +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.info.click-remove") +
            "</gray>'>" +
            "<red>" +
            plugin.getLanguageManager().getMessage("symbols.arrow-right") +
            " " +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.info.remove-zone") +
            "</red></hover></click>\n";
        sender.sendMessage(miniMessage.deserialize(actions));

        String footer =
            "<gradient:#3498DB:#2980B9>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>\n";
        sender.sendMessage(miniMessage.deserialize(footer));
    }

    private String formatBoolean(boolean value) {
        return value
            ? "<green>" +
            plugin.getLanguageManager().getMessage("symbols.checkmark") +
            " " +
            plugin.getLanguageManager().getMessage("commands.zone.info.yes") +
            "</green>"
            : "<red>" +
            plugin.getLanguageManager().getMessage("symbols.cross") +
            " " +
            plugin.getLanguageManager().getMessage("commands.zone.info.no") +
            "</red>";
    }

    private String formatCondition(Condition condition) {
        String direction = condition.getDirection() != null
            ? condition.getDirection().toString().toLowerCase()
            : "unknown";
        String material = condition.getBlockDataString() != null
            ? condition.getBlockDataString()
            : "unknown";
        return direction + " " + material;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            String partial = args[0].toLowerCase();
            for (Zone zone : plugin.getZoneManager().getZones()) {
                if (zone.getName().toLowerCase().startsWith(partial)) {
                    completions.add(zone.getName());
                }
            }
        }

        return completions;
    }
}
