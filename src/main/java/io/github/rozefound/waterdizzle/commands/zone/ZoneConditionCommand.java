package io.github.rozefound.waterdizzle.commands.zone;

import io.github.rozefound.waterdizzle.WaterDizzle;
import io.github.rozefound.waterdizzle.Zone;
import io.github.rozefound.waterdizzle.utils.Condition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;

/**
 * Command for managing zone conditions
 * Usage: /zone condition <zone> <add|remove|list|clear> [direction] [block]
 */
public class ZoneConditionCommand extends AbstractZoneSubCommand {

    public ZoneConditionCommand(WaterDizzle plugin) {
        super(
            plugin,
            "condition",
            "waterdizzle.zone.condition",
            "/zone condition <zone> <add|remove|list|clear> [direction] [block]",
            "Manage zone conditions",
            false
        );
    }

    @Override
    protected boolean executeCommand(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sendError(
                sender,
                plugin
                    .getLanguageManager()
                    .getMessage("commands.invalid-syntax")
            );
            sender.sendMessage(
                Component.text()
                    .append(
                        Component.text(
                            plugin
                                .getLanguageManager()
                                .getMessage("commands.usage") +
                            " ",
                            NamedTextColor.GRAY
                        )
                    )
                    .append(Component.text(getUsage(), NamedTextColor.YELLOW))
            );
            return true;
        }

