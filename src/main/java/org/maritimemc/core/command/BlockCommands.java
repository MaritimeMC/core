package org.maritimemc.core.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.maritimemc.core.Module;

import java.util.Arrays;
import java.util.List;

/**
 * Module to block use of some commands.
 */
public class BlockCommands implements Module {

    private final List<String> HELP = Arrays.asList(
            "help",
            "/help",
            "?",
            "/?",
            "bukkit:help",
            "/bukkit:help",
            "bukkit:?",
            "/bukkit:?"
    );

    private final List<String> PLUGINS = Arrays.asList(
            "pl", "/pl", "bukkit:pl", "/bukkit:pl",
            "plugins", "/plugins", "bukkit:plugins", "/bukkit:plugins",
            "ver", "/ver", "bukkit:ver", "/bukkit:ver",
            "version", "/version", "bukkit:version", "/bukkit:version",
            "icanhasbukkit"
    );

    private final List<String> ME = Arrays.asList("me", "/me", "bukkit:me", "/bukkit:me");

    public BlockCommands() {
        Module.registerEvents(this);
    }

    @EventHandler
    public void playerCommandPreProcess(PlayerCommandPreprocessEvent event) {

        if (PLUGINS.contains(event.getMessage())) {
            event.setCancelled(true);
            pluginMessage(event.getPlayer());
        } else if (HELP.contains(event.getMessage())) {
            event.setCancelled(true);
            helpMessage(event.getPlayer());
        } else if (ME.contains(event.getMessage())) {
            event.setCancelled(true);
            meMessage(event.getPlayer());
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
        player.sendMessage(color(" &9/contact&7."));
        player.sendMessage(" ");
        player.sendMessage(color(" &7You can visit our store and Discord using"));
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
