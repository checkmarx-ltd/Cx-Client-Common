package com.cx.restclient;

import com.cx.restclient.ast.AstSastClient;
import com.cx.restclient.ast.AstScaClient;
import com.cx.restclient.ast.dto.sca.AstScaResults;
import com.cx.restclient.common.Scanner;
import com.cx.restclient.common.summary.SummaryUtils;
import com.cx.restclient.configuration.CxScanConfig;
import com.cx.restclient.configuration.PropertyFileLoader;
import com.cx.restclient.dto.Results;
import com.cx.restclient.dto.ScanResults;
import com.cx.restclient.dto.ScannerType;
import com.cx.restclient.osa.dto.OSAResults;
import com.cx.restclient.sast.dto.SASTResults;
import com.cx.restclient.sast.utils.State;
import org.slf4j.Logger;

import java.net.MalformedURLException;
import java.util.EnumMap;
import java.util.Map;

import static com.cx.restclient.common.CxPARAM.*;
import static com.cx.restclient.cxArm.utils.CxARMUtils.getPoliciesNames;

/**
 * Created by Galn on 05/02/2018.
 */

public class CxClientDelegator implements Scanner {
    private static final PropertyFileLoader properties = PropertyFileLoader.getDefaultInstance();

    private static final String PRINT_LINE = "-----------------------------------------------------------------------------------------";

    private Logger log;
    private CxScanConfig config;

    Map<ScannerType, Scanner> scannersMap = new EnumMap<>(ScannerType.class);

    public CxClientDelegator(CxScanConfig config, Logger log) throws MalformedURLException {

        this.config = config;
        this.log = log;
        if (config.isAstSastEnabled()) {
            scannersMap.put(ScannerType.AST_SAST, new AstSastClient(config, log));
        }

        if (config.isSastEnabled()) {
            scannersMap.put(ScannerType.SAST, new CxSASTClient(config, log));
        }

        if (config.isOsaEnabled()) {
            scannersMap.put(ScannerType.OSA, new CxOSAClient(config, log));
        }

        if (config.isAstScaEnabled()) {
            scannersMap.put(ScannerType.AST_SCA, new AstScaClient(config, log));
        }
    }


    public CxClientDelegator(String serverUrl, String username, String password, String origin, boolean disableCertificateValidation, Logger log) throws MalformedURLException {
        this(new CxScanConfig(serverUrl, username, password, origin, disableCertificateValidation), log);
    }

    @Override
    public ScanResults init() {
        log.info("Initializing Cx client [{}]", properties.get("version"));
        ScanResults scanResultsCombined = new ScanResults();

        scannersMap.forEach((key, scanner) -> {
            Results scanResults = scanner.init();
            scanResultsCombined.put(key, scanResults);
        });

        return scanResultsCombined;
    }


    @Override
    public ScanResults initiateScan() {

        ScanResults scanResultsCombined = new ScanResults();

        scannersMap.forEach((key, scanner) -> {
            if (scanner.getState() == State.SUCCESS) {
                Results scanResults = scanner.initiateScan();
                scanResultsCombined.put(key, scanResults);
            }
        });

        return scanResultsCombined;
    }


    @Override
    public ScanResults waitForScanResults() {

        ScanResults scanResultsCombined = new ScanResults();

        scannersMap.forEach((key, scanner) -> {
            if (scanner.getState() == State.SUCCESS) {
                Results scanResults = scanner.waitForScanResults();
                scanResultsCombined.put(key, scanResults);
            }
        });

        return scanResultsCombined;
    }

    @Override
    public ScanResults getLatestScanResults() {

        ScanResults scanResultsCombined = new ScanResults();

        scannersMap.forEach((key, scanner) -> {
            if (scanner.getState() == State.SUCCESS) {
                Results scanResults = scanner.getLatestScanResults();
                scanResultsCombined.put(key, scanResults);
            }
        });

        return scanResultsCombined;

    }

    public void printIsProjectViolated(ScanResults scanResults) {
        if (config.getEnablePolicyViolations()) {
            log.info(PRINT_LINE);
            log.info("Policy Management: SAST and OSA ");
            log.info("--------------------");

            OSAResults osaResults = (OSAResults) scanResults.get(ScannerType.OSA);
            SASTResults sastResults = (SASTResults) scanResults.get(ScannerType.SAST);

            boolean hasOsaViolations =
                    osaResults != null &&
                            osaResults.getOsaPolicies() != null &&
                            !osaResults.getOsaPolicies().isEmpty();

            boolean hasSastPolicies = false;

            if (sastResults != null && sastResults.getSastPolicies() != null && !sastResults.getSastPolicies().isEmpty()) {
                hasSastPolicies = true;
            }            

            if (!hasSastPolicies && !hasOsaViolations) {
                log.info(PROJECT_POLICY_COMPLIANT_STATUS_SAST);
                log.info(PRINT_LINE);
            } else {
                log.info(PROJECT_POLICY_VIOLATED_STATUS_SAST);
                if (hasSastPolicies) {
                    log.info("SAST violated policies names: {}", getPoliciesNames(sastResults.getSastPolicies()));
                }
                if (hasOsaViolations) {
                    log.info("OSA violated policies names: {}", getPoliciesNames(osaResults.getOsaPolicies()));
                }
                log.info(PRINT_LINE);
            }
        }
        if (config.getEnablePolicyViolationsSCA()) {
            log.info(PRINT_LINE);
            log.info("Policy Management: SCA ");
            log.info("--------------------");

            AstScaResults scaResults = (AstScaResults) scanResults.get(ScannerType.AST_SCA);

            boolean hasScaViolations = false;
            if (scaResults != null && scaResults.getPolicyEvaluations() != null && !scaResults.getPolicyEvaluations().isEmpty()) {
                hasScaViolations = true;
            }

            if (!hasScaViolations) {
                log.info(PROJECT_POLICY_COMPLIANT_STATUS_SCA);
                log.info(PRINT_LINE);
            } else {
                log.info(PROJECT_POLICY_VIOLATED_STATUS_SCA);
                if (hasScaViolations) {
                    log.info("SCA policies are violated.");
                }
                log.info(PRINT_LINE);
            }
        }
    }


    public String generateHTMLSummary(ScanResults combinedResults) throws Exception {

        return SummaryUtils.generateSummary(
                (SASTResults) combinedResults.get(ScannerType.SAST),
                (OSAResults) combinedResults.get(ScannerType.OSA),
                (AstScaResults) combinedResults.get(ScannerType.AST_SCA), config);
    }

    public String generateHTMLSummary(SASTResults sastResults, OSAResults osaResults, AstScaResults scaResults) throws Exception {
        return SummaryUtils.generateSummary(sastResults, osaResults, scaResults, config);
    }

    public CxSASTClient getSastClient() {
        return (CxSASTClient) scannersMap.get(ScannerType.SAST);
    }

    public CxOSAClient getOsaClient() {
        return (CxOSAClient) scannersMap.get(ScannerType.OSA);
    }

    public AstScaClient getScaClient() {
        return (AstScaClient) scannersMap.get(ScannerType.AST_SCA);
    }

    public void close() {
        scannersMap.values().forEach(Scanner::close);
    }


}