package org.maritimemc.core.reports.ui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.maritimemc.core.menu.api.Menu;
import org.maritimemc.core.menu.api.MenuButton;
import org.maritimemc.core.reports.ReportController;
import org.maritimemc.core.reports.model.Report;
import org.maritimemc.core.reports.model.ReportCategory;
import org.maritimemc.core.reports.model.ReportSkeleton;
import org.maritimemc.core.util.ItemBuilder;

import java.util.function.BiConsumer;

public class ReportCategoryMenu implements ReportPage {

    private final ReportController reportController;
    private final ReportSkeleton reportSkeleton;
    private final BiConsumer<ReportController.ReportCreationResponse, Report> creationCallback;

    public ReportCategoryMenu(ReportController reportController, ReportSkeleton reportSkeleton, BiConsumer<ReportController.ReportCreationResponse, Report> creationCallback) {
        this.reportController = reportController;
        this.reportSkeleton = reportSkeleton;
        this.creationCallback = creationCallback;
    }

    @Override
    public void open(Player player) {

        Menu menu = new Menu("Report Category", 3);

        menu.registerButton(new MenuButton(
                new ItemBuilder(Material.COAL)
                        .displayName("&7Reporting: &d&l" + reportSkeleton.getName())
                        .lore("&d'" + reportSkeleton.getReason() + "&d'")
                        .build()
        ), 4);

        menu.registerButton(new MenuButton(
                new ItemBuilder(ReportCategory.CHAT.getMaterial())
                        .displayName(ReportCategory.CHAT.getChatColor() + ReportCategory.CHAT.getName())
                        .lore("&7Click to report the user in", "&7this category.")
                        .build()
        ).setWhenClicked(p -> createReport(p, ReportCategory.CHAT)), 11);

        menu.registerButton(new MenuButton(
                new ItemBuilder(ReportCategory.CLIENT.getMaterial())
                        .displayName(ReportCategory.CLIENT.getChatColor() + ReportCategory.CLIENT.getName())
                        .lore("&7Click to report the user in", "&7this category.")
                        .build()
        ).setWhenClicked(p -> createReport(p, ReportCategory.CLIENT)), 13);

        menu.registerButton(new MenuButton(
                new ItemBuilder(ReportCategory.GAMEPLAY.getMaterial())
                        .displayName(ReportCategory.GAMEPLAY.getChatColor() + ReportCategory.GAMEPLAY.getName())
                        .lore("&7Click to report the user in", "&7this category.")
                        .build()
        ).setWhenClicked(p -> createReport(p, ReportCategory.GAMEPLAY)), 15);

        MenuButton glass = new MenuButton(
                new ItemBuilder(Material.STAINED_GLASS_PANE)
                        .durability(15)
                        .displayName("&8Report Category")
                        .build()
        );

        for (int i = 0; i < 27; i++) {
            if (menu.isSlotEmpty(i)) menu.registerButton(glass, i);
        }

        menu.open(player);

    }

    private void createReport(Player player, ReportCategory reportCategory) {
        player.closeInventory();

        reportSkeleton.setCategory(reportCategory);

        reportController.createReport(player, reportSkeleton).whenComplete((pair, x) -> {
            if (x != null) {
                x.printStackTrace();
                return;
            }

            creationCallback.accept(pair.getReportCreationResponse(), pair.getReport());
        });

    }
}
