package org.maritimemc.core.chatlog;

import com.google.common.collect.Sets;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.maritimemc.core.Formatter;
import org.maritimemc.core.Module;
import org.maritimemc.core.chat.MaritimeChatEvent;
import org.maritimemc.core.chatlog.command.CommandChatLog;
import org.maritimemc.core.command.CommandCenter;
import org.maritimemc.core.message.channel.ChatChannelLogEvent;
import org.maritimemc.core.message.message.event.PrivateMessageLogEvent;
import org.maritimemc.core.perm.PermissionManager;
import org.maritimemc.core.server.ServerDataManager;
import org.maritimemc.core.service.Locator;
import org.maritimemc.core.thread.ThreadPool;
import org.maritimemc.core.versioning.VersionHandler;
import org.maritimemc.data.perm.Permission;
import org.maritimemc.data.perm.PermissionGroup;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class ChatLogModule implements Module {

    private final ChatLogDataManager chatLogDataManager;

    private final ServerDataManager serverDataManager = Locator.locate(ServerDataManager.class);

    private final Set<LogMessage> messageSet;

    public ChatLogModule() {
        this.chatLogDataManager = new ChatLogDataManager();
        this.messageSet = new HashSet<>();

        PermissionManager permissionManager = Locator.locate(PermissionManager.class);
        permissionManager.addPermission(PermissionGroup.L3_DONATOR, ChatLogPerm.CHAT_LOG_COMMAND, true);

        Locator.locate(CommandCenter.class).register(new CommandChatLog("chatlog", this));

        Module.registerEvents(this);
    }

    public enum ChatLogPerm implements Permission {
        CHAT_LOG_COMMAND;
    }

    @EventHandler
    public void chat(MaritimeChatEvent event) {
        if (event.isCancelled()) {
            return;
        }

        LogMessage logMessage = new LogMessage(event.getPlayerMessage().getPlayer().getUniqueId(),
                event.getRecipients(),
                event.getPlayerMessage().getContent(),
                serverDataManager.getServerName(),
                null,
                MessageType.CHAT);

        cacheMessage(logMessage);
    }

    @EventHandler
    public void message(PrivateMessageLogEvent event) {
        LogMessage logMessage = new LogMessage(event.getMessage().getSender().getUuid(),
                Sets.newHashSet(event.getMessage().getRecipient().getUuid()),
                event.getMessage().getContent(),
                event.getMessage().getSenderServerName(),
                null,
                MessageType.PM,
                event.getMessage().getTime());

        cacheMessage(logMessage);
    }

    @EventHandler
    public void message(ChatChannelLogEvent event) {
        LogMessage logMessage = new LogMessage(event.getSender(),
                event.getRecipients(),
                event.getContent(),
                event.getSenderServerName(),
                event.getChannel().getName(),
                MessageType.CHANNEL,
                event.getTime());

        cacheMessage(logMessage);
    }

    public void cacheMessage(LogMessage logMessage) {
        ThreadPool.ASYNC_POOL.execute(() -> {
            cleanCache();
            messageSet.add(logMessage);
        });
    }

    private void cleanCache() {
        Set<LogMessage> toRemove = new HashSet<>();

        for (LogMessage logMessage : messageSet) {
            if ((System.currentTimeMillis() - logMessage.getTime()) > 3 * 60 * 1000) {
                toRemove.add(logMessage);
            }
        }

        messageSet.removeAll(toRemove);
    }

    public Set<LogMessage> getMessagesInvolving(UUID uuid) {
        return messageSet.stream().filter((m) -> m.getSender().equals(uuid) || m.getRecipients().contains(uuid)).collect(Collectors.toSet());
    }

    public CompletableFuture<String> createChatLog(UUID creator, UUID target, boolean inform) {

        return CompletableFuture.supplyAsync(() -> {
            ChatLog cl = new ChatLog(creator);
            Set<LogMessage> messagesInvolving = getMessagesInvolving(target);

            long current = System.currentTimeMillis();
            chatLogDataManager.addChatLog(cl, messagesInvolving);

            long time = System.currentTimeMillis();

            System.out.println("Added ChatLog " + cl.getId() + ": took " + (time - current) + "ms");

            if (inform) {
                Player player = Bukkit.getPlayer(creator);

                if (player != null) {
                    player.sendMessage(Formatter.format("Log", "&7We created a ChatLog for you. Your token: &a" + cl.getToken()));
                    player.sendMessage(Formatter.format("Log", "&7Access at &6https://log.maritimemc.org/view/" + cl.getToken()));
                    player.playSound(player.getLocation(), VersionHandler.NMS_HANDLER.getNotePling(), 7, 7);
                }
            }

            return cl.getToken();
        });

    }

}
