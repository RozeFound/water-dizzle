package io.github.rozefound.waterdizzle.commands.zone;

import org.bukkit.command.CommandSender;

/**
 * Base interface for all zone subcommands
 */
public interface ZoneSubCommand {
    /**
     * Execute the subcommand
     *
     * @param sender The command sender
     * @param args The command arguments (excluding the subcommand name)
     * @return true if the command was executed successfully
     */
    boolean execute(CommandSender sender, String[] args);

    /**
     * Get the name of this subcommand
     *
     * @return The subcommand name
     */
    String getName();

    /**
     * Get the permission required to use this subcommand
     *
     * @return The permission string, or null if no permission is required
     */
    String getPermission();

    /**
     * Get the usage/syntax for this subcommand
     *
     * @return The usage string
     */
    String getUsage();

    /**
     * Get a brief description of what this subcommand does
     *
     * @return The description
     */
    String getDescription();

    /**
     * Check if this command requires the sender to be a player
     *
     * @return true if player-only command
     */
    boolean requiresPlayer();

    /**
     * Provide tab completion suggestions for this subcommand
     *
     * @param sender The command sender
     * @param args The command arguments (excluding the subcommand name)
     * @return A list of suggestions for tab completion
     */
    default java.util.List<String> onTabComplete(
        CommandSender sender,
        String[] args
    ) {
        // Default implementation returns empty list
        // Override in subcommands that provide tab completion
        return new java.util.ArrayList<>();
    }

    /**
     * Clean up any resources used by this subcommand
     * Called when the plugin is disabled or command is unregistered
     */
    default void cleanup() {
        // Default implementation does nothing
        // Override in subcommands that need cleanup
    }
}
