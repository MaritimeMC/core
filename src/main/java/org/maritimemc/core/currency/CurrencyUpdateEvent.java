package org.maritimemc.core.currency;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.maritimemc.core.chat.BasicChatFormatter;
import org.maritimemc.core.chat.ChatFormatter;
import org.maritimemc.core.chat.PlayerMessage;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@Data
public class CurrencyUpdateEvent extends Event implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    private final UUID uuid;
    private final Currency currency;
    private final int gameId;

    private boolean cancelled = false;

    @Override
    public HandlerList getHandlers() { return HANDLER_LIST; }
}
