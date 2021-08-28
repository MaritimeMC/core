package org.maritimemc.core.suffix.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.maritimemc.core.Formatter;
import org.maritimemc.core.command.CommandBase;
import org.maritimemc.core.db.messaging.DatabaseMessageManager;
import org.maritimemc.core.db.messaging.format.StringMessageFormat;
import org.maritimemc.core.service.Locator;
import org.maritimemc.core.suffix.SuffixManager;
import org.maritimemc.core.util.UuidNameFetcher;
import org.maritimemc.data.perm.PermissionGroup;
import org.maritimemc.data.player.Suffix;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class CommandSuffixManage extends CommandBase {

    private final SuffixManager suffixManager;
    private final DatabaseMessageManager databaseMessageManager = Locator.locate(DatabaseMessageManager.class);

    public CommandSuffixManage(String name, SuffixManager suffixManager) {
        super(name);
        setConsoleExecutable(true);
        setExecuteAsync(true);
        setRequiredPermission(SuffixManager.SuffixPerm.SUFFIX_MANAGE);

        this.suffixManager = suffixManager;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 2 && args[0].equalsIgnoreCase("info")) {
            // Print suffix information

            UUID uuid = UuidNameFetcher.fetchUuid(args[1]);
            if (uuid == null) {
                sender.sendMessage(Formatter.format("Suffix", "That is not a valid username."));
                return;
            }

            sender.sendMessage(Formatter.format("Suffix", "Listing suffixes for requested user..."));
            sender.sendMessage(" ");

            Set<Suffix> suffixes = suffixManager.getSuffixes(uuid);

            for (Suffix suffix : suffixes) {
                sender.sendMessage(Formatter.toChatColor(suffix.getColor()) + suffix.getName());
            }

            return;
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("add")) {
            // Add group

            UUID uuid = UuidNameFetcher.fetchUuid(args[1]);
            if (uuid == null) {
                sender.sendMessage(Formatter.format("Suffix", "That is not a valid username."));
                return;
            }

            Suffix suffix = Suffix.match(args[2]);
            if (suffix == null) {
                sender.sendMessage(Formatter.format("Suffix", "That is not a valid suffix."));
                return;
            }

            if (suffixManager.getSuffixes(uuid).contains(suffix)) {
                sender.sendMessage(Formatter.format("Suffix", "That player already owns that suffix."));
                return;
            }

            suffixManager.addSuffix(uuid, suffix);
            databaseMessageManager.send(SuffixManager.SUFFIX_RELOAD_CHANNEL, new StringMessageFormat(uuid.toString()));

            sender.sendMessage(Formatter.format("Suffix", "You successfully added suffix " + Formatter.toChatColor(suffix.getColor()) + suffix.getName()) + ChatColor.GRAY + ".");

            return;
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("remove")) {
            // Remove group

            UUID uuid = UuidNameFetcher.fetchUuid(args[1]);
            if (uuid == null) {
                sender.sendMessage(Formatter.format("Suffix", "That is not a valid username."));
                return;
            }

            Suffix suffix = Suffix.match(args[2]);
            if (suffix == null) {
                sender.sendMessage(Formatter.format("Suffix", "That is not a valid suffix."));
                return;
            }

            if (!suffixManager.getSuffixes(uuid).contains(suffix)) {
                sender.sendMessage(Formatter.format("Suffix", "That player does not own that suffix."));
                return;
            }

            suffixManager.removeSuffix(uuid, suffix);
            databaseMessageManager.send(SuffixManager.SUFFIX_RELOAD_CHANNEL, new StringMessageFormat(uuid.toString()));

            sender.sendMessage(Formatter.format("Suffix", "You successfully removed suffix " + Formatter.toChatColor(suffix.getColor()) + suffix.getName()) + ChatColor.GRAY + ".");

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
            return Arrays.stream(Suffix.values()).map(Suffix::name).collect(Collectors.toList());
        }

        return null;
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage(Formatter.format("Suffix", "Incorrect arguments: Listing usage..."));
        sender.sendMessage(Formatter.format("Suffix", "&b/suffixmanage info <player> &7Outputs suffix information about a player."));
        sender.sendMessage(Formatter.format("Suffix", "&b/suffixmanage add <player> <name> &7Adds a specified suffix to a player"));
        sender.sendMessage(Formatter.format("Suffix", "&b/suffixmanage remove <player> <name> &7Removes a specified suffix from a player"));
    }
}
