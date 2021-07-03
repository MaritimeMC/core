package org.maritimemc.core.db.messaging.listen;

import org.maritimemc.core.db.messaging.DatabaseMessageManager;
import org.maritimemc.core.db.messaging.format.MessageFormat;
import redis.clients.jedis.JedisPubSub;

/**
 * An implementation of {@link JedisPubSub} to listen for incoming messages and pass them
 * to their relevant {@link Callback}.
 */
public class MessageListener extends JedisPubSub {

    private final DatabaseMessageManager manager;

    public MessageListener(DatabaseMessageManager manager) {
        this.manager = manager;
    }

    @Override
    public void onPMessage(String pattern, String channel, String message) {
        MessageResponse<? extends MessageFormat> response = manager.getResponse(channel);

        if (response != null) {
            try {
                response.getCallback().run(manager.getGson().fromJson(message, response.getDataFormat()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
