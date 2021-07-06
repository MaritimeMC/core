package org.maritimemc.core.sync.api;

import java.util.UUID;

/**
 * Represents a class to manage account links.
 */
public interface ILinkManager {

    /**
     * @return A randomly generated verification code.
     */
    String generateVerificationCode();

    /**
     * Inserts an entry into the Redis verification cache.
     *
     * @param uuid The UUID of the player.
     * @param code The player's verification code.
     */
    void insertIntoCache(UUID uuid, String code);

    /**
     * @param uuid The UUID of the player.
     * @return The player's linked Discord ID, or {@code null} if none.
     */
    Long getDiscordId(UUID uuid);

    /**
     * @param discordId The Discord ID of the user.
     * @return The user's linked Minecraft UUID, or {@code null} if none.
     */
    UUID getMinecraftUuid(long discordId);

    /**
     * @param uuid The UUID of the user.
     * @return Whether or not the UUID is present in the Redis verification cache.
     */
    boolean isUserInCache(UUID uuid);

    /**
     * @param uuid The UUID of the user.
     * @return Whether or not the UUID has been linked with a Discord ID.
     */
    boolean isUserLinked(UUID uuid);

    /**
     * @param discordId The Discord ID of the user.
     * @return Whether or not the Discord ID has been linked with a Minecraft UUID.
     */
    boolean isUserLinked(long discordId);

    /**
     * Unlinks a user from their Discord ID.
     *
     * @param uuid The UUID of the user.
     */
    void unlinkUser(UUID uuid);

    /**
     * Unlinks a user from a UUID.
     *
     * @param discordId The Discord ID of the user.
     */
    void unlinkUser(long discordId);

    /**
     * Forcibly links two accounts.
     *
     * @param uuid      The UUID of the user.
     * @param discordId The Discord ID of the user.
     */
    void linkUser(UUID uuid, long discordId);

}
