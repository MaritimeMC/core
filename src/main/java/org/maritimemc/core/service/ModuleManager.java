package org.maritimemc.core.service;

import org.maritimemc.core.command.CommandCenter;
import org.maritimemc.core.db.RedisModule;
import org.maritimemc.core.db.SqlModule;
import org.maritimemc.core.db.messaging.DatabaseMessageManager;
import org.maritimemc.core.menu.MenuManager;
import org.maritimemc.core.perm.PermissionManager;
import org.maritimemc.core.perm.RemotePermissionManager;
import org.maritimemc.core.perm.group.GroupManagementModule;
import org.maritimemc.core.profile.ProfileManager;
import org.maritimemc.core.server.ServerDataManager;
import org.maritimemc.core.suffix.SuffixManager;
import org.maritimemc.core.vanish.VanishManager;

import static org.maritimemc.core.service.Locator.locate;

public class ModuleManager {

    public static void loadRequiredModules() {
        locate(MenuManager.class);

        locate(SqlModule.class);
        locate(RedisModule.class);
        locate(DatabaseMessageManager.class).listen();

        locate(RemotePermissionManager.class);
        locate(PermissionManager.class);
        locate(ServerDataManager.class);

        locate(ProfileManager.class);
        locate(CommandCenter.class);

        locate(GroupManagementModule.class);
        locate(SuffixManager.class);
        locate(VanishManager.class);
    }
}
