package org.maritimemc.core.message.contact.redis;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.maritimemc.core.db.messaging.format.MessageFormat;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
public class ReplyContactFormat implements MessageFormat {

    private final UUID For;
    private final String staffName;
    private final ChatColor staffColor;
    private final String content;

}
