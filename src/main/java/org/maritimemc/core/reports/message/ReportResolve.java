package org.maritimemc.core.reports.message;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.maritimemc.core.Formatter;
import org.maritimemc.core.db.messaging.format.MessageFormat;
import org.maritimemc.core.db.messaging.format.StringMessageFormat;
import org.maritimemc.core.db.messaging.listen.Callback;
import org.maritimemc.core.perm.PermissionManager;
import org.maritimemc.core.profile.ProfileManager;
import org.maritimemc.core.reports.ReportController;
import org.maritimemc.core.reports.Reports;
import org.maritimemc.core.reports.model.Report;

import static org.maritimemc.core.service.Locator.locate;

public class ReportResolve implements Callback {

    private final ProfileManager profileManager = locate(ProfileManager.class);
    private final PermissionManager permissionManager = locate(PermissionManager.class);
    private final ReportController reportController;

    public ReportResolve(ReportController reportController) {
        this.reportController = reportController;
    }

    @Override
    public void run(MessageFormat data) {
        int id = Integer.parseInt(((StringMessageFormat) data).getString());

        Report report = reportController.getById(id);

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (permissionManager.hasPermission(profileManager.getCached(onlinePlayer), Reports.ReportPerm.VIEW_REPORT_RESOLUTIONS)) {

                String send = ChatColor.translateAlternateColorCodes('&',
                        "&3&lReport Resolved &7Report &a#" + report.getId() + " &7was resolved.");

                onlinePlayer.sendMessage(send);

            }

            if (onlinePlayer.getUniqueId().equals(report.getCreator())) {
                String send = Formatter.format("Reports", "The report you created against &a" + report.getOffenderName() + " &7has been resolved. Thank you for reporting!");

                onlinePlayer.sendMessage(send);
            }
        }
    }
}
