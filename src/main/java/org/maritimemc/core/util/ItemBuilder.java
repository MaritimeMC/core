package org.maritimemc.core.util;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.maritimemc.core.versioning.VersionHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Item builder utility class, used to
 * generate an {@link ItemStack}.
 */
public class ItemBuilder {

    private final ItemStack wrappedStack;
    private final ItemMeta wrappedMeta;

    /**
     * Class constructor
     *
     * @param material The material to use for this item.
     */
    public ItemBuilder(Material material) {
        wrappedStack = new ItemStack(material);
        wrappedMeta = wrappedStack.getItemMeta();
    }

    /**
     * Sets the amount of the item.
     *
     * @param amount The amount to use.
     */
    public ItemBuilder amount(int amount) {
        wrappedStack.setAmount(amount);
        return this;
    }

    /**
     * Sets the durability of this item.
     *
     * @param durability The durability.
     */
    public ItemBuilder durability(int durability) {
        wrappedStack.setDurability((short) durability);
        return this;
    }

    /**
     * Sets and formats the display-name of this item.
     *
     * @param name The name to use.
     */
    public ItemBuilder displayName(String name) {
        wrappedMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        return this;
    }

    /**
     * Sets the lore of this item. Lores are formatted before adding.
     *
     * @param lores An array of lores, representing each line.
     */
    public ItemBuilder lore(String... lores) {
        List<String> loresList = new ArrayList<>();
        for (String lore : lores) {
            loresList.add(ChatColor.translateAlternateColorCodes('&', lore));
        }

        wrappedMeta.setLore(loresList);
        return this;
    }

    /**
     * Append to the current lore.
     *
     * @param lores The lores to append.
     */
    public ItemBuilder addToLore(String... lores) {
        List<String> loresList = wrappedMeta.getLore();
        for (String lore : lores) {
            loresList.add(ChatColor.translateAlternateColorCodes('&', lore));
        }

        wrappedMeta.setLore(loresList);
        return this;
    }

    /**
     * Glows if a boolean returns true.
     *
     * @param b The boolean to query.
     */
    public ItemBuilder glowIf(boolean b) {
        if (b) glow();
        return this;
    }

    /**
     * Sets the lore of this item. Lores are formatted before adding.
     *
     * @param lores A List of lores, each index corresponding to each line.
     */
    public ItemBuilder lore(List<String> lores) {
        List<String> loresList = new ArrayList<>();
        for (String lore : lores) {
            loresList.add(ChatColor.translateAlternateColorCodes('&', lore));
        }
        wrappedMeta.setLore(loresList);
        return this;
    }

    /**
     * Makes the item glow; adds an enchantment and adds the flag HIDE_ENCHANTS.
     */
    public ItemBuilder glow() {
        wrappedMeta.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
        wrappedMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        return this;
    }

    /**
     * Enchants this item based upon the specified parameters.
     *
     * @param enchantment The enchantment to use.
     * @param level       The level of this enchantment.
     * @param safe        Should the enchantment follow Minecraft safety guidelines?
     */
    public ItemBuilder enchant(Enchantment enchantment, int level, boolean safe) {
        wrappedMeta.addEnchant(enchantment, level, !safe);
        return this;
    }

    public ItemBuilder skullOwner(String name) {
        if (wrappedMeta instanceof SkullMeta) {
            if (VersionHandler.NMS_HANDLER.usesModernSkulls()) {
                VersionHandler.NMS_HANDLER.setSkullOwner((SkullMeta) wrappedMeta, String.valueOf(UuidNameFetcher.fetchUuid(name)));
            } else {
                durability(3);
                VersionHandler.NMS_HANDLER.setSkullOwner((SkullMeta) wrappedMeta, name);
            }
        } else {
            throw new UnsupportedOperationException("Cannot set skull owner on a non-skull material.");
        }
        return this;
    }

    /**
     * Builds the item based upon the object.
     *
     * @return An ItemStack from this object.
     */
    public ItemStack build() {
        wrappedStack.setItemMeta(wrappedMeta);
        return wrappedStack;
    }

}
