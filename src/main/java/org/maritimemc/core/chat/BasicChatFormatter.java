package org.maritimemc.core.chat;

import org.maritimemc.core.Formatter;

public class BasicChatFormatter implements ChatFormatter {

    @Override
    public String format(PlayerMessage playerMessage) {
        return Formatter.format(
                String.format(" &8(%s%s&8) %s%s %s&8> &7%s",
                        Formatter.toChatColor(playerMessage.getPrefix().getColour()),
                        playerMessage.getPrefix().getName(),
                        Formatter.toChatColor(playerMessage.getPrefix().getColour()),
                        playerMessage.getPlayer().getName(),
                        (playerMessage.getSuffix() != null ?
                                String.format("&8[%s%s&8] ",
                                        Formatter.toChatColor(playerMessage.getSuffix().getColor()),
                                        playerMessage.getSuffix().getName())
                                : ""),
                        playerMessage.getContent()
                )
        );
    }
}
