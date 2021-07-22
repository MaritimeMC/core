package org.maritimemc.core.punish.api.pojo;

import lombok.Getter;
import org.maritimemc.core.punish.api.model.PunishmentType;

/**
 * Represents a sentence for a {@link PresetReason}.
 */
@Getter
public class Sentence {

    private final PunishmentType type;
    private final long duration;

    /**
     * Class constructor
     *
     * @param type     The type of punishment for this sentence.
     * @param duration The duration of the punishment, in milliseconds.
     */
    public Sentence(PunishmentType type, long duration) {
        this.type = type;
        this.duration = duration;
    }
}
