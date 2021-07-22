package org.maritimemc.core.punish.api.model;

import org.maritimemc.core.punish.api.PunishClient;
import org.maritimemc.core.punish.api.Punishment;
import org.maritimemc.core.punish.api.pojo.PresetReason;
import org.maritimemc.core.punish.util.SentenceUtil;

import java.util.UUID;

/**
 * Represents a base punishment manager.
 */
public interface IPunishmentManager {

    /**
     * Gets the {@link PunishClient} for a specified user.
     * If not loaded in cache, it loads from MongoDB using {@link IPunishmentManager#loadClientDb(UUID)}
     *
     * @param uuid The user to get the PunishClient for.
     * @return A PunishClient object for the specified user.
     */
    PunishClient getClientForUser(UUID uuid);

    /**
     * Loads a {@link PunishClient} for a specified user.
     *
     * @param uuid The user to generate the PunishClient from.
     * @return A PunishClient object for the specified user.
     */
    PunishClient loadClientDb(UUID uuid);

    /**
     * Creates and sends a warn based upon the parameters given.
     *
     * @param uuid            The UUID of the player being punished.
     * @param staffUuid       The UUID of the staff member punishing the player.
     * @param reason          The reason for the punishment.
     * @param offenceCategory The category of this offence.
     * @param severe          Is this offence severe?
     */
    void doWarn(UUID uuid, UUID staffUuid, String reason, Punishment.OffenceCategory offenceCategory, boolean severe);

    /**
     * Creates and sends a permanent ban based upon the parameters given.
     *
     * @param uuid            The UUID of the player being punished.
     * @param staffUuid       The UUID of the staff member punishing the player.
     * @param reason          The reason for the punishment.
     * @param offenceCategory The category of this offence.
     * @param severe          Is this offence severe?
     */
    void doPermBan(UUID uuid, UUID staffUuid, String reason, Punishment.OffenceCategory offenceCategory, boolean severe);

    /**
     * Creates and sends a temporary ban based upon the parameters given.
     *
     * @param uuid            The UUID of the player being punished.
     * @param staffUuid       The UUID of the staff member punishing the player.
     * @param reason          The reason for this punishment.
     * @param duration        The duration of the ban.
     * @param offenceCategory The category of this offence.
     * @param severe          Is this offence severe?
     */
    void doTempBan(UUID uuid, UUID staffUuid, String reason, long duration, Punishment.OffenceCategory offenceCategory, boolean severe);


    /**
     * Creates and sends a permanent report ban based upon the parameters given.
     *
     * @param uuid            The UUID of the player being punished.
     * @param staffUuid       The UUID of the staff member punishing the player.
     * @param reason          The reason for the punishment.
     * @param offenceCategory The category of this offence.
     * @param severe          Is this offence severe?
     */
    void doPermReportBan(UUID uuid, UUID staffUuid, String reason, Punishment.OffenceCategory offenceCategory, boolean severe);


    /**
     * Creates and sends a temporary report ban based upon the parameters given.
     *
     * @param uuid            The UUID of the player being punished.
     * @param staffUuid       The UUID of the staff member punishing the player.
     * @param reason          The reason for this punishment.
     * @param duration        The duration of the ban.
     * @param offenceCategory The category of this offence.
     * @param severe          Is this offence severe?
     */
    void doTempReportBan(UUID uuid, UUID staffUuid, String reason, long duration, Punishment.OffenceCategory offenceCategory, boolean severe);

    /**
     * Creates and sends a kick based upon the parameters given.
     *
     * @param uuid            The UUID of the player being punished.
     * @param staffUuid       The UUID of the staff member punishing the player.
     * @param reason          The reason for the punishment.
     * @param offenceCategory The category of this offence.
     * @param severe          Is this offence severe?
     */
    void doKick(UUID uuid, UUID staffUuid, String reason, Punishment.OffenceCategory offenceCategory, boolean severe);

    /**
     * Creates and sends a permanent mute based upon the parameters given.
     *
     * @param uuid            The UUID of the player being punished.
     * @param staffUuid       The UUID of the staff member punishing the player.
     * @param reason          The reason for the punishment.
     * @param offenceCategory The category of this offence.
     * @param severe          Is this offence severe?
     */
    void doPermMute(UUID uuid, UUID staffUuid, String reason, Punishment.OffenceCategory offenceCategory, boolean severe);

    /**
     * Creates and sends a temporary mute based upon the parameters given.
     *
     * @param uuid            The UUID of the player being punished.
     * @param staffUuid       The UUID of the staff member punishing the player.
     * @param reason          The reason for this punishment.
     * @param duration        The duration of the mute.
     * @param offenceCategory The category of this offence.
     * @param severe          Is this offence severe?
     */
    void doTempMute(UUID uuid, UUID staffUuid, String reason, long duration, Punishment.OffenceCategory offenceCategory, boolean severe);


    /**
     * Punishes the specified user based on a preset reason.
     * Decides the type of punishment to use using {@link SentenceUtil}.
     * Calls a 'do' method within this class to perform the punishment.
     *
     * @param uuid         The UUID of the player being punished.
     * @param staffUuid    The UUID of the staff member punishing the player.
     * @param presetReason The reason of this preset punishment.
     * @param saveChat     Should a user's messages be saved and a ChatLog generated?
     */
    void doPunishPreset(UUID uuid, UUID staffUuid, PresetReason presetReason, boolean saveChat);
}

