package org.maritimemc.core.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.maritimemc.core.Module;
import org.maritimemc.core.util.UtilVersion;
import org.maritimemc.core.versioning.VersionHandler;
import org.maritimemc.data.versioning.ServerVersion;

/**
 * Module to block use of some commands.
 */
public class BlockCommands implements Module {

    @EventHandler
    public void playerCommandPreProcess(PlayerCommandPreprocessEvent event) {

        if (UtilVersion.getVersion() == ServerVersion._18) {
            if (event.getMessage().equalsIgnoreCase("plugins") || event.getMessage().equalsIgnoreCase("pl")) {
                event.setCancelled(true);
                pluginMessage(event.getPlayer());
            }

            if (event.getMessage().equalsIgnoreCase("help") || event.getMessage().equalsIgnoreCase("?")) {
                event.setCancelled(true);
                helpMessage(event.getPlayer());
            }

            if (event.getMessage().equalsIgnoreCase("ver") || event.getMessage().equalsIgnoreCase("version") || event.getMessage().equalsIgnoreCase("icanhasbukkit")) {
                event.setCancelled(true);
                pluginMessage(event.getPlayer());
            }

            if (event.getMessage().equalsIgnoreCase("me")) {
                event.setCancelled(true);
                meMessage(event.getPlayer());
            }   
        } else {
            if (event.getMessage().equalsIgnoreCase("/plugins") || event.getMessage().equalsIgnoreCase("/pl")) {
                event.setCancelled(true);
                pluginMessage(event.getPlayer());
            }

            if (event.getMessage().equalsIgnoreCase("/help") || event.getMessage().equalsIgnoreCase("/?")) {
                event.setCancelled(true);
                helpMessage(event.getPlayer());
            }

            if (event.getMessage().equalsIgnoreCase("/ver") || event.getMessage().equalsIgnoreCase("/version") || event.getMessage().equalsIgnoreCase("/icanhasbukkit")) {
                event.setCancelled(true);
                pluginMessage(event.getPlayer());
            }

            if (event.getMessage().equalsIgnoreCase("/me")) {
                event.setCancelled(true);
                meMessage(event.getPlayer());
            }
        }

    }

    private void pluginMessage(Player player) {
        player.sendMessage(color("&cYou are not allowed to do this."));
    }

    private void helpMessage(Player player) {

        player.sendMessage(" ");
        player.sendMessage(color(" &9&lMaritimeMC"));
        player.sendMessage(" ");
        player.sendMessage(color(" &7Need help? Ask a staff member using"));
        player.sendMessage(color(" &9contact&7."));
        player.sendMessage(" ");
        player.sendMessage(color(" &7You can visit our store and forums using"));
        player.sendMessage(color(" &7the link &9https://maritimemc.org&7."));
        player.sendMessage(" ");

    }

    private void meMessage(Player player) {
        player.sendMessage(color("&cYou are not allowed to do this."));
    }

    private String color(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }
}
