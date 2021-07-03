package org.maritimemc.core.db.messaging;

import org.maritimemc.core.Module;

/**
 * A channel over which a database message is sent.
 * <p>
 * Channels are categorised into their respective Module and the message's ID to avoid
 * multiple callbacks unintentionally handling a single message.
 */
public class MessageChannel {

    private final String module;
    private final String id;

    public MessageChannel(String module, String id) {
        this.module = module;
        this.id = id;
    }

    public String toChannel() {
        return "minedroid:" + module + ":" + id;
    }

}
