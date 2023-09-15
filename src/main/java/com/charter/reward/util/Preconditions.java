package com.charter.reward.util;

final public class Preconditions {

    private Preconditions() {
        throw new IllegalStateException("Utility class, not intended for instantiation");
    }

    public static void checkArgument(boolean expression, RuntimeException exceptionToThrow) {
        if (!expression) {
            throw exceptionToThrow;
        }
    }
}
