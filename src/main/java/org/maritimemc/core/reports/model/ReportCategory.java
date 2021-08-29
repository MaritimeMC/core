package org.maritimemc.core.reports.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.maritimemc.abstraction.IMaterialMapper;
import org.maritimemc.core.versioning.VersionHandler;

@Getter
@RequiredArgsConstructor
public enum ReportCategory {

    CHAT("Chat", ChatColor.GOLD, Material.ENCHANTED_BOOK),
    CLIENT("Client", ChatColor.BLUE, VersionHandler.NMS_HANDLER.getMaterialMappings().sword(IMaterialMapper.ToolMaterial.GOLD)),
    GAMEPLAY("Gameplay", ChatColor.GREEN, VersionHandler.NMS_HANDLER.getMaterialMappings().grassBlock());

    private final String name;
    private final ChatColor chatColor;
    private final Material material;

}
