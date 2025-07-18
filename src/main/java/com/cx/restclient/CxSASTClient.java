package com.cx.restclient;

import com.cx.restclient.common.Scanner;
import com.cx.restclient.common.ShragaUtils;
import com.cx.restclient.common.Waiter;
import com.cx.restclient.configuration.CxScanConfig;
import com.cx.restclient.dto.*;
import com.cx.restclient.dto.Status;
import com.cx.restclient.exception.CxClientException;
import com.cx.restclient.exception.CxHTTPClientException;
import com.cx.restclient.sast.dto.*;
import com.cx.restclient.sast.utils.LegacyClient;
import com.cx.restclient.sast.utils.SASTUtils;
import com.cx.restclient.sast.utils.State;
import com.cx.restclient.sast.utils.zip.CxZipUtils;
import com.google.gson.Gson;
import org.apache.http.HttpEntity;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.awaitility.core.ConditionTimeoutException;
import org.json.JSONObject;
import org.slf4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.cx.restclient.cxArm.dto.CxProviders.SAST;
import static com.cx.restclient.cxArm.utils.CxARMUtils.getProjectViolatedPolicies;
import static com.cx.restclient.httpClient.utils.ContentType.*;
import static com.cx.restclient.httpClient.utils.HttpClientHelper.convertToJson;
import static com.cx.restclient.sast.utils.SASTParam.*;
import static com.cx.restclient.sast.utils.SASTUtils.*;


/**
 * Created by Galn on 05/02/2018.
 */
public class CxSASTClient extends LegacyClient implements Scanner {

    public static final String JENKINS = "jenkins";

    private int reportTimeoutSec = 5000;
    private int cxARMTimeoutSec = 1000;
    private Waiter<ResponseQueueScanStatus> sastWaiter;
    private static final String SCAN_ID_PATH_PARAM = "{scanId}";
    private static final String PROJECT_ID_PATH_PARAM = "{projectId}";

    private static final String SCAN_WITH_SETTINGS_URL = "sast/scanWithSettings";
    private static final String ENGINE_CONFIGURATION_ID_DEFAULT = "0";
    private long scanId;
    private SASTResults sastResults = new SASTResults();
    private static final String SWAGGER_LOCATION = "help/swagger/docs/v1.1";
    private static final String ZIPPED_SOURCE = "zippedSource";
    private static final String SAST_SCAN = "SAST scan status";
    private static final String MSG_AVOID_DUPLICATE_PROJECT_SCANS = "\nAvoid duplicate project scans in queue\n";

    private String language = "en-US";

    private Waiter<ReportStatus> reportWaiter = new Waiter<ReportStatus>("Scan report", 10, 3) {
        @Override
        public ReportStatus getStatus(String id) throws IOException {
            return getReportStatus(id);
        }

        @Override
        public void printProgress(ReportStatus reportStatus) {
            printReportProgress(reportStatus, getStartTimeSec());
        }

        @Override
        public ReportStatus resolveStatus(ReportStatus reportStatus) {
            return resolveReportStatus(reportStatus);
        }

        //Report Waiter - overload methods
        private ReportStatus getReportStatus(String reportId) throws CxClientException, IOException {
            ReportStatus reportStatus = httpClient.getRequest(SAST_GET_REPORT_STATUS.replace("{reportId}", reportId), CONTENT_TYPE_APPLICATION_JSON_V1, ReportStatus.class, 200, " report status", false);
            reportStatus.setBaseId(reportId);
            String currentStatus = reportStatus.getStatus().getValue();
            if (currentStatus.equals(ReportStatusEnum.INPROCESS.value())) {
                reportStatus.setBaseStatus(Status.IN_PROGRESS);
            } else if (currentStatus.equals(ReportStatusEnum.FAILED.value())) {
                reportStatus.setBaseStatus(Status.FAILED);
            } else {
                reportStatus.setBaseStatus(Status.SUCCEEDED); //todo fix it!!
            }

            return reportStatus;
        }

        private ReportStatus resolveReportStatus(ReportStatus reportStatus) throws CxClientException {
            if (reportStatus != null) {
                if (Status.SUCCEEDED == reportStatus.getBaseStatus()) {
                    return reportStatus;
                } else {
                    throw new CxClientException("Generation of scan report [id=" + reportStatus.getBaseId() + "] failed.");
                }
            } else {
                throw new CxClientException("Generation of scan report failed.");
            }
        }

        private void printReportProgress(ReportStatus reportStatus, long startTime) {
            String reportType = reportStatus.getContentType().replace("application/", "");
            log.info("Waiting for server to generate " + reportType + " report. " + (startTime + reportTimeoutSec - (System.currentTimeMillis() / 1000)) + " seconds left to timeout");
        }

    };

