package com.cx.restclient.general;

import com.cx.restclient.CxClientDelegator;
import com.cx.restclient.configuration.CxScanConfig;

import com.cx.restclient.dto.ScanResults;
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
        ScanResults results = runScan(config);
        Assert.assertNotEquals("Expected valid SAST scan id", 0, results.getSastResults().getScanId());
    }

 
}
