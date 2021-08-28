package org.maritimemc.core.punish.ui.preset;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.maritimemc.core.menu.api.Menu;
import org.maritimemc.core.menu.api.MenuButton;
import org.maritimemc.core.punish.Punish;
import org.maritimemc.core.punish.api.Punishment;
import org.maritimemc.core.punish.api.exception.PlayerExemptException;
import org.maritimemc.core.punish.ui.PunishHistoryPage;
import org.maritimemc.core.punish.ui.PunishPage;
import org.maritimemc.core.util.ItemBuilder;
import org.maritimemc.core.util.UuidNameFetcher;
import org.maritimemc.core.versioning.VersionHandler;
import org.maritimemc.data.player.PlayerProfile;

import java.util.UUID;

public class PunishGUIPreset implements PunishPage {

    private final Player staff;
    private final UUID targetUuid;
    private final String formattedName; // The formatted name of the target.

    private final Punish punish;

    /**
     * Class constructor
     *
     * @param staff      The staff member punishing the player.
     * @param targetName The unformatted name of the target.
     * @param punish     A {@link Punish} instance.
     * @throws IllegalArgumentException The player name passed was not valid.
     * @throws PlayerExemptException    The target is exempt from punishment and {@code staff} does not have the permission {@link org.maritimemc.core.punish.Punish.PunishPerm#PUNISHMENT_EXEMPT_BYPASS}.
     */
    public PunishGUIPreset(Player staff, String targetName, Punish punish) throws IllegalArgumentException, PlayerExemptException {
        this.staff = staff;
        this.punish = punish;

        // Ensure passed player name is an *actual* name
        this.targetUuid = UuidNameFetcher.fetchUuid(targetName);
        this.formattedName = UuidNameFetcher.fetchName(targetUuid);

        // Check that the target player is allowed to be punished.
        PlayerProfile client = punish.getProfileManager().getTemporaryClient(targetUuid, targetName);
        PlayerProfile staffClient = punish.getProfileManager().getCached(staff.getUniqueId());

        if (punish.getPermissionManager().hasPermission(client, Punish.PunishPerm.EXEMPT_FROM_PUNISHMENT)) {
            if (!punish.getPermissionManager().hasPermission(staffClient, Punish.PunishPerm.PUNISHMENT_EXEMPT_BYPASS)) {
                throw new PlayerExemptException();
            }
        }
    }

    @Override
    public void open() {

        Menu menu = new Menu("Punish", 3);

        // Skull
        menu.registerButton(

                new MenuButton(
                        new ItemBuilder(VersionHandler.NMS_HANDLER.getPlayerHeadItem())
                                .displayName("&d&lPunish: &r&d" + formattedName)
                                .lore("&7Use this GUI to punish", "&7the player.")
                                .skullOwner(formattedName)
                                .build()
                ), 4
        );

        // CHAT category
        menu.registerButton(
                generateForCategory(Punishment.OffenceCategory.CHAT, new PresetOffenceCategoryPage(staff, targetUuid, formattedName, punish, Punishment.OffenceCategory.CHAT, this)),
                11
        );

        // GAMEPLAY category
        menu.registerButton(
                generateForCategory(Punishment.OffenceCategory.GAMEPLAY, new PresetOffenceCategoryPage(staff, targetUuid, formattedName, punish, Punishment.OffenceCategory.GAMEPLAY, this)),
                13
        );

        // CLIENT category
        menu.registerButton(
                generateForCategory(Punishment.OffenceCategory.CLIENT, new PresetOffenceCategoryPage(staff, targetUuid, formattedName, punish, Punishment.OffenceCategory.CLIENT, this)),
                15
        );

        // REPORT category
        menu.registerButton(
                generateForCategory(Punishment.OffenceCategory.REPORT, new PresetOffenceCategoryPage(staff, targetUuid, formattedName, punish, Punishment.OffenceCategory.REPORT, this)),
                8
        );

        // Punishment history
        menu.registerButton(

                new MenuButton(
                        new ItemBuilder(Material.BOOK)
                                .displayName("&aPunishment history")
                                .lore("&7View the player's punishment history.")
                                .build()
                ).setWhenClicked(
                        (player) -> new PunishHistoryPage(staff, targetUuid, formattedName, punish, this).open()
                ), 22
        );

        menu.open(staff);
    }

    private MenuButton generateForCategory(Punishment.OffenceCategory offenceCategory, PresetOffenceCategoryPage page) {

        return new MenuButton(
                new ItemBuilder(offenceCategory.getMaterial())
                        .displayName("&7Category: " + offenceCategory.getColor() + "&l" + offenceCategory.getName())
                        .lore(offenceCategory.getColor() + offenceCategory.getName() + " &7offences can be punished", "&7through this category.")
                        .build()
        ).setWhenClicked(
                (player) -> page.open()
        );

    }

}
