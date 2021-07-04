package org.maritimemc.version.v1_17_R1;

import lombok.SneakyThrows;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.maritimemc.core.Formatter;
import org.maritimemc.core.versioning.INmsHandler;

public class NmsHandler implements INmsHandler {

    @Override
    public void sendTitle(Player player, String title, String subtitle, int fadeIn, int hold, int fadeOut) {
        title = Formatter.format(title);
        subtitle = Formatter.format(subtitle);

        player.sendTitle(title, subtitle, fadeIn, hold, fadeOut);
    }

    @Override
    public void sendActionBar(Player player, String message) {
        message = Formatter.format(message);

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
    }

    @SneakyThrows
    @Override
    public void sendTabHeaderFooter(Player player, String header, String footer) {
        header = Formatter.format(header);
        footer = Formatter.format(footer);

        player.setPlayerListHeaderFooter(header, footer);
    }

}
