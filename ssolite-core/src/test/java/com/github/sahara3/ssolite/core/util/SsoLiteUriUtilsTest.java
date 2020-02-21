package com.github.sahara3.ssolite.core.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.web.util.UriUtils;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.isEmptyString;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SsoLiteUriUtilsTest {

    @ParameterizedTest
    @ValueSource(
            strings = {"http://localhost:8080", "https://www.github.com/sahara3", "svn+ssh://svn.example.org/trunk/"})
    void testGetDomainUri(String url) throws URISyntaxException {
        URI uri = new URI(url);
        URI result = SsoLiteUriUtils.getDomainUri(uri);

        assertThat(result.getScheme(), is(equalTo(uri.getScheme())));
        assertThat(result.getHost(), is(equalTo(uri.getHost())));
        assertThat(result.getPort(), is(equalTo(uri.getPort())));

        assertThat(result.getUserInfo(), is(nullValue()));
        assertThat(result.getPath(), isEmptyString());
        assertThat(result.getQuery(), is(nullValue()));
        assertThat(result.getFragment(), is(nullValue()));
    }

    @Test
    void testGetDomainUriWithNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            SsoLiteUriUtils.getDomainUri(null);
        });
    }

    @ParameterizedTest
    @ValueSource(
            strings = {"http://localhost:8080", "https://www.github.com/sahara3", "svn+ssh://svn.example.org/trunk/"})
    void testIsAbsoluteUrlWithAbsoluteUrl(String url) {
        assertThat(SsoLiteUriUtils.isAbsoluteUrl(url), is(true));
    }

    @ParameterizedTest
    @ValueSource(strings = {"/path", "C:/Windows", "", "internal:/"})
    void testIsAbsoluteUrlWithRelativeUrl(String url) {
        assertThat(SsoLiteUriUtils.isAbsoluteUrl(url), is(false));
    }

    @Test
    void testIsAbsoluteUrlWithNull() {
        assertThat(SsoLiteUriUtils.isAbsoluteUrl(null), is(false));
    }

    @ParameterizedTest
    @MethodSource("asciiCharactersProvider")
    void testEncodeQueryParam(String text, String expected) {
        String actual = SsoLiteUriUtils.encodeQueryParam(text, StandardCharsets.UTF_8);
        assertThat(actual, is(equalTo(expected)));
    }

    static Stream<Arguments> asciiCharactersProvider() {
        return Stream.of( //
                Arguments.of(" !\"#$%&'()*+,-./", "%20%21%22%23%24%25%26%27%28%29%2A%2B%2C-.%2F"), //
                Arguments.of("0123456789:;<=>?", "0123456789%3A%3B%3C%3D%3E%3F"), //
                Arguments.of("@ABCDEFGHIJKLMNO", "%40ABCDEFGHIJKLMNO"), //
                Arguments.of("PQRSTUVWXYZ[\\]^_", "PQRSTUVWXYZ%5B%5C%5D%5E_"), //
                Arguments.of("`abcdefghijklmno", "%60abcdefghijklmno"), //
                Arguments.of("pqrstuvwxyz{|}~", "pqrstuvwxyz%7B%7C%7D~"));
    }

    @Test
    void testEncodeQueryWithMultiBytes() {
        String text = "こんにちは";
        String utf8 = SsoLiteUriUtils.encodeQueryParam(text, StandardCharsets.UTF_8);
        assertThat(utf8, is(equalTo(UriUtils.encodeQueryParam(text, StandardCharsets.UTF_8))));
    }

    @Test
    void testEncodeQueryParamWithNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            SsoLiteUriUtils.encodeQueryParam(null, StandardCharsets.UTF_8);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            SsoLiteUriUtils.encodeQueryParam("text", null);
        });
    }
}
