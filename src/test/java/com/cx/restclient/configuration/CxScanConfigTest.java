package com.cx.restclient.configuration;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

public class CxScanConfigTest {
    private Logger logUnitTests = LoggerFactory.getLogger("CxCommonClient Unit tests ");
    private CxScanConfig cxScanConfig = new CxScanConfig();

    String url;
    String username;
    String password;
    String cxOrigin;
    boolean disableCertificateValidation = false;
    private CxScanConfig cxScanConfigWithParameters = new CxScanConfig(url, username, password, cxOrigin, disableCertificateValidation);

    @Test
    public void CxScanConfig() {
        String url = "http://XX.XX.XX.XX";
        String username = "userName";
        String password = "password";
        String cxOrigin = "cxOrigin";
        boolean disableCertificateValidation = false;
        CxScanConfig cxScanConfigWithParameters = new CxScanConfig(url, username, password, cxOrigin, disableCertificateValidation);

        assertEquals("Incorrect URL", cxScanConfigWithParameters.getUrl(), url);
        assertEquals("Incorrect userName", cxScanConfigWithParameters.getUsername(), username);
        assertEquals("Incorrect password", cxScanConfigWithParameters.getPassword(), password);
        assertEquals("Incorrect CxOrigin", cxScanConfigWithParameters.getCxOrigin(), cxOrigin);
        assertEquals("Incorrect disableCertificateValidation", cxScanConfigWithParameters.isDisableCertificateValidation(), disableCertificateValidation);
    }


    @Test
    public void getSetSastEnabled() {

        logUnitTests.info("Current test validate that we get correct values from  getSastEnabled method\n");

        logUnitTests.info("1 test --> Set 'setSastEnabled' to value 'false' and validate that get 'false' value\n");
        cxScanConfig.setSastEnabled(false);
        assertEquals("I expected to get 'false' value but got different value", cxScanConfig.isSastEnabled(), false);


        logUnitTests.info("2 test --> Set 'setSastEnabled' to value 'true' and validate that get 'true' value\n");
        cxScanConfig.setSastEnabled(true);
        assertEquals("I expected to get 'true' value but got different value", cxScanConfig.isSastEnabled(), true);


        logUnitTests.info("3 test --> Negative test - Set 'setSastEnabled' to value 'true' and validate that get 'false' value\n");
        cxScanConfig.setSastEnabled(true);
        assertEquals("Negative test - expected to see different values", !(cxScanConfig.isSastEnabled()), false);


        logUnitTests.info("4 test --> Negative test - Set 'setSastEnabled' to value 'false' and validate that get 'true' value\n");
        cxScanConfig.setSastEnabled(false);
        assertEquals("Negative test - expected to see different values", !(cxScanConfig.isSastEnabled()), true);

    }

    @Test
    public void getSetCxOrigin() {

        logUnitTests.info("Current test validate that we get correct values from  getCxOrigin method\n");

        logUnitTests.info("1 test --> Set 'setCxOrigin' to value ANY String value and validate that getCxOrigin method will return correct value\n");
        String cxOriginValue = "SetCxOriginValueUnitTest";
        cxScanConfig.setCxOrigin(cxOriginValue);
        assertEquals("I expected to get" + cxOriginValue + " value but got different value", cxScanConfig.getCxOrigin(), cxOriginValue);

        //TODO current test failed because we allow to set empty string
        logUnitTests.info("2 test --> Set 'setCxOrigin' to value to EMPTY String and validate that getCxOrigin method will return correct value\n");
        cxScanConfig.setCxOrigin("  ");
        assertEquals("I expected to get '  ' value but got different value", cxScanConfig.getCxOrigin().trim().isEmpty(), true);

        logUnitTests.info("3 test --> Set 'setCxOrigin' to value to NOT STRING and validate that getCxOrigin method throw error\n");
        cxScanConfig.setCxOrigin(null);
        assertNull("Did not Get NULL value", cxScanConfig.getCxOrigin());

    }
}