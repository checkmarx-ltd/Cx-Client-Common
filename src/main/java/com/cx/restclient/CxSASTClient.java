package com.cx.restclient;

import com.cx.restclient.common.IScanner;
import com.cx.restclient.common.ShragaUtils;
import com.cx.restclient.common.Waiter;
import com.cx.restclient.configuration.CxScanConfig;
import com.cx.restclient.dto.*;
import com.cx.restclient.exception.CxClientException;

import com.cx.restclient.sast.dto.*;
import com.cx.restclient.sast.utils.LegacyClient;
import com.cx.restclient.sast.utils.SASTUtils;
import com.cx.restclient.sast.utils.zip.CxZipUtils;
import com.google.gson.Gson;
import org.apache.http.HttpEntity;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.json.JSONObject;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
public class CxSASTClient extends LegacyClient implements IScanner {

    public static final String JENKINS = "jenkins";
 
    private int reportTimeoutSec = 5000;
    private int cxARMTimeoutSec = 1000;
    private Waiter<ResponseQueueScanStatus> sastWaiter;
    private static final String SCAN_ID_PATH_PARAM = "{scanId}";
    private static final String PROJECT_ID_PATH_PARAM = "{projectId}";
    private long scanId;
    private SASTResults sastResults = new SASTResults();
    
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
            if(reportStatus != null ) {
                if (Status.SUCCEEDED == reportStatus.getBaseStatus()) {
                    return reportStatus;
                } else {
                    throw new CxClientException("Generation of scan report [id=" + reportStatus.getBaseId() + "] failed.");
                }
            }else{
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
        public CxARMStatus getStatus(String id) throws  IOException {
            return getCxARMStatus(id);
        }

        @Override
        public void printProgress(CxARMStatus cxARMStatus) {
            printCxARMProgress(getStartTimeSec());
        }

        @Override
        public CxARMStatus resolveStatus(CxARMStatus cxARMStatus)  {
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

        private void printCxARMProgress( long startTime) {
            log.info("Waiting for server to retrieve policy violations. " + (startTime + cxARMTimeoutSec - (System.currentTimeMillis() / 1000)) + " seconds left to timeout");
        }

        private CxARMStatus resolveCxARMStatus(CxARMStatus cxARMStatus) throws CxClientException {
            if (cxARMStatus != null) {
                if (Status.SUCCEEDED == cxARMStatus.getBaseStatus()) {
                    return cxARMStatus;
                } else {
                    throw new CxClientException("Getting policy violations of project [id=" + cxARMStatus.getBaseId() + "] failed.");
                }
            }else{
                throw new CxClientException("Getting policy violations of project failed.");
            }
        }
    };


    CxSASTClient(Logger log, CxScanConfig config) throws MalformedURLException {
        super( config, log );

        int interval = config.getProgressInterval() != null ? config.getProgressInterval() : 20;
        int retry = config.getConnectionRetries() != null ? config.getConnectionRetries() : 3;
        sastWaiter = new Waiter<ResponseQueueScanStatus>("CxSAST scan", interval, retry) {
            @Override
            public ResponseQueueScanStatus getStatus(String id) throws  IOException {
                return getSASTScanStatus(id);
            }

            @Override
            public void printProgress(ResponseQueueScanStatus scanStatus) {
                printSASTProgress(scanStatus, getStartTimeSec());
            }

            @Override
            public ResponseQueueScanStatus resolveStatus(ResponseQueueScanStatus scanStatus)  {
                return resolveSASTStatus(scanStatus);
            }
        };
    }


    
    
    //**------ API  ------**//

    //CREATE SAST scan
    private long createSASTScan(long projectId)  {
        try {
            log.info("-----------------------------------Create CxSAST Scan:------------------------------------");
            if (config.isAvoidDuplicateProjectScans() != null && config.isAvoidDuplicateProjectScans() && projectHasQueuedScans(projectId)) {
                throw new CxClientException("\nAvoid duplicate project scans in queue\n");
            }
            if (config.getRemoteType() == null) { //scan is local
                return createLocalSASTScan(projectId);
            } else {
                return createRemoteSourceScan(projectId);
            }
        }catch (IOException e){
            throw new CxClientException(e);
        }
    }

    private long createLocalSASTScan(long projectId) throws IOException{
        configureScanSettings(projectId);
        //prepare sources for scan
        PathFilter filter = new PathFilter(config.getSastFolderExclusions(), config.getSastFilterPattern(), log);
        File zipFile = CxZipUtils.getZippedSources(config, filter, config.getSourceDir(), log);
        uploadZipFile(zipFile, projectId);
        CxZipUtils.deleteZippedSources(zipFile, config, log);

        return createScan(projectId);
    }

    private long createRemoteSourceScan(long projectId) throws IOException {
        HttpEntity entity;
        excludeProjectSettings(projectId);
        RemoteSourceRequest req = new RemoteSourceRequest(config);
        RemoteSourceTypes type = req.getType();
        boolean isSSH = false;

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
        configureScanSettings(projectId);
        createRemoteSourceRequest(projectId, entity, type.value(), isSSH);

        return createScan(projectId);
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

    //GET SAST results + reports
    @Override
    public IResults waitForScanResults() throws InterruptedException {
        //SASTResults sastResults;

        log.info("------------------------------------Get CxSAST Results:-----------------------------------");
        //wait for SAST scan to finish
        log.info("Waiting for CxSAST scan to finish.");
        sastWaiter.waitForTaskToFinish(Long.toString(scanId), config.getSastScanTimeoutInMinutes() * 60, log);
        log.info("Retrieving SAST scan results");

        try {
            //retrieve SAST scan results
            sastResults = retrieveSASTResults(scanId, projectId);
            if (config.getEnablePolicyViolations()) {
                resolveSASTViolation(sastResults, projectId);
            }
            SASTUtils.printSASTResultsToConsole(sastResults, config.getEnablePolicyViolations(), log);

            //PDF report
            if (config.getGeneratePDFReport()) {
                log.info("Generating PDF report");
                byte[] pdfReport = getScanReport(sastResults.getScanId(), ReportType.PDF, CONTENT_TYPE_APPLICATION_PDF_V1);
                sastResults.setPDFReport(pdfReport);
                if (config.getReportsDir() != null) {
                    String now = new SimpleDateFormat("dd_MM_yyyy-HH_mm_ss").format(new Date());
                    String pdfFileName = PDF_REPORT_NAME + "_" + now + ".pdf";
                    pdfFileName = writePDFReport(pdfReport, config.getReportsDir(), pdfFileName, log);
                    sastResults.setPdfFileName(pdfFileName);
                }
            }
            // CLI report/s
            else if (!config.getReports().isEmpty()) {
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
            }
        }catch (IOException e){
            throw new CxClientException(e.getMessage());
        }
        return sastResults;
    }

    private void resolveSASTViolation(SASTResults sastResults, long projectId)  {
        try {
            cxARMWaiter.waitForTaskToFinish(Long.toString(projectId), cxARMTimeoutSec, log);
            getProjectViolatedPolicies(httpClient, config.getCxARMUrl(), projectId, SAST.value())
                    .forEach(sastResults::addPolicy);
        } catch (Exception ex) {
            throw new CxClientException("CxARM is not available. Policy violations for SAST cannot be calculated: " + ex.getMessage());
        }
    }

    private SASTResults retrieveSASTResults(long scanId, long projectId) throws  IOException {

        SASTStatisticsResponse statisticsResults = getScanStatistics(scanId);
        sastResults.setResults(scanId, statisticsResults, config.getUrl(), projectId);

        //SAST detailed report
        if (config.getGenerateXmlReport() == null || config.getGenerateXmlReport()) {
            byte[] cxReport = getScanReport(sastResults.getScanId(), ReportType.XML, CONTENT_TYPE_APPLICATION_XML_V1);
            CxXMLResults reportObj = convertToXMLResult(cxReport);
            sastResults.setScanDetailedReport(reportObj);
            sastResults.setRawXMLReport(cxReport);
        }
        sastResults.setSastResultsReady(true);
        return sastResults;
    }

    @Override
    public IResults getLatestScanResults() throws InterruptedException {
        
        try {
            log.info("---------------------------------Get Last CxSAST Results:--------------------------------");
            List<LastScanResponse> scanList = getLatestSASTStatus(projectId);
            for (LastScanResponse s : scanList) {
                if (CurrentStatus.FINISHED.value().equals(s.getStatus().getName())) {
                    return retrieveSASTResults(s.getId(), projectId);
                }
            }
        }catch(IOException e){
            throw new CxClientException(e.getMessage());
        }
        return new SASTResults();
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

    private void uploadZipFile(File zipFile, long projectId) throws IOException {
        log.info("Uploading zip file");

        InputStreamBody streamBody = new InputStreamBody(new FileInputStream(zipFile.getAbsoluteFile()), ContentType.APPLICATION_OCTET_STREAM, "zippedSource");
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        builder.addPart("zippedSource", streamBody);
        HttpEntity entity = builder.build();
        httpClient.postRequest(SAST_ZIP_ATTACHMENTS.replace(PROJECT_ID_PATH_PARAM, Long.toString(projectId)), null, new BufferedHttpEntity(entity), null, 204, "upload ZIP file");
    }

    private long createScan(long projectId) throws IOException {
        CreateScanRequest scanRequest = new CreateScanRequest(projectId, config.getIncremental(), config.getPublic(), config.getForceScan(), config.getScanComment() == null ? "" : config.getScanComment());

        log.info("Sending SAST scan request");
        StringEntity entity = new StringEntity(convertToJson(scanRequest), StandardCharsets.UTF_8);
        CxID createScanResponse = httpClient.postRequest(SAST_CREATE_SCAN, CONTENT_TYPE_APPLICATION_JSON_V1, entity, CxID.class, 201, "create new SAST Scan");
        log.info(String.format("SAST Scan created successfully. Link to project state: " + config.getUrl() + LINK_FORMAT, projectId));

        return createScanResponse.getId();
    }

    private CxID createRemoteSourceRequest(long projectId, HttpEntity entity, String sourceType, boolean isSSH) throws IOException {
        return httpClient.postRequest(String.format(SAST_CREATE_REMOTE_SOURCE_SCAN, projectId, sourceType, isSSH ? "ssh" : ""), isSSH? null : CONTENT_TYPE_APPLICATION_JSON_V1,
                entity, CxID.class, 204, "create " + sourceType + " remote source scan setting");
        
    }

    private SASTStatisticsResponse getScanStatistics(long scanId) throws IOException {
        return httpClient.getRequest(SAST_SCAN_RESULTS_STATISTICS.replace(SCAN_ID_PATH_PARAM, Long.toString(scanId)), CONTENT_TYPE_APPLICATION_JSON_V1, SASTStatisticsResponse.class, 200, "SAST scan statistics", false);
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
        ResponseQueueScanStatus scanStatus = httpClient.getRequest(SAST_QUEUE_SCAN_STATUS.replace(SCAN_ID_PATH_PARAM, scanId), CONTENT_TYPE_APPLICATION_JSON_V1, ResponseQueueScanStatus.class, 200, "SAST scan status", false);
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

    private void printSASTProgress(ResponseQueueScanStatus scanStatus, long startTime) {
        String timestamp = ShragaUtils.getTimestampSince(startTime);

        String prefix = (scanStatus.getTotalPercent() < 10) ? " " : "";
        log.info("Waiting for SAST scan results. Elapsed time: " + timestamp + ". " + prefix +
                scanStatus.getTotalPercent() + "% processed. Status: " + scanStatus.getStage().getValue() + ".");
    }

    private ResponseQueueScanStatus resolveSASTStatus(ResponseQueueScanStatus scanStatus)  {
        if(scanStatus != null ) {
            if (Status.SUCCEEDED == scanStatus.getBaseStatus()) {
                log.info("SAST scan finished successfully.");
                return scanStatus;
            } else {
                throw new CxClientException("SAST scan cannot be completed. status [" + scanStatus.getStage().getValue() + "]: " + scanStatus.getStageDetails());
            }
        }else{
            throw new CxClientException("SAST scan cannot be completed.");
        }
    }

 

    @Override
    public IResults createScan() {

        this.scanId  = createSASTScan(projectId);
        sastResults.setSastScanLink(config.getUrl(), this.scanId, projectId);
        sastResults.setScanId(this.scanId);
        return sastResults;
    }

    
}
