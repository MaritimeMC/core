package org.maritimemc.core.admin.command;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.maritimemc.core.Formatter;
import org.maritimemc.core.admin.Administrate;
import org.maritimemc.core.command.CommandBase;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GamemodeCommand extends CommandBase {

    public GamemodeCommand(String name) {
        super(name);
        setAliases(Arrays.asList("gm", "creative"));
        setRequiredPermission(Administrate.AdministratePerm.GAMEMODE_COMMAND);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        if (args.length == 0) {
            if (player.getGameMode() != GameMode.CREATIVE) {
                player.setGameMode(GameMode.CREATIVE);
                player.sendMessage(Formatter.format("Gamemode", player.getName() + "'s Creative Mode: &aEnabled"));
            } else {
                player.setGameMode(GameMode.SURVIVAL);
                player.sendMessage(Formatter.format("Gamemode", player.getName() + "'s Creative Mode: &cDisabled"));
            }
        } else {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                player.sendMessage(Formatter.format("Gamemode", "That player is not online."));
            } else {

                if (target.getGameMode() != GameMode.CREATIVE) {
                    target.setGameMode(GameMode.CREATIVE);
                    player.sendMessage(Formatter.format("Gamemode", target.getName() + "'s Creative Mode: &aEnabled"));
                } else {
                    target.setGameMode(GameMode.SURVIVAL);
                    player.sendMessage(Formatter.format("Gamemode", target.getName() + "'s Creative Mode: &cDisabled"));
                }

            }
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1)
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
        return null;
    }
}
