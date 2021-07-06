package org.maritimemc.core.info.command;

import org.bukkit.command.CommandSender;
import org.maritimemc.core.Formatter;
import org.maritimemc.core.command.CommandBase;

import java.util.List;

public class StoreCommand extends CommandBase {

    public StoreCommand(String name) {
        super(name);
        setConsoleExecutable(true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage(Formatter.format("Store", "store.maritimemc.org"));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return null;
    }
}
