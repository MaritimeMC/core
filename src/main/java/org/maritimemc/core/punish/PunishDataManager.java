package org.maritimemc.core.punish;

import lombok.SneakyThrows;
import org.maritimemc.core.db.SqlModule;
import org.maritimemc.core.punish.api.Punishment;
import org.maritimemc.core.punish.api.model.PunishmentType;
import org.maritimemc.core.punish.api.pojo.Archival;
import org.maritimemc.core.service.Locator;
import org.maritimemc.core.util.UtilUuid;
import org.maritimemc.db.SqlDatastore;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PunishDataManager {

    private static final String CREATE_PUNISH_TABLE = "CREATE TABLE IF NOT EXISTS user_punishments (id INT NOT NULL AUTO_INCREMENT, uuid BINARY(16), staffUuid BINARY(16), reason TEXT, type TEXT, category TEXT, severe TINYINT(1), timePunished BIGINT, duration BIGINT, seen TINYINT(1), archivedBy BINARY(16), archivedAt BIGINT, PRIMARY KEY (id));";
    private static final String GET_PUNISHMENTS = "SELECT * FROM user_punishments WHERE uuid = ?;";
    private static final String ADD_PUNISHMENT = "INSERT INTO user_punishments (uuid, staffUuid, reason, type, category, severe, timePunished, duration, seen, archivedBy, archivedAt) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
    private static final String SET_ARCHIVAL = "UPDATE user_punishments SET archivedBy = ?, archivedAt = ? WHERE id = ?;";
    private static final String SET_SEEN_TRUE = "UPDATE user_punishments SET seen = 1 WHERE id = ?;";
    private static final String DELETE_PUNISHMENT = "DELETE FROM user_punishments WHERE id = ?;";

    private final SqlDatastore sqlDatastore = Locator.locate(SqlModule.class);

    public PunishDataManager() {
        createTables();
    }

    @SneakyThrows
    public void createTables() {
        try (Connection conn = sqlDatastore.getMaritimeDatabase().getConnection()) {
            conn.prepareStatement(CREATE_PUNISH_TABLE).executeUpdate();
        }
    }

    @SneakyThrows
    public Set<Punishment> getPunishmentsForUser(UUID uuid) {
        try (Connection conn = sqlDatastore.getMaritimeDatabase().getConnection()) {

            PreparedStatement ps = conn.prepareStatement(GET_PUNISHMENTS);
            ps.setBytes(1, UtilUuid.toBytes(uuid));

            ResultSet resultSet = ps.executeQuery();

            Set<Punishment> punishments = new HashSet<>();
            while (resultSet.next()) {
                punishments.add(parse(resultSet));
            }

            return punishments;
        }
    }

    public CompletableFuture<Void> addPunishment(Punishment punishment) {
        return CompletableFuture.runAsync(() -> {
            try (Connection conn = sqlDatastore.getMaritimeDatabase().getConnection()) {

                PreparedStatement ps = conn.prepareStatement(ADD_PUNISHMENT, Statement.RETURN_GENERATED_KEYS);
                ps.setBytes(1, UtilUuid.toBytes(punishment.getUuid()));
                ps.setBytes(2, UtilUuid.toBytes(punishment.getStaffUuid()));
                ps.setString(3, punishment.getReason());
                ps.setString(4, punishment.getType().getId());
                ps.setString(5, punishment.getCategory().name());
                ps.setInt(6, punishment.isSevere() ? 1 : 0);
                ps.setLong(7, punishment.getTimePunished());
                ps.setLong(8, punishment.getDuration());
                ps.setInt(9, punishment.isSeen() ? 1 : 0);

                if (punishment.getArchival() == null) {
                    ps.setNull(10, Types.BINARY);
                    ps.setNull(11, Types.BIGINT);
                } else {
                    ps.setBytes(10, UtilUuid.toBytes(punishment.getArchival().getArchivedBy()));
                    ps.setLong(11, punishment.getArchival().getArchivedAt());
                }

                ps.executeUpdate();

                ResultSet generatedKeys = ps.getGeneratedKeys();
                generatedKeys.next();
                punishment.setId(generatedKeys.getInt(1));

            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

    public CompletableFuture<Void> setArchival(Punishment punishment, Archival archival) {
        return CompletableFuture.runAsync(() -> {
            try (Connection conn = sqlDatastore.getMaritimeDatabase().getConnection()) {

                PreparedStatement ps = conn.prepareStatement(SET_ARCHIVAL);
                ps.setBytes(1, UtilUuid.toBytes(archival.getArchivedBy()));
                ps.setLong(2, archival.getArchivedAt());
                ps.setInt(3, punishment.getId());

                ps.executeUpdate();

            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

    public CompletableFuture<Void> setSeenTrue(Punishment punishment) {
        return CompletableFuture.runAsync(() -> {
            try (Connection conn = sqlDatastore.getMaritimeDatabase().getConnection()) {

                PreparedStatement ps = conn.prepareStatement(SET_SEEN_TRUE);
                ps.setInt(1, punishment.getId());

                ps.executeUpdate();

            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

    public CompletableFuture<Void> deletePunishment(Punishment punishment) {
        return CompletableFuture.runAsync(() -> {
            try (Connection conn = sqlDatastore.getMaritimeDatabase().getConnection()) {

                PreparedStatement ps = conn.prepareStatement(DELETE_PUNISHMENT);
                ps.setInt(1, punishment.getId());

                ps.executeUpdate();

            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

    @SneakyThrows
    private Punishment parse(ResultSet resultSet) {
        int id = resultSet.getInt("id");
        UUID uuid = UtilUuid.fromBytes(resultSet.getBytes("uuid"));
        UUID staffUuid = UtilUuid.fromBytes(resultSet.getBytes("staffUuid"));
        String reason = resultSet.getString("reason");
        PunishmentType type = PunishmentType.matchFromId(resultSet.getString("type"));
        Punishment.OffenceCategory category = Punishment.OffenceCategory.valueOf(resultSet.getString("category"));
        boolean severe = resultSet.getInt("severe") == 1;
        long timePunished = resultSet.getLong("timePunished");
        long duration = resultSet.getLong("duration");
        boolean seen = resultSet.getInt("seen") == 1;
        UUID archivedBy = UtilUuid.fromBytes(resultSet.getBytes("archivedBy"));
        long archivedAt = resultSet.getLong("archivedAt");

        return new Punishment(
                id,
                uuid,
                staffUuid,
                reason,
                type,
                category,
                severe,
                timePunished,
                duration,
                seen,
                (archivedBy == null && archivedAt == 0 ? null : new Archival(archivedBy, archivedAt))
        );
    }
}
