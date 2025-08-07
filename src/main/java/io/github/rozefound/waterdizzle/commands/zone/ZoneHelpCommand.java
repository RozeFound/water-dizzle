package io.github.rozefound.waterdizzle.commands.zone;

import io.github.rozefound.waterdizzle.WaterDizzle;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;

/**
 * Command for displaying zone command help with styled formatting
 */
public class ZoneHelpCommand extends AbstractZoneSubCommand {

    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    public ZoneHelpCommand(WaterDizzle plugin) {
        super(
            plugin,
            "help",
            null,
            "/zone help",
            "Display help for zone commands",
            false
        );
    }

    @Override
    protected boolean executeCommand(CommandSender sender, String[] args) {
        StringBuilder helpMessage = new StringBuilder();

        helpMessage.append(buildHeader());

        helpMessage.append(buildZoneCreationSection());
        helpMessage.append(buildZoneManagementSection());
        helpMessage.append(buildPluginInfoSection());
        helpMessage.append(buildZoneConfigSection());
        helpMessage.append(buildZoneConditionsSection());
        helpMessage.append(buildDamageTypesSection());
        helpMessage.append(buildQuickTipsSection());
        helpMessage.append(buildParticleColorsSection());

        helpMessage.append(buildFooter());

        sender.sendMessage(miniMessage.deserialize(helpMessage.toString()));
        return true;
    }

    private String buildHeader() {
        return (
            "<gradient:#7A20D2:#0BE372>┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓\n" +
            "<gradient:#7A20D2:#0BE372>┃         <bold><white>" +
            plugin.getLanguageManager().getMessage("commands.zone.help.title") +
            "</white></bold>         ┃\n" +
            "<gradient:#7A20D2:#0BE372>┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛</gradient>\n\n"
        );
    }

    private String buildZoneCreationSection() {
        String zap = plugin.getLanguageManager().getMessage("symbols.zap");

        return (
            "<gradient:#8E44AD:#2ECC71>" +
            zap +
            " <white>" +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.help.zone-creation-setup") +
            "</white></gradient>\n" +
            buildCommand(
                "/zone select [zone | new <name>]",
                "commands.zone.help.select-hover",
                "commands.zone.help.select-description"
            ) +
            buildCommand(
                "/zone deselect",
                "commands.zone.help.deselect-hover",
                "commands.zone.help.deselect-description"
            ) +
            "\n"
        );
    }

    private String buildZoneManagementSection() {
        String zap = plugin.getLanguageManager().getMessage("symbols.zap");

        return (
            "<gradient:#8E44AD:#2ECC71>" +
            zap +
            " <white>" +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.help.zone-management") +
            "</white></gradient>\n" +
            buildCommand(
                "/zone list",
                "commands.zone.help.list-hover",
                "commands.zone.help.list-description"
            ) +
            buildCommand(
                "/zone info <name>",
                "commands.zone.help.info-hover",
                "commands.zone.help.info-description"
            ) +
            buildCommand(
                "/zone enableVisibleBorders",
                "commands.zone.help.borders-hover",
                "commands.zone.help.borders-description"
            ) +
            buildCommand(
                "/zone remove <name>",
                "commands.zone.help.remove-hover",
                "commands.zone.help.remove-description"
            ) +
            "\n"
        );
    }

    private String buildPluginInfoSection() {
        String zap = plugin.getLanguageManager().getMessage("symbols.zap");

        return (
            "<gradient:#8E44AD:#2ECC71>" +
            zap +
            " <white>" +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.help.plugin-information") +
            "</white></gradient>\n" +
            buildCommand(
                "/zone version",
                "commands.zone.help.version-hover",
                "commands.zone.help.version-description"
            ) +
            buildCommand(
                "/zone author",
                "commands.zone.help.author-hover",
                "commands.zone.help.author-description"
            ) +
            buildCommand(
                "/zone reload",
                "commands.zone.help.reload-hover",
                "commands.zone.help.reload-description"
            ) +
            "\n"
        );
    }

