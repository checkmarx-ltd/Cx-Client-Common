package com.cx.restclient;

import com.cx.restclient.common.IScanner;
import com.cx.restclient.common.summary.SummaryUtils;
import com.cx.restclient.configuration.CxScanConfig;
import com.cx.restclient.dto.*;
import com.cx.restclient.exception.CxClientException;
import com.cx.restclient.osa.dto.ClientType;
import com.cx.restclient.osa.dto.OSAResults;
import com.cx.restclient.sast.dto.*;
import com.cx.restclient.sast.utils.LegacyClient;
import com.cx.restclient.sca.dto.SCAResults;
import org.apache.http.cookie.Cookie;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;

import static com.cx.restclient.common.CxPARAM.*;
import static com.cx.restclient.cxArm.utils.CxARMUtils.getPoliciesNames;

/**
 * Created by Galn on 05/02/2018.
 */
//SHRAGA
//System Holistic Rest Api Generic Application
public class CxClientDelegator {

    private static final String PRINT_LINE = "-----------------------------------------------------------------------------------------";


    private Logger log;
    private CxScanConfig config;
    

    private CxOSAClient osaClient;
    private SCAClient scaClient;
    private CxSASTClient sastClient;
    
     
    public CxClientDelegator(CxScanConfig config, Logger log) throws MalformedURLException, CxClientException {

        this.config = config;
        this.log = log;


        if (config.getSastEnabled()) {
            sastClient = new CxSASTClient(log, config);
        }

        if (config.getScannerType() == DependencyScannerType.OSA) {
            osaClient = new CxOSAClient( log, config);
        } else if (config.getScannerType() == DependencyScannerType.SCA) {
            scaClient = new SCAClient(config, log);
        }
    }



    public CxClientDelegator(String serverUrl, String username, String password, String origin, boolean disableCertificateValidation, Logger log) throws MalformedURLException, CxClientException {
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

        }
        return version;
    }

    public void init() throws CxClientException, IOException {
        log.info("Initializing Cx client [" + getClientVersion() + "]");
        getScannerList().forEach(scanner->{
            scanner.getHttpClient().close();
        });
    }

  
    public ScanResults createScan() throws CxClientException {
   
        SASTResults sastResults = null;
        OSAResults osaResults = null;
        SCAResults scaResults = null;
        
        if(sastClient !=null) {
            sastResults = (SASTResults)sastClient.createScan();
        }
        
        if (osaClient != null) {
            osaResults = (OSAResults)osaClient.createScan();
        }

        if (scaClient != null) {
            scaResults = (SCAResults)scaClient.createScan();
        }

       return combineResults( sastResults, osaResults, scaResults);

    }

    private ScanResults combineResults(SASTResults sastResults, OSAResults osaResults, SCAResults scaResults) {
        ScanResults scanResults = new ScanResults();
        DependencyScanResults dependencyScanResults = new DependencyScanResults();
        dependencyScanResults.setOsaResults(osaResults);
        dependencyScanResults.setScaResults(scaResults);
        scanResults.setSastResults(sastResults);
        scanResults.setDependencyScanResults(dependencyScanResults);
        return scanResults;
    }

//    public String createDependencyScan() throws CxClientException {
//        String scanId = getDependencyClient().createScan(dependencyScanResults);
//        return scanId;
//    }

    public void cancelSASTScan() throws IOException, CxClientException {
        sastClient.cancelSASTScan();
    }

//    public SASTResults waitForSASTResults() throws InterruptedException, CxClientException, IOException {
//        sastResults = getSastClient().waitForSASTResults(sastScanId, projectId);
//        return sastResults;
//    }

    public ScanResults waitForScanResults() throws InterruptedException, CxClientException, IOException {

        SASTResults sastResults = null;
        OSAResults osaResults = null;
        SCAResults scaResults = null;

        if(sastClient !=null) {
            sastResults = (SASTResults)sastClient.waitForScanResults();
        }

        if (osaClient != null) {
            osaResults = (OSAResults)osaClient.waitForScanResults();
        }

        if (scaClient != null) {
            scaResults = (SCAResults)scaClient.waitForScanResults();
        }

        return combineResults( sastResults, osaResults, scaResults);
        
    }

    public IResults getLatestScanResults() throws  CxClientException, InterruptedException {

        SASTResults sastResults = null;
        OSAResults osaResults = null;
        SCAResults scaResults = null;

        if(sastClient !=null) {
            sastResults = (SASTResults)sastClient.getLatestScanResults();
        }

        if (osaClient != null) {
            osaResults = (OSAResults)osaClient.getLatestScanResults();
        }

        if (scaClient != null) {
            scaResults = (SCAResults)scaClient.getLatestScanResults();
        }

        return combineResults( sastResults, osaResults, scaResults);

    }
