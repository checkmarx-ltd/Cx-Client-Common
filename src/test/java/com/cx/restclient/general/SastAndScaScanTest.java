package com.cx.restclient.general;

import com.cx.restclient.configuration.CxScanConfig;
import com.cx.restclient.dto.ScanResults;
import lombok.extern.slf4j.Slf4j;
import org.junit.Ignore;
import org.junit.Test;

import java.net.MalformedURLException;

@Slf4j
public class SastAndScaScanTest extends ScaTestsBase {
    //TODO : fix this test
    @Test
    @Ignore("this test fails and needs to be fixed")
    public void scan_remotePublicRepo() throws MalformedURLException {
        scanRemoteRepo(PUBLIC_REPO_PROP, false);
    }

    //TODO : fix this test
    @Test
    @Ignore("this test fails and needs to be fixed")
    public void scan_remotePrivateRepo() throws MalformedURLException {
        scanRemoteRepo(PRIVATE_REPO_PROP, false);
    }

    private void scanRemoteRepo(String repoUrlProp, boolean useOnPremAuthentication) throws MalformedURLException {
        CxScanConfig config = initScaConfig(repoUrlProp, useOnPremAuthentication);
        config = initSastConfig(config, "SastAndSca");
        
        ScanResults scanResults = runScan(config);
        verifyScanResults(scanResults);
    }
}
