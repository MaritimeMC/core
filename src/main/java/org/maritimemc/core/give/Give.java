package org.maritimemc.core.give;

import org.maritimemc.core.Module;
import org.maritimemc.core.command.CommandCenter;
import org.maritimemc.core.give.commands.GiveCommand;
import org.maritimemc.core.perm.PermissionManager;
import org.maritimemc.core.service.Locator;
import org.maritimemc.data.perm.Permission;
import org.maritimemc.data.perm.PermissionGroup;

public class Give implements Module {

    private final PermissionManager permissionManager = Locator.locate(PermissionManager.class);

    public Give() {
        CommandCenter commandCenter = Locator.locate(CommandCenter.class);
        commandCenter.register(new GiveCommand("give"));

        generatePermissions();
    }

    public void generatePermissions() {
        permissionManager.addPermission(PermissionGroup.ADMINISTRATOR, GivePerm.USE_GIVE, true);
        permissionManager.addPermission(PermissionGroup.BUILDER, GivePerm.USE_GIVE, false);
    }

    public enum GivePerm implements Permission {
        USE_GIVE;
    }
}
