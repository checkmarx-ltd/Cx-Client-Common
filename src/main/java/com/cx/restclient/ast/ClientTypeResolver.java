package com.cx.restclient.ast;

import com.cx.restclient.osa.dto.ClientType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClientTypeResolver {
    private static final String CLOUD_ACCESS_CONTROL_BASE_URL = "https://platform.checkmarx.net";

    public ClientType determineClientType(String discoveryBaseUrl) {
        boolean isAccessControlInCloud = (discoveryBaseUrl != null &&
                discoveryBaseUrl.startsWith(CLOUD_ACCESS_CONTROL_BASE_URL));

        log.info(isAccessControlInCloud ? "Using cloud authentication." : "Using on-premise authentication.");

        return isAccessControlInCloud ? ClientType.SCA_CLI : ClientType.RESOURCE_OWNER;
    }
}
