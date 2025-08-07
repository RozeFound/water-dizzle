package io.github.rozefound.waterdizzle.commands;

import io.github.rozefound.waterdizzle.WaterDizzle;
import io.github.rozefound.waterdizzle.commands.zone.*;
import io.github.rozefound.waterdizzle.listeners.WaterDizzleListener;
import io.github.rozefound.waterdizzle.utils.SelectionManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

/**
 * Main zone command handler that delegates to subcommands
 */
public class ZoneCommand implements CommandExecutor, TabCompleter {

    private final WaterDizzle plugin;
    private final Map<String, ZoneSubCommand> subCommands;
    private final SelectionManager selectionManager;

    public ZoneCommand(WaterDizzle plugin, WaterDizzleListener listener) {
        this.plugin = plugin;
        this.subCommands = new HashMap<>();
        this.selectionManager = plugin.getSelectionManager();

        registerSubCommands();
    }

    /**
     * Register all available subcommands
     */
    private void registerSubCommands() {
        // Register each subcommand
        registerSubCommand(new ZoneHelpCommand(plugin));
        registerSubCommand(new ZoneListCommand(plugin));
        registerSubCommand(new ZoneInfoCommand(plugin));
        registerSubCommand(new ZoneRemoveCommand(plugin));
        registerSubCommand(new ZoneEditCommand(plugin, selectionManager));
        registerSubCommand(new ZoneConditionCommand(plugin));
        registerSubCommand(new ZoneReloadCommand(plugin));

        // Register selection-related subcommands
        registerSubCommand(new ZoneSelectCommand(plugin, selectionManager));
        registerSubCommand(new ZoneDeselectCommand(plugin, selectionManager));

        // Register author and version commands
        ZoneAuthorCommand authorCommand = new ZoneAuthorCommand(plugin);
        ZoneVersionCommand versionCommand = new ZoneVersionCommand(plugin);
        registerSubCommand(authorCommand);
        registerSubCommand(versionCommand);

        // Register aliases after main commands
        registerAliases(authorCommand, versionCommand);
    }

    /**
     * Register command aliases
     */
    private void registerAliases(
        ZoneAuthorCommand authorCommand,
        ZoneVersionCommand versionCommand
    ) {
        // Register version aliases
        registerSubCommand(
            new AliasSubCommand(
                "v",
                "version",
                versionCommand,
                "Display version information (alias for /zone version)"
            )
        );
        registerSubCommand(
            new AliasSubCommand(
                "ver",
                "version",
                versionCommand,
                "Display version information (alias for /zone version)"
            )
        );

        // Register author alias
        registerSubCommand(
            new AliasSubCommand(
                "about",
                "author",
                authorCommand,
                "Display author information (alias for /zone author)"
            )
        );
    }

    /**
     * Register a single subcommand
     */
    private void registerSubCommand(ZoneSubCommand subCommand) {
        subCommands.put(subCommand.getName().toLowerCase(), subCommand);
    }

    @Override
    public boolean onCommand(
        CommandSender sender,
        Command command,
        String label,
        String[] args
    ) {
        // If no arguments, show help
        if (args.length == 0) {
            return executeSubCommand(sender, "help", new String[0]);
        }

        String subCommandName = args[0].toLowerCase();

        // Get remaining arguments
        String[] subArgs = new String[args.length - 1];
        if (args.length > 1) {
            System.arraycopy(args, 1, subArgs, 0, args.length - 1);
        }

        // Execute the subcommand
        return executeSubCommand(sender, subCommandName, subArgs);
    }

    /**
     * Execute a subcommand
     */
    private boolean executeSubCommand(
        CommandSender sender,
        String subCommandName,
        String[] args
    ) {
        ZoneSubCommand subCommand = subCommands.get(subCommandName);

        if (subCommand == null) {
            // Unknown subcommand
            net.kyori.adventure.text.minimessage.MiniMessage miniMessage =
                net.kyori.adventure.text.minimessage.MiniMessage.miniMessage();

            String errorMessage =
                "\n<gradient:#E74C3C:#C0392B>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>\n" +
                "<gradient:#E74C3C:#C0392B>        <bold>" +
                plugin
                    .getLanguageManager()
                    .getMessage("commands.unknown-command-title") +
                "</bold></gradient>\n" +
                "<gradient:#E74C3C:#C0392B>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>\n\n" +
                "<gradient:#95A5A6:#7F8C8D>" +
                plugin
                    .getLanguageManager()
                    .getMessage("commands.zone.unknown-subcommand") +
                "</gradient> <yellow><bold>" +
                subCommandName +
                "</bold></yellow>\n\n" +
                "<gradient:#3498DB:#2980B9>" +
                plugin.getLanguageManager().getMessage("symbols.bulb") +
                " <bold>" +
                plugin
                    .getLanguageManager()
                    .getMessage("commands.available-commands") +
                "</bold></gradient>\n";

            List<String> availableCommands = new ArrayList<>(
                subCommands.keySet()
            );
            availableCommands.sort(String::compareTo);

            for (String cmdName : availableCommands) {
                ZoneSubCommand cmd = subCommands.get(cmdName);
                errorMessage +=
                    "<gradient:#9B59B6:#27AE60>" +
                    plugin.getLanguageManager().getMessage("symbols.bullet") +
                    "</gradient> <hover:show_text:'<gray>" +
                    cmd.getDescription() +
                    "</gray>'><click:suggest_command:'" +
                    cmd.getUsage() +
                    "'><yellow>" +
                    cmd.getUsage() +
                    "</yellow></click></hover> <dark_gray>-</dark_gray> <gray>" +
                    cmd.getDescription() +
                    "</gray>\n";
            }

            errorMessage +=
                "\n<gradient:#3498DB:#2980B9>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>\n" +
                "<click:suggest_command:'/zone help'><hover:show_text:'<gray>" +
                plugin.getLanguageManager().getMessage("commands.view-help") +
                "</gray>'>" +
                "<gradient:#9B59B6:#27AE60>" +
                plugin.getLanguageManager().getMessage("symbols.info") +
                " " +
                plugin.getLanguageManager().getMessage("commands.help-hint") +
                "</gradient></hover></click>\n";

            sender.sendMessage(miniMessage.deserialize(errorMessage));

            return true;
        }

        // Execute the subcommand
        return subCommand.execute(sender, args);
    }

