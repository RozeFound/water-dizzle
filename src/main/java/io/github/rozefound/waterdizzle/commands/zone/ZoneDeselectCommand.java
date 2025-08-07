package io.github.rozefound.waterdizzle.commands.zone;

import io.github.rozefound.waterdizzle.WaterDizzle;
import io.github.rozefound.waterdizzle.Zone;
import io.github.rozefound.waterdizzle.utils.SelectionManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Subcommand to disable selection mode and clear zone anchors
 * Also handles exiting zone editing mode
 */
public class ZoneDeselectCommand extends AbstractZoneSubCommand {

    private final SelectionManager selectionManager;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    public ZoneDeselectCommand(
        WaterDizzle plugin,
        SelectionManager selectionManager
    ) {
        super(
            plugin,
            "deselect",
            "waterdizzle.zone.select",
            "/zone deselect",
            "Exit selection mode and clear anchor selections",
            true
        );
        this.selectionManager = selectionManager;
    }

    @Override
    protected boolean executeCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        if (!selectionManager.isInSelectionMode(player)) {
            String notInSelectionMessage =
                "\n<gradient:#F39C12:#E67E22>" +
                plugin.getLanguageManager().getMessage("symbols.warning") +
                " <bold>" +
                plugin
                    .getLanguageManager()
                    .getMessage(
                        "commands.zone.selection.not-in-selection-mode"
                    ) +
                "</bold></gradient>\n\n" +
                "<gradient:#9B59B6:#27AE60>" +
                plugin.getLanguageManager().getMessage("symbols.bulb") +
                " <bold>" +
                plugin
                    .getLanguageManager()
                    .getMessage("commands.zone.condition.tip") +
                "</bold></gradient> " +
                "<click:suggest_command:'/zone select'><hover:show_text:'<gray>" +
                plugin
                    .getLanguageManager()
                    .getMessage(
                        "commands.zone.selection.enter-selection-mode"
                    ) +
                "</gray>'>" +
                "<yellow>/zone select</yellow> <gray>" +
                plugin
                    .getLanguageManager()
                    .getMessage(
                        "commands.zone.selection.not-in-selection-mode-tip"
                    ) +
                "</gray></hover></click>\n";
            player.sendMessage(miniMessage.deserialize(notInSelectionMessage));
            return true;
        }

        Zone editingZone = selectionManager.getEditingZone(player);
        if (editingZone != null) {
            int width = (int) (editingZone.getBounds().getMaxX() -
                editingZone.getBounds().getMinX());
            int height = (int) (editingZone.getBounds().getMaxY() -
                editingZone.getBounds().getMinY());
            int length = (int) (editingZone.getBounds().getMaxZ() -
                editingZone.getBounds().getMinZ());
            int volume = width * height * length;

            selectionManager.disableSelection(player);

            String editCompleteMessage =
                "\n<gradient:#2ECC71:#27AE60>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>\n" +
                "<gradient:#2ECC71:#27AE60>    <bold>" +
                plugin
                    .getLanguageManager()
                    .getMessage(
                        "commands.zone.selection.editing-complete-title"
                    ) +
                "</bold></gradient>\n" +
                "<gradient:#2ECC71:#27AE60>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>\n\n" +
                "<gradient:#2ECC71:#27AE60>" +
                plugin.getLanguageManager().getMessage("symbols.success") +
                " " +
                plugin
                    .getLanguageManager()
                    .getMessage(
                        "commands.zone.selection.zone-boundaries-saved"
                    ) +
                "</gradient>\n\n" +
                "<gradient:#9B59B6:#27AE60>" +
                plugin.getLanguageManager().getMessage("symbols.pencil") +
                " <bold>" +
                plugin
                    .getLanguageManager()
                    .getMessage("commands.zone.create.zone-name") +
                "</bold></gradient> <yellow><bold>" +
                editingZone.getName() +
                "</bold></yellow>\n" +
                "<gradient:#9B59B6:#27AE60>" +
                plugin.getLanguageManager().getMessage("symbols.ruler") +
                " <bold>" +
                plugin
                    .getLanguageManager()
                    .getMessage("commands.zone.selection.final-size") +
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
                "</gold>\n";

            player.sendMessage(miniMessage.deserialize(editCompleteMessage));
            String saveMessage =
                "\n<gradient:#3498DB:#2980B9>" +
                plugin.getLanguageManager().getMessage("symbols.save") +
                " " +
                plugin
                    .getLanguageManager()
                    .getMessage("commands.zone.selection.all-changes-saved") +
                "</gradient>\n";
            player.sendMessage(miniMessage.deserialize(saveMessage));

            return true;
        }

        boolean hadFirstAnchor =
            selectionManager.getFirstAnchor(player) != null;
        boolean hadSecondAnchor =
            selectionManager.getSecondAnchor(player) != null;

        selectionManager.disableSelection(player);

        String exitMessage =
            "\n<gradient:#2ECC71:#27AE60>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>\n" +
            "<gradient:#2ECC71:#27AE60>    <bold>" +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.selection.mode-disabled-title") +
            "</bold></gradient>\n" +
            "<gradient:#2ECC71:#27AE60>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>\n\n";

        if (hadFirstAnchor || hadSecondAnchor) {
            exitMessage +=
                "<gradient:#95A5A6:#7F8C8D>" +
                plugin.getLanguageManager().getMessage("symbols.success") +
                " " +
                plugin
                    .getLanguageManager()
                    .getMessage("commands.zone.selection.selection-cleared") +
                "</gradient>\n";
        }

        exitMessage +=
            "\n<gradient:#9B59B6:#27AE60>" +
            plugin.getLanguageManager().getMessage("symbols.bulb") +
            " <bold>" +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.selection.quick-actions") +
            "</bold></gradient>\n" +
            "<click:suggest_command:'/zone select'><hover:show_text:'<gray>" +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.selection.enter-selection-again") +
            "</gray>'>" +
            "<yellow>/zone select</yellow> <gray>- " +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.selection.enter-selection-mode") +
            "</gray></hover></click>\n" +
            "<click:suggest_command:'/zone list'><hover:show_text:'<gray>" +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.list.view-all-zones") +
            "</gray>'>" +
            "<yellow>/zone list</yellow> <gray>- " +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.list.view-all-zones") +
            "</gray></hover></click>\n";

        player.sendMessage(miniMessage.deserialize(exitMessage));

        return true;
    }
}
