package org.maritimemc.abstraction;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scoreboard.Team;

public interface INmsHandler {

    IMaterialMapper getMaterialMappings();

    void sendTitle(Player player, String title, String subtitle, int fadeIn, int hold, int fadeOut);

    void sendActionBar(Player player, String message);

    void sendTabHeaderFooter(Player player, String header, String footer);

    Sound getNotePling();

    void setSkullOwner(SkullMeta meta, String data);

    boolean usesModernSkulls();

    Material getPlayerHeadItem();

    void setTeamColour(Team team, ChatColor color);
}
