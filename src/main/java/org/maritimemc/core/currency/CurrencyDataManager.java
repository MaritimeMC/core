package org.maritimemc.core.currency;

import lombok.SneakyThrows;
import org.maritimemc.core.db.SqlModule;
import org.maritimemc.core.service.Locator;
import org.maritimemc.core.util.UtilUuid;
import org.maritimemc.db.SqlDatastore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;

public class CurrencyDataManager {

    private static final String CREATE_LOCAL_TABLE = "CREATE TABLE IF NOT EXISTS currency_local (uuid BINARY(16), currency TEXT, gameId INT, amount INT)";
    private static final String CREATE_GLOBAL_TABLE = "CREATE TABLE IF NOT EXISTS currency_global (uuid BINARY(16), currency TEXT, amount INT);";
    private static final String GET_USER_CURRENCY_LOCAL = "SELECT amount FROM currency_local WHERE uuid = ? AND currency = ? AND gameId = ?;";
    private static final String GET_USER_CURRENCY_GLOBAL = "SELECT amount FROM currency_global WHERE uuid = ? AND currency = ?;";
    private static final String INSERT_USER_CURRENCY_LOCAL = "INSERT INTO currency_local (uuid, currency, gameId, amount) VALUES (?, ?, ?, ?);";
    private static final String INSERT_USER_CURRENCY_GLOBAL = "INSERT INTO currency_global (uuid, currency, amount) VALUES (?, ?, ?);";
    private static final String UPDATE_USER_CURRENCY_LOCAL = "UPDATE currency_local SET amount = ? WHERE uuid = ? AND currency = ? AND gameId = ?;";
    private static final String UPDATE_USER_CURRENCY_GLOBAL = "UPDATE currency_global SET amount = ? WHERE uuid = ? AND currency = ?;";

    private final SqlDatastore sqlDatastore = Locator.locate(SqlModule.class);

    public CurrencyDataManager() {
        createTables();
    }

    @SneakyThrows
    private void createTables() {
        try (Connection conn = sqlDatastore.getMaritimeDatabase().getConnection()) {
            conn.prepareStatement(CREATE_LOCAL_TABLE).executeUpdate();
            conn.prepareStatement(CREATE_GLOBAL_TABLE).executeUpdate();
        }
    }

    @SneakyThrows
    public void setLocalCurrency(UUID user, Currency currency, int gameId, int amount) {
        try (Connection conn = sqlDatastore.getMaritimeDatabase().getConnection()) {
            PreparedStatement ps = conn.prepareStatement(UPDATE_USER_CURRENCY_LOCAL);

            ps.setInt(1, amount);
            ps.setBytes(2, UtilUuid.toBytes(user));
            ps.setString(3, currency.getId());
            ps.setInt(4, gameId);

            if (ps.executeUpdate() <= 0) {
                PreparedStatement ins = conn.prepareStatement(INSERT_USER_CURRENCY_LOCAL);

                ins.setBytes(1, UtilUuid.toBytes(user));
                ins.setString(2, currency.getId());
                ins.setInt(3, gameId);
                ins.setInt(4, amount);

                ins.executeUpdate();
            }

        }
    }

    @SneakyThrows
    public void setGlobalCurrency(UUID user, Currency currency, int amount) {
        try (Connection conn = sqlDatastore.getMaritimeDatabase().getConnection()) {
            PreparedStatement ps = conn.prepareStatement(UPDATE_USER_CURRENCY_GLOBAL);

            ps.setInt(1, amount);
            ps.setBytes(2, UtilUuid.toBytes(user));
            ps.setString(3, currency.getId());

            if (ps.executeUpdate() <= 0) {
                PreparedStatement ins = conn.prepareStatement(INSERT_USER_CURRENCY_GLOBAL);

                ins.setBytes(1, UtilUuid.toBytes(user));
                ins.setString(2, currency.getId());
                ins.setInt(3, amount);

                ins.executeUpdate();
            }

        }
    }

    @SneakyThrows
    public int getLocalCurrency(UUID uuid, Currency currency, int gameId) {
        try (Connection conn = sqlDatastore.getMaritimeDatabase().getConnection()) {
            PreparedStatement ps = conn.prepareStatement(GET_USER_CURRENCY_LOCAL);

            ps.setBytes(1, UtilUuid.toBytes(uuid));
            ps.setString(2, currency.getId());
            ps.setInt(3, gameId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("amount");
            }

            return -1;
        }
    }

    @SneakyThrows
    public int getGlobalCurrency(UUID uuid, Currency currency) {
        try (Connection conn = sqlDatastore.getMaritimeDatabase().getConnection()) {
            PreparedStatement ps = conn.prepareStatement(GET_USER_CURRENCY_GLOBAL);

            ps.setBytes(1, UtilUuid.toBytes(uuid));
            ps.setString(2, currency.getId());

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("amount");
            }

            return -1;
        }
    }

}
