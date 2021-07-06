package org.maritimemc.core.sync;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.maritimemc.core.Formatter;
import org.maritimemc.core.Module;
import org.maritimemc.core.command.CommandCenter;
import org.maritimemc.core.db.messaging.DatabaseMessageManager;
import org.maritimemc.core.db.messaging.MessageChannel;
import org.maritimemc.core.db.messaging.format.StringMessageFormat;
import org.maritimemc.core.perm.PermissionManager;
import org.maritimemc.core.service.Locator;
import org.maritimemc.core.sync.command.ForceLinkCommand;
import org.maritimemc.core.sync.command.LinkCommand;
import org.maritimemc.core.sync.command.UnlinkCommand;
import org.maritimemc.data.perm.Permission;
import org.maritimemc.data.perm.PermissionGroup;

import java.util.UUID;

/**
 * Discord account linking module.
 */
public class DiscordSyncModule implements Module {

    public static final MessageChannel USER_LINKED_CHANNEL = new MessageChannel("DiscordSyncModule", "userLinked");

    public static final String REDIS_LINK_CACHE_PREFIX = "link_code";

    private final PermissionManager permissionManager = Locator.locate(PermissionManager.class);

    @Getter
    private final LinkManager linkManager;

    public DiscordSyncModule() {
        linkManager = new LinkManager();

        generatePermissions();

        DatabaseMessageManager databaseMessageManager = Locator.locate(DatabaseMessageManager.class);
        databaseMessageManager.registerStringCallback(USER_LINKED_CHANNEL, (s) -> {
            StringMessageFormat st = (StringMessageFormat) s;
            UUID uuid = UUID.fromString(st.getString());

            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.sendMessage(Formatter.format("Discord", "Thank you for linking your Discord account."));
            }
        });

        Locator.locate(CommandCenter.class).register(
                new LinkCommand("link", linkManager),
                new UnlinkCommand("unlink", linkManager),
                new ForceLinkCommand("forcelink", linkManager)
        );

    }

    public void generatePermissions() {
        permissionManager.addPermission(PermissionGroup.ADMINISTRATOR, DiscordSyncPerm.UNLINK_OTHER_USERS, true);
        permissionManager.addPermission(PermissionGroup.ADMINISTRATOR, DiscordSyncPerm.FORCE_LINK, true);
    }

    public enum DiscordSyncPerm implements Permission {
        UNLINK_OTHER_USERS,
        FORCE_LINK;
    }
}
