package org.maritimemc.core.twofactor;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.maritimemc.core.Formatter;
import org.maritimemc.core.thread.ThreadPool;
import org.maritimemc.core.twofactor.map.QRMap;
import org.maritimemc.core.twofactor.util.QRImageGenerator;
import org.maritimemc.core.twofactor.util.SecretDataUtil;
import org.maritimemc.core.util.ItemBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Two-factor authentication manager.
 */
public class TwoFactorManager {

    private final TwoFactor twoFactor;

    private final GoogleAuthenticator googleAuthenticator = new GoogleAuthenticator();

    private final Map<UUID, TwoFactorData> lockedClientData;
    private final Map<UUID, ItemStack[]> setupInventory;

    /**
     * Class constructor
     *
     * @param twoFactor TwoFactor module instance.
     */
    public TwoFactorManager(TwoFactor twoFactor) {
        this.twoFactor = twoFactor;

        lockedClientData = new HashMap<>();
        setupInventory = new HashMap<>();
    }

    /**
     * Checks if a player needs to input an authentication
     * code based on the time, their current IP address, and
     * their old two-factor authentication data.
     *
     * @param currentTime The current time.
     * @param ipAddress   The player's IP address.
     * @param old         The OLD two-factor data stored in the database.
     * @return Whether or not the player needs to authorise.
     */
    public boolean needsToAuthorise(long currentTime, String ipAddress, TwoFactorData old) {
        return !(ipAddress.equals(old.getLastIp())) || currentTime - old.getLastLoginTime() >= 24 * 60 * 60 * 1000L;
    }

    /**
     * Authorises a player and unlocks them.
     *
     * @param player The player.
     * @param reason The reason for authorisation.
     */
    public void authorisePlayer(Player player, String reason) {

        ThreadPool.ASYNC_POOL.execute(() -> twoFactor.getTwoFactorDb().setLoginData(
                player,
                System.currentTimeMillis(),
                twoFactor.getIpAddressCache().get(player.getUniqueId())
        ));

        lockedClientData.remove(player.getUniqueId());
        if (setupInventory.containsKey(player.getUniqueId())) restoreInventoryOfSetup(player);

        player.sendMessage(Formatter.format("2FA", "Authorised" + (reason == null ? "" : " (" + reason + ")")));

    }

    /**
     * @param player The player
     * @return Whether or not a player is authentication-locked.
     */
    public boolean isPlayerLocked(Player player) {
        return lockedClientData.containsKey(player.getUniqueId());
    }

    public void lockPlayer(Player player, boolean message) {
        lockedClientData.put(player.getUniqueId(), twoFactor.getTwoFactorDb().getDataForPlayer(player));
        if (message)
            player.sendMessage(Formatter.format("2FA", "Please input your 2FA code in chat."));
    }

    public void playerInputCode(Player player, String code) {

        String secret = lockedClientData.get(player.getUniqueId()).getSecretKey();

        try {
            int i = Integer.parseInt(code);

            if (verify(secret, i)) {
                authorisePlayer(player, null);
            } else {
                player.sendMessage(Formatter.format("2FA", "That code is incorrect."));
            }
        } catch (NumberFormatException exception) {
            player.sendMessage(Formatter.format("2FA", "Codes must be numbers."));
        }

    }

    public void setupPlayer(Player player) {
        setupInventory.put(player.getUniqueId(), player.getInventory().getContents());
        player.getInventory().setContents(new ItemStack[]{});

        String secret = googleAuthenticator.createCredentials().getKey();
        MapRenderer renderer = new QRMap(QRImageGenerator.getImageFromUrl(SecretDataUtil.getUrl(player, secret)));

        MapView view = Bukkit.createMap(player.getWorld());
        view.getRenderers().forEach(view::removeRenderer);

        view.addRenderer(renderer);

        ItemStack map = new ItemBuilder(Material.MAP)
                .displayName("&b2FA QR Code &7(Open Me)")
                .lore("&7A map containing a QR code", "&7for 2FA setup.")
                .glow()
                .build();

        map.setDurability(view.getId());

        player.getInventory().setItem(4, map);
        player.getInventory().setHeldItemSlot(4);

        TwoFactorData twoFactorData = new TwoFactorData(player.getUniqueId(), secret, -1L, twoFactor.getIpAddressCache().get(player.getUniqueId()));

        twoFactor.getTwoFactorDb().addDataIntoDb(twoFactorData);

        player.sendMessage(Formatter.format("2FA",
                "Setup: Please scan the QR code shown on the map in your inventory in an authenticator app such as " +
                        "Google Authenticator or Authy.\n \n" +
                        "Once this is completed, please enter the code shown into chat."
        ));

        lockPlayer(player, false);
    }

    public void promptSetup2fa(Player player) {
        player.sendMessage(Formatter.format("2FA",
                "It appears you are eligible for two-factor authentication at Maritime. You may execute &a/2fasetup &7to begin."
        ));
    }

    public void forceSetup2fa(Player player) {
        player.sendMessage(Formatter.format("2FA",
                "Due to your rank, it is mandatory for you to setup two-factor authentication. Starting setup..."
        ));
        setupPlayer(player);
    }

    public void restoreInventoryOfSetup(Player player) {

        if (setupInventory.containsKey(player.getUniqueId())) {
            player.getInventory().setContents(setupInventory.get(player.getUniqueId()));
            setupInventory.remove(player.getUniqueId());
        }

    }

    public void tryResetUser(UUID uuid) {
        lockedClientData.remove(uuid);
        setupInventory.remove(uuid);

        twoFactor.getTwoFactorDb().removeFromDb(uuid);
    }

    public boolean verify(String secret, int code) {
        return googleAuthenticator.authorize(secret, code);
    }
}
