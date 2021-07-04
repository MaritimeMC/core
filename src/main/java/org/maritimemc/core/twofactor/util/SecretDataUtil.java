package org.maritimemc.core.twofactor.util;

import org.bukkit.entity.Player;

/**
 * Utility class for secret keys
 * and URLs.
 */
public class SecretDataUtil {

    /**
     * Gets the URL of a QR code for a player
     * based on their secret.
     *
     * @param player The player.
     * @param secret The secret key for the player.
     * @return A String URL.
     */
    public static String getUrl(Player player, String secret) {
        StringBuilder sb = new StringBuilder(128);
        sb.append("https://chart.googleapis.com/chart?chs=128x128&cht=qr&chl=200x200&chld=M|0&cht=qr&chl=");
        addOtpAuth(sb, player, secret);

        return sb.toString();
    }

    private static void addOtpAuth(StringBuilder sb, Player player, String secret) {
        sb.append(String.format("otpauth://totp/%s?secret=%s&issuer=%s", "MaritimeMC:" + player.getName(), secret, "MaritimeMC"));
    }
}
