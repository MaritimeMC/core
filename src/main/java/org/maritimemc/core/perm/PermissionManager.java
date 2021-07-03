package org.maritimemc.core.perm;

import lombok.SneakyThrows;
import org.maritimemc.core.Module;
import org.maritimemc.data.perm.Permission;
import org.maritimemc.data.perm.PermissionGroup;
import org.maritimemc.data.perm.WrappedPermission;
import org.maritimemc.data.player.PlayerProfile;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class PermissionManager implements Module {

    private final HashMap<PermissionGroup, Set<WrappedPermission>> permissionMap;

    public PermissionManager() {
        this.permissionMap = new HashMap<>();
    }

    /**
     * Adds a permission to a PermissionGroup in memory.
     *
     * @param group       The group to add to.
     * @param permission  The permission to add.
     * @param inheritable Whether or not this permission can be inherited.
     */
    public void addPermission(PermissionGroup group, Permission permission, boolean inheritable) {
        if (!permissionMap.containsKey(group)) permissionMap.put(group, new HashSet<>());

        permissionMap.get(group).add(new WrappedPermission(permission, inheritable));
    }

    /**
     * @param profile     The client.
     * @param permission The Permission to check for.
     * @return Whether or not the client's PermissionGroups have this permission.
     */
    public boolean hasPermission(PlayerProfile profile, Permission permission) {

        boolean hasPermission = false;
        for (PermissionGroup permissionGroup : profile.getPermissionGroups()) {
            if (hasPermission(permissionGroup, permission)) {
                hasPermission = true;
                break;
            }
        }

        return hasPermission;

    }

    /**
     * Check if a permission group has a specified permission.
     * Checks inheritance status and the group's inherited groups.
     *
     * @param group      The PermissionGroups.
     * @param permission The Permission to check for.
     * @return Whether or not the PermissionGroup has the permission.
     */
    private boolean hasPermission(PermissionGroup group, Permission permission) {

        boolean hasDirect = hasPermDirect(group, permission);

        if (hasDirect) return true;
        else {

            Set<PermissionGroup> inherited = getInheritedGroups(group);

            for (PermissionGroup permissionGroup : inherited) {
                if (hasPermDirect(permissionGroup, permission)) {
                    return true;
                }
            }

            boolean inheritable = false;
            for (PermissionGroup permissionGroup : inherited) {

                inheritable = permissionMap.get(permissionGroup).stream().anyMatch((wrap) -> wrap.getPermission() == permission && wrap.isInheritable());

            }

            return inheritable;
        }
    }

    /**
     * Checks if a PermissionGroup has a specified permission *directly*, not by any inheritance.
     *
     * @param group      The PermissionGroup.
     * @param permission The Permission to check for.
     * @return
     */
    private boolean hasPermDirect(PermissionGroup group, Permission permission) {
        if (!permissionMap.containsKey(group)) permissionMap.put(group, new HashSet<>());
        return permissionMap.get(group).stream().anyMatch((perm) -> perm.getPermission() == permission);
    }

    /**
     * @param group The PermissionGroup.
     * @return A {@link Set} of PermissionGroups which the group inherits permissions from.
     */
    @SneakyThrows
    private Set<PermissionGroup> getInheritedGroups(PermissionGroup group) {

        Set<PermissionGroup> inherited = new HashSet<>();

        for (PermissionGroup permissionGroup : group.getInheritPermissionsFrom()) {
            inherited.add(permissionGroup);
            inherited.addAll(getInheritedGroups(permissionGroup));
        }

        return inherited;
    }

}
