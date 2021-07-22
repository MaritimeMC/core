package org.maritimemc.core.message.channel;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.maritimemc.core.message.message.Message;

import java.util.Set;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
public class ChatChannelLogEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    private final UUID sender;
    private final Set<UUID> recipients;
    private final ChatChannel channel;
    private final String content;
    private final String senderServerName;
    private final long time;

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

}
