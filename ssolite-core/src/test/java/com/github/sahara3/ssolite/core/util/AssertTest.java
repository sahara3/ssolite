package com.github.sahara3.ssolite.core.util;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AssertTest {

    @Test
    void testNotNullWithNull() {
        String message = "this is an exception message.";

        Throwable exception = assertThrows(IllegalArgumentException.class, () -> {
            Assert.notNull(null, message);
        });
        assertThat(exception.getMessage(), is(equalTo(message)));
    }

    @Test
    void testNotNullWithNonNull() {
        Assert.notNull("string", "not thrown");
        Assert.notNull(new Object(), "not thrown");
    }
}
