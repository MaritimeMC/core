package org.maritimemc.core.twofactor.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.maritimemc.core.Formatter;
import org.maritimemc.core.command.CommandBase;
import org.maritimemc.core.twofactor.TwoFactor;

import java.util.List;

/**
 * Command to initiate two-factor authentication
 * setup.
 */
public class SetupCommand extends CommandBase {

    private final TwoFactor twoFactor;

    public SetupCommand(String name, TwoFactor twoFactor) {
        super(name);
        this.twoFactor = twoFactor;
        setRequiredPermission(TwoFactor.TwoFactorPerm.USE_2FA);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        Player player = (Player) sender;

        if (twoFactor.getTwoFactorDb().hasDataInDb(player.getUniqueId())) {
            player.sendMessage(Formatter.format("2FA", "You have already setup two-factor authentication."));
        } else {
            twoFactor.getTwoFactorManager().setupPlayer(player);
        }

    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return null;
    }
}
