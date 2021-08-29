package org.maritimemc.core.announce;

import org.maritimemc.core.Module;
import org.maritimemc.core.command.CommandCenter;
import org.maritimemc.core.db.messaging.DatabaseMessageManager;
import org.maritimemc.core.db.messaging.MessageChannel;
import org.maritimemc.core.perm.PermissionManager;
import org.maritimemc.core.service.Locator;
import org.maritimemc.data.perm.Permission;
import org.maritimemc.data.perm.PermissionGroup;

public class AnnouncementModule implements Module {

    public static final MessageChannel ANNOUNCE_GLOBAL_CHANNEL = new MessageChannel("Administrate", "announceGlobal");

    private final PermissionManager permissionManager = Locator.locate(PermissionManager.class);

    public AnnouncementModule() {
        CommandCenter commandCenter = Locator.locate(CommandCenter.class);

        commandCenter.register(new AnnounceCommand("announce"));

        generatePermissions();

        DatabaseMessageManager databaseMessageManager = Locator.locate(DatabaseMessageManager.class);
        databaseMessageManager.registerStringCallback(ANNOUNCE_GLOBAL_CHANNEL, new AnnounceGlobalHandler());
    }

    public void generatePermissions() {
        permissionManager.addPermission(PermissionGroup.ADMINISTRATOR, AnnouncePerm.ANNOUNCE_COMMAND_BASE, true);
        permissionManager.addPermission(PermissionGroup.ADMINISTRATOR, AnnouncePerm.ANNOUNCE_LOCAL, true);
        permissionManager.addPermission(PermissionGroup.MANAGER, AnnouncePerm.ANNOUNCE_GLOBAL, true);
        permissionManager.addPermission(PermissionGroup.MODERATOR, AnnouncePerm.SHOUT, true);
    }

    public enum AnnouncePerm implements Permission {
        ANNOUNCE_COMMAND_BASE,
        ANNOUNCE_LOCAL,
        ANNOUNCE_GLOBAL,
        SHOUT
    }

}