    private Waiter<CxARMStatus> cxARMWaiter = new Waiter<CxARMStatus>("CxARM policy violations", 20, 3) {
        @Override
        public CxARMStatus getStatus(String id) throws IOException {
            return getCxARMStatus(id);
        }

        @Override
        public void printProgress(CxARMStatus cxARMStatus) {
            printCxARMProgress(getStartTimeSec());
        }

        @Override
        public CxARMStatus resolveStatus(CxARMStatus cxARMStatus) {
            return resolveCxARMStatus(cxARMStatus);
        }


        //CxARM Waiter - overload methods
        private CxARMStatus getCxARMStatus(String projectId) throws CxClientException, IOException {
            CxARMStatus cxARMStatus = httpClient.getRequest(SAST_GET_CXARM_STATUS.replace(PROJECT_ID_PATH_PARAM, projectId), CONTENT_TYPE_APPLICATION_JSON_V1, CxARMStatus.class, 200, " cxARM status", false);
            cxARMStatus.setBaseId(projectId);

            String currentStatus = cxARMStatus.getStatus();
            if (currentStatus.equals(CxARMStatusEnum.IN_PROGRESS.value())) {
                cxARMStatus.setBaseStatus(Status.IN_PROGRESS);
            } else if (currentStatus.equals(CxARMStatusEnum.FAILED.value())) {
                cxARMStatus.setBaseStatus(Status.FAILED);
            } else if (currentStatus.equals(CxARMStatusEnum.FINISHED.value())) {
                cxARMStatus.setBaseStatus(Status.SUCCEEDED);
            } else {
                cxARMStatus.setBaseStatus(Status.FAILED);
            }

            return cxARMStatus;
        }

        private void printCxARMProgress(long startTime) {
            log.info("Waiting for server to retrieve policy violations. " + (startTime + cxARMTimeoutSec - (System.currentTimeMillis() / 1000)) + " seconds left to timeout");
        }

        private CxARMStatus resolveCxARMStatus(CxARMStatus cxARMStatus) throws CxClientException {
            if (cxARMStatus != null) {
                if (Status.SUCCEEDED == cxARMStatus.getBaseStatus()) {
                    return cxARMStatus;
                } else {
                    throw new CxClientException("Getting policy violations of project [id=" + cxARMStatus.getBaseId() + "] failed.");
                }
            } else {
                throw new CxClientException("Getting policy violations of project failed.");
            }
        }
    };


    public CxSASTClient(CxScanConfig config, Logger log) throws MalformedURLException {
        super(config, log);


        int interval = config.getProgressInterval() != null ? config.getProgressInterval() : 20;
        int retry = config.getConnectionRetries() != null ? config.getConnectionRetries() : 3;
        sastWaiter = new Waiter<ResponseQueueScanStatus>("CxSAST scan", interval, retry) {
            @Override
            public ResponseQueueScanStatus getStatus(String id) throws IOException {
                ResponseQueueScanStatus statusResponse = null;
                try {
                    statusResponse = getSASTScanStatus(id);
                } catch (CxHTTPClientException e) {
                    try {
                        ResponseSastScanStatus statusResponseTemp = getSASTScanOutOfQueueStatus(id);
                        statusResponse = statusResponseTemp.convertResponseSastScanStatusToResponseQueueScanStatus(statusResponseTemp);
                    } catch (MalformedURLException exception) {
                        throw new MalformedURLException("Failed with next error: " + exception);
                    }
                }
                return statusResponse;
            }

            @Override
            public void printProgress(ResponseQueueScanStatus scanStatus) {
                printSASTProgress(scanStatus, getStartTimeSec());
            }

            @Override
            public ResponseQueueScanStatus resolveStatus(ResponseQueueScanStatus scanStatus) {
                return resolveSASTStatus(scanStatus);
            }
        };
    }

    @Override
    public Results init() {
        SASTResults initSastResults = new SASTResults();
        try {
            initiate();
            language = httpClient.getLanguageFromAccessToken();
            initSastResults.setSastLanguage(language);
        } catch (CxClientException e) {
            log.error(e.getMessage());
            setState(State.FAILED);
            initSastResults.setException(e);
        }
        return initSastResults;
    }

    //**------ API  ------**//

    //CREATE SAST scan
    private void createSASTScan(long projectId) {
        boolean dupScanFound = false;
        try {
            log.info("-----------------------------------Create CxSAST Scan:------------------------------------");
            if (config.isAvoidDuplicateProjectScans() != null && config.isAvoidDuplicateProjectScans() && projectHasQueuedScans(projectId)) {
                throw new CxClientException(MSG_AVOID_DUPLICATE_PROJECT_SCANS);
            }
            if (config.getRemoteType() == null) { //scan is local
                scanId = createLocalSASTScan(projectId);
            } else {
                scanId = createRemoteSourceScan(projectId);
            }
            if (config.getProjectLevelCustomFields() != null) {
                updateProjectCustomFields();
            }
            sastResults.setSastLanguage(language);
            sastResults.setScanId(scanId);
            log.info("SAST scan created successfully: Scan ID is {}", scanId);
            sastResults.setSastScanLink(config.getUrl(), scanId, projectId);
        } catch (Exception e) {
            setState(State.FAILED);
            if (!errorToBeSuppressed(e)) {
                sastResults.setException(new CxClientException(e));
            }
        }
    }

    private long createLocalSASTScan(long projectId) throws IOException {
        if (isScanWithSettingsSupported()) {
            log.info("Uploading the zipped source code.");
            PathFilter filter = new PathFilter(config.getSastFolderExclusions(), config.getSastFilterPattern(), log);
            byte[] zipFile = CxZipUtils.getZippedSources(config, filter, config.getSourceDir(), log);
            ScanWithSettingsResponse response = scanWithSettings(zipFile, projectId, false);
            return response.getId();
        } else {
            configureScanSettings(projectId);
            //prepare sources for scan
            PathFilter filter = new PathFilter(config.getSastFolderExclusions(), config.getSastFilterPattern(), log);
            byte[] zipFile = CxZipUtils.getZippedSources(config, filter, config.getSourceDir(), log);
            uploadZipFile(zipFile, projectId);

            return createScan(projectId);
        }
    }

