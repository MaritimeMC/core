package org.maritimemc.core.menu.api;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class MenuButton {

    private final ItemStack itemStack;
    private Map<ClickType, Consumer<Player>> whenClicked;

    /**
     * Class constructor
     *
     * @param itemStack The ItemStack to use for this button.
     */
    public MenuButton(ItemStack itemStack) {
        this.itemStack = itemStack;
        this.whenClicked = new HashMap<>();
    }

    /**
     * Returns the value of the whenClicked consumer.
     *
     * @return The whenClicked consumer.
     */
    public Consumer<Player> getWhenClicked() {
        return getWhenClicked(ClickType.LEFT);
    }

    public Consumer<Player> getWhenClicked(ClickType clickType) {
        return whenClicked.get(clickType);
    }

    /**
     * Sets the value of the whenClicked consumer.
     *
     * @param whenClicked The consumer to set.
     * @return Returns this object.
     */
    public MenuButton setWhenClicked(Consumer<Player> whenClicked) {
        return setWhenClicked(ClickType.LEFT, whenClicked);
    }

    public MenuButton setWhenClicked(ClickType clickType, Consumer<Player> whenClicked) {
        this.whenClicked.put(clickType, whenClicked);
        return this;
    }

    /**
     * Returns the ItemStack for this button.
     *
     * @return The itemStack supplied in the constructor.
     */
    public ItemStack getItemStack() {
        return itemStack;
    }
}
