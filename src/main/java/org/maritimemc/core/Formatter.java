package org.maritimemc.core;

import org.bukkit.ChatColor;
import org.maritimemc.data.versioning.MaritimeColour;

public class Formatter {

    public static String format(String category, String message) {
        return format("&8(&9" + category + "&8) &7" + message);
    }

    public static String format(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static ChatColor toChatColor(MaritimeColour colour) {
        return ChatColor.valueOf(colour.name());
    }
}