    private long createRemoteSourceScan(long projectId) throws IOException {
        HttpEntity entity;
        excludeProjectSettings(projectId);
        RemoteSourceRequest req = new RemoteSourceRequest(config);
        RemoteSourceTypes type = req.getType();
        boolean isSSH = false;
        String apiVersion = getContentTypeAndApiVersion(config, SAST_CREATE_REMOTE_SOURCE_SCAN);

        switch (type) {
            case SVN:
                if (req.getPrivateKey() != null && req.getPrivateKey().length > 1) {
                    isSSH = true;
                    MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                    builder.addBinaryBody("privateKey", req.getPrivateKey(), ContentType.APPLICATION_JSON, null)
                            .addTextBody("absoluteUrl", req.getUri().getAbsoluteUrl())
                            .addTextBody("port", String.valueOf(req.getUri().getPort()))
                            .addTextBody("paths", config.getSourceDir());   //todo add paths to req OR using without
                    entity = builder.build();
                } else {
                    entity = new StringEntity(convertToJson(req), ContentType.APPLICATION_JSON);
                }
                break;
            case TFS:
                entity = new StringEntity(convertToJson(req), ContentType.APPLICATION_JSON);
                break;
            case PERFORCE:
                if (config.getPerforceMode() != null) {
                    req.setBrowseMode("Workspace");
                } else {
                    req.setBrowseMode("Depot");
                }
                entity = new StringEntity(convertToJson(req), StandardCharsets.UTF_8);
                break;
            case SHARED:
                entity = new StringEntity(new Gson().toJson(req), StandardCharsets.UTF_8);
                break;
            case GIT:
                if (req.getPrivateKey() == null || req.getPrivateKey().length < 1) {
                    Map<String, String> content = new HashMap<>();
                    content.put("url", req.getUri().getAbsoluteUrl());
                    content.put("branch", config.getRemoteSrcBranch());
                    entity = new StringEntity(new JSONObject(content).toString(), StandardCharsets.UTF_8);
                } else {
                    isSSH = true;
                    MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                    builder.addTextBody("url", req.getUri().getAbsoluteUrl(), ContentType.APPLICATION_JSON);
                    builder.addTextBody("branch", config.getRemoteSrcBranch(), ContentType.APPLICATION_JSON); //todo add branch to req OR using without this else??
                    builder.addBinaryBody("privateKey", req.getPrivateKey(), ContentType.MULTIPART_FORM_DATA, null);
                    entity = builder.build();
                }
                break;
            default:
                log.error("todo");
                entity = new StringEntity("", StandardCharsets.UTF_8);

        }
        if (isScanWithSettingsSupported()) {
            createRemoteSourceRequest(projectId, apiVersion, entity, type.value(), isSSH);
            ScanWithSettingsResponse response = scanWithSettings(null, projectId, true);
            return response.getId();
        } else {
            configureScanSettings(projectId);
            createRemoteSourceRequest(projectId, apiVersion, entity, type.value(), isSSH);
            return createScan(projectId);

        }
    }

    /**
     * Determine the appropriate content type and API version based on the provided configuration and API name.
     * <p>
     * scanWithSettings api and some other api have got different versions in different sast server because those implement different capabilities.
     * The cx-client-common as and when gets enhanced to support those new capabilities and it should also take care of sending required version.
     * This logic takes care of search api's that may have different versions of different capabilities accross different sast servers.
     * This logic is to identify what should be content type for actual version name and api version name that should be used while calling api
     * <p>
     * If the apiName is equal to SAST_RETENTION_RATE and data retention is enabled in the configuration,
     * it sets the apiVersion to "application/json;v=1.1".
     * <p>
     * If the apiName is equal to SCAN_WITH_SETTINGS_URL, it checks if custom fields are defined in the configuration.
     * If custom fields exist, it sets the apiVersion to "application/json;v=1.2".
     * If not, it checks if a post-scan action ID is defined and sets the apiVersion to "application/json;v=1.2".
     * If none of these conditions are met, it sets the apiVersion to the default "application/json;v=1.0".
     * <p>
     * If the current version is between 9.2 and 9.3 (inclusive), it sets the apiVersion to "application/json;v=1.0".
     */
    public String getContentTypeAndApiVersion(CxScanConfig config, String apiName) {
        CxVersion cxVersion = config.getCxVersion();
        String sastVersion = cxVersion.getVersion();
        String apiVersion = CONTENT_TYPE_APPLICATION_JSON_V1;
        if (sastVersion != null && !sastVersion.isEmpty()) {
            String[] versionComponents = sastVersion.split("\\.");
            if (versionComponents.length >= 2) {
                String currentVersion = versionComponents[0] + "." + versionComponents[1];
                float currentVersionFloat = Float.parseFloat(currentVersion);
				if (currentVersionFloat >= Float.parseFloat("9.7")) {
		               if (apiName.equalsIgnoreCase(SAST_SCAN_RESULTS_STATISTICS)) {
		                   apiVersion = CONTENT_TYPE_APPLICATION_XML_V6;
		               }
		           } else if (currentVersionFloat >= Float.parseFloat("9.4")) {
                    if (SAST_RETENTION_RATE.equalsIgnoreCase(apiName) && config.isEnableDataRetention()) {
                        apiVersion = CONTENT_TYPE_API_VERSION_1_1;
                    } else if (SCAN_WITH_SETTINGS_URL.equalsIgnoreCase(apiName)) {
                        String customFields = config.getCustomFields();
                        if (customFields != null && !customFields.isEmpty()) {
                            apiVersion = CONTENT_TYPE_API_VERSION_1_2;
                        } else if (config.getPostScanActionId() != null) {
                            apiVersion = CONTENT_TYPE_API_VERSION_1_2;
                        } else {
                            apiVersion = CONTENT_TYPE_APPLICATION_JSON_V1;
                        }
					}
					else {
		                   apiVersion = CONTENT_TYPE_APPLICATION_JSON_V1;                        
                    }
                } else if (currentVersionFloat >= 9.2 && currentVersionFloat <= 9.3) {
                    apiVersion = CONTENT_TYPE_APPLICATION_JSON_V1;
                }
            }
        }
        return apiVersion;
    }

