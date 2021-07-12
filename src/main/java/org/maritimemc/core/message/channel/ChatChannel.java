package org.maritimemc.core.message.channel;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.maritimemc.core.Formatter;
import org.maritimemc.core.perm.PermissionManager;
import org.maritimemc.core.profile.ProfileManager;
import org.maritimemc.core.service.Locator;
import org.maritimemc.data.perm.Permission;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
@Getter
public class ChatChannel {

    private final String name;
    private final char shortcut;
    private final boolean global;

    private Permission permission = null;

    public ChatChannel(String name, char shortcut, boolean global, Permission permission) {
        this.name = name;
        this.shortcut = shortcut;
        this.global = global;
        this.permission = permission;
    }

    public ChatColor getPlayerColor(Player player) {
        ProfileManager profileManager = Locator.locate(ProfileManager.class);
        return Formatter.toChatColor(profileManager.getCached(player).getHighestPrimaryGroup().getColour());
    }

    public String format(String playerName, ChatColor color, String message) {
        return Formatter.format("&8[&9" + name + "&8] " + color + playerName + " &8> &7" + message);
    }

    public Set<Player> getRecipients() {
        if (permission == null) {
            // Assume overridden
            return new HashSet<>();
        }

        ProfileManager profileManager = Locator.locate(ProfileManager.class);
        PermissionManager permissionManager = Locator.locate(PermissionManager.class);

        Set<Player> playerSet = new HashSet<>();

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (permissionManager.hasPermission(profileManager.getCached(onlinePlayer), permission)) {
                playerSet.add(onlinePlayer);
            }
        }

        return playerSet;
    }

}
