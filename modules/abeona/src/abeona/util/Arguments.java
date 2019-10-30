package abeona.util;

public final class Arguments {
    public static void requireNonNull(Object obj, String name) {
        if (obj == null) {
            throw new IllegalArgumentException(name + " is null");
        }
    }

    public static void requireInstanceOf(Object obj, Class<?> superClass, String name) {
        if (!superClass.isAssignableFrom(obj.getClass())) {
            throw new IllegalArgumentException("Argument " + name + " is not a instance/subclass of " + superClass);
        }
    }

    public static void preventInstanceOf(Object obj, Class<?> superClass, String name) {
        if (superClass.isAssignableFrom(obj.getClass())) {
            throw new IllegalArgumentException("Argument " + name + " is not allowed to be a instance/subclass of " + superClass);
        }
    }

    public static void requireMinimum(long minimumInclusive, long realValue, String name) {
        if (realValue < minimumInclusive) {
            throw new IllegalArgumentException("Argument " + name + " must be larger than or equal to " + minimumInclusive);
        }
    }

    public static void requireBelowMaximum(long maximumExclusive, long realValue, String name) {
        if (realValue > maximumExclusive) {
            throw new IllegalArgumentException("Argument " + name + " must be smaller than " + maximumExclusive);
        }
    }

    public static void requireRange(long minimumInclusive, long maximumExclusive, long realValue, String name) {
        if (realValue < minimumInclusive || realValue >= maximumExclusive) {
            throw new IllegalArgumentException("Argument " + name + " must be in the range of " + minimumInclusive + " (inclusive) up to " + maximumExclusive + "(exclusive)");
        }
    }
}
