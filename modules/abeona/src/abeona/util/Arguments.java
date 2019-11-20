package abeona.util;

/**
 * Utility functions that perform argument checking
 * Each method throws {@link IllegalArgumentException} when the conditions are not met, the exception has a formatted messae mentioning the argument by name.
 */
public final class Arguments {
    /**
     * Ensures an argument is not null
     * @param obj  The object to test null for
     * @param name The name of the argument
     */
    public static void requireNonNull(Object obj, String name) {
        if (obj == null) {
            throw new IllegalArgumentException(name + " is null");
        }
    }

    /**
     * Ensures an argument implements a given class
     * @param obj The instance
     * @param superClass The class that the instance needs to implement
     * @param name The name of the argument
     */
    public static void requireInstanceOf(Object obj, Class<?> superClass, String name) {
        requireNonNull(obj, name);
        if (!superClass.isAssignableFrom(obj.getClass())) {
            throw new IllegalArgumentException("Argument " + name + " is not a instance/subclass of " + superClass);
        }
    }

    /**
     * Requires an integer argument to have a minimal (inclusive) value
     * @param minimumInclusive The inclusive lower bound of the value
     * @param realValue The actual value of the argument
     * @param name The name of the argument
     */
    public static void requireMinimum(long minimumInclusive, long realValue, String name) {
        if (realValue < minimumInclusive) {
            throw new IllegalArgumentException("Argument " + name + " must be larger than or equal to " + minimumInclusive);
        }
    }

    /**
     * Requires a double argument to have a minimal (inclusive) value
     * @param minimumInclusive The inclusive lower bound of the value
     * @param realValue The actual value of the argument
     * @param name The name of the argument
     */
    public static void requireMinimum(double minimumInclusive, double realValue, String name) {
        if (realValue < minimumInclusive) {
            throw new IllegalArgumentException("Argument " + name + " must be larger than or equal to " + minimumInclusive);
        }
    }

    /**
     * Requires a integer argument to be below a given limit
     * @param maximumExclusive The exclusive upper bound of the value
     * @param realValue The actual value of the argument
     * @param name The name of the argument
     */
    public static void requireBelowMaximum(long maximumExclusive, long realValue, String name) {
        if (realValue > maximumExclusive) {
            throw new IllegalArgumentException("Argument " + name + " must be smaller than " + maximumExclusive);
        }
    }

    /**
     * Requires an integer argument to fall within a range
     * @param minimumInclusive The inclusive lower bound of the value
     * @param maximumExclusive The exclusive upper bound of the value
     * @param realValue The actual value of the argument
     * @param name The name of the argument
     */
    public static void requireRange(long minimumInclusive, long maximumExclusive, long realValue, String name) {
        if (realValue < minimumInclusive || realValue >= maximumExclusive) {
            throw new IllegalArgumentException("Argument " + name + " must be in the range of " + minimumInclusive + " (inclusive) up to " + maximumExclusive + "(exclusive)");
        }
    }
}
