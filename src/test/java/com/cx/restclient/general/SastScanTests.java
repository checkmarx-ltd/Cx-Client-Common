package com.cx.restclient.general;

import com.cx.restclient.configuration.CxScanConfig;
import com.cx.restclient.dto.ScanResults;
import com.cx.restclient.exception.CxClientException;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.net.MalformedURLException;

@Slf4j
public class SastScanTests extends CommonClientTest {
    private static final String PROJECT_NAME = "sastOnlyScan";

    @Test
    public void runSastScan() throws MalformedURLException, CxClientException {
        CxScanConfig config = initSastConfig(new CxScanConfig(), PROJECT_NAME);
        runSastScan(config);
    }

    @Test
    @Ignore("There is no stable environment for this test")
    public void runSastScanWithProxy() throws MalformedURLException, CxClientException {
        CxScanConfig config = initSastConfig(new CxScanConfig(), PROJECT_NAME);
        setProxy(config);
        runSastScan(config);
    }

    private void runSastScan(CxScanConfig config) throws MalformedURLException, CxClientException {
        ScanResults results = runScan(config);
        Assert.assertNotEquals("Expected valid SAST scan id", 0, results.getSastResults().getScanId());
    }
}
