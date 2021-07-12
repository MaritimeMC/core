package org.maritimemc.core.message.contact;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.maritimemc.core.Formatter;
import org.maritimemc.core.command.CommandBase;
import org.maritimemc.core.message.MessageManager;
import org.maritimemc.core.util.UtilText;

import java.util.Collections;
import java.util.List;

public class ReplyContactCommand extends CommandBase {

    private final MessageManager messageManager;

    public ReplyContactCommand(String name, MessageManager messageManager) {
        super(name);
        this.messageManager = messageManager;
        setAliases(Collections.singletonList("rc"));
        setRequiredPermission(MessageManager.MessagePerm.REPLY_CONTACT);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(Formatter.format("Contact", "&9/replycontact <player> <message> &7Reply to a contact."));
            return;
        }

        messageManager.send((Player) sender, args[0], UtilText.getArguments(args, 1));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return null;
    }
}