    @Override
    public List<String> onTabComplete(
        CommandSender sender,
        Command command,
        String alias,
        String[] args
    ) {
        List<String> completions = new ArrayList<>();

        // Check if we have a subcommand that can handle its own tab completion
        if (args.length >= 2) {
            String subCommandName = args[0].toLowerCase();
            ZoneSubCommand subCommand = subCommands.get(subCommandName);

            if (subCommand != null) {
                // Pass the args without the subcommand name
                String[] subArgs = new String[args.length - 1];
                System.arraycopy(args, 1, subArgs, 0, args.length - 1);

                // Use the subcommand's own tab completion
                List<String> subCompletions = subCommand.onTabComplete(
                    sender,
                    subArgs
                );
                if (!subCompletions.isEmpty()) {
                    return subCompletions;
                }
            }
        }

        if (args.length == 1) {
            // Complete subcommand names
            String partial = args[0].toLowerCase();

            for (String subCommandName : subCommands.keySet()) {
                if (subCommandName.startsWith(partial)) {
                    ZoneSubCommand subCommand = subCommands.get(subCommandName);

                    // Check permission if required
                    if (
                        subCommand.getPermission() == null ||
                        sender.hasPermission(subCommand.getPermission())
                    ) {
                        completions.add(subCommandName);
                    }
                }
            }
        } else if (args.length == 2) {
            // Complete based on the subcommand
            String subCommandName = args[0].toLowerCase();
            String partial = args[1].toLowerCase();

            // For commands that take zone names (backwards compatibility)
            if (
                subCommandName.equals("edit") ||
                subCommandName.equals("remove") ||
                subCommandName.equals("info") ||
                subCommandName.equals("condition")
            ) {
                // Add all zone names that match
                for (String zoneName : plugin.getZoneManager().getZoneNames()) {
                    if (zoneName.toLowerCase().startsWith(partial)) {
                        completions.add(zoneName);
                    }
                }
            }
        } else if (args.length == 3) {
            String subCommandName = args[0].toLowerCase();

            if (subCommandName.equals("edit")) {
                // Complete edit properties
                String partial = args[2].toLowerCase();
                List<String> properties = Arrays.asList(
                    "enabled",
                    "damagetype",
                    "damageamount",
                    "damageinterval",
                    "anchors",
                    "deathmessage",
                    "damageanimal",
                    "damageentity",
                    "damageplayer",
                    "destroyitem"
                );

                for (String property : properties) {
                    if (property.startsWith(partial)) {
                        completions.add(property);
                    }
                }
            }
        } else if (args.length == 4) {
            String subCommandName = args[0].toLowerCase();

            if (subCommandName.equals("edit")) {
                String property = args[2].toLowerCase();
                String partial = args[3].toLowerCase();

                switch (property) {
                    case "enabled":
                        for (String value : Arrays.asList("true", "false")) {
                            if (value.startsWith(partial)) {
                                completions.add(value);
                            }
                        }
                        break;
                    case "damagetype":
                        List<String> damageTypes = Arrays.asList(
                            "drowning",
                            "freezing",
                            "lava",
                            "fire",
                            "wither",
                            "poison",
                            "magic",
                            "starve",
                            "none"
                        );

                        for (String type : damageTypes) {
                            if (type.startsWith(partial)) {
                                completions.add(type);
                            }
                        }
                        break;
                    case "damageamount":
                        // Suggest common damage amounts
                        for (String amount : Arrays.asList(
                            "1.0",
                            "2.0",
                            "5.0",
                            "10.0",
                            "20.0"
                        )) {
                            if (amount.startsWith(partial)) {
                                completions.add(amount);
                            }
                        }
                        break;
                    case "damageinterval":
                        // Suggest common tick intervals (in seconds converted to ticks)
                        for (String ticks : Arrays.asList(
                            "0",
                            "20",
                            "40",
                            "60",
                            "100",
                            "200"
                        )) {
                            if (ticks.startsWith(partial)) {
                                completions.add(ticks);
                            }
                        }
                        break;
                    case "damageanimal":
                    case "damageentity":
                    case "damageplayer":
                    case "destroyitem":
                        // Suggest boolean values
                        for (String bool : Arrays.asList("true", "false")) {
                            if (bool.startsWith(partial)) {
                                completions.add(bool);
                            }
                        }
                        break;
                    case "deathmessage":
                        // No specific suggestions for death message
                        break;
                }
            }
        }

        // Sort completions alphabetically
        completions.sort(String::compareTo);

        return completions;
    }
}
