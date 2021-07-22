package org.maritimemc.core.punish;

import org.maritimemc.core.chatlog.ChatLogModule;
import org.maritimemc.core.punish.api.PunishClient;
import org.maritimemc.core.punish.api.Punishment;
import org.maritimemc.core.punish.api.model.IPunishmentManager;
import org.maritimemc.core.punish.api.model.PunishmentType;
import org.maritimemc.core.punish.api.pojo.PresetReason;
import org.maritimemc.core.punish.api.pojo.Sentence;
import org.maritimemc.core.punish.type.*;
import org.maritimemc.core.punish.util.SentenceUtil;
import org.maritimemc.core.service.Locator;

import java.util.UUID;

public class PunishmentManager implements IPunishmentManager {

    private final Punish punish;

    private final ChatLogModule chatLogModule = Locator.locate(ChatLogModule.class);

    /**
     * Class constructor
     *
     * @param punish A Punish instance.
     */
    public PunishmentManager(Punish punish) {
        this.punish = punish;
    }

    @Override
    public PunishClient getClientForUser(UUID uuid) {
        PunishClient punishClient;
        if (punish.getPunishClients().containsKey(uuid)) {
            // Already loaded, online locally
            punishClient = punish.getPunishClients().get(uuid);
        } else {
            // Not online, generate and load
            return loadClientDb(uuid);
        }

        return punishClient;
    }

    @Override
    public PunishClient loadClientDb(UUID uuid) {
        PunishClient client = new PunishClient(uuid, punish.getPunishDataManager());
        punish.getPunishClients().put(uuid, client);
        return client;
    }

    @Override
    public void doWarn(UUID uuid, UUID staffUuid, String reason, Punishment.OffenceCategory offenceCategory, boolean severe) {
        Punishment punishment = new Punishment(
                uuid,
                staffUuid,
                reason,
                PunishmentType.matchFromClass(TypeWarn.class),
                offenceCategory,
                severe,
                System.currentTimeMillis(),
                -1,
                false,
                null
        );

        PunishClient punishClient = getClientForUser(uuid);

        punishClient.addPunishment(punishment).whenComplete((v, x) -> {
            if (x != null) {
                x.printStackTrace();
                return;
            }

            punishment.getType().onPunish(punishment, punishClient, punish);
        });
    }

    @Override
    public void doPermBan(UUID uuid, UUID staffUuid, String reason, Punishment.OffenceCategory offenceCategory, boolean severe) {
        Punishment punishment = new Punishment(
                uuid,
                staffUuid,
                reason,
                PunishmentType.matchFromClass(TypeBan.class),
                offenceCategory,
                severe,
                System.currentTimeMillis(),
                -1,
                false,
                null
        );

        PunishClient punishClient = getClientForUser(uuid);

        punishClient.addPunishment(punishment).whenComplete((v, x) -> {
            if (x != null) {
                x.printStackTrace();
                return;
            }

            punishment.getType().onPunish(punishment, punishClient, punish);
        });
    }

    @Override
    public void doTempBan(UUID uuid, UUID staffUuid, String reason, long duration, Punishment.OffenceCategory offenceCategory, boolean severe) {
        Punishment punishment = new Punishment(
                uuid,
                staffUuid,
                reason,
                PunishmentType.matchFromClass(TypeBan.class),
                offenceCategory,
                severe,
                System.currentTimeMillis(),
                duration,
                false,
                null
        );

        PunishClient punishClient = getClientForUser(uuid);

        punishClient.addPunishment(punishment).whenComplete((v, x) -> {
            if (x != null) {
                x.printStackTrace();
                return;
            }

            punishment.getType().onPunish(punishment, punishClient, punish);
        });
    }

    @Override
    public void doPermReportBan(UUID uuid, UUID staffUuid, String reason, Punishment.OffenceCategory offenceCategory, boolean severe) {
        Punishment punishment = new Punishment(
                uuid,
                staffUuid,
                reason,
                PunishmentType.matchFromClass(TypeReportBan.class),
                offenceCategory,
                severe,
                System.currentTimeMillis(),
                -1,
                false,
                null
        );

        PunishClient punishClient = getClientForUser(uuid);

        punishClient.addPunishment(punishment).whenComplete((v, x) -> {
            if (x != null) {
                x.printStackTrace();
                return;
            }

            punishment.getType().onPunish(punishment, punishClient, punish);
        });
    }

    @Override
    public void doTempReportBan(UUID uuid, UUID staffUuid, String reason, long duration, Punishment.OffenceCategory offenceCategory, boolean severe) {
        Punishment punishment = new Punishment(
                uuid,
                staffUuid,
                reason,
                PunishmentType.matchFromClass(TypeReportBan.class),
                offenceCategory,
                severe,
                System.currentTimeMillis(),
                duration,
                false,
                null
        );

        PunishClient punishClient = getClientForUser(uuid);

        punishClient.addPunishment(punishment).whenComplete((v, x) -> {
            if (x != null) {
                x.printStackTrace();
                return;
            }

            punishment.getType().onPunish(punishment, punishClient, punish);
        });
    }

