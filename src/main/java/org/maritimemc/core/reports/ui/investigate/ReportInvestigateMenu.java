package org.maritimemc.core.reports.ui.investigate;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.maritimemc.core.menu.api.Menu;
import org.maritimemc.core.menu.api.MenuButton;
import org.maritimemc.core.reports.ReportController;
import org.maritimemc.core.reports.model.Report;
import org.maritimemc.core.reports.model.ReportStatus;
import org.maritimemc.core.reports.ui.ReportPage;
import org.maritimemc.core.util.ItemBuilder;
import org.maritimemc.core.util.UuidNameFetcher;
import org.maritimemc.core.versioning.VersionHandler;

public class ReportInvestigateMenu implements ReportPage {

    private final Report report;
    private final ReportController reportController;

    public ReportInvestigateMenu(Report report, ReportController reportController) {
        this.report = report;
        this.reportController = reportController;
    }

    @Override
    public void open(Player player) {
        if (report.isResolved()) {
            new ReportAlreadyResolvedMenu(report, reportController).open(player);
            return;
        }

        Menu menu = new Menu("Report Investigate", 3);

        String name = UuidNameFetcher.fetchName(report.getOffenderUuid());
        if (name == null) return;

        MenuButton head = new MenuButton(
                new ItemBuilder(VersionHandler.NMS_HANDLER.getPlayerHeadItem())
                        .displayName("&dInvestigating #" + report.getId())
                        .skullOwner(name)
                        .lore("&7Player Name: &d" + name)
                .build()
        );

        menu.registerButton(head, 4);

        MenuButton status = new MenuButton(
                new ItemBuilder(Material.STAINED_CLAY)
                        .displayName("&7Status: " + report.getStatus().getColor() + report.getStatus().getName())
                        .lore("&7This is the report's current status.", " ", "&cClick to change.")
                        .durability(getColorForStatus(report.getStatus()))
                .build()
        ).setWhenClicked(p -> new ReportStatusChangeMenu(report, reportController, this).open(p));

        menu.registerButton(status, 11);

        MenuButton resolve = new MenuButton(
                new ItemBuilder(Material.PAPER)
                        .displayName("&aResolve Report")
                        .lore("&7Click to approve/deny the report.")
                        .build()
        ).setWhenClicked(p -> new ReportResolveMenu(report, reportController).open(p));

        menu.registerButton(resolve, 13);

        MenuButton information = new MenuButton(
                new ItemBuilder(Material.MAP)
                        .displayName("&6View Information")
                        .lore("&7Click to view the report's information.")
                        .build()
        ).setWhenClicked(p -> new ReportInformationPage(report, this).open(p));

        menu.registerButton(information, 15);

        MenuButton glass = new MenuButton(new ItemBuilder(Material.STAINED_GLASS_PANE).durability(15).displayName("&8Report Investigate").build());
        for (int i = 0; i < 27; i++) if (menu.isSlotEmpty(i)) menu.registerButton(glass, i);

        menu.open(player);
    }

    public static int getColorForStatus(ReportStatus status) {
        switch (status) {
            case APPROVED:
                return 5;
            case DENIED:
                return 14;
            case PENDING:
                return 4;
            case UNDER_INVESTIGATION:
                return 3;
        }

        return 0;
    }
}
