package io.github.rozefound.waterdizzle.commands.zone;

import io.github.rozefound.waterdizzle.WaterDizzle;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Abstract base class for zone subcommands providing common functionality
 */
public abstract class AbstractZoneSubCommand implements ZoneSubCommand {

    protected final WaterDizzle plugin;
    protected final String name;
    protected final String permission;
    protected final String usage;
    protected final String description;
    protected final boolean requiresPlayer;
    protected final MiniMessage miniMessage = MiniMessage.miniMessage();

    public AbstractZoneSubCommand(
        WaterDizzle plugin,
        String name,
        String permission,
        String usage,
        String description,
        boolean requiresPlayer
    ) {
        this.plugin = plugin;
        this.name = name;
        this.permission = permission;
        this.usage = usage;
        this.description = description;
        this.requiresPlayer = requiresPlayer;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPermission() {
        return permission;
    }

    @Override
    public String getUsage() {
        return usage;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public boolean requiresPlayer() {
        return requiresPlayer;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        // Check if command requires player
        if (requiresPlayer && !(sender instanceof Player)) {
            String errorMessage =
                "\n<gradient:#E74C3C:#C0392B>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>\n" +
                "<gradient:#E74C3C:#C0392B>        <bold>" +
                plugin
                    .getLanguageManager()
                    .getMessage("permissions.player-only-title") +
                "</bold></gradient>\n" +
                "<gradient:#E74C3C:#C0392B>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>\n\n" +
                "<gradient:#95A5A6:#7F8C8D>❌ " +
                plugin
                    .getLanguageManager()
                    .getMessage("permissions.player-only") +
                "</gradient>\n\n" +
                "<gradient:#9B59B6:#27AE60>💡 <bold>Tip:</bold></gradient> <gray>" +
                plugin
                    .getLanguageManager()
                    .getMessage("permissions.player-only-tip") +
                "</gray>\n";
            sender.sendMessage(miniMessage.deserialize(errorMessage));
            return true;
        }

        // Check permission
        if (permission != null && !sender.hasPermission(permission)) {
            String errorMessage =
                "\n<gradient:#E74C3C:#C0392B>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>\n" +
                "<gradient:#E74C3C:#C0392B>        <bold>" +
                plugin
                    .getLanguageManager()
                    .getMessage("permissions.access-denied-title") +
                "</bold></gradient>\n" +
                "<gradient:#E74C3C:#C0392B>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>\n\n" +
                "<gradient:#95A5A6:#7F8C8D>❌ " +
                plugin
                    .getLanguageManager()
                    .getMessage("permissions.no-permission") +
                "</gradient>\n\n" +
                "<gradient:#9B59B6:#27AE60>💡 <bold>" +
                plugin
                    .getLanguageManager()
                    .getMessage(
                        "permissions.required-permission",
                        "permission",
                        permission
                    ) +
                "</bold></gradient>\n";
            sender.sendMessage(miniMessage.deserialize(errorMessage));
            return true;
        }

        // Execute the actual command logic
        return executeCommand(sender, args);
    }

    /**
     * Execute the actual command logic
     * This method should be implemented by subclasses
     *
     * @param sender The command sender (already validated for player requirement and permissions)
     * @param args The command arguments
     * @return true if the command was executed successfully
     */
    protected abstract boolean executeCommand(
        CommandSender sender,
        String[] args
    );

    /**
     * Send an error message to the sender
     *
     * @param sender The command sender
     * @param message The error message
     */
    protected void sendError(CommandSender sender, String message) {
        sender.sendMessage(
            plugin.getLanguageManager().getErrorMessage(message)
        );
    }

    /**
     * Send a success message to the sender
     *
     * @param sender The command sender
     * @param message The success message
     */
    protected void sendSuccess(CommandSender sender, String message) {
        sender.sendMessage(
            plugin.getLanguageManager().getSuccessMessage(message)
        );
    }

    /**
     * Send an info message to the sender
     *
     * @param sender The command sender
     * @param message The info message
     */
    protected void sendInfo(CommandSender sender, String message) {
        sender.sendMessage(plugin.getLanguageManager().getInfoMessage(message));
    }
}
