package org.maritimemc.core.db.messaging.format;

import org.maritimemc.core.db.messaging.DatabaseMessageManager;
import org.maritimemc.core.db.messaging.MessageChannel;

import java.util.function.Consumer;

/**
 * An implementation of {@link MessageFormat} used for simple String messages
 * holding one value.
 *
 * @see DatabaseMessageManager#registerSimple(MessageChannel, Consumer)
 */
public class StringMessageFormat implements MessageFormat {

    private final String string;

    public StringMessageFormat(String string) {
        this.string = string;
    }

    public String getString() {
        return string;
    }
}
