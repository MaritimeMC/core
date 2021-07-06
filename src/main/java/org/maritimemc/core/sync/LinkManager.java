package org.maritimemc.core.sync;

import lombok.SneakyThrows;
import org.maritimemc.core.db.RedisModule;
import org.maritimemc.core.db.SqlModule;
import org.maritimemc.core.service.Locator;
import org.maritimemc.core.sync.api.ILinkManager;
import org.maritimemc.core.thread.ThreadPool;
import org.maritimemc.core.util.UtilUuid;
import org.maritimemc.db.RedisDatastore;
import org.maritimemc.db.SqlDatastore;
import redis.clients.jedis.Jedis;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class LinkManager implements ILinkManager {

    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS user_links (minecraft_uuid BINARY(16), discord_id BIGINT);";
    private static final String INSERT_LINK = "INSERT INTO user_links (minecraft_uuid, discord_id) VALUES (?, ?);";
    private static final String IS_LINKED_UUID = "SELECT 1 FROM user_links WHERE minecraft_uuid = ?;";
    private static final String IS_LINKED_DISCORD = "SELECT 1 FROM user_links WHERE discord_id = ?;";
    private static final String GET_DISCORD_ID = "SELECT discord_id FROM user_links WHERE minecraft_uuid = ?;";
    private static final String GET_UUID = "SELECT minecraft_uuid FROM user_links WHERE discord_id = ?;";
    private static final String UNLINK_BY_DISCORD = "DELETE FROM user_links WHERE discord_id = ?;";
    private static final String UNLINK_BY_UUID = "DELETE FROM user_links WHERE minecraft_uuid = ?;";

    private final SqlDatastore sqlDatastore = Locator.locate(SqlModule.class);
    private final RedisDatastore redisDatastore = Locator.locate(RedisModule.class);

    public LinkManager() {
        createTable();
    }

    @SneakyThrows
    private void createTable() {
        try (Connection conn = sqlDatastore.getMaritimeDatabase().getConnection()) {
            conn.prepareStatement(CREATE_TABLE).executeUpdate();
        }
    }

    @Override
    public String generateVerificationCode() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    @Override
    public void insertIntoCache(UUID uuid, String code) {
        ThreadPool.ASYNC_POOL.execute(() -> {
            try (Jedis j = redisDatastore.getResource()) {
                String key = DiscordSyncModule.REDIS_LINK_CACHE_PREFIX + ":" + code;
                j.set(key, uuid.toString());
                j.expire(key, 10 * 60L);
            }
        });
    }

    @SneakyThrows
    @Override
    public Long getDiscordId(UUID uuid) {
        try (Connection conn = sqlDatastore.getMaritimeDatabase().getConnection()) {
            PreparedStatement ps = conn.prepareStatement(GET_DISCORD_ID);
            ps.setBytes(1, UtilUuid.toBytes(uuid));

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getLong("discord_id");
            }

            return null;
        }
    }

    @SneakyThrows
    @Override
    public UUID getMinecraftUuid(long discordId) {
        try (Connection conn = sqlDatastore.getMaritimeDatabase().getConnection()) {
            PreparedStatement ps = conn.prepareStatement(GET_UUID);
            ps.setLong(1, discordId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return UtilUuid.fromBytes(rs.getBytes("minecraft_uuid"));
            }

            return null;
        }
    }

    @Override
    public boolean isUserInCache(UUID uuid) {

        try (Jedis j = redisDatastore.getResource()) {

            for (String key : j.keys(DiscordSyncModule.REDIS_LINK_CACHE_PREFIX + ":*")) {
                if (j.get(key).equals(uuid.toString())) return true;
            }

        }

        return false;
    }

    @SneakyThrows
    @Override
    public boolean isUserLinked(UUID uuid) {
        try (Connection conn = sqlDatastore.getMaritimeDatabase().getConnection()) {
            PreparedStatement ps = conn.prepareStatement(IS_LINKED_UUID);
            ps.setBytes(1, UtilUuid.toBytes(uuid));

            ResultSet rs = ps.executeQuery();
            return rs.next();
        }
    }

    @SneakyThrows
    @Override
    public boolean isUserLinked(long discordId) {
        try (Connection conn = sqlDatastore.getMaritimeDatabase().getConnection()) {
            PreparedStatement ps = conn.prepareStatement(IS_LINKED_DISCORD);
            ps.setLong(1, discordId);

            ResultSet rs = ps.executeQuery();
            return rs.next();
        }
    }

    @Override
    public void unlinkUser(UUID uuid) {
        ThreadPool.ASYNC_POOL.execute(() -> {
            try (Connection conn = sqlDatastore.getMaritimeDatabase().getConnection()) {
                PreparedStatement ps = conn.prepareStatement(UNLINK_BY_UUID);
                ps.setBytes(1, UtilUuid.toBytes(uuid));

                ps.executeUpdate();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
    }

    @Override
    public void unlinkUser(long discordId) {
        ThreadPool.ASYNC_POOL.execute(() -> {
            try (Connection conn = sqlDatastore.getMaritimeDatabase().getConnection()) {
                PreparedStatement ps = conn.prepareStatement(UNLINK_BY_DISCORD);
                ps.setLong(1, discordId);

                ps.executeUpdate();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
    }

    @Override
    public void linkUser(UUID uuid, long discordId) {
        ThreadPool.ASYNC_POOL.execute(() -> {
            try (Connection conn = sqlDatastore.getMaritimeDatabase().getConnection()) {
                PreparedStatement ps = conn.prepareStatement(INSERT_LINK);
                ps.setBytes(1, UtilUuid.toBytes(uuid));
                ps.setLong(2, discordId);

                ps.executeUpdate();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
    }
}
