package org.maritimemc.core.twofactor;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.maritimemc.core.Formatter;
import org.maritimemc.core.Module;
import org.maritimemc.core.command.CommandCenter;
import org.maritimemc.core.perm.PermissionManager;
import org.maritimemc.core.profile.ProfileManager;
import org.maritimemc.core.twofactor.command.ResetCommand;
import org.maritimemc.core.twofactor.command.SetupCommand;
import org.maritimemc.data.perm.Permission;
import org.maritimemc.data.perm.PermissionGroup;
import org.maritimemc.data.player.PlayerProfile;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.maritimemc.core.service.Locator.locate;

/**
 * Two-factor authentication module.
 */
public class TwoFactor implements Module {

    private final PermissionManager permissionManager = locate(PermissionManager.class);

    @Getter
    private final ProfileManager profileManager = locate(ProfileManager.class);

    @Getter
    private final TwoFactorDb twoFactorDb;
    @Getter
    private final TwoFactorManager twoFactorManager;

    private final Map<UUID, Location> lastLocation; // Used to store the last location of locked players.

    @Getter
    private final Map<UUID, String> ipAddressCache; // A cache of player IP addresses to add to 2FA if required.

    public TwoFactor() {
        lastLocation = new HashMap<>();
        ipAddressCache = new HashMap<>();

        generatePermisisons();

        twoFactorDb = new TwoFactorDb();
        twoFactorManager = new TwoFactorManager(this);

        locate(CommandCenter.class).register(
            new SetupCommand("2fasetup", this),
            new ResetCommand("2fareset", this)
        );

        Module.registerEvents(this);
    }

    private void generatePermisisons() {
        // Builder/Media perms not inherited by ANYONE until higher ranks, assign separately...
        permissionManager.addPermission(PermissionGroup.BUILDER, TwoFactorPerm.USE_2FA, false);
        permissionManager.addPermission(PermissionGroup.MEDIA, TwoFactorPerm.USE_2FA, false);
        // Make helper inheritable to apply to all staff members
        permissionManager.addPermission(PermissionGroup.HELPER, TwoFactorPerm.USE_2FA, true);

        permissionManager.addPermission(PermissionGroup.HELPER, TwoFactorPerm.FORCE_2FA, true);
        permissionManager.addPermission(PermissionGroup.ADMINISTRATOR, TwoFactorPerm.RESET_2FA, true);
    }

    @EventHandler
    public void join(PlayerJoinEvent event) {

        PlayerProfile profile = profileManager.getCached(event.getPlayer());

        if (permissionManager.hasPermission(profile, TwoFactorPerm.USE_2FA)) {

            if (twoFactorDb.hasDataInDb(event.getPlayer().getUniqueId())) {
                TwoFactorData data = twoFactorDb.getDataForPlayer(event.getPlayer());

                if (twoFactorManager.needsToAuthorise(System.currentTimeMillis(), ipAddressCache.get(event.getPlayer().getUniqueId()), data)) {
                    twoFactorManager.lockPlayer(event.getPlayer(), true);
                } else {
                    twoFactorManager.authorisePlayer(event.getPlayer(), "Code not required");
                }
            } else {
                if (permissionManager.hasPermission(profile, TwoFactorPerm.FORCE_2FA)) {
                    twoFactorManager.forceSetup2fa(event.getPlayer());
                } else {
                    twoFactorManager.promptSetup2fa(event.getPlayer());
                }
            }

        }

    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void chat(AsyncPlayerChatEvent event) {
        // Input code via chat & block chat from processing

        if (twoFactorManager.isPlayerLocked(event.getPlayer())) {
            event.setCancelled(true);
            twoFactorManager.playerInputCode(event.getPlayer(), event.getMessage());
        }
    }

    @EventHandler
    public void move(PlayerMoveEvent event) {
        // Block players from moving whilst locked

        if (twoFactorManager.isPlayerLocked(event.getPlayer())) {

            if (lastLocation.containsKey(event.getPlayer().getUniqueId())) {

                Location old = lastLocation.get(event.getPlayer().getUniqueId());

                double oldX = old.getX();
                double oldY = old.getY();
                double oldZ = old.getZ();

                double newX = event.getPlayer().getLocation().getX();
                double newY = event.getPlayer().getLocation().getY();
                double newZ = event.getPlayer().getLocation().getZ();

                if (Math.max(oldX, newX) - Math.min(oldX, newX) > 1 ||
                        Math.max(oldY, newY) - Math.min(oldY, newY) > 1 ||
                        Math.max(oldZ, newZ) - Math.min(oldZ, newZ) > 1) {
                    event.getPlayer().teleport(old);
                }

            } else {
                lastLocation.put(event.getPlayer().getUniqueId(), event.getPlayer().getLocation());
            }

        }
    }

    @EventHandler
    public void command(PlayerCommandPreprocessEvent event) {
        if (twoFactorManager.isPlayerLocked(event.getPlayer())) {
            event.getPlayer().sendMessage(Formatter.format("&cYou cannot execute commands whilst locked."));
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        if (twoFactorManager.isPlayerLocked(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void inventoryClick(InventoryClickEvent event) {
        if (twoFactorManager.isPlayerLocked((Player) event.getWhoClicked())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void drop(PlayerDropItemEvent event) {
        if (twoFactorManager.isPlayerLocked(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void pickup(PlayerPickupItemEvent event) {
        if (twoFactorManager.isPlayerLocked(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void login(PlayerLoginEvent event) {
        // Put IP address into cache if user is successfully logging in

        if (event.getResult() != PlayerLoginEvent.Result.KICK_OTHER) {
            ipAddressCache.put(event.getPlayer().getUniqueId(), event.getAddress().toString());
        }
    }

    @EventHandler
    public void quit(PlayerQuitEvent event) {
        twoFactorManager.restoreInventoryOfSetup(event.getPlayer());
    }

    public enum TwoFactorPerm implements Permission {
        USE_2FA,
        FORCE_2FA,
        RESET_2FA;
    }

}
