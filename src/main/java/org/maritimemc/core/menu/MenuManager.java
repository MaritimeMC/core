package org.maritimemc.core.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.maritimemc.core.Module;
import org.maritimemc.core.menu.api.Menu;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MenuManager implements Module {

    public final Map<UUID, Menu> openMenus;

    /**
     * Class constructor
     */
    public MenuManager() {
        this.openMenus = new HashMap<>();
        Module.registerEvents(this);
    }

    @EventHandler
    public void inventoryClick(InventoryClickEvent event) {
        Menu matchedMenu = matchMenu(event.getWhoClicked().getUniqueId());

        if (matchedMenu != null) {
            // Menu found.
            matchedMenu.handleClick(event);
        }
    }

    @EventHandler
    public void inventoryClose(InventoryCloseEvent event) {
        Menu matchedMenu = matchMenu(event.getPlayer().getUniqueId());

        if (matchedMenu != null) {
            // Menu found.
            matchedMenu.handleClose((Player) event.getPlayer());
        }

        // Unregister menu - it has been closed.
        unregisterMenu(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void playerQuit(PlayerQuitEvent event) {
        Menu matchedMenu = matchMenu(event.getPlayer().getUniqueId());

        if (matchedMenu != null) {
            // Menu found.
            matchedMenu.handleClose(event.getPlayer());
        }

        // Unregister menu - the player has quit.
        unregisterMenu(event.getPlayer().getUniqueId());
    }

    /**
     * Register a menu to a user.
     *
     * @param toRegister The user.
     * @param menu       The menu.
     */
    public void registerMenu(UUID toRegister, Menu menu) {
        openMenus.put(toRegister, menu);
    }

    /**
     * Unregister a menu.
     *
     * @param toUnRegister The user's menu to unregister.
     */
    public void unregisterMenu(UUID toUnRegister) {
        openMenus.remove(toUnRegister);
    }

    /**
     * Find a menu.
     *
     * @param user The user to search for.
     * @return The Menu found, or null if it does not exist.
     */
    public Menu matchMenu(UUID user) {
        return openMenus.get(user);
    }
}