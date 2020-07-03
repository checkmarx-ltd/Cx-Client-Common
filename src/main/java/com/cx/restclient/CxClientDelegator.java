package com.cx.restclient;

import com.cx.restclient.common.IScanner;
import com.cx.restclient.common.summary.SummaryUtils;
import com.cx.restclient.configuration.CxScanConfig;
import com.cx.restclient.dto.*;
import com.cx.restclient.exception.CxClientException;

import com.cx.restclient.osa.dto.OSAResults;
import com.cx.restclient.sast.dto.*;

import com.cx.restclient.sca.dto.SCAResults;

import org.slf4j.Logger;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;

import static com.cx.restclient.common.CxPARAM.*;
import static com.cx.restclient.cxArm.utils.CxARMUtils.getPoliciesNames;

/**
 * Created by Galn on 05/02/2018.
 */

public class CxClientDelegator {

    private static final String PRINT_LINE = "-----------------------------------------------------------------------------------------";

    private Logger log;
    private CxScanConfig config;
    
    
    Map<ScannerType,IScanner> scannersMap = new HashMap<>();
     
    public CxClientDelegator(CxScanConfig config, Logger log) throws MalformedURLException {

        this.config = config;
        this.log = log;


        if (config.getSastEnabled()) {
            scannersMap.put(ScannerType.SAST, new CxSASTClient(log, config));
        }

        if (config.getScannerType() == DependencyScannerType.OSA) {
            scannersMap.put(ScannerType.OSA, new CxOSAClient(log, config));
        } 
        else if (config.getScannerType() == DependencyScannerType.SCA) {
            scannersMap.put(ScannerType.SCA, new SCAClient(config, log));
        }
    }



    public CxClientDelegator(String serverUrl, String username, String password, String origin, boolean disableCertificateValidation, Logger log) throws MalformedURLException {
        this(new CxScanConfig(serverUrl, username, password, origin, disableCertificateValidation), log);
    }

    //API Scans methods
    public String getClientVersion() {
        String version = "";
        try {
            Properties properties = new Properties();
            java.io.InputStream is = getClass().getClassLoader().getResourceAsStream("common.properties");
            if (is != null) {
                properties.load(is);
                version = properties.getProperty("version");
            }
        } catch (Exception e) {
            throw new CxClientException(e.getMessage());
        }
        return version;
    }

    public void init()  {
        log.info("Initializing Cx client [" + getClientVersion() + "]");
        scannersMap.values().forEach(scanner->
            scanner.init()
        );
    }

  
    public ScanResults createScan()  {
   
        SASTResults sastResults = null;
        OSAResults osaResults = null;
        SCAResults scaResults = null;

        if(scannersMap.containsKey(ScannerType.SAST)){
            sastResults = (SASTResults)scannersMap.get(ScannerType.SAST).createScan();
        }

        if (scannersMap.containsKey(ScannerType.OSA)) {
            osaResults = (OSAResults)scannersMap.get(ScannerType.OSA).createScan();
        }

        if (scannersMap.containsKey(ScannerType.SCA)) {
            scaResults = (SCAResults)scannersMap.get(ScannerType.SCA).createScan();
        }

       return combineResults( sastResults, osaResults, scaResults);

    }

    private ScanResults combineResults(SASTResults sastResults, OSAResults osaResults, SCAResults scaResults) {
        ScanResults scanResults = new ScanResults();
        scanResults.setOsaResults(osaResults);
        scanResults.setScaResults(scaResults);
        scanResults.setSastResults(sastResults);
        return scanResults;
    }
    


    public ScanResults waitForScanResults() throws InterruptedException {

        SASTResults sastResults = null;
        OSAResults osaResults = null;
        SCAResults scaResults = null;

        if(scannersMap.containsKey(ScannerType.SAST)){
            sastResults = (SASTResults)scannersMap.get(ScannerType.SAST).waitForScanResults();
        }

        if (scannersMap.containsKey(ScannerType.OSA)) {
            osaResults = (OSAResults)scannersMap.get(ScannerType.OSA).waitForScanResults();
        }

        if (scannersMap.containsKey(ScannerType.SCA)) {
            scaResults = (SCAResults)scannersMap.get(ScannerType.SCA).waitForScanResults();
        }


        return combineResults( sastResults, osaResults, scaResults);
        
    }

    public ScanResults getLatestScanResults() throws InterruptedException {

        SASTResults sastResults = null;
        OSAResults osaResults = null;
        SCAResults scaResults = null;

        if(scannersMap.containsKey(ScannerType.SAST)){
            sastResults = (SASTResults)scannersMap.get(ScannerType.SAST).getLatestScanResults();
        }

        if (scannersMap.containsKey(ScannerType.OSA)) {
            osaResults = (OSAResults)scannersMap.get(ScannerType.OSA).getLatestScanResults();
        }

        if (scannersMap.containsKey(ScannerType.SCA)) {
            scaResults = (SCAResults)scannersMap.get(ScannerType.SCA).getLatestScanResults();
        }

        return combineResults( sastResults, osaResults, scaResults);

    }

    public void printIsProjectViolated(ScanResults scanResults ) {
        if (config.getEnablePolicyViolations()) {
            log.info(PRINT_LINE);
            log.info("Policy Management: ");
            log.info("--------------------");

            boolean hasOsaViolations = 
                    scanResults.getOsaResults() != null &&
                    scanResults.getOsaResults().getOsaPolicies() != null &&
                    !scanResults.getOsaResults().getOsaPolicies().isEmpty();
            
            boolean hasSastPolicies = false;
            
            if(scanResults.getSastResults() != null && !scanResults.getSastResults().getSastPolicies().isEmpty()) {
                hasSastPolicies = true;
            }

            if (!hasSastPolicies && !hasOsaViolations) {
                log.info(PROJECT_POLICY_COMPLIANT_STATUS);
                log.info(PRINT_LINE);
            } else {
                log.info(PROJECT_POLICY_VIOLATED_STATUS);
                if (hasSastPolicies) {
                    log.info("SAST violated policies names: " + getPoliciesNames(scanResults.getSastResults().getSastPolicies()));
                }
                if (hasOsaViolations) {
                    log.info("OSA violated policies names: " + getPoliciesNames(scanResults.getOsaResults().getOsaPolicies()));
                }
                log.info(PRINT_LINE);
            }
            
        }
    }


  

    public String generateHTMLSummary(ScanResults combinedResults) throws Exception {
        return SummaryUtils.generateSummary(combinedResults.getSastResults(), combinedResults.getOsaResults(), combinedResults.getScaResults(), config);
    }

    public String generateHTMLSummary(SASTResults sastResults, OSAResults osaResults, SCAResults scaResults) throws Exception {
        return SummaryUtils.generateSummary(sastResults, osaResults, scaResults, config);
    }

    public CxSASTClient getSastClient(){
        return (CxSASTClient)scannersMap.get(ScannerType.SAST);
    }

    public CxOSAClient getOsaClient(){
        return (CxOSAClient)scannersMap.get(ScannerType.OSA);
    }

    public SCAClient getScaClient(){
        return (SCAClient)scannersMap.get(ScannerType.SCA);
    }
    
    public void close() {

       scannersMap.values().forEach(scanner->
                scanner.close()
        );
    }
    
  
    public void login() throws IOException {
        getScanner().login();
    }
    
    
    private IScanner getScanner() {

        if(scannersMap.size()!=1){
            throw new CxClientException("Login is allowed when only one scanner is defined");
        }
        return scannersMap.get(0);
    }

  



  
 

  

  

}