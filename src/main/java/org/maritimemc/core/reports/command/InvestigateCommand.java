package org.maritimemc.core.reports.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.maritimemc.core.Formatter;
import org.maritimemc.core.command.CommandBase;
import org.maritimemc.core.reports.ReportController;
import org.maritimemc.core.reports.Reports;
import org.maritimemc.core.reports.model.Report;
import org.maritimemc.core.reports.ui.investigate.ReportInvestigateMenu;
import org.maritimemc.core.util.UtilNumber;

import java.util.Collections;
import java.util.List;

public class InvestigateCommand extends CommandBase {

    private final ReportController reportController;

    /**
     * Class constructor
     *
     * @param name              The name of this command.
     * @param reportController  A ReportController instance.
     */
    public InvestigateCommand(String name, ReportController reportController) {
        super(name);
        this.reportController = reportController;

        setAliases(Collections.singletonList("invrep"));
        setRequiredPermission(Reports.ReportPerm.INVESTIGATE_REPORTS);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(Formatter.format("Reports", "&a/investigate <id> &7Investigate a report."));
            return;
        }

        Integer id = UtilNumber.fromString(args[0]);

        if (id == null) {
            sender.sendMessage(Formatter.format("Reports", "That is not a number."));
            return;
        }

        Report report = reportController.getById(id);
        if (report == null) {
            sender.sendMessage(Formatter.format("Reports", "That is not a valid Report ID."));
            return;
        }

        sender.sendMessage(Formatter.format("Reports", "Investigating &aReport #" + id + "&7..."));

        Player player = (Player) sender;
        new ReportInvestigateMenu(report, reportController).open(player);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return null;
    }
}
