package com.cx.restclient.general;

import com.cx.restclient.CxClientDelegator;
import com.cx.restclient.configuration.CxScanConfig;
import com.cx.restclient.dto.DependencyScannerType;
import com.cx.restclient.dto.ScanResults;
import com.cx.restclient.exception.CxClientException;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.net.MalformedURLException;

@Slf4j
public class OsaScanTests extends CommonClientTest {
    @Test
    public void runOsaScan() throws MalformedURLException, CxClientException {
        CxScanConfig config = initOsaConfig();
        CxClientDelegator client = new CxClientDelegator(config, log);
        try {
            client.init();
            client.createScan();
            ScanResults results = client.waitForScanResults();
            Assert.assertNotNull(results);
            Assert.assertNull(results.getDependencyScanResults().getScaResults());
            Assert.assertNotNull(results.getDependencyScanResults().getOsaResults());
            Assert.assertNotNull("Expected valid osa scan id", results.getDependencyScanResults().getOsaResults().getOsaScanId());
        } catch (Exception e) {
            failOnException(e);
        }
    }

    private CxScanConfig initOsaConfig() {
        CxScanConfig config = new CxScanConfig();
        config.setDependencyScannerType(DependencyScannerType.OSA);
        config.setSastEnabled(false);
        config.setSourceDir(props.getProperty("dependencyScanSourceDir"));
        config.setReportsDir(new File("C:\\report"));
        config.setUrl(props.getProperty("serverUrl"));
        config.setUsername(props.getProperty("username"));
        config.setPassword(props.getProperty("password"));

        config.setCxOrigin("common");
        config.setProjectName("osaOnlyScan");
        config.setPresetName("Default");
        config.setTeamPath("\\CxServer");
        config.setSynchronous(true);
        config.setGeneratePDFReport(true);

        config.setOsaRunInstall(true);
        config.setOsaThresholdsEnabled(true);
        config.setPublic(true);

        return config;
    }
}
