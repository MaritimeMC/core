package org.maritimemc.core.admin;

import org.maritimemc.core.Module;
import org.maritimemc.core.admin.command.*;
import org.maritimemc.core.admin.message.AnnounceGlobalHandler;
import org.maritimemc.core.admin.message.ManagementChatHandler;
import org.maritimemc.core.command.CommandCenter;
import org.maritimemc.core.db.messaging.DatabaseMessageManager;
import org.maritimemc.core.db.messaging.MessageChannel;
import org.maritimemc.core.perm.PermissionManager;
import org.maritimemc.core.service.Locator;
import org.maritimemc.data.perm.Permission;
import org.maritimemc.data.perm.PermissionGroup;

/**
 * Generic module for administration features
 */
public class Administrate implements Module {

    public static final MessageChannel MANAGEMENT_CHAT_CHANNEL = new MessageChannel("Administrate", "managementChat");
    public static final MessageChannel ANNOUNCE_GLOBAL_CHANNEL = new MessageChannel("Administrate", "announceGlobal");

    private final PermissionManager permissionManager = Locator.locate(PermissionManager.class);

    public Administrate() {
        CommandCenter commandCenter = Locator.locate(CommandCenter.class);

        commandCenter.register(
                new AnnounceCommand("announce"),
                new ManagementChat("managementchat"),
                new GamemodeCommand("gamemode"),
                new ClearCommand("clear"),
                new GiveCommand("give")
        );

        generatePermissions();

        DatabaseMessageManager databaseMessageManager = Locator.locate(DatabaseMessageManager.class);
        databaseMessageManager.register(MANAGEMENT_CHAT_CHANNEL, ManagementChatHandler.ManagementChatIncomingFormat.class, new ManagementChatHandler());
        databaseMessageManager.registerStringCallback(ANNOUNCE_GLOBAL_CHANNEL, new AnnounceGlobalHandler());
    }

    public void generatePermissions() {
        permissionManager.addPermission(PermissionGroup.ADMINISTRATOR, AdministratePerm.ANNOUNCE_COMMAND_BASE, true);
        permissionManager.addPermission(PermissionGroup.ADMINISTRATOR, AdministratePerm.ANNOUNCE_LOCAL, true);
        permissionManager.addPermission(PermissionGroup.MANAGER, AdministratePerm.ANNOUNCE_GLOBAL, true);

        permissionManager.addPermission(PermissionGroup.ADMINISTRATOR, AdministratePerm.MANAGEMENT_CHAT, true);

        permissionManager.addPermission(PermissionGroup.ADMINISTRATOR, AdministratePerm.GAMEMODE_COMMAND, true);
        permissionManager.addPermission(PermissionGroup.BUILDER, AdministratePerm.GAMEMODE_COMMAND, false);

        permissionManager.addPermission(PermissionGroup.ADMINISTRATOR, AdministratePerm.CLEAR_COMMAND, true);
        permissionManager.addPermission(PermissionGroup.BUILDER, AdministratePerm.CLEAR_COMMAND, false);

        permissionManager.addPermission(PermissionGroup.ADMINISTRATOR, AdministratePerm.GIVE_COMMAND, true);
        permissionManager.addPermission(PermissionGroup.BUILDER, AdministratePerm.GIVE_COMMAND, false);
    }

    public enum AdministratePerm implements Permission {
        ANNOUNCE_COMMAND_BASE,
        ANNOUNCE_LOCAL,
        ANNOUNCE_GLOBAL,

        MANAGEMENT_CHAT,

        GAMEMODE_COMMAND,
        CLEAR_COMMAND,
        GIVE_COMMAND
    }
}
