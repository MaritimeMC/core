package org.maritimemc.core.message.message.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.maritimemc.core.Formatter;
import org.maritimemc.core.command.CommandBase;
import org.maritimemc.core.message.MessageManager;
import org.maritimemc.core.util.UtilText;

import java.util.Collections;
import java.util.List;

public class CommandReply extends CommandBase {

    private final MessageManager messageManager;

    public CommandReply(String name, MessageManager messageManager) {
        super(name);

        this.messageManager = messageManager;
        setAliases(Collections.singletonList("r"));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(Formatter.format("Message", "&9/reply <message> &7Reply to your last message."));
            return;
        }

        String content = UtilText.getArguments(args);

        messageManager.doReply((Player) sender, content);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return null;
    }
}
