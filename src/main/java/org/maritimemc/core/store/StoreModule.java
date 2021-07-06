package org.maritimemc.core.store;

import org.maritimemc.core.Module;
import org.maritimemc.core.command.CommandCenter;
import org.maritimemc.core.db.messaging.DatabaseMessageManager;
import org.maritimemc.core.db.messaging.MessageChannel;
import org.maritimemc.core.perm.PermissionManager;
import org.maritimemc.core.service.Locator;
import org.maritimemc.core.store.command.StoreAnnounceCommand;
import org.maritimemc.core.store.message.StoreAnnounceHandler;
import org.maritimemc.data.perm.Permission;
import org.maritimemc.data.perm.PermissionGroup;

public class StoreModule implements Module {

    public static final MessageChannel STORE_ANNOUNCE_CHANNEL = new MessageChannel("StoreModule", "storeAnnounce");

    public StoreModule() {

        Locator.locate(CommandCenter.class).register(
                new StoreAnnounceCommand("storeannounce")
        );

        PermissionManager permissionManager = Locator.locate(PermissionManager.class);
        permissionManager.addPermission(PermissionGroup.MANAGER, StoreModulePerm.STORE_ANNOUNCE_COMMAND, true);

        DatabaseMessageManager databaseMessageManager = Locator.locate(DatabaseMessageManager.class);
        databaseMessageManager.register(STORE_ANNOUNCE_CHANNEL, StoreAnnounceHandler.StoreAnnounceFormat.class, new StoreAnnounceHandler());
    }

    public enum StoreModulePerm implements Permission {
        STORE_ANNOUNCE_COMMAND
    }
}
