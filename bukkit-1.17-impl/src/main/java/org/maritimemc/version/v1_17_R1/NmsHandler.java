package org.maritimemc.version.v1_17_R1;

import lombok.SneakyThrows;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.SkullMeta;
import org.maritimemc.abstraction.INmsHandler;

import java.util.UUID;

public class NmsHandler implements INmsHandler {

    @Override
    public void sendTitle(Player player, String title, String subtitle, int fadeIn, int hold, int fadeOut) {
        subtitle = ChatColor.translateAlternateColorCodes('&', subtitle);
        title = ChatColor.translateAlternateColorCodes('&', title);

        player.sendTitle(title, subtitle, fadeIn, hold, fadeOut);
    }

    @Override
    public void sendActionBar(Player player, String message) {
        message = ChatColor.translateAlternateColorCodes('&', message);

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
    }

    @SneakyThrows
    @Override
    public void sendTabHeaderFooter(Player player, String header, String footer) {
        header = ChatColor.translateAlternateColorCodes('&', header);
        footer = ChatColor.translateAlternateColorCodes('&', footer);

        player.setPlayerListHeaderFooter(header, footer);
    }

    @Override
    public Sound getNotePling() {
        return Sound.BLOCK_NOTE_BLOCK_PLING;
    }

    @Override
    public void setSkullOwner(SkullMeta meta, String data) {
        meta.setOwningPlayer(Bukkit.getOfflinePlayer(UUID.fromString(data)));
    }

    @Override
    public boolean usesSkullUUIDs() {
        return true;
    }

}
