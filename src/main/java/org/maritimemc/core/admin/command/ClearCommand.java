package org.maritimemc.core.admin.command;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.maritimemc.core.Formatter;
import org.maritimemc.core.admin.Administrate;
import org.maritimemc.core.command.CommandBase;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ClearCommand extends CommandBase {

    public ClearCommand(String name) {
        super(name);
        setAliases(Arrays.asList("clearinv", "ci", "cl"));
        setRequiredPermission(Administrate.AdministratePerm.CLEAR_COMMAND);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        if (args.length == 0) {
            player.getInventory().clear();
            player.getInventory().setArmorContents(new ItemStack[]{});
            player.sendMessage(Formatter.format("Clear", "You cleared your inventory."));
        } else {
            Player target = Bukkit.getPlayer(args[0]);
            if (target != null) {
                target.getInventory().clear();
                target.getInventory().setArmorContents(new ItemStack[]{});
                player.sendMessage(Formatter.format("Clear", "You cleared the inventory of &a" + target.getName() + "&7."));
            } else {
                player.sendMessage(Formatter.format("Clear", "That player is not online."));
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
