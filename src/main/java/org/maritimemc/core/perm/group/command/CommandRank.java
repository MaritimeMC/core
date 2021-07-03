package org.maritimemc.core.perm.group.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.maritimemc.core.Formatter;
import org.maritimemc.core.command.CommandBase;
import org.maritimemc.core.db.messaging.DatabaseMessageManager;
import org.maritimemc.core.db.messaging.format.StringMessageFormat;
import org.maritimemc.core.perm.RemotePermissionManager;
import org.maritimemc.core.perm.group.GroupManagementModule;
import org.maritimemc.core.profile.ProfileManager;
import org.maritimemc.core.thread.ThreadPool;
import org.maritimemc.core.util.UuidNameFetcher;
import org.maritimemc.data.perm.PermissionGroup;

import java.util.*;
import java.util.stream.Collectors;

import static org.maritimemc.core.service.Locator.locate;

public class CommandRank extends CommandBase {

    private final RemotePermissionManager remotePermissionManager = locate(RemotePermissionManager.class);
    private final DatabaseMessageManager databaseMessageManager = locate(DatabaseMessageManager.class);

    public CommandRank(String name) {
        super(name);

        setAliases(Arrays.asList("group", "rankset", "setrank"));
        setConsoleExecutable(true);
        setExecuteAsync(true);
        setRequiredPermission(GroupManagementModule.GroupPerm.RANK_COMMAND);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 2 && args[0].equalsIgnoreCase("info")) {
            // Print rank information

            UUID uuid = UuidNameFetcher.fetchUuid(args[1]);
            if (uuid == null) {
                sender.sendMessage(Formatter.format("Ranks", "That is not a valid username."));
                return;
            }

            sender.sendMessage(Formatter.format("Ranks", "Listing groups for requested user..."));
            sender.sendMessage(" ");

            Set<PermissionGroup> permissionGroupSet = remotePermissionManager.getDirectGroups(uuid);
            permissionGroupSet = permissionGroupSet.stream().sorted(Comparator.comparingInt(PermissionGroup::getPrefixLevel).reversed()).collect(Collectors.toCollection(LinkedHashSet::new));

            for (PermissionGroup permissionGroup : permissionGroupSet) {
                sender.sendMessage(
                        Formatter.toChatColor(permissionGroup.getColour()) + permissionGroup.getName()
                                + (permissionGroup.isPrimary() ? ChatColor.GRAY + " " + ChatColor.ITALIC + "(Primary)" : ""));
            }

            return;
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("add")) {
            // Add group

            UUID uuid = UuidNameFetcher.fetchUuid(args[1]);
            if (uuid == null) {
                sender.sendMessage(Formatter.format("Ranks", "That is not a valid username."));
                return;
            }

            PermissionGroup group = PermissionGroup.match(args[2]);
            if (group == null) {
                sender.sendMessage(Formatter.format("Ranks", "That is not a valid group."));
                return;
            }

            if (group == PermissionGroup.MEMBER) {
                sender.sendMessage(Formatter.format("Ranks", "Adding group MEMBER has been disallowed."));
                return;
            }

            if (remotePermissionManager.getDirectGroups(uuid).contains(group)) {
                sender.sendMessage(Formatter.format("Ranks", "That player is already a member of that group."));
                return;
            }

            remotePermissionManager.applyGroupToUuid(uuid, group);
            databaseMessageManager.send(ProfileManager.RELOAD_PROFILE_CHANNEL, new StringMessageFormat(uuid.toString()));

            sender.sendMessage(Formatter.format("Ranks", "You successfully added group " + Formatter.toChatColor(group.getColour()) + group.getName()) + ChatColor.GRAY + ".");

            return;
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("remove")) {
            // Remove group

            UUID uuid = UuidNameFetcher.fetchUuid(args[1]);
            if (uuid == null) {
                sender.sendMessage(Formatter.format("Ranks", "That is not a valid username."));
                return;
            }

            PermissionGroup group = PermissionGroup.match(args[2]);
            if (group == null) {
                sender.sendMessage(Formatter.format("Ranks", "That is not a valid group."));
                return;
            }

            if (group == PermissionGroup.MEMBER) {
                sender.sendMessage(Formatter.format("Ranks", "Removing group MEMBER has been disallowed."));
                return;
            }

            if (!remotePermissionManager.getDirectGroups(uuid).contains(group)) {
                sender.sendMessage(Formatter.format("Ranks", "That player is not a member of that group."));
                return;
            }

            remotePermissionManager.removeGroupFromUuid(uuid, group);
            databaseMessageManager.send(ProfileManager.RELOAD_PROFILE_CHANNEL, new StringMessageFormat(uuid.toString()));

            sender.sendMessage(Formatter.format("Ranks", "You successfully removed group " + Formatter.toChatColor(group.getColour()) + group.getName()) + ChatColor.GRAY + ".");

            return;
        }

        sendUsage(sender);

    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("info", "add", "remove");
        } else if (args.length == 2) {
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
        } else if (args.length == 3) {
            return Arrays.stream(PermissionGroup.values()).map(PermissionGroup::name).collect(Collectors.toList());
        }

        return null;
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage(Formatter.format("Ranks", "Incorrect arguments: Listing usage..."));
        sender.sendMessage(Formatter.format("Ranks", "&b/rank info <player> &7Outputs rank information about a player."));
        sender.sendMessage(Formatter.format("Ranks", "&b/rank add <player> <name> &7Adds a specified group to a player"));
        sender.sendMessage(Formatter.format("Ranks", "&b/rank remove <player> <name> &7Removes a specified group from a player"));
    }
}
