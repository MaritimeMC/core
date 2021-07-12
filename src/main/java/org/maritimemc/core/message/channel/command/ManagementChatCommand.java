package org.maritimemc.core.message.channel.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.maritimemc.core.Formatter;
import org.maritimemc.core.command.CommandBase;
import org.maritimemc.core.message.MessageManager;
import org.maritimemc.core.util.UtilText;

import java.util.Collections;
import java.util.List;

public class ManagementChatCommand extends CommandBase {

    private final MessageManager messageManager;

    public ManagementChatCommand(String name, MessageManager messageManager) {
        super(name);
        this.messageManager = messageManager;

        setRequiredPermission(MessageManager.MessagePerm.USE_MANAGEMENT_CHAT);
        setAliases(Collections.singletonList("mchat"));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(Formatter.format("&c/mchat <message>"));
            return;
        }

        messageManager.send(MessageManager.MANAGEMENT_CHAT, (Player) sender, UtilText.getArguments(args));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return null;
    }
}
