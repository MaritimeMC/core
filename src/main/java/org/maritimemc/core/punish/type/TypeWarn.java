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
import org.maritimemc.core.util.UuidNameFetcher;

public class TypeWarn implements PunishmentType {

    @Override
    public String getId() {
        return "WARN";
    }

    @Override
    public String getName() {
        return "Warn";
    }

    @Override
    public boolean isAlwaysPermanent() {
        return true;
    }

    @Override
    public Material getMaterialType() {
        return Material.PAPER;
    }

    @Override
    public void onPunish(Punishment punishment, PunishClient punishClient, Punish punish) {
        // Inform staff of warn

        punish.getDatabaseMessageManager().send(Punish.PUNISH_INFORM_CHANNEL, new StringMessageFormat("&a" + UuidNameFetcher.fetchName(punishment.getUuid()) + " &7was warned by &a" + UuidNameFetcher.fetchName(punishment.getStaffUuid()) + "&7. (ID: " + punishment.getId() + ")"));

        if (Locator.locate(ProfileManager.class).isOnlineOnNetwork(punishment.getUuid())) {
            // Player is online, set seen true, inform player

            punish.getDatabaseMessageManager().send(Punish.PUNISH_MESSAGE_PLAYER, new PunishMessagePlayer.PunishMessagePlayerFormat(punishment.getUuid(), "You were &awarned &7for &a" + punishment.getReason() + "&7. (ID: " + punishment.getId() + ")"));

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
            // Set seen true & recently warned message
            punishClient.setSeenTrue(punishment);
            return Formatter.format("Punish", "You were recently warned for &a" + punishment.getReason() + "&7. (ID: " + punishment.getId() + ")");
        }

        return null;
    }

    @Override
    public String onChat(Punishment punishment) {
        return null;
    }
}
