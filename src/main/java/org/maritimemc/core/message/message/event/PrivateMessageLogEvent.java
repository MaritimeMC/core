package org.maritimemc.core.message.message.event;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.maritimemc.core.message.message.Message;

@EqualsAndHashCode(callSuper = true)
@Data
public class PrivateMessageLogEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    private final Message message;

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

}
