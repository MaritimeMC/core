package org.maritimemc.core.punish.util;

import org.maritimemc.core.punish.api.Punishment;
import org.maritimemc.core.punish.api.model.PunishmentType;
import org.maritimemc.core.punish.api.pojo.PresetReason;
import org.maritimemc.core.punish.api.pojo.Sentence;
import org.maritimemc.core.punish.type.TypeBan;
import org.maritimemc.core.punish.type.TypeMute;
import org.maritimemc.core.punish.type.TypeReportBan;
import org.maritimemc.core.punish.type.TypeWarn;

import java.util.Set;

public class SentenceUtil {

    /**
     * Calculate a {@link Sentence} based on a user's previous punishments and the reason.
     *
     * @param punishmentSet A set of a user's previous punishments.
     * @param presetReason  A reason for this punishment.
     * @return A sentence, containing the punishment type and duration.
     */
    public static Sentence getSentence(Set<Punishment> punishmentSet, PresetReason presetReason) {

        switch (presetReason.getCategory()) {
            case CHAT: {

                Sentence[] sentencesNonSevere = new Sentence[]{
                        new Sentence(PunishmentType.matchFromClass(TypeWarn.class), -1L), // Warn
                        new Sentence(PunishmentType.matchFromClass(TypeMute.class), 30 * 60 * 1000L), // 30 Minute mute
                        new Sentence(PunishmentType.matchFromClass(TypeMute.class), 60 * 60 * 1000L), // 1 Hour mute
                        new Sentence(PunishmentType.matchFromClass(TypeMute.class), 10 * 60 * 60 * 1000L), // 10 Hour mute
                        new Sentence(PunishmentType.matchFromClass(TypeMute.class), 24 * 60 * 60 * 1000L), // 24 Hour mute
                        new Sentence(PunishmentType.matchFromClass(TypeMute.class), 7 * 24 * 60 * 60 * 1000L) // 7 Day mute
                };

                Sentence[] sentencesSevere = new Sentence[]{
                        new Sentence(PunishmentType.matchFromClass(TypeMute.class), 24 * 60 * 60 * 1000L), // 24 Hour mute
                        new Sentence(PunishmentType.matchFromClass(TypeMute.class), 7 * 24 * 60 * 60 * 1000L), // 7 Day mute
                        new Sentence(PunishmentType.matchFromClass(TypeMute.class), 30 * 24 * 60 * 60 * 1000L), // 30 Day mute
                        new Sentence(PunishmentType.matchFromClass(TypeMute.class), -1L) // Permanent Mute
                };

                return getSentence(sentencesNonSevere, sentencesSevere, punishmentSet, presetReason);
            }

            case GAMEPLAY: {

                Sentence[] sentencesNonSevere = new Sentence[]{
                        new Sentence(PunishmentType.matchFromClass(TypeWarn.class), -1L), // Warn
                        new Sentence(PunishmentType.matchFromClass(TypeBan.class), 12 * 60 * 60 * 1000L), // 12 Hour ban
                        new Sentence(PunishmentType.matchFromClass(TypeBan.class), 48 * 60 * 60 * 1000L), // 48 Hour ban
                        new Sentence(PunishmentType.matchFromClass(TypeBan.class), 7 * 24 * 60 * 60 * 1000L), // 7 Day ban
                        new Sentence(PunishmentType.matchFromClass(TypeBan.class), 30 * 24 * 60 * 60 * 1000L), // 30 Day ban
                        new Sentence(PunishmentType.matchFromClass(TypeBan.class), -1L) // Permanent ban
                };

                Sentence[] sentencesSevere = new Sentence[]{
                        new Sentence(PunishmentType.matchFromClass(TypeBan.class), 7 * 24 * 60 * 60 * 1000L), // 7 Day ban
                        new Sentence(PunishmentType.matchFromClass(TypeBan.class), 30 * 24 * 60 * 60 * 1000L), // 30 Day ban
                        new Sentence(PunishmentType.matchFromClass(TypeBan.class), -1L) // Permanent ban
                };

                return getSentence(sentencesNonSevere, sentencesSevere, punishmentSet, presetReason);

            }

            case CLIENT: {

                Sentence[] sentencesNonSevere = new Sentence[]{
                        new Sentence(PunishmentType.matchFromClass(TypeBan.class), 7 * 24 * 60 * 60 * 1000L), // 7 Day ban
                        new Sentence(PunishmentType.matchFromClass(TypeBan.class), 14 * 24 * 60 * 60 * 1000L), // 14 Day ban
                        new Sentence(PunishmentType.matchFromClass(TypeBan.class), 30 * 24 * 60 * 60 * 1000L), // 30 Day ban
                        new Sentence(PunishmentType.matchFromClass(TypeBan.class), -1L) // Permanent ban
                };

                Sentence[] sentencesSevere = new Sentence[]{
                        new Sentence(PunishmentType.matchFromClass(TypeBan.class), 20 * 24 * 60 * 60 * 1000L), // 20 Day ban
                        new Sentence(PunishmentType.matchFromClass(TypeBan.class), 30 * 24 * 60 * 60 * 1000L), // 30 Day ban
                        new Sentence(PunishmentType.matchFromClass(TypeBan.class), -1L) // Permanent ban
                };

                return getSentence(sentencesNonSevere, sentencesSevere, punishmentSet, presetReason);


            }

            case REPORT: {
                Sentence[] sentencesNonSevere = new Sentence[]{
                        new Sentence(PunishmentType.matchFromClass(TypeReportBan.class), 24 * 60 * 60 * 1000L), // 24 hour report ban
                        new Sentence(PunishmentType.matchFromClass(TypeReportBan.class), 7 * 24 * 60 * 60 * 1000L), // 7 Day report ban
                        new Sentence(PunishmentType.matchFromClass(TypeReportBan.class), 30 * 24 * 60 * 60 * 1000L), // 30 Day report ban
                        new Sentence(PunishmentType.matchFromClass(TypeReportBan.class), -1L) // Permanent report ban
                };

                Sentence[] sentencesSevere = new Sentence[]{
                        new Sentence(PunishmentType.matchFromClass(TypeReportBan.class), -1L) // Permanent report ban
                };

                return getSentence(sentencesNonSevere, sentencesSevere, punishmentSet, presetReason);
            }

            default:
                throw new UnsupportedOperationException("Punish category not implemented");
        }
    }

