package com.github.sahara3.ssolite.samples.client.spring.controller;

import java.util.Arrays;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Web MVC controller for the client.
 *
 * @author sahara3
 */
@Controller
public class WebMvcController {

    private static final Logger LOG = LoggerFactory.getLogger(WebMvcController.class);

    /**
     * Local login page.
     *
     * <p>If an user has logged in, this method returns a redirect URL for the top page.</p>
     *
     * @param request        must not be null.
     * @param authentication not null for logged in users.
     * @param model          must not be null.
     * @return &quot;login&quot; for the login page, or redirect URL.
     */
    @GetMapping(path = "/login")
    public String localLogin(HttpServletRequest request, Authentication authentication, Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
            LOG.debug("Already logged in. Redirect to '/'");
            return "redirect:/";
        }

        // not logged in.
        Arrays.asList("error", "logout").forEach(key -> model.addAttribute(key, request.getParameter(key)));

        String from = request.getParameter("from");
        model.addAttribute("from", from);

        return "login";
    }

    /**
     * @return &quot;index&quot; for the index page.
     */
    @GetMapping(path = "/")
    public String index() {
        return "index";
    }
}
