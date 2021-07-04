package org.maritimemc.core.reports.ui.investigate;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.maritimemc.core.menu.api.Menu;
import org.maritimemc.core.menu.api.MenuButton;
import org.maritimemc.core.reports.model.Report;
import org.maritimemc.core.reports.ui.ReportPage;
import org.maritimemc.core.util.ItemBuilder;
import org.maritimemc.core.util.UtilTime;
import org.maritimemc.core.util.UuidNameFetcher;

public class ReportInformationPage implements ReportPage {

    private final Report report;
    private final ReportPage goBack;

    public ReportInformationPage(Report report, ReportPage goBack) {
        this.report = report;
        this.goBack = goBack;
    }

    @Override
    public void open(Player player) {

        Menu menu = new Menu("Report Information", 3);

        MenuButton head = new MenuButton(
                new ItemBuilder(Material.MELON)
                        .displayName("&7Investigating: &d&l#" + report.getId())
                        .lore("&7You are viewing information about this report.")
                        .build()
        );

        menu.registerButton(head, 4);

        String creatorName = UuidNameFetcher.fetchName(report.getCreator());
        String offenderName = UuidNameFetcher.fetchName(report.getOffenderUuid());

        MenuButton creator = new MenuButton(new ItemBuilder(Material.SKULL_ITEM).displayName("&d&lReport Creator").lore("&7Name: &d" + creatorName, "&7UUID: &d" + report.getCreator().toString()).skullOwner(creatorName).build());
        MenuButton offender = new MenuButton(new ItemBuilder(Material.SKULL_ITEM).displayName("&d&lSuspect").lore("&7Name: &d" + offenderName, "&7Name at Report Creation: &d" + report.getOffenderName(), "&7UUID: &d" + report.getOffenderUuid().toString()).skullOwner(offenderName).build());
        MenuButton status = new MenuButton(new ItemBuilder(Material.STAINED_CLAY).displayName("&d&lStatus").lore("&7Current Status: " + report.getStatus().getColor() + report.getStatus().getName()).durability(ReportInvestigateMenu.getColorForStatus(report.getStatus())).build());
        MenuButton category = new MenuButton(new ItemBuilder(report.getCategory().getMaterial()).displayName("&d&lOffence Category").lore("&7Category: " + report.getCategory().getChatColor() + report.getCategory().getName()).build());
        ItemBuilder resolvedBuilder = new ItemBuilder(Material.SHEARS).displayName("&d&lResolution").lore("&7Is Resolved: " + ((report.isResolved()) ? "&aYes" : "&cNo"));
        if (report.isResolved()) {
            resolvedBuilder.addToLore(" ", "&7Resolved At: &d" + UtilTime.formatDate(report.getResolvedTime()), "&7Resolved By: &d" + UuidNameFetcher.fetchName(report.getResolvedBy()));
        }
        MenuButton resolved = new MenuButton(resolvedBuilder.build());

        MenuButton reason = new MenuButton(new ItemBuilder(Material.PAPER).displayName("&d&lReason for Report").lore("&7Reason: &d'" + report.getReason() + "&d'").build());
        MenuButton time = new MenuButton(new ItemBuilder(Material.WATCH).displayName("&d&lReport Time").lore("&7Time of Creation: &d" + UtilTime.formatDate(report.getReportTime())).build());

        menu.registerButton(creator, 10);
        menu.registerButton(offender, 11);
        menu.registerButton(status, 12);
        menu.registerButton(category, 13);
        menu.registerButton(resolved, 14);
        menu.registerButton(reason, 15);
        menu.registerButton(time, 16);

        MenuButton back = new MenuButton(
                new ItemBuilder(Material.BARRIER)
                        .displayName("&c&lGo Back")
                        .lore("&7Return to the previous page.")
                        .build()
        ).setWhenClicked(goBack::open);

        menu.registerButton(back, 18);

        MenuButton glass = new MenuButton(new ItemBuilder(Material.STAINED_GLASS_PANE).durability(15).displayName("&8Report Information").build());
        for (int i = 0; i < 27; i++) if (menu.isSlotEmpty(i)) menu.registerButton(glass, i);

        menu.open(player);
        
    }
}
