package org.maritimemc.core.util;

/**
 * Utility class for mathematics operations.
 */
public class UtilMath {

    /**
     * Divides and rounds UP for two numbers.
     * If the answer is an integer, it does not round.
     * (e.g. 13 / 4 would return FOUR (4) )
     *
     * @param a The dividend.
     * @param b The divisor.
     * @return The result of the calculation.
     */
    public static int divideAndRound(double a, double b) {
        // Same function as java.util.Math.ceil()
        // but Math.ceil() seems to take 500x as longer? (literally)

        double result = a / b;

        int integerResult = (int) result;
        if (!(result == integerResult)) {
            integerResult++;
        }

        return integerResult;

    }
}
