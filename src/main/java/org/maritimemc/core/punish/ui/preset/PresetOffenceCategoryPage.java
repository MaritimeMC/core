package org.maritimemc.core.punish.ui.preset;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.maritimemc.core.menu.api.Menu;
import org.maritimemc.core.menu.api.MenuButton;
import org.maritimemc.core.punish.Punish;
import org.maritimemc.core.punish.api.Punishment;
import org.maritimemc.core.punish.api.pojo.PresetReason;
import org.maritimemc.core.punish.ui.PunishPage;
import org.maritimemc.core.util.ItemBuilder;
import org.maritimemc.core.util.UtilMath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class PresetOffenceCategoryPage implements PunishPage {

    private final Player staff;
    private final UUID targetUuid;
    private final String formattedName;
    private final Punish punish;
    private final Punishment.OffenceCategory category;
    private final PunishPage backTo;

    /**
     * Class constructor
     *
     * @param staff         The staff member punishing the player.
     * @param targetUuid    The UUID of the target player.
     * @param formattedName The formatted name of the target player.
     * @param punish        A Punish instance.
     * @param category      The category of this punishment.
     * @param backTo        The PunishPage to go back to on click of the 'Go Back' button.
     */
    public PresetOffenceCategoryPage(Player staff, UUID targetUuid, String formattedName, Punish punish, Punishment.OffenceCategory category, PunishPage backTo) {
        this.staff = staff;
        this.targetUuid = targetUuid;
        this.formattedName = formattedName;
        this.punish = punish;
        this.category = category;
        this.backTo = backTo;
    }

    @Override
    public void open() {

        Menu menu = new Menu(category.getName() + " Offences", 5);
        menu.registerButton(
                new MenuButton(
                        new ItemBuilder(category.getMaterial())
                                .displayName(category.getColor() + "&l" + category.getName() + " Offences")
                                .lore("&7Use this page to punish", "&7a player with a " + category.getName() + " Offence.")
                                .build()
                ), 4
        );

        menu.registerButton(
                new MenuButton(
                        new ItemBuilder(Material.OBSIDIAN)
                                .displayName("&cGo Back")
                                .lore("&7Click this to go back", "&7to your previous page.")
                                .build()
                ).setWhenClicked((player) -> backTo.open()), 40
        );

        List<Integer> slotsExclude = Arrays.asList(9, 17, 18, 26, 27, 35, 36, 45);

        int slot = 10;
        for (PresetReason value : PresetReason.values()) {

            if (value.getCategory() == category) {

                String displayName = "&" + ((value.isSevere()) ? "6" : "a") + value.getName();
                List<String> lore = new ArrayList<>();

                String description = value.getDescription();
                String[] words = description.split(" ");
                int amountOfLinesNeeded = UtilMath.divideAndRound(words.length, 4);

                for (int i = 1; i <= amountOfLinesNeeded; i++) {
                    // i is equal to LINE NUMBER

                    int endWord = i * 4;
                    int startWord = i * 4 - 4;

                    List<String> wordsOnLine = new ArrayList<>();

                    for (int wordIndex = startWord; wordIndex < endWord; wordIndex++) {

                        if (wordIndex == words.length) break;

                        wordsOnLine.add(words[wordIndex]);
                    }

                    lore.add("&7" + String.join(" ", wordsOnLine));
                }

                Material material = Material.PAPER;

                menu.registerButton(
                        new MenuButton(
                                new ItemBuilder(material)
                                        .displayName(displayName)
                                        .lore(lore)
                                        .build()
                        ).setWhenClicked((player) -> {
                            // Only chat log when it's a chat-based punishment
                            new ConfirmPresetPunishment(staff, targetUuid, formattedName, value, value.getCategory() == Punishment.OffenceCategory.CHAT, punish).open();
                        }),
                        slot
                );


                do {
                    slot++;
                }
                while (slotsExclude.contains(slot));

            }

        }

        menu.open(staff);

    }
}