    /**
     * Calculate the sentence based for a category based on the
     * sentences for non-severe punishments within that category, and the sentences
     * for severe punishments within that category.
     *
     * @param sentencesNonSevere An array of sentences for non-severe punishments. [0] is first non-severe punishment, and the last index is the last non-severe punishment. If a user has more than the last, only the last is applied.
     * @param sentencesSevere    An array of sentences for severe punishments. [0] is first severe punishment, and the last index is the last severe punishment. If a user has more than the last, only the last is applied.
     * @param punishmentSet      A set of the user's previous punishments.
     * @param reason             The reason for this punishment.
     * @return A sentence, containing the punishment type and duration.
     */
    private static Sentence getSentence(Sentence[] sentencesNonSevere, Sentence[] sentencesSevere, Set<Punishment> punishmentSet, PresetReason reason) {
        if (reason.isSevere()) {
            int amountSevereInThisCategory = 0;
            for (Punishment punishment : punishmentSet) {
                if (punishment.getArchival() == null) {
                    if (punishment.getCategory() == reason.getCategory() && punishment.isSevere()) {
                        amountSevereInThisCategory++;
                    }
                }
            }

            Sentence sentence;
            if (sentencesSevere.length < amountSevereInThisCategory) {
                sentence = sentencesSevere[sentencesSevere.length - 1];
            } else {
                sentence = sentencesSevere[amountSevereInThisCategory];
            }

            return sentence;

        } else {

            int amountNonSevereInThisCategory = 0;
            for (Punishment punishment : punishmentSet) {
                if (punishment.getArchival() == null) {
                    if (punishment.getCategory() == reason.getCategory() && !punishment.isSevere()) {
                        amountNonSevereInThisCategory++;
                    }
                }
            }

            Sentence sentence;
            if (sentencesSevere.length < amountNonSevereInThisCategory) {
                sentence = sentencesNonSevere[sentencesNonSevere.length - 1];
            } else {
                sentence = sentencesNonSevere[amountNonSevereInThisCategory];
            }

            return sentence;

        }
    }
}
