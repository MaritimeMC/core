package org.maritimemc.core.db.messaging;

import com.google.gson.Gson;
import org.maritimemc.core.Module;
import org.maritimemc.core.db.RedisModule;
import org.maritimemc.core.db.messaging.format.MessageFormat;
import org.maritimemc.core.db.messaging.format.StringMessageFormat;
import org.maritimemc.core.db.messaging.listen.Callback;
import org.maritimemc.core.db.messaging.listen.MessageListener;
import org.maritimemc.core.db.messaging.listen.MessageResponse;
import org.maritimemc.core.service.Locator;
import org.maritimemc.core.thread.ThreadPool;
import org.maritimemc.db.RedisDatastore;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * A general management class used for the sending/receiving of Redis
 * publish-subscribe messages.
 *
 * @author Embrasure
 */
public class DatabaseMessageManager implements Module {

    private final Gson gson = new Gson();

    private final RedisDatastore redisDatastore = Locator.locate(RedisModule.class);

    private final Map<String, MessageResponse<?>> registeredChannels;

    public DatabaseMessageManager() {
        this.registeredChannels = new HashMap<>();
    }

    public Gson getGson() {
        return gson;
    }

    public MessageResponse<?> getResponse(String channel) {
        for (String s : registeredChannels.keySet()) {
            if (s.equals(channel)) {
                return registeredChannels.get(s);
            }
        }

        return null;
    }

    public void listen() {
        Executors.newSingleThreadExecutor().execute(() -> {
            try (Jedis j = redisDatastore.getResource()) {
                j.psubscribe(new MessageListener(this), "maritime:*");
            }
        });
    }

    public <T extends MessageFormat> void register(MessageChannel channel, Class<T> dataFormatClass, Callback callback) {
        registeredChannels.put(
                channel.toChannel(),
                new MessageResponse<>(dataFormatClass, callback)
        );
    }

    public void register(MessageChannel channel, Consumer<String> callback) {
        registeredChannels.put(
                channel.toChannel(),
                new MessageResponse<>(StringMessageFormat.class, data -> {
                    callback.accept(((StringMessageFormat) data).getString());
                })
        );
    }

    public <T extends MessageFormat> void send(MessageChannel channel, T data) {
        ThreadPool.ASYNC_POOL.execute(() -> {
            try (Jedis j = redisDatastore.getResource()) {
                j.publish(channel.toChannel(), gson.toJson(data));
            }
        });
    }

}
