package org.maritimemc.core.server;

import org.bukkit.command.CommandSender;
import org.maritimemc.core.Formatter;
import org.maritimemc.core.command.CommandBase;
import org.maritimemc.core.profile.ProfileManager;
import org.maritimemc.core.service.Locator;
import org.maritimemc.core.util.UuidNameFetcher;
import org.maritimemc.data.player.PlayerProfile;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class CommandFind extends CommandBase {

    private final ProfileManager profileManager = Locator.locate(ProfileManager.class);

    public CommandFind(String name) {
        super(name);
        setAliases(Collections.singletonList("locate"));
        setConsoleExecutable(true);
        setRequiredPermission(BungeeInfoModule.BungeeInfoPermission.FIND_COMMAND);
        setExecuteAsync(true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(Formatter.format("Find", "&9/find <player> &7Find a networked player."));
            return;
        }

        String name = args[0];
        UUID uuid = UuidNameFetcher.fetchUuid(name);

        if (uuid == null) {
            sender.sendMessage(Formatter.format("Find", "&7That player does not exist."));
            return;
        }

        PlayerProfile profile = profileManager.getCached(uuid);
        if (profile == null) {
            profile = profileManager.getFromRedis(uuid);
        }

        if (profile == null) {
            sender.sendMessage(Formatter.format("Find", "That player is not online."));
            return;
        }

        sender.sendMessage(Formatter.format("Find", "&a" + profile.getName() + " &7is in &a" + profile.getServerName() + "&7."));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return null;
    }
}
