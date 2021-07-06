package org.maritimemc.core.sync.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.maritimemc.core.Formatter;
import org.maritimemc.core.command.CommandBase;
import org.maritimemc.core.perm.PermissionManager;
import org.maritimemc.core.profile.ProfileManager;
import org.maritimemc.core.service.Locator;
import org.maritimemc.core.sync.DiscordSyncModule;
import org.maritimemc.core.sync.api.ILinkManager;
import org.maritimemc.core.util.UuidNameFetcher;

import java.util.List;
import java.util.UUID;

public class UnlinkCommand extends CommandBase {

    private final ILinkManager linkManager;
    private final ProfileManager profileManager = Locator.locate(ProfileManager.class);
    private final PermissionManager permissionManager = Locator.locate(PermissionManager.class);

    public UnlinkCommand(String name, ILinkManager linkManager) {
        super(name);
        this.linkManager = linkManager;
        setConsoleExecutable(true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (args.length == 0) {
            // Unlink themselves, allow anyone

            if (!(sender instanceof Player)) {
                sendOtherMessage(sender);
                return;
            }

            Player player = (Player) sender;
            if (linkManager.isUserLinked(player.getUniqueId())) {
                linkManager.unlinkUser(player.getUniqueId());
                sender.sendMessage(Formatter.format("Link", "&7You unlinked your account."));
            } else {
                sender.sendMessage(Formatter.format("Link", "&7You are not linked to a Discord Account."));
            }
        } else if (args.length == 2) {
            // Unlink another via mc/discord, check perms!
            // if not then invalid usage

            if (!(sender instanceof Player)) {

                if (args[0].equalsIgnoreCase("mc")) {

                    String name = args[1];
                    UUID uuid = UuidNameFetcher.fetchUuid(name);
                    if (uuid == null) {
                        sender.sendMessage(Formatter.format("Link", "That is not a valid Minecraft name."));
                        return;
                    }

                    if (linkManager.isUserLinked(uuid)) {
                        linkManager.unlinkUser(uuid);
                        sender.sendMessage(Formatter.format("Link", "You successfully unlinked the user."));
                    } else {
                        sender.sendMessage(Formatter.format("Link", "That user is not linked to a Discord Account."));
                    }

                } else if (args[0].equalsIgnoreCase("discord")) {

                    try {
                        long id = Long.parseLong(args[1]);

                        if (linkManager.isUserLinked(id)) {
                            linkManager.unlinkUser(id);
                            sender.sendMessage(Formatter.format("Link", "You successfully unlinked the user."));
                        } else {
                            sender.sendMessage(Formatter.format("Link", "That user is not linked to a Minecraft Account."));
                        }

                    } catch (NumberFormatException exception) {
                        sender.sendMessage(Formatter.format("Link", "That is not a valid Discord ID."));
                    }

                } else {
                    sendSelfMessage(sender);
                }

            } else {
                Player player = (Player) sender;

                if (permissionManager.hasPermission(profileManager.getCached(player), DiscordSyncModule.DiscordSyncPerm.UNLINK_OTHER_USERS)) {

                    if (args[0].equalsIgnoreCase("mc")) {

                        String name = args[1];
                        UUID uuid = UuidNameFetcher.fetchUuid(name);
                        if (uuid == null) {
                            sender.sendMessage(Formatter.format("Link", "That is not a valid Minecraft name."));
                            return;
                        }

                        if (linkManager.isUserLinked(uuid)) {
                            linkManager.unlinkUser(uuid);
                            sender.sendMessage(Formatter.format("Link", "You successfully unlinked the user."));
                        } else {
                            sender.sendMessage(Formatter.format("Link", "That user is not linked to a Discord Account."));
                        }

                    } else if (args[0].equalsIgnoreCase("discord")) {

                        try {
                            long id = Long.parseLong(args[1]);

                            if (linkManager.isUserLinked(id)) {
                                linkManager.unlinkUser(id);
                                sender.sendMessage(Formatter.format("Link", "You successfully unlinked the user."));
                            } else {
                                sender.sendMessage(Formatter.format("Link", "That user is not linked to a Minecraft Account."));
                            }

                        } catch (NumberFormatException exception) {
                            sender.sendMessage(Formatter.format("Link", "That is not a valid Discord ID."));
                        }

                    } else {
                        sendSelfMessage(player);
                        sendOtherMessage(player);
                    }

                } else {
                    sendSelfMessage(player);
                }
            }

        } else {
            sendAppropriateInvalidUsageMessage(sender);
        }
    }

    private void sendAppropriateInvalidUsageMessage(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sendOtherMessage(sender);
            return;
        }

        Player player = (Player) sender;
        sendSelfMessage(player);

        if (permissionManager.hasPermission(profileManager.getCached(player), DiscordSyncModule.DiscordSyncPerm.UNLINK_OTHER_USERS)) {
            sendOtherMessage(player);
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return null;
    }

    private void sendOtherMessage(CommandSender sender) {
        sender.sendMessage(Formatter.format("Link", "&9/unlink <mc/discord> <name/id> &7Un-links another player's Minecraft UUID or Discord ID."));
    }

    private void sendSelfMessage(CommandSender sender) {
        sender.sendMessage(Formatter.format("Link", "&9/unlink &7Unlink your linked Discord Account."));
    }
}
