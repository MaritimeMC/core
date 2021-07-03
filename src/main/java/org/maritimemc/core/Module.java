package org.maritimemc.core;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.maritimemc.core.util.UtilServer;

public interface Module extends Listener {

    static void registerEvents(Module module) {
        Bukkit.getServer().getPluginManager().registerEvents(module, UtilServer.getPlugin());
    }

}
