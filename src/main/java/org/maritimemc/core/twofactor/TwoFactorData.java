package org.maritimemc.core.twofactor;

import lombok.Getter;

import java.util.UUID;

/**
 * A class to hold the data of players
 * two-factor authentication states.
 */
public class TwoFactorData {

    @Getter
    private final UUID uuid;
    @Getter
    private final String secretKey;

    @Getter
    private final long lastLoginTime;
    @Getter
    private final String lastIp;

    /**
     * Class constructor
     *
     * @param uuid          The UUID of the player.
     * @param secretKey     The secret key of the player.
     * @param lastLoginTime The last login time for the player.
     * @param lastIp        The last IP of the player.
     */
    public TwoFactorData(UUID uuid, String secretKey, long lastLoginTime, String lastIp) {
        this.uuid = uuid;
        this.secretKey = secretKey;
        this.lastLoginTime = lastLoginTime;
        this.lastIp = lastIp;
    }
}