        String zoneName = args[0];
        String action = args[1].toLowerCase();

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
            return true;
        }

        switch (action) {
            case "add":
                return handleAddCondition(sender, zone, args);
            case "remove":
                return handleRemoveCondition(sender, zone, args);
            case "list":
                return handleListConditions(sender, zone);
            case "clear":
                return handleClearConditions(sender, zone);
            default:
                sendError(
                    sender,
                    plugin
                        .getLanguageManager()
                        .getMessage(
                            "commands.zone.condition.invalid-action",
                            "action",
                            action
                        )
                );
                sender.sendMessage(
                    Component.text()
                        .append(
                            Component.text(
                                plugin
                                    .getLanguageManager()
                                    .getMessage(
                                        "commands.zone.condition.valid-actions"
                                    ) +
                                " ",
                                NamedTextColor.GRAY
                            )
                        )
                        .append(
                            Component.text(
                                plugin
                                    .getLanguageManager()
                                    .getMessage(
                                        "commands.zone.condition.valid-actions-list"
                                    ),
                                NamedTextColor.YELLOW
                            )
                        )
                );
                return true;
        }
    }

    private boolean handleAddCondition(
        CommandSender sender,
        Zone zone,
        String[] args
    ) {
        if (args.length < 4) {
            sendError(
                sender,
                plugin
                    .getLanguageManager()
                    .getMessage("commands.zone.condition.invalid-syntax-add")
            );
            sender.sendMessage(
                Component.text()
                    .append(
                        Component.text(
                            plugin
                                .getLanguageManager()
                                .getMessage("commands.usage") +
                            " ",
                            NamedTextColor.GRAY
                        )
                    )
                    .append(
                        Component.text(
                            plugin
                                .getLanguageManager()
                                .getMessage(
                                    "commands.zone.condition.usage-add"
                                ),
                            NamedTextColor.YELLOW
                        )
                    )
            );
            sender.sendMessage(
                Component.text()
                    .append(
                        Component.text(
                            plugin
                                .getLanguageManager()
                                .getMessage(
                                    "commands.zone.condition.directions-label"
                                ) +
                            " ",
                            NamedTextColor.GRAY
                        )
                    )
                    .append(
                        Component.text(
                            plugin
                                .getLanguageManager()
                                .getMessage(
                                    "commands.zone.condition.valid-directions"
                                ),
                            NamedTextColor.AQUA
                        )
                    )
            );
            return true;
        }

        String directionStr = args[2].toLowerCase();

        StringBuilder blockDataBuilder = new StringBuilder();
        for (int i = 3; i < args.length; i++) {
            if (i > 3) blockDataBuilder.append(" ");
            blockDataBuilder.append(args[i]);
        }
        String blockDataStr = blockDataBuilder.toString();

        Condition.Direction direction;
        switch (directionStr) {
            case "inside":
                direction = Condition.Direction.Inside;
                break;
            case "standingon":
            case "standing_on":
            case "on":
                direction = Condition.Direction.StandingOn;
                break;
            default:
                sendError(
                    sender,
                    plugin
                        .getLanguageManager()
                        .getMessage(
                            "commands.zone.condition.invalid-direction",
                            "direction",
                            directionStr
                        )
                );
                sender.sendMessage(
                    Component.text()
                        .append(
                            Component.text(
                                plugin
                                    .getLanguageManager()
                                    .getMessage(
                                        "commands.zone.condition.valid-directions"
                                    ) +
                                " ",
                                NamedTextColor.GRAY
                            )
                        )
                        .append(
                            Component.text(
                                plugin
                                    .getLanguageManager()
                                    .getMessage(
                                        "commands.zone.condition.valid-directions"
                                    ),
                                NamedTextColor.YELLOW
                            )
                        )
                );
                return true;
        }

        String materialOnly = blockDataStr.split("\\[")[0];

        org.bukkit.block.data.BlockData blockData;
        try {
            blockData = org.bukkit.Bukkit.createBlockData(materialOnly);
        } catch (IllegalArgumentException e) {
            sendError(
                sender,
                plugin
                    .getLanguageManager()
                    .getMessage(
                        "commands.zone.condition.invalid-block",
                        "block",
                        blockDataStr
                    )
            );
            sender.sendMessage(
                Component.text()
                    .append(
                        Component.text(
                            plugin
                                .getLanguageManager()
                                .getMessage(
                                    "commands.zone.condition.block-examples-label"
                                ) +
                            " ",
                            NamedTextColor.GRAY
                        )
                    )
                    .append(
                        Component.text(
                            plugin
                                .getLanguageManager()
                                .getMessage(
                                    "commands.zone.condition.block-examples"
                                ),
                            NamedTextColor.YELLOW
                        )
                    )
            );
            sender.sendMessage(
                Component.text().append(
                    Component.text(
                        plugin
                            .getLanguageManager()
                            .getMessage(
                                "commands.zone.condition.block-setblock-hint"
                            ),
                        NamedTextColor.GRAY
                    )
                )
            );
            return true;
        }

        for (Condition existing : zone.getConditions()) {
            if (
                existing.getDirection() == direction &&
                existing.getBlockDataString().equalsIgnoreCase(materialOnly)
            ) {
                sendError(
                    sender,
                    plugin
                        .getLanguageManager()
                        .getMessage(
                            "commands.zone.condition.condition-exists",
                            "zone",
                            zone.getName()
                        )
                );
                return true;
            }
        }

        Condition condition = new Condition(direction, materialOnly);
        zone.addCondition(condition);
        plugin.getZoneManager().saveZones();

        String border = plugin
            .getLanguageManager()
            .getMessage("commands.zone.condition.condition-added-header-style");
        String successMessage =
            "\n" +
            border +
            "\n" +
            "<gradient:#2ECC71:#27AE60>        <bold>" +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.condition.condition-added-title") +
            "</bold></gradient>\n" +
            border +
            "\n\n" +
            "<gradient:#9B59B6:#27AE60>" +
            plugin.getLanguageManager().getMessage("symbols.lightning") +
            " <bold>" +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.condition.zone-label") +
            "</bold></gradient> <yellow><bold>" +
            zone.getName() +
            "</bold></yellow>\n" +
            "<gradient:#9B59B6:#27AE60>" +
            plugin.getLanguageManager().getMessage("symbols.lightning") +
            " <bold>" +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.condition.condition-label") +
            "</bold></gradient> <aqua>" +
            formatCondition(direction, blockData) +
            "</aqua>\n\n" +
            "<green>" +
            plugin.getLanguageManager().getMessage("symbols.success") +
            " " +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.condition.condition-added") +
            "</green>\n";

        sender.sendMessage(
            plugin.getLanguageManager().parseMiniMessage(successMessage)
        );

        return true;
    }

    private boolean handleRemoveCondition(
        CommandSender sender,
        Zone zone,
        String[] args
    ) {
        if (args.length < 3) {
            sendError(
                sender,
                plugin
                    .getLanguageManager()
                    .getMessage("commands.zone.condition.invalid-syntax-remove")
            );
            sender.sendMessage(
                Component.text()
                    .append(
                        Component.text(
                            plugin
                                .getLanguageManager()
                                .getMessage("commands.usage") +
                            " ",
                            NamedTextColor.GRAY
                        )
                    )
                    .append(
                        Component.text(
                            plugin
                                .getLanguageManager()
                                .getMessage(
                                    "commands.zone.condition.usage-remove"
                                ),
                            NamedTextColor.YELLOW
                        )
                    )
            );
            sender.sendMessage(
                Component.text()
                    .append(
                        Component.text(
                            plugin
                                .getLanguageManager()
                                .getMessage(
                                    "commands.zone.condition.use-prefix"
                                ),
                            NamedTextColor.GRAY
                        )
                    )
                    .append(
                        Component.text(
                            "/zone condition " + zone.getName() + " list",
                            NamedTextColor.YELLOW
                        )
                    )
                    .append(
                        Component.text(
                            " " +
                            plugin
                                .getLanguageManager()
                                .getMessage(
                                    "commands.zone.condition.use-list-indices"
                                ),
                            NamedTextColor.GRAY
                        )
                    )
            );
            return true;
        }

        String indexStr = args[2];
        int index;

        try {
            index = Integer.parseInt(indexStr) - 1; // Convert to 0-based index
        } catch (NumberFormatException e) {
            sendError(
                sender,
                plugin
                    .getLanguageManager()
                    .getMessage(
                        "commands.zone.condition.invalid-index",
                        "index",
                        indexStr
                    )
            );
            return true;
        }

        ArrayList<Condition> conditions = zone.getConditions();

        if (index < 0 || index >= conditions.size()) {
            sendError(
                sender,
                plugin
                    .getLanguageManager()
                    .getMessage(
                        "commands.zone.condition.index-out-range",
                        "count",
                        String.valueOf(conditions.size())
                    )
            );
            return true;
        }

        Condition removed = conditions.get(index);
        String removedStr = formatCondition(removed);

        conditions.remove(index);
        plugin.getZoneManager().saveZones();

        String border = plugin
            .getLanguageManager()
            .getMessage(
                "commands.zone.condition.condition-removed-header-style"
            );
        String removalMessage =
            "\n" +
            border +
            "\n" +
            "<gradient:#F39C12:#E67E22>       <bold>" +
            plugin
                .getLanguageManager()
                .getMessage(
                    "commands.zone.condition.condition-removed-header"
                ) +
            "</bold></gradient>\n" +
            border +
            "\n\n" +
            "<gradient:#E67E22:#D68910>" +
            plugin.getLanguageManager().getMessage("symbols.lightning") +
            " <bold>" +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.condition.zone-label") +
            "</bold></gradient> <yellow><bold>" +
            zone.getName() +
            "</bold></yellow>\n" +
            "<gradient:#E67E22:#D68910>" +
            plugin.getLanguageManager().getMessage("symbols.lightning") +
            " <bold>" +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.condition.removed-label") +
            "</bold></gradient> <red>" +
            removedStr +
            "</red>\n\n" +
            "<green>" +
            plugin.getLanguageManager().getMessage("symbols.success") +
            " " +
            plugin
                .getLanguageManager()
                .getMessage(
                    "commands.zone.condition.condition-removed-success"
                ) +
            "</green>\n";

        sender.sendMessage(
            plugin.getLanguageManager().parseMiniMessage(removalMessage)
        );

        return true;
    }

    private boolean handleListConditions(CommandSender sender, Zone zone) {
        ArrayList<Condition> conditions = zone.getConditions();

        String border = plugin
            .getLanguageManager()
            .getMessage("commands.zone.condition.condition-list-header-style");
        String headerMessage =
            "\n" +
            border +
            "\n" +
            "<gradient:#3498DB:#2980B9>       <bold>" +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.condition.zone-conditions-header") +
            "</bold></gradient>\n" +
            border +
            "\n\n" +
            "<gradient:#9B59B6:#27AE60>" +
            plugin.getLanguageManager().getMessage("symbols.lightning") +
            " <bold>" +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.condition.zone-label") +
            "</bold></gradient> <yellow><bold>" +
            zone.getName() +
            "</bold></yellow>\n\n";

        sender.sendMessage(
            plugin.getLanguageManager().parseMiniMessage(headerMessage)
        );

        if (conditions.isEmpty()) {
            String emptyMessage =
                "<gray>" +
                plugin
                    .getLanguageManager()
                    .getMessage(
                        "commands.zone.condition.no-conditions-configured"
                    ) +
                "</gray>\n\n" +
                "<gradient:#9B59B6:#27AE60>" +
                plugin.getLanguageManager().getMessage("symbols.bulb") +
                " <bold>" +
                plugin
                    .getLanguageManager()
                    .getMessage("commands.zone.condition.tip") +
                "</bold></gradient> " +
                "<click:suggest_command:'/zone condition " +
                zone.getName() +
                " add '>" +
                "<hover:show_text:'<gray>" +
                plugin
                    .getLanguageManager()
                    .getMessage("commands.zone.condition.add-new") +
                "</gray>'>" +
                "<aqua>" +
                plugin
                    .getLanguageManager()
                    .getMessage("commands.zone.condition.use-prefix") +
                "</aqua><yellow>/zone condition " +
                zone.getName() +
                " add</yellow>" +
                "<aqua>" +
                plugin
                    .getLanguageManager()
                    .getMessage("commands.zone.condition.to-add-conditions") +
                "</aqua></hover></click>\n";
            sender.sendMessage(
                plugin.getLanguageManager().parseMiniMessage(emptyMessage)
            );
        } else {
            String conditionsHeader =
                "<gradient:#E67E22:#D68910><bold> " +
                plugin
                    .getLanguageManager()
                    .getMessage(
                        "commands.zone.condition.zone-conditions-header"
                    ) +
                " (" +
                conditions.size() +
                "):</bold></gradient>\n";
            sender.sendMessage(
                plugin.getLanguageManager().parseMiniMessage(conditionsHeader)
            );

            for (int i = 0; i < conditions.size(); i++) {
                Condition condition = conditions.get(i);
                String conditionLine =
                    "<gradient:#95A5A6:#7F8C8D><bold>" +
                    (i + 1) +
                    ".</bold></gradient> <aqua>" +
                    formatCondition(condition) +
                    "</aqua>";
                sender.sendMessage(
                    plugin.getLanguageManager().parseMiniMessage(conditionLine)
                );
            }

            String footerMessage =
                "\n<gradient:#9B59B6:#27AE60>" +
                plugin.getLanguageManager().getMessage("symbols.bulb") +
                " <bold>" +
                plugin
                    .getLanguageManager()
                    .getMessage("commands.zone.condition.actions-label") +
                "</bold></gradient>\n" +
                "<click:suggest_command:'/zone condition " +
                zone.getName() +
                " remove '>" +
                "<hover:show_text:'<gray>" +
                plugin
                    .getLanguageManager()
                    .getMessage("commands.zone.condition.remove-by-index") +
                "</gray>'>" +
                "<aqua>" +
                plugin
                    .getLanguageManager()
                    .getMessage("commands.zone.condition.bullet-point") +
                "</aqua><yellow>/zone condition " +
                zone.getName() +
                " remove <index></yellow>" +
                "<aqua>" +
                plugin
                    .getLanguageManager()
                    .getMessage(
                        "commands.zone.condition.remove-condition-action"
                    ) +
                "</aqua></hover></click>\n" +
                "<click:suggest_command:'/zone condition " +
                zone.getName() +
                " add '>" +
                "<hover:show_text:'<gray>" +
                plugin
                    .getLanguageManager()
                    .getMessage("commands.zone.condition.add-new") +
                "</gray>'>" +
                "<aqua>" +
                plugin
                    .getLanguageManager()
                    .getMessage("commands.zone.condition.bullet-point") +
                "</aqua><yellow>/zone condition " +
                zone.getName() +
                " add</yellow>" +
                "<aqua>" +
                plugin
                    .getLanguageManager()
                    .getMessage("commands.zone.condition.add-new-condition") +
                "</aqua></hover></click>\n" +
                "<click:suggest_command:'/zone condition " +
                zone.getName() +
                " clear'>" +
                "<hover:show_text:'<gray>" +
                plugin
                    .getLanguageManager()
                    .getMessage("commands.zone.condition.clear-all-conditions")
                    .replace(" - ", "") +
                "</gray>'>" +
                "<aqua>" +
                plugin
                    .getLanguageManager()
                    .getMessage("commands.zone.condition.bullet-point") +
                "</aqua><yellow>/zone condition " +
                zone.getName() +
                " clear</yellow>" +
                "<aqua>" +
                plugin
                    .getLanguageManager()
                    .getMessage(
                        "commands.zone.condition.clear-all-conditions"
                    ) +
                "</aqua></hover></click>\n";
            sender.sendMessage(
                plugin.getLanguageManager().parseMiniMessage(footerMessage)
            );
        }

        return true;
    }

    private boolean handleClearConditions(CommandSender sender, Zone zone) {
        ArrayList<Condition> conditions = zone.getConditions();

        if (conditions.isEmpty()) {
            sendError(
                sender,
                plugin
                    .getLanguageManager()
                    .getMessage(
                        "commands.zone.condition.no-conditions-clear",
                        "zone",
                        zone.getName()
                    )
            );
            return true;
        }

        int count = conditions.size();
        conditions.clear();
        plugin.getZoneManager().saveZones();

        String border = plugin
            .getLanguageManager()
            .getMessage(
                "commands.zone.condition.condition-removed-header-style"
            );
        String clearMessage =
            "\n" +
            border +
            "\n" +
            "<gradient:#F39C12:#E67E22>      <bold>" +
            plugin
                .getLanguageManager()
                .getMessage(
                    "commands.zone.condition.conditions-cleared-header"
                ) +
            "</bold></gradient>\n" +
            border +
            "\n\n" +
            "<gradient:#E67E22:#D68910>" +
            plugin.getLanguageManager().getMessage("symbols.lightning") +
            " <bold>" +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.condition.zone-label") +
            "</bold></gradient> <yellow><bold>" +
            zone.getName() +
            "</bold></yellow>\n" +
            "<gradient:#E67E22:#D68910>" +
            plugin.getLanguageManager().getMessage("symbols.lightning") +
            " <bold>" +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.condition.cleared-label") +
            "</bold></gradient> <red>" +
            count +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.condition.conditions-suffix") +
            "</red>\n\n" +
            "<green>" +
            plugin.getLanguageManager().getMessage("symbols.success") +
            " " +
            plugin
                .getLanguageManager()
                .getMessage(
                    "commands.zone.condition.conditions-cleared-success"
                ) +
            "</green>\n";

        sender.sendMessage(
            plugin.getLanguageManager().parseMiniMessage(clearMessage)
        );

        return true;
    }

    private String formatCondition(Condition condition) {
        String dirStr = condition.getDirection() == Condition.Direction.Inside
            ? plugin
                .getLanguageManager()
                .getMessage("commands.zone.condition.inside")
            : plugin
                .getLanguageManager()
                .getMessage("commands.zone.condition.standing-on");
        String blockStr = condition.getBlockDataString() != null
            ? condition.getBlockDataString()
            : plugin
                .getLanguageManager()
                .getMessage("commands.zone.condition.unknown-block");
        return dirStr + " " + blockStr;
    }

    private String formatCondition(
        Condition.Direction direction,
        org.bukkit.block.data.BlockData blockData
    ) {
        String dirStr = direction == Condition.Direction.Inside
            ? plugin
                .getLanguageManager()
                .getMessage("commands.zone.condition.inside")
            : plugin
                .getLanguageManager()
                .getMessage("commands.zone.condition.standing-on");
        String blockStr = blockData != null
            ? blockData.getAsString()
            : plugin
                .getLanguageManager()
                .getMessage("commands.zone.condition.unknown-block");
        return dirStr + " " + blockStr;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            String partial = args[0].toLowerCase();
            for (String zoneName : plugin.getZoneManager().getZoneNames()) {
                if (zoneName.toLowerCase().startsWith(partial)) {
                    completions.add(zoneName);
                }
            }
        } else if (args.length == 2) {
            String partial = args[1].toLowerCase();
            for (String action : Arrays.asList(
                "add",
                "remove",
                "list",
                "clear"
            )) {
                if (action.startsWith(partial)) {
                    completions.add(action);
                }
            }
        } else if (args.length == 3) {
            String action = args[1].toLowerCase();
            String partial = args[2].toLowerCase();

            if (action.equals("add")) {
                for (String direction : Arrays.asList("inside", "standingon")) {
                    if (direction.startsWith(partial)) {
                        completions.add(direction);
                    }
                }
            } else if (action.equals("remove")) {
                Zone zone = plugin.getZoneManager().getZone(args[0]);
                if (zone != null) {
                    int count = zone.getConditions().size();
                    for (int i = 1; i <= count; i++) {
                        String index = String.valueOf(i);
                        if (index.startsWith(partial)) {
                            completions.add(index);
                        }
                    }
                }
            }
        } else if (args.length == 4) {
            String action = args[1].toLowerCase();

            if (action.equals("add")) {
                String partial = args[3].toLowerCase();

                if (partial.length() >= 2) {
                    for (org.bukkit.Material material : org.bukkit.Material.values()) {
                        if (material.isBlock()) {
                            String simpleName = material.name().toLowerCase();
                            String namespacedKey = material.getKey().toString();

                            if (simpleName.startsWith(partial)) {
                                completions.add(simpleName);
                            }
                            if (namespacedKey.startsWith(partial)) {
                                completions.add(namespacedKey);
                            }
                        }
                    }
                }

                if (partial.length() >= 2) {
                    for (Material material : Material.values()) {
                        String materialName = material.name().toLowerCase();
                        if (materialName.startsWith(partial)) {
                            completions.add(materialName);
                        }
                    }
                }
            }
        }

        return completions;
    }
}
