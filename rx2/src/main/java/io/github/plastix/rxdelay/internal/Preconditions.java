package io.github.plastix.rxdelay.internal;

public final class Preconditions {
    public static <T> void checkNotNull(T value, String message) {
        if (value == null) {
            throw new NullPointerException(message);
        }
    }

    private Preconditions() {
        throw new AssertionError("No instances.");
    }
}
