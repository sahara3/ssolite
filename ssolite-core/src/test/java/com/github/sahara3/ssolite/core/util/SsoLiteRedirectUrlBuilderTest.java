package com.github.sahara3.ssolite.core.util;

import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.web.PortResolver;
import org.springframework.security.web.PortResolverImpl;
import org.springframework.security.web.util.RedirectUrlBuilder;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SsoLiteRedirectUrlBuilderTest {

    private SsoLiteRedirectUrlBuilder builder;

    @BeforeEach
    void init() {
        this.builder = new SsoLiteRedirectUrlBuilder();
    }

    @ParameterizedTest
    @ValueSource(strings = {"http://localhost:8080", "https://www.github.com/sahara3", "http://example.org:80/"})
    void testBuildRedirectUrlWithAbsoluteUrl(String url) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        String redirect = this.builder.buildRedirectUrl(request, url);
        assertThat(redirect, is(equalTo(url)));
    }

    @ParameterizedTest
    @CsvSource({ //
            "GET, http://localhost:8080, /", //
            "POST, https://www.github.com/sahara3/, /features", //
            "GET, https://ftp.example.org:8443/pub, /path/to/somewhere", //
            "GET, http://example.org:80, /", //
            "GET, https://example.org:443, /", //
    })
    void testBuildRedirectUrlWithRelativeUrl(String method, String requestUrl, String redirectPath) {
        HttpServletRequest request = HttpServletRequestGenerator.from(method, requestUrl, false);

        String redirect = this.builder.buildRedirectUrl(request, redirectPath);
        String expected = expectedUrlFromRequest(request, redirectPath, false);
        assertThat(redirect, is(equalTo(expected)));
    }

    @ParameterizedTest
    @CsvSource({ //
            "GET, http://localhost:8080, internal:/", //
            "POST, https://www.github.com/sahara3/, internal:/", //
            "GET, https://ftp.example.org:8443/pub/, internal:/path/to/somewhere", //
            "GET, http://example.org:80, internal:/", //
            "GET, https://example.org:443, internal:/", //
    })
    void testBuildRedirectUrlWithInternal(String method, String requestUrl, String redirectPath) {
        HttpServletRequest request = HttpServletRequestGenerator.from(method, requestUrl, true);

        String expected = expectedUrlFromRequest(request, redirectPath.replace("internal:", ""), true);
        String redirect = this.builder.buildRedirectUrl(request, redirectPath);
        assertThat(redirect, is(equalTo(expected)));
    }

    @Test
    void testBuildRedirectUrlWithNull() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        assertThrows(IllegalArgumentException.class, () -> {
            this.builder.buildRedirectUrl(request, null);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            this.builder.buildRedirectUrl(null, "http://localhost:8080");
        });
    }

    static class HttpServletRequestGenerator {
        public static HttpServletRequest from(String method, String requestUrl, boolean internal) {
            return new HttpServletRequestGenerator(method, requestUrl).generate(internal);
        }

        private String method;
        private String requestUrl;

        HttpServletRequestGenerator(String method, String requestUrl) {
            this.method = method;
            this.requestUrl = requestUrl;
        }

        HttpServletRequest generate(boolean internal) {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setMethod(this.method);
            request.setRequestURI(this.requestUrl);

            URL url = this.toUrl();
            request.setScheme(url.getProtocol());
            request.setServerName(url.getHost());

            int port = url.getPort();
            if (port < 0) {
                String scheme = url.getProtocol().toLowerCase();
                if ("http".equals(scheme)) {
                    port = 80;
                }
                else if ("https".equals(scheme)) {
                    port = 443;
                }
                else {
                    throw new RuntimeException("unknown scheme: " + scheme);
                }
            }
            request.setServerPort(port);

            if (internal) {
                String path = url.getPath();
                if (path == null || path.isEmpty() || "/".equals(path)) {
                    request.setContextPath("");
                    request.setPathInfo(path);
                }
                else {
                    String[] splitted = path.split("/", 3);
                    request.setContextPath("/" + splitted[1]);
                    if (splitted.length >= 3) {
                        request.setPathInfo("/" + splitted[2]);
                    }
                }
            }
            else {
                request.setContextPath("/some-context-path");
                request.setPathInfo(url.getPath());
            }

            return request;
        }

        private URL toUrl() {
            try {
                return new URL(this.requestUrl);
            }
            catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static String expectedUrlFromRequest(HttpServletRequest request, String path, boolean internal) {
        PortResolver portResolver = new PortResolverImpl();
        RedirectUrlBuilder builder = new RedirectUrlBuilder();
        builder.setScheme(request.getScheme());
        builder.setServerName(request.getServerName());
        builder.setPort(portResolver.getServerPort(request));
        if (internal) {
            builder.setContextPath(request.getContextPath());
        }
        builder.setPathInfo(path);
        return builder.getUrl();
    }

}
