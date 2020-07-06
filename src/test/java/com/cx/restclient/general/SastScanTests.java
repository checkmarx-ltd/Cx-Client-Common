package com.cx.restclient.general;

import com.cx.restclient.CxClientDelegator;
import com.cx.restclient.configuration.CxScanConfig;

import com.cx.restclient.dto.ScannerType;
import com.cx.restclient.exception.CxClientException;
import com.cx.restclient.sast.dto.SASTResults;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.net.MalformedURLException;

@Ignore
@Slf4j
public class SastScanTests extends CommonClientTest {
    @Test
    public void runSastScan() throws MalformedURLException, CxClientException {
        CxScanConfig config = initSastConfig();
        runSastScan(config);
    }

    @Test
    public void runSastScanWithProxy() throws MalformedURLException, CxClientException {
        CxScanConfig config = initSastConfig();
        setProxy(config);
        runSastScan(config);
    }

    private void runSastScan(CxScanConfig config) throws MalformedURLException, CxClientException {
        CxClientDelegator client = new CxClientDelegator(config, log);
        try {
            client.init();
            client.createScan();
            SASTResults results =  client.waitForScanResults().getSastResults();
            Assert.assertNotNull(results);
            Assert.assertNotEquals("Expected valid SAST scan id", 0, results.getScanId());
        } catch (Exception e) {
            failOnException(e);
        }
    }

    private CxScanConfig initSastConfig() {
        CxScanConfig config = new CxScanConfig();
        config.setReportsDir(new File("C:\\report"));
        config.setSourceDir(props.getProperty("sastSource"));
        config.setUsername(props.getProperty("username"));
        config.setPassword(props.getProperty("password"));
        config.setUrl(props.getProperty("serverUrl"));
        config.setCxOrigin("common");
        config.setProjectName("sastOnlyScan");
        config.setPresetName("Default");
        config.setTeamPath("\\CxServer");
        config.setSynchronous(true);
        config.setGeneratePDFReport(true);
        config.addScannerType(ScannerType.SAST);
        config.setPresetName("Default");
//        config.setPresetId(7);

        return config;
    }
}
