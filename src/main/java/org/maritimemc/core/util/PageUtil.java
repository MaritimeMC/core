package org.maritimemc.core.util;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class PageUtil {

    /**
     * Gets all items for the given page.
     *
     * @param allItems A list of all items.
     * @param page     The page number.
     * @param spaces   The amount of spaces available for pages.
     * @return A List of applicable ItemStacks.
     */
    public static List<ItemStack> getPageItems(List<ItemStack> allItems, int page, int spaces) {
        int upperBound = page * spaces;
        int lowerBound = upperBound - spaces;

        List<ItemStack> newItems = new ArrayList<>();

        for (int i = lowerBound; i < upperBound; i++) {
            try {
                newItems.add(allItems.get(i));
            } catch (IndexOutOfBoundsException ignored) {
            }
        }

        return newItems;
    }

    /**
     * Checks whether a page number is valid based on the amount of items and
     * spaces available for each page.
     *
     * @param allItems A list of all items.
     * @param page The current page.
     * @param spaces The amount of spaces available per page.
     * @return Whether or not a page is valid based on parameters,
     */
    public static boolean isPageValid(List<ItemStack> allItems, int page, int spaces) {
        if (page <= 0) {
            return false;
        }

        int upperBound = page * spaces;
        int lowerBound = upperBound - spaces;

        return allItems.size() > lowerBound;
    }
}
