package com.github.sahara3.ssolite.core.util;

/**
 * Assertion utilities.
 *
 * @author sahara3
 */
public class Assert {

    /**
     * Asserts that the object is not null.
     *
     * @param object  the object to check.
     * @param message an exception message to use when the assertion fails.
     * @throws IllegalArgumentException thrown if the object is null.
     */
    public static void notNull(Object object, String message) throws IllegalArgumentException {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }
}
