package org.maritimemc.core.punish.api;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.maritimemc.abstraction.IMaterialMapper;
import org.maritimemc.core.punish.api.model.PunishmentType;
import org.maritimemc.core.punish.api.pojo.Archival;
import org.maritimemc.core.versioning.VersionHandler;

import java.util.UUID;

/**
 * A basic punishment POJO. Any fields set
 * here are saved locally in memory and should be
 * updated to database for them to be saved permanently.
 */
public class Punishment {

    @Getter @Setter
    private int id = -1;

    @Getter
    private final UUID uuid;
    @Getter
    private final UUID staffUuid;
    @Getter
    private final String reason;
    @Getter
    private final PunishmentType type;
    @Getter
    private final OffenceCategory category;
    @Getter
    private final boolean severe;
    @Getter
    private final long timePunished;
    @Getter
    private final long duration;
    @Getter
    @Setter
    private boolean seen;
    @Getter
    @Setter
    private Archival archival;

    /**
     * Class constructor
     *
     * @param id           The punishment ID.
     * @param uuid         The UUID of the player being punished.
     * @param staffUuid    The UUID of the staff member that punished the player.
     * @param reason       The reason for their punishment.
     * @param type         The {@link PunishmentType} of this punishment.
     * @param category     The {@link OffenceCategory} of this punishment. {@link OffenceCategory#OTHER} if not preset.
     * @param severe       The severity of this punishment (used for autoamtic duration handling). {@code false} if not preset.
     * @param timePunished The time the user was punished, in milliseconds.
     * @param duration     The duration of this punishment, in milliseconds. {@code -1L} if Permanent.
     * @param seen         Has the user seen/been notified of this punishment?
     * @param archival     An {@link Archival} object for this punishment. {@code null} if it has not been archived.
     */
    public Punishment(int id, UUID uuid, UUID staffUuid, String reason, PunishmentType type, OffenceCategory category, boolean severe, long timePunished, long duration, boolean seen, Archival archival) {
        this.id = id;
        this.uuid = uuid;
        this.staffUuid = staffUuid;
        this.reason = reason;
        this.type = type;
        this.category = category;
        this.severe = severe;
        this.timePunished = timePunished;
        this.duration = duration;
        this.seen = seen;
        this.archival = archival;
    }
    /**
     * Class constructor
     *
     * @param uuid         The UUID of the player being punished.
     * @param staffUuid    The UUID of the staff member that punished the player.
     * @param reason       The reason for their punishment.
     * @param type         The {@link PunishmentType} of this punishment.
     * @param category     The {@link OffenceCategory} of this punishment. {@link OffenceCategory#OTHER} if not preset.
     * @param severe       The severity of this punishment (used for autoamtic duration handling). {@code false} if not preset.
     * @param timePunished The time the user was punished, in milliseconds.
     * @param duration     The duration of this punishment, in milliseconds. {@code -1L} if Permanent.
     * @param seen         Has the user seen/been notified of this punishment?
     * @param archival     An {@link Archival} object for this punishment. {@code null} if it has not been archived.
     */
    public Punishment(UUID uuid, UUID staffUuid, String reason, PunishmentType type, OffenceCategory category, boolean severe, long timePunished, long duration, boolean seen, Archival archival) {
        this.uuid = uuid;
        this.staffUuid = staffUuid;
        this.reason = reason;
        this.type = type;
        this.category = category;
        this.severe = severe;
        this.timePunished = timePunished;
        this.duration = duration;
        this.seen = seen;
        this.archival = archival;
    }

    /**
     * Calculates whether or not this punishment is active based on archival and duration.
     *
     * @return Whether or not this punishment is currently active.
     */
    public boolean isActive() {
        if (archival != null) return false; // Archived by staff

        if (getDuration() == -1) return true; // Duration is permanent

        return System.currentTimeMillis() < getTimePunished() + getDuration(); // Expired
    }

    /**
     * Punishment categories. <strong>Different to {@link PunishmentType}</strong>
     */
    public enum OffenceCategory {
        CHAT("Chat", Material.ENCHANTED_BOOK, ChatColor.GOLD),
        GAMEPLAY("Gameplay", VersionHandler.NMS_HANDLER.getMaterialMappings().grassBlock(), ChatColor.GREEN),
        CLIENT("Client", VersionHandler.NMS_HANDLER.getMaterialMappings().sword(IMaterialMapper.ToolMaterial.WOOD), ChatColor.DARK_AQUA),
        REPORT("Report", VersionHandler.NMS_HANDLER.getMaterialMappings().book(IMaterialMapper.BookState.WRITABLE_BOOK), ChatColor.RED),
        OTHER("Other", Material.BARRIER, ChatColor.RED);

        @Getter
        private final String name;
        @Getter
        private final Material material;
        @Getter
        private final ChatColor color;

        /**
         * Enum constructor
         *
         * @param name     The user-friendly name of this category.
         * @param material The material to use as this category's icon in the 'preset' Punishment GUI.
         * @param color    The colour of this offence category to use in the 'preset' Punishment GUI..
         */
        OffenceCategory(String name, Material material, ChatColor color) {
            this.name = name;
            this.material = material;
            this.color = color;
        }
    }

}
