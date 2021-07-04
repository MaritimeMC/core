package org.maritimemc.core.reports;

import org.maritimemc.core.Module;
import org.maritimemc.core.command.CommandCenter;
import org.maritimemc.core.db.messaging.DatabaseMessageManager;
import org.maritimemc.core.db.messaging.MessageChannel;
import org.maritimemc.core.perm.PermissionManager;
import org.maritimemc.core.reports.command.InvestigateCommand;
import org.maritimemc.core.reports.command.ReportCommand;
import org.maritimemc.core.reports.command.ReportsCommand;
import org.maritimemc.core.reports.message.ReportCreation;
import org.maritimemc.core.reports.message.ReportResolve;
import org.maritimemc.data.perm.Permission;
import org.maritimemc.data.perm.PermissionGroup;

import static org.maritimemc.core.service.Locator.locate;

public class Reports implements Module {

    public static final MessageChannel REPORT_CREATION_CHANNEL = new MessageChannel("Reports", "reportCreation");
    public static final MessageChannel REPORT_RESOLVE_CHANNEL = new MessageChannel("Reports", "reportResolve");

    private final PermissionManager permissionManager = locate(PermissionManager.class);

    public Reports() {

        ReportController reportController = new ReportController();

        locate(CommandCenter.class).register(
                new ReportCommand("report", reportController),
                new InvestigateCommand("investigate", reportController),
                new ReportsCommand("reports", reportController)
        );

        DatabaseMessageManager databaseMessageManager = locate(DatabaseMessageManager.class);

        databaseMessageManager.registerStringCallback(REPORT_CREATION_CHANNEL, new ReportCreation(reportController));
        databaseMessageManager.registerStringCallback(REPORT_RESOLVE_CHANNEL, new ReportResolve(reportController));

        generatePermissions();
    }

    public enum ReportPerm implements Permission {
        VIEW_REPORT_CREATIONS,
        INVESTIGATE_REPORTS,
        VIEW_REPORT_RESOLUTIONS,
        REPORTS_COMMAND;
    }

    public void generatePermissions() {
        permissionManager.addPermission(PermissionGroup.HELPER, ReportPerm.VIEW_REPORT_CREATIONS, true);
        permissionManager.addPermission(PermissionGroup.HELPER, ReportPerm.VIEW_REPORT_RESOLUTIONS, true);
        permissionManager.addPermission(PermissionGroup.HELPER, ReportPerm.INVESTIGATE_REPORTS, true);
        permissionManager.addPermission(PermissionGroup.HELPER, ReportPerm.REPORTS_COMMAND, true);
    }
}
