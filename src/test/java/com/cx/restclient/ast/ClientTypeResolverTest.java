package com.cx.restclient.ast;

import com.cx.restclient.general.CommonClientTest;
import com.cx.restclient.osa.dto.ClientType;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;

public class ClientTypeResolverTest extends CommonClientTest {
    @Test
    public void determineClientType_cloudAccessControl() {
        ClientTypeResolver resolver = new ClientTypeResolver();
        ClientType clientType = resolver.determineClientType(prop("astSca.cloud.accessControlUrl"));
        Assert.assertNotNull("Client type is null.", clientType);
        Assert.assertTrue("Client ID is empty.", StringUtils.isNotEmpty(clientType.getClientId()));
        Assert.assertTrue("Scopes are empty.", StringUtils.isNotEmpty(clientType.getScopes()));
    }
}