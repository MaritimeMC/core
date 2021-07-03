package org.maritimemc.core.vanish;

import lombok.SneakyThrows;
import org.maritimemc.core.db.SqlModule;
import org.maritimemc.core.util.UtilUuid;
import org.maritimemc.db.SqlDatastore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;

import static org.maritimemc.core.service.Locator.locate;

public class VanishDataManager {

    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS staff_incognito (uuid BINARY(16), status TINYINT(1));";
    private static final String GET_STATUS = "SELECT status FROM staff_incognito WHERE uuid = ?;";
    private static final String UPDATE_STATUS = "UPDATE staff_incognito SET status = ? WHERE uuid = ?;";
    private static final String INSERT_STATUS = "INSERT INTO staff_incognito (uuid, status) VALUES (?, ?);";

    private final SqlDatastore sqlDatastore = locate(SqlModule.class);

    public VanishDataManager() {
        createTable();
    }

    @SneakyThrows
    private void createTable() {
        try (Connection conn = sqlDatastore.getMaritimeDatabase().getConnection()) {
            conn.prepareStatement(CREATE_TABLE).executeUpdate();
        }
    }

    @SneakyThrows
    public boolean getStatus(UUID uuid) {
        try (Connection conn = sqlDatastore.getMaritimeDatabase().getConnection()) {
            PreparedStatement ps = conn.prepareStatement(GET_STATUS);
            ps.setBytes(1, UtilUuid.toBytes(uuid));

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("status") == 1;
            }

            return false;
        }
    }

    @SneakyThrows
    public void setStatus(UUID uuid, boolean status) {
        try (Connection conn = sqlDatastore.getMaritimeDatabase().getConnection()) {
            PreparedStatement ps = conn.prepareStatement(UPDATE_STATUS);
            ps.setInt(1, status ? 1 : 0);
            ps.setBytes(2, UtilUuid.toBytes(uuid));

            if (ps.executeUpdate() <= 0) {
                ps = conn.prepareStatement(INSERT_STATUS);
                ps.setBytes(1, UtilUuid.toBytes(uuid));
                ps.setInt(2, status ? 1 : 0);

                ps.executeUpdate();
            }
        }
    }
}
