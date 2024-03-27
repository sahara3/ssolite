package com.github.sahara3.ssolite.spring.jackson2;

import java.io.Serial;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;

import com.github.sahara3.ssolite.spring.client.SsoLiteAccessTokenAuthenticationToken;

/**
 * Jackson2 module for SSOLite.
 *
 * @author sahara3
 */
public class SsoLiteJackson2Module extends SimpleModule {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Module version.
     */
    public static final Version VERSION = new Version(1, 0, 0, null, null, null);

    /**
     * Constructor.
     */
    public SsoLiteJackson2Module() {
        super(SsoLiteJackson2Module.class.getName(), VERSION);
    }

    @Override
    public void setupModule(SetupContext context) {
        context.setMixInAnnotations(SsoLiteAccessTokenAuthenticationToken.class,
                SsoLiteAccessTokenAuthenticationTokenMixin.class);
    }
}
