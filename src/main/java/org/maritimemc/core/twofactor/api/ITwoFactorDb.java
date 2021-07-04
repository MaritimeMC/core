package org.maritimemc.core.twofactor.api;

import org.bukkit.entity.Player;
import org.maritimemc.core.twofactor.TwoFactorData;

import java.util.UUID;

public interface ITwoFactorDb {

    /**
     * Add a newly-setup player's 2FA data into the database.
     *
     * @param twoFactorData The TwoFactorData object containing the data of the player.
     */
    void addDataIntoDb(TwoFactorData twoFactorData);

    /**
     * Gets the data for a player from the database.
     *
     * @param player A specified player.
     * @return A TwoFactorData object containing the data of the player.
     */
    TwoFactorData getDataForPlayer(Player player);

    /**
     * @param uuid The UUID of the player in question.
     * @return Whether the player has two-factor authentication data present for them in the database.
     */
    boolean hasDataInDb(UUID uuid);

    /**
     * Remove a player's two-factor authentication data from the database.
     *
     * @param uuid The UUID of the player in question.
     */
    void removeFromDb(UUID uuid);

    /**
     * Sets new login data for a player in the database.
     *
     * @param player        The player in question.
     * @param lastLoginTime The time of the player's login.
     * @param lastIp        The IP address of the player.
     */
    void setLoginData(Player player, long lastLoginTime, String lastIp);

}
