package org.maritimemc.core.reports.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;

@Getter
@RequiredArgsConstructor
public enum ReportStatus {

    PENDING("Pending", ChatColor.GOLD),
    APPROVED("Approved", ChatColor.GREEN),
    UNDER_INVESTIGATION("Under Investigation", ChatColor.AQUA),
    DENIED("Denied", ChatColor.RED);

    private final String name;
    private final ChatColor color;

}
