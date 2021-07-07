package org.maritimemc.core.versioning;

import org.bukkit.Bukkit;
import org.maritimemc.abstraction.INmsHandler;
import org.maritimemc.core.util.UtilLog;

import java.util.regex.Pattern;

public class VersionHandler {

    private static final String VERSIONING_PACKAGE = "org.maritimemc.version";
    private static final String NMS_HANDLER_CLASS_NAME = "NmsHandler";

    public static INmsHandler NMS_HANDLER;

    static {
        String bukkitPackage = Bukkit.getServer().getClass().getPackage().getName();
        String version = bukkitPackage.split(Pattern.quote("."))[3];

        String ourPackage = VERSIONING_PACKAGE + "." + version;

        try {
            NMS_HANDLER = (INmsHandler) Class.forName(ourPackage + "." + NMS_HANDLER_CLASS_NAME).newInstance();
            UtilLog.log("Found version " + version + " and loaded class.");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
