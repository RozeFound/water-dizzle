package io.github.rozefound.waterdizzle.commands.zone;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.CommandSender;

/**
 * Wrapper class for command aliases that delegates to actual commands
 * with transformed arguments
 */
public class AliasSubCommand implements ZoneSubCommand {

    private final String aliasName;
    private final String[] prependArgs;
    private final ZoneSubCommand targetSubCommand;
    private final String usage;
    private final String description;

    /**
     * Create an alias that delegates to another command
     *
     * @param aliasName The name of this alias
     * @param targetCommand The full target command string (e.g., "set 1")
     * @param targetSubCommand The actual subcommand to delegate to
     * @param description The description of this alias
     */
    public AliasSubCommand(
        String aliasName,
        String targetCommand,
        ZoneSubCommand targetSubCommand,
        String description
    ) {
        this.aliasName = aliasName;
        this.targetSubCommand = targetSubCommand;
        this.description = description;

        String[] parts = targetCommand.split(" ");
        if (parts.length > 1) {
            this.prependArgs = new String[parts.length - 1];
            System.arraycopy(parts, 1, this.prependArgs, 0, parts.length - 1);
        } else {
            this.prependArgs = new String[0];
        }

        this.usage = "/zone " + aliasName + generateUsageSuffix();
    }

    @Override
    public String getName() {
        return aliasName;
    }

    @Override
    public String getPermission() {
        return targetSubCommand.getPermission();
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
        return targetSubCommand.requiresPlayer();
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        String[] newArgs = new String[prependArgs.length + args.length];
        System.arraycopy(prependArgs, 0, newArgs, 0, prependArgs.length);
        System.arraycopy(args, 0, newArgs, prependArgs.length, args.length);

        return targetSubCommand.execute(sender, newArgs);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        String[] newArgs = new String[prependArgs.length + args.length];
        System.arraycopy(prependArgs, 0, newArgs, 0, prependArgs.length);
        System.arraycopy(args, 0, newArgs, prependArgs.length, args.length);

        List<String> completions = targetSubCommand.onTabComplete(
            sender,
            newArgs
        );

        if (prependArgs.length > 0 && completions != null) {
            List<String> filteredCompletions = new ArrayList<>();
            for (String completion : completions) {
                boolean isPrepended = false;
                for (String prepended : prependArgs) {
                    if (completion.equals(prepended)) {
                        isPrepended = true;
                        break;
                    }
                }
                if (!isPrepended) {
                    filteredCompletions.add(completion);
                }
            }
            return filteredCompletions;
        }

        return completions;
    }

    private String generateUsageSuffix() {
        String targetUsage = targetSubCommand.getUsage();

        String[] usageParts = targetUsage.split(" ");
        StringBuilder suffix = new StringBuilder();

        int skipCount = 2 + prependArgs.length;
        for (int i = skipCount; i < usageParts.length; i++) {
            suffix.append(" ").append(usageParts[i]);
        }

        return suffix.toString();
    }
}
