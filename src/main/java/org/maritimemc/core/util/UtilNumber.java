package org.maritimemc.core.util;

public class UtilNumber {

    public static Integer fromString(String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
