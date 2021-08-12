package org.maritimemc.core.server;

import com.google.gson.JsonObject;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.maritimemc.core.Module;
import org.maritimemc.core.command.CommandCenter;
import org.maritimemc.core.db.RedisModule;
import org.maritimemc.core.perm.PermissionManager;
import org.maritimemc.core.service.Locator;
import org.maritimemc.core.thread.ThreadPool;
import org.maritimemc.data.perm.Permission;
import org.maritimemc.data.perm.PermissionGroup;
import org.maritimemc.db.RedisDatastore;
import redis.clients.jedis.Jedis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.UUID;

public class ServerDataManager implements Module {

    private String proxyId;
    private String serverName;

    private final RedisModule redisModule = Locator.locate(RedisModule.class);

    public ServerDataManager() {
        Module.registerEvents(this);
    }

    @SneakyThrows
    public String getProxyId() {
        if (proxyId != null) {
            return proxyId;
        }

        File f = new File("maritime_proxy_data.dat");
        if (!f.exists()) {
            return "ProxyNameNotFound";
        }

        this.proxyId = new BufferedReader(new FileReader(f)).readLine();
        return proxyId;
    }

    @SneakyThrows
    public String getServerName() {
        if (serverName != null) {
            return serverName;
        }

        File f = new File("maritime_server_data.dat");
        if (!f.exists()) {
            return "ServerNameNotFound";
        }

        this.serverName = new BufferedReader(new FileReader(f)).readLine();
        return serverName;
    }

    @EventHandler
    public void join(PlayerJoinEvent event) {
        JsonObject o = new JsonObject();
        o.addProperty("server", getServerName());
        o.addProperty("count", Bukkit.getOnlinePlayers().size());

        ThreadPool.ASYNC_POOL.submit(() -> {
            try (Jedis j = redisModule.getResource()) {
                j.publish("masthead:player_count_update", o.toString());
            }
        });
    }

    @EventHandler
    public void quit(PlayerQuitEvent event) {
        JsonObject o = new JsonObject();
        o.addProperty("server", getServerName());
        o.addProperty("count", Bukkit.getOnlinePlayers().size()-1);

        ThreadPool.ASYNC_POOL.submit(() -> {
            try (Jedis j = redisModule.getResource()) {
                j.publish("masthead:player_count_update", o.toString());
            }
        });
    }

    public void send(Player player, String serverGroupName) {
        send(player.getUniqueId(), serverGroupName);
    }

    public void send(UUID uuid, String serverGroupName) {
        send(uuid.toString(), serverGroupName);
    }

    public void send(String player, String serverGroupName) {
        JsonObject o = new JsonObject();
        o.addProperty("player", player);
        o.addProperty("group", serverGroupName);

        ThreadPool.ASYNC_POOL.submit(() -> {
            try (Jedis j = redisModule.getResource()) {
                j.publish("masthead:group_connect", o.toString());
            }
        });
    }

}
