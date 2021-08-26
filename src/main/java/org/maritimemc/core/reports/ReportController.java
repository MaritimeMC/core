package org.maritimemc.core.reports;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.maritimemc.core.Formatter;
import org.maritimemc.core.chatlog.ChatLog;
import org.maritimemc.core.chatlog.ChatLogModule;
import org.maritimemc.core.db.messaging.DatabaseMessageManager;
import org.maritimemc.core.db.messaging.format.StringMessageFormat;
import org.maritimemc.core.reports.event.ReportCreateEvent;
import org.maritimemc.core.reports.model.Report;
import org.maritimemc.core.reports.model.ReportCategory;
import org.maritimemc.core.reports.model.ReportSkeleton;
import org.maritimemc.core.reports.model.ReportStatus;
import org.maritimemc.core.service.Locator;
import org.maritimemc.core.sync.DiscordSyncModule;
import org.maritimemc.core.thread.ThreadPool;
import org.maritimemc.core.util.ReplacingHashSet;
import org.maritimemc.core.util.UtilServer;
import org.maritimemc.core.util.UuidNameFetcher;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ReportController {

    private final DatabaseMessageManager databaseMessageManager = Locator.locate(DatabaseMessageManager.class);
    private final DiscordSyncModule discordSyncModule = Locator.locate(DiscordSyncModule.class);

    private final ChatLogModule chatLogModule = Locator.locate(ChatLogModule.class);

    private final ReportDataManager reportDataManager;
    private final Set<Report> reportCache;

    public ReportController() {
        this.reportDataManager = new ReportDataManager();
        this.reportCache = new ReplacingHashSet<>();
    }

    public Report getById(int id) {
        Optional<Report> any = reportCache.stream().filter(r -> r.getId() == id).findAny();

        return any.orElseGet(() -> fetchReport(id));
    }

    private Report fetchReport(int id) {
        Report report = reportDataManager.getReportById(id);

        if (report != null) {
            reportCache.add(report);
        }

        return report;
    }

    public CompletableFuture<ReportCreationPair> createReport(Player creator, ReportSkeleton reportSkeleton) {
        return CompletableFuture.supplyAsync(() -> {
            ReportCreateEvent reportCreateEvent = new ReportCreateEvent(creator.getUniqueId());
            Bukkit.getScheduler().runTask(UtilServer.getPlugin(), () -> Bukkit.getPluginManager().callEvent(reportCreateEvent));

            if (reportCreateEvent.isCancelled()) {
                return new ReportCreationPair(ReportCreationResponse.REPORT_BANNED, null);
            }

            String offenderName = reportSkeleton.getName();
            String reason = reportSkeleton.getReason();
            ReportCategory reportCategory = reportSkeleton.getCategory();

            Long discordId = discordSyncModule.getLinkManager().getDiscordId(creator.getUniqueId());


            UUID uuid = UuidNameFetcher.fetchUuid(offenderName);
            if (uuid == null) {
                return new ReportCreationPair(ReportCreationResponse.NAME_INVALID, null);
            }

            String name = UuidNameFetcher.fetchName(uuid);
            if (name == null) {
                return new ReportCreationPair(ReportCreationResponse.NAME_INVALID, null);
            }

            String token = null;
            if (reportCategory == ReportCategory.CHAT) {
                token = chatLogModule.createChatLog(creator.getUniqueId(), creator.getUniqueId(), false).join();
            }

            Report report = new Report(
                    -1,
                    creator.getUniqueId(),
                    creator.getName(),
                    discordId,
                    uuid,
                    name,
                    reportCategory,
                    false,
                    null,
                    null,
                    reason,
                    System.currentTimeMillis(),
                    ReportStatus.PENDING,
                    null,
                    null,
                    token
            );

            reportCache.add(report);

            int i = reportDataManager.insertReport(report);
            report.setId(i);
            databaseMessageManager.send(Reports.REPORT_CREATION_CHANNEL, new StringMessageFormat(String.valueOf(i)));

            return new ReportCreationPair(ReportCreationResponse.SUCCESS, report);
        });
    }

    public void updateReportStatus(Report report, ReportStatus status) {
        report.setStatus(status);

        ThreadPool.ASYNC_POOL.submit(() -> {
            reportDataManager.updateReportStatus(report.getId(), status);
        });
    }

    public void doResolution(Report report, Player p) {
        report.setResolved(true);
        report.setResolvedBy(p.getUniqueId());
        report.setResolvedTime(System.currentTimeMillis());

        ThreadPool.ASYNC_POOL.submit(() -> {
            reportDataManager.updateResolution(report.getId(), report.isResolved(), report.getResolvedTime(), report.getResolvedBy());
        });

        databaseMessageManager.send(Reports.REPORT_RESOLVE_CHANNEL, new StringMessageFormat(String.valueOf(report.getId())));

        p.sendMessage(Formatter.format("Reports", "You resolved the report."));
    }

    public CompletableFuture<Set<Report>> getOpenReports() {
        return CompletableFuture.supplyAsync(() -> {
            Set<Report> reports = new HashSet<>();

            for (Report openReport : reportDataManager.getOpenReports()) {
                reports.add(openReport);
                reportCache.add(openReport);
            }

            return reports;
        });
    }

    public void undoResolution(Report report) {
        report.setResolved(false);
        report.setResolvedBy(null);
        report.setResolvedTime(null);

        ThreadPool.ASYNC_POOL.submit(() -> {
            reportDataManager.undoResolution(report.getId());
        });
    }

    public enum ReportCreationResponse {
        SUCCESS,
        NAME_INVALID,
        REPORT_BANNED;
    }
}