    private void configureScanSettings(long projectId) throws IOException {
        ScanSettingResponse scanSettingResponse = getScanSetting(projectId);
        ScanSettingRequest scanSettingRequest = new ScanSettingRequest();
        scanSettingRequest.setEngineConfigurationId(scanSettingResponse.getEngineConfiguration().getId());
        scanSettingRequest.setProjectId(projectId);
        scanSettingRequest.setPresetId(config.getPresetId());
        if (config.getEngineConfigurationId() != null) {
            scanSettingRequest.setEngineConfigurationId(config.getEngineConfigurationId());
        }
        //Define createSASTScan settings
        defineScanSetting(scanSettingRequest);
    }


    /*
     * Suppress only those conditions for which it is generally acceptable
     * to have plugin not error out so that rest of the pipeline can continue.
     */
    private boolean errorToBeSuppressed(Exception error) {

        final String additionalMessage = "Build status will be marked successfull as this error is benign. Results from last scan will be displayed, if available.";
        boolean suppressed = false;

        //log actual error as it is first.
        log.error(error.getMessage());

        if (error instanceof ConditionTimeoutException && config.getContinueBuild()) {
            suppressed = true;
        }
        //Plugins will control if errors handled here will be ignored.
        else if (config.isIgnoreBenignErrors()) {

            if (error.getMessage().contains("source folder is empty,") || (sastResults.getException() != null
                    && sastResults.getException().getMessage().contains("No files to zip"))) {

                suppressed = true;
            } else if (error.getMessage().contains("No files to zip")) {
                suppressed = true;
            } else if (error.getMessage().equalsIgnoreCase(MSG_AVOID_DUPLICATE_PROJECT_SCANS)) {
                suppressed = true;
            }
        }

        if (suppressed) {
            log.info(additionalMessage);
            try {
                sastResults = getLatestScanResults();
                if (super.isIsNewProject() && sastResults.getSastScanLink() == null) {
                    String message = String
                            .format("The project %s is a new project. Hence there is no last scan report to be shown.", config.getProjectName());
                    log.info(message);
                }
            } catch (Exception okayToNotHaveResults) {
                sastResults = null;
            }

            if (sastResults == null)
                sastResults = new SASTResults();

            sastResults.setException(null);
            setState(State.SKIPPED);

        }
        return suppressed;
    }


    //GET SAST results + reports
    @Override
    public Results waitForScanResults() {
        try {
            log.info("------------------------------------Get CxSAST Results:-----------------------------------");
            //wait for SAST scan to finish
            log.info("Waiting for CxSAST scan to finish.");
            try {

                sastWaiter.waitForTaskToFinish(Long.toString(scanId), config.getSastScanTimeoutInMinutes() * 60, log);
                log.info("Retrieving SAST scan results");
                //retrieve SAST scan results
                sastResults = retrieveSASTResults(scanId, projectId);
            } catch (ConditionTimeoutException e) {

                if (!errorToBeSuppressed(e)) {
                    // throw the exception so that caught by outer catch
                    throw new Exception(e.getMessage());
                }
            } catch (CxClientException | IOException e) {
                if (!errorToBeSuppressed(e)) {
                    // throw the exception so that caught by outer catch
                    throw new Exception(e.getMessage());
                }
            }
            if (config.getEnablePolicyViolations()) {
                resolveSASTViolation(sastResults, projectId);
            }
            if (sastResults.getSastScanLink() != null) {
                SASTUtils.printSASTResultsToConsole(config, sastResults, config.getEnablePolicyViolations(), log);
            }

            if (!config.getReports().isEmpty()) {
                for (Map.Entry<ReportType, String> report : config.getReports().entrySet()) {
                    if (report != null) {
                        log.info("Generating " + report.getKey().value() + " report");
                        byte[] scanReport = getScanReport(sastResults.getScanId(), report.getKey(), CONTENT_TYPE_APPLICATION_PDF_V1);
                        writeReport(scanReport, report.getValue(), log);
                        if (report.getKey().value().equals("PDF")) {
                            sastResults.setPDFReport(scanReport);
                            sastResults.setPdfFileName(report.getValue());
                        }
                    }
                }
            } else if (config.getGeneratePDFReport()) {
                log.info("Generating PDF report");
                byte[] pdfReport = getScanReport(sastResults.getScanId(), ReportType.PDF, CONTENT_TYPE_APPLICATION_PDF_V1);
                sastResults.setPDFReport(pdfReport);
                if (config.getReportsDir() == null) {
                    config.setReportsDir(new File(System.getProperty("user.dir")));
                }
                String now = new SimpleDateFormat("dd_MM_yyyy-HH_mm_ss").format(new Date());
                String pdfFileName = PDF_REPORT_NAME + "_" + now + ".pdf";
                String pdfLink = writePDFReport(pdfReport, config.getReportsDir(), pdfFileName, log, "PDF");
                sastResults.setSastPDFLink(pdfLink);
                sastResults.setPdfFileName(pdfFileName);
            }
        } catch (Exception e) {
            if (!errorToBeSuppressed(e))
                sastResults.setException(new CxClientException(e));
        }

        return sastResults;
    }

