package com.github.sahara3.ssolite.spring.jackson2;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.MissingNode;
import org.springframework.security.core.GrantedAuthority;

import com.github.sahara3.ssolite.spring.client.SsoLiteAccessTokenAuthenticationToken;

/**
 * Jackson2 deserializer for {@link SsoLiteAccessTokenAuthenticationToken}.
 *
 * @author sahara3
 */
public class SsoLiteAccessTokenAuthenticationTokenDeserializer
        extends JsonDeserializer<SsoLiteAccessTokenAuthenticationToken> {

    @Override
    @SuppressWarnings("boxing")
    public SsoLiteAccessTokenAuthenticationToken deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        SsoLiteAccessTokenAuthenticationToken token = null;
        ObjectMapper mapper = (ObjectMapper) jp.getCodec();
        JsonNode jsonNode = mapper.readTree(jp);

        Boolean authenticated = readJsonNode(jsonNode, "authenticated").asBoolean();

        JsonNode principalNode = readJsonNode(jsonNode, "principal");
        Object principal = null;
        if (principalNode.isObject()) {
            principal = mapper.readValue(principalNode.traverse(mapper), Object.class);
        }
        else {
            principal = principalNode.asText();
        }

        JsonNode credentialsNode = readJsonNode(jsonNode, "credentials");
        Object credentials;
        if (credentialsNode.isNull() || credentialsNode.isMissingNode()) {
            credentials = null;
        }
        else {
            credentials = credentialsNode.asText();
        }

        if (authenticated) {
            List<GrantedAuthority> authorities =
                    mapper.readValue(readJsonNode(jsonNode, "authorities").traverse(mapper),
                            mapper.getTypeFactory().constructParametricType(List.class, GrantedAuthority.class));
            token = new SsoLiteAccessTokenAuthenticationToken(principal, credentials, authorities);
        }
        else {
            if (credentials == null) {
                throw new JsonMappingException(jp, "credentials must not be null when not authenticated.");
            }
            token = new SsoLiteAccessTokenAuthenticationToken((String) credentials);
        }

        JsonNode detailsNode = readJsonNode(jsonNode, "details");
        if (detailsNode.isNull() || detailsNode.isMissingNode()) {
            token.setDetails(null);
        }
        else {
            token.setDetails(detailsNode);
        }
        return token;
    }

    private static JsonNode readJsonNode(JsonNode jsonNode, String field) {
        return jsonNode.has(field) ? jsonNode.get(field) : MissingNode.getInstance();
    }
}
