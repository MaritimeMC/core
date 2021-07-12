package org.maritimemc.core.message.channel.redis;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.maritimemc.core.db.messaging.format.MessageFormat;

@RequiredArgsConstructor
@Getter
public class ChatChannelMessageFormat implements MessageFormat {

    private final String channelName;
    private final String playerName;
    private final ChatColor color;
    private final String message;

}
