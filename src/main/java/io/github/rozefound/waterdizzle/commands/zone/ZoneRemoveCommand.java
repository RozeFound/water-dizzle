package io.github.rozefound.waterdizzle.commands.zone;

import io.github.rozefound.waterdizzle.WaterDizzle;
import io.github.rozefound.waterdizzle.Zone;
import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;

/**
 * Command for removing existing zones
 */
public class ZoneRemoveCommand extends AbstractZoneSubCommand {

    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    public ZoneRemoveCommand(WaterDizzle plugin) {
        super(
            plugin,
            "remove",
            "waterdizzle.zone.remove",
            "/zone remove <name>",
            "Remove an existing zone",
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
                    .getMessage("commands.zone.remove.specify-name")
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
                        "commands.zone.remove.not-found",
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

        String worldName = zone.getWorld().getName();
        int minX = (int) zone.getBounds().getMinX();
        int minY = (int) zone.getBounds().getMinY();
        int minZ = (int) zone.getBounds().getMinZ();
        int maxX = (int) zone.getBounds().getMaxX();
        int maxY = (int) zone.getBounds().getMaxY();
        int maxZ = (int) zone.getBounds().getMaxZ();

        plugin.getZoneManager().removeZone(zoneName);

        String removalMessage =
            "\n<gradient:#E74C3C:#C0392B>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>\n" +
            "<gradient:#E74C3C:#C0392B>        <bold>" +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.remove.zone-removed-title") +
            "</bold></gradient>\n" +
            "<gradient:#E74C3C:#C0392B>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>\n\n" +
            "<gradient:#E74C3C:#E67E22>" +
            plugin.getLanguageManager().getMessage("symbols.zap") +
            " <bold>" +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.remove.removed-zone") +
            "</bold></gradient> <yellow><bold>" +
            zoneName +
            "</bold></yellow>\n" +
            "<gradient:#E74C3C:#E67E22>" +
            plugin.getLanguageManager().getMessage("symbols.zap") +
            " <bold>" +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.create.world-name") +
            "</bold></gradient> <aqua>" +
            worldName +
            "</aqua>\n" +
            "<gradient:#E74C3C:#E67E22>" +
            plugin.getLanguageManager().getMessage("symbols.zap") +
            " <bold>" +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.remove.location") +
            "</bold></gradient> <white>" +
            String.format(
                "(%d, %d, %d) to (%d, %d, %d)",
                minX,
                minY,
                minZ,
                maxX,
                maxY,
                maxZ
            ) +
            "</white>\n\n" +
            "<dark_red><italic>" +
            plugin.getLanguageManager().getMessage("symbols.warning") +
            " " +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.remove.cannot-undo") +
            "</italic></dark_red>\n\n" +
            "<gradient:#E74C3C:#C0392B>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>\n\n" +
            "<green>" +
            plugin.getLanguageManager().getMessage("symbols.checkmark") +
            " " +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.remove.removed", "name", zoneName) +
            "</green>\n";

        sender.sendMessage(miniMessage.deserialize(removalMessage));

        if (!plugin.getZoneManager().getZones().isEmpty()) {
            String remainingMessage =
                "<gradient:#9B59B6:#27AE60>" +
                plugin.getLanguageManager().getMessage("symbols.bulb") +
                " <bold>" +
                plugin
                    .getLanguageManager()
                    .getMessage("commands.zone.condition.tip") +
                "</bold></gradient> " +
                "<click:suggest_command:'/zone list'>" +
                "<hover:show_text:'<gray>" +
                plugin
                    .getLanguageManager()
                    .getMessage("commands.zone.info.click-visualize")
                    .replace("visualize this zone", "see remaining zones") +
                "</gray>'>" +
                "<aqua>" +
                plugin
                    .getLanguageManager()
                    .getMessage("commands.zone.remove.see-remaining") +
                "</aqua>" +
                "</hover></click>";
            sender.sendMessage(miniMessage.deserialize(remainingMessage));
        } else {
            String noZonesMessage =
                "<gradient:#95A5A6:#7F8C8D>" +
                plugin.getLanguageManager().getMessage("symbols.info") +
                " " +
                plugin
                    .getLanguageManager()
                    .getMessage("commands.zone.remove.no-zones-remaining") +
                "</gradient>";
            sender.sendMessage(miniMessage.deserialize(noZonesMessage));
        }

        return true;
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
