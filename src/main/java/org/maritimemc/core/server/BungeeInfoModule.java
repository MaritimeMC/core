package org.maritimemc.core.server;

import org.maritimemc.core.Module;
import org.maritimemc.core.command.CommandCenter;
import org.maritimemc.core.perm.PermissionManager;
import org.maritimemc.core.service.Locator;
import org.maritimemc.data.perm.Permission;
import org.maritimemc.data.perm.PermissionGroup;

public class BungeeInfoModule implements Module {

    public BungeeInfoModule() {
        PermissionManager permissionManager = Locator.locate(PermissionManager.class);
        permissionManager.addPermission(PermissionGroup.MODERATOR, BungeeInfoPermission.FIND_COMMAND, true);

        Locator.locate(CommandCenter.class)
                .register(new CommandFind("find"));
    }

    public enum BungeeInfoPermission implements Permission {
        FIND_COMMAND;
    }

}
