package org.maritimemc.core.sync.command;

import org.bukkit.command.CommandSender;
import org.maritimemc.core.Formatter;
import org.maritimemc.core.command.CommandBase;
import org.maritimemc.core.sync.DiscordSyncModule;
import org.maritimemc.core.sync.LinkManager;
import org.maritimemc.core.util.UuidNameFetcher;

import java.util.List;
import java.util.UUID;

public class ForceLinkCommand extends CommandBase {

    private final LinkManager linkManager;

    public ForceLinkCommand(String name, LinkManager linkManager) {
        super(name);
        this.linkManager = linkManager;
        setConsoleExecutable(true);
        setRequiredPermission(DiscordSyncModule.DiscordSyncPerm.FORCE_LINK);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (args.length != 2) {
            sender.sendMessage(Formatter.format("Link", "&9/forcelink <name> <id> &7Forcibly links a Minecraft Account to a Discord ID."));
            return;
        }

        UUID uuid = UuidNameFetcher.fetchUuid(args[0]);
        if (uuid == null) {
            sender.sendMessage(Formatter.format("Link", "That is not a valid Minecraft name."));
        } else {

            try {
                long l = Long.parseLong(args[1]);

                if (linkManager.isUserLinked(uuid)) {
                    sender.sendMessage(Formatter.format("Link", "That Minecraft Account is already linked to a Discord ID."));
                } else if (linkManager.isUserLinked(l)) {
                    sender.sendMessage(Formatter.format("Link", "That Discord ID is already linked to a Minecraft Account."));
                } else {
                    linkManager.linkUser(uuid, l);
                    sender.sendMessage(Formatter.format("Link", "You linked the accounts."));
                }

            } catch (NumberFormatException exception) {
                sender.sendMessage(Formatter.format("Link", "That is not a valid Discord ID."));
            }

        }

    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return null;
    }
}
