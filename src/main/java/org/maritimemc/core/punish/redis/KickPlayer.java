package org.maritimemc.core.punish.redis;

import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.maritimemc.core.db.messaging.format.MessageFormat;
import org.maritimemc.core.db.messaging.listen.Callback;
import org.maritimemc.core.util.UtilServer;

import java.util.UUID;

public class KickPlayer implements Callback {

    @Override
    public void run(MessageFormat data) {
        KickPlayerFormat f = (KickPlayerFormat) data;

        Player player = Bukkit.getPlayer(f.getPlayer());
        if (player != null) {
            Bukkit.getScheduler().runTask(UtilServer.getPlugin(), () -> player.kickPlayer(f.getMessage()));
        }
    }

    @Data
    public static class KickPlayerFormat implements MessageFormat {
        private final UUID player;
        private final String message;
    }
}
