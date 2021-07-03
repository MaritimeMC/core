/*
 * Copyright © Minedroid Network 2020
 *
 * You may not use, distribute, or share this code under any circumstances
 * without explicit permission from Minedroid Network. All source code and
 * binaries are owned by Minedroid Network.
 *
 * All rights reserved.
 */

package org.maritimemc.core.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Utility class for fetching UUIDs and names
 * from Mojang.
 */
public class UuidNameFetcher {

    private static Map<String, UUID> cache = new HashMap<>();

    @SneakyThrows
    public static UUID fetchUuid(String name) {
        if (cache.containsKey(name.toLowerCase())) {
            return cache.get(name.toLowerCase());
        }

        Player player = Bukkit.getPlayer(name);
        if (player != null) {
            return player.getUniqueId();
        }

        URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);

        String s = IOUtils.toString(url, Charset.defaultCharset());

        if (s.isEmpty()) {
            return null;
        } else {
            String trimmedUuid = new JsonParser().parse(s).getAsJsonObject().get("id").getAsString();

            // SOURCE https://spigotmc.org/threads/free-code-easily-convert-between-trimmed-and-full-uuids.165615/
            StringBuilder builder = new StringBuilder(trimmedUuid.trim());

            /* Backwards adding to avoid index adjustments */
            try {
                builder.insert(20, "-");
                builder.insert(16, "-");
                builder.insert(12, "-");
                builder.insert(8, "-");
            } catch (StringIndexOutOfBoundsException e) {
                throw new IllegalArgumentException();
            }

            UUID uuid = UUID.fromString(builder.toString());
            cache.put(name.toLowerCase(), uuid);

            return uuid;
        }
    }

    @SneakyThrows
    public static String fetchName(UUID uuid) {
        if (cache.containsValue(uuid)) {
            for (String s : cache.keySet()) {
                if (cache.get(s).equals(uuid)) {
                    return s;
                }
            }
        }

        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            return player.getName();
        }

        URL url = new URL("https://api.mojang.com/user/profiles/" + uuid.toString().replace("-", "") + "/names");
        String s = IOUtils.toString(url, Charset.defaultCharset());

        JsonArray array = new JsonParser().parse(s).getAsJsonArray();

        String name = array.get(array.size() - 1).getAsJsonObject().get("name").getAsString();
        cache.put(name, uuid);

        return name;
    }
}
