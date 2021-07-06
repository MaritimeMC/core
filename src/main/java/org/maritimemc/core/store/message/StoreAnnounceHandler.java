package org.maritimemc.core.store.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.maritimemc.core.Formatter;
import org.maritimemc.core.db.messaging.format.MessageFormat;
import org.maritimemc.core.db.messaging.listen.Callback;

public class StoreAnnounceHandler implements Callback {

    @Override
    public void run(MessageFormat data) {
        StoreAnnounceFormat f = (StoreAnnounceFormat) data;
        Bukkit.broadcastMessage(Formatter.format("&lStore Announcement", "&a" + f.getUser() + " &7has purchased &a" + f.getPurchase() + " &7from our store!"));
    }

    @AllArgsConstructor
    @Getter
    public static class StoreAnnounceFormat implements MessageFormat {
        private final String user;
        private final String purchase;
    }

}
