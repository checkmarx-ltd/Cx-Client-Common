package com.cx.restclient.ast;

import com.cx.restclient.configuration.CxScanConfig;
import com.cx.restclient.exception.CxClientException;
import com.cx.restclient.httpClient.CxHttpClient;
import com.cx.restclient.osa.dto.ClientType;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static com.cx.restclient.httpClient.utils.ContentType.CONTENT_TYPE_APPLICATION_JSON_V1;

public class ClientTypeResolver {
    private static final String WELL_KNOWN_CONFIG_PATH = "identity/.well-known/openid-configuration";
    private static final String SCOPES_JSON_PROP = "scopes_supported";

    private static final Set<String> scopesForCloudAuth = new HashSet<>(Arrays.asList("sca_api", "offline_access"));
    private static final Set<String> scopesForOnPremAuth = new HashSet<>(Arrays.asList("sast_rest_api", "cxarm_api"));

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private CxHttpClient httpClient;

    private CxScanConfig config;
    
    static Logger log;

    public ClientTypeResolver(CxScanConfig config) {
        this.config = config;
    }
    public ClientTypeResolver(CxScanConfig config, Logger logger) {
        this.config = config;
        this.log = logger;
        
        log.info(config.toString());
        System.out.println(config.toString());
        
    }

    /**
     * Determines which scopes and client secret must be used for SCA login.
     *
     * @param accessControlServerBaseUrl used to determine scopes supported by this server.
     * @return client settings for the provided AC server.
     */
    public ClientType determineClientType(String accessControlServerBaseUrl) {
    	log.info("ClientTypeResolver determineClientType accessControlServerBaseUrl:"+accessControlServerBaseUrl);
    	System.out.println("ClientTypeResolver determineClientType accessControlServerBaseUrl:"+accessControlServerBaseUrl);
        JsonNode response = getConfigResponse(accessControlServerBaseUrl);
        log.info("ClientTypeResolver determineClientType response:"+response.toPrettyString());
    	System.out.println("ClientTypeResolver determineClientType response:"+response.toPrettyString());
        Set<String> supportedScopes = getSupportedScopes(response);
        log.info("ClientTypeResolver determineClientType supportedScopes:"+(supportedScopes==null?"returned null":supportedScopes.toString()));
    	System.out.println("ClientTypeResolver determineClientType supportedScopes:"+(supportedScopes==null?"returned null":supportedScopes.toString()));
        Set<String> scopesToUse = getScopesForAuth(supportedScopes);
        log.info("ClientTypeResolver determineClientType scopesToUse:"+(scopesToUse==null?"returned null":scopesToUse.toString()));
    	System.out.println("ClientTypeResolver determineClientType scopesToUse:"+(scopesToUse==null?"returned null":scopesToUse.toString()));

        String clientSecret = scopesToUse.equals(scopesForOnPremAuth) ? ClientType.RESOURCE_OWNER.getClientSecret() : "";
        log.info("ClientTypeResolver determineClientType clientSecret:"+(clientSecret==null?"is null":"not null"));
    	System.out.println("ClientTypeResolver determineClientType clientSecret:"+(clientSecret==null?"is null":"not null"));

        String scopesForRequest = String.join(" ", scopesToUse);
        log.info("ClientTypeResolver determineClientType scopesForRequest:"+(scopesForRequest==null?"is null":scopesForRequest));
    	System.out.println("ClientTypeResolver determineClientType scopesForRequest:"+(scopesForRequest==null?"is null":scopesForRequest));
    	
    	return ClientType.builder().clientId(ClientType.RESOURCE_OWNER.getClientId())
                .scopes(scopesForRequest)
                .clientSecret(clientSecret)
                .build();
    }

    private Set<String> getScopesForAuth(Set<String> supportedScopes) {
        Set<String> result;
        if (supportedScopes.containsAll(scopesForCloudAuth)) {
        	log.info("ClientTypeResolver getScopesForAuth containsAll scopesForCloudAuth");
        	System.out.println("ClientTypeResolver getScopesForAuth containsAll scopesForCloudAuth");
            result = scopesForCloudAuth;
        } else if (supportedScopes.containsAll(scopesForOnPremAuth)) {
        	log.info("ClientTypeResolver getScopesForAuth containsAll scopesForOnPremAuth");
        	System.out.println("ClientTypeResolver getScopesForAuth containsAll scopesForOnPremAuth");
            result = scopesForOnPremAuth;
        } else {
            String message = String.format("Access control server doesn't support the necessary scopes (either %s or %s)." +
                            " It only supports the following scopes: %s.",
                    scopesForCloudAuth,
                    scopesForOnPremAuth,
                    supportedScopes);
            log.info("ClientTypeResolver getScopesForAuth ERROR:"+message);
        	System.out.println("ClientTypeResolver getScopesForAuth ERROR:"+message);
            throw new CxClientException(message);
        }
        log.debug(String.format("Using scopes: %s", result));
        return result;
    }

    private JsonNode getConfigResponse(String accessControlServerBaseUrl) {
        try {
            String res = getHttpClient(accessControlServerBaseUrl).getRequest(WELL_KNOWN_CONFIG_PATH, CONTENT_TYPE_APPLICATION_JSON_V1, String.class, 200, "Get openId configuration", false);
            log.info("ClientTypeResolver getConfigResponse res:"+res);
        	System.out.println("ClientTypeResolver getConfigResponse res:"+res);
            return objectMapper.readTree(res);
        } catch (Exception e) {
        	log.error(e.getMessage());
        	log.info("ClientTypeResolver getScopesForAuth ERROR:"+e.getMessage());
        	System.out.println("ClientTypeResolver getScopesForAuth ERROR:"+e.getMessage());
        	e.printStackTrace();
            throw new CxClientException("Error getting OpenID config response.", e);
        }
    }

    private CxHttpClient getHttpClient(String acBaseUrl) {
        if (httpClient == null) {
        	log.info("ClientTypeResolver getHttpClient httpClient is null");
        	System.out.println("ClientTypeResolver getHttpClient httpClient is null");
            httpClient = new CxHttpClient(
                    StringUtils.appendIfMissing(acBaseUrl, "/"),
                    config.getCxOrigin(),
                    config.getCxOriginUrl(),
                    config.isDisableCertificateValidation(),
                    config.isUseSSOLogin(),
                    config.getRefreshToken(),
                    config.isScaProxy(),
                    config.getScaProxyConfig(),
                    log,
                    config.getNTLM());
        }
        return httpClient;
    }

    private static Set<String> getSupportedScopes(JsonNode response) {
        Set<String> result = null;
        if (response != null) {
            TypeReference<Set<String>> typeRef = new TypeReference<Set<String>>() {
            };
            log.info("ClientTypeResolver getSupportedScopes before objecMapper convertValue");
        	System.out.println("ClientTypeResolver getSupportedScopes before objecMapper convertValue");
            result = objectMapper.convertValue(response.get(SCOPES_JSON_PROP), typeRef);
            log.info("ClientTypeResolver getSupportedScopes after objecMapper convertValue");
        	System.out.println("ClientTypeResolver getSupportedScopes after objecMapper convertValue");
        }
        return Optional.ofNullable(result).orElse(new HashSet<>());
    }

}
