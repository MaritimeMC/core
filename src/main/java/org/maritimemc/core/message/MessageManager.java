package org.maritimemc.core.message;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.maritimemc.core.Formatter;
import org.maritimemc.core.Module;
import org.maritimemc.core.command.CommandCenter;
import org.maritimemc.core.db.messaging.DatabaseMessageManager;
import org.maritimemc.core.db.messaging.MessageChannel;
import org.maritimemc.core.message.channel.ChatChannel;
import org.maritimemc.core.message.channel.ChatChannelLogEvent;
import org.maritimemc.core.message.channel.command.ManagementChatCommand;
import org.maritimemc.core.message.channel.command.StaffChatCommand;
import org.maritimemc.core.message.channel.redis.ChatChannelMessageFormat;
import org.maritimemc.core.message.contact.ContactCommand;
import org.maritimemc.core.message.contact.ReplyContactCommand;
import org.maritimemc.core.message.contact.redis.ContactFormat;
import org.maritimemc.core.message.contact.redis.ReplyContactFormat;
import org.maritimemc.core.message.message.Message;
import org.maritimemc.core.message.message.MessagePlayer;
import org.maritimemc.core.message.message.command.CommandMessage;
import org.maritimemc.core.message.message.command.CommandReply;
import org.maritimemc.core.message.message.event.PrivateMessageLogEvent;
import org.maritimemc.core.perm.PermissionManager;
import org.maritimemc.core.profile.ProfileManager;
import org.maritimemc.core.server.ServerDataManager;
import org.maritimemc.core.service.Locator;
import org.maritimemc.core.thread.ThreadPool;
import org.maritimemc.core.util.UuidNameFetcher;
import org.maritimemc.core.versioning.VersionHandler;
import org.maritimemc.data.perm.Permission;
import org.maritimemc.data.perm.PermissionGroup;
import org.maritimemc.data.player.PlayerProfile;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class MessageManager implements Module {

    public static final ChatChannel STAFF_CHAT = new ChatChannel("Staff", '#', true, MessagePerm.USE_STAFF_CHAT);
    public static final ChatChannel MANAGEMENT_CHAT = new ChatChannel("Management", '!', true, MessagePerm.USE_MANAGEMENT_CHAT);

    private final Set<ChatChannel> channelSet;
    private final Map<UUID, UUID> replyMap;

    private static final MessageChannel CHAT_CHANNEL_REDIS = new MessageChannel("MessageManager", "chatChannel");
    private static final MessageChannel MESSAGE_REDIS = new MessageChannel("MessageManager", "message");
    private static final MessageChannel CONTACT_REDIS = new MessageChannel("MessageManager", "contact");
    private static final MessageChannel REPLY_CONTACT_REDIS = new MessageChannel("MessageManager", "replyContact");

    private final DatabaseMessageManager databaseMessageManager = Locator.locate(DatabaseMessageManager.class);

    private final PermissionManager permissionManager = Locator.locate(PermissionManager.class);
    private final ProfileManager profileManager = Locator.locate(ProfileManager.class);
    private final ServerDataManager serverDataManager = Locator.locate(ServerDataManager.class);


    public MessageManager() {
        this.channelSet = new HashSet<>();
        this.replyMap = new HashMap<>();

        databaseMessageManager.register(CHAT_CHANNEL_REDIS, ChatChannelMessageFormat.class, (Consumer<ChatChannelMessageFormat>) this::handle);
        databaseMessageManager.register(MESSAGE_REDIS, Message.class, (Consumer<Message>) this::handle);
        databaseMessageManager.register(CONTACT_REDIS, ContactFormat.class, (Consumer<ContactFormat>) this::handle);
        databaseMessageManager.register(REPLY_CONTACT_REDIS, ReplyContactFormat.class, (Consumer<ReplyContactFormat>) this::handle);
        registerChannels();

        permissionManager.addPermission(PermissionGroup.HELPER, MessagePerm.USE_STAFF_CHAT, true);
        permissionManager.addPermission(PermissionGroup.ADMINISTRATOR, MessagePerm.USE_MANAGEMENT_CHAT, true);

        permissionManager.addPermission(PermissionGroup.HELPER, MessagePerm.RECEIVE_CONTACTS, true);
        permissionManager.addPermission(PermissionGroup.HELPER, MessagePerm.REPLY_CONTACT, true);

        Locator.locate(CommandCenter.class).register(
                new StaffChatCommand("staffchat", this),
                new ManagementChatCommand("managementchat", this),
                new CommandMessage("message", this),
                new CommandReply("reply", this),
                new ContactCommand("contact", this),
                new ReplyContactCommand("replycontact", this)
        );

        Module.registerEvents(this);
    }

    public enum MessagePerm implements Permission {
        USE_STAFF_CHAT,
        USE_MANAGEMENT_CHAT,
        RECEIVE_CONTACTS,
        REPLY_CONTACT;
    }

    public void registerChannels() {
        channelSet.add(STAFF_CHAT);
        channelSet.add(MANAGEMENT_CHAT);
    }

    public void send(ChatChannel channel, Player player, String message) {
        ChatChannelMessageFormat format =
                new ChatChannelMessageFormat(channel.getName(),
                        player.getName(),
                        player.getUniqueId(),
                        channel.getPlayerColor(player),
                        message,
                        serverDataManager.getServerName(),
                        System.currentTimeMillis());

        if (!channel.isGlobal()) {
            handle(format);
        } else {
            ThreadPool.ASYNC_POOL.execute(() -> databaseMessageManager.send(CHAT_CHANNEL_REDIS, format));
        }
    }

    public void handle(ChatChannelMessageFormat data) {
        ChatChannel channel = channelSet.stream().filter((c) -> c.getName().equals(data.getChannelName())).findFirst().orElse(null);

        if (channel == null) {
            return;
        }

        Set<UUID> recipients = channel.getRecipients().stream().map(Player::getUniqueId).collect(Collectors.toSet());

        ChatChannelLogEvent event = new ChatChannelLogEvent(data.getPlayerUuid(), recipients, channel, data.getMessage(), data.getSenderServerName(), data.getTime());
        Bukkit.getPluginManager().callEvent(event);

        for (UUID recipient : recipients) {
            Bukkit.getPlayer(recipient).sendMessage(channel.format(data.getPlayerName(), data.getColor(), data.getMessage()));
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void chat(AsyncPlayerChatEvent event) {

        if (event.isCancelled()) return;

        char c = event.getMessage().charAt(0);

        for (ChatChannel channel : channelSet) {
            if (channel.getShortcut() != c) continue;

            if (channel.getPermission() != null) {
                if (!permissionManager.hasPermission(profileManager.getCached(event.getPlayer()), channel.getPermission())) {
                    continue;
                }
            }

            event.setCancelled(true);

            send(channel, event.getPlayer(), event.getMessage().substring(1));
        }
    }

    public void doMessage(Player player, String recipientName, String content, boolean isReply) {
        UUID uuid = UuidNameFetcher.fetchUuid(recipientName);

        if (uuid == null) {
            player.sendMessage(Formatter.format("Message", "That player does not exist."));
            return;
        }

        doMessage(player, uuid, content, isReply);
    }

    public void doMessage(Player player, UUID uuid, String content, boolean isReply) {
        // ToDo Mute check, message prefs check

        PlayerProfile profile = profileManager.getFromRedis(uuid);

        if (profile == null) {
            player.sendMessage(Formatter.format("Message", "That player is not online."));
            return;
        }

        Message m = new Message(new MessagePlayer(player.getName(), player.getUniqueId()), new MessagePlayer(profile.getName(), uuid), content, serverDataManager.getServerName(), System.currentTimeMillis());

        player.sendMessage(Formatter.format("&2Message sent to: &a" + profile.getName() + " &8> &7" + ChatColor.stripColor(Formatter.format(content))));

        if (profile.getPermissionGroups().contains(PermissionGroup.DEVELOPER) && !isReply) {
            player.sendMessage(Formatter.format("&7&oThat player is often AFK due to responsibilities as a Developer. You may not get a response."));
        }

        replyMap.put(player.getUniqueId(), uuid);
        Bukkit.getPluginManager().callEvent(new PrivateMessageLogEvent(m));

        if (Bukkit.getPlayer(uuid) != null) {
            handle(m);
        } else {
            ThreadPool.ASYNC_POOL.execute(() -> databaseMessageManager.send(MESSAGE_REDIS, m));
        }
    }

    public void handle(Message message) {
        Player player = Bukkit.getPlayer(message.getRecipient().getUuid());

        if (player != null) {
            replyMap.put(player.getUniqueId(), message.getSender().getUuid());
            player.sendMessage(Formatter.format("&2&lMessage from: &a" + message.getSender().getName() + " &8> &7" + ChatColor.stripColor(Formatter.format(message.getContent()))));
            player.playSound(player.getLocation(), VersionHandler.NMS_HANDLER.getNotePling(), 7, 8);

            // Only log if the sender isn't online (gets logged by default for the sender)
            if (Bukkit.getPlayer(message.getRecipient().getUuid()) == null) {
                Bukkit.getPluginManager().callEvent(new PrivateMessageLogEvent(message));
            }
        }
    }

    public void doReply(Player player, String content) {
        if (!replyMap.containsKey(player.getUniqueId())) {
            player.sendMessage(Formatter.format("Message", "You have nobody to reply to."));
            return;
        }

        doMessage(player, replyMap.get(player.getUniqueId()), content, true);
    }

    public void send(Player player, String contactMessage) {
        contactMessage = ChatColor.stripColor(Formatter.format(contactMessage));
        player.sendMessage(Formatter.format("&8[&9Sent Contact&8] &7You said: &a" + contactMessage));

        ContactFormat c = new ContactFormat(player.getName(), contactMessage);
        ThreadPool.ASYNC_POOL.execute(() -> databaseMessageManager.send(CONTACT_REDIS, c));
    }

    public void handle(ContactFormat contactFormat) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerProfile profile = profileManager.getCached(player);
            if (permissionManager.hasPermission(profile, MessagePerm.RECEIVE_CONTACTS)) {
                player.sendMessage(Formatter.format("&8[&9Incoming Contact] "
                        + Formatter.toChatColor(profile.getHighestPrimaryGroup().getColour())
                        + contactFormat.getSenderName()
                        + " &8> &7"
                        + contactFormat.getContent()));
                player.playSound(player.getLocation(), VersionHandler.NMS_HANDLER.getNotePling(), 7, 8);
            }
        }
    }

    public void send(Player player, String other, String reply) {
        UUID For = UuidNameFetcher.fetchUuid(other);

        if (For == null) {
            player.sendMessage(Formatter.format("Contact", "That player does not exist."));
            return;
        }

        PlayerProfile otherProfile = profileManager.getFromRedis(For);
        if (otherProfile == null) {
            player.sendMessage(Formatter.format("Contact", "That player is not online."));
            return;
        }

        ChatColor color = Formatter.toChatColor(profileManager.getCached(player).getHighestPrimaryGroup().getColour());

        ReplyContactFormat r = new ReplyContactFormat(For, player.getName(), color, reply);
        ThreadPool.ASYNC_POOL.execute(() -> databaseMessageManager.send(REPLY_CONTACT_REDIS, r));

        player.sendMessage(Formatter.format("&8[&9Reply Contact&8] &7To &a" + otherProfile.getName() + " &8> &7" + reply));
    }

    public void handle(ReplyContactFormat replyContactFormat) {
        Player whoFor = Bukkit.getPlayer(replyContactFormat.getFor());

        if (whoFor != null) {
            whoFor.sendMessage(Formatter.format("&8[&9Contact Reply&8] " + replyContactFormat.getStaffColor() + replyContactFormat.getStaffName() + " &8> &7" + replyContactFormat.getContent()));
            whoFor.playSound(whoFor.getLocation(), VersionHandler.NMS_HANDLER.getNotePling(), 7, 8);
        }
    }

}
