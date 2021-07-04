package org.maritimemc.core.reports.message;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.maritimemc.core.db.messaging.format.MessageFormat;
import org.maritimemc.core.db.messaging.format.StringMessageFormat;
import org.maritimemc.core.db.messaging.listen.Callback;
import org.maritimemc.core.perm.PermissionManager;
import org.maritimemc.core.profile.ProfileManager;
import org.maritimemc.core.reports.ReportController;
import org.maritimemc.core.reports.Reports;
import org.maritimemc.core.reports.model.Report;

import static org.maritimemc.core.service.Locator.locate;

public class ReportCreation implements Callback {

    private final ProfileManager profileManager = locate(ProfileManager.class);
    private final PermissionManager permissionManager = locate(PermissionManager.class);

    private final ReportController reportController;

    public ReportCreation(ReportController reportController) {
        this.reportController = reportController;
    }

    @Override
    public void run(MessageFormat data) {
        int id = Integer.parseInt(((StringMessageFormat) data).getString());

        Report report = reportController.getById(id);

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (permissionManager.hasPermission(profileManager.getCached(onlinePlayer), Reports.ReportPerm.VIEW_REPORT_CREATIONS)) {

                // New Report Report created by PLAYER with ID x.
                String send = ChatColor.translateAlternateColorCodes('&',
                        "&3&lNew Report &7Report created by &a" + report.getCreatorName() + " &7with ID &a" + report.getId() + "&7. Please investigate.");

                onlinePlayer.sendMessage(send);

            }
        }
    }
}