//    public DependencyScanResults waitForDependencyScanResults() throws CxClientException {
//        getDependencyClient().waitForScanResults(dependencyScanResults);
//        return dependencyScanResults;
//    }

//    public ScanResults getLatestDependencyScanResults() throws CxClientException {
//        DependencyScanResults dependencyScanResults = getDependencyClient().getLatestScanResults();
//        return dependencyScanResults;
//    }

    public void printIsProjectViolated(ScanResults scanResults ) {
        if (config.getEnablePolicyViolations()) {
            log.info(PRINT_LINE);
            log.info("Policy Management: ");
            log.info("--------------------");

            boolean hasOsaViolations = scanResults.getDependencyScanResults() != null &&
                    scanResults.getDependencyScanResults().getOsaResults() != null &&
                    scanResults.getDependencyScanResults().getOsaResults().getOsaPolicies() != null &&
                    !scanResults.getDependencyScanResults().getOsaResults().getOsaPolicies().isEmpty();
            
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
                    log.info("OSA violated policies names: " + getPoliciesNames(scanResults.getDependencyScanResults().getOsaResults().getOsaPolicies()));
                }
                log.info(PRINT_LINE);
            }
            
        }
    }

    /**
     * @param config The following config properties are used:
     *               scaConfig
     *               proxyConfig
     *               cxOrigin
     *               disableCertificateValidation
     */
    public static void testScaConnection(CxScanConfig config, Logger log) throws CxClientException {
        SCAClient client = new SCAClient(config, log);
        try {
            client.testConnection();
        } catch (IOException e) {
            throw new CxClientException(e);
        }
    }

  

    public String generateHTMLSummary(ScanResults combinedResults) throws Exception {
        return SummaryUtils.generateSummary(combinedResults.getSastResults(), combinedResults.getDependencyScanResults(), config);
    }

    public String generateHTMLSummary(SASTResults sastResults, DependencyScanResults dependencyScanResults) throws Exception {
        return SummaryUtils.generateSummary(sastResults, dependencyScanResults, config);
    }

  

    public void close() {

       getScannerList().forEach(scanner->{
                scanner.getHttpClient().close();
        });
    }

    
    private List<IScanner> getScannerList(){
        
        List scanners = new LinkedList<IScanner>();
                
        if(sastClient !=null) {
            scanners.add(sastClient);
        }
        if (osaClient != null) {
            scanners.add(osaClient);
        }
        if (scaClient != null) {
            scanners.add(scaClient);
        }
        return scanners;
    }
    //HELP config  Methods

    

    public String getTokenLegacyClient() throws IOException, CxClientException {
        LegacyClient legacyClient = getLegacyClient();
        LoginSettings settings = legacyClient.getDefaultLoginSettings();
        settings.setClientTypeForPasswordAuth(ClientType.CLI);
        final TokenLoginResponse tokenLoginResponse = legacyClient.getHttpClient().generateToken(settings);
        return tokenLoginResponse.getRefresh_token();
    }

    public void revokeToken(String token) throws IOException, CxClientException {
        getScanner().getHttpClient().revokeToken(token);
    }
    

    public List<Team> getTeamList() throws IOException, CxClientException {
        
        return getLegacyClient().getTeamList();

    }

    public void login() throws IOException, CxClientException {
        getScanner().login();
    }

    public void loginLegacy(String version) throws IOException, CxClientException {
        getLegacyClient().login(version);
    }
    
    private IScanner getScanner() {
        List<IScanner> scanners = getScannerList();

        if(getScannerList().size()!=1){
            throw new CxClientException("Login is allowed when only one scanner is defined");
        }
        return scanners.get(0);
    }

    private LegacyClient getLegacyClient() {

        if(sastClient!= null) {
            return  sastClient;
        }
        if(osaClient != null) {
            return  osaClient;
        }
        throw new UnsupportedOperationException();
    }




    public void setOsaFSAProperties(Properties fsaConfig) {  //For CxMaven plugin
        config.setOsaFsaConfig(fsaConfig);
    }
    //Private methods

  
 

  

  

    public ResponseQueueScanStatus getStatus(String scanId) throws IOException {
        return sastClient.getSASTScanStatus(scanId);
    }



    public ScanSettingResponse getScanSetting(Long projectId) throws IOException {
        return sastClient.getScanSetting(projectId);
    }

    public List<LastScanResponse> getLastScansByProjectId(long projectId) throws IOException {
        return sastClient.getLatestSASTStatus(projectId);
    }

    public List<Cookie> ssoLegacyLogin(){
        return sastClient.getHttpClient().ssoLegacyLogin();
    }
}