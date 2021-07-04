package org.maritimemc.core.twofactor.command;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.maritimemc.core.Formatter;
import org.maritimemc.core.command.CommandBase;
import org.maritimemc.core.twofactor.TwoFactor;
import org.maritimemc.core.util.UuidNameFetcher;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Command to reset two-factor authentication
 * for a player.
 */
public class ResetCommand extends CommandBase {

    private final TwoFactor twoFactor;

    public ResetCommand(String name, TwoFactor twoFactor) {
        super(name);
        this.twoFactor = twoFactor;
        setRequiredPermission(TwoFactor.TwoFactorPerm.RESET_2FA);
        setConsoleExecutable(true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (args.length != 1) {
            sender.sendMessage(Formatter.format("2FA", "&a/2fareset <player> &7Resets a player's 2FA key."));
        } else {

            UUID uuid = UuidNameFetcher.fetchUuid(args[0]);
            if (uuid == null) {
                sender.sendMessage(Formatter.format("2FA", "That is not a valid name."));
                return;
            }

            twoFactor.getTwoFactorManager().tryResetUser(uuid);
            sender.sendMessage(Formatter.format("2FA", "You reset the player's 2FA."));

        }

    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
    }
}
