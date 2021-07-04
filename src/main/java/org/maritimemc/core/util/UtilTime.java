package org.maritimemc.core.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Utility class for time operations.
 */
public class UtilTime {

    /**
     * Formats a date using the format DAY-MONTH-YEAR HOUR-MINUTE-SECOND
     *
     * @param date The date to format, in milliseconds.
     * @return The formatted date string.
     */
    public static String formatDate(long date) {
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        return format.format(new Date(date));
    }

    /**
     * Default method to convert time to a string, rounding to 1DP.
     *
     * @param time The time to convert, in milliseconds.
     * @return A formatted string.
     */
    public static String timeToString(long time) {
        return convertString(time, 1);
    }

    /**
     * Method to convert time to a string, rounding to the specified amount.
     *
     * @param time The time to convert, in milliseconds.
     * @param trim The amount of decimal places to round the result to.
     * @return A formatted string.
     */
    public static String timeToString(long time, int trim) {
        return convertString(Math.max(0, time), trim);
    }

    /**
     * Converts a time to a string.
     *
     * @param time The time to convert, in milliseconds.
     * @param trim The amount of decimal places to round the result to.
     * @return A formatted string.
     */
    private static String convertString(long time, int trim) {
        if (time == -1) return "Permanent";

        TimeUnit timeUnit;

        if (time < 60000) timeUnit = TimeUnit.SECONDS;
        else if (time < 3600000) timeUnit = TimeUnit.MINUTES;
        else if (time < 86400000) timeUnit = TimeUnit.HOURS;
        else timeUnit = TimeUnit.DAYS;

        String fullText;
        double numToDisplay;

        if (trim == 0) {
            // Round to an int.
            if (timeUnit == TimeUnit.DAYS) {
                numToDisplay = trimDecimal(trim, time / 86400000D);
                fullText = (int) numToDisplay + " day";
            } else if (timeUnit == TimeUnit.HOURS) {
                numToDisplay = trimDecimal(trim, time / 3600000D);
                fullText = (int) numToDisplay + " hour";
            } else if (timeUnit == TimeUnit.MINUTES) {
                numToDisplay = trimDecimal(trim, time / 60000D);
                fullText = (int) numToDisplay + " minute";
            } else {
                numToDisplay = trimDecimal(trim, time / 1000D);
                fullText = (int) numToDisplay + " second";
            }
        } else {
            if (timeUnit == TimeUnit.DAYS) {
                numToDisplay = trimDecimal(trim, time / 86400000D);
                fullText = numToDisplay + " day";
            } else if (timeUnit == TimeUnit.HOURS) {
                numToDisplay = trimDecimal(trim, time / 3600000D);
                fullText = numToDisplay + " hour";
            } else if (timeUnit == TimeUnit.MINUTES) {
                numToDisplay = trimDecimal(trim, time / 60000D);
                fullText = numToDisplay + " minute";
            } else {
                numToDisplay = trimDecimal(trim, time / 1000D);
                fullText = numToDisplay + " second";
            }
        }

        if (numToDisplay != 1)
            fullText += "s";

        return fullText;
    }

    /**
     * Trims a decimal by a degree of places.
     *
     * @param degree The amount to trim by.
     * @param d      The decimal to trim.
     * @return The trimmed decimal.
     */
    private static double trimDecimal(int degree, double d) {
        StringBuilder format = new StringBuilder("#.#");

        for (int i = 1; i < degree; i++) format.append("#");

        DecimalFormatSymbols symb = new DecimalFormatSymbols(Locale.ENGLISH);
        DecimalFormat twoDForm = new DecimalFormat(format.toString(), symb);
        return Double.parseDouble(twoDForm.format(d));
    }

    /**
     * Represents a unit of time.
     */
    public enum TimeUnit {
        DAYS,
        HOURS,
        MINUTES,
        SECONDS,
        MILLISECONDS
    }
}

