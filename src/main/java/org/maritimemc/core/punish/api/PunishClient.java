/*
 * Copyright © Minedroid Network 2020
 *
 * You may not use, distribute, or share this code under any circumstances
 * without explicit permission from Minedroid Network. All source code and
 * binaries are owned by Minedroid Network.
 *
 * All rights reserved.
 */

package org.maritimemc.core.punish.api;

import lombok.Getter;
import org.maritimemc.core.punish.PunishDataManager;
import org.maritimemc.core.punish.api.pojo.Archival;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * A cached object containing a user's punishments
 * and methods to edit them.
 */
public class PunishClient {

    private final PunishDataManager punishDataManager;

    @Getter
    private final UUID uuid;

    @Getter
    private final Set<Punishment> punishments;

    /**
     * Class constructor
     *
     * @param uuid   The UUID of the player whose punishment client this is.
     */
    public PunishClient(UUID uuid, PunishDataManager punishDataManager) {
        this.uuid = uuid;
        this.punishDataManager = punishDataManager;
        this.punishments = new HashSet<>();

        loadPunishments();
    }

    /**
     * Loads a user's punishments from MongoDB.
     */
    private void loadPunishments() {
        punishments.addAll(punishDataManager.getPunishmentsForUser(uuid));
    }

    /**
     * Adds a punishment to MySQL and local cache for this user.
     *
     * @param punishment The punishment to add.
     */
    public CompletableFuture<Void> addPunishment(Punishment punishment) {
        punishments.add(punishment);
        return punishDataManager.addPunishment(punishment);
    }

    /**
     * Sets the archival of a user's punishment.
     *
     * @param punishment The punishment in question.
     * @param archival   The archival object to set.
     */
    public void setArchival(Punishment punishment, Archival archival) {
        punishment.setArchival(archival);
        punishDataManager.setArchival(punishment, archival);
    }

    /**
     * Sets the 'seen' value of a punishment to True, indicating that they have seen/been
     * notified of this punishment.
     *
     * @param punishment The punishment in question.
     */
    public void setSeenTrue(Punishment punishment) {
        punishment.setSeen(true);
        punishDataManager.setSeenTrue(punishment);
    }


    /**
     * Deletes a punishment from MySQL and local cache.
     * Used AFTER archiving to remove any log of this punishment.
     *
     * @param punishment The punishment in question.
     */
    public void deletePunishment(Punishment punishment) {
        getPunishments().remove(punishment);
        punishDataManager.deletePunishment(punishment);
    }
}
