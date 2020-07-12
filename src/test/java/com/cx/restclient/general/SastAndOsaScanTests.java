package com.cx.restclient.general;

import com.cx.restclient.configuration.CxScanConfig;
import com.cx.restclient.dto.ScanResults;
import com.cx.restclient.exception.CxClientException;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.net.MalformedURLException;

@Ignore
@Slf4j
public class SastAndOsaScanTests extends CommonClientTest {
    
    @Test
    public void runSastAndOsaScan() throws MalformedURLException, CxClientException {
        String projectName = "SastAndOsa";
        CxScanConfig config = initSastConfig(projectName);
        config = initOsaConfig(config,projectName);
        
        ScanResults results = runScan(config);
        
        Assert.assertNull(results.getScaResults());
        Assert.assertNotNull(results.getOsaResults());
        System.out.println("Osa scan ID: " + results.getOsaResults().getOsaScanId());
        System.out.println("Osa Vulnerabilities: " + results.getOsaResults().getOsaVulnerabilities());
        System.out.println("Osa Libraries:" + results.getOsaResults().getOsaLibraries());
        Assert.assertNotNull("Expected valid osa scan id", results.getOsaResults().getOsaScanId());

        Assert.assertNotNull(results.getSastResults());
        System.out.println("Sast scan ID: " + results.getSastResults().getScanId());
        System.out.println("Sast High: " + results.getSastResults().getHigh() + "Sast Medium: " + results.getSastResults().getMedium());

        Assert.assertNotNull("Expected valid osa scan id", results.getOsaResults().getOsaScanId());
    }
    

    protected ScanResults runScan(CxScanConfig config) throws MalformedURLException, CxClientException {
        ScanResults results = super.runScan(config);
        Assert.assertNotEquals("Expected valid SAST scan id", 0, results.getSastResults().getScanId());
        return results;
    }

 
}
