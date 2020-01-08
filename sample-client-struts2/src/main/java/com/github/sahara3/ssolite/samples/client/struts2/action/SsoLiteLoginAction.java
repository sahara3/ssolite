package com.github.sahara3.ssolite.samples.client.struts2.action;

import java.io.IOException;

import javax.servlet.http.HttpSession;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sahara3.ssolite.model.SsoLiteAccessToken;
import com.github.sahara3.ssolite.samples.client.struts2.interceptor.AuthInterceptor;
import com.github.sahara3.ssolite.samples.client.struts2.model.AuthToken;
import com.github.sahara3.ssolite.samples.client.struts2.model.LocalUser;
import com.github.sahara3.ssolite.samples.client.struts2.service.AuthException;
import com.github.sahara3.ssolite.samples.client.struts2.service.BadPasswordException;
import com.github.sahara3.ssolite.samples.client.struts2.service.LocalUserService;
import com.github.sahara3.ssolite.samples.client.struts2.service.UserNotFoundException;

import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Slf4j
public class SsoLiteLoginAction extends AbstractLoginAction {

    private static final long serialVersionUID = 1L;

    @Setter
    private String token;

    @Setter
    private String from;

    private final LocalUserService userService = LocalUserService.getInstance();

    // private static final MediaType JSON = MediaType.parse("application/json;
    // charset=utf-8");

    private final OkHttpClient httpClient = new OkHttpClient();

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected AuthToken authenticate() throws AuthException {
        String url = this.getText("ssolite.token-api-url") + "/" + this.token;
        SsoLiteAccessToken accessToken = this.retriveAccessToken(url);
        String username = accessToken.getUsername();

        // get the local user object via LocalUserService.
        try {
            LocalUser user = this.userService.findByName(username);
            return new AuthToken(user.getName(), "[SECRET]", true);
        }
        catch (UserNotFoundException e) {
            throw e;
        }
        catch (Exception e) {
            throw new AuthException(e.getMessage(), e);
        }
    }

    private SsoLiteAccessToken retriveAccessToken(@NonNull String url) throws AuthException {
        // call RESTful API to retrieve the username in the access token.
        LOG.debug("GET {}", url);

        Request request = new Request.Builder().get().url(url).build();
        try (Response response = this.httpClient.newCall(request).execute()) {
            LOG.debug("Token API response: {}", response);
            if (!response.isSuccessful()) {
                LOG.debug("Access token is not found, or HTTP error.");
                throw new BadPasswordException("Bad credentials.");
            }

            String json = response.body().string();
            return this.objectMapper.readValue(json, SsoLiteAccessToken.class);
        }
        catch (IOException e) {
            throw new AuthException("Token API error.", e);
        }
    }

    @Override
    protected String determineRedirectUrl(HttpSession session) {
        if (this.from != null) {
            return this.from;
        }
        return AuthInterceptor.getSavedRequestURL(session);
    }
}
