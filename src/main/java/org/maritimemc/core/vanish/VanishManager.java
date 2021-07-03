package org.maritimemc.core.vanish;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.maritimemc.core.Module;
import org.maritimemc.core.command.CommandCenter;
import org.maritimemc.core.perm.PermissionManager;
import org.maritimemc.core.profile.ProfileManager;
import org.maritimemc.core.vanish.command.CommandVanish;
import org.maritimemc.core.vanish.command.CommandVanishForce;
import org.maritimemc.core.vanish.event.VanishShowPlayerEvent;
import org.maritimemc.data.perm.Permission;
import org.maritimemc.data.perm.PermissionGroup;
import org.maritimemc.data.player.PlayerProfile;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.maritimemc.core.service.Locator.locate;

public class VanishManager implements Module {

    private final PermissionManager permissionManager = locate(PermissionManager.class);
    private final ProfileManager profileManager = locate(ProfileManager.class);
    private final CommandCenter commandCenter = locate(CommandCenter.class);

    private final VanishDataManager vanishDataManager;

    private final Set<UUID> localVanishedPlayers;

    public VanishManager() {
        this.vanishDataManager = new VanishDataManager();
        this.localVanishedPlayers = new HashSet<>();

        permissionManager.addPermission(PermissionGroup.HELPER, VanishPerm.USE_VANISH, true);
        permissionManager.addPermission(PermissionGroup.ADMINISTRATOR, VanishPerm.VANISH_ADMIN, true);

        commandCenter.register(new CommandVanish("vanish"), new CommandVanishForce("vanishforce"));

        Module.registerEvents(this);
    }

    public enum VanishPerm implements Permission {
        USE_VANISH,
        VANISH_ADMIN;
    }

    private boolean canSeeThroughVanish(Player viewer, Player vanished) {
        PermissionGroup viewerGroup = profileManager.getCached(viewer).getHighestPrimaryGroup();
        PermissionGroup vanishedGroup = profileManager.getCached(vanished).getHighestPrimaryGroup();

        return viewerGroup.getPrefixLevel() > vanishedGroup.getPrefixLevel();
    }

    public boolean canUseVanish(Player player) {
        return permissionManager.hasPermission(profileManager.getCached(player), VanishPerm.USE_VANISH);
    }

    public boolean isVanished(PlayerProfile playerProfile) {
        return isVanished(Bukkit.getPlayer(playerProfile.getUuid()));
    }

    public boolean isVanished(Player player) {
        return localVanishedPlayers.contains(player.getUniqueId());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void join(PlayerJoinEvent event) {

        Player player = event.getPlayer();

        if (vanishDataManager.getStatus(player.getUniqueId())) {

            if (!canUseVanish(player)) {
                disableVanish(player, "You have lost the permission to be vanished");
            } else {
                localVanishedPlayers.add(player.getUniqueId());

                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    if (!canSeeThroughVanish(onlinePlayer, player)) {
                        onlinePlayer.hidePlayer(player);
                    }
                }

                player.sendMessage(" ");
                player.sendMessage(" &3&lYou are now vanished. &7(Carried from last login)");
                player.sendMessage(" ");
            }

        }

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer != player && isVanished(onlinePlayer)) {
                if (!canSeeThroughVanish(player, onlinePlayer)) {
                    event.getPlayer().hidePlayer(onlinePlayer);
                }
            }
        }

    }

    @EventHandler
    public void quit(PlayerQuitEvent event) {
        localVanishedPlayers.remove(event.getPlayer().getUniqueId());
    }

    public void disableVanish(Player player, String reason) {
        localVanishedPlayers.remove(player.getUniqueId());

        player.sendMessage(" ");
        player.sendMessage(" &3&lYou are no longer vanished." + ((!reason.equals("") ? " &7(" + reason + ")" : "")));
        player.sendMessage(" ");

        VanishShowPlayerEvent event = new VanishShowPlayerEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(event);

        if (!event.isCancelled()) {

            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                onlinePlayer.showPlayer(player);
            }

        }

        vanishDataManager.setStatus(player.getUniqueId(), false);
    }

    public void enableVanish(Player player, String reason) {
        localVanishedPlayers.add(player.getUniqueId());

        player.sendMessage(" ");
        player.sendMessage(" &3&lYou are now vanished." + ((!reason.equals("") ? " &7(" + reason + ")" : "")));
        player.sendMessage(" ");

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.hidePlayer(player);
        }

        vanishDataManager.setStatus(player.getUniqueId(), true);
    }

    public void enableVanish(Player player) {
        enableVanish(player, "");
    }

    public void disableVanish(Player player) {
        disableVanish(player, "");
    }
}
