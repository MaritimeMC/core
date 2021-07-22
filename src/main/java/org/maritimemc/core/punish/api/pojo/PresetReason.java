package org.maritimemc.core.punish.api.pojo;

import lombok.Getter;
import org.maritimemc.core.punish.api.Punishment;

/**
 * All preset reasons to be used in the Punishment GUI.
 */
public enum PresetReason {

    EXCESSIVE_CAPS("Excessive Caps", Punishment.OffenceCategory.CHAT, "Excessive spamming of capital letters over one or multiple messages.", false),
    SPAM("Spam", Punishment.OffenceCategory.CHAT, "Saying the same/similar message multiple times or flooding chat.", false),
    CHARACTER_SPAM("Character Spam", Punishment.OffenceCategory.CHAT, "Deliberately spamming the same character an unnecessary times within a message.", false),
    CHAT_TROLLING("Chat Trolling", Punishment.OffenceCategory.CHAT, "Trolling another player within chat.", false),
    INAPPROPRIATE_LANGUAGE("Inappropriate Language", Punishment.OffenceCategory.CHAT, "Using inappropriate language within public chat.", false),
    GENERAL_RUDENESS("General Rudeness", Punishment.OffenceCategory.CHAT, "Being generally rude to other players and causing frustration.", false),
    HARASSMENT("Harassment", Punishment.OffenceCategory.CHAT, "Repeatedly harassing other players.", true),
    RACISM("Racism", Punishment.OffenceCategory.CHAT, "Targeted racism within chat", true),
    MALICIOUS_THREATS("Malicious Threats", Punishment.OffenceCategory.CHAT, "Maliciously threatening other players", true),

    ABUSIVE_BEHAVIOUR("Abusive Behaviour", Punishment.OffenceCategory.CHAT, "Being very verbally abusive to another player with the intent to upset them.", true),
    ADVERTISEMENT("Advertisement", Punishment.OffenceCategory.CHAT, "Advertising other servers or blacklisted websites within chat.", true),

    TEAM_KILLING("Team Killing", Punishment.OffenceCategory.GAMEPLAY, "Killing other members of a player's own team.", false),
    TEAM_TROLLING("Team Trolling", Punishment.OffenceCategory.GAMEPLAY, "Trolling other members of a player's own team.", false),
    CROSS_TEAMING("Cross Teaming", Punishment.OffenceCategory.GAMEPLAY, "Defying a player's team and forming a truce with other non-team players.", false),
    BUG_EXPLOITING("Bug Exploiting", Punishment.OffenceCategory.GAMEPLAY, "Knowingly exploiting gameplay bugs instead of reporting.", true),
    MAP_EXPLOITING("Map Exploiting", Punishment.OffenceCategory.GAMEPLAY, "Knowingly exploiting flaws in a map instead of reporting.", true),
    GRIEFING("Griefing (Survival Claims)", Punishment.OffenceCategory.GAMEPLAY, "Griefing another player's survival claim.", true),

    NO_SLOWDOWN("No Slowdown", Punishment.OffenceCategory.CLIENT, "The player does not slow down when performing actions such as eating.", false),
    CRITICALS("Criticals", Punishment.OffenceCategory.CLIENT, "The player always deals critical hits on other players due to client modifications.", false),
    JESUS("Jesus", Punishment.OffenceCategory.CLIENT, "Allows the player to walk on water.", false),
    SPIDER("Spider", Punishment.OffenceCategory.CLIENT, "Allows the player to walk up walls.", false),
    KILL_AURA("Kill-aura", Punishment.OffenceCategory.CLIENT, "Usage of kill-aura hacks.", true),
    REACH("Reach", Punishment.OffenceCategory.CLIENT, "The player shows non-default increased reach modifications.", true),
    FLIGHT("Flight", Punishment.OffenceCategory.CLIENT, "Non-allowed flight around the server", true),
    BUNNY_HOP("Bunny-hop (B-Hop)", Punishment.OffenceCategory.CLIENT, "The player uses bunny-hopping hacks.", true),
    SPEED("Speed", Punishment.OffenceCategory.CLIENT, "The player's walking speed increases.", true),
    ANTI_KNOCKBACK("Anti-knockback", Punishment.OffenceCategory.CLIENT, "Taking no knockback due to client modifications.", true),
    X_RAY("X-ray", Punishment.OffenceCategory.CLIENT, "Modifications which allow you to through blocks.", true),

    MINOR_REPORT_ABUSE("Minor Report System Abuse", Punishment.OffenceCategory.REPORT, "Minor abuse of the Report System. (e.g. false report)", false),
    MAJOR_REPORT_ABUSE("Report System Abuse", Punishment.OffenceCategory.REPORT, "Major abuse of the Report System. (e.g. repeated report spam, abuse towards staff members)", true);

    @Getter
    private final String name;
    @Getter
    private final Punishment.OffenceCategory category;
    @Getter
    private final String description;
    @Getter
    private final boolean severe;

    /**
     * Enum constructor
     *
     * @param name        The name of the reason, user-friendly.
     * @param category    The category of this punishment reason.
     * @param description A description of this reason, to be used as the lore in the GUI.
     * @param severe      Is this punishment classed as severe? Used for automatic duration handling.
     */
    PresetReason(String name, Punishment.OffenceCategory category, String description, boolean severe) {
        this.name = name;
        this.category = category;
        this.description = description;
        this.severe = severe;
    }


}