    @Override
    public void doKick(UUID uuid, UUID staffUuid, String reason, Punishment.OffenceCategory offenceCategory, boolean severe) {
        Punishment punishment = new Punishment(
                uuid,
                staffUuid,
                reason,
                PunishmentType.matchFromClass(TypeKick.class),
                offenceCategory,
                severe,
                System.currentTimeMillis(),
                -1,
                true,
                null
        );

        PunishClient punishClient = getClientForUser(uuid);

        punishClient.addPunishment(punishment).whenComplete((v, x) -> {
            if (x != null) {
                x.printStackTrace();
                return;
            }

            punishment.getType().onPunish(punishment, punishClient, punish);
        });
    }

    @Override
    public void doPermMute(UUID uuid, UUID staffUuid, String reason, Punishment.OffenceCategory offenceCategory, boolean severe) {
        Punishment punishment = new Punishment(
                uuid,
                staffUuid,
                reason,
                PunishmentType.matchFromClass(TypeMute.class),
                offenceCategory,
                severe,
                System.currentTimeMillis(),
                -1,
                false,
                null
        );

        PunishClient punishClient = getClientForUser(uuid);

        punishClient.addPunishment(punishment).whenComplete((v, x) -> {
            if (x != null) {
                x.printStackTrace();
                return;
            }

            punishment.getType().onPunish(punishment, punishClient, punish);
        });
    }

    @Override
    public void doTempMute(UUID uuid, UUID staffUuid, String reason, long duration, Punishment.OffenceCategory offenceCategory, boolean severe) {
        Punishment punishment = new Punishment(
                uuid,
                staffUuid,
                reason,
                PunishmentType.matchFromClass(TypeMute.class),
                offenceCategory,
                severe,
                System.currentTimeMillis(),
                duration,
                false,
                null
        );

        PunishClient punishClient = getClientForUser(uuid);

        punishClient.addPunishment(punishment).whenComplete((v, x) -> {
            if (x != null) {
                x.printStackTrace();
                return;
            }

            punishment.getType().onPunish(punishment, punishClient, punish);
        });
    }

    @Override
    public void doPunishPreset(UUID uuid, UUID staffUuid, PresetReason presetReason, boolean saveChat) {
        String reason = presetReason.getName();

        PunishClient clientForUser = getClientForUser(uuid);

        Sentence sentence = SentenceUtil.getSentence(clientForUser.getPunishments(), presetReason);

        PunishmentType type = sentence.getType();

        if (type == null) return;

        if (saveChat) {
            chatLogModule.createChatLog(staffUuid, uuid, false).whenComplete((s, x) -> {
                String updated = reason;

                if (x != null) {
                    x.printStackTrace();
                    return;
                }

                updated += " (ChatLog: " + s + ")";

                process(uuid, staffUuid, presetReason, updated, sentence, type);
            });
        } else {
            process(uuid, staffUuid, presetReason, reason, sentence, type);
        }


    }

    private void process(UUID uuid, UUID staffUuid, PresetReason presetReason, String reason, Sentence sentence, PunishmentType type) {
        if (type instanceof TypeWarn) {
            doWarn(uuid, staffUuid, reason, presetReason.getCategory(), presetReason.isSevere());
        } else if (type instanceof TypeMute) {
            if (sentence.getDuration() == -1)
                doPermMute(uuid, staffUuid, reason, presetReason.getCategory(), presetReason.isSevere());
            else
                doTempMute(uuid, staffUuid, reason, sentence.getDuration(), presetReason.getCategory(), presetReason.isSevere());
        } else if (type instanceof TypeKick) {
            doKick(uuid, staffUuid, reason, presetReason.getCategory(), presetReason.isSevere());
        } else if (type instanceof TypeBan) {
            if (sentence.getDuration() == -1)
                doPermBan(uuid, staffUuid, reason, presetReason.getCategory(), presetReason.isSevere());
            else
                doTempBan(uuid, staffUuid, reason, sentence.getDuration(), presetReason.getCategory(), presetReason.isSevere());
        } else if (type instanceof TypeReportBan) {
            if (sentence.getDuration() == -1)
                doPermReportBan(uuid, staffUuid, reason, presetReason.getCategory(), presetReason.isSevere());
            else
                doTempReportBan(uuid, staffUuid, reason, sentence.getDuration(), presetReason.getCategory(), presetReason.isSevere());
        } else {

            // Throw exception - unimplemented PunishmentType.
            throw new UnsupportedOperationException("Punishment type not supported for preset reasons: " + type.getId());
        }
    }
}
