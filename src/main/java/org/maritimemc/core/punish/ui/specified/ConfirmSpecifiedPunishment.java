package org.maritimemc.core.punish.ui.specified;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.maritimemc.core.Formatter;
import org.maritimemc.core.menu.api.Menu;
import org.maritimemc.core.menu.api.MenuButton;
import org.maritimemc.core.punish.Punish;
import org.maritimemc.core.punish.api.Punishment;
import org.maritimemc.core.punish.api.model.PunishmentType;
import org.maritimemc.core.punish.type.*;
import org.maritimemc.core.punish.ui.PunishPage;
import org.maritimemc.core.util.ItemBuilder;
import org.maritimemc.core.util.UtilTime;

import java.util.UUID;

public class ConfirmSpecifiedPunishment implements PunishPage {

    private final Player staff;
    private final UUID targetUuid;
    private final String formattedName;
    private final String reason;
    private final PunishmentType punishmentType;
    private final long duration;
    private final Punish punish;

    /**
     * Class constructor
     *
     * @param staff          The staff member punishing the player.
     * @param targetUuid     The UUID of the target player.
     * @param formattedName  The formatted name of the target player.
     * @param reason         The (specified) reason for this punishment.
     * @param punishmentType The type of this punishment.
     * @param duration       The duration of this punishment.
     * @param punish         A Punish instance
     */
    public ConfirmSpecifiedPunishment(Player staff, UUID targetUuid, String formattedName, String reason, PunishmentType punishmentType, long duration, Punish punish) {
        this.staff = staff;
        this.targetUuid = targetUuid;
        this.formattedName = formattedName;
        this.reason = reason;
        this.punishmentType = punishmentType;
        this.duration = duration;
        this.punish = punish;
    }

    @Override
    public void open() {

        Menu menu = new Menu("Confirm Punishment", 3);

        menu.registerButton(

                new MenuButton(
                        new ItemBuilder(Material.SKULL_ITEM)
                                .displayName("&d&lPunish: &r&d" + formattedName)
                                .lore("&7Confirm punishment for", "&7the player.")
                                .skullOwner(formattedName)
                                .build()
                ), 4
        );

        menu.registerButton(
                new MenuButton(
                        new ItemBuilder(Material.STAINED_CLAY)
                                .durability(5)
                                .displayName("&a&lConfirm Punishment")
                                .lore("&7Confirm that you would like to",
                                        "&7execute this punishment.",
                                        " ",
                                        "&7Name: &a" + formattedName,
                                        "&7Reason: &a" + reason,
                                        "&7Duration: &a" + UtilTime.timeToString(duration))
                                .build()
                ).setWhenClicked((player) -> {

                    if (punishmentType instanceof TypeBan) {
                        if (duration == -1)
                            punish.getPunishmentManager().doPermBan(targetUuid, staff.getUniqueId(), reason, Punishment.OffenceCategory.OTHER, false);
                        else
                            punish.getPunishmentManager().doTempBan(targetUuid, staff.getUniqueId(), reason, duration, Punishment.OffenceCategory.OTHER, false);
                    } else if (punishmentType instanceof TypeWarn) {
                        punish.getPunishmentManager().doWarn(targetUuid, staff.getUniqueId(), reason, Punishment.OffenceCategory.OTHER, false);
                    } else if (punishmentType instanceof TypeMute) {
                        if (duration == -1)
                            punish.getPunishmentManager().doPermMute(targetUuid, staff.getUniqueId(), reason, Punishment.OffenceCategory.OTHER, false);
                        else
                            punish.getPunishmentManager().doTempMute(targetUuid, staff.getUniqueId(), reason, duration, Punishment.OffenceCategory.OTHER, false);
                    } else if (punishmentType instanceof TypeKick) {
                        punish.getPunishmentManager().doKick(targetUuid, staff.getUniqueId(), reason, Punishment.OffenceCategory.OTHER, false);
                    } else if (punishmentType instanceof TypeReportBan) {
                        if (duration == -1)
                            punish.getPunishmentManager().doPermReportBan(targetUuid, staff.getUniqueId(), reason, Punishment.OffenceCategory.OTHER, false);
                        else
                            punish.getPunishmentManager().doTempReportBan(targetUuid, staff.getUniqueId(), reason, duration, Punishment.OffenceCategory.OTHER, false);
                    } else {
                        throw new UnsupportedOperationException("Punishment type not supported");
                    }

                    player.closeInventory();
                }), 12
        );

        menu.registerButton(
                new MenuButton(
                        new ItemBuilder(Material.STAINED_CLAY)
                                .durability(14)
                                .displayName("&c&lCancel Punishment")
                                .lore("&7Cancel this punishment.")
                                .build()
                ).setWhenClicked((player) -> {
                    player.closeInventory();
                    player.sendMessage(Formatter.format("Punish", "You cancelled the punishment."));
                }), 14
        );

        menu.open(staff);
    }
}