    private void resolveSASTViolation(SASTResults sastResults, long projectId) {
        try {
            cxARMWaiter.waitForTaskToFinish(Long.toString(projectId), cxARMTimeoutSec, log);
            getProjectViolatedPolicies(httpClient, config.getCxARMUrl(), projectId, SAST.value())
                    .forEach(sastResults::addPolicy);
        } catch (Exception ex) {
            throw new CxClientException("CxARM is not available. Policy violations for SAST cannot be calculated: " + ex.getMessage());
        }
    }

    private SASTResults retrieveSASTResults(long scanId, long projectId) throws IOException {


        SASTStatisticsResponse statisticsResults = getScanStatistics(scanId);

        sastResults.setResults(scanId, statisticsResults, config.getUrl(), projectId);

        //SAST detailed report
        if (config.getGenerateXmlReport() == null || config.getGenerateXmlReport()) {
            byte[] cxReport = getScanReport(sastResults.getScanId(), ReportType.XML, CONTENT_TYPE_APPLICATION_XML_V1);
            CxXMLResults reportObj = convertToXMLResult(cxReport);
            sastResults.setScanDetailedReport(reportObj, config);
            sastResults.setRawXMLReport(cxReport);
        }
        sastResults.setSastResultsReady(true);
        return sastResults;
    }