    private String buildZoneConfigSection() {
        String zap = plugin.getLanguageManager().getMessage("symbols.zap");
        return (
            "<gradient:#8E44AD:#2ECC71>" +
            zap +
            " <white>" +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.help.zone-configuration") +
            "</white></gradient>\n" +
            buildCommand(
                "/zone edit <name>",
                "commands.zone.help.edit-hover",
                "commands.zone.help.edit-description"
            ) +
            buildSubCommand(
                "enabled",
                "[true/false]",
                "commands.zone.help.enabled-hover",
                "├"
            ) +
            buildSubCommand(
                "damagetype",
                "[type/none]",
                "commands.zone.help.damagetype-hover",
                "├"
            ) +
            buildSubCommand(
                "damageamount",
                "[amount]",
                "commands.zone.help.damageamount-hover",
                "├"
            ) +
            buildSubCommand(
                "damageinterval",
                "[ticks]",
                "commands.zone.help.damageinterval-hover",
                "├"
            ) +
            buildSubCommand(
                "deathmessage",
                "[message]",
                "commands.zone.help.deathmessage-hover",
                "├"
            ) +
            buildSubCommand(
                "damageanimal",
                "[true/false]",
                "commands.zone.help.damageanimal-hover",
                "├"
            ) +
            buildSubCommand(
                "damageentity",
                "[true/false]",
                "commands.zone.help.damageentity-hover",
                "├"
            ) +
            buildSubCommand(
                "damageplayer",
                "[true/false]",
                "commands.zone.help.damageplayer-hover",
                "├"
            ) +
            buildSubCommand(
                "destroyitem",
                "[true/false]",
                "commands.zone.help.destroyitem-hover",
                "└"
            ) +
            "\n"
        );
    }

    private String buildZoneConditionsSection() {
        String zap = plugin.getLanguageManager().getMessage("symbols.zap");

        return (
            "<gradient:#8E44AD:#2ECC71>" +
            zap +
            " <white>" +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.help.zone-conditions") +
            "</white></gradient>\n" +
            buildCommand(
                "/zone condition <name>",
                "commands.zone.help.condition-hover",
                "commands.zone.help.condition-description"
            ) +
            buildSubCommand(
                "add",
                "[direction] [material]",
                "commands.zone.help.add-hover",
                "├"
            ) +
            buildSubCommand(
                "remove",
                "[index]",
                "commands.zone.help.remove-hover",
                "├"
            ) +
            buildSubCommand(
                "list",
                "",
                "commands.zone.help.list-conditions-hover",
                "├"
            ) +
            buildSubCommand(
                "clear",
                "",
                "commands.zone.help.clear-hover",
                "└"
            ) +
            "\n"
        );
    }

    private String buildDamageTypesSection() {
        String zap = plugin.getLanguageManager().getMessage("symbols.zap");
        String bullet = plugin
            .getLanguageManager()
            .getMessage("symbols.bullet");

        StringBuilder section = new StringBuilder();
        section
            .append("<gradient:#8E44AD:#2ECC71>")
            .append(zap)
            .append(" <white>")
            .append(
                plugin
                    .getLanguageManager()
                    .getMessage("commands.zone.help.damage-types")
            )
            .append("</white></gradient>\n");

        String[] damageTypes = {
            "drowning",
            "freezing",
            "lava",
            "fire",
            "wither",
            "poison",
            "magic",
            "starve",
        };
        for (String type : damageTypes) {
            section
                .append("<gradient:#9B59B6:#27AE60>")
                .append(bullet)
                .append(" <red>")
                .append(type)
                .append("</red> <dark_gray>-</dark_gray> <gray>")
                .append(
                    plugin
                        .getLanguageManager()
                        .getMessage("commands.zone.help." + type + "-desc")
                )
                .append("</gray>\n");
        }
        section.append("\n");

        return section.toString();
    }

    private String buildQuickTipsSection() {
        String bulb = plugin.getLanguageManager().getMessage("symbols.bulb");
        String bullet = plugin
            .getLanguageManager()
            .getMessage("symbols.bullet");

        StringBuilder section = new StringBuilder();
        section
            .append("<gradient:#8E44AD:#2ECC71>")
            .append(bulb)
            .append(" <white>")
            .append(
                plugin
                    .getLanguageManager()
                    .getMessage("commands.zone.help.quick-tips")
            )
            .append("</white></gradient>\n");

        for (int i = 1; i <= 6; i++) {
            section
                .append("<gradient:#9B59B6:#27AE60>")
                .append(bullet)
                .append(" <yellow>")
                .append(
                    plugin
                        .getLanguageManager()
                        .getMessage("commands.zone.help.tip" + i)
                )
                .append("</yellow>\n");
        }
        section.append("\n");

        return section.toString();
    }

