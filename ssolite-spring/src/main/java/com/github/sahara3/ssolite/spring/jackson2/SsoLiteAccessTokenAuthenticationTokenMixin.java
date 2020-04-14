package com.github.sahara3.ssolite.spring.jackson2;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.sahara3.ssolite.spring.client.SsoLiteAccessTokenAuthenticationToken;

/**
 * Jackson2 mix-in for {@link SsoLiteAccessTokenAuthenticationToken}.
 *
 * @author sahara3
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
@JsonAutoDetect( //
        fieldVisibility = JsonAutoDetect.Visibility.ANY, //
        getterVisibility = JsonAutoDetect.Visibility.NONE, //
        isGetterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonDeserialize(using = SsoLiteAccessTokenAuthenticationTokenDeserializer.class)
class SsoLiteAccessTokenAuthenticationTokenMixin {
    // no member.
}
