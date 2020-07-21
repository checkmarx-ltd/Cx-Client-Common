package com.cx.restclient.ast;

import com.cx.restclient.ast.dto.common.ASTConfig;
import com.cx.restclient.ast.dto.common.AstSastResults;
import com.cx.restclient.ast.dto.common.AstSastSummaryResults;
import com.cx.restclient.ast.dto.common.HandlerRef;
import com.cx.restclient.ast.dto.common.RemoteRepositoryInfo;
import com.cx.restclient.ast.dto.common.ScanConfig;
import com.cx.restclient.ast.dto.common.ScanConfigValue;
import com.cx.restclient.ast.dto.sast.AstSastConfig;
import com.cx.restclient.ast.dto.sast.SastScanConfigValue;
import com.cx.restclient.ast.dto.sast.report.ScansSummary;
import com.cx.restclient.ast.dto.sast.report.SeverityCounter;
import com.cx.restclient.ast.dto.sast.report.Summary;
import com.cx.restclient.common.Scanner;
import com.cx.restclient.configuration.CxScanConfig;
import com.cx.restclient.dto.Results;
import com.cx.restclient.dto.ScanResults;
import com.cx.restclient.dto.ScannerType;
import com.cx.restclient.dto.SourceLocationType;
import com.cx.restclient.dto.scansummary.Severity;
import com.cx.restclient.exception.CxClientException;
import com.cx.restclient.httpClient.CxHttpClient;
import com.cx.restclient.httpClient.utils.ContentType;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AUTH;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

public class AstSastClient extends AstClient implements Scanner {
    private static final String ENGINE_TYPE_FOR_API = "sast";
    private static final String REF_TYPE_BRANCH = "branch";
    private static final String SUMMARY_PATH = "/api/scan-summary";
    private static final String SCAN_ID_PARAM = "scan-ids";

    private String scanId;

    public AstSastClient(CxScanConfig config, Logger log) {
        super(config, log);

        AstSastConfig astConfig = this.config.getAstSastConfig();
        validate(astConfig);

        // Make sure we won't get URLs like "http://example.com//api/scans".
        String normalizedUrl = StringUtils.stripEnd(astConfig.getApiUrl(), "/");

        httpClient = createHttpClient(normalizedUrl);
    }

    @Override
    public void init() {
        log.debug(String.format("Initializing %s client.", getScannerDisplayName()));
        AstSastConfig astConfig = config.getAstSastConfig();
        httpClient.addCustomHeader(AUTH.WWW_AUTH_RESP, String.format("Bearer %s", astConfig.getAccessToken()));
    }

    @Override
    protected String getScannerDisplayName() {
        return ScannerType.AST_SAST.getDisplayName();
    }

    @Override
    public Results initiateScan() {
        log.info(String.format("----------------------------------- Initiating %s Scan:------------------------------------",
                getScannerDisplayName()));

        AstSastResults astResults = new AstSastResults();
        scanId = null;

        AstSastConfig astConfig = config.getAstSastConfig();
        try {
            SourceLocationType locationType = astConfig.getSourceLocationType();
            HttpResponse response;
            if (locationType == SourceLocationType.REMOTE_REPOSITORY) {
                response = submitSourcesFromRemoteRepo(astConfig, config.getProjectName());
            } else {
                throw new NotImplementedException("The upload flow is not yet supported.");
            }
            scanId = extractScanIdFrom(response);
            astResults.setScanId(scanId);
            return astResults;
        } catch (IOException e) {
            throw new CxClientException("Error creating scan.", e);
        }
    }

    @Override
    protected ScanConfig getScanConfig() {
        boolean isIncremental = Boolean.TRUE.equals(config.getAstSastConfig().isIncremental());

        if (StringUtils.isEmpty(config.getPresetName())) {
            throw new CxClientException("Scan preset must be specified.");
        }

        ScanConfigValue configValue = SastScanConfigValue.builder()
                .incremental(Boolean.toString(isIncremental))
                .presetName(config.getPresetName())
                .build();

        return ScanConfig.builder()
                .type(ENGINE_TYPE_FOR_API)
                .value(configValue)
                .build();
    }

