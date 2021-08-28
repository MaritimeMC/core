package org.maritimemc.core.punish.ui.specified;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.maritimemc.core.menu.api.Menu;
import org.maritimemc.core.menu.api.MenuButton;
import org.maritimemc.core.punish.Punish;
import org.maritimemc.core.punish.api.exception.PlayerExemptException;
import org.maritimemc.core.punish.api.model.PunishmentType;
import org.maritimemc.core.punish.type.*;
import org.maritimemc.core.punish.ui.PunishHistoryPage;
import org.maritimemc.core.punish.ui.PunishPage;
import org.maritimemc.core.util.ItemBuilder;
import org.maritimemc.core.util.UuidNameFetcher;
import org.maritimemc.core.versioning.VersionHandler;
import org.maritimemc.data.player.PlayerProfile;

import java.util.UUID;

public class PunishGUISpecified implements PunishPage {

    private final Player staff;
    private final UUID targetUuid;
    private final String formattedName;
    private final String reason;
    private final Punish punish;

    /**
     * Class constructor
     *
     * @param staff      The staff member punishing the player.
     * @param targetName The unformatted name of the target player.
     * @param reason     The reason for this punishment.
     * @param punish     A Punish instance.
     * @throws IllegalArgumentException The player name passed was not valid.
     * @throws PlayerExemptException    The target is exempt from punishment and {@code staff} does not have the permission {@link org.maritimemc.core.punish.Punish.PunishPerm#PUNISHMENT_EXEMPT_BYPASS}.
     */
    public PunishGUISpecified(Player staff, String targetName, String reason, Punish punish) throws PlayerExemptException, IllegalArgumentException {
        this.staff = staff;
        this.reason = reason;
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

        menu.registerButton(

                new MenuButton(
                        new ItemBuilder(VersionHandler.NMS_HANDLER.getPlayerHeadItem())
                                .displayName("&d&lPunish: &r&d" + formattedName)
                                .lore("&7Use this GUI to punish", "&7the player.", " ", "&7Reason: &d" + reason)
                                .skullOwner(formattedName)
                                .build()
                ), 4
        );

        PunishmentType warn = PunishmentType.matchFromClass(TypeWarn.class),
                kick = PunishmentType.matchFromClass(TypeKick.class),
                mute = PunishmentType.matchFromClass(TypeMute.class),
                ban = PunishmentType.matchFromClass(TypeBan.class),
                reportBan = PunishmentType.matchFromClass(TypeReportBan.class);

        assert warn != null &&
                kick != null
                && mute != null
                && ban != null
                && reportBan != null;

        menu.registerButton(generateForType(warn), 10);
        menu.registerButton(generateForType(kick), 12);
        menu.registerButton(generateForType(mute), 14);
        menu.registerButton(generateForType(ban), 16);
        menu.registerButton(generateForType(reportBan), 8);

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

    private MenuButton generateForType(PunishmentType type) {

        return new MenuButton(
                new ItemBuilder(type.getMaterialType())
                        .displayName("&7Type: &c&l" + type.getName())
                        .lore("&7Click this if you wish to", "&c" + type.getName().toLowerCase() + " &7the player.")
                        .build()
        ).setWhenClicked(
                (player) -> {
                    if (type.isAlwaysPermanent()) {
                        new ConfirmSpecifiedPunishment(staff, targetUuid, formattedName, reason, type, -1, punish).open();
                    } else {
                        new DurationChooserSpecified(staff, targetUuid, formattedName, reason, type, this, punish).open();
                    }
                }
        );

    }
}
