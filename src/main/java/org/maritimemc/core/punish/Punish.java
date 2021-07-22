package org.maritimemc.core.punish;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.maritimemc.core.Module;
import org.maritimemc.core.chatlog.ChatLogModule;
import org.maritimemc.core.command.CommandCenter;
import org.maritimemc.core.db.messaging.DatabaseMessageManager;
import org.maritimemc.core.db.messaging.MessageChannel;
import org.maritimemc.core.perm.PermissionManager;
import org.maritimemc.core.profile.ProfileManager;
import org.maritimemc.core.punish.api.PunishClient;
import org.maritimemc.core.punish.api.Punishment;
import org.maritimemc.core.punish.api.model.IPunishmentManager;
import org.maritimemc.core.punish.command.CommandPunish;
import org.maritimemc.core.punish.redis.KickPlayer;
import org.maritimemc.core.punish.redis.PunishInform;
import org.maritimemc.core.punish.redis.PunishMessagePlayer;
import org.maritimemc.core.reports.event.ReportCreateEvent;
import org.maritimemc.core.service.Locator;
import org.maritimemc.data.perm.Permission;
import org.maritimemc.data.perm.PermissionGroup;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Punish implements Module {

    public static final MessageChannel PUNISH_INFORM_CHANNEL = new MessageChannel("Punish", "punishInform");
    public static final MessageChannel UPDATE_PUNISH_RECORDS_CHANNEL = new MessageChannel("Punish", "updateRecords");
    public static final MessageChannel PUNISH_MESSAGE_PLAYER = new MessageChannel("Punish", "messagePlayer");
    public static final MessageChannel KICK_PLAYER = new MessageChannel("Punish", "kickPlayer");

    @Getter
    private final Map<UUID, PunishClient> punishClients;
    @Getter
    private final IPunishmentManager punishmentManager;

    @Getter
    private final PermissionManager permissionManager = Locator.locate(PermissionManager.class);
    @Getter
    private final DatabaseMessageManager databaseMessageManager = Locator.locate(DatabaseMessageManager.class);
    @Getter
    private final ProfileManager profileManager = Locator.locate(ProfileManager.class);

    @Getter
    private final ChatLogModule chatLogModule = Locator.locate(ChatLogModule.class);

    @Getter
    private final PunishDataManager punishDataManager;

    public Punish() {
        // Create instances & variables
        this.punishClients = new HashMap<>();
        this.punishmentManager = new PunishmentManager(this);
        this.punishDataManager = new PunishDataManager();

        // Module setup
        generatePermissions();
        registerCommands();
        beginSubscriptions();

        Module.registerEvents(this);
    }

    private void generatePermissions() {
        permissionManager.addPermission(PermissionGroup.HELPER, PunishPerm.VIEW_PUNISHMENT_NOTIFICATIONS, true);
        permissionManager.addPermission(PermissionGroup.HELPER, PunishPerm.PUNISH_COMMAND, true);
        permissionManager.addPermission(PermissionGroup.HELPER, PunishPerm.CHAT_LOG_COMMAND, true);
        permissionManager.addPermission(PermissionGroup.APPEALS_TEAM, PunishPerm.ARCHIVE_PUNISHMENTS, false);
        permissionManager.addPermission(PermissionGroup.ADMINISTRATOR, PunishPerm.DELETE_PUNISHMENTS, true);
        permissionManager.addPermission(PermissionGroup.HELPER, PunishPerm.EXEMPT_FROM_PUNISHMENT, true);
        permissionManager.addPermission(PermissionGroup.CEO, PunishPerm.PUNISHMENT_EXEMPT_BYPASS, false);
    }

    private void registerCommands() {

        Locator.locate(CommandCenter.class).register(
                new CommandPunish("punish", this)
        );

    }

    private void beginSubscriptions() {
        databaseMessageManager.registerStringCallback(PUNISH_INFORM_CHANNEL, new PunishInform());

        databaseMessageManager.registerSimple(UPDATE_PUNISH_RECORDS_CHANNEL, (s) -> {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (onlinePlayer.getUniqueId().toString().equals(s)) {
                    punishmentManager.loadClientDb(onlinePlayer.getUniqueId());
                }
            }
        });

        databaseMessageManager.register(PUNISH_MESSAGE_PLAYER, PunishMessagePlayer.PunishMessagePlayerFormat.class, new PunishMessagePlayer());
        databaseMessageManager.register(KICK_PLAYER, KickPlayer.KickPlayerFormat.class, new KickPlayer());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void playerChat(AsyncPlayerChatEvent event) {

        // Called FIRST before any other chat events.
        // Plugins should run chat events at EventPriority.NORMAL and ensure they
        // have not already been cancelled.

        PunishClient punishClient = punishClients.get(event.getPlayer().getUniqueId());
        if (punishClient != null) {

            // Loop through each punishment and only investigate active punishments.
            for (Punishment punishment : punishClient.getPunishments()) {

                if (punishment.isActive()) {
                    String send = punishment.getType().onChat(punishment);
                    if (send != null) { // Has the type told us to block chat?
                        event.getPlayer().sendMessage(send);
                        event.setCancelled(true);

                        /*
                            Break from the loop to ensure that multiple
                            'you may not chat' messages do not send.
                        */
                        break;
                    }
                }
            }
        }

    }

    @EventHandler
    public void playerJoin(PlayerJoinEvent event) {

        // Used to send 'recently punished' messages on join

        PunishClient punishClient = punishClients.get(event.getPlayer().getUniqueId());
        if (punishClient != null) {

            for (Punishment punishment : punishClient.getPunishments()) {
                if (punishment.isActive()) {
                    String send = punishment.getType().onJoin(punishment, punishClient);
                    if (send != null) {
                        event.getPlayer().sendMessage(send);
                    }
                }
            }
        }

    }

    @EventHandler
    public void reportCreate(ReportCreateEvent event) {
        PunishClient punishClient = punishClients.get(event.getUuid());
        if (punishClient != null) {

            // Loop through each punishment and only investigate active punishments.
            for (Punishment punishment : punishClient.getPunishments()) {

                if (punishment.isActive()) {
                    String send = punishment.getType().onReportCreate(punishment);
                    if (send != null) { // Has the type told us to block reporting?
                        Player player = Bukkit.getPlayer(event.getUuid());
                        if (player != null) player.sendMessage(send);

                        event.setCancelled(true);

                        /*
                            Break from the loop to ensure that multiple
                            messages do not send.
                        */
                        break;
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void playerLoginClientLoad(PlayerLoginEvent event) {
        // Load punish client on login

        // Runs at EventPriority.LOWEST to ensure that IPunishmentType#onLogin() can be checked
        // using the PunishClient of this user.

        UUID uuid = event.getPlayer().getUniqueId();
        punishmentManager.getClientForUser(uuid);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void playerLogin(PlayerLoginEvent event) {

        PunishClient punishClient = punishClients.get(event.getPlayer().getUniqueId());
        if (punishClient != null) {

            for (Punishment punishment : punishClient.getPunishments()) {
                if (punishment.isActive()) {
                    String kick = punishment.getType().onLogin(punishment, punishClient);
                    if (kick != null) {
                        event.disallow(PlayerLoginEvent.Result.KICK_OTHER, kick);
                        break;
                    }
                }
            }
        }

    }

    public enum PunishPerm implements Permission {
        VIEW_PUNISHMENT_NOTIFICATIONS,
        PUNISH_COMMAND,
        ARCHIVE_PUNISHMENTS,
        DELETE_PUNISHMENTS,
        CHAT_LOG_COMMAND,
        EXEMPT_FROM_PUNISHMENT,
        PUNISHMENT_EXEMPT_BYPASS
    }

}
