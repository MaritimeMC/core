package org.maritimemc.core.punish.type;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.maritimemc.core.db.messaging.format.StringMessageFormat;
import org.maritimemc.core.punish.Punish;
import org.maritimemc.core.punish.api.PunishClient;
import org.maritimemc.core.punish.api.Punishment;
import org.maritimemc.core.punish.api.model.KickingType;
import org.maritimemc.core.punish.api.model.PunishmentType;
import org.maritimemc.core.punish.redis.KickPlayer;
import org.maritimemc.core.util.UuidNameFetcher;

public class TypeKick implements PunishmentType, KickingType {

    @Override
    public String getId() {
        return "KICK";
    }

    @Override
    public String getName() {
        return "Kick";
    }

    @Override
    public boolean isAlwaysPermanent() {
        return true;
    }

    @Override
    public Material getMaterialType() {
        return Material.STONE_AXE;
    }

    @Override
    public void onPunish(Punishment punishment, PunishClient punishClient, Punish punish) {
        // No need to set seen=true - kicks are true by default.

        // Inform staff of a kick
        punish.getDatabaseMessageManager().send(Punish.PUNISH_INFORM_CHANNEL, new StringMessageFormat("&a" + UuidNameFetcher.fetchName(punishment.getUuid()) + " &7was kicked by &a" + UuidNameFetcher.fetchName(punishment.getStaffUuid()) + "&7."));

        // Send kick publish to entire network for Bukkit server to kick player.
        punish.getDatabaseMessageManager().send(Punish.KICK_PLAYER, new KickPlayer.KickPlayerFormat(punishClient.getUuid(), generateKickMessage(punishment)));
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
        return null;
    }

    @Override
    public String onChat(Punishment punishment) {
        return null;
    }

    @Override
    public String generateKickMessage(Punishment punishment) {
        return String.join("\n" + ChatColor.GRAY,
                "§8(§dMinedroid Network§8)",
                " ",
                "§cYou have been kicked from this server.",
                " ",
                "Reason: §f" + punishment.getReason(),
                "Staff Member: §f" + UuidNameFetcher.fetchName(punishment.getStaffUuid()),
                " ",
                "ID: §f" + punishment.getId(),
                " ",
                "§bUnfairly punished? §7Appeal on our forums.");
    }
}
