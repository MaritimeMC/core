package org.maritimemc.core.admin.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.maritimemc.core.Formatter;
import org.maritimemc.core.admin.Administrate;
import org.maritimemc.core.admin.message.ManagementChatHandler;
import org.maritimemc.core.command.CommandBase;
import org.maritimemc.core.db.messaging.DatabaseMessageManager;
import org.maritimemc.core.server.ServerDataManager;
import org.maritimemc.core.service.Locator;

import java.util.Collections;
import java.util.List;

public class ManagementChat extends CommandBase {

    private final ServerDataManager serverDataManager = Locator.locate(ServerDataManager.class);
    private final DatabaseMessageManager databaseMessageManager = Locator.locate(DatabaseMessageManager.class);

    public ManagementChat(String name) {
        super(name);
        setAliases(Collections.singletonList("mc"));
        setExecuteAsync(true);
        setRequiredPermission(Administrate.AdministratePerm.MANAGEMENT_CHAT);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage(Formatter.format("Management",  "&9/managementchat <message> " + ChatColor.GRAY + "Sends a message to all online management."));
            return;
        }

        String server = serverDataManager.getServerName();

        StringBuilder message = new StringBuilder("§7");
        for (int i = 0; i < args.length; i++) {
            if (i == args.length - 1) {
                message.append(args[i]);
            } else {
                message.append(args[i]).append(" ");
            }
        }

        databaseMessageManager.send(Administrate.MANAGEMENT_CHAT_CHANNEL, new ManagementChatHandler.ManagementChatIncomingFormat(server, player.getName(), message.toString()));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return null;
    }
}
