package org.maritimemc.core.admin.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.maritimemc.core.Formatter;
import org.maritimemc.core.admin.Administrate;
import org.maritimemc.core.db.messaging.format.MessageFormat;
import org.maritimemc.core.db.messaging.listen.Callback;
import org.maritimemc.core.perm.PermissionManager;
import org.maritimemc.core.profile.ProfileManager;

import static org.maritimemc.core.service.Locator.locate;

public class ManagementChatHandler implements Callback {

    private final PermissionManager permissionManager = locate(PermissionManager.class);
    private final ProfileManager profileManager = locate(ProfileManager.class);

    @Override
    public void run(MessageFormat data) {
        ManagementChatIncomingFormat f = (ManagementChatIncomingFormat) data;

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (permissionManager.hasPermission(profileManager.getCached(player), Administrate.AdministratePerm.MANAGEMENT_CHAT)) {
                player.sendMessage(Formatter.format("&8[&4Management Chat&8] &6" + f.getName() + " in " + f.getServer() + "  &8> &7" + f.getMessage()));
            }
        }
    }

    @Getter
    @AllArgsConstructor
    public static class ManagementChatIncomingFormat implements MessageFormat {
        private final String server;
        private final String name;
        private final String message;
    }
}
