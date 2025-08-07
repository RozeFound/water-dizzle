package io.github.rozefound.waterdizzle.commands.zone;

import io.github.rozefound.waterdizzle.WaterDizzle;
import io.github.rozefound.waterdizzle.Zone;
import io.github.rozefound.waterdizzle.utils.SelectionManager;
import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Subcommand to enable selection mode for zone anchors
 * Can also enter zone editing mode when a zone name is provided
 */
public class ZoneSelectCommand extends AbstractZoneSubCommand {

    private final SelectionManager selectionManager;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    public ZoneSelectCommand(
        WaterDizzle plugin,
        SelectionManager selectionManager
    ) {
        super(
            plugin,
            "select",
            "waterdizzle.zone.select",
            "/zone select [zone_name | new <name>]",
            "Enter selection mode, edit a zone, or create a new zone",
            true
        );
        this.selectionManager = selectionManager;
    }

    @Override
    protected boolean executeCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("new")) {
                return handleNewZone(player, args);
            }

            String zoneName = args[0];
            Zone zone = plugin.getZoneManager().getZone(zoneName);

            if (zone == null) {
                sendError(player, "Zone '" + zoneName + "' does not exist!");
                sendInfo(player, "Use /zone list to see all available zones.");
                return true;
            }

            // Check if zone has a valid world
            if (zone.getWorld() == null) {
                sendError(
                    player,
                    "Zone '" + zoneName + "' is in an unloaded world!"
                );
                sendInfo(player, "The world must be loaded to edit the zone.");
                return true;
            }

            if (!player.hasPermission("waterdizzle.zone.edit")) {
                sendError(player, "You don't have permission to edit zones!");
                return true;
            }

            selectionManager.enableZoneEditingMode(player, zone);

            String editMessage =
                "\n<gradient:#F39C12:#E67E22>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>\n" +
                "<gradient:#F39C12:#E67E22>      <bold>" +
                plugin
                    .getLanguageManager()
                    .getMessage("commands.zone.selection.editing-mode-title") +
                "</bold></gradient>\n" +
                "<gradient:#F39C12:#E67E22>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>\n\n" +
                "<gradient:#9B59B6:#27AE60>" +
                plugin.getLanguageManager().getMessage("symbols.pencil") +
                " <bold>" +
                plugin
                    .getLanguageManager()
                    .getMessage("commands.zone.selection.editing-zone") +
                "</bold></gradient> <yellow><bold>" +
                zoneName +
                "</bold></yellow>\n" +
                "<gradient:#2ECC71:#27AE60>" +
                plugin.getLanguageManager().getMessage("symbols.success") +
                " " +
                plugin
                    .getLanguageManager()
                    .getMessage("commands.zone.selection.current-boundaries") +
                "</gradient>\n";

            player.sendMessage(miniMessage.deserialize(editMessage));

            int width = (int) (zone.getBounds().getMaxX() -
                zone.getBounds().getMinX());
            int height = (int) (zone.getBounds().getMaxY() -
                zone.getBounds().getMinY());
            int length = (int) (zone.getBounds().getMaxZ() -
                zone.getBounds().getMinZ());
            int volume = width * height * length;

            String sizeMessage =
                "<gradient:#9B59B6:#27AE60>" +
                plugin.getLanguageManager().getMessage("symbols.ruler") +
                " <bold>" +
                plugin
                    .getLanguageManager()
                    .getMessage("commands.zone.selection.current-size") +
                "</bold></gradient> <aqua>" +
                String.format("%d × %d × %d", width, height, length) +
                "</aqua>\n" +
                "<gradient:#9B59B6:#27AE60>" +
                plugin.getLanguageManager().getMessage("symbols.box") +
                " <bold>" +
                plugin
                    .getLanguageManager()
                    .getMessage("commands.zone.selection.volume") +
                "</bold></gradient> <gold>" +
                String.format("%,d blocks", volume) +
                "</gold>\n\n" +
                "<gradient:#F39C12:#E67E22>" +
                plugin.getLanguageManager().getMessage("symbols.lightning") +
                " <bold>" +
                plugin
                    .getLanguageManager()
                    .getMessage("commands.zone.selection.modify-boundaries") +
                "</bold></gradient>\n" +
                "<gradient:#3498DB:#2980B9>" +
                plugin.getLanguageManager().getMessage("symbols.bullet") +
                " <aqua>Left-click</aqua> <gray>- " +
                plugin
                    .getLanguageManager()
                    .getMessage("commands.zone.selection.set-first-corner") +
                "</gray></gradient>\n" +
                "<gradient:#3498DB:#2980B9>" +
                plugin.getLanguageManager().getMessage("symbols.bullet") +
                " <aqua>Right-click</aqua> <gray>- " +
                plugin
                    .getLanguageManager()
                    .getMessage("commands.zone.selection.set-second-corner") +
                "</gray></gradient>\n" +
                "<gradient:#3498DB:#2980B9>" +
                plugin.getLanguageManager().getMessage("symbols.bullet") +
                " <yellow>/zone deselect</yellow> <gray>- " +
                plugin
                    .getLanguageManager()
                    .getMessage("commands.zone.selection.save-exit-editing") +
                "</gray></gradient>\n";

            player.sendMessage(miniMessage.deserialize(sizeMessage));

            return true;
        }

        if (selectionManager.isInSelectionMode(player)) {
            Zone editingZone = selectionManager.getEditingZone(player);
            if (editingZone != null) {
                String alreadyEditingMessage =
                    "\n<gradient:#F39C12:#E67E22>" +
                    plugin.getLanguageManager().getMessage("symbols.warning") +
                    " <bold>" +
                    plugin
                        .getLanguageManager()
                        .getMessage("commands.zone.selection.already-editing") +
                    "</bold></gradient>\n" +
                    "<gray>" +
                    plugin
                        .getLanguageManager()
                        .getMessage(
                            "commands.zone.selection.already-editing-zone"
                        ) +
                    "</gray> <yellow><bold>" +
                    editingZone.getName() +
                    "</bold></yellow><gray>!</gray>\n\n" +
                    "<gradient:#9B59B6:#27AE60>" +
                    plugin.getLanguageManager().getMessage("symbols.bulb") +
                    " <bold>" +
                    plugin
                        .getLanguageManager()
                        .getMessage("commands.zone.condition.tip") +
                    "</bold></gradient> " +
                    "<click:suggest_command:'/zone deselect'><hover:show_text:'<gray>" +
                    plugin
                        .getLanguageManager()
                        .getMessage(
                            "commands.zone.selection.exit-editing-tip"
                        ) +
                    "</gray>'>" +
                    "<yellow>/zone deselect</yellow> <gray>" +
                    plugin
                        .getLanguageManager()
                        .getMessage(
                            "commands.zone.selection.exit-editing-tip"
                        ) +
                    "</gray></hover></click>\n";
                player.sendMessage(
                    miniMessage.deserialize(alreadyEditingMessage)
                );
            } else {
                String alreadySelectingMessage =
                    "\n<gradient:#F39C12:#E67E22>" +
                    plugin.getLanguageManager().getMessage("symbols.warning") +
                    " <bold>" +
                    plugin
                        .getLanguageManager()
                        .getMessage(
                            "commands.zone.selection.already-selecting"
                        ) +
                    "</bold></gradient>\n\n" +
                    "<gradient:#9B59B6:#27AE60>" +
                    plugin.getLanguageManager().getMessage("symbols.bulb") +
                    " <bold>" +
                    plugin
                        .getLanguageManager()
                        .getMessage("commands.zone.condition.tip") +
                    "</bold></gradient> " +
                    "<click:suggest_command:'/zone deselect'><hover:show_text:'<gray>" +
                    plugin
                        .getLanguageManager()
                        .getMessage(
                            "commands.zone.selection.exit-selection-tip"
                        ) +
                    "</gray>'>" +
                    "<yellow>/zone deselect</yellow> <gray>" +
                    plugin
                        .getLanguageManager()
                        .getMessage(
                            "commands.zone.selection.exit-selection-tip"
                        ) +
                    "</gray></hover></click>\n";
                player.sendMessage(
                    miniMessage.deserialize(alreadySelectingMessage)
                );
            }
            return true;
        }

        selectionManager.enableSelection(player);

        String selectionMessage =
            "\n<gradient:#2ECC71:#27AE60>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>\n" +
            "<gradient:#2ECC71:#27AE60>     <bold>" +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.selection.mode-enabled-title") +
            "</bold></gradient>\n" +
            "<gradient:#2ECC71:#27AE60>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>\n\n" +
            "<gradient:#3498DB:#2980B9>" +
            plugin.getLanguageManager().getMessage("symbols.pin") +
            " <bold>" +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.selection.set-corner-instructions") +
            "</bold></gradient>\n" +
            "<gradient:#9B59B6:#27AE60>" +
            plugin.getLanguageManager().getMessage("symbols.bullet") +
            " <aqua>Left-click</aqua> <gray>- " +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.selection.first-corner-click") +
            "</gray></gradient>\n" +
            "<gradient:#9B59B6:#27AE60>" +
            plugin.getLanguageManager().getMessage("symbols.bullet") +
            " <aqua>Right-click</aqua> <gray>- " +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.selection.second-corner-click") +
            "</gray></gradient>\n" +
            "<gradient:#F39C12:#E67E22>" +
            plugin.getLanguageManager().getMessage("symbols.bulb") +
            " <bold>Commands:</bold></gradient>\n" +
            "<click:suggest_command:'/zone select new '><hover:show_text:'<gray>" +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.selection.create-new-zone") +
            "</gray>'>" +
            "<yellow>/zone select new <name></yellow> <gray>- " +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.selection.create-new-zone") +
            "</gray></hover></click>\n" +
            "<click:suggest_command:'/zone deselect'><hover:show_text:'<gray>" +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.selection.exit-selection") +
            "</gray>'>" +
            "<yellow>/zone deselect</yellow> <gray>- " +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.selection.exit-selection") +
            "</gray></hover></click>\n";

        player.sendMessage(miniMessage.deserialize(selectionMessage));

        return true;
    }

    private boolean handleNewZone(Player player, String[] args) {
        if (args.length < 2) {
            sendError(player, "Usage: /zone select new <name>");
            sendInfo(player, "Please specify a name for the new zone.");
            return true;
        }

        String zoneName = args[1];

        if (zoneName.equalsIgnoreCase("new")) {
            String errorMessage =
                "\n<gradient:#E74C3C:#C0392B>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>\n" +
                "<gradient:#E74C3C:#C0392B>        <bold>" +
                plugin
                    .getLanguageManager()
                    .getMessage("commands.zone.create.reserved-name-title") +
                "</bold></gradient>\n" +
                "<gradient:#E74C3C:#C0392B>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>\n\n" +
                "<red>" +
                plugin.getLanguageManager().getMessage("symbols.error") +
                " " +
                plugin
                    .getLanguageManager()
                    .getMessage("commands.zone.create.reserved-name-error") +
                "</red>\n\n" +
                "<gradient:#9B59B6:#27AE60>" +
                plugin.getLanguageManager().getMessage("symbols.bulb") +
                " <bold>" +
                plugin
                    .getLanguageManager()
                    .getMessage("commands.zone.condition.tip") +
                "</bold></gradient> <yellow>" +
                plugin
                    .getLanguageManager()
                    .getMessage("commands.zone.create.reserved-name-tip") +
                "</yellow>\n";
            player.sendMessage(miniMessage.deserialize(errorMessage));
            return true;
        }

        // Check if zone name already exists
        if (plugin.getZoneManager().getZone(zoneName) != null) {
            String errorMessage =
                "\n<gradient:#E74C3C:#C0392B>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>\n" +
                "<gradient:#E74C3C:#C0392B>        <bold>" +
                plugin
                    .getLanguageManager()
                    .getMessage("commands.zone.create.name-taken-title") +
                "</bold></gradient>\n" +
                "<gradient:#E74C3C:#C0392B>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>\n\n" +
                "<red>" +
                plugin.getLanguageManager().getMessage("symbols.error") +
                " " +
                plugin
                    .getLanguageManager()
                    .getMessage(
                        "commands.zone.create.name-taken-error",
                        "name",
                        zoneName
                    ) +
                "</red>\n\n" +
                "<gradient:#9B59B6:#27AE60>" +
                plugin.getLanguageManager().getMessage("symbols.bulb") +
                " <bold>" +
                plugin
                    .getLanguageManager()
                    .getMessage("commands.zone.create.name-taken-options") +
                "</bold></gradient>\n" +
                "<yellow>" +
                plugin.getLanguageManager().getMessage("symbols.bullet") +
                " " +
                plugin
                    .getLanguageManager()
                    .getMessage("commands.zone.create.choose-different") +
                "</yellow>\n" +
                "<click:suggest_command:'/zone remove " +
                zoneName +
                "'><hover:show_text:'<gray>" +
                plugin
                    .getLanguageManager()
                    .getMessage("commands.zone.create.remove-existing") +
                "</gray>'>" +
                "<yellow>" +
                plugin.getLanguageManager().getMessage("symbols.bullet") +
                " " +
                plugin
                    .getLanguageManager()
                    .getMessage("commands.zone.create.remove-existing") +
                "</yellow></hover></click>\n";
            player.sendMessage(miniMessage.deserialize(errorMessage));
            return true;
        }

        Location[] anchors = null;
        if (selectionManager.hasCompleteSelection(player)) {
            anchors = selectionManager.getAnchors(player);
        }

        if (anchors == null || anchors[0] == null || anchors[1] == null) {
            String errorMessage =
                "\n<gradient:#E74C3C:#C0392B>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>\n" +
                "<gradient:#E74C3C:#C0392B>     <bold>" +
                plugin
                    .getLanguageManager()
                    .getMessage(
                        "commands.zone.selection.incomplete-selection"
                    ) +
                "</bold></gradient>\n" +
                "<gradient:#E74C3C:#C0392B>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>\n\n" +
                "<red>" +
                plugin.getLanguageManager().getMessage("symbols.error") +
                " " +
                plugin
                    .getLanguageManager()
                    .getMessage(
                        "commands.zone.selection.incomplete-selection-error"
                    ) +
                "</red>\n\n" +
                "<gradient:#9B59B6:#27AE60>" +
                plugin.getLanguageManager().getMessage("symbols.bulb") +
                " <bold>" +
                plugin
                    .getLanguageManager()
                    .getMessage("commands.zone.selection.set-corners-using") +
                "</bold></gradient>\n" +
                "<yellow>" +
                plugin.getLanguageManager().getMessage("symbols.bullet") +
                " Left-click</yellow> <gray>- " +
                plugin
                    .getLanguageManager()
                    .getMessage("commands.zone.selection.first-corner-click") +
                "</gray>\n" +
                "<yellow>" +
                plugin.getLanguageManager().getMessage("symbols.bullet") +
                " Right-click</yellow> <gray>- " +
                plugin
                    .getLanguageManager()
                    .getMessage("commands.zone.selection.second-corner-click") +
                "</gray>\n" +
                "<yellow>" +
                plugin.getLanguageManager().getMessage("symbols.bullet") +
                " Left-click</yellow> <gray>- " +
                plugin
                    .getLanguageManager()
                    .getMessage(
                        "commands.zone.selection.first-corner-position"
                    ) +
                "</gray>\n" +
                "<yellow>" +
                plugin.getLanguageManager().getMessage("symbols.bullet") +
                " Right-click</yellow> <gray>- " +
                plugin
                    .getLanguageManager()
                    .getMessage(
                        "commands.zone.selection.second-corner-position"
                    ) +
                "</gray>\n";
            player.sendMessage(miniMessage.deserialize(errorMessage));
            return true;
        }

        if (!anchors[0].getWorld().equals(anchors[1].getWorld())) {
            sendError(player, "Both corners must be in the same world!");
            return true;
        }

        Zone zone = new Zone(plugin, zoneName, anchors[0], anchors[1]);

        plugin.getZoneManager().addZone(zone);
        plugin.getZoneManager().saveZones();

        if (selectionManager.isInSelectionMode(player)) {
            selectionManager.disableSelection(player);
        }

        int width = (int) (zone.getBounds().getMaxX() -
            zone.getBounds().getMinX());
        int height = (int) (zone.getBounds().getMaxY() -
            zone.getBounds().getMinY());
        int length = (int) (zone.getBounds().getMaxZ() -
            zone.getBounds().getMinZ());
        int volume = width * height * length;

        String successMessage =
            "\n<gradient:#2ECC71:#27AE60>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>\n" +
            "<gradient:#2ECC71:#27AE60>        <bold>" +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.create.zone-created-title") +
            "</bold></gradient>\n" +
            "<gradient:#2ECC71:#27AE60>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>\n\n" +
            "<gradient:#9B59B6:#27AE60>" +
            plugin.getLanguageManager().getMessage("symbols.pencil") +
            " <bold>" +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.create.zone-name") +
            "</bold></gradient> <yellow><bold>" +
            zoneName +
            "</bold></yellow>\n" +
            "<gradient:#9B59B6:#27AE60>" +
            plugin.getLanguageManager().getMessage("symbols.world") +
            " <bold>" +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.create.world-name") +
            "</bold></gradient> <aqua>" +
            zone.getWorld().getName() +
            "</aqua>\n" +
            "<gradient:#9B59B6:#27AE60>" +
            plugin.getLanguageManager().getMessage("symbols.ruler") +
            " <bold>" +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.selection.size") +
            "</bold></gradient> <gold>" +
            String.format("%d × %d × %d", width, height, length) +
            "</gold>\n" +
            "<gradient:#9B59B6:#27AE60>" +
            plugin.getLanguageManager().getMessage("symbols.box") +
            " <bold>" +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.selection.volume") +
            "</bold></gradient> <gold>" +
            String.format("%,d blocks", volume) +
            "</gold>\n\n" +
            "<gradient:#F39C12:#E67E22>" +
            plugin.getLanguageManager().getMessage("symbols.bulb") +
            " <bold>" +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.create.next-steps") +
            "</bold></gradient>\n" +
            "<click:suggest_command:'/zone edit " +
            zoneName +
            " '><hover:show_text:'<gray>" +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.create.configure-properties") +
            "</gray>'>" +
            "<yellow>/zone edit " +
            zoneName +
            "</yellow> <gray>- " +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.create.configure-properties") +
            "</gray></hover></click>\n" +
            "<click:suggest_command:'/zone condition " +
            zoneName +
            " '><hover:show_text:'<gray>" +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.create.add-conditions") +
            "</gray>'>" +
            "<yellow>/zone condition " +
            zoneName +
            "</yellow> <gray>- " +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.create.add-conditions") +
            "</gray></hover></click>\n";

        player.sendMessage(miniMessage.deserialize(successMessage));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            String partial = args[0].toLowerCase();

            if ("new".startsWith(partial)) {
                completions.add("new");
            }

            if (sender.hasPermission("waterdizzle.zone.edit")) {
                for (Zone zone : plugin.getZoneManager().getZones()) {
                    if (zone.getName().toLowerCase().startsWith(partial)) {
                        completions.add(zone.getName());
                    }
                }
            }
        }

        return completions;
    }
}
