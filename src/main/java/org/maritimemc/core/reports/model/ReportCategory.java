package org.maritimemc.core.reports.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;

@Getter
@RequiredArgsConstructor
public enum ReportCategory {

    CHAT("Chat", ChatColor.GOLD, Material.ENCHANTED_BOOK),
    CLIENT("Client", ChatColor.BLUE, Material.GOLD_SWORD),
    GAMEPLAY("Gameplay", ChatColor.GREEN, Material.GRASS);

    private final String name;
    private final ChatColor chatColor;
    private final Material material;

}
