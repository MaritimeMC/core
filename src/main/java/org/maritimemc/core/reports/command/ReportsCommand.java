package org.maritimemc.core.reports.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.maritimemc.core.Formatter;
import org.maritimemc.core.command.CommandBase;
import org.maritimemc.core.reports.ReportController;
import org.maritimemc.core.reports.Reports;
import org.maritimemc.core.reports.ui.list.ReportListMenu;

import java.util.List;

public class ReportsCommand extends CommandBase {

    private final ReportController reportController;

    /**
     * Class constructor
     *
     * @param name              The name of this command.
     */
    public ReportsCommand(String name, ReportController reportController) {
        super(name);
        this.reportController = reportController;

        setRequiredPermission(Reports.ReportPerm.REPORTS_COMMAND);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        Player p = (Player) sender;
        p.sendMessage(Formatter.format("Reports", "Viewing open reports..."));
        new ReportListMenu(reportController).open(p);

    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return null;
    }
}
