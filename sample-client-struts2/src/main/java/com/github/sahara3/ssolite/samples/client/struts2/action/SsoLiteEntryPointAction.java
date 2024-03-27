package com.github.sahara3.ssolite.samples.client.struts2.action;

import java.io.Serial;
import java.nio.charset.StandardCharsets;
import javax.servlet.http.HttpServletRequest;

import com.opensymphony.xwork2.ActionSupport;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.struts2.action.ServletRequestAware;

import com.github.sahara3.ssolite.core.util.LegacySsoLiteRedirectUrlBuilder;
import com.github.sahara3.ssolite.core.util.SsoLiteUriUtils;

@Slf4j
public class SsoLiteEntryPointAction extends ActionSupport implements ServletRequestAware {

    @Serial
    private static final long serialVersionUID = 1L;

    private HttpServletRequest servletRequest;

    @Override
    public void withServletRequest(HttpServletRequest request) {
        this.servletRequest = request;
    }

    @Getter
    private String redirectUrl;

    private final LegacySsoLiteRedirectUrlBuilder urlBuilder = new LegacySsoLiteRedirectUrlBuilder();

    @Override
    public String execute() throws Exception {
        String loginUrl = this.getText("ssolite.client.login-url");
        boolean sameDomain = Boolean.getBoolean(this.getText("ssolite.same-domain"));
        this.redirectUrl = this.buildRedirectUrl(loginUrl, sameDomain);
        return "redirect";
    }

    private String buildRedirectUrl(@NonNull String loginUrl, boolean sameDomain) {
        String from = this.generateFromUrl(sameDomain);
        LOG.debug("buildRedirectUrl: url={}, from={}", loginUrl, from);

        this.redirectUrl = this.urlBuilder.buildRedirectUrl(this.servletRequest, loginUrl);

        // append "from".
        if (from != null) {
            String encoded = SsoLiteUriUtils.encodeQueryParam(from, StandardCharsets.UTF_8);
            this.redirectUrl += "?from=" + encoded;
        }

        LOG.debug("Redirect URL: {}", this.redirectUrl);
        return this.redirectUrl;
    }

    private String generateFromUrl(boolean sameDomain) {
        assert this.servletRequest != null;

        if (sameDomain) {
            String from = this.servletRequest.getRequestURI();
            String query = this.servletRequest.getQueryString();
            if (query != null) {
                from += "?" + query;
            }
            return from;
        }

        // not a same domain.
        StringBuffer from = this.servletRequest.getRequestURL();
        String query = this.servletRequest.getQueryString();
        if (query != null) {
            from.append('?').append(query);
        }
        return from.toString();
    }
}
