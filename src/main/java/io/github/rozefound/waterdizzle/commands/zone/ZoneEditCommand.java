package io.github.rozefound.waterdizzle.commands.zone;

import io.github.rozefound.waterdizzle.WaterDizzle;
import io.github.rozefound.waterdizzle.Zone;
import io.github.rozefound.waterdizzle.utils.LanguageManager;
import io.github.rozefound.waterdizzle.utils.SelectionManager;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Player;

/**
 * Command for editing existing zone properties
 */
public class ZoneEditCommand extends AbstractZoneSubCommand {

    public ZoneEditCommand(
        WaterDizzle plugin,
        SelectionManager selectionManager
    ) {
        super(
            plugin,
            "edit",
            "waterdizzle.zone.edit",
            "/zone edit <name> <property> <value>",
            "Edit properties of an existing zone",
            false
        );
    }

    @Override
    protected boolean executeCommand(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sendError(sender, "Usage: /zone edit <name> [property] [value]");
            sendInfo(
                sender,
                plugin
                    .getLanguageManager()
                    .getMessage("commands.zone.edit.show-edit-menu")
            );
            sendInfo(
                sender,
                plugin
                    .getLanguageManager()
                    .getMessage(
                        "commands.zone.edit.edit-boundaries-hint",
                        "zone",
                        args.length > 0 ? args[0] : "<name>"
                    )
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
                        "commands.zone.edit.not-found",
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

        if (args.length < 2) {
            showEditMenu(sender, zone);
            return true;
        }

        String property = args[1].toLowerCase();

        switch (property) {
            case "damageamount":
                return handleEditDamageAmount(sender, zone, args);
            case "damagetype":
                return handleEditDamageType(sender, zone, args);
            case "damageinterval":
                return handleEditDamageInterval(sender, zone, args);
            case "deathmessage":
                return handleEditDeathMessage(sender, zone, args);
            case "damageanimal":
                return handleEditDamageAnimal(sender, zone, args);
            case "damageentity":
                return handleEditDamageEntity(sender, zone, args);
            case "damageplayer":
                return handleEditDamagePlayer(sender, zone, args);
            case "destroyitem":
                return handleEditDestroyItem(sender, zone, args);
            case "enabled":
                return handleEditEnabled(sender, zone, args);
            default:
                sendError(
                    sender,
                    plugin
                        .getLanguageManager()
                        .getMessage(
                            "commands.zone.edit.unknown-property",
                            "property",
                            property
                        )
                );
                sendInfo(
                    sender,
                    plugin
                        .getLanguageManager()
                        .getMessage("commands.zone.edit.valid-properties")
                );
                return true;
        }
    }

    private void showEditMenu(CommandSender sender, Zone zone) {
        String menuMessage =
            "\n" +
            "<gold><bold>═══════════════════════════════════</bold></gold>\n" +
            "<gold><bold>        " +
            plugin
                .getLanguageManager()
                .getMessage(
                    "commands.zone.edit.editing-zone-title",
                    "name",
                    zone.getName().toUpperCase()
                ) +
            "</bold></gold>\n" +
            "<gold><bold>═══════════════════════════════════</bold></gold>\n\n" +
            "<yellow><bold>" +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.edit.current-settings") +
            "</bold></yellow>\n" +
            "<gray>" +
            plugin.getLanguageManager().getMessage("symbols.bullet") +
            " " +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.edit.status-label") +
            " </gray>" +
            (zone.isEnabled()
                    ? "<green><bold>" +
                    plugin
                        .getLanguageManager()
                        .getMessage("commands.zone.info.enabled") +
                    "</bold></green>"
                    : "<red><bold>" +
                    plugin
                        .getLanguageManager()
                        .getMessage("commands.zone.info.disabled") +
                    "</bold></red>") +
            "\n" +
            "<gray>" +
            plugin.getLanguageManager().getMessage("symbols.bullet") +
            " " +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.edit.damage-type-label") +
            " </gray>" +
            (zone.getDamageType() != null
                    ? "<red>" + zone.getDamageType().key().value() + "</red>"
                    : "<dark_gray>" +
                    plugin
                        .getLanguageManager()
                        .getMessage("commands.zone.edit.none") +
                    "</dark_gray>") +
            "\n" +
            "<gray>" +
            plugin.getLanguageManager().getMessage("symbols.bullet") +
            " " +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.edit.damage-amount-label") +
            " </gray>" +
            "<light_purple>" +
            String.format("%.1f", zone.getDamageAmount()) +
            "</light_purple>\n" +
            "<gray>" +
            plugin.getLanguageManager().getMessage("symbols.bullet") +
            " " +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.edit.damage-interval-label") +
            " </gray>" +
            "<aqua>" +
            (zone.getDamageInterval() > 0
                    ? plugin
                        .getLanguageManager()
                        .getMessage(
                            "commands.zone.edit.ticks-seconds",
                            LanguageManager.placeholders(
                                "ticks",
                                String.valueOf(zone.getDamageInterval()),
                                "seconds",
                                String.format(
                                    "%.1f",
                                    zone.getDamageInterval() / 20.0
                                )
                            )
                        )
                    : plugin
                        .getLanguageManager()
                        .getMessage("commands.zone.edit.instant")) +
            "</aqua>\n\n" +
            "<yellow><bold>" +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.edit.available-commands-label") +
            "</bold></yellow>\n" +
            "<dark_gray>" +
            plugin.getLanguageManager().getMessage("symbols.bullet") +
            " </dark_gray><white>/zone edit " +
            zone.getName() +
            " enabled </white><green>[true/false]</green>\n" +
            "<dark_gray>" +
            plugin.getLanguageManager().getMessage("symbols.bullet") +
            " </dark_gray><white>/zone edit " +
            zone.getName() +
            " damagetype </white><green>[type]</green>\n" +
            "<dark_gray>" +
            plugin.getLanguageManager().getMessage("symbols.bullet") +
            " </dark_gray><white>/zone edit " +
            zone.getName() +
            " damageamount </white><green>[amount]</green>\n" +
            "<dark_gray>" +
            plugin.getLanguageManager().getMessage("symbols.bullet") +
            " </dark_gray><white>/zone edit " +
            zone.getName() +
            " damageinterval </white><green>[ticks]</green>\n";

        if (sender instanceof Player) {
            menuMessage +=
                "<dark_gray>" +
                plugin.getLanguageManager().getMessage("symbols.bullet") +
                " </dark_gray><white>/zone edit " +
                zone.getName() +
                " anchors</white><gray> - " +
                plugin
                    .getLanguageManager()
                    .getMessage("commands.zone.edit.update-boundaries") +
                "</gray>\n";
        }

        menuMessage +=
            "\n<gold><bold>═══════════════════════════════════</bold></gold>\n";

        sender.sendMessage(
            plugin.getLanguageManager().parseMiniMessage(menuMessage)
        );
    }

    private boolean handleEditDamageAmount(
        CommandSender sender,
        Zone zone,
        String[] args
    ) {
        if (args.length < 3) {
            sendError(
                sender,
                plugin
                    .getLanguageManager()
                    .getMessage(
                        "commands.usage",
                        "usage",
                        "/zone edit " +
                        zone.getName() +
                        " damageamount <amount>"
                    )
            );
            sendInfo(
                sender,
                plugin
                    .getLanguageManager()
                    .getMessage(
                        "commands.zone.edit.current-value",
                        "value",
                        String.valueOf(zone.getDamageAmount())
                    )
            );
            return true;
        }

        try {
            double damageAmount = Double.parseDouble(args[2]);
            if (damageAmount < 0) {
                sendError(
                    sender,
                    plugin
                        .getLanguageManager()
                        .getMessage("commands.zone.edit.invalid-damage-amount")
                );
                return true;
            }

            zone.setDamageAmount(damageAmount);
            plugin.getZoneManager().saveZones();

            sendSuccess(
                sender,
                plugin
                    .getLanguageManager()
                    .getMessage(
                        "commands.zone.edit.damage-amount-set",
                        "name",
                        zone.getName()
                    )
            );
            return true;
        } catch (NumberFormatException e) {
            sendError(
                sender,
                plugin
                    .getLanguageManager()
                    .getMessage("commands.zone.edit.invalid-damage-amount")
            );
            return true;
        }
    }

    private boolean handleEditDamageType(
        CommandSender sender,
        Zone zone,
        String[] args
    ) {
        if (args.length < 3) {
            sendError(
                sender,
                plugin
                    .getLanguageManager()
                    .getMessage(
                        "commands.usage",
                        "usage",
                        "/zone edit " + zone.getName() + " damagetype <type>"
                    )
            );
            sendInfo(
                sender,
                plugin
                    .getLanguageManager()
                    .getMessage("commands.zone.edit.damage-type-examples")
            );
            sendInfo(
                sender,
                plugin
                    .getLanguageManager()
                    .getMessage("commands.zone.edit.use-none-disable")
            );
            return true;
        }

        String damageTypeStr = args[2].toLowerCase();

        if (damageTypeStr.equals("none")) {
            zone.setDamageType(null);
            plugin.getZoneManager().saveZones();
            sendSuccess(
                sender,
                plugin
                    .getLanguageManager()
                    .getMessage(
                        "commands.zone.edit.damage-type-removed",
                        "name",
                        zone.getName()
                    )
            );
            return true;
        }

        try {
            DamageType damageType = getDamageType(damageTypeStr);
            if (damageType == null) {
                sendError(
                    sender,
                    plugin
                        .getLanguageManager()
                        .getMessage(
                            "commands.zone.edit.invalid-damage-type",
                            "type",
                            damageTypeStr
                        )
                );
                sendInfo(
                    sender,
                    plugin
                        .getLanguageManager()
                        .getMessage("commands.zone.edit.damage-type-examples")
                );
                return true;
            }

            zone.setDamageType(damageType);
            plugin.getZoneManager().saveZones();

            sendSuccess(
                sender,
                plugin
                    .getLanguageManager()
                    .getMessage(
                        "commands.zone.edit.damage-type-set",
                        LanguageManager.placeholders(
                            "type",
                            damageTypeStr,
                            "name",
                            zone.getName()
                        )
                    )
            );
            return true;
        } catch (Exception e) {
            sendError(
                sender,
                plugin
                    .getLanguageManager()
                    .getMessage(
                        "commands.zone.edit.failed-set-damage",
                        "error",
                        e.getMessage()
                    )
            );
            return true;
        }
    }

    private boolean handleEditDamageInterval(
        CommandSender sender,
        Zone zone,
        String[] args
    ) {
        if (args.length < 3) {
            sendError(
                sender,
                plugin
                    .getLanguageManager()
                    .getMessage(
                        "commands.usage",
                        "usage",
                        "/zone edit " +
                        zone.getName() +
                        " damageinterval <ticks>"
                    )
            );
            sendInfo(
                sender,
                plugin
                    .getLanguageManager()
                    .getMessage(
                        "commands.zone.edit.current-value",
                        "value",
                        String.valueOf(zone.getDamageInterval())
                    )
            );
            sendInfo(
                sender,
                plugin
                    .getLanguageManager()
                    .getMessage("commands.zone.edit.damage-interval-note")
            );
            return true;
        }

        try {
            long damageInterval = Long.parseLong(args[2]);
            if (damageInterval < 0) {
                sendError(
                    sender,
                    plugin
                        .getLanguageManager()
                        .getMessage(
                            "commands.zone.edit.invalid-damage-interval"
                        )
                );
                return true;
            }

            zone.setDamageInterval(damageInterval);
            plugin.getZoneManager().saveZones();

            sendSuccess(
                sender,
                plugin
                    .getLanguageManager()
                    .getMessage(
                        "commands.zone.edit.damage-interval-set",
                        "name",
                        zone.getName()
                    )
            );
            return true;
        } catch (NumberFormatException e) {
            sendError(
                sender,
                plugin
                    .getLanguageManager()
                    .getMessage("commands.zone.edit.invalid-damage-interval")
            );
            return true;
        }
    }

    private boolean handleEditDeathMessage(
        CommandSender sender,
        Zone zone,
        String[] args
    ) {
        if (args.length < 3) {
            sendError(
                sender,
                plugin
                    .getLanguageManager()
                    .getMessage(
                        "commands.usage",
                        "usage",
                        "/zone edit " +
                        zone.getName() +
                        " deathmessage <message>"
                    )
            );
            sendInfo(
                sender,
                plugin
                    .getLanguageManager()
                    .getMessage(
                        "commands.zone.edit.current-value",
                        "value",
                        zone.getDeathMessage()
                    )
            );
            return true;
        }

        String message = String.join(
            " ",
            Arrays.copyOfRange(args, 2, args.length)
        );

        zone.setDeathMessageString(message);
        plugin.getZoneManager().saveZones();

        sendSuccess(
            sender,
            plugin
                .getLanguageManager()
                .getMessage(
                    "commands.zone.edit.death-message-set",
                    "name",
                    zone.getName()
                )
        );

        return true;
    }

    private boolean handleEditDamageAnimal(
        CommandSender sender,
        Zone zone,
        String[] args
    ) {
        if (args.length < 3) {
            sendError(
                sender,
                plugin
                    .getLanguageManager()
                    .getMessage(
                        "commands.usage",
                        "usage",
                        "/zone edit " +
                        zone.getName() +
                        " damageanimal <true/false>"
                    )
            );
            sendInfo(
                sender,
                plugin
                    .getLanguageManager()
                    .getMessage(
                        "commands.zone.edit.current-value",
                        "value",
                        String.valueOf(zone.damageAnimal())
                    )
            );
            return true;
        }

        boolean damageAnimal = Boolean.parseBoolean(args[2]);
        zone.setDamageAnimal(damageAnimal);
        plugin.getZoneManager().saveZones();

        sendSuccess(
            sender,
            plugin
                .getLanguageManager()
                .getMessage(
                    "commands.zone.edit.damage-animal-set",
                    LanguageManager.placeholders(
                        "value",
                        String.valueOf(damageAnimal),
                        "name",
                        zone.getName()
                    )
                )
        );

        return true;
    }

    private boolean handleEditDamageEntity(
        CommandSender sender,
        Zone zone,
        String[] args
    ) {
        if (args.length < 3) {
            sendError(
                sender,
                plugin
                    .getLanguageManager()
                    .getMessage(
                        "commands.usage",
                        "usage",
                        "/zone edit " +
                        zone.getName() +
                        " damageentity <true/false>"
                    )
            );
            sendInfo(
                sender,
                plugin
                    .getLanguageManager()
                    .getMessage(
                        "commands.zone.edit.current-value",
                        "value",
                        String.valueOf(zone.damageEntity())
                    )
            );
            return true;
        }

        boolean damageEntity = Boolean.parseBoolean(args[2]);
        zone.setDamageEntity(damageEntity);
        plugin.getZoneManager().saveZones();

        sendSuccess(
            sender,
            plugin
                .getLanguageManager()
                .getMessage(
                    "commands.zone.edit.damage-entity-set",
                    LanguageManager.placeholders(
                        "value",
                        String.valueOf(damageEntity),
                        "name",
                        zone.getName()
                    )
                )
        );

        return true;
    }

    private boolean handleEditDamagePlayer(
        CommandSender sender,
        Zone zone,
        String[] args
    ) {
        if (args.length < 3) {
            sendError(
                sender,
                plugin
                    .getLanguageManager()
                    .getMessage(
                        "commands.usage",
                        "usage",
                        "/zone edit " +
                        zone.getName() +
                        " damageplayer <true/false>"
                    )
            );
            sendInfo(
                sender,
                plugin
                    .getLanguageManager()
                    .getMessage(
                        "commands.zone.edit.current-value",
                        "value",
                        String.valueOf(zone.damagePlayer())
                    )
            );
            return true;
        }

        boolean damagePlayer = Boolean.parseBoolean(args[2]);
        zone.setDamagePlayer(damagePlayer);
        plugin.getZoneManager().saveZones();

        sendSuccess(
            sender,
            plugin
                .getLanguageManager()
                .getMessage(
                    "commands.zone.edit.damage-player-set",
                    LanguageManager.placeholders(
                        "value",
                        String.valueOf(damagePlayer),
                        "name",
                        zone.getName()
                    )
                )
        );

        return true;
    }

    private boolean handleEditDestroyItem(
        CommandSender sender,
        Zone zone,
        String[] args
    ) {
        if (args.length < 3) {
            sendError(
                sender,
                plugin
                    .getLanguageManager()
                    .getMessage(
                        "commands.usage",
                        "usage",
                        "/zone edit " +
                        zone.getName() +
                        " destroyitem <true/false>"
                    )
            );
            sendInfo(
                sender,
                plugin
                    .getLanguageManager()
                    .getMessage(
                        "commands.zone.edit.current-value",
                        "value",
                        String.valueOf(zone.destroyItem())
                    )
            );
            return true;
        }

        boolean destroyItem = Boolean.parseBoolean(args[2]);
        zone.setDestroyItem(destroyItem);
        plugin.getZoneManager().saveZones();

        sendSuccess(
            sender,
            plugin
                .getLanguageManager()
                .getMessage(
                    "commands.zone.edit.destroy-item-set",
                    LanguageManager.placeholders(
                        "value",
                        String.valueOf(destroyItem),
                        "name",
                        zone.getName()
                    )
                )
        );

        return true;
    }

    private boolean handleEditEnabled(
        CommandSender sender,
        Zone zone,
        String[] args
    ) {
        if (args.length < 3) {
            sendError(
                sender,
                plugin
                    .getLanguageManager()
                    .getMessage(
                        "commands.usage",
                        "usage",
                        "/zone edit " + zone.getName() + " enabled <true/false>"
                    )
            );
            sendInfo(
                sender,
                plugin
                    .getLanguageManager()
                    .getMessage(
                        "commands.zone.edit.current-value",
                        "value",
                        zone.isEnabled() ? "ENABLED" : "DISABLED"
                    )
            );
            return true;
        }

        String enabledStr = args[2].toLowerCase();
        boolean enabled;

        if (
            enabledStr.equals("true") ||
            enabledStr.equals("on") ||
            enabledStr.equals("yes")
        ) {
            enabled = true;
        } else if (
            enabledStr.equals("false") ||
            enabledStr.equals("off") ||
            enabledStr.equals("no")
        ) {
            enabled = false;
        } else {
            sendError(
                sender,
                plugin
                    .getLanguageManager()
                    .getMessage(
                        "commands.zone.edit.invalid-value",
                        "property",
                        "enabled"
                    )
            );
            return true;
        }

        zone.setEnabled(enabled);
        plugin.getZoneManager().saveZones();

        sendSuccess(
            sender,
            plugin
                .getLanguageManager()
                .getMessage(
                    "commands.zone.edit.enabled-set",
                    LanguageManager.placeholders(
                        "name",
                        zone.getName(),
                        "status",
                        enabled ? "enabled" : "disabled"
                    )
                )
        );

        return true;
    }

    private DamageType getDamageType(String name) {
        try {
            NamespacedKey key = NamespacedKey.minecraft(name);
            return RegistryAccess.registryAccess()
                .getRegistry(RegistryKey.DAMAGE_TYPE)
                .get(key);
        } catch (Exception e) {
            return null;
        }
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
        } else if (args.length == 2) {
            String partial = args[1].toLowerCase();
            List<String> properties = Arrays.asList(
                "damageamount",
                "damagetype",
                "damageinterval",
                "deathmessage",
                "damageanimal",
                "damageentity",
                "damageplayer",
                "destroyitem",
                "enabled"
            );

            for (String property : properties) {
                if (property.startsWith(partial)) {
                    completions.add(property);
                }
            }
        } else if (args.length == 3) {
            String property = args[1].toLowerCase();
            String partial = args[2].toLowerCase();

            switch (property) {
                case "enabled":
                case "damageanimal":
                case "damageentity":
                case "damageplayer":
                case "destroyitem":
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
            }
        }

        return completions;
    }
}
