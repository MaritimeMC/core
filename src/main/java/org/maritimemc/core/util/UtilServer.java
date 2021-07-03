package org.maritimemc.core.util;

import org.bukkit.plugin.java.JavaPlugin;

public class UtilServer {

    public static JavaPlugin getPlugin() {
        return JavaPlugin.getProvidingPlugin(UtilServer.class);
    }
}
