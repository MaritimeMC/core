package org.maritimemc.core.util;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.maritimemc.data.versioning.MaritimeMaterial;
import org.maritimemc.data.versioning.ServerVersion;

public class UtilVersion {

    public static ServerVersion getVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().contains("1.8") ? ServerVersion._18 : ServerVersion._117;
    }

    public static Material getMaterial(MaritimeMaterial material) {
        return Material.valueOf(getVersion() == ServerVersion._18 ? material.getLegacyName() : material.getSeventeenName());
    }
}
