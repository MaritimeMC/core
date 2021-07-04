package org.maritimemc.core.reports.ui.investigate;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.maritimemc.core.menu.api.Menu;
import org.maritimemc.core.menu.api.MenuButton;
import org.maritimemc.core.reports.ReportController;
import org.maritimemc.core.reports.model.Report;
import org.maritimemc.core.reports.ui.ReportPage;
import org.maritimemc.core.util.ItemBuilder;

public class ReportAlreadyResolvedMenu implements ReportPage {

    private final Report report;
    private final ReportController reportController;

    public ReportAlreadyResolvedMenu(Report report, ReportController reportController) {
        this.report = report;
        this.reportController = reportController;
    }

    @Override
    public void open(Player player) {

        Menu menu = new Menu("Report Investigate", 3);

        MenuButton button = new MenuButton(
                new ItemBuilder(Material.WOOL)
                        .displayName("&a&lReport Already Resolved")
                        .lore("&7This report has already been resolved.", " ", "&cClick to re-open the report.")
                        .glow()
                        .build()
        ).setWhenClicked(this::undoResolve);

        menu.registerButton(button, 12);

        MenuButton info = new MenuButton(
                new ItemBuilder(Material.MAP)
                        .displayName("&6&lView Report Information")
                        .lore("&7View information about this report.")
                        .glow()
                        .build()
        ).setWhenClicked(p -> new ReportInformationPage(report, this).open(player));

        menu.registerButton(info, 14);

        MenuButton glass = new MenuButton(new ItemBuilder(Material.STAINED_GLASS_PANE).durability(15).displayName("&8Report Investigate").build());
        for (int i = 0; i < 27; i++) if (menu.isSlotEmpty(i)) menu.registerButton(glass, i);

        menu.open(player);
    }

    public void undoResolve(Player player) {
        reportController.undoResolution(report);
        new ReportInvestigateMenu(report, reportController).open(player);
    }
}
