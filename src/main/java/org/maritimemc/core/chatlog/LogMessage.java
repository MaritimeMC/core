package org.maritimemc.core.chatlog;

import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public class LogMessage {

    private int id;

    private final UUID sender;
    private final Set<UUID> recipients;

    private final String content;
    private final String serverName; // For private messages, this is the sender's server.
    private final String channelName;
    private final MessageType type;

    private final long time;

    public LogMessage(UUID sender, Set<UUID> recipients, String content, String serverName, String channelName, MessageType type, long time) {
        this.sender = sender;
        this.recipients = recipients;
        this.content = content;
        this.serverName = serverName;
        this.channelName = channelName;
        this.type = type;
        this.time = time;
    }

    public LogMessage(UUID sender, Set<UUID> recipients, String content, String serverName, String channelName, MessageType type) {
        this.sender = sender;
        this.recipients = recipients;
        this.content = content;
        this.serverName = serverName;
        this.channelName = channelName;
        this.type = type;
        this.time = System.currentTimeMillis();
    }
}
