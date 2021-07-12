package org.maritimemc.core.util;

public class UtilText {

    public static String getArguments(String[] args) {
        return getArguments(args, 0);
    }

    public static String getArguments(String[] args, int startingIndex) {
        StringBuilder sb = new StringBuilder();

        for (int i = startingIndex; i < args.length; i++) {
            if (i != startingIndex) sb.append(" ");
            sb.append(args[i]);
        }

        return sb.toString();
    }
}
