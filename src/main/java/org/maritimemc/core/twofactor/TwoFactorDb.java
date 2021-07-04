package org.maritimemc.core.twofactor;

import lombok.SneakyThrows;
import org.bukkit.entity.Player;
import org.maritimemc.core.db.SqlModule;
import org.maritimemc.core.service.Locator;
import org.maritimemc.core.twofactor.api.ITwoFactorDb;
import org.maritimemc.core.util.UtilUuid;
import org.maritimemc.db.SqlDatastore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;

public class TwoFactorDb implements ITwoFactorDb {

    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS user_2fa (user BINARY(16), secret TEXT, lastLoginTime BIGINT, lastIp TEXT);";
    private static final String INSERT_DATA = "INSERT INTO user_2fa (user, secret, lastLoginTime, lastIp) VALUES (?, ?, ?, ?);";
    private static final String GET_DATA = "SELECT * FROM user_2fa WHERE user = ?;";
    private static final String HAS_DATA = "SELECT 1 FROM user_2fa WHERE user = ?;";
    private static final String REMOVE_DATA = "DELETE FROM user_2fa WHERE user = ?;";
    private static final String UPDATE_LOGIN_DATA = "UPDATE user_2fa SET lastLoginTime = ?, lastIp = ? WHERE user = ?;";


    private final SqlDatastore sqlDatastore = Locator.locate(SqlModule.class);

    public TwoFactorDb() {
        createTable();
    }

    @SneakyThrows
    private void createTable() {
        try (Connection conn = sqlDatastore.getMaritimeDatabase().getConnection()) {
            conn.prepareStatement(CREATE_TABLE).executeUpdate();
        }
    }

    @SneakyThrows
    @Override
    public void addDataIntoDb(TwoFactorData twoFactorData) {
        try (Connection conn = sqlDatastore.getMaritimeDatabase().getConnection()) {

            PreparedStatement ps = conn.prepareStatement(INSERT_DATA);
            ps.setBytes(1, UtilUuid.toBytes(twoFactorData.getUuid()));
            ps.setString(2, twoFactorData.getSecretKey());
            ps.setLong(3, twoFactorData.getLastLoginTime());
            ps.setString(4, twoFactorData.getLastIp());

            ps.executeUpdate();

        }
    }

    @SneakyThrows
    @Override
    public TwoFactorData getDataForPlayer(Player player) {
        try (Connection conn = sqlDatastore.getMaritimeDatabase().getConnection()) {

            PreparedStatement ps = conn.prepareStatement(GET_DATA);
            ps.setBytes(1, UtilUuid.toBytes(player.getUniqueId()));

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new TwoFactorData(player.getUniqueId(),
                        rs.getString("secret"),
                        rs.getLong("lastLoginTime"),
                        rs.getString("lastIp"));
            }

            return null;
        }
    }

    @SneakyThrows
    @Override
    public boolean hasDataInDb(UUID uuid) {
        try (Connection conn = sqlDatastore.getMaritimeDatabase().getConnection()) {
            PreparedStatement ps = conn.prepareStatement(HAS_DATA);
            ps.setBytes(1, UtilUuid.toBytes(uuid));

            return ps.executeQuery().next();
        }
    }

    @SneakyThrows
    @Override
    public void removeFromDb(UUID uuid) {
        try (Connection conn = sqlDatastore.getMaritimeDatabase().getConnection()) {

            PreparedStatement ps = conn.prepareStatement(REMOVE_DATA);
            ps.setBytes(1, UtilUuid.toBytes(uuid));

            ps.executeUpdate();

        }
    }

    @SneakyThrows
    @Override
    public void setLoginData(Player player, long lastLoginTime, String lastIp) {
        try (Connection conn = sqlDatastore.getMaritimeDatabase().getConnection()) {
            PreparedStatement ps = conn.prepareStatement(UPDATE_LOGIN_DATA);
            ps.setLong(1, lastLoginTime);
            ps.setString(2, lastIp);
            ps.setBytes(3, UtilUuid.toBytes(player.getUniqueId()));

            ps.executeUpdate();

        }
    }
}
