package com.github.sahara3.ssolite.spring.client;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

/**
 * Authentication success handler for SSOLite client.
 *
 * @author sahara3
 */
public class SsoLiteClientAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        String next = request.getParameter("next");
        if (next == null) {
            super.onAuthenticationSuccess(request, response, authentication);
            return;
        }

        this.clearAuthenticationAttributes(request);
        this.logger.debug("Redirect URL: " + next);
        this.getRedirectStrategy().sendRedirect(request, response, next);
    }
}
