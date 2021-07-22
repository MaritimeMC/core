package org.maritimemc.core.punish.api.model;

import com.google.common.collect.Sets;
import org.bukkit.Material;
import org.maritimemc.core.punish.Punish;
import org.maritimemc.core.punish.api.PunishClient;
import org.maritimemc.core.punish.api.Punishment;
import org.maritimemc.core.punish.type.*;

import java.util.Set;
import java.util.UUID;

/**
 * Represents a type of Punishment.
 */
public interface PunishmentType {

    // Set of PunishmentTypes to be used
    Set<PunishmentType> PUNISHMENT_TYPES = Sets.newHashSet(
            new TypeWarn(),
            new TypeKick(),
            new TypeMute(),
            new TypeBan(),
            new TypeReportBan()
    );

    /**
     * Match a PunishmentType from its class.
     *
     * @param clazz The class to use.
     * @return A matched PunishmentType. {@code null} if no match was found.
     */
    static PunishmentType matchFromClass(Class<? extends PunishmentType> clazz) {

        for (PunishmentType punishmentType : PUNISHMENT_TYPES) {
            if (punishmentType.getClass() == clazz) return punishmentType;
        }

        return null;

    }

    /**
     * Match a PunishmentType from its ID.
     *
     * @param type The ID to use.
     * @return A matched PunishmentType. {@code null} if no match was found.
     */
    static PunishmentType matchFromId(String type) {

        for (PunishmentType punishmentType : PUNISHMENT_TYPES) {
            if (punishmentType.getId().equals(type)) return punishmentType;
        }

        return null;
    }

    /**
     * @return An ID. for this type of punishment within the database.
     */
    String getId();

    /**
     * @return A user-friendly name for this punishment type.
     */
    String getName();

    /**
     * @return Whether punishments for this type are ALWAYS permanent. (e.g. warn)
     */
    boolean isAlwaysPermanent();

    /**
     * @return The material to display in the Punish GUI under punishment history.
     */
    Material getMaterialType();

    /**
     * The actions to complete when a staff member punishes
     * a player using this punishment type.
     *
     * @param punishment   The punishment instance that the staff member has created.
     * @param punishClient The client of the punished user
     * @param punish       The Punish instance to use to access Redis.
     */
    void onPunish(Punishment punishment, PunishClient punishClient, Punish punish);

    /**
     * The actions to complete when a player attempts to login
     * with this punishment type linked to their client.
     * <p>
     * Only occurs if this punishment is active.
     *
     * @param punishment   The punishment instance being queried.
     * @param punishClient The client of the punished user. Doesn't access via {@link IPunishmentManager#getClientForUser(UUID)} as some clients may be offline and not be loaded.
     * @return The message to kick a user with.
     */
    String onLogin(Punishment punishment, PunishClient punishClient);

    /**
     * The actions to complete when a player attempts to
     * create a report in-game.
     * <p>
     * Only occurs if this punishment is active.
     *
     * @param punishment   The punishment instance being queried.
     * @return The message to send if a report is blocked, otherwise <code>null</code>.
     */
    String onReportCreate(Punishment punishment);

    /**
     * The actions to complete when a player joins
     * with this punishment type linked to their client.
     * <p>
     * Only occurs if this punishment is active.
     *
     * @param punishment   The punishment instance being queried.
     * @param punishClient The client of the punished user
     * @return The message to send to a user upon joining.
     */
    String onJoin(Punishment punishment, PunishClient punishClient);

    /**
     * The actions to complete when a player attempts to chat
     * with this punishment type linked to their client.
     * <p>
     * Only occurs if this punishment is active.
     *
     * @param punishment The punishment instance preventing them from chatting.
     * @return The message to send to a user, blocking them from chatting.
     */
    String onChat(Punishment punishment);
}
