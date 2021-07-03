package org.maritimemc.core.vanish.command;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.maritimemc.core.Formatter;
import org.maritimemc.core.command.CommandBase;
import org.maritimemc.core.perm.PermissionManager;
import org.maritimemc.core.service.Locator;
import org.maritimemc.core.vanish.VanishManager;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.maritimemc.core.service.Locator.locate;

public class CommandVanishForce extends CommandBase {

    private final VanishManager vanishManager = locate(VanishManager.class);

    /**
     * Class constructor
     *
     * @param name The name of this command.
     */
    public CommandVanishForce(String name) {
        super(name);
        setAliases(Collections.singletonList("vf"));
        setRequiredPermission(VanishManager.VanishPerm.VANISH_ADMIN);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage(Formatter.format("Vanish", "&9/vf <player> &7Toggle a player's vanish."));
            return;
        }

        String name = args[0];
        Player target = Bukkit.getPlayer(name);

        if (target == null) {
            player.sendMessage(Formatter.format("Vanish", "That player is not online."));
            return;
        }

        if (!vanishManager.canUseVanish(target)) {
            player.sendMessage(Formatter.format("Vanish", "&7That player does not have permission to use vanish."));
            return;
        }

        if (vanishManager.isVanished(target)) {
            vanishManager.disableVanish(target, "Disabled by an administrator");
            player.sendMessage(Formatter.format("Vanish", "You disabled &b" + target.getName() + "&7's vanish."));
        } else {
            vanishManager.enableVanish(target, "Enabled by an administrator");
            player.sendMessage(Formatter.format("Vanish", "You enabled &b" + target.getName() + "&7's vanish."));
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
        }

        return null;
    }
}
