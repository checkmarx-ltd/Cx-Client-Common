package com.cx.restclient.ast;

import com.cx.restclient.ast.dto.common.ASTConfig;
import com.cx.restclient.ast.dto.common.HandlerRef;
import com.cx.restclient.ast.dto.common.RemoteRepositoryInfo;
import com.cx.restclient.ast.dto.common.ScanConfig;
import com.cx.restclient.ast.dto.common.ScanConfigValue;
import com.cx.restclient.ast.dto.sast.AstSastConfig;
import com.cx.restclient.ast.dto.sast.AstSastResults;
import com.cx.restclient.ast.dto.sast.SastScanConfigValue;
import com.cx.restclient.ast.dto.sast.report.AstSastSummaryResults;
import com.cx.restclient.ast.dto.sast.report.Finding;
import com.cx.restclient.ast.dto.sast.report.ScanResultsResponse;
import com.cx.restclient.ast.dto.sast.report.SeverityCounter;
import com.cx.restclient.ast.dto.sast.report.SingleScanSummary;
import com.cx.restclient.ast.dto.sast.report.SummaryResponse;
import com.cx.restclient.common.Scanner;
import com.cx.restclient.common.UrlUtils;
import com.cx.restclient.configuration.CxScanConfig;
import com.cx.restclient.dto.LoginSettings;
import com.cx.restclient.dto.Results;
import com.cx.restclient.dto.ScannerType;
import com.cx.restclient.dto.SourceLocationType;
import com.cx.restclient.dto.scansummary.Severity;
import com.cx.restclient.exception.CxClientException;
import com.cx.restclient.exception.CxHTTPClientException;
import com.cx.restclient.httpClient.CxHttpClient;
import com.cx.restclient.httpClient.utils.ContentType;
import com.cx.restclient.osa.dto.ClientType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AstSastClient extends AstClient implements Scanner {
    private static final String ENGINE_TYPE_FOR_API = "sast";
    private static final String REF_TYPE_BRANCH = "branch";
    private static final String SUMMARY_PATH = "/api/scan-summary";     // NOSONAR: changes in these paths are very unlikely
    private static final String SCAN_RESULTS_PATH = "/api/results";     // NOSONAR
    private static final String AUTH_PATH = "/auth/realms/organization/protocol/openid-connect/token";     // NOSONAR
    private static final String URL_PARSING_EXCEPTION = "URL parsing exception.";

    private static final int DEFAULT_PAGE_SIZE = 1000;
    private static final int NO_FINDINGS_CODE = 4004;

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String API_VERSION = "*/*; version=0.1";

    private String scanId;

    public AstSastClient(CxScanConfig config, Logger log) {
        super(config, log);

        AstSastConfig astConfig = this.config.getAstSastConfig();
        validate(astConfig);

        // Make sure we won't get URLs like "http://example.com//api/scans".
        String normalizedUrl = StringUtils.stripEnd(astConfig.getApiUrl(), "/");

        httpClient = createHttpClient(normalizedUrl);
        httpClient.addCustomHeader(HttpHeaders.ACCEPT, API_VERSION);
    }

    @Override
    public void init() {
        log.debug("Initializing {} client.", getScannerDisplayName());
        try {
            ClientType clientType = getClientType();
            LoginSettings settings = getLoginSettings(clientType);
            httpClient.login(settings);
        } catch (IOException e) {
            super.handleInitError(e);
        }
    }

    private LoginSettings getLoginSettings(ClientType clientType) throws MalformedURLException {
        String authUrl = UrlUtils.parseURLToString(config.getAstSastConfig().getApiUrl(), AUTH_PATH);
        return LoginSettings.builder()
                .accessControlBaseUrl(authUrl)
                .clientTypeForPasswordAuth(clientType)
                .build();
    }

    private ClientType getClientType() {
        AstSastConfig astConfig = config.getAstSastConfig();
        return ClientType.builder()
                .clientId(astConfig.getClientId())
                .clientSecret(astConfig.getClientSecret())
                .scopes("ast-api")
                .grantType("client_credentials")
                .build();
    }

    @Override
    protected String getScannerDisplayName() {
        return ScannerType.AST_SAST.getDisplayName();
    }

    @Override
    public Results initiateScan() {
        log.info("----------------------------------- Initiating {} Scan:------------------------------------",
                getScannerDisplayName());

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
        } catch (Exception e) {
            CxClientException ex = new CxClientException("Error creating scan.", e);
            astResults.setCreateException(ex);
        }
        return astResults;
    }

    @Override
    protected ScanConfig getScanConfig() {
        String presetName = config.getAstSastConfig().getPresetName();
        if (StringUtils.isEmpty(presetName)) {
            throw new CxClientException("Scan preset must be specified.");
        }

        String isIncremental = Boolean.toString(config.getAstSastConfig().isIncremental());
        ScanConfigValue configValue = SastScanConfigValue.builder()
                .incremental(isIncremental)
                .presetName(presetName)
                .build();

        return ScanConfig.builder()
                .type(ENGINE_TYPE_FOR_API)
                .value(configValue)
                .build();
    }

    @Override
    protected HandlerRef getBranchToScan(RemoteRepositoryInfo repoInfo) {
        // We need to return this object even if no branch is specified in repoInfo.
        return HandlerRef.builder()
                .type(REF_TYPE_BRANCH)
                .value(repoInfo.getBranch())
                .build();
    }

    @Override
    public Results waitForScanResults() {
        AstSastResults result;
        try {
            waitForScanToFinish(scanId);
            result = retrieveScanResults();
        } catch (CxClientException e) {
            result = new AstSastResults();
            result.setWaitException(e);
        }
        return result;
    }

    private AstSastResults retrieveScanResults() {
        try {
            AstSastResults result = new AstSastResults();
            result.setScanId(scanId);

            AstSastSummaryResults scanSummary = getSummary();
            result.setSummary(scanSummary);

            List<Finding> findings = getFindings();
            result.setFindings(findings);

            return result;
        } catch (IOException e) {
            String message = String.format("Error getting %s scan results.", getScannerDisplayName());
            throw new CxClientException(message, e);
        }
    }

    private AstSastSummaryResults getSummary() {
        AstSastSummaryResults result = new AstSastSummaryResults();

        String summaryUrl = getRelativeSummaryUrl();
        SummaryResponse summaryResponse = getSummaryResponse(summaryUrl);

        SingleScanSummary nativeSummary = getNativeSummary(summaryResponse);
        setFindingCountsPerSeverity(nativeSummary.getSeverityCounters(), result);

        result.setStatusCounters(nativeSummary.getStatusCounters());
        result.setTotalCounter(nativeSummary.getTotalCounter());

        return result;
    }

    private List<Finding> getFindings() throws IOException {
        int offset = 0;
        int limit = config.getAstSastConfig().getResultsPageSize();
        if (limit <= 0) {
            limit = DEFAULT_PAGE_SIZE;
        }

        List<Finding> allFindings = new ArrayList<>();
        while (true) {
            String relativeUrl = getRelativeResultsUrl(offset, limit);
            ScanResultsResponse response = getScanResultsResponse(relativeUrl);
            List<Finding> findingsFromResponse = response.getResults();
            allFindings.addAll(findingsFromResponse);
            offset += findingsFromResponse.size();
            if (offset >= response.getTotalCount()) {
                break;
            }
        }

        if (log.isInfoEnabled()) {
            log.info(String.format("Total findings: %d", allFindings.size()));
        }

        return allFindings;
    }

    private ScanResultsResponse getScanResultsResponse(String relativeUrl) throws IOException {
        return httpClient.getRequest(relativeUrl,
                ContentType.CONTENT_TYPE_APPLICATION_JSON,
                ScanResultsResponse.class,
                HttpStatus.SC_OK,
                "retrieving scan results",
                false);
    }

    private SummaryResponse getSummaryResponse(String relativeUrl) {
        SummaryResponse result;
        try {
            result = httpClient.getRequest(relativeUrl,
                    ContentType.CONTENT_TYPE_APPLICATION_JSON,
                    SummaryResponse.class,
                    HttpStatus.SC_OK,
                    "retrieving scan summary",
                    false);
        } catch (Exception e) {
            result = getEmptySummaryIfApplicable(e);
        }
        return result;
    }

    private SummaryResponse getEmptySummaryIfApplicable(Exception e) {
        SummaryResponse result;
        if (noFindingsWereDetected(e)) {
            result = new SummaryResponse();
            result.getScansSummaries().add(new SingleScanSummary());
        } else {
            throw new CxClientException("Error getting scan summary.", e);
        }
        return result;
    }

    /**
     * When no findings are detected, AST-SAST API returns the 404 status with a specific
     * error code, which is quite awkward.
     * Response example: {"code":4004,"message":"can't find all the provided scan ids","data":null}
     *
     * @return true: scan completed successfully and the result contains no findings (normal flow).
     * false: some other error has occurred (error flow).
     */
    private boolean noFindingsWereDetected(Exception e) {
        boolean result = false;
        if (e instanceof CxHTTPClientException) {
            CxHTTPClientException httpException = (CxHTTPClientException) e;
            if (httpException.getStatusCode() == HttpStatus.SC_NOT_FOUND &&
                    StringUtils.isNotEmpty(httpException.getResponseBody())) {
                try {
                    JsonNode body = objectMapper.readTree(httpException.getResponseBody());
                    result = (body.get("code").asInt() == NO_FINDINGS_CODE);
                } catch (Exception parsingException) {
                    log.warn("Error parsing the 'Not found' response.", parsingException);
                }
            }
        }
        return result;
    }

    private String getRelativeResultsUrl(int offset, int limit) {
        try {
            String result = new URIBuilder()
                    .setPath(SCAN_RESULTS_PATH)
                    .setParameter("scan-id", scanId)
                    .setParameter("offset", Integer.toString(offset))
                    .setParameter("limit", Integer.toString(limit))
                    .build()
                    .toString();

            if (log.isDebugEnabled()) {
                log.debug(String.format("Getting findings from %s", result));
            }

            return result;
        } catch (URISyntaxException e) {
            throw new CxClientException(URL_PARSING_EXCEPTION, e);
        }
    }

    private String getRelativeSummaryUrl() {
        try {
            String result = new URIBuilder()
                    .setPath(SUMMARY_PATH)
                    .setParameter("scan-ids", scanId)
                    .build()
                    .toString();

            if (log.isDebugEnabled()) {
                log.debug(String.format("Getting summary from %s", result));
            }

            return result;
        } catch (URISyntaxException e) {
            throw new CxClientException(URL_PARSING_EXCEPTION, e);
        }
    }

    private static void setFindingCountsPerSeverity(List<SeverityCounter> nativeCounters, AstSastSummaryResults target) {
        if (nativeCounters == null) {
            return;
        }

        for (SeverityCounter counter : nativeCounters) {
            Severity parsedSeverity = EnumUtils.getEnum(Severity.class, counter.getSeverity());
            int value = counter.getCounter();
            if (parsedSeverity != null) {
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

    private static SingleScanSummary getNativeSummary(SummaryResponse summaryResponse) {
        return Optional.ofNullable(summaryResponse).map(SummaryResponse::getScansSummaries)
                // We are sending a single scan ID in the request and therefore expect exactly 1 scan summary.
                .filter(scanSummaries -> scanSummaries.size() == 1)
                .map(scanSummaries -> scanSummaries.get(0))
                .orElseThrow(() -> new CxClientException("Invalid summary response."));
    }

    @Override
    public Results getLatestScanResults() {
        AstSastResults result = new AstSastResults();
        result.setWaitException(new UnsupportedOperationException());
        return result;
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
