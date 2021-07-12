package org.maritimemc.core.announce;

import org.bukkit.Bukkit;
import org.maritimemc.core.Formatter;
import org.maritimemc.core.db.messaging.format.MessageFormat;
import org.maritimemc.core.db.messaging.format.StringMessageFormat;
import org.maritimemc.core.db.messaging.listen.Callback;
import org.maritimemc.core.versioning.VersionHandler;

public class AnnounceGlobalHandler implements Callback {

    @Override
    public void run(MessageFormat data) {
        String message = ((StringMessageFormat) data).getString();

        Bukkit.broadcastMessage(Formatter.format("&2Network Announcement", message));
        Bukkit.getOnlinePlayers().forEach((player) -> {
            VersionHandler.NMS_HANDLER.sendTitle(player, "§2§lNetwork Announcement", message, 13, 80, 13);
            player.playSound(player.getLocation(), VersionHandler.NMS_HANDLER.getNotePling(), 7, 5);
        });
    }
}
