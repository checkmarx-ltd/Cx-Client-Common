package com.cx.restclient.ast;

import com.cx.restclient.exception.CxClientException;
import com.cx.restclient.general.CommonClientTest;
import com.cx.restclient.osa.dto.ClientType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;


@Slf4j
public class ClientTypeResolverTest extends CommonClientTest {
    @Test
    public void determineClientType_cloudAccessControl() {
        testDetermineClientType("astSca.cloud.accessControlUrl");
    }

    //TODO : fix this test
    @Test
    @Ignore("this test fails and needs to be fixed")
    public void determineClientType_onPremAccessControl() {
        testDetermineClientType("astSca.onPremise.accessControlUrl");
    }

    @Test
    public void determineClientType_invalidServer() {
        checkThatExceptionIsThrown("https://example.com");
    }

    @Test
    public void determineClientType_invalidUrlFormat() {
        checkThatExceptionIsThrown("incorrect!url?format");
    }

    private static void checkThatExceptionIsThrown(String url) {
        ClientTypeResolver resolver = new ClientTypeResolver();
        try {
            resolver.determineClientType(url);
            Assert.fail("Expected exception, but didn't get any.");
        } catch (Exception e) {
            log.info("Got an exception", e);
            Assert.assertTrue("Unexpected exception type.", e instanceof CxClientException);
            Assert.assertTrue("Exception message is empty.", StringUtils.isNotEmpty(e.getMessage()));
        }
    }

    private static void testDetermineClientType(String urlPropName) {
        ClientTypeResolver resolver = new ClientTypeResolver();
        ClientType clientType = resolver.determineClientType(prop(urlPropName));
        Assert.assertNotNull("Client type is null.", clientType);
        Assert.assertTrue("Client ID is empty.", StringUtils.isNotEmpty(clientType.getClientId()));
        Assert.assertTrue("Scopes are empty.", StringUtils.isNotEmpty(clientType.getScopes()));
    }
}