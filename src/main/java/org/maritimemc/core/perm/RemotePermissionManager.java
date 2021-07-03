package org.maritimemc.core.perm;

import lombok.SneakyThrows;
import org.maritimemc.core.Module;
import org.maritimemc.core.db.SqlModule;
import org.maritimemc.core.util.UtilUuid;
import org.maritimemc.data.perm.PermissionGroup;
import org.maritimemc.db.SqlDatastore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.maritimemc.core.service.Locator.locate;

public class RemotePermissionManager implements Module {

    private final SqlDatastore sqlDatastore = locate(SqlModule.class);

    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS user_groups (uuid BINARY(16), group_entry TEXT);";
    private static final String GET_GROUPS_FOR_UUID = "SELECT * FROM user_groups WHERE uuid = ?";
    private static final String APPLY_GROUP_TO_UUID = "INSERT INTO user_groups (uuid, group_entry) VALUES (?, ?);";
    private static final String REMOVE_GROUP_FROM_UUID = "DELETE FROM user_groups WHERE uuid = ? AND group_entry = ?;";

    public RemotePermissionManager() {
        createTable();
    }

    @SneakyThrows
    public void createTable() {
        try (Connection conn = sqlDatastore.getMaritimeDatabase().getConnection()) {
            conn.prepareStatement(CREATE_TABLE).executeUpdate();
        }
    }

    @SneakyThrows
    public Set<PermissionGroup> getDirectGroups(UUID uuid) {
        try (Connection conn = sqlDatastore.getMaritimeDatabase().getConnection()) {

            PreparedStatement ps = conn.prepareStatement(GET_GROUPS_FOR_UUID);
            ps.setBytes(1, UtilUuid.toBytes(uuid));

            Set<PermissionGroup> groups = new HashSet<>();
            groups.add(PermissionGroup.MEMBER);

            ResultSet s = ps.executeQuery();
            while (s.next()) {
                String group = s.getString("group_entry");
                groups.add(PermissionGroup.valueOf(group));
            }

            return groups;
        }
    }

    @SneakyThrows
    public void applyGroupToUuid(UUID uuid, PermissionGroup group) {
        try (Connection conn = sqlDatastore.getMaritimeDatabase().getConnection()) {
            PreparedStatement ps = conn.prepareStatement(APPLY_GROUP_TO_UUID);
            ps.setBytes(1, UtilUuid.toBytes(uuid));
            ps.setString(2, group.name());

            ps.executeUpdate();
        }
    }

    @SneakyThrows
    public boolean removeGroupFromUuid(UUID uuid, PermissionGroup group) {
        try (Connection conn = sqlDatastore.getMaritimeDatabase().getConnection()) {

            PreparedStatement ps = conn.prepareStatement(REMOVE_GROUP_FROM_UUID);
            ps.setBytes(1, UtilUuid.toBytes(uuid));
            ps.setString(2, group.name());

            return ps.executeUpdate() > 0;
        }
    }
}
