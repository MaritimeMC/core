package org.maritimemc.core.admin.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.maritimemc.core.Formatter;
import org.maritimemc.core.admin.Administrate;
import org.maritimemc.core.command.CommandBase;
import org.maritimemc.core.db.messaging.DatabaseMessageManager;
import org.maritimemc.core.db.messaging.format.StringMessageFormat;
import org.maritimemc.core.perm.PermissionManager;
import org.maritimemc.core.profile.ProfileManager;
import org.maritimemc.core.versioning.VersionHandler;

import java.util.Arrays;
import java.util.List;

import static org.maritimemc.core.service.Locator.locate;

public class AnnounceCommand extends CommandBase {

    private final ProfileManager profileManager = locate(ProfileManager.class);
    private final DatabaseMessageManager databaseMessageManager = locate(DatabaseMessageManager.class);
    private final PermissionManager permissionManager = locate(PermissionManager.class);

    public AnnounceCommand(String name) {
        super(name);
        setConsoleExecutable(true);
        setExecuteAsync(true);
        setRequiredPermission(Administrate.AdministratePerm.ANNOUNCE_COMMAND_BASE);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(Formatter.format("Announce", "&9/announce global <message> &7Globally announces to the entire network."));
            sender.sendMessage(Formatter.format("Announce", "&9/announce local <message> &7Announces to the your current server."));
            return;
        }

        if (!args[0].equalsIgnoreCase("local") && !args[0].equalsIgnoreCase("global")) {
            sender.sendMessage(Formatter.format("Announce", "&9/announce global <message> &7Globally announces to the entire network."));
            sender.sendMessage(Formatter.format("Announce", "&9/announce local <message> &7Announces to the your current server."));
            return;
        }

        if (args[0].equalsIgnoreCase("global")) {

            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (!permissionManager.hasPermission(profileManager.getCached(player), Administrate.AdministratePerm.ANNOUNCE_GLOBAL)) {
                    sender.sendMessage(Formatter.format("Announce", "You do not have access to that announcement type."));
                    return;
                }
            }

            StringBuilder message = new StringBuilder("&7");
            for (int i = 1; i < args.length; i++) {
                if (i == args.length - 1) {
                    message.append(args[i]);
                } else {
                    message.append(args[i]).append(" ");
                }
            }

            String strMessage = ChatColor.translateAlternateColorCodes('&', message.toString());

            databaseMessageManager.send(Administrate.ANNOUNCE_GLOBAL_CHANNEL, new StringMessageFormat(strMessage));
        } else if (args[0].equalsIgnoreCase("local")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (!permissionManager.hasPermission(profileManager.getCached(player.getUniqueId()), Administrate.AdministratePerm.ANNOUNCE_LOCAL)) {
                    sender.sendMessage(Formatter.format("Announce", "You do not have access to that announcement type."));
                    return;
                }
            }

            StringBuilder message = new StringBuilder("§7");
            for (int i = 1; i < args.length; i++) {
                if (i == args.length - 1) {
                    message.append(args[i]);
                } else {
                    message.append(args[i]).append(" ");
                }
            }

            String strMessage = ChatColor.translateAlternateColorCodes('&', message.toString());

            /*
             * Hacky workaround for colouring, oops.
             */
            Bukkit.broadcastMessage(Formatter.format("&2Local Announcement", strMessage));

            Bukkit.getOnlinePlayers().forEach((player) -> VersionHandler.NMS_HANDLER.sendTitle(player, "§2§lLocal Announcement", strMessage, 13, 80, 13));
        }

    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) return Arrays.asList("global", "local");
        return null;
    }
}
