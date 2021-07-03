package org.maritimemc.core.board;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;

@Getter
@AllArgsConstructor
public class TeamedPlayerEntry {

    private final ChatColor color;
    private final PlayerNameEntry playerNameEntry;
}
