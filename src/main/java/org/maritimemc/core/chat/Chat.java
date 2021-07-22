package org.maritimemc.core.chat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.maritimemc.core.Formatter;
import org.maritimemc.core.Module;
import org.maritimemc.core.profile.ProfileManager;
import org.maritimemc.core.service.Locator;
import org.maritimemc.core.suffix.SuffixManager;
import org.maritimemc.data.player.PlayerProfile;
import org.maritimemc.data.player.Suffix;

import java.util.UUID;

public class Chat implements Module {

    private final ProfileManager profileManager = Locator.locate(ProfileManager.class);
    private final SuffixManager suffixManager = Locator.locate(SuffixManager.class);

    public Chat() {
        Module.registerEvents(this);
    }

    // Called AFTER punishment and chat channels
    @EventHandler(priority = EventPriority.HIGH)
    public void chat(AsyncPlayerChatEvent event) {

        if (event.isCancelled()) {
            return;
        }

        PlayerProfile profile = profileManager.getCached(event.getPlayer());
        if (profile == null) {
            event.getPlayer().sendMessage(Formatter.format("&cWe couldn't find your profile? Please re-log."));
            event.setCancelled(true);
            return;
        }

        Suffix activeSuffix = suffixManager.getActiveSuffix(event.getPlayer().getUniqueId());

        String message = ChatColor.stripColor(Formatter.format(event.getMessage()));

        PlayerMessage playerMessage = new PlayerMessage(event.getPlayer(), profile.getHighestPrimaryGroup(), activeSuffix, message);

        MaritimeChatEvent mce = new MaritimeChatEvent(playerMessage);
        Bukkit.getPluginManager().callEvent(mce);

        if (mce.isCancelled()) {
            return;
        }

        event.setCancelled(true);

        for (UUID recipient : mce.getRecipients()) {
            Player player = Bukkit.getPlayer(recipient);

            if (player != null) {
                player.sendMessage(mce.getChatFormatter().format(mce.getPlayerMessage()));
            }
        }
    }

}
