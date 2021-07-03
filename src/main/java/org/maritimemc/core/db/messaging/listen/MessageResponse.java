package org.maritimemc.core.db.messaging.listen;

import org.maritimemc.core.db.messaging.format.MessageFormat;

/**
 * An internal class used for executing the {@link Callback} linked to a message.
 *
 * @param <T> The implementation of MessageFormat to which this message is parsed.
 */
public class MessageResponse<T extends MessageFormat> {

    private final Class<T> dataFormat;
    private final Callback callback;

    public MessageResponse(Class<T> dataFormat, Callback callback) {
        this.dataFormat = dataFormat;
        this.callback = callback;
    }

    public Class<T> getDataFormat() {
        return dataFormat;
    }

    public Callback getCallback() {
        return callback;
    }
}
