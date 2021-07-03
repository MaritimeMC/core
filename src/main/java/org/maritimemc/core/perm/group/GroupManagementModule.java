package org.maritimemc.core.perm.group;

import org.maritimemc.core.Module;
import org.maritimemc.core.command.CommandCenter;
import org.maritimemc.core.perm.PermissionManager;
import org.maritimemc.core.perm.group.command.CommandRank;
import org.maritimemc.data.perm.Permission;

import static org.maritimemc.core.service.Locator.locate;

public class GroupManagementModule implements Module {

    private final PermissionManager permissionManager = locate(PermissionManager.class);
    private final CommandCenter commandCenter = locate(CommandCenter.class);

    public GroupManagementModule() {
        commandCenter.register(new CommandRank("rank"));
    }

    public enum GroupPerm implements Permission {
        RANK_COMMAND;
    }
}
