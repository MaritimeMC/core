package org.maritimemc.core.reports.model;

import lombok.*;

import java.util.Objects;
import java.util.UUID;

@AllArgsConstructor
@Getter
@ToString
public class Report {

    // The ID of this report.
    @Setter
    private int id;

    // The UUID of the creator, otherwise null.
    private final UUID creator;

    // The name of the creator, at time of report.
    private final String creatorName;

    // The Discord ID of the creator, otherwise null.
    private final Long discordId;

    // The UUID of the offender.
    private final UUID offenderUuid;

    // The name of the offender at time of report.
    private final String offenderName;

    // The category of this report.
    private final ReportCategory category;

    // Whether or not this report has been resolved and closed.
    @Setter
    private boolean resolved;

    // The time at which this report was resolved, or null.
    @Setter
    private Long resolvedTime;

    // The player who resolved this report, or null.
    @Setter
    private UUID resolvedBy;

    // The reason for the report.
    private final String reason;

    // The time at which the report was created.
    private final Long reportTime;

    // The status of this report.
    @Setter
    private ReportStatus status;

    // The Channel ID of this report in the Public Discord Server. (or null)
    private final Long publicDiscordChannelId;

    // The Message ID of this report in the Staff Discord Server. (or null)
    private final Long staffDiscordMessageId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Report report = (Report) o;
        return id == report.id && creator.equals(report.creator) && creatorName.equals(report.creatorName) && offenderUuid.equals(report.offenderUuid) && offenderName.equals(report.offenderName) && reason.equals(report.reason) && reportTime.equals(report.reportTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, creator, creatorName, offenderUuid, offenderName, reason, reportTime);
    }
}