    @Override
    protected HandlerRef getBranchToScan(RemoteRepositoryInfo repoInfo) {
        if (StringUtils.isEmpty(repoInfo.getBranch())) {
            String message = String.format("Branch must be specified for the %s scan.", getScannerDisplayName());
            throw new CxClientException(message);
        }

        return HandlerRef.builder()
                .type(REF_TYPE_BRANCH)
                .value(repoInfo.getBranch())
                .build();
    }

    @Override
    public Results waitForScanResults() {
        waitForScanToFinish(scanId);
        AstSastResults result;
        try {
            result = retrieveScanResults();
        } catch (IOException e) {
            String message = String.format("Error getting %s scan results.", getScannerDisplayName());
            throw new CxClientException(message, e);
        }
        return result;
    }

    private AstSastResults retrieveScanResults() throws IOException {
        AstSastResults result = new AstSastResults();
        result.setScanId(scanId);
        AstSastSummaryResults scanSummary = getSummaryReport();
        result.setSummary(scanSummary);
        return result;
    }

    private AstSastSummaryResults getSummaryReport() throws IOException {
        AstSastSummaryResults result = new AstSastSummaryResults();

        String summaryUrl = getRelativeSummaryUrl();
        Summary summaryResponse = getSummaryResponse(summaryUrl);

        ScansSummary nativeSummary = getNativeSummary(summaryResponse);
        setFindingCountsPerSeverity(nativeSummary.getSeverityCounters(), result);

        result.setStatusCounters(nativeSummary.getStatusCounters());

        int total = Optional.ofNullable(nativeSummary.getTotalCounter()).orElse(0);
        result.setTotalCounter(total);

        return result;
    }

    private Summary getSummaryResponse(String relativeUrl) throws IOException {
        return httpClient.getRequest(relativeUrl,
                    ContentType.CONTENT_TYPE_APPLICATION_JSON,
                    Summary.class,
                    HttpStatus.SC_OK,
                    "retrieving scan summary",
                    false);
    }

    private String getRelativeSummaryUrl() {
        String relativeUrl;
        try {
            relativeUrl = new URIBuilder()
                    .setPath(SUMMARY_PATH)
                    .setParameter(SCAN_ID_PARAM, scanId)
                    .build()
                    .toString();
        } catch (URISyntaxException e) {
            throw new CxClientException("URL parsing exception.", e);
        }
        return relativeUrl;
    }

    private static void setFindingCountsPerSeverity(List<SeverityCounter> nativeCounters, AstSastSummaryResults target) {
        if (nativeCounters == null) {
            return;
        }

        for (SeverityCounter counter : nativeCounters) {
            Severity parsedSeverity = EnumUtils.getEnum(Severity.class, counter.getSeverity());
            Integer value = counter.getCounter();
            if (parsedSeverity != null && value != null) {
                if (parsedSeverity == Severity.HIGH) {
                    target.setHighVulnerabilityCount(value);
                } else if (parsedSeverity == Severity.MEDIUM) {
                    target.setMediumVulnerabilityCount(value);
                } else if (parsedSeverity == Severity.LOW) {
                    target.setLowVulnerabilityCount(value);
                }
            }
        }
    }

    private static ScansSummary getNativeSummary(Summary summaryResponse) {
        return Optional.ofNullable(summaryResponse).map(Summary::getScansSummaries)
                // We are sending a single scan ID in the request and therefore expect exactly 1 scan summary.
                .filter(scanSummaries -> scanSummaries.size() == 1)
                .map(scanSummaries -> scanSummaries.get(0))
                .orElseThrow(() -> new CxClientException("Invalid summary response."));
    }

    @Override
    public ScanResults getLatestScanResults() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() {
        Optional.ofNullable(httpClient).ifPresent(CxHttpClient::close);
    }

    private void validate(ASTConfig astSastConfig) {
        log.debug("Validating config.");
        String error = null;
        if (astSastConfig == null) {
            error = "%s config must be provided.";
        } else if (StringUtils.isBlank(astSastConfig.getApiUrl())) {
            error = "%s API URL must be provided.";
        }

        if (error != null) {
            throw new IllegalArgumentException(String.format(error, getScannerDisplayName()));
        }
    }
}
