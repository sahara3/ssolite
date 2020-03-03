package com.github.sahara3.ssolite.spring.jackson2;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.jackson2.CoreJackson2Module;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sahara3.ssolite.spring.client.SsoLiteAccessTokenAuthenticationToken;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SsoLiteJackson2ModuleTest {

    private ObjectMapper mapper;

    @BeforeEach
    void init() {
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new CoreJackson2Module());
        this.mapper.registerModule(new SsoLiteJackson2Module());
    }

    static Stream<SsoLiteAccessTokenAuthenticationToken> tokenProvider() {
        SsoLiteAccessTokenAuthenticationToken t1, t2;
        t1 = new SsoLiteAccessTokenAuthenticationToken("testTokenId");

        User user = new User("user", "password", Arrays.asList(new SimpleGrantedAuthority("ROLE_TEST")));
        t2 = new SsoLiteAccessTokenAuthenticationToken(user, user.getPassword(), user.getAuthorities());

        return Stream.of(t1, t2);
    }

    @ParameterizedTest
    @MethodSource("tokenProvider")
    void testSerializeAndDeserialize(SsoLiteAccessTokenAuthenticationToken token) throws IOException {
        String json = this.mapper.writeValueAsString(token);
        SsoLiteAccessTokenAuthenticationToken actual =
                this.mapper.readValue(json, SsoLiteAccessTokenAuthenticationToken.class);
        assertThat(token, is(equalTo(actual)));
    }

    @ParameterizedTest
    @MethodSource("tokenProvider")
    void testFailureWithoutJacksonModule(SsoLiteAccessTokenAuthenticationToken token) throws IOException {
        ObjectMapper m = new ObjectMapper();
        m.registerModule(new CoreJackson2Module());

        assertThrows(IllegalArgumentException.class, () -> {
            String json = m.writeValueAsString(token);
            m.readValue(json, SsoLiteAccessTokenAuthenticationToken.class);
        });
    }
}
