package org.maritimemc.core.chatlog.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.maritimemc.core.Formatter;
import org.maritimemc.core.chatlog.ChatLogModule;
import org.maritimemc.core.command.CommandBase;

import java.util.List;
import java.util.UUID;

public class CommandChatLog extends CommandBase {

    private final ChatLogModule chatLogModule;

    /**
     * Class constructor
     *
     * @param name The name of this command.
     */
    public CommandChatLog(String name, ChatLogModule chatLogModule) {
        super(name);
        this.chatLogModule = chatLogModule;
        setRequiredPermission(ChatLogModule.ChatLogPerm.CHAT_LOG_COMMAND);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        player.sendMessage(Formatter.format("Log", "Creating a ChatLog..."));

        UUID uuid = player.getUniqueId();
        chatLogModule.createChatLog(uuid, uuid, true).whenComplete((t, x) -> {
            if (x != null) {
                x.printStackTrace();
            }
        });
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return null;
    }
}
