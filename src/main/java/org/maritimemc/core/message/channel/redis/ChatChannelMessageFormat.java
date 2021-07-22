package org.maritimemc.core.message.channel.redis;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.maritimemc.core.db.messaging.format.MessageFormat;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
public class ChatChannelMessageFormat implements MessageFormat {

    private final String channelName;
    private final String playerName;
    private final UUID playerUuid;
    private final ChatColor color;
    private final String message;
    private final String senderServerName;
    private final long time;

}
