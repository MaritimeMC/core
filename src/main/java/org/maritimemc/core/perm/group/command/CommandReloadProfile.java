package org.maritimemc.core.perm.group.command;

import org.bukkit.command.CommandSender;
import org.maritimemc.core.Formatter;
import org.maritimemc.core.command.CommandBase;
import org.maritimemc.core.db.messaging.DatabaseMessageManager;
import org.maritimemc.core.db.messaging.format.StringMessageFormat;
import org.maritimemc.core.perm.group.GroupManagementModule;
import org.maritimemc.core.profile.ProfileManager;
import org.maritimemc.core.service.Locator;
import org.maritimemc.core.util.UuidNameFetcher;

import javax.xml.crypto.Data;
import java.util.List;
import java.util.UUID;

public class CommandReloadProfile extends CommandBase {

    private final DatabaseMessageManager databaseMessageManager = Locator.locate(DatabaseMessageManager.class);

    public CommandReloadProfile(String name) {
        super(name);
        setConsoleExecutable(true);
        setRequiredPermission(GroupManagementModule.GroupPerm.PROFILE_RELOAD_COMMAND);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (args.length == 0) {
            sender.sendMessage(Formatter.format("Profile", "&9/reloadprofile <player>"));
            return;
        }

        UUID uuid = UuidNameFetcher.fetchUuid(args[0]);

        if (uuid == null) {
            sender.sendMessage(Formatter.format("Profile", "That player does not exist."));
            return;
        }

        databaseMessageManager.send(ProfileManager.RELOAD_PROFILE_CHANNEL, new StringMessageFormat(uuid.toString()));
        sender.sendMessage(Formatter.format("Profile", "Commenced."));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return null;
    }
}
