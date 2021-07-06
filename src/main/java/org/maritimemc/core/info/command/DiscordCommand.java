package org.maritimemc.core.info.command;

import org.bukkit.command.CommandSender;
import org.maritimemc.core.Formatter;
import org.maritimemc.core.command.CommandBase;

import java.util.List;

public class DiscordCommand extends CommandBase {

    public DiscordCommand(String name) {
        super(name);
        setConsoleExecutable(true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage(Formatter.format("Discord", "https://discord.maritimemc.org"));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return null;
    }
}
