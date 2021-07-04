package org.maritimemc.core.reports.ui.investigate;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.maritimemc.core.Formatter;
import org.maritimemc.core.menu.api.Menu;
import org.maritimemc.core.menu.api.MenuButton;
import org.maritimemc.core.reports.ReportController;
import org.maritimemc.core.reports.model.Report;
import org.maritimemc.core.reports.model.ReportStatus;
import org.maritimemc.core.reports.ui.ReportPage;
import org.maritimemc.core.util.ItemBuilder;

public class ReportResolveMenu implements ReportPage {

    private final Report report;
    private final ReportController reportController;

    public ReportResolveMenu(Report report, ReportController reportController) {
        this.report = report;
        this.reportController = reportController;
    }

    @Override
    public void open(Player player) {

        Menu menu = new Menu("Report Resolve", 3);

        MenuButton paper = new MenuButton(
                new ItemBuilder(Material.PAPER)
                        .displayName("&7Investigating: &d&l" + report.getId())
                        .lore("&7You are resolving the report.")
                        .build()
        );

        menu.registerButton(paper, 4);

        MenuButton confirm = new MenuButton(
                new ItemBuilder(Material.WOOL)
                    .durability(13)
                    .displayName("&a&lConfirm")
                    .lore("&aConfirm &7that you would like to resolve", "&7this report.", " ", "&7Status: " + report.getStatus().getColor() + report.getStatus().getName())
                .build()
        ).setWhenClicked(this::resolve);

        menu.registerButton(confirm, 12);

        MenuButton cancel = new MenuButton(
                new ItemBuilder(Material.WOOL)
                        .durability(14)
                        .displayName("&c&lCancel")
                        .lore("&cCancel &7the resolution of this report.")
                        .build()
        ).setWhenClicked(this::cancel);

        menu.registerButton(cancel, 14);

        MenuButton glass = new MenuButton(new ItemBuilder(Material.STAINED_GLASS_PANE).durability(15).displayName("&8Report Resolve").build());
        for (int i = 0; i < 27; i++) if (menu.isSlotEmpty(i)) menu.registerButton(glass, i);

        menu.open(player);

    }

    private void resolve(Player p) {
        p.closeInventory();
        if (report.getStatus() == ReportStatus.PENDING || report.getStatus() == ReportStatus.UNDER_INVESTIGATION) {
            p.sendMessage(Formatter.format("Reports", "You cannot resolve a report without a verdict. Update its status first."));
            return;
        }

        p.playSound(p.getLocation(), Sound.LEVEL_UP, 5F, 7F);
        reportController.doResolution(report, p);
    }

    private void cancel(Player p) {
        p.closeInventory();
        p.sendMessage(Formatter.format("Reports", "You cancelled the resolution."));
    }
}
