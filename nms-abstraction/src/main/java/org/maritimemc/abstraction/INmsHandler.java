package org.maritimemc.abstraction;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.SkullMeta;

public interface INmsHandler {

    void sendTitle(Player player, String title, String subtitle, int fadeIn, int hold, int fadeOut);

    void sendActionBar(Player player, String message);

    void sendTabHeaderFooter(Player player, String header, String footer);

    Sound getNotePling();

    void setSkullOwner(SkullMeta meta, String data);

    boolean usesSkullUUIDs();
}
