package io.github.rozefound.waterdizzle.commands.zone;

import io.github.rozefound.waterdizzle.WaterDizzle;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;

/**
 * Command for displaying plugin author information
 */
public class ZoneAuthorCommand extends AbstractZoneSubCommand {

    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    public ZoneAuthorCommand(WaterDizzle plugin) {
        super(
            plugin,
            "author",
            null,
            "/zone author",
            "Display plugin author information",
            false
        );
    }

    @Override
    protected boolean executeCommand(CommandSender sender, String[] args) {
        String authorMessage =
            "\n<gradient:#3498DB:#2980B9>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>\n" +
            "<gradient:#3498DB:#2980B9>         <bold>" +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.author.title") +
            "</bold></gradient>\n" +
            "<gradient:#3498DB:#2980B9>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>\n\n" +
            "<gradient:#9B59B6:#27AE60>" +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.author.author") +
            ":</gradient> <yellow>RozeFound</yellow>\n" +
            "<gradient:#9B59B6:#27AE60>" +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.author.github") +
            ":</gradient> <aqua><click:open_url:'https://github.com/rozefound'><hover:show_text:'<gray>" +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.author.click-visit") +
            "</gray>'>github.com/rozefound</hover></click></aqua>\n" +
            "<gradient:#9B59B6:#27AE60>" +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.author.repository") +
            ":</gradient> <aqua><click:open_url:'https://github.com/rozefound/water-dizzle'><hover:show_text:'<gray>" +
            plugin
                .getLanguageManager()
                .getMessage("commands.zone.author.click-visit") +
            "</gray>'>water-dizzle</hover></click></aqua>\n\n" +
            "<gradient:#3498DB:#2980B9>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>\n";

        sender.sendMessage(miniMessage.deserialize(authorMessage));
        return true;
    }
}
