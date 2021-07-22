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

    // The token of the linked ChatLog.
    private final String chatLogToken;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Report report = (Report) o;
        return Objects.equals(creator, report.creator) && Objects.equals(creatorName, report.creatorName) && Objects.equals(discordId, report.discordId) && Objects.equals(offenderUuid, report.offenderUuid) && Objects.equals(offenderName, report.offenderName) && category == report.category && Objects.equals(reason, report.reason) && Objects.equals(reportTime, report.reportTime) && Objects.equals(chatLogToken, report.chatLogToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(creator, creatorName, discordId, offenderUuid, offenderName, category, reason, reportTime, chatLogToken);
    }
}
