package com.cx.restclient.ast;

import com.cx.restclient.exception.CxClientException;
import com.cx.restclient.osa.dto.ClientType;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Slf4j
public class ClientTypeResolver {
    private static final String WELL_KNOWN_CONFIG_PATH = "identity/.well-known/openid-configuration";
    private static final String SCOPES_JSON_PROP = "scopes_supported";

    private static final Set<String> scopesForCloudAuth = new HashSet<>(Arrays.asList("sca_api", "offline_access"));
    private static final Set<String> scopesForOnPremAuth = new HashSet<>(Arrays.asList("sast_rest_api", "cxarm_api"));

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Determines which scopes and client secret must be used for SCA login.
     *
     * @param accessControlServerBaseUrl used to determine scopes supported by this server.
     * @return client settings for the provided AC server.
     */
    public ClientType determineClientType(String accessControlServerBaseUrl) {
        String fullUrl = getFullUrl(accessControlServerBaseUrl);
        JsonNode response = getConfigResponse(fullUrl);
        Set<String> supportedScopes = getSupportedScopes(response);
        Set<String> scopesToUse = getScopesForAuth(supportedScopes);

        String clientSecret = scopesToUse.equals(scopesForOnPremAuth) ? ClientType.RESOURCE_OWNER.getClientSecret() : "";

        String scopesForRequest = String.join(" ", scopesToUse);

        return ClientType.builder().clientId(ClientType.RESOURCE_OWNER.getClientId())
                .scopes(scopesForRequest)
                .clientSecret(clientSecret)
                .build();
    }

    private Set<String> getScopesForAuth(Set<String> supportedScopes) {
        Set<String> result;
        if (supportedScopes.containsAll(scopesForCloudAuth)) {
            result = scopesForCloudAuth;
        } else if (supportedScopes.containsAll(scopesForOnPremAuth)) {
            result = scopesForOnPremAuth;
        } else {
            String message = String.format("Access control server doesn't support the necessary scopes (either %s or %s)." +
                            " It only supports the following scopes: %s.",
                    scopesForCloudAuth,
                    scopesForOnPremAuth,
                    supportedScopes);

            throw new CxClientException(message);
        }
        log.debug(String.format("Using scopes: %s", result));
        return result;
    }

    private JsonNode getConfigResponse(String fullUrl) {
        HttpGet request = new HttpGet(fullUrl);
        try (CloseableHttpClient apacheClient = HttpClients.createDefault();
             CloseableHttpResponse response = apacheClient.execute(request)) {
            return objectMapper.readTree(response.getEntity().getContent());
        } catch (Exception e) {
            throw new CxClientException("Error getting OpenID config response.", e);
        }
    }

    private static Set<String> getSupportedScopes(JsonNode response) {
        Set<String> result = null;
        if (response != null) {
            TypeReference<Set<String>> typeRef = new TypeReference<Set<String>>() {
            };
            result = objectMapper.convertValue(response.get(SCOPES_JSON_PROP), typeRef);
        }
        return Optional.ofNullable(result).orElse(new HashSet<>());
    }

    private static String getFullUrl(String baseUrl) {
        String result = StringUtils.appendIfMissing(baseUrl, "/") + WELL_KNOWN_CONFIG_PATH;
        log.debug(String.format("Using OpenID configuration URL: %s", result));
        return result;

    }
}
