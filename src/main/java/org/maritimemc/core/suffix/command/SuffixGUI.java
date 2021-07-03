package org.maritimemc.core.suffix.command;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.maritimemc.core.Formatter;
import org.maritimemc.core.menu.api.Menu;
import org.maritimemc.core.menu.api.MenuButton;
import org.maritimemc.core.service.Locator;
import org.maritimemc.core.suffix.SuffixManager;
import org.maritimemc.core.util.ItemBuilder;
import org.maritimemc.core.util.UtilVersion;
import org.maritimemc.data.player.Suffix;

import java.util.UUID;

public class SuffixGUI {

    private final SuffixManager suffixManager = Locator.locate(SuffixManager.class);

    public void open(Player player) {

        UUID uuid = player.getUniqueId();

        Menu menu = new Menu("Activated Suffix", 3);
        menu.registerButton(
                new MenuButton(new ItemBuilder(Material.EGG)
                        .displayName("&9&lActivated Suffix")
                        .lore("&7Edit your activated suffix.")
                        .build()),
                4
        );

        boolean set = false;
        int index = 9;

        Suffix activeSuffix = suffixManager.getActiveSuffix(player.getUniqueId());
        for (Suffix suffix : suffixManager.getSuffixes(player.getUniqueId())) {

            if (activeSuffix == suffix) {
                menu.registerButton(new MenuButton(
                        new ItemBuilder(UtilVersion.getMaterial(suffix.getMaterial()))
                                .displayName(Formatter.toChatColor(suffix.getColor()) + "&l" + suffix.getName())
                                .lore("&7This is currently your active suffix. Click to disable.")
                                .glow()
                                .build())
                        .setWhenClicked((clicked) -> {
                            suffixManager.removeActiveSuffix(uuid);
                            open(clicked);
                        }), index);
            } else {
                menu.registerButton(new MenuButton(
                        new ItemBuilder(UtilVersion.getMaterial(suffix.getMaterial()))
                                .displayName(Formatter.toChatColor(suffix.getColor()) + "&l" + suffix.getName())
                                .lore("&7Make this your activated suffix.")
                                .build())
                        .setWhenClicked((clicked) -> {
                            suffixManager.setActiveSuffix(uuid, suffix);
                            open(clicked);
                        }), index);
            }
            index++;
            if (!set) set = true;
        }

        if (!set) {
            menu.registerButton(new MenuButton(
                    new ItemBuilder(Material.BARRIER)
                            .displayName("&c&lNo Suffixes Owned")
                            .lore("&7You do not own any suffixes.")
                            .glow()
                            .build()), index);
        }

        menu.open(player);

    }
}