    @Override
    public SASTResults getLatestScanResults() {
        sastResults = new SASTResults();
        sastResults.setSastLanguage(language);
        try {
            log.info("---------------------------------Get Last CxSAST Results:--------------------------------");
            List<LastScanResponse> scanList = getLatestSASTStatus(projectId);
            for (LastScanResponse s : scanList) {
                if (CurrentStatus.FINISHED.value().equals(s.getStatus().getName())) {
                    return retrieveSASTResults(s.getId(), projectId);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            sastResults.setException(new CxClientException(e));
        }
        return sastResults;
    }

    //Cancel SAST Scan
    public void cancelSASTScan() throws IOException {
        UpdateScanStatusRequest request = new UpdateScanStatusRequest(CurrentStatus.CANCELED);
        String json = convertToJson(request);
        StringEntity entity = new StringEntity(json, StandardCharsets.UTF_8);
        httpClient.patchRequest(SAST_QUEUE_SCAN_STATUS.replace(SCAN_ID_PATH_PARAM, Long.toString(scanId)), CONTENT_TYPE_APPLICATION_JSON_V1, entity, 200, "cancel SAST scan");
        log.info("SAST Scan canceled. (scanId: " + scanId + ")");
    }

    //**------ Private Methods  ------**//
    private boolean projectHasQueuedScans(long projectId) throws IOException {
        List<ResponseQueueScanStatus> queuedScans = getQueueScans(projectId);
        for (ResponseQueueScanStatus scan : queuedScans) {
            if (isStatusToAvoid(scan.getStage().getValue()) && scan.getProject().getId() == projectId) {
                return true;
            }
        }
        return false;
    }

    private boolean isStatusToAvoid(String status) {
        QueueStatus qStatus = QueueStatus.valueOf(status);

        switch (qStatus) {
            case New:
            case PreScan:
            case SourcePullingAndDeployment:
            case Queued:
            case Scanning:
            case PostScan:
                return true;
            default:
                return false;
        }
    }

    public ScanSettingResponse getScanSetting(long projectId) throws IOException {
        return httpClient.getRequest(SAST_GET_SCAN_SETTINGS.replace(PROJECT_ID_PATH_PARAM, Long.toString(projectId)), CONTENT_TYPE_APPLICATION_JSON_V1, ScanSettingResponse.class, 200, "Scan setting", false);
    }

    private void defineScanSetting(ScanSettingRequest scanSetting) throws IOException {
        StringEntity entity = new StringEntity(convertToJson(scanSetting), StandardCharsets.UTF_8);
        httpClient.putRequest(SAST_UPDATE_SCAN_SETTINGS, CONTENT_TYPE_APPLICATION_JSON_V1, entity, CxID.class, 200, "define scan setting");
    }

    private void excludeProjectSettings(long projectId) throws IOException {
        String excludeFoldersPattern = Arrays.stream(config.getSastFolderExclusions().split(",")).map(String::trim).collect(Collectors.joining(","));
        String excludeFilesPattern = Arrays.stream(config.getSastFilterPattern().split(",")).map(String::trim).map(file -> file.replace("!**/", "")).collect(Collectors.joining(","));
        ExcludeSettingsRequest excludeSettingsRequest = new ExcludeSettingsRequest(excludeFoldersPattern, excludeFilesPattern);
        StringEntity entity = new StringEntity(convertToJson(excludeSettingsRequest), StandardCharsets.UTF_8);
        log.info("Exclude folders pattern: " + excludeFoldersPattern);
        log.info("Exclude files pattern: " + excludeFilesPattern);
        httpClient.putRequest(String.format(SAST_EXCLUDE_FOLDERS_FILES_PATTERNS, projectId), CONTENT_TYPE_APPLICATION_JSON_V1, entity, null, 200, "exclude project's settings");
    }

    private void uploadZipFile(byte[] zipFile, long projectId) throws CxClientException, IOException {
        log.info("Uploading zip file");

        try (InputStream is = new ByteArrayInputStream(zipFile)) {
            InputStreamBody streamBody = new InputStreamBody(is, ContentType.APPLICATION_OCTET_STREAM, ZIPPED_SOURCE);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            builder.addPart(ZIPPED_SOURCE, streamBody);
            String apiVersion = getContentTypeAndApiVersion(config, SAST_ZIP_ATTACHMENTS);
            HttpEntity entity = builder.build();
            httpClient.postRequest(SAST_ZIP_ATTACHMENTS.replace(PROJECT_ID_PATH_PARAM, Long.toString(projectId)), null, apiVersion, new BufferedHttpEntity(entity), null, 204, "upload ZIP file");
        }
    }

    private long createScan(long projectId) throws IOException {
        CreateScanRequest scanRequest = new CreateScanRequest(projectId, config.getIncremental(), config.getPublic(), config.getForceScan(), config.getScanComment() == null ? "" : config.getScanComment());

        log.info("Sending SAST scan request");
        StringEntity entity = new StringEntity(convertToJson(scanRequest), StandardCharsets.UTF_8);
        CxID createScanResponse = httpClient.postRequest(SAST_CREATE_SCAN, CONTENT_TYPE_APPLICATION_JSON_V1, entity, CxID.class, 201, "create new SAST Scan");
        log.info(String.format("SAST Scan created successfully. Link to project state: " + config.getUrl() + LINK_FORMAT + projectId + LINK_FORMAL_SUMMARY));

        return createScanResponse.getId();
    }


    private CxID createRemoteSourceRequest(long projectId, String apiVersion, HttpEntity entity, String sourceType, boolean isSSH) throws IOException {

        return httpClient.postRequest(String.format(SAST_CREATE_REMOTE_SOURCE_SCAN, projectId, sourceType, isSSH ? "ssh" : ""), isSSH ? null : CONTENT_TYPE_APPLICATION_JSON_V1, apiVersion,
                entity, CxID.class, 204, "create " + sourceType + " remote source scan setting");

    }

    private SASTStatisticsResponse getScanStatistics(long scanId) throws IOException {
    	String apiVersion = getContentTypeAndApiVersion(config, SAST_SCAN_RESULTS_STATISTICS);
        return httpClient.getRequest(SAST_SCAN_RESULTS_STATISTICS.replace(SCAN_ID_PATH_PARAM, Long.toString(scanId)), apiVersion, SASTStatisticsResponse.class, 200, "SAST scan statistics", false);
    }

    public List<LastScanResponse> getLatestSASTStatus(long projectId) throws IOException {
        return (List<LastScanResponse>) httpClient.getRequest(SAST_GET_PROJECT_SCANS.replace(PROJECT_ID_PATH_PARAM, Long.toString(projectId)), CONTENT_TYPE_APPLICATION_JSON_V1, LastScanResponse.class, 200, "last SAST scan ID", true);
    }

    private List<ResponseQueueScanStatus> getQueueScans(long projectId) throws IOException {
        return (List<ResponseQueueScanStatus>) httpClient.getRequest(SAST_GET_QUEUED_SCANS.replace(PROJECT_ID_PATH_PARAM, Long.toString(projectId)), CONTENT_TYPE_APPLICATION_JSON_V1, ResponseQueueScanStatus.class, 200, "scans in the queue. (projectId: )" + projectId, true);
    }

    private CreateReportResponse createScanReport(CreateReportRequest reportRequest) throws IOException {
        StringEntity entity = new StringEntity(convertToJson(reportRequest), StandardCharsets.UTF_8);
        return httpClient.postRequest(SAST_CREATE_REPORT, CONTENT_TYPE_APPLICATION_JSON_V1, entity, CreateReportResponse.class, 202, "to create " + reportRequest.getReportType() + " scan report");
    }

    private byte[] getScanReport(long scanId, ReportType reportType, String contentType) throws IOException {
        CreateReportRequest reportRequest = new CreateReportRequest(scanId, reportType.name());
        CreateReportResponse createReportResponse = createScanReport(reportRequest);
        int reportId = createReportResponse.getReportId();
        reportWaiter.waitForTaskToFinish(Long.toString(reportId), reportTimeoutSec, log);

        return getReport(reportId, contentType);
    }

    private byte[] getReport(long reportId, String contentType) throws IOException {
        return httpClient.getRequest(SAST_GET_REPORT.replace("{reportId}", Long.toString(reportId)), contentType, byte[].class, 200, " scan report: " + reportId, false);
    }

    //SCAN Waiter - overload methods
    public ResponseQueueScanStatus getSASTScanStatus(String scanId) throws IOException {

        ResponseQueueScanStatus scanStatus = httpClient.getRequest(SAST_QUEUE_SCAN_STATUS.replace(SCAN_ID_PATH_PARAM, scanId), CONTENT_TYPE_APPLICATION_JSON_V1, ResponseQueueScanStatus.class, 200, SAST_SCAN, false);
        String currentStatus = scanStatus.getStage().getValue();

        if (CurrentStatus.FAILED.value().equals(currentStatus) || CurrentStatus.CANCELED.value().equals(currentStatus) ||
                CurrentStatus.DELETED.value().equals(currentStatus) || CurrentStatus.UNKNOWN.value().equals(currentStatus)) {
            scanStatus.setBaseStatus(Status.FAILED);
        } else if (CurrentStatus.FINISHED.value().equals(currentStatus)) {
            scanStatus.setBaseStatus(Status.SUCCEEDED);
        } else {
            scanStatus.setBaseStatus(Status.IN_PROGRESS);
        }

        return scanStatus;
    }

    //Check SAST scan status via sast/scans/{scanId} API
    public ResponseSastScanStatus getSASTScanOutOfQueueStatus(String scanId) throws IOException {
        ResponseSastScanStatus scanStatus = httpClient.getRequest(SAST_SCAN_STATUS.replace(SCAN_ID_PATH_PARAM, scanId), CONTENT_TYPE_APPLICATION_JSON_V1, ResponseSastScanStatus.class, 200, SAST_SCAN, false);
        String currentStatus = scanStatus.getStatus().getName();

        if (CurrentStatus.FAILED.value().equals(currentStatus) || CurrentStatus.CANCELED.value().equals(currentStatus) ||
                CurrentStatus.DELETED.value().equals(currentStatus) || CurrentStatus.UNKNOWN.value().equals(currentStatus)) {
            scanStatus.setBaseStatus(Status.FAILED);
        } else if (CurrentStatus.FINISHED.value().equals(currentStatus)) {
            scanStatus.setBaseStatus(Status.SUCCEEDED);
        } else {
            scanStatus.setBaseStatus(Status.IN_PROGRESS);
        }

        return scanStatus;
    }

    private void printSASTProgress(ResponseQueueScanStatus scanStatus, long startTime) {
        String timestamp = ShragaUtils.getTimestampSince(startTime);

        String prefix = (scanStatus.getTotalPercent() < 10) ? " " : "";
        log.info("Waiting for SAST scan results. Elapsed time: " + timestamp + ". " + prefix +
                scanStatus.getTotalPercent() + "% processed. Status: " + scanStatus.getStage().getValue() + ".");
    }

    private ResponseQueueScanStatus resolveSASTStatus(ResponseQueueScanStatus scanStatus) {
        if (scanStatus != null) {
            if (Status.SUCCEEDED == scanStatus.getBaseStatus()) {
                log.info("SAST scan finished successfully.");
                return scanStatus;
            } else {
                throw new CxClientException("SAST scan cannot be completed. status [" + scanStatus.getStage().getValue() + "]: " + scanStatus.getStageDetails());
            }
        } else {
            throw new CxClientException("SAST scan cannot be completed.");
        }
    }

    @Override
    public Results initiateScan() {
        sastResults = new SASTResults();
        sastResults.setSastLanguage(language);
        createSASTScan(projectId);
        return sastResults;
    }

    private boolean isScanWithSettingsSupported() {
        try {
            HashMap swaggerResponse = this.httpClient.getRequest(SWAGGER_LOCATION, CONTENT_TYPE_APPLICATION_JSON, HashMap.class, 200, SAST_SCAN, false);
            return swaggerResponse.toString().contains("/sast/scanWithSettings");
        } catch (Exception e) {
            // Assuming something went wrong but SAST version is greater than 9.x
            return true;
        }
    }

    public void updateProjectCustomFields() {
        try {
            log.info("Updating Project Custom Fields.");
            if (config != null) {
                String projectId = String.valueOf(this.projectId);
                String apiVersion = getContentTypeAndApiVersion(config, PROJECT_PATH);
                String apiVersionCustomField = getContentTypeAndApiVersion(config, CUSTOM_FIELD_PATH);
                String projectCustomFieldsString = config.getProjectLevelCustomFields();
                if (projectCustomFieldsString != null && !projectCustomFieldsString.isEmpty()) {
                    List<ProjectLevelCustomFields> fetchSASTProjectCustomFields = (List<ProjectLevelCustomFields>) httpClient.getRequest(
                            CUSTOM_FIELD_PATH, apiVersionCustomField, ProjectLevelCustomFields.class, 200, SAST_SCAN, true
                    );
                    ArrayList<ProjectLevelCustomFields> custObj = new ArrayList<>();
                    Map<String, String> projectCustomFieldMap = customFieldMap(projectCustomFieldsString);
                    Project getProjectRequest = httpClient.getRequest(PROJECT_PATH + projectId, CONTENT_TYPE_APPLICATION_JSON_V2, Project.class, 200, SAST_SCAN, false);
                    ProjectPutRequest projectPutRequest = new ProjectPutRequest();
                    projectPutRequest.setName(getProjectRequest.getName());
                    Integer team = Integer.parseInt(getProjectRequest.getTeamId());
                    List<ProjectLevelCustomFields> tempCustomFields = getProjectRequest.getCustomFields();
                    Boolean validCustomFields = false;
                    for (int i = 0; i < fetchSASTProjectCustomFields.size(); i++) {
                        if (projectCustomFieldMap.containsKey(fetchSASTProjectCustomFields.get(i).getName())) {
                            validCustomFields = true;
                            ProjectLevelCustomFields customProjectField = new ProjectLevelCustomFields(
                                    fetchSASTProjectCustomFields.get(i).getId(),
                                    projectCustomFieldMap.get(fetchSASTProjectCustomFields.get(i).getName()),
                                    fetchSASTProjectCustomFields.get(i).getName()
                            );
                            custObj.add(customProjectField);
                        }
                    }
                    if (!validCustomFields) {
                        log.error("Project level custom fields not configured in SAST");
                    }
                    List<ProjectLevelCustomFields> additionalCustomFields = new ArrayList<>();
                    for (ProjectLevelCustomFields existingCustomField : tempCustomFields) {
                        String existingCustomFieldName = existingCustomField.getName();
                        boolean isIdExists = projectCustomFieldMap.containsKey(existingCustomFieldName);
                        if (!isIdExists) {
                            additionalCustomFields.add(existingCustomField);
                        }
                    }
                    custObj.addAll(additionalCustomFields);
                    projectPutRequest.setOwningTeam(team);
                    if (!custObj.isEmpty()) {
                        projectPutRequest.setCustomFields(custObj);
                        String json = convertToJson(projectPutRequest);
                        StringEntity entity = new StringEntity(json);
                        try {
                            httpClient.putRequest(PROJECT_PATH + projectId, apiVersion, entity, null, 204, "define project level custom field");
                            log.info("Project Level-Custom Fields updated successfully.");
                        } catch (CxHTTPClientException e) {
                            log.error("Error updating Project Level-Custom Fields: {}", e.getMessage());
                        }
                    }
                }
            }
        } catch (Exception ex) {
            throw new CxClientException("Failed to Update Project Level-Custom Fields: " + ex.getMessage());
        }
    }

    private Map<String, String> customFieldMap(String projectCustomField) {
        Map<String, String> customFieldMap = new HashMap<String, String>();
        StringTokenizer tokenizer = new StringTokenizer(projectCustomField, ",");
        log.info("Project custom field: {}", projectCustomField);
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            String[] keyValue = token.split(":");
            customFieldMap.put(keyValue[0], keyValue[1]);
        }
        return customFieldMap;
    }

    private ScanWithSettingsResponse scanWithSettings(byte[] zipFile, long projectId, boolean isRemote) throws IOException {
        log.info("Uploading zip file");
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        if (!isRemote) {
            try (InputStream is = new ByteArrayInputStream(zipFile)) {
                InputStreamBody streamBody = new InputStreamBody(is, ContentType.APPLICATION_OCTET_STREAM, ZIPPED_SOURCE);
                builder.addPart(ZIPPED_SOURCE, streamBody);
            }
        }
        builder.addTextBody("projectId", Long.toString(projectId), ContentType.APPLICATION_JSON);
        if (config.getIsOverrideProjectSetting()) {
            builder.addTextBody("overrideProjectSetting", config.getIsOverrideProjectSetting() + "", ContentType.APPLICATION_JSON);
        } else {
            builder.addTextBody("overrideProjectSetting", super.isIsNewProject() ? "true" : "false", ContentType.APPLICATION_JSON);
        }
        builder.addTextBody("isIncremental", config.getIncremental().toString(), ContentType.APPLICATION_JSON);
        builder.addTextBody("isPublic", config.getPublic().toString(), ContentType.APPLICATION_JSON);
        builder.addTextBody("forceScan", config.getForceScan().toString(), ContentType.APPLICATION_JSON);
        builder.addTextBody("presetId", config.getPresetId().toString(), ContentType.APPLICATION_JSON);
        builder.addTextBody("comment", config.getScanComment() == null ? "" : config.getScanComment(), ContentType.APPLICATION_JSON);
        builder.addTextBody("engineConfigurationId", config.getEngineConfigurationId() != null ? config.getEngineConfigurationId().toString() : ENGINE_CONFIGURATION_ID_DEFAULT, ContentType.APPLICATION_JSON);

        builder.addTextBody("postScanActionId",
                config.getPostScanActionId() != null && config.getPostScanActionId() != 0 ?
                        config.getPostScanActionId().toString() : "",
                ContentType.APPLICATION_JSON);

        builder.addTextBody("customFields", config.getCustomFields() != null ?
                config.getCustomFields() : "", ContentType.APPLICATION_JSON);
        String apiVersion = getContentTypeAndApiVersion(config, SCAN_WITH_SETTINGS_URL);

        HttpEntity entity = builder.build();
        return httpClient.postRequest(SCAN_WITH_SETTINGS_URL, null, apiVersion, new BufferedHttpEntity(entity), ScanWithSettingsResponse.class, 201, "upload ZIP file");
    }
}
