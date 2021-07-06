package org.maritimemc.core.profile;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.maritimemc.core.ConstantGson;
import org.maritimemc.core.Module;
import org.maritimemc.core.db.RedisModule;
import org.maritimemc.core.db.messaging.DatabaseMessageManager;
import org.maritimemc.core.db.messaging.MessageChannel;
import org.maritimemc.core.perm.PermissionManager;
import org.maritimemc.core.perm.RemotePermissionManager;
import org.maritimemc.core.profile.event.ProfileReloadEvent;
import org.maritimemc.core.server.ServerDataManager;
import org.maritimemc.core.thread.ThreadPool;
import org.maritimemc.core.util.UtilServer;
import org.maritimemc.data.player.PlayerProfile;
import org.maritimemc.db.RedisDatastore;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.maritimemc.core.Module.registerEvents;
import static org.maritimemc.core.service.Locator.locate;

public class ProfileManager implements Module {

    private static final String CLIENT_PREFIX = "client_cache";

    public static final MessageChannel RELOAD_PROFILE_CHANNEL = new MessageChannel("ProfileManager", "reloadProfile");

    private final PermissionManager permissionManager = locate(PermissionManager.class);
    private final RemotePermissionManager remotePermissionManager = locate(RemotePermissionManager.class);
    private final DatabaseMessageManager databaseMessageManager = locate(DatabaseMessageManager.class);
    private final ServerDataManager serverDataManager = locate(ServerDataManager.class);

    private final RedisDatastore redisDatastore = locate(RedisModule.class);

    private final Map<UUID, PlayerProfile> profileCache;

    public ProfileManager() {
        this.profileCache = new HashMap<>();

        registerEvents(this);

        databaseMessageManager.registerSimple(RELOAD_PROFILE_CHANNEL, (s) -> reloadCachedProfile(UUID.fromString(s)));
    }

    @EventHandler
    public void connect(PlayerLoginEvent event) {
        if (event.getResult() != PlayerLoginEvent.Result.KICK_OTHER) {

            PlayerProfile profile = getFromRedis(event.getPlayer().getUniqueId());
            if (profile != null) {
                profile.setServerName(serverDataManager.getServerName());
                ThreadPool.ASYNC_POOL.execute(() -> loadIntoRedis(profile.getUuid(), profile));

                profileCache.put(event.getPlayer().getUniqueId(), profile);
            } else {
                loadNewProfile(event.getPlayer());
            }
        }
    }

    @EventHandler
    public void quit(PlayerQuitEvent event) {
        profileCache.remove(event.getPlayer().getUniqueId());
    }

    public PlayerProfile getFromRedis(UUID uuid) {
        try (Jedis j = redisDatastore.getResource()) {
            String s = j.get(CLIENT_PREFIX + ":" + uuid.toString());

            if (s == null) {
                return null;
            }

            return ConstantGson.GSON.fromJson(s, PlayerProfile.class);
        }
    }

    public void deleteFromRedis(UUID uuid) {
        try (Jedis j = redisDatastore.getResource()) {
            j.del(CLIENT_PREFIX + ":" + uuid.toString());
        }
    }

    public void loadIntoRedis(UUID uuid, PlayerProfile profile) {
        try (Jedis j = redisDatastore.getResource()) {
            String s = ConstantGson.GSON.toJson(profile);

            j.set(CLIENT_PREFIX + ":" + uuid.toString(), s);
        }
    }

    public PlayerProfile getCached(Player player) {
        return getCached(player.getUniqueId());
    }

    public PlayerProfile getCached(UUID uuid) {
        return profileCache.get(uuid);
    }

    public void loadNewProfile(Player player) {
        PlayerProfile profile = new PlayerProfile(
                player.getName(),
                player.getUniqueId(),
                serverDataManager.getProxyId(),
                serverDataManager.getServerName(),
                remotePermissionManager.getDirectGroups(player.getUniqueId())
        );

        profileCache.put(player.getUniqueId(), profile);
        ThreadPool.ASYNC_POOL.execute(() -> loadIntoRedis(profile.getUuid(), profile));
    }

    public void reloadCachedProfile(UUID uuid) {
        if (Bukkit.getPlayer(uuid) != null) {
            Bukkit.getPluginManager().callEvent(new ProfileReloadEvent(uuid));
            loadNewProfile(Bukkit.getPlayer(uuid));
        }
    }
}
