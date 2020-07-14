package com.cx.restclient.ast;

import com.cx.restclient.common.UrlUtils;
import com.cx.restclient.exception.CxClientException;
import com.cx.restclient.osa.dto.ClientType;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Slf4j
public class ClientTypeResolver {
    private static final Set<String> scopesForCloudAuth = new HashSet<>(Arrays.asList("sca_api", "offline_access"));
    private static final Set<String> scopesForOnPremAuth = new HashSet<>(Arrays.asList("sast_rest_api", "cxarm_api"));
    private static final String WELL_KNOWN_CONFIG_PATH = "identity/.well-known/openid-configuration";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public ClientType determineClientType(String discoveryBaseUrl) {
        String fullUrl = getFullUrl(discoveryBaseUrl);
        JsonNode response = getConfigResponse(fullUrl);
        Set<String> scopesSupported = getScopesSupported(response);
        Set<String> scopesForAuth;
        String clientSecret;
        if (scopesSupported.containsAll(scopesForCloudAuth)) {
            log.info("Using cloud authentication.");
            scopesForAuth = scopesForCloudAuth;
            clientSecret = "";
        } else if (scopesSupported.containsAll(scopesForOnPremAuth)) {
            log.info("Using on-premise authentication.");
            scopesForAuth = scopesForOnPremAuth;
            clientSecret = ClientType.RESOURCE_OWNER.getClientSecret();
        } else {
            String message = String.format("Access control server doesn't support the necessary scopes (either %s or %s)." +
                            " It only supports the following scopes: %s.",
                    scopesForCloudAuth,
                    scopesForOnPremAuth,
                    scopesSupported);

            throw new CxClientException(message);
        }

        String scopesForRequest = String.join(" ", scopesForAuth);
        return new ClientType(ClientType.RESOURCE_OWNER.getClientId(), scopesForRequest, clientSecret);
    }

    private JsonNode getConfigResponse(String fullUrl) {
        HttpGet request = new HttpGet(fullUrl);
        try (CloseableHttpClient apacheClient = HttpClients.createDefault()) {
            HttpResponse response = apacheClient.execute(request);
            return objectMapper.readTree(response.getEntity().getContent());
        } catch (Exception e) {
            throw new CxClientException("Error getting OpenID config response.", e);
        }
    }

    private static Set<String> getScopesSupported(JsonNode response) {
        Set<String> result = null;
        if (response != null) {
            TypeReference<Set<String>> ref = new TypeReference<Set<String>>() {
            };
            result = objectMapper.convertValue(response.get("scopes_supported"), ref);
        }
        return Optional.ofNullable(result).orElse(new HashSet<>());
    }

    private static String getFullUrl(String baseUrl) {
        try {
            String result = UrlUtils.parseURLToString(baseUrl, WELL_KNOWN_CONFIG_PATH);
            log.debug("Using discovery URL: {}", result);
            return result;
        } catch (MalformedURLException e) {
            String message = String.format("Invalid base URL for OpenID configuration: %s", baseUrl);
            throw new CxClientException(message, e);
        }
    }
}
