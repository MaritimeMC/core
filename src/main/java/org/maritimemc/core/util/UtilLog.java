package org.maritimemc.core.util;

import org.bukkit.Bukkit;

public class UtilLog {

    public static void log(String s) {
        Bukkit.getConsoleSender().sendMessage("[MTMC Core] " + s);
    }
}
