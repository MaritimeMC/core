package org.maritimemc.core.punish.redis;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.maritimemc.core.Formatter;
import org.maritimemc.core.db.messaging.format.MessageFormat;
import org.maritimemc.core.db.messaging.format.StringMessageFormat;
import org.maritimemc.core.db.messaging.listen.Callback;
import org.maritimemc.core.perm.PermissionManager;
import org.maritimemc.core.profile.ProfileManager;
import org.maritimemc.core.punish.Punish;
import org.maritimemc.core.service.Locator;

public class PunishInform implements Callback {

    private final ProfileManager profileManager = Locator.locate(ProfileManager.class);
    private final PermissionManager permissionManager = Locator.locate(PermissionManager.class);

    @Override
    public void run(MessageFormat data) {
        String s = ((StringMessageFormat)data).getString();

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (permissionManager.hasPermission(profileManager.getCached(onlinePlayer), Punish.PunishPerm.VIEW_PUNISHMENT_NOTIFICATIONS)) {
                onlinePlayer.sendMessage(Formatter.format("Punish", s));
            }
        }
    }
}
