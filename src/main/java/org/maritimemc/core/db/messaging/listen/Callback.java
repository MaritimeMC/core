package org.maritimemc.core.db.messaging.listen;


import org.maritimemc.core.db.messaging.format.MessageFormat;

/**
 * A callback to a {@link MessageFormat} received from Redis.
 */
public interface Callback {

    void run(MessageFormat data);

}
