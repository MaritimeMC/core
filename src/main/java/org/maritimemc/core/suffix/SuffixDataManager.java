package org.maritimemc.core.suffix;

import lombok.SneakyThrows;
import org.maritimemc.core.db.SqlModule;
import org.maritimemc.core.service.Locator;
import org.maritimemc.core.util.UtilUuid;
import org.maritimemc.data.player.Suffix;
import org.maritimemc.db.SqlDatastore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class SuffixDataManager {

    private static final String CREATE_SUFFIX_TABLE = "CREATE TABLE IF NOT EXISTS user_suffix (uuid BINARY(16), suffix_entry TEXT);";
    private static final String CREATE_ACTIVE_TABLE = "CREATE TABLE IF NOT EXISTS user_active_suffix (uuid BINARY(16), suffix TEXT);";

    private static final String GET_SUFFIXES = "SELECT suffix_entry FROM user_suffix WHERE uuid = ?;";
    private static final String INSERT_SUFFIX = "INSERT INTO user_suffix (uuid, suffix_entry) VALUES (?, ?);";
    private static final String REMOVE_SUFFIX = "DELETE FROM user_suffix WHERE uuid = ? AND suffix_entry = ?;";

    private static final String GET_ACTIVE_SUFFIX = "SELECT suffix FROM user_active_suffix WHERE uuid = ?;";
    private static final String INSERT_ACTIVE_SUFFIX = "INSERT INTO user_active_suffix (uuid, suffix) VALUES (?, ?);";
    private static final String UPDATE_ACTIVE_SUFFIX = "UPDATE user_active_suffix SET suffix = ? WHERE uuid = ?;";
    private static final String REMOVE_ACTIVE_SUFFIX = "DELETE FROM user_active_suffix WHERE uuid = ?;";

    private final SqlDatastore sqlDatastore = Locator.locate(SqlModule.class);

    public SuffixDataManager() {
        createTables();
    }

    @SneakyThrows
    public void createTables() {
        try (Connection conn = sqlDatastore.getMaritimeDatabase().getConnection()) {
            conn.prepareStatement(CREATE_SUFFIX_TABLE).executeUpdate();
            conn.prepareStatement(CREATE_ACTIVE_TABLE).executeUpdate();
        }
    }

    @SneakyThrows
    public Set<Suffix> getSuffixes(UUID uuid) {
        try (Connection conn = sqlDatastore.getMaritimeDatabase().getConnection()) {

            PreparedStatement ps = conn.prepareStatement(GET_SUFFIXES);
            ps.setBytes(1, UtilUuid.toBytes(uuid));

            ResultSet resultSet = ps.executeQuery();

            Set<Suffix> set = new HashSet<>();

            while (resultSet.next()) {
                set.add(Suffix.valueOf(resultSet.getString("suffix_entry")));
            }

            return set;
        }
    }

    @SneakyThrows
    public void insertSuffix(UUID uuid, Suffix suffix) {
        try (Connection conn = sqlDatastore.getMaritimeDatabase().getConnection()) {
            PreparedStatement ps = conn.prepareStatement(INSERT_SUFFIX);
            ps.setBytes(1, UtilUuid.toBytes(uuid));
            ps.setString(2, suffix.name());

            ps.executeUpdate();
        }
    }

    @SneakyThrows
    public void removeSuffix(UUID uuid, Suffix suffix) {
        try (Connection conn = sqlDatastore.getMaritimeDatabase().getConnection()) {
            PreparedStatement ps = conn.prepareStatement(REMOVE_SUFFIX);
            ps.setBytes(1, UtilUuid.toBytes(uuid));
            ps.setString(2, suffix.name());

            ps.executeUpdate();
        }
    }

    @SneakyThrows
    public Suffix getActiveSuffix(UUID uuid) {
        try (Connection conn = sqlDatastore.getMaritimeDatabase().getConnection()) {

            PreparedStatement ps = conn.prepareStatement(GET_ACTIVE_SUFFIX);
            ps.setBytes(1, UtilUuid.toBytes(uuid));

            ResultSet resultSet = ps.executeQuery();

            if (resultSet.next()) {
                return Suffix.valueOf(resultSet.getString("suffix"));
            }

            return null;
        }
    }

    @SneakyThrows
    public void removeActiveSuffix(UUID uuid) {
        try (Connection conn = sqlDatastore.getMaritimeDatabase().getConnection()) {
            PreparedStatement ps = conn.prepareStatement(REMOVE_ACTIVE_SUFFIX);
            ps.setBytes(1, UtilUuid.toBytes(uuid));

            ps.executeUpdate();
        }
    }

    @SneakyThrows
    public void setActiveSuffix(UUID uuid, Suffix suffix) {
        try (Connection conn = sqlDatastore.getMaritimeDatabase().getConnection()) {
            PreparedStatement ps = conn.prepareStatement(UPDATE_ACTIVE_SUFFIX);
            ps.setString(1, suffix.name());
            ps.setBytes(2, UtilUuid.toBytes(uuid));

            if (ps.executeUpdate() <= 0) {
                ps = conn.prepareStatement(INSERT_ACTIVE_SUFFIX);
                ps.setBytes(1, UtilUuid.toBytes(uuid));
                ps.setString(2, suffix.name());

                ps.executeUpdate();
            }
        }
    }


}
