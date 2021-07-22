package org.maritimemc.core.punish.api.pojo;

import lombok.Getter;
import org.maritimemc.core.punish.api.Punishment;

import java.util.UUID;

/**
 * Represents the archival of a {@link Punishment}.
 */
@Getter
public class Archival {

    private final UUID archivedBy;
    private final long archivedAt;

    /**
     * Class constructor
     *
     * @param archivedBy The UUID of the staff member who archived the punishment.
     * @param archivedAt The time that the punishment was archived, in milliseconds.
     */
    public Archival(UUID archivedBy, long archivedAt) {
        this.archivedBy = archivedBy;
        this.archivedAt = archivedAt;
    }
}
