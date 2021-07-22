package org.maritimemc.core.punish.ui;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.maritimemc.core.menu.api.Menu;
import org.maritimemc.core.menu.api.MenuButton;
import org.maritimemc.core.punish.Punish;
import org.maritimemc.core.punish.api.PunishClient;
import org.maritimemc.core.punish.api.Punishment;
import org.maritimemc.core.punish.api.pojo.Archival;
import org.maritimemc.core.punish.type.TypeBan;
import org.maritimemc.core.punish.util.FormatTypeName;
import org.maritimemc.core.util.ItemBuilder;
import org.maritimemc.core.util.UtilTime;
import org.maritimemc.core.util.UuidNameFetcher;
import org.maritimemc.data.player.PlayerProfile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class PunishHistoryPage implements PunishPage {

    private final Player staff;
    private final UUID targetUuid;
    private final String formattedName;
    private final Punish punish;
    private final PunishPage backTo;

    /**
     * Class constructor
     *
     * @param staff         The staff member punishing the player.
     * @param targetUuid    The UUID of the target player.
     * @param formattedName The formatted name of the target player.
     * @param punish        A Punish instance.
     * @param backTo        The page to return to on click of the 'Go Back' button.
     */
    public PunishHistoryPage(Player staff, UUID targetUuid, String formattedName, Punish punish, PunishPage backTo) {
        this.staff = staff;
        this.targetUuid = targetUuid;
        this.formattedName = formattedName;
        this.punish = punish;
        this.backTo = backTo;
    }

    @Override
    public void open() {

        Menu menu = new Menu("Punishment History", 6);

        menu.registerButton(
                new MenuButton(
                        new ItemBuilder(Material.SKULL_ITEM)
                                .displayName("&aHistory for " + formattedName)
                                .lore("&7View punishment history for this", "&7player")
                                .skullOwner(formattedName)
                                .build()
                ), 4
        );

        if (backTo != null) {
            menu.registerButton(
                    new MenuButton(
                            new ItemBuilder(Material.OBSIDIAN)
                                    .displayName("&cGo Back")
                                    .lore("&7Click this to go back", "&7to your previous page.")
                                    .build()
                    ).setWhenClicked((player) -> backTo.open()), 49
            );
        }

        int slot = 9;

        PunishClient client = punish.getPunishmentManager().getClientForUser(targetUuid);
        for (Punishment punishment : client.getPunishments()) {

            Material material = punishment.getType().getMaterialType();
            String displayName = ChatColor.translateAlternateColorCodes('&',
                    "&a&l" + FormatTypeName.format(punishment)
            );

            String seenString;
            if (punishment.getType() instanceof TypeBan) seenString = "Attempted Login";
            else seenString = "Seen Punishment";

            String isSeen = String.valueOf(punishment.isSeen());
            String formattedBoolean = isSeen.substring(0, 1).toUpperCase() + isSeen.substring(1);

            List<String> lores = new ArrayList<>(Arrays.asList(
                    "&7Punishment Type: &e" + punishment.getType().getName(),
                    "&7Reason: &e" + punishment.getReason(),
                    " ",
                    "&7Staff: &e" + UuidNameFetcher.fetchName(punishment.getStaffUuid()),
                    " ",
                    "&7Punished At: &e" + UtilTime.formatDate(punishment.getTimePunished()),
                    "&7Duration: &e" + UtilTime.timeToString(punishment.getDuration()),
                    " ",
                    "&7" + seenString + ": &e" + formattedBoolean,
                    " ",
                    "&7ID: &e" + punishment.getId()
            ));

            PlayerProfile userClient = punish.getProfileManager().getCached(staff.getUniqueId());

            boolean staffCanArchive = punish.getPermissionManager().hasPermission(userClient, Punish.PunishPerm.ARCHIVE_PUNISHMENTS);
            boolean staffCanDelete = punish.getPermissionManager().hasPermission(userClient, Punish.PunishPerm.DELETE_PUNISHMENTS);

            if (punishment.getArchival() != null) {
                lores.add(" ");
                lores.add("&7Archived By: &e" + UuidNameFetcher.fetchName(punishment.getArchival().getArchivedBy()));
                lores.add("&7Archived At: &e" + UtilTime.formatDate(punishment.getArchival().getArchivedAt()));

                if (staffCanDelete) {
                    lores.add(" ");
                    lores.add("&cClick to fully delete.");
                }
            } else if (staffCanArchive) {
                lores.add(" ");
                lores.add("&cClick to archive.");
            }

            ItemBuilder itemBuilder = new ItemBuilder(material)
                    .displayName(displayName)
                    .lore(lores);

            if (punishment.isActive()) itemBuilder.glow();

            menu.registerButton(
                    new MenuButton(
                            itemBuilder.build()
                    )
                            .setWhenClicked((player) -> {
                                if (punishment.getArchival() == null) {
                                    if (staffCanArchive) {
                                        client.setArchival(punishment, new Archival(staff.getUniqueId(), System.currentTimeMillis()));
                                        open();
                                    }

                                } else {
                                    if (staffCanDelete) {
                                        client.deletePunishment(punishment);
                                        open();
                                    }
                                }
                            }), slot
            );

            slot++;

        }

        menu.open(staff);

    }
}
