package org.maritimemc.core.versioning;

import org.bukkit.entity.Player;

public interface INmsHandler {

    void sendTitle(Player player, String title, String subtitle, int fadeIn, int hold, int fadeOut);

    void sendActionBar(Player player, String message);

    void sendTabHeaderFooter(Player player, String header, String footer);

}
