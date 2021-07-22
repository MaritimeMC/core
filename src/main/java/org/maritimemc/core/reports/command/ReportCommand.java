package org.maritimemc.core.reports.command;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.maritimemc.core.Formatter;
import org.maritimemc.core.command.CommandBase;
import org.maritimemc.core.reports.ReportController;
import org.maritimemc.core.reports.model.Report;
import org.maritimemc.core.reports.model.ReportSkeleton;
import org.maritimemc.core.reports.ui.ReportCategoryMenu;

import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class ReportCommand extends CommandBase {

    private final ReportController reportController;

    /**
     * Class constructor
     *
     * @param name              The name of this command.
     * @param reportController A ReportController instance.
     */
    public ReportCommand(String name, ReportController reportController) {
        super(name);
        this.reportController = reportController;

        setAliases(Collections.singletonList("newreport"));
        setConsoleExecutable(false);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (args.length < 2) {
            sender.sendMessage(Formatter.format("Reports", "&a/report <player> <reason> &7Report a player."));
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            sb.append(args[i]).append(" ");
        }

        String name = args[0];
        String reason = sb.substring(0, sb.length() - 1);

        ReportSkeleton reportSkeleton = new ReportSkeleton(name, reason);

        Player player = (Player) sender;

        BiConsumer<ReportController.ReportCreationResponse, Report> callback = (r, rep) -> {

            switch (r) {
                case REPORT_BANNED:
                    return;
                case NAME_INVALID:
                    sender.sendMessage(Formatter.format("Reports",  "That player name is invalid."));
                    return;
                case SUCCESS: {
                    sender.sendMessage(Formatter.format("Reports", "You created the report. &a(ID: " + rep.getId() + ")"));
                    if (rep.getChatLogToken() != null) {
                        sender.sendMessage(Formatter.format("Reports", "We created a log of all your messages so that staff members can view the player's offence. The token is: &a" + rep.getChatLogToken()));
                    }
                }
            }

        };

        new ReportCategoryMenu(reportController, reportSkeleton, callback).open(player);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
        }

        return null;
    }
}
