package org.maritimemc.core.reports.ui.list;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.maritimemc.core.menu.api.Menu;
import org.maritimemc.core.menu.api.MenuButton;
import org.maritimemc.core.reports.ReportController;
import org.maritimemc.core.reports.model.Report;
import org.maritimemc.core.reports.ui.ReportPage;
import org.maritimemc.core.reports.ui.investigate.ReportInvestigateMenu;
import org.maritimemc.core.util.ItemBuilder;
import org.maritimemc.core.util.PageUtil;
import org.maritimemc.core.util.UtilTime;
import org.maritimemc.core.util.UuidNameFetcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ReportListMenu implements ReportPage {

    private final ReportController reportController;

    private final CompletableFuture<?> future;

    private final List<ItemStack> stackList;

    public ReportListMenu(ReportController reportController) {
        this.reportController = reportController;

        this.stackList = new ArrayList<>();

        CompletableFuture<Set<Report>> completableFuture = reportController.getOpenReports();

        this.future = completableFuture.whenComplete((reports, x) -> {
            if (x != null) {
                x.printStackTrace();
                return;
            }

            for (Report openReport : reports) {
                ItemBuilder builder = new ItemBuilder(Material.SKULL_ITEM)
                        .displayName("&d&lReport #" + openReport.getId())
                        .skullOwner(UuidNameFetcher.fetchName(openReport.getOffenderUuid()))
                        .lore(" ", "&7Current Status: " + openReport.getStatus().getColor() + openReport.getStatus().getName(), "&7Time Created: &d" + UtilTime.formatDate(openReport.getReportTime()), " ", "&cClick to investigate.");

                stackList.add(builder.build());
            }
        });
    }

    @Override
    public void open(Player player) {
        open(player, 1);
    }

    public void open(Player player, int page) {
        future.whenComplete((v, x) -> {
            if (x != null) {
                x.printStackTrace();
                return;
            }

            Menu menu = new Menu("Reports", 6);

            menu.registerButton(new MenuButton(new ItemBuilder(Material.SIGN).displayName("&c&lView Open Reports").lore("&7View all unresolved reports.").build()),4);

            MenuButton glass = new MenuButton(new ItemBuilder(Material.STAINED_GLASS_PANE).durability(15).displayName("&8Reports").build());
            for (int i = 0; i < 54; i++) if (isSlotBlackPane(i)) menu.registerButton(glass, i);

            List<ItemStack> pageItems = PageUtil.getPageItems(stackList, page, 28);

            if (PageUtil.isPageValid(stackList, page-1, 28))
                menu.registerButton(new MenuButton(new ItemBuilder(Material.ARROW).displayName("&c&lPrevious Page").lore("&7Go back 1 page.").build()).setWhenClicked(p -> new ReportListMenu(reportController).open(p, page-1)), 48);
            else
                menu.registerButton(glass, 48);

            if (PageUtil.isPageValid(stackList, page+1, 28))
                menu.registerButton(new MenuButton(new ItemBuilder(Material.ARROW).displayName("&a&lNext Page").lore("&7Go forward 1 page.").build()).setWhenClicked(p -> new ReportListMenu(reportController).open(p, page+1)), 50);
            else
                menu.registerButton(glass, 50);

            menu.registerButton(new MenuButton(new ItemBuilder(Material.NAME_TAG).displayName("&e&lPage " + page).lore("&7This is your current page.").build()),49);

            int slot = 10;
            for (ItemStack pageItem : pageItems) {
                while (isSlotBlackPane(slot)) slot++;

                int id = Integer.parseInt(ChatColor.stripColor(pageItem.getItemMeta().getDisplayName()).replace("Report #", ""));

                menu.registerButton(new MenuButton(pageItem).setWhenClicked(p -> new ReportInvestigateMenu(reportController.getById(id), reportController).open(p)), slot);

                slot++;
            }

            menu.open(player);
        });
    }

    public boolean isSlotBlackPane(int i) {
        if (i >= 0 && i < 9 && i != 4) return true;
        if (i % 9 == 0) return true;
        if ((i+1) % 9 == 0) return true;

        return i >= 45 && i < 54 && !(i >= 48 && i < 51);
    }
}
