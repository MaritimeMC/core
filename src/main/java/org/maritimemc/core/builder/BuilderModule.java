package org.maritimemc.core.builder;

import org.maritimemc.core.Module;
import org.maritimemc.core.command.CommandCenter;
import org.maritimemc.core.perm.PermissionManager;
import org.maritimemc.core.service.Locator;
import org.maritimemc.data.perm.Permission;
import org.maritimemc.data.perm.PermissionGroup;

public class BuilderModule implements Module {

    public BuilderModule() {
        PermissionManager permissionManager = Locator.locate(PermissionManager.class);

        permissionManager.addPermission(PermissionGroup.ADMINISTRATOR, BuildPerm.CLEAR_COMMAND, true);
        permissionManager.addPermission(PermissionGroup.ADMINISTRATOR, BuildPerm.GAMEMODE_COMMAND, true);

        permissionManager.addPermission(PermissionGroup.BUILDER, BuildPerm.CLEAR_COMMAND, false);
        permissionManager.addPermission(PermissionGroup.BUILDER, BuildPerm.GAMEMODE_COMMAND, false);

        Locator.locate(CommandCenter.class).register(
                new ClearCommand("clear"),
                new GamemodeCommand("gamemode")
        );
    }

    public enum BuildPerm implements Permission {
        CLEAR_COMMAND,
        GAMEMODE_COMMAND;
    }
}
