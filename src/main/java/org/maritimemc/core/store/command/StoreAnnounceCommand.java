package org.maritimemc.core.store.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.maritimemc.core.command.CommandBase;
import org.maritimemc.core.db.messaging.DatabaseMessageManager;
import org.maritimemc.core.service.Locator;
import org.maritimemc.core.store.StoreModule;
import org.maritimemc.core.store.message.StoreAnnounceHandler;

import java.util.List;

public class StoreAnnounceCommand extends CommandBase {

    private final DatabaseMessageManager databaseMessageManager = Locator.locate(DatabaseMessageManager.class);

    public StoreAnnounceCommand(String name) {
        super(name);

        setExecuteAsync(true);
        setConsoleExecutable(true);
        setRequiredPermission(StoreModule.StoreModulePerm.STORE_ANNOUNCE_COMMAND);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Bad usage");
            return;
        }

        String user = args[0];
        StringBuilder purchase = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            if (i == args.length - 1) {
                purchase.append(args[i]);
            } else {
                purchase.append(args[i]).append(" ");
            }
        }

        databaseMessageManager.send(StoreModule.STORE_ANNOUNCE_CHANNEL, new StoreAnnounceHandler.StoreAnnounceFormat(user, purchase.toString()));
        sender.sendMessage(ChatColor.GREEN + "Pushed store announcement!");

    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return null;
    }
}
