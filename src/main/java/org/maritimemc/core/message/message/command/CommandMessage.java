package org.maritimemc.core.message.message.command;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.maritimemc.core.Formatter;
import org.maritimemc.core.command.CommandBase;
import org.maritimemc.core.message.MessageManager;
import org.maritimemc.core.util.UtilText;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CommandMessage extends CommandBase {

    private final MessageManager messageManager;

    public CommandMessage(String name, MessageManager messageManager) {
        super(name);

        this.messageManager = messageManager;
        setAliases(Arrays.asList("msg", "w", "whisper", "m"));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(Formatter.format("Message", "&9/msg <player> <message> &7Messages another online player."));
            return;
        }

        String recipientName = args[0];
        String content = UtilText.getArguments(args, 1);

        messageManager.doMessage((Player) sender, recipientName, content, false);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 0) {
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
        }

        return null;
    }
}
