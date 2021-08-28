package org.maritimemc.core.teleport;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.maritimemc.core.Formatter;
import org.maritimemc.core.command.CommandBase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TeleportCommand extends CommandBase {

    public TeleportCommand(String name) {
        super(name);
        setAliases(Collections.singletonList("tp"));
        setRequiredPermission(TeleportModule.TeleportPerm.TELEPORT_COMMAND);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        Player player = (Player) sender;

        if (args.length == 0) {
            sendUsage(player);
            return;
        }

        if (args.length == 1) {
            // Teleport to specific player
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                player.sendMessage(Formatter.format("Teleport", "That player is not online."));
                return;
            }

            player.teleport(target);
            player.sendMessage(Formatter.format("Teleport", "You were teleported to &a" + target.getName() + "&7."));

        } else if (args.length == 2) {

            if (args[0].equalsIgnoreCase("here")) {

                if (args[1].equalsIgnoreCase("all")) {
                    // Teleport all to player

                    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {

                        if (onlinePlayer != player) {
                            onlinePlayer.teleport(player);
                        }

                    }

                    player.sendMessage(Formatter.format("Teleport", "You teleported all players to you."));

                } else {
                    // Teleport specific target (args[1]) to player

                    Player target = Bukkit.getPlayer(args[1]);
                    if (target == null) {
                        player.sendMessage(Formatter.format("Teleport", "That player is not online."));
                        return;
                    }

                    target.teleport(player);
                    player.sendMessage(Formatter.format("Teleport", "You teleported &a" + target.getName() + " &7to you."));

                }

            } else {
                if (args[0].equalsIgnoreCase("all")) {
                    // Teleport all players to target (args[1])
                    Player target = Bukkit.getPlayer(args[1]);
                    if (target == null) {
                        player.sendMessage(Formatter.format("Teleport", "That player is not online."));
                        return;
                    }

                    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                        onlinePlayer.teleport(target);
                    }

                    player.sendMessage(Formatter.format("Teleport", "You teleported all players to &a" + target.getName() + "&7."));

                } else {
                    // Teleport specific player to target (args[1])

                    Player playerToTeleport = Bukkit.getPlayer(args[0]);
                    if (playerToTeleport == null) {
                        player.sendMessage(Formatter.format("Teleport", "That player is not online."));
                        return;
                    }

                    Player teleportTo = Bukkit.getPlayer(args[1]);
                    if (teleportTo == null) {
                        player.sendMessage(Formatter.format("Teleport", "That player is not online."));
                        return;
                    }

                    playerToTeleport.teleport(teleportTo);
                    player.sendMessage(Formatter.format("Teleport", "You teleported &a" + playerToTeleport.getName() + " &7to &a" + teleportTo.getName() + "&7."));

                }

            }

        } else {
            sendUsage(player);
        }

    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            List<String> tabs = new ArrayList<>();
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                tabs.add(onlinePlayer.getName());
            }

            tabs.add("here");
            tabs.add("all");

            return tabs;
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("here")) {
                List<String> tabs = new ArrayList<>();
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    tabs.add(onlinePlayer.getName());
                }

                tabs.add("all");

                return tabs;
            } else {
                return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
            }

        }

        return null;
    }

    private void sendUsage(Player player) {

        player.sendMessage(Formatter.format("Teleport", "Incorrect Arguments: Listing usage..."));
        player.sendMessage(Formatter.format("Teleport", "&9/tp <player> &7Teleports you to another player."));
        player.sendMessage(Formatter.format("Teleport", "&9/tp here <player/all> &7Teleports another player/all players to you."));
        player.sendMessage(Formatter.format("Teleport", "&9/tp <player/all> <player> &7Teleports another player/all players to another player."));

    }
}