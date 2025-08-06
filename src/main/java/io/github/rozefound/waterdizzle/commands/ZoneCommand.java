package io.github.rozefound.waterdizzle.commands;

import io.github.rozefound.waterdizzle.WaterDizzle;
import io.github.rozefound.waterdizzle.listeners.WaterDizzleListener;
import io.github.rozefound.waterdizzle.utils.ZoneUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ZoneCommand implements CommandExecutor {

    private final WaterDizzle plugin;

    public ZoneCommand(WaterDizzle plugin, WaterDizzleListener listener) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(
        CommandSender sender,
        Command command,
        String label,
        String[] args
    ) {
        if (args.length == 0) {
            sender.sendMessage(
                Component.text()
                    .content("Usage: /zone [set1|set2|info|particles]")
                    .color(NamedTextColor.RED)
            );
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "set1":
                return handleSetAnchor(sender, args, 0);
            case "set2":
                return handleSetAnchor(sender, args, 1);
            case "info":
                return handleInfo(sender);
            case "particles":
                return handleParticles(sender, args);
            default:
                sender.sendMessage(
                    Component.text()
                        .content("Unknown subcommand: " + subCommand)
                        .color(NamedTextColor.RED)
                );
                return true;
        }
    }

    private boolean handleSetAnchor(
        CommandSender sender,
        String[] args,
        int anchorIndex
    ) {
        Location newLocation;

        if (args.length == 1) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(
                    Component.text()
                        .content(
                            "Console must specify coordinates: /zone " +
                            args[0] +
                            " <x> <y> <z>"
                        )
                        .color(NamedTextColor.RED)
                );
                return true;
            }

            Player player = (Player) sender;
            newLocation = player.getLocation().clone();
            newLocation.setX(Math.floor(newLocation.getX()));
            newLocation.setY(Math.floor(newLocation.getY()));
            newLocation.setZ(Math.floor(newLocation.getZ()));
        } else if (args.length == 4) {
            try {
                double x = Double.parseDouble(args[1]);
                double y = Double.parseDouble(args[2]);
                double z = Double.parseDouble(args[3]);

                if (sender instanceof Player) {
                    newLocation = new Location(
                        ((Player) sender).getWorld(),
                        x,
                        y,
                        z
                    );
                } else {
                    newLocation = new Location(
                        plugin.getServer().getWorlds().get(0),
                        x,
                        y,
                        z
                    );
                }
            } catch (NumberFormatException e) {
                sender.sendMessage(
                    Component.text()
                        .content("Invalid coordinates! Please use numbers.")
                        .color(NamedTextColor.RED)
                );
                return true;
            }
        } else {
            sender.sendMessage(
                Component.text()
                    .content("Usage: /zone " + args[0] + " [x] [y] [z]")
                    .color(NamedTextColor.RED)
            );
            return true;
        }

        plugin.setZoneAnchor(anchorIndex, newLocation);

        String anchorName = anchorIndex == 0 ? "first" : "second";
        sender.sendMessage(
            Component.text()
                .content("Zone " + anchorName + " anchor set to: ")
                .color(NamedTextColor.GREEN)
                .append(
                    Component.text()
                        .content(
                            String.format(
                                "%.0f, %.0f, %.0f",
                                newLocation.getX(),
                                newLocation.getY(),
                                newLocation.getZ()
                            )
                        )
                        .color(NamedTextColor.YELLOW)
                )
                .append(
                    Component.text()
                        .content(" in world ")
                        .color(NamedTextColor.GREEN)
                )
                .append(
                    Component.text()
                        .content(newLocation.getWorld().getName())
                        .color(NamedTextColor.AQUA)
                )
        );

        return true;
    }

    private boolean handleInfo(CommandSender sender) {
        Location[] anchors = plugin.getZoneAnchors();

        sender.sendMessage(
            Component.text()
                .content("=== Damage Zone Information ===")
                .color(NamedTextColor.GOLD)
        );

        for (int i = 0; i < 2; i++) {
            Location anchor = anchors[i];
            String anchorName = i == 0 ? "First" : "Second";

            sender.sendMessage(
                Component.text()
                    .content(anchorName + " anchor: ")
                    .color(NamedTextColor.GREEN)
                    .append(
                        Component.text()
                            .content(
                                String.format(
                                    "%.0f, %.0f, %.0f",
                                    anchor.getX(),
                                    anchor.getY(),
                                    anchor.getZ()
                                )
                            )
                            .color(NamedTextColor.YELLOW)
                    )
                    .append(
                        Component.text()
                            .content(" (" + anchor.getWorld().getName() + ")")
                            .color(NamedTextColor.GRAY)
                    )
            );
        }

        var zoneBounds = ZoneUtils.getZoneBounds(anchors[0], anchors[1]);

        double sizeX = zoneBounds.getMaxX() - zoneBounds.getMinX();
        double sizeY = zoneBounds.getMaxY() - zoneBounds.getMinY();
        double sizeZ = zoneBounds.getMaxZ() - zoneBounds.getMinZ();

        sender.sendMessage(
            Component.text()
                .content("Zone size: ")
                .color(NamedTextColor.GREEN)
                .append(
                    Component.text()
                        .content(
                            String.format(
                                "%.0f × %.0f × %.0f blocks",
                                sizeX,
                                sizeY,
                                sizeZ
                            )
                        )
                        .color(NamedTextColor.YELLOW)
                )
        );

        sender.sendMessage(
            Component.text()
                .content("Zone bounds: ")
                .color(NamedTextColor.GREEN)
                .append(
                    Component.text()
                        .content(
                            String.format(
                                "(%.0f, %.0f, %.0f) to (%.0f, %.0f, %.0f)",
                                zoneBounds.getMinX(),
                                zoneBounds.getMinY(),
                                zoneBounds.getMinZ(),
                                zoneBounds.getMaxX(),
                                zoneBounds.getMaxY(),
                                zoneBounds.getMaxZ()
                            )
                        )
                        .color(NamedTextColor.YELLOW)
                )
        );

        return true;
    }

    private boolean handleParticles(CommandSender sender, String[] args) {
        var enabled = !plugin.toggleParticles();
        sender.sendMessage(
            Component.text()
                .content("Zone border particles ")
                .color(NamedTextColor.GREEN)
                .append(
                    Component.text()
                        .content(enabled ? "ENABLED" : "DISABLED")
                        .color(
                            enabled ? NamedTextColor.GREEN : NamedTextColor.RED
                        )
                )
        );

        return true;
    }
}
