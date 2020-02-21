package com.github.sahara3.ssolite.core.model;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

class SsoLiteAccessTokenTest {

    @Test
    void testConstructor() {
        SsoLiteAccessToken token = new SsoLiteAccessToken();
        assertThat(token.getId(), is(nullValue()));
        assertThat(token.getUsername(), is(nullValue()));
        assertThat(token.getExpired(), is(nullValue()));
    }

    @ParameterizedTest
    @MethodSource("propertiesProvider")
    void testConsutructorWithArguments(String id, String username, OffsetDateTime expired) {
        SsoLiteAccessToken token = new SsoLiteAccessToken(id, username, expired);
        assertThat(token.getId(), is(equalTo(id)));
        assertThat(token.getUsername(), is(equalTo(username)));
        assertThat(token.getExpired(), is(equalTo(expired)));
    }

    @ParameterizedTest
    @MethodSource("propertiesProvider")
    void testProperties(String id, String username, OffsetDateTime expired) {
        SsoLiteAccessToken token = new SsoLiteAccessToken();
        token.setId(id);
        token.setUsername(username);
        token.setExpired(expired);

        assertThat(token.getId(), is(equalTo(id)));
        assertThat(token.getUsername(), is(equalTo(username)));
        assertThat(token.getExpired(), is(equalTo(expired)));
    }

    @ParameterizedTest
    @MethodSource("propertiesProvider")
    void testEqualsAndHashCode(String id, String username, OffsetDateTime expired) {
        SsoLiteAccessToken token1 = new SsoLiteAccessToken(id, username, expired);

        SsoLiteAccessToken token2 = new SsoLiteAccessToken();
        token2.setId(id);
        token2.setUsername(username);
        token2.setExpired(expired);

        SsoLiteAccessToken token3 = token1;

        assertThat(token1, is(equalTo(token2)));
        assertThat(token2, is(equalTo(token1)));
        assertThat(token1.hashCode(), is(token2.hashCode()));

        assertThat(token1, is(equalTo(token3)));
        assertThat(token1.hashCode(), is(token3.hashCode()));
    }

    @Test
    void testNotEquals() {
        OffsetDateTime t1 = OffsetDateTime.of(1970, 1, 1, 0, 0, 0, 0, ZoneOffset.ofHours(0));
        OffsetDateTime t2 = OffsetDateTime.of(2000, 1, 1, 0, 0, 0, 0, ZoneOffset.ofHours(0));

        SsoLiteAccessToken token1 = new SsoLiteAccessToken("id", "user", t1);
        SsoLiteAccessToken token2 = new SsoLiteAccessToken();

        assertThat(token1, is(not(equalTo(token2))));

        token2.setId(token1.getId());
        assertThat(token1, is(not(equalTo(token2))));

        token2.setUsername(token1.getUsername());
        assertThat(token1, is(not(equalTo(token2))));

        token2.setExpired(t2);
        assertThat(token1, is(not(equalTo(token2))));

        token2.setExpired(t1);
        token2.setId("xyz");
        assertThat(token1, is(not(equalTo(token2))));

        assertThat(token1.equals(null), is(false));
        assertThat(token1.equals(new Object()), is(false));
    }

    static Stream<? extends Arguments> propertiesProvider() {
        return Stream.of( //
                new String[] {"id-1", "user-1", "2020-01-01T00:00:00Z"}, //
                new String[] {"id with space", "user with space", "1970-01-01T00:00:00Z"} //
        ).map(Arguments::of);
    }
}
