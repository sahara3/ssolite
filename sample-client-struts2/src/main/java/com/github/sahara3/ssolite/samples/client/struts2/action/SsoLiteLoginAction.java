package com.github.sahara3.ssolite.samples.client.struts2.action;

import javax.servlet.http.HttpSession;

import com.github.sahara3.ssolite.core.model.SsoLiteAccessToken;
import com.github.sahara3.ssolite.samples.client.struts2.interceptor.AuthInterceptor;
import com.github.sahara3.ssolite.samples.client.struts2.model.AuthToken;
import com.github.sahara3.ssolite.samples.client.struts2.model.LocalUser;
import com.github.sahara3.ssolite.samples.client.struts2.service.AuthException;
import com.github.sahara3.ssolite.samples.client.struts2.service.LocalUserService;
import com.github.sahara3.ssolite.samples.client.struts2.service.SsoLiteAccessTokenService;
import com.github.sahara3.ssolite.samples.client.struts2.service.UserNotFoundException;

import lombok.Setter;

public class SsoLiteLoginAction extends AbstractLoginAction {

    private static final long serialVersionUID = 1L;

    @Setter
    private String token;

    @Setter
    private String from;

    private final LocalUserService userService = LocalUserService.getInstance();

    private final SsoLiteAccessTokenService ssoLiteAccessTokenService = SsoLiteAccessTokenService.getInstance();

    @Override
    protected AuthToken authenticate() throws AuthException {
        String url = this.getText("ssolite.token-api-url") + "/" + this.token;
        SsoLiteAccessToken accessToken = this.ssoLiteAccessTokenService.retriveAccessToken(url);

        try {
            // get the local user object via LocalUserService.
            LocalUser user = this.userService.findByName(accessToken.getUsername());
            return new AuthToken(user.getName(), "[SECRET]", true);
        }
        catch (UserNotFoundException e) {
            throw e;
        }
        catch (Exception e) {
            throw new AuthException(e.getMessage(), e);
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
