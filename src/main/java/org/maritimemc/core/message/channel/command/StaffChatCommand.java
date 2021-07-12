package org.maritimemc.core.message.channel.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.maritimemc.core.Formatter;
import org.maritimemc.core.command.CommandBase;
import org.maritimemc.core.message.MessageManager;
import org.maritimemc.core.util.UtilText;

import java.util.Collections;
import java.util.List;

public class StaffChatCommand extends CommandBase {

    private MessageManager messageManager;

    public StaffChatCommand(String name, MessageManager messageManager) {
        super(name);
        this.messageManager = messageManager;

        setRequiredPermission(MessageManager.MessagePerm.USE_STAFF_CHAT);
        setAliases(Collections.singletonList("schat"));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(Formatter.format("&c/schat <message>"));
            return;
        }

        messageManager.send(MessageManager.STAFF_CHAT, (Player) sender, UtilText.getArguments(args));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return null;
    }
}
