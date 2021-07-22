package org.maritimemc.core.punish.type;

import org.bukkit.Material;
import org.maritimemc.core.Formatter;
import org.maritimemc.core.db.messaging.format.StringMessageFormat;
import org.maritimemc.core.profile.ProfileManager;
import org.maritimemc.core.punish.Punish;
import org.maritimemc.core.punish.api.PunishClient;
import org.maritimemc.core.punish.api.Punishment;
import org.maritimemc.core.punish.api.model.PunishmentType;
import org.maritimemc.core.punish.redis.PunishMessagePlayer;
import org.maritimemc.core.service.Locator;
import org.maritimemc.core.util.UtilTime;
import org.maritimemc.core.util.UuidNameFetcher;

public class TypeMute implements PunishmentType {

    @Override
    public String getId() {
        return "MUTE";
    }

    @Override
    public String getName() {
        return "Mute";
    }

    @Override
    public boolean isAlwaysPermanent() {
        return false;
    }

    @Override
    public Material getMaterialType() {
        return Material.COOKED_FISH;
    }

    @Override
    public void onPunish(Punishment punishment, PunishClient punishClient, Punish punish) {
        if (punishment.getDuration() == -1) {
            // Inform staff of a permanent mute

            punish.getDatabaseMessageManager().send(Punish.PUNISH_INFORM_CHANNEL, new StringMessageFormat("&a" + UuidNameFetcher.fetchName(punishment.getUuid()) + " &7was permanently muted by &a" + UuidNameFetcher.fetchName(punishment.getStaffUuid()) + "&7."));
        } else {
            // Inform staff of a temporary mute

            punish.getDatabaseMessageManager().send(Punish.PUNISH_INFORM_CHANNEL, new StringMessageFormat("&a" + UuidNameFetcher.fetchName(punishment.getUuid()) + " &7was temporarily muted by &a" + UuidNameFetcher.fetchName(punishment.getStaffUuid()) + "&7."));
        }

        if (Locator.locate(ProfileManager.class).isOnlineOnNetwork(punishment.getUuid())) {
            // Player is online, set seen true, inform player

            if (punishment.getDuration() == -1) {
                punish.getDatabaseMessageManager().send(Punish.PUNISH_MESSAGE_PLAYER, new PunishMessagePlayer.PunishMessagePlayerFormat(punishClient.getUuid(), "You were &apermanently muted &7for &a" + punishment.getReason() + "&7. (ID: " + punishment.getId() + ")"));
            } else {
                punish.getDatabaseMessageManager().send(Punish.PUNISH_MESSAGE_PLAYER, new PunishMessagePlayer.PunishMessagePlayerFormat(punishClient.getUuid(), "You were &atemporarily muted &7for &a" + punishment.getReason() + "&7. This will expire in &a" + UtilTime.timeToString(punishment.getDuration()) + "&7. (ID: " + punishment.getId() + ")"));
            }

            punishClient.setSeenTrue(punishment);
        }
    }

    @Override
    public String onLogin(Punishment punishment, PunishClient punishClient) {
        return null;
    }

    @Override
    public String onReportCreate(Punishment punishment) {
        return null;
    }

    @Override
    public String onJoin(Punishment punishment, PunishClient punishClient) {

        if (!punishment.isSeen()) {
            // Set seen true & recently muted message
            punishClient.setSeenTrue(punishment);

            if (punishment.getDuration() == -1) {
                return
                        Formatter.format("Punish", "You were recently permanently muted &7for &a" + punishment.getReason() + "&7. (ID: " + punishment.getId() + ")");
            } else {
                return
                        Formatter.format("Punish", "You were recently temporarily muted &7for &a" + punishment.getReason() + "&7. This will expire in &a" + UtilTime.timeToString((punishment.getTimePunished() + punishment.getDuration()) - System.currentTimeMillis()) + "&7. (ID: " + punishment.getId() + ")");
            }

        }

        return null;
    }

    @Override
    public String onChat(Punishment punishment) {
        if (punishment.getDuration() == -1) {
            return
                    Formatter.format("Punish", "Be quiet! You're &apermanently muted &7for &a" + punishment.getReason() + "&7. (ID: " + punishment.getId() + ")");
        } else {
            return
                    Formatter.format("Punish", "Be quiet! You're &atemporarily muted &7for &a" + punishment.getReason() + "&7. This will expire in &a" + UtilTime.timeToString((punishment.getTimePunished() + punishment.getDuration()) - System.currentTimeMillis()) + "&7. (ID: " + punishment.getId() + ")");
        }
    }
}
