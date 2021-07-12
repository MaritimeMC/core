package org.maritimemc.core.reports;

import lombok.SneakyThrows;
import org.maritimemc.core.db.SqlModule;
import org.maritimemc.core.reports.model.Report;
import org.maritimemc.core.reports.model.ReportCategory;
import org.maritimemc.core.reports.model.ReportStatus;
import org.maritimemc.core.service.Locator;
import org.maritimemc.core.util.UtilUuid;
import org.maritimemc.db.SqlDatastore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ReportDataManager {

    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS user_reports (id INT NOT NULL AUTO_INCREMENT, creator BINARY(16), creatorName VARCHAR(16), discordId BIGINT, offenderUuid BINARY(16), offenderName VARCHAR(16), category TEXT, resolved TINYINT(1), resolvedTime BIGINT, resolvedBy BINARY(16), reason TEXT, reportTime BIGINT, reportStatus TEXT, publicDiscordChannelId BIGINT, staffDiscordMessageId BIGINT, PRIMARY KEY (id));";
    private static final String INSERT_REPORT = "INSERT INTO user_reports (creator, creatorName, discordId, offenderUuid, offenderName, category, reason, reportTime, reportStatus) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";
    private static final String GET_MAX_ID = "SELECT MAX(id) AS m_id FROM user_reports;";
    private static final String GET_REPORT_BY_ID = "SELECT * FROM user_reports WHERE id = ?;";
    private static final String UPDATE_REPORT_STATUS = "UPDATE user_reports SET reportStatus = ? WHERE id = ?;";
    private static final String UPDATE_RESOLUTION = "UPDATE user_reports SET resolved = ?, resolvedTime = ?, resolvedBy = ? WHERE id = ?;";
    private static final String UNDO_RESOLUTION = "UPDATE user_reports SET resolved = 0, resolvedTime = NULL, resolvedBy = NULL WHERE id = ?;";
    private static final String GET_OPEN_REPORTS = "SELECT * FROM user_reports WHERE resolved IS NOT TRUE;";

    private final SqlDatastore sqlDatastore = Locator.locate(SqlModule.class);

    public ReportDataManager() {
        createTable();
    }

    @SneakyThrows
    private void createTable() {
        try (Connection conn = sqlDatastore.getMaritimeDatabase().getConnection()) {
            conn.prepareStatement(CREATE_TABLE).executeUpdate();
        }
    }

    @SneakyThrows
    public int insertReport(Report report) {
        try (Connection conn = sqlDatastore.getMaritimeDatabase().getConnection()) {
            PreparedStatement ps = conn.prepareStatement(INSERT_REPORT);
            ps.setBytes(1, UtilUuid.toBytes(report.getCreator()));
            ps.setString(2, report.getCreatorName());

            if (report.getDiscordId() == null) {
                ps.setNull(3, Types.BIGINT);
            } else {
                ps.setLong(3, report.getDiscordId());
            }

            ps.setBytes(4, UtilUuid.toBytes(report.getOffenderUuid()));
            ps.setString(5, report.getOffenderName());
            ps.setString(6, report.getCategory().name());
            ps.setString(7, report.getReason());
            ps.setLong(8, report.getReportTime());
            ps.setString(9, report.getStatus().name());

            ps.executeUpdate();

            ResultSet rs = conn.prepareStatement(GET_MAX_ID).executeQuery();
            if (rs.next()) {
                return rs.getInt("m_id");
            }

            return -1;
        }
    }

    @SneakyThrows
    public Report getReportById(int id) {
        try (Connection conn = sqlDatastore.getMaritimeDatabase().getConnection()) {

            PreparedStatement ps = conn.prepareStatement(GET_REPORT_BY_ID);
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return decodeReport(rs);

            }

            return null;
        }
    }

    @SneakyThrows
    private Report decodeReport(ResultSet rs) {
        return decodeReport(rs.getInt("id"), rs);
    }

    @SneakyThrows
    private Report decodeReport(int id, ResultSet rs) {
        UUID creator = UtilUuid.fromBytes(rs.getBytes("creator"));
        String creatorName = rs.getString("creatorName");

        long temp = rs.getLong("discordId");
        Long discordId = temp == 0 ? null : temp;

        UUID offenderUuid = UtilUuid.fromBytes(rs.getBytes("offenderUuid"));
        String offenderName = rs.getString("offenderName");

        ReportCategory category = ReportCategory.valueOf(rs.getString("category"));

        boolean resolved = rs.getInt("resolved") == 1;

        temp = rs.getLong("resolvedTime");
        Long resolvedTime = temp == 0 ? null : temp;

        UUID resolvedBy = UtilUuid.fromBytes(rs.getBytes("resolvedBy"));

        String reason = rs.getString("reason");
        long reportTime = rs.getLong("reportTime");
        ReportStatus status = ReportStatus.valueOf(rs.getString("reportStatus"));

        temp = rs.getLong("publicDiscordChannelId");
        Long publicDiscordChannelId = temp == 0 ? null : temp;

        temp = rs.getLong("staffDiscordMessageId");
        Long staffDiscordMessageId = temp == 0 ? null : temp;

        return new Report(
                id,
                creator,
                creatorName,
                discordId,
                offenderUuid,
                offenderName,
                category,
                resolved,
                resolvedTime,
                resolvedBy,
                reason,
                reportTime,
                status,
                publicDiscordChannelId,
                staffDiscordMessageId
        );
    }

    @SneakyThrows
    public void updateReportStatus(int id, ReportStatus status) {
        try (Connection conn = sqlDatastore.getMaritimeDatabase().getConnection()) {

            PreparedStatement ps = conn.prepareStatement(UPDATE_REPORT_STATUS);
            ps.setString(1, status.name());
            ps.setInt(2, id);

            ps.executeUpdate();
        }
    }

    @SneakyThrows
    public void updateResolution(int id, boolean resolved, long resolvedTime, UUID resolvedBy) {
        try (Connection conn = sqlDatastore.getMaritimeDatabase().getConnection()) {

            PreparedStatement ps = conn.prepareStatement(UPDATE_RESOLUTION);
            ps.setInt(1, resolved ? 1 : 0);
            ps.setLong(2, resolvedTime);
            ps.setBytes(3, UtilUuid.toBytes(resolvedBy));
            ps.setInt(4, id);

            ps.executeUpdate();
        }
    }

    @SneakyThrows
    public void undoResolution(int id) {
        try (Connection conn = sqlDatastore.getMaritimeDatabase().getConnection()) {

            PreparedStatement ps = conn.prepareStatement(UNDO_RESOLUTION);
            ps.setInt(1, id);

            ps.executeUpdate();
        }
    }

    @SneakyThrows
    public Set<Report> getOpenReports() {
        try (Connection conn = sqlDatastore.getMaritimeDatabase().getConnection()) {

            ResultSet rs = conn.prepareStatement(GET_OPEN_REPORTS).executeQuery();

            Set<Report> set = new HashSet<>();
            while (rs.next()) {
                set.add(decodeReport(rs));
            }

            return set;
        }
    }
}
