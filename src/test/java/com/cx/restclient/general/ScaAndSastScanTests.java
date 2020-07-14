package com.cx.restclient.general;

import com.cx.restclient.configuration.CxScanConfig;
import com.cx.restclient.dto.ScanResults;
import lombok.extern.slf4j.Slf4j;

import java.net.MalformedURLException;

@Slf4j
public class ScaAndSastScanTests extends AbstractScaScanTests {
    public void scanRemoteRepo(String repoUrlProp, boolean useOnPremAuthentication) throws MalformedURLException {
        CxScanConfig config = initScaConfig(repoUrlProp, useOnPremAuthentication);
        config = initSastConfig(config, "SastAndSca");
        
        ScanResults scanResults = runScan(config);
        verifyScanResults(scanResults);
    }
}
