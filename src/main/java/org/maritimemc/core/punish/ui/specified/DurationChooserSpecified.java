package org.maritimemc.core.punish.ui.specified;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.maritimemc.abstraction.IMaterialMapper;
import org.maritimemc.core.menu.api.Menu;
import org.maritimemc.core.menu.api.MenuButton;
import org.maritimemc.core.punish.Punish;
import org.maritimemc.core.punish.api.model.PunishmentType;
import org.maritimemc.core.punish.ui.PunishPage;
import org.maritimemc.core.util.ItemBuilder;
import org.maritimemc.core.versioning.VersionHandler;

import java.util.UUID;

public class DurationChooserSpecified implements PunishPage {

    private final Player staff;
    private final UUID targetUuid;
    private final String formattedName;
    private final String reason;
    private final PunishmentType punishmentType;
    private final PunishPage backTo;
    private final Punish punish;

    /**
     * Class constructor
     *
     * @param staff          The staff member punishing the player.
     * @param targetUuid     The UUID of the target player.
     * @param formattedName  The formatted name of the target player.
     * @param reason         The (specified) reason for this punishment.
     * @param punishmentType The type of this punishment.
     * @param backTo         The page to return to on click of the 'Go Back' button.
     * @param punish         A Punish instance.
     */
    public DurationChooserSpecified(Player staff, UUID targetUuid, String formattedName, String reason, PunishmentType punishmentType, PunishPage backTo, Punish punish) {
        this.staff = staff;
        this.targetUuid = targetUuid;
        this.formattedName = formattedName;
        this.reason = reason;
        this.punishmentType = punishmentType;
        this.backTo = backTo;
        this.punish = punish;
    }

    @Override
    public void open() {

        Menu menu = new Menu("Choose Duration", 5);

        menu.registerButton(new MenuButton(
                new ItemBuilder(VersionHandler.NMS_HANDLER.getMaterialMappings().clock())
                        .displayName("&d&lChoose Duration")
                        .lore("&7Choose the duration of the", "&7punishment")
                        .build()
        ), 4);

        for (MenuDuration value : MenuDuration.values()) {

            menu.registerButton(new MenuButton(
                            new ItemBuilder(VersionHandler.NMS_HANDLER.getMaterialMappings().book(IMaterialMapper.BookState.EMPTY_BOOK))
                                    .displayName(value.getColor() + "&l" + value.getName())
                                    .lore("&7Punish the player for " + value.getColor() + value.getName() + "&7.")
                                    .build()
                    ).setWhenClicked((player) -> new ConfirmSpecifiedPunishment(staff, targetUuid, formattedName, reason, punishmentType, value.getDuration(), punish).open())
                    , value.getSlot());

        }

        menu.registerButton(
                new MenuButton(
                        new ItemBuilder(Material.OBSIDIAN)
                                .displayName("&cGo Back")
                                .lore("&7Click this to go back", "&7to your previous page.")
                                .build()
                ).setWhenClicked((player) -> backTo.open()), 40
        );

        menu.open(staff);

    }

    private enum MenuDuration {

        ONE_HOUR("1 Hour", ChatColor.GREEN, 1000 * 60 * 60L, 11),
        TWO_HOURS("2 Hours", ChatColor.GREEN, 1000 * 60 * 60 * 2L, 12),
        FOUR_HOURS("4 Hours", ChatColor.GREEN, 1000 * 60 * 60 * 4L, 13),
        TWELVE_HOURS("12 Hours", ChatColor.GREEN, 1000 * 60 * 60 * 12L, 14),
        ONE_DAY("1 Day", ChatColor.GOLD, 1000 * 60 * 60 * 24L, 15),
        TWO_DAYS("2 Days", ChatColor.GOLD, 1000 * 60 * 60 * 24 * 2L, 20),
        SEVEN_DAYS("7 Days", ChatColor.GOLD, 1000 * 60 * 60 * 24 * 7L, 21),
        FOURTEEN_DAYS("14 Days", ChatColor.GOLD, 1000 * 60 * 60 * 24 * 14L, 22),
        THIRTY_DAYS("30 Days", ChatColor.GOLD, 1000 * 60 * 60 * 24 * 30L, 23),
        SIXTY_DAYS("60 Days", ChatColor.RED, 1000 * 60 * 60 * 24 * 60L, 24),
        PERMANENT("Permanent", ChatColor.DARK_RED, -1L, 31);

        @Getter
        private final String name;
        @Getter
        private final ChatColor color;
        @Getter
        private final long duration;
        @Getter
        private final int slot;

        MenuDuration(String name, ChatColor color, long duration, int slot) {
            this.name = name;
            this.color = color;
            this.duration = duration;
            this.slot = slot;
        }

    }
}
