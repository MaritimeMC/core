package org.maritimemc.core.punish.api.model;

import org.maritimemc.core.punish.api.Punishment;

/**
 * Represents a {@link PunishmentType} which needs to KICK players.
 */
public interface KickingType {

    /**
     * @param punishment The punishment to generate from.
     * @return A message to kick a player with, generated based on punishment values.
     */
    String generateKickMessage(Punishment punishment);

}