    private String buildParticleColorsSection() {
        String chart = plugin.getLanguageManager().getMessage("symbols.chart");
        String bullet = plugin
            .getLanguageManager()
            .getMessage("symbols.bullet");

        return (
            "<gradient:#8E44AD:#2ECC71>" +
            chart +
            " <white>" +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.help.particle-colors") +
            "</white></gradient>\n" +
            "<gradient:#9B59B6:#27AE60>" +
            bullet +
            " <green>Green</green> <dark_gray>-</dark_gray> <gray>" +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.help.green-particles") +
            "</gray>\n" +
            "<gradient:#9B59B6:#27AE60>" +
            bullet +
            " <red>Red</red> <dark_gray>-</dark_gray> <gray>" +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.help.red-particles") +
            "</gray>\n" +
            "<gradient:#9B59B6:#27AE60>" +
            bullet +
            " <gray>Gray</gray> <dark_gray>-</dark_gray> <gray>" +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.help.gray-particles") +
            "</gray>\n\n"
        );
    }

    private String buildFooter() {
        return (
            "<gradient:#7A20D2:#0BE372>◇◇◇◇◇◇◇◇◇◇◇◇◇◇◇◇◇◇◇◇◇◇◇◇◇◇◇◇◇◇◇◇◇◇◇◇◇◇◇◇◇◇◇◇◇◇◇</gradient>\n" +
            "<gradient:#9B59B6:#27AE60>" +
            buildFooterLink(
                "/zone list",
                "commands.zone.help.footer-list-hover"
            ) +
            " <gray>|</gray> " +
            buildFooterLink(
                "/zone help",
                "commands.zone.help.footer-help-hover"
            ) +
            " <gray>|</gray> " +
            buildFooterLink(
                "/zone version",
                "commands.zone.help.footer-version-hover"
            ) +
            " <gray>|</gray> " +
            buildFooterLink(
                "/zone author",
                "commands.zone.help.footer-author-hover"
            ) +
            "</gradient>"
        );
    }

    private String buildCommand(
        String command,
        String hoverKey,
        String descKey
    ) {
        String bullet = plugin
            .getLanguageManager()
            .getMessage("symbols.bullet");
        String hover = plugin.getLanguageManager().getMessage(hoverKey);
        String desc = plugin.getLanguageManager().getMessage(descKey);

        String baseCommand =
            command.split(" ")[0] + " " + command.split(" ")[1] + " ";

        return (
            "<gradient:#9B59B6:#27AE60>" +
            bullet +
            " <hover:show_text:'<gray>" +
            hover +
            "</gray>'>" +
            "<click:suggest_command:'" +
            baseCommand +
            "'>" +
            command +
            "</click></hover> <dark_gray>- " +
            desc +
            "</dark_gray>\n"
        );
    }

    private String buildSubCommand(
        String property,
        String args,
        String hoverKey,
        String branch
    ) {
        String hover = plugin.getLanguageManager().getMessage(hoverKey);

        return (
            "<gradient:#9B59B6:#27AE60>  <gray>" +
            branch +
            "</gray> " +
            "<hover:show_text:'<gray>" +
            hover +
            "</gray>'>" +
            "<click:suggest_command:'/zone edit <name> " +
            property +
            " '>" +
            "<aqua>" +
            property +
            "</aqua> <green>" +
            args +
            "</green>" +
            "</click></hover>\n"
        );
    }

    private String buildFooterLink(String command, String hoverKey) {
        String hover = plugin.getLanguageManager().getMessage(hoverKey);
        String baseCommand = command.endsWith(" ") ? command : command + " ";

        return (
            "<hover:show_text:'<gray>" +
            hover +
            "</gray>'>" +
            "<click:suggest_command:'" +
            baseCommand +
            "'>" +
            command +
            "</click></hover>"
        );
    }
}
