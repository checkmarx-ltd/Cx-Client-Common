package com.cx.restclient.general;

import com.cx.restclient.CxClientDelegator;
import com.cx.restclient.configuration.CxScanConfig;
import com.cx.restclient.exception.CxClientException;
import com.cx.restclient.sca.dto.SCAConfig;
import com.cx.utility.TestingUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.MalformedURLException;

@Slf4j
public class ConnectionTests extends CommonClientTest {

    @Test
    public void ssoConnectionTest() {
        CxScanConfig config = initConfig();
        try {
            CxClientDelegator client = new CxClientDelegator(config, log);
            client.init();
        } catch (IOException | CxClientException e) {
            e.printStackTrace();
            log.error("Error running  osa scan: " + e.getMessage());
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void scaConnectionTest() {
        CxScanConfig config = new CxScanConfig();
        config.setCxOrigin("common");
        SCAConfig scaConfig = TestingUtils.getScaConfig(props, false);
        config.setScaConfig(scaConfig);
        try {
            CxClientDelegator delegator = new CxClientDelegator(config, log);
            delegator.getScaClient().testScaConnection(config, log);
        } catch (CxClientException | MalformedURLException e) {
            failOnException(e);
        }
    }

    private CxScanConfig initConfig() {
        CxScanConfig config = new CxScanConfig();
        config.setSastEnabled(true);
        config.setUseSSOLogin(true);
        config.setUsername(props.getProperty("username"));
        config.setPassword(props.getProperty("password"));
        config.setUrl(props.getProperty("serverUrl"));
        config.setCxOrigin("common");

        return config;
    }


}
