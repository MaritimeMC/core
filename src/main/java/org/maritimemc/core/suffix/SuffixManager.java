package org.maritimemc.core.suffix;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerLoginEvent;
import org.maritimemc.core.Module;
import org.maritimemc.core.command.CommandCenter;
import org.maritimemc.core.db.messaging.DatabaseMessageManager;
import org.maritimemc.core.db.messaging.MessageChannel;
import org.maritimemc.core.service.Locator;
import org.maritimemc.core.suffix.command.CommandSuffix;
import org.maritimemc.core.suffix.command.CommandSuffixManage;
import org.maritimemc.core.thread.ThreadPool;
import org.maritimemc.data.perm.Permission;
import org.maritimemc.data.player.Suffix;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class SuffixManager implements Module {

    public static final MessageChannel SUFFIX_RELOAD_CHANNEL = new MessageChannel("SuffixManager", "suffixReload");

    private final SuffixDataManager suffixDataManager;

    private final CommandCenter commandCenter = Locator.locate(CommandCenter.class);

    private final Map<UUID, SuffixProfile> suffixCache;

    public SuffixManager() {
        this.suffixDataManager = new SuffixDataManager();
        this.suffixCache = new HashMap<>();

        DatabaseMessageManager databaseMessageManager = Locator.locate(DatabaseMessageManager.class);
        databaseMessageManager.register(SUFFIX_RELOAD_CHANNEL, (s) -> loadSuffixProfile(UUID.fromString(s)));

        commandCenter.register(new CommandSuffix("suffix"), new CommandSuffixManage("suffixmanage"));

        Module.registerEvents(this);
    }

    public void addSuffix(UUID uuid, Suffix suffix) {
        if (suffixCache.get(uuid) != null) {
            suffixCache.get(uuid).getSuffixSet().add(suffix);
        }

        ThreadPool.ASYNC_POOL.execute(() -> suffixDataManager.insertSuffix(uuid, suffix));
    }

    public void removeSuffix(UUID uuid, Suffix suffix) {
        if (suffixCache.get(uuid) != null) {
            suffixCache.get(uuid).getSuffixSet().remove(suffix);
        }

        ThreadPool.ASYNC_POOL.execute(() -> suffixDataManager.removeSuffix(uuid, suffix));
    }

    public enum SuffixPerm implements Permission {
        SUFFIX_MANAGE;
    }

    @EventHandler
    public void login(PlayerLoginEvent event) {
        loadSuffixProfile(event.getPlayer());
    }

    private void loadSuffixProfile(Player player) {
        loadSuffixProfile(player.getUniqueId());
    }

    private void loadSuffixProfile(UUID uuid) {
        Set<Suffix> suffixes = suffixDataManager.getSuffixes(uuid);
        Suffix activeSuffix = suffixDataManager.getActiveSuffix(uuid);

        suffixCache.put(uuid, new SuffixProfile(suffixes, activeSuffix));
    }

    public Suffix getActiveSuffix(UUID uuid) {
        SuffixProfile suffixProfile = suffixCache.get(uuid);

        if (suffixProfile == null) {
            return suffixDataManager.getActiveSuffix(uuid);
        }

        return suffixProfile.getActiveSuffix();
    }

    public Set<Suffix> getSuffixes(UUID uuid) {
        SuffixProfile suffixProfile = suffixCache.get(uuid);

        if (suffixProfile == null) {
            return suffixDataManager.getSuffixes(uuid);
        }

        return suffixProfile.getSuffixSet();
    }

    public void setActiveSuffix(UUID uuid, Suffix suffix) {
        suffixCache.get(uuid).setActiveSuffix(suffix);

        ThreadPool.ASYNC_POOL.execute(() -> suffixDataManager.setActiveSuffix(uuid, suffix));
    }

    public void removeActiveSuffix(UUID uuid) {
        suffixCache.get(uuid).setActiveSuffix(null);
        ThreadPool.ASYNC_POOL.execute(() -> suffixDataManager.removeActiveSuffix(uuid));
    }
}
