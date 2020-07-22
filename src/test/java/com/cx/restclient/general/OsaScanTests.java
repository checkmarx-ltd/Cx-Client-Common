package com.cx.restclient.general;

import com.cx.restclient.configuration.CxScanConfig;
import com.cx.restclient.dto.ScanResults;
import com.cx.restclient.exception.CxClientException;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.net.MalformedURLException;

@Slf4j
public class OsaScanTests extends CommonClientTest {
    @Test
    public void runOsaScan() throws MalformedURLException, CxClientException {
        CxScanConfig config = initOsaConfig(new CxScanConfig(), "osaOnlyScan");
        ScanResults results = runScan(config);
        Assert.assertNull(results.getScaResults());
        Assert.assertNotNull(results.getOsaResults());
        Assert.assertNotNull("Expected valid osa scan id", results.getOsaResults().getOsaScanId());
    }
}
