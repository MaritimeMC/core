package org.maritimemc.core.punish.type;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.maritimemc.core.db.messaging.format.StringMessageFormat;
import org.maritimemc.core.profile.ProfileManager;
import org.maritimemc.core.punish.Punish;
import org.maritimemc.core.punish.api.PunishClient;
import org.maritimemc.core.punish.api.Punishment;
import org.maritimemc.core.punish.api.model.KickingType;
import org.maritimemc.core.punish.api.model.PunishmentType;
import org.maritimemc.core.punish.redis.KickPlayer;
import org.maritimemc.core.service.Locator;
import org.maritimemc.core.util.UtilTime;
import org.maritimemc.core.util.UuidNameFetcher;

public class TypeBan implements PunishmentType, KickingType {

    @Override
    public String getId() {
        return "BAN";
    }

    @Override
    public String getName() {
        return "Ban";
    }

    @Override
    public boolean isAlwaysPermanent() {
        return false;
    }

    @Override
    public Material getMaterialType() {
        return Material.BED;
    }

    @Override
    public void onPunish(Punishment punishment, PunishClient punishClient, Punish punish) {

        if (punishment.getDuration() == -1) {
            // Inform staff of a permanent mute

            punish.getDatabaseMessageManager().send(Punish.PUNISH_INFORM_CHANNEL, new StringMessageFormat("&a" + UuidNameFetcher.fetchName(punishment.getUuid()) + " &7was permanently banned by &a" + UuidNameFetcher.fetchName(punishment.getStaffUuid()) + "&7. (ID: " + punishment.getId() + ")"));
        } else {
            // Inform staff of a temporary mute

            punish.getDatabaseMessageManager().send(Punish.PUNISH_INFORM_CHANNEL, new StringMessageFormat("&a" + UuidNameFetcher.fetchName(punishment.getUuid()) + " &7was temporarily banned by &a" + UuidNameFetcher.fetchName(punishment.getStaffUuid()) + "&7. (ID: " + punishment.getId() + ")"));
        }

        if (Locator.locate(ProfileManager.class).isOnlineOnNetwork(punishment.getUuid())) {
            // Player is online, set seen true, kick (banned)

            punish.getDatabaseMessageManager().send(Punish.KICK_PLAYER, new KickPlayer.KickPlayerFormat(punishClient.getUuid(), generateKickMessage(punishment)));

            punishClient.setSeenTrue(punishment);
        }
    }

    @Override
    public String onLogin(Punishment punishment, PunishClient punishClient) {

        // Set seen true if not seen
        if (!punishment.isSeen()) punishClient.setSeenTrue(punishment);

        // Return generated kick message
        return generateKickMessage(punishment);

    }

    @Override
    public String onReportCreate(Punishment punishment) {
        return null;
    }

    @Override
    public String onJoin(Punishment punishment, PunishClient punishClient) {
        return null;
    }

    @Override
    public String onChat(Punishment punishment) {
        return null;
    }

    @Override
    public String generateKickMessage(Punishment punishment) {
        if (punishment.getDuration() == -1) {
            return String.join("\n" + ChatColor.GRAY,
                    "§8(§9MaritimeMC§8)",
                    " ",
                    "§cYou are banned.",
                    " ",
                    "Reason: §f" + punishment.getReason(),
                    "Staff Member: §f" + UuidNameFetcher.fetchName(punishment.getStaffUuid()),
                    " ",
                    "ID: §f" + punishment.getId(),
                    " ",
                    "§bUnfairly punished? §7Appeal on our forums.");
        } else {
            return String.join("\n" + ChatColor.GRAY,
                    "§8(§9MaritimeMC§8)",
                    " ",
                    "§cYou are temporarily banned.",
                    " ",
                    "Reason: §f" + punishment.getReason(),
                    "Expires: §f" + UtilTime.timeToString((punishment.getTimePunished() + punishment.getDuration()) - System.currentTimeMillis()),
                    "Staff Member: §f" + UuidNameFetcher.fetchName(punishment.getStaffUuid()),
                    " ",
                    "ID: §f" + punishment.getId(),
                    " ",
                    "§bUnfairly punished? §7Appeal on our forums.");
        }
    }
}
