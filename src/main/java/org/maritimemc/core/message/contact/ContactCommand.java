package org.maritimemc.core.message.contact;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.maritimemc.core.Formatter;
import org.maritimemc.core.command.CommandBase;
import org.maritimemc.core.message.MessageManager;
import org.maritimemc.core.util.UtilText;

import java.util.Collections;
import java.util.List;

public class ContactCommand extends CommandBase {

    private final MessageManager messageManager;

    public ContactCommand(String name, MessageManager messageManager) {
        super(name);
        this.messageManager = messageManager;
        setAliases(Collections.singletonList("c"));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(Formatter.format("Contact", "&9/contact <message> &7Contact online staff members."));
            return;
        }

        messageManager.send((Player) sender, UtilText.getArguments(args));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return null;
    }
}
