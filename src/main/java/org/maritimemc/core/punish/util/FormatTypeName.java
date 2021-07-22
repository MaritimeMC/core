package org.maritimemc.core.punish.util;

import org.maritimemc.core.punish.api.Punishment;

public class FormatTypeName {

    /**
     * Format the display name of a Punishment.
     *
     * @param punishment The punishment object to use.
     * @return A name of the punishment, e.g. (Permanent) Mute.
     */
    public static String format(Punishment punishment) {
        String name = "";
        if (punishment.getDuration() == -1 && !(punishment.getType().isAlwaysPermanent())) {
            name += "Permanent ";
        }
        name += punishment.getType().getName();

        return name;
    }
}
