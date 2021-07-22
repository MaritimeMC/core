package org.maritimemc.core.chat;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@Data
public class MaritimeChatEvent extends Event implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    private final PlayerMessage playerMessage;

    private ChatFormatter chatFormatter = new BasicChatFormatter();
    private Set<UUID> recipients = Bukkit.getOnlinePlayers().stream().map(Player::getUniqueId).collect(Collectors.toSet());
    private boolean cancelled = false;

    @Override
    public HandlerList getHandlers() { return HANDLER_LIST; }
}
