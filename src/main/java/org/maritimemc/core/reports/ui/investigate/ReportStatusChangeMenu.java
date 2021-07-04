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

public class ReportStatusChangeMenu implements ReportPage {

    private final Report report;
    private final ReportController reportController;
    private final ReportPage goBack;

    public ReportStatusChangeMenu(Report report, ReportController reportController, ReportPage goBack) {
        this.report = report;
        this.reportController = reportController;
        this.goBack = goBack;
    }

    public void open(Player player) {

        Menu menu = new Menu("Report Status", 3);

        MenuButton paper = new MenuButton(
                new ItemBuilder(Material.PAPER)
                        .displayName("&7Investigating: &d&l" + report.getId())
                        .lore("&7Current Status: " + report.getStatus().getColor() + report.getStatus().getName())
                        .build()
        );

        menu.registerButton(paper, 4);

        MenuButton denied = new MenuButton(
                new ItemBuilder(Material.STAINED_CLAY)
                        .displayName(ReportStatus.DENIED.getColor() + "&l" + ReportStatus.DENIED.getName())
                        .glowIf(report.getStatus() == ReportStatus.DENIED)
                        .durability(ReportInvestigateMenu.getColorForStatus(ReportStatus.DENIED))
                        .lore("&7Set the Report Status to " + ReportStatus.DENIED.getColor() + ReportStatus.DENIED.getName() + "&7.")
                        .build()
        ).setWhenClicked((p) -> {
            reportController.updateReportStatus(report, ReportStatus.DENIED);
            open(player);
        });

        MenuButton underInv = new MenuButton(
                new ItemBuilder(Material.STAINED_CLAY)
                        .displayName(ReportStatus.UNDER_INVESTIGATION.getColor() + "&l" + ReportStatus.UNDER_INVESTIGATION.getName())
                        .glowIf(report.getStatus() == ReportStatus.UNDER_INVESTIGATION)
                        .durability(ReportInvestigateMenu.getColorForStatus(ReportStatus.UNDER_INVESTIGATION))
                        .lore("&7Set the Report Status to " + ReportStatus.UNDER_INVESTIGATION.getColor() + ReportStatus.UNDER_INVESTIGATION.getName() + "&7.")
                        .build()
        ).setWhenClicked((p) -> {
            reportController.updateReportStatus(report, ReportStatus.UNDER_INVESTIGATION);
            open(player);
        });

        MenuButton pending = new MenuButton(
                new ItemBuilder(Material.STAINED_CLAY)
                        .displayName(ReportStatus.PENDING.getColor() + "&l" + ReportStatus.PENDING.getName())
                        .glowIf(report.getStatus() == ReportStatus.PENDING)
                        .durability(ReportInvestigateMenu.getColorForStatus(ReportStatus.PENDING))
                        .lore("&7Set the Report Status to " + ReportStatus.PENDING.getColor() + ReportStatus.PENDING.getName() + "&7.")
                        .build()
        ).setWhenClicked((p) -> {
            reportController.updateReportStatus(report, ReportStatus.PENDING);
            open(player);
        });

        MenuButton approved = new MenuButton(
                new ItemBuilder(Material.STAINED_CLAY)
                        .displayName(ReportStatus.APPROVED.getColor() + "&l" + ReportStatus.APPROVED.getName())
                        .glowIf(report.getStatus() == ReportStatus.APPROVED)
                        .durability(ReportInvestigateMenu.getColorForStatus(ReportStatus.APPROVED))
                        .lore("&7Set the Report Status to " + ReportStatus.APPROVED.getColor() + ReportStatus.APPROVED.getName() + "&7.")
                        .build()
        ).setWhenClicked((p) -> {
            reportController.updateReportStatus(report, ReportStatus.APPROVED);
            open(player);
        });

        menu.registerButton(denied, 10);
        menu.registerButton(pending, 12);
        menu.registerButton(underInv, 14);
        menu.registerButton(approved, 16);

        MenuButton back = new MenuButton(
                new ItemBuilder(Material.BARRIER)
                        .displayName("&c&lGo Back")
                        .lore("&7Return to the previous page.")
                        .build()
        ).setWhenClicked(goBack::open);

        menu.registerButton(back, 18);

        MenuButton glass = new MenuButton(new ItemBuilder(Material.STAINED_GLASS_PANE).durability(15).displayName("&8Report Status").build());
        for (int i = 0; i < 27; i++) if (menu.isSlotEmpty(i)) menu.registerButton(glass, i);

        menu.open(player);
    }

}
