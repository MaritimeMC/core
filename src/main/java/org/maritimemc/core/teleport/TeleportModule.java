package org.maritimemc.core.teleport;

import org.maritimemc.core.Module;
import org.maritimemc.core.command.CommandCenter;
import org.maritimemc.core.perm.PermissionManager;
import org.maritimemc.core.service.Locator;
import org.maritimemc.data.perm.Permission;
import org.maritimemc.data.perm.PermissionGroup;

public class TeleportModule implements Module {

    public TeleportModule() {
        PermissionManager permissionManager = Locator.locate(PermissionManager.class);
        permissionManager.addPermission(PermissionGroup.HELPER, TeleportPerm.TELEPORT_COMMAND, true);

        Locator.locate(CommandCenter.class).register(new TeleportCommand("teleport"));
    }

    public enum TeleportPerm implements Permission {
        TELEPORT_COMMAND
    }
}
