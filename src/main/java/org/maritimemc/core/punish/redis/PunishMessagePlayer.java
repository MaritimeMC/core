package org.maritimemc.core.punish.redis;

import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.maritimemc.core.Formatter;
import org.maritimemc.core.db.messaging.format.MessageFormat;
import org.maritimemc.core.db.messaging.listen.Callback;

import java.util.UUID;

public class PunishMessagePlayer implements Callback {

    @Override
    public void run(MessageFormat data) {
        PunishMessagePlayerFormat f = (PunishMessagePlayerFormat) data;

        Player player = Bukkit.getPlayer(f.getPlayer());
        if (player != null) {
            player.sendMessage(Formatter.format("Punish", f.getMessage()));
        }
    }

    @Data
    public static class PunishMessagePlayerFormat implements MessageFormat {
        private final UUID player;
        private final String message;
    }
}
