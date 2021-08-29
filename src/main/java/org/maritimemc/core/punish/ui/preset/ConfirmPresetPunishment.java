package org.maritimemc.core.punish.ui.preset;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.maritimemc.core.Formatter;
import org.maritimemc.core.menu.api.Menu;
import org.maritimemc.core.menu.api.MenuButton;
import org.maritimemc.core.punish.Punish;
import org.maritimemc.core.punish.api.pojo.PresetReason;
import org.maritimemc.core.punish.ui.PunishPage;
import org.maritimemc.core.util.ItemBuilder;
import org.maritimemc.core.versioning.VersionHandler;

import java.util.UUID;

public class ConfirmPresetPunishment implements PunishPage {

    private final Player staff;
    private final UUID targetUuid;
    private final String formattedName;
    private final PresetReason presetReason;
    private final boolean attemptChatLog;
    private final Punish punish;

    /**
     * Class constructor
     *
     * @param staff          The staff member punishing the player.
     * @param targetUuid     The UUID of the target player.
     * @param formattedName  The formatted name of the target player.
     * @param presetReason   The PresetReason for this punishment.
     * @param attemptChatLog Should we attempt to make a ChatLog for this player?
     * @param punish         A Punish instance.
     */
    public ConfirmPresetPunishment(Player staff, UUID targetUuid, String formattedName, PresetReason presetReason, boolean attemptChatLog, Punish punish) {
        this.staff = staff;
        this.targetUuid = targetUuid;
        this.formattedName = formattedName;
        this.presetReason = presetReason;
        this.attemptChatLog = attemptChatLog;
        this.punish = punish;
    }

    @Override
    public void open() {

        Menu menu = new Menu("Confirm Punishment", 3);

        menu.registerButton(

                new MenuButton(
                        new ItemBuilder(VersionHandler.NMS_HANDLER.getPlayerHeadItem())
                                .displayName("&d&lPunish: &r&d" + formattedName)
                                .lore("&7Confirm punishment for", "&7the player.")
                                .skullOwner(formattedName)
                                .build()
                ), 4
        );

        menu.registerButton(
                new MenuButton(
                        new ItemBuilder(VersionHandler.NMS_HANDLER.getMaterialMappings().stainedClay())
                                .durability(5)
                                .displayName("&a&lConfirm Punishment")
                                .lore("&7Confirm that you would like to", "&7execute this punishment.", " ", "&7Name: &a" + formattedName, "&7Reason: &a" + presetReason.getName())
                                .build()
                ).setWhenClicked((player) -> {

                    punish.getPunishmentManager()
                            .doPunishPreset(
                                    targetUuid,
                                    staff.getUniqueId(),
                                    presetReason,
                                    attemptChatLog);

                    player.closeInventory();

                }), 12
        );

        menu.registerButton(
                new MenuButton(
                        new ItemBuilder(VersionHandler.NMS_HANDLER.getMaterialMappings().stainedClay())
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
