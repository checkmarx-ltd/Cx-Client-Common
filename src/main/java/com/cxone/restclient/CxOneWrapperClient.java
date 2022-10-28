package com.cxone.restclient;

import java.net.MalformedURLException;

import org.slf4j.Logger;

import static com.cxone.restclient.cxOneArm.dto.CxOneProviders.AST;
import static com.cxone.restclient.cxOneArm.utils.CxOneARMUtils.getProjectViolatedPolicies;
import static com.cxone.restclient.httpClient.utils.ContentType.CONTENT_TYPE_APPLICATION_JSON;
import static com.cxone.restclient.httpClient.utils.ContentType.CONTENT_TYPE_APPLICATION_JSON_V1;
import static com.cxone.restclient.httpClient.utils.ContentType.CONTENT_TYPE_APPLICATION_XML_V1;
import static com.cxone.restclient.httpClient.utils.HttpClientHelper.convertToJson;

import static com.cxone.restclient.ast.utils.ASTParam.AST_CREATE_REMOTE_SOURCE_SCAN;
import static com.cxone.restclient.ast.utils.ASTParam.AST_CREATE_REPORT;
import static com.cxone.restclient.ast.utils.ASTParam.AST_CREATE_SCAN;
import static com.cxone.restclient.ast.utils.ASTParam.AST_EXCLUDE_FOLDERS_FILES_PATTERNS;
import static com.cxone.restclient.ast.utils.ASTParam.AST_GET_PROJECT_SCANS;
import static com.cxone.restclient.ast.utils.ASTParam.AST_GET_QUEUED_SCANS;
import static com.cxone.restclient.ast.utils.ASTParam.AST_GET_REPORT;
import static com.cxone.restclient.ast.utils.ASTParam.AST_GET_SCAN_SETTINGS;
import static com.cxone.restclient.ast.utils.ASTParam.AST_QUEUE_SCAN_STATUS;
import static com.cxone.restclient.ast.utils.ASTParam.AST_SCAN_RESULTS_STATISTICS;
import static com.cxone.restclient.ast.utils.ASTParam.AST_SCAN_STATUS;
import static com.cxone.restclient.ast.utils.ASTParam.AST_UPDATE_SCAN_SETTINGS;
import static com.cxone.restclient.ast.utils.ASTParam.AST_ZIP_ATTACHMENTS;
import static com.cxone.restclient.ast.utils.ASTParam.LINK_FORMAT;
import static com.cxone.restclient.httpClient.utils.ContentType.CONTENT_TYPE_APPLICATION_PDF_V1;
import static com.cxone.restclient.ast.utils.ASTParam.PDF_REPORT_NAME;
import static com.cxone.restclient.ast.utils.Astutils.convertToXMLResult;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.http.HttpEntity;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.awaitility.core.ConditionTimeoutException;
import org.json.JSONObject;

import com.cx.restclient.common.Scanner;
import com.cx.restclient.configuration.CxScanConfig;
import com.cx.restclient.dto.PathFilter;
import com.cx.restclient.dto.RemoteSourceRequest;
import com.cx.restclient.dto.RemoteSourceTypes;
import com.cx.restclient.dto.Results;
import com.cx.restclient.dto.Status;
import com.cx.restclient.sast.dto.QueueStatus;
import com.cx.restclient.sast.utils.LegacyClient;
import com.cx.restclient.sast.utils.State;
import com.cxone.restclient.ast.dto.ASTResults1;
import com.cxone.restclient.ast.dto.ASTStatisticsResponse;
import com.cxone.restclient.ast.dto.CreateReportRequest;
import com.cxone.restclient.ast.dto.CreateReportResponse;
import com.cxone.restclient.ast.dto.CreateScanRequest;
import com.cxone.restclient.ast.dto.CurrentStatus;
import com.cxone.restclient.ast.dto.CxARMStatus;
import com.cxone.restclient.ast.dto.CxOneID;
import com.cxone.restclient.ast.dto.CxOneXMLResults;
import com.cxone.restclient.ast.dto.LastScanResponse;
import com.cxone.restclient.ast.dto.ReportStatus;
import com.cxone.restclient.ast.dto.ReportType;
import com.cxone.restclient.ast.dto.ResponseAstScanStatus;
import com.cxone.restclient.ast.dto.ResponseQueueScanStatus;
import com.cxone.restclient.ast.dto.ScanSettingRequest;
import com.cxone.restclient.ast.dto.ScanSettingResponse;
import com.cxone.restclient.ast.dto.ScanWithSettingsResponse;
import com.cxone.restclient.ast.dto.UpdateScanStatusRequest;
import com.cxone.restclient.ast.utils.ASTUtils;
import com.cxone.restclient.ast.utils.zip.CxOneZipUtils;
import com.cxone.restclient.common.ShragaUtils;
import com.cxone.restclient.common.Waiter;
import com.cxone.restclient.exception.CxOneClientException;
import com.cxone.restclient.exception.CxOneHTTPClientException;
import com.google.gson.Gson;


public class CxOneWrapperClient extends LegacyClient implements Scanner {

	
	private String language = "en-US";
	private ASTResults1 astResults = new ASTResults1();
	private Waiter<ResponseQueueScanStatus> astWaiter;
	private long scanId;
	private static final String MSG_AVOID_DUPLICATE_PROJECT_SCANS = "\nAvoid duplicate project scans in queue\n";
	private int cxARMTimeoutSec = 1000;
	private static final String SWAGGER_LOCATION = "help/swagger/docs/v1.1"; // need to change
	private static final String AST_SCAN = "AST scan status";
	private static final String ZIPPED_SOURCE = "zippedSource";
	private static final String ENGINE_CONFIGURATION_ID_DEFAULT = "0";
	private static final String SCAN_WITH_SETTINGS_URL = "ast/scanWithSettings";
	private static final String PROJECT_ID_PATH_PARAM = "{projectId}";
	private static final String SCAN_ID_PATH_PARAM = "{scanId}";
	private int reportTimeoutSec = 5000;
	
	

	private Waiter<ReportStatus> reportWaiter=new Waiter<ReportStatus>("Scan report",10,3){@Override public ReportStatus getStatus(String id)throws IOException{return getReportStatus(id);}

	@Override public void printProgress(ReportStatus reportStatus){printReportProgress(reportStatus,getStartTimeSec());}

	@Override public ReportStatus resolveStatus(ReportStatus reportStatus){return resolveReportStatus(reportStatus);}

	// Report Waiter - overload methods
	private ReportStatus getReportStatus(String reportId)throws CxClientException,IOException{ReportStatus reportStatus=httpClient.getRequest(AST_GET_REPORT_STATUS.replace("{reportId}",reportId),CONTENT_TYPE_APPLICATION_JSON_V1,ReportStatus.class,200," report status",false);reportStatus.setBaseId(reportId);String currentStatus=reportStatus.getStatus().getValue();if(currentStatus.equals(ReportStatusEnum.INPROCESS.value())){reportStatus.setBaseStatus(Status.IN_PROGRESS);}else if(currentStatus.equals(ReportStatusEnum.FAILED.value())){reportStatus.setBaseStatus(Status.FAILED);}else{reportStatus.setBaseStatus(Status.SUCCEEDED); // todo
																																																																																																																																																														// fix
																																																																																																																																																														// it!!
	}

	return reportStatus;}

	private ReportStatus resolveReportStatus(ReportStatus reportStatus)throws CxClientException{if(reportStatus!=null){if(Status.SUCCEEDED==reportStatus.getBaseStatus()){return reportStatus;}else{throw new CxClientException("Generation of scan report [id="+reportStatus.getBaseId()+"] failed.");}}else{throw new CxClientException("Generation of scan report failed.");}}

	private void printReportProgress(ReportStatus reportStatus,long startTime){String reportType=reportStatus.getContentType().replace("application/","");log.info("Waiting for server to generate "+reportType+" report. "+(startTime+reportTimeoutSec-(System.currentTimeMillis()/1000))+" seconds left to timeout");}

	};

	private Waiter<CxARMStatus> cxARMWaiter=new Waiter<CxARMStatus>("CxARM policy violations",20,3){@Override public CxARMStatus getStatus(String id)throws IOException{return getCxARMStatus(id);}

	@Override public void printProgress(CxARMStatus cxARMStatus){printCxARMProgress(getStartTimeSec());}

	@Override public CxARMStatus resolveStatus(CxARMStatus cxARMStatus){return resolveCxARMStatus(cxARMStatus);}

	// CxARM Waiter - overload methods
	private CxARMStatus getCxARMStatus(String projectId)throws CxOneClientException,IOException{CxARMStatus cxARMStatus=httpClient.getRequest(AST_GET_CXARM_STATUS.replace(PROJECT_ID_PATH_PARAM,projectId),CONTENT_TYPE_APPLICATION_JSON_V1,CxARMStatus.class,200," cxOneARM status",false);cxOneARMStatus.setBaseId(projectId);

	String currentStatus=cxARMStatus.getStatus();if(currentStatus.equals(CxARMStatusEnum.IN_PROGRESS.value())){cxARMStatus.setBaseStatus(Status.IN_PROGRESS);}else if(currentStatus.equals(CxARMStatusEnum.FAILED.value())){cxARMStatus.setBaseStatus(Status.FAILED);}else if(currentStatus.equals(CxOneARMStatusEnum.FINISHED.value())){cxOneARMStatus.setBaseStatus(Status.SUCCEEDED);}else{cxOneARMStatus.setBaseStatus(Status.FAILED);}

	return cxARMStatus;}

	private void printCxARMProgress(long startTime){log.info("Waiting for server to retrieve policy violations. "+(startTime+cxOneARMTimeoutSec-(System.currentTimeMillis()/1000))+" seconds left to timeout");}

	private CxARMStatus resolveCxARMStatus(CxARMStatus cxARMStatus)throws CxOneClientException{if(cxARMStatus!=null){if(Status.SUCCEEDED==CxARMStatus.getBaseStatus()){return CxARMStatus;}else{throw new CxOneClientException("Getting policy violations of project [id="+cxARMStatus.getBaseId()+"] failed.");}}else{throw new CxOneClientException("Getting policy violations of project failed.");}}};

	CxOneWrapperClient(CxScanConfig config, Logger log) throws MalformedURLException {
		super(config, log);
		int interval = config.getProgressInterval() != null ? config.getProgressInterval() : 20;
		int retry = config.getConnectionRetries() != null ? config.getConnectionRetries() : 3;
		astWaiter = new Waiter<ResponseQueueScanStatus>("CxOneAST scan", interval, retry) {
			@Override
			public ResponseQueueScanStatus getStatus(String id) throws IOException {
				ResponseQueueScanStatus statusResponse = null;
				try {
					statusResponse = getASTScanStatus(id);
				} catch (CxOneHTTPClientException e) {
					try {
						ResponseAstScanStatus statusResponseTemp = getASTScanOutOfQueueStatus(id);
						statusResponse = statusResponseTemp.convertResponseAstScanStatusToResponseQueueScanStatus(statusResponseTemp);
					} catch (MalformedURLException exception) {
						throw new MalformedURLException("Failed with next error: " + exception);
					}
				}
				return statusResponse;
			}

			@Override
			public void printProgress(ResponseQueueScanStatus scanStatus) {
				printASTProgress(scanStatus, getStartTimeSec());
			}

			@Override
			public ResponseQueueScanStatus resolveStatus(ResponseQueueScanStatus scanStatus) {
				return resolveASTStatus(scanStatus);
			}
		};

	}

	@Override
	public Results init() {
		ASTResults1 initastResults = new ASTResults1();
		try {
			initiate();
			language = httpClient.getLanguageFromAccessToken();
			initastResults.setAstLanguage(language);
		} catch (CxOneClientException e) {
			log.error(e.getMessage());
			setState(State.FAILED);
			initastResults.setException(e);
		}
		return initastResults;
	}

	// **------ API ------**//

	// CREATE AST scan
	private void createASTScan(long projectId) {
		boolean dupScanFound = false;
		try {
			log.info("-----------------------------------Create CxOneAST Scan:------------------------------------");
			if (config.isAvoidDuplicateProjectScans() != null && config.isAvoidDuplicateProjectScans()
					&& projectHasQueuedScans(projectId)) {
				throw new CxOneClientException(MSG_AVOID_DUPLICATE_PROJECT_SCANS);
			}
			if (config.getRemoteType() == null) { // scan is local
				scanId = createLocalASTScan(projectId);
			} else {
				scanId = createRemoteSourceScan(projectId);
			}
			astResults.setAstLanguage(language);
			astResults.setScanId(scanId);
			log.info("AST scan created successfully: Scan ID is {}", scanId);
			astResults.setAstScanLink(config.getUrl(), scanId, projectId);
		} catch (Exception e) {
			setState(State.FAILED);
			if (!errorToBeSuppressed(e)) {
				astResults.setException(new CxOneClientException(e));
			}
		}
	}

	private long createLocalASTScan(long projectId) throws IOException {
		if (isScanWithSettingsSupported()) {
			log.info("Uploading the zipped source code.");
			PathFilter filter = new PathFilter(config.getAstFolderExclusions(), config.getAstFilterPattern(), log);
			byte[] zipFile = CxOneZipUtils.getZippedSources(config, filter, config.getSourceDir(), log);
			ScanWithSettingsResponse response = scanWithSettings(zipFile, projectId, false);
			return response.getId();
		} else {
			configureScanSettings(projectId);
			// prepare sources for scan
			PathFilter filter = new PathFilter(config.getAstFolderExclusions(), config.getAstFilterPattern(), log);
			byte[] zipFile = CxOneZipUtils.getZippedSources(config, filter, config.getSourceDir(), log);
			uploadZipFile(zipFile, projectId);

			return createScan(projectId);
		}
	}

	private long createRemoteSourceScan(long projectId) {
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
						.addTextBody("paths", config.getSourceDir()); // todo add paths to req OR using without
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
				builder.addTextBody("branch", config.getRemoteSrcBranch(), ContentType.APPLICATION_JSON); // todo add branch to req OR using without this else??
				builder.addBinaryBody("privateKey", req.getPrivateKey(), ContentType.MULTIPART_FORM_DATA, null);
				entity = builder.build();
			}
			break;
		default:
			log.error("todo");
			entity = new StringEntity("", StandardCharsets.UTF_8);

		}
		if (isScanWithSettingsSupported()) {
			createRemoteSourceRequest(projectId, entity, type.value(), isSSH);
			ScanWithSettingsResponse response = scanWithSettings(null, projectId, true);
			return response.getId();
		} else {
			configureScanSettings(projectId);
			createRemoteSourceRequest(projectId, entity, type.value(), isSSH);
			return createScan(projectId);

		}
	}

	private void configureScanSettings(long projectId) {

		ScanSettingResponse scanSettingResponse = getScanSetting(projectId);
		ScanSettingRequest scanSettingRequest = new ScanSettingRequest();
		scanSettingRequest.setEngineConfigurationId(scanSettingResponse.getEngineConfiguration().getId());
		scanSettingRequest.setProjectId(projectId);
		scanSettingRequest.setPresetId(config.getPresetId());
		if (config.getEngineConfigurationId() != null) {
			scanSettingRequest.setEngineConfigurationId(config.getEngineConfigurationId());
		}
		// Define createASTScan settings
		defineScanSetting(scanSettingRequest);
	}

	/*
	 * Suppress only those conditions for which it is generally acceptable to have
	 * plugin not error out so that rest of the pipeline can continue.
	 */
	private boolean errorToBeSuppressed(Exception error) {
		final String additionalMessage = "Build status will be marked successfull as this error is benign. Results from last scan will be displayed, if available.";
		boolean suppressed = false;

		// log actual error as it is first.
		log.error(error.getMessage());

		if (error instanceof ConditionTimeoutException && config.getContinueBuild()) {
			suppressed = true;
		}
		// Plugins will control if errors handled here will be ignored.
		else if (config.isIgnoreBenignErrors()) {

			if (error.getMessage().contains("source folder is empty,") || (astResults.getException() != null
					&& astResults.getException().getMessage().contains("No files to zip"))) {

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
				astResults = getLatestScanResults();
				if (super.isIsNewProject() && astResults.getAstScanLink() == null) {
					String message = String.format(
							"The project %s is a new project. Hence there is no last scan report to be shown.",
							config.getProjectName());
					log.info(message);
				}
			} catch (Exception okayToNotHaveResults) {
				astResults = null;
			}

			if (astResults == null)
				astResults = new ASTResults1();

			astResults.setException(null);
			setState(State.SKIPPED);

		}
		return suppressed;
	}

	// GET AST results + reports
	@Override
	public Results waitForScanResults() {
		try {
			log.info("------------------------------------Get CxOneAST Results:-----------------------------------");
			// wait for AST scan to finish
			log.info("Waiting for CxOneAST scan to finish.");
			try {

				astWaiter.waitForTaskToFinish(Long.toString(scanId), config.getAstScanTimeoutInMinutes() * 60, log);
				log.info("Retrieving AST scan results");
				// retrieve AST scan results
				astResults = retrieveASTResults(scanId, projectId);
			} catch (ConditionTimeoutException e) {

				if (!errorToBeSuppressed(e)) {
					// throw the exception so that caught by outer catch
					throw new Exception(e.getMessage());
				}
			} catch (CxOneClientException | IOException e) {
				if (!errorToBeSuppressed(e)) {
					// throw the exception so that caught by outer catch
					throw new Exception(e.getMessage());
				}
			}
			if (config.getEnablePolicyViolations()) {
				resolveASTViolation(astResults, projectId);
			}
			if (astResults.getAstScanLink() != null)
				ASTUtils.printASTResultsToConsole(astResults, config.getEnablePolicyViolations(), log);

			// PDF report
			if (config.getGeneratePDFReport()) {
				log.info("Generating PDF report");
				byte[] pdfReport = getScanReport(astResults.getScanId(), ReportType.PDF,
						CONTENT_TYPE_APPLICATION_PDF_V1);
				astResults.setPDFReport(pdfReport);
				if (config.getReportsDir() != null) {
					String now = new SimpleDateFormat("dd_MM_yyyy-HH_mm_ss").format(new Date());
					String pdfFileName = PDF_REPORT_NAME + "_" + now + ".pdf";
					String pdfLink = writePDFReport(pdfReport, config.getReportsDir(), pdfFileName, log);
					astResults.setAstPDFLink(pdfLink);
					astResults.setPdfFileName(pdfFileName);
				}
			}
			// CLI report/s
			else if (!config.getReports().isEmpty()) {
				for (Map.Entry<ReportType, String> report : config.getReports().entrySet()) {
					if (report != null) {
						log.info("Generating " + report.getKey().value() + " report");
						byte[] scanReport = getScanReport(astResults.getScanId(), report.getKey(),
								CONTENT_TYPE_APPLICATION_PDF_V1);
						writeReport(scanReport, report.getValue(), log);
						if (report.getKey().value().equals("PDF")) {
							astResults.setPDFReport(scanReport);
							astResults.setPdfFileName(report.getValue());
						}
					}
				}
			}
		} catch (Exception e) {
			if (!errorToBeSuppressed(e))
				astResults.setException(new CxOneClientException(e));
		}

		return astResults;
	}

	private void resolveASTViolation(ASTResults1 astResults1, long projectId) {
		try {
			cxARMWaiter.waitForTaskToFinish(Long.toString(projectId), cxARMTimeoutSec, log);
			getProjectViolatedPolicies(httpClient, config.getCxARMUrl(), projectId, AST.value())
					.forEach(astResults1::addPolicy);
		} catch (Exception ex) {
			throw new CxOneClientException(
					"CxARM is not available. Policy violations for AST cannot be calculated: " + ex.getMessage());
		}

	}

	private ASTResults1 retrieveASTResults(long scanId, long projectId) {
		ASTStatisticsResponse statisticsResults = getScanStatistics(scanId);

		astResults.setResults(scanId, statisticsResults, config.getUrl(), projectId);

		// AST detailed report
		if (config.getGenerateXmlReport() == null || config.getGenerateXmlReport()) {
			byte[] cxOneReport = getScanReport(astResults.getScanId(), ReportType.XML, CONTENT_TYPE_APPLICATION_XML_V1);
			CxOneXMLResults reportObj = convertToXMLResult(cxOneReport);
			astResults.setScanDetailedReport(reportObj, config);
			astResults.setRawXMLReport(cxOneReport);
		}
		astResults.setAstResultsReady(true);
		return astResults;
	}

	@Override
	public ASTResults1 getLatestScanResults() {
		astResults = new ASTResults1();
		astResults.setAstLanguage(language);
		try {
			log.info("---------------------------------Get Last CxOneAST Results:--------------------------------");
			List<LastScanResponse> scanList = getLatestASTStatus(projectId);
			for (LastScanResponse s : scanList) {
				if (CurrentStatus.FINISHED.value().equals(s.getStatus().getName())) {
					return retrieveASTResults(s.getId(), projectId);
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			astResults.setException(new CxOneClientException(e));
		}

		return astResults;
	}

	// Cancel SAST Scan
	public void cancelASTScan() throws IOException {
		UpdateScanStatusRequest request = new UpdateScanStatusRequest(CurrentStatus.CANCELED);
		String json = convertToJson(request);
		StringEntity entity = new StringEntity(json, StandardCharsets.UTF_8);
		httpClient.patchRequest(AST_QUEUE_SCAN_STATUS.replace(SCAN_ID_PATH_PARAM, Long.toString(scanId)),
				CONTENT_TYPE_APPLICATION_JSON_V1, entity, 200, "cancel AST scan");
		log.info("AST Scan canceled. (scanId: " + scanId + ")");
	}
	
	 private ResponseQueueScanStatus resolveASTStatus(ResponseQueueScanStatus scanStatus) {
	        if (scanStatus != null) {
	            if (Status.SUCCEEDED == scanStatus.getBaseStatus()) {
	                log.info("AST scan finished successfully.");
	                return scanStatus;
	            } else {
	                throw new CxOneClientException("AST scan cannot be completed. status [" + scanStatus.getStage().getValue() + "]: " + scanStatus.getStageDetails());
	            }
	        } else {
	            throw new CxOneClientException("AST scan cannot be completed.");
	        }
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

	@Override
	public Results initiateScan() {
		astResults = new ASTResults1();
		astResults.setAstLanguage(language);
		createASTScan(projectId);
		return astResults;
	}

	private byte[] getScanReport(long scanId, ReportType reportType, String contentType) throws IOException {
		CreateReportRequest reportRequest = new CreateReportRequest(scanId, reportType.name());
		CreateReportResponse createReportResponse = createScanReport(reportRequest);
		int reportId = createReportResponse.getReportId();
		reportWaiter.waitForTaskToFinish(Long.toString(reportId), reportTimeoutSec, log);

		return getReport(reportId, contentType);
	}

	private byte[] getReport(long reportId, String contentType) throws IOException {
		return httpClient.getRequest(AST_GET_REPORT.replace("{reportId}", Long.toString(reportId)), contentType,
				byte[].class, 200, " scan report: " + reportId, false);
	}

	// SCAN Waiter - overload methods
	public ResponseQueueScanStatus getASTScanStatus(String scanId) throws IOException {

		ResponseQueueScanStatus scanStatus = httpClient.getRequest(
				AST_QUEUE_SCAN_STATUS.replace(SCAN_ID_PATH_PARAM, scanId), CONTENT_TYPE_APPLICATION_JSON_V1,
				ResponseQueueScanStatus.class, 200, AST_SCAN, false);
		String currentStatus = scanStatus.getStage().getValue();

		if (CurrentStatus.FAILED.value().equals(currentStatus) || CurrentStatus.CANCELED.value().equals(currentStatus)
				|| CurrentStatus.DELETED.value().equals(currentStatus)
				|| CurrentStatus.UNKNOWN.value().equals(currentStatus)) {
			scanStatus.setBaseStatus(Status.FAILED);
		} else if (CurrentStatus.FINISHED.value().equals(currentStatus)) {
			scanStatus.setBaseStatus(Status.SUCCEEDED);
		} else {
			scanStatus.setBaseStatus(Status.IN_PROGRESS);
		}

		return scanStatus;
	}

	// Check AST scan status via ast/scans/{scanId} API
	public ResponseAstScanStatus getASTScanOutOfQueueStatus(String scanId) throws IOException {
		ResponseAstScanStatus scanStatus = httpClient.getRequest(AST_SCAN_STATUS.replace(SCAN_ID_PATH_PARAM, scanId),
				CONTENT_TYPE_APPLICATION_JSON_V1, ResponseAstScanStatus.class, 200, AST_SCAN, false);
		String currentStatus = scanStatus.getStatus().getName();

		if (CurrentStatus.FAILED.value().equals(currentStatus) || CurrentStatus.CANCELED.value().equals(currentStatus)
				|| CurrentStatus.DELETED.value().equals(currentStatus)
				|| CurrentStatus.UNKNOWN.value().equals(currentStatus)) {
			scanStatus.setBaseStatus(Status.FAILED);
		} else if (CurrentStatus.FINISHED.value().equals(currentStatus)) {
			scanStatus.setBaseStatus(Status.SUCCEEDED);
		} else {
			scanStatus.setBaseStatus(Status.IN_PROGRESS);
		}

		return scanStatus;
	}
	
	private void printASTProgress(ResponseQueueScanStatus scanStatus, long startTime) {
        String timestamp = ShragaUtils.getTimestampSince(startTime);

        String prefix = (scanStatus.getTotalPercent() < 10) ? " " : "";
        log.info("Waiting for AST scan results. Elapsed time: " + timestamp + ". " + prefix +
                scanStatus.getTotalPercent() + "% processed. Status: " + scanStatus.getStage().getValue() + ".");
    }

	private CreateReportResponse createScanReport(CreateReportRequest reportRequest) throws IOException {
		StringEntity entity = new StringEntity(convertToJson(reportRequest), StandardCharsets.UTF_8);
		return httpClient.postRequest(AST_CREATE_REPORT, CONTENT_TYPE_APPLICATION_JSON_V1, entity,
				CreateReportResponse.class, 202, "to create " + reportRequest.getReportType() + " scan report");
	}

	private ASTStatisticsResponse getScanStatistics(long scanId) throws IOException {
		return httpClient.getRequest(AST_SCAN_RESULTS_STATISTICS.replace(SCAN_ID_PATH_PARAM, Long.toString(scanId)),
				CONTENT_TYPE_APPLICATION_JSON_V1, ASTStatisticsResponse.class, 200, "SAST scan statistics", false);
	}
	
	private List<ResponseQueueScanStatus> getQueueScans(long projectId) throws IOException {
        return (List<ResponseQueueScanStatus>) httpClient.getRequest(AST_GET_QUEUED_SCANS.replace(PROJECT_ID_PATH_PARAM, Long.toString(projectId)), CONTENT_TYPE_APPLICATION_JSON_V1, ResponseQueueScanStatus.class, 200, "scans in the queue. (projectId: )" + projectId, true);
    }

	@SuppressWarnings("unchecked")
	private List<LastScanResponse> getLatestASTStatus(long projectId) throws IOException {
		return (List<LastScanResponse>) httpClient.getRequest(
				AST_GET_PROJECT_SCANS.replace(PROJECT_ID_PATH_PARAM, Long.toString(projectId)),
				CONTENT_TYPE_APPLICATION_JSON_V1, LastScanResponse.class, 200, "last AST scan ID", true);
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}
	
	 private void uploadZipFile(byte[] zipFile, long projectId) throws CxOneClientException, IOException {
	        log.info("Uploading zip file");

	        try (InputStream is = new ByteArrayInputStream(zipFile)) {
	            InputStreamBody streamBody = new InputStreamBody(is, ContentType.APPLICATION_OCTET_STREAM, ZIPPED_SOURCE);
	            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
	            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
	            builder.addPart(ZIPPED_SOURCE, streamBody);
	            HttpEntity entity = builder.build();
	            httpClient.postRequest(AST_ZIP_ATTACHMENTS.replace(PROJECT_ID_PATH_PARAM, Long.toString(projectId)), null, new BufferedHttpEntity(entity), null, 204, "upload ZIP file");
	        }
	    }


	private long createScan(long projectId) throws IOException {
		CreateScanRequest scanRequest = new CreateScanRequest(projectId, config.getIncremental(), config.getPublic(),
				config.getForceScan(), config.getScanComment() == null ? "" : config.getScanComment());

		log.info("Sending AST scan request");
		StringEntity entity = new StringEntity(convertToJson(scanRequest), StandardCharsets.UTF_8);
		CxOneID createScanResponse = httpClient.postRequest(AST_CREATE_SCAN, CONTENT_TYPE_APPLICATION_JSON_V1, entity,
				CxOneID.class, 201, "create new AST Scan");
		log.info(String.format("AST Scan created successfully. Link to project state: " + config.getUrl() + LINK_FORMAT,
				projectId));

		return createScanResponse.getId();
	}

	private void defineScanSetting(ScanSettingRequest scanSetting) throws IOException {
        StringEntity entity = new StringEntity(convertToJson(scanSetting), StandardCharsets.UTF_8);
        httpClient.putRequest(AST_UPDATE_SCAN_SETTINGS, CONTENT_TYPE_APPLICATION_JSON_V1, entity, CxOneID.class, 200, "define scan setting");
    }

	private ScanSettingResponse getScanSetting(long projectId) {
		return httpClient.getRequest(AST_GET_SCAN_SETTINGS.replace(PROJECT_ID_PATH_PARAM, Long.toString(projectId)),
				CONTENT_TYPE_APPLICATION_JSON_V1, ScanSettingResponse.class, 200, "Scan setting", false);
	}

	private ScanWithSettingsResponse scanWithSettings(byte[] zipFile, long projectId, boolean isRemote) {
		log.info("Uploading zip file");
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		if (!isRemote) {
			try (InputStream is = new ByteArrayInputStream(zipFile)) {
				InputStreamBody streamBody = new InputStreamBody(is, ContentType.APPLICATION_OCTET_STREAM,
						ZIPPED_SOURCE);
				builder.addPart(ZIPPED_SOURCE, streamBody);
			}
		}
		builder.addTextBody("projectId", Long.toString(projectId), ContentType.APPLICATION_JSON);
		if (config.getIsOverrideProjectSetting()) {
			builder.addTextBody("overrideProjectSetting", config.getIsOverrideProjectSetting() + "",
					ContentType.APPLICATION_JSON);
		} else {
			builder.addTextBody("overrideProjectSetting", super.isIsNewProject() ? "true" : "false",
					ContentType.APPLICATION_JSON);
		}
		builder.addTextBody("isIncremental", config.getIncremental().toString(), ContentType.APPLICATION_JSON);
		builder.addTextBody("isPublic", config.getPublic().toString(), ContentType.APPLICATION_JSON);
		builder.addTextBody("forceScan", config.getForceScan().toString(), ContentType.APPLICATION_JSON);
		builder.addTextBody("presetId", config.getPresetId().toString(), ContentType.APPLICATION_JSON);
		builder.addTextBody("comment", config.getScanComment() == null ? "" : config.getScanComment(),
				ContentType.APPLICATION_JSON);
		builder.addTextBody("engineConfigurationId",
				config.getEngineConfigurationId() != null ? config.getEngineConfigurationId().toString()
						: ENGINE_CONFIGURATION_ID_DEFAULT,
				ContentType.APPLICATION_JSON);

		builder.addTextBody("postScanActionId",
				config.getPostScanActionId() != null && config.getPostScanActionId() != 0
						? config.getPostScanActionId().toString()
						: "",
				ContentType.APPLICATION_JSON);

		builder.addTextBody("customFields", config.getCustomFields() != null ? config.getCustomFields() : "",
				ContentType.APPLICATION_JSON);

		HttpEntity entity = builder.build();
		return httpClient.postRequest(SCAN_WITH_SETTINGS_URL, null, new BufferedHttpEntity(entity),
				ScanWithSettingsResponse.class, 201, "upload ZIP file");
	}

	private CxOneID createRemoteSourceRequest(long projectId, HttpEntity entity, String sourceType, boolean isSSH) {
		return httpClient.postRequest(
				String.format(AST_CREATE_REMOTE_SOURCE_SCAN, projectId, sourceType, isSSH ? "ssh" : ""),
				isSSH ? null : CONTENT_TYPE_APPLICATION_JSON_V1, entity, CxOneID.class, 204,
				"create " + sourceType + " remote source scan setting");
	}

	private boolean isScanWithSettingsSupported() {
		 try {
	            HashMap swaggerResponse = this.httpClient.getRequest(SWAGGER_LOCATION, CONTENT_TYPE_APPLICATION_JSON, HashMap.class, 200, AST_SCAN, false);
	            return swaggerResponse.toString().contains("/ast/scanWithSettings");
	        } catch (Exception e) {
	            return false;
	        }
	}

	private void excludeProjectSettings(long projectId) {
		    String excludeFoldersPattern = Arrays.stream(config.getAstFolderExclusions().split(",")).map(String::trim).collect(Collectors.joining(","));
	        String excludeFilesPattern = Arrays.stream(config.getAstFilterPattern().split(",")).map(String::trim).map(file -> file.replace("!**/", "")).collect(Collectors.joining(","));
	        ExcludeSettingsRequest excludeSettingsRequest = new ExcludeSettingsRequest(excludeFoldersPattern, excludeFilesPattern);
	        StringEntity entity = new StringEntity(convertToJson(excludeSettingsRequest), StandardCharsets.UTF_8);
	        log.info("Exclude folders pattern: " + excludeFoldersPattern);
	        log.info("Exclude files pattern: " + excludeFilesPattern);
	        httpClient.putRequest(String.format(AST_EXCLUDE_FOLDERS_FILES_PATTERNS, projectId), CONTENT_TYPE_APPLICATION_JSON_V1, entity, null, 200, "exclude project's settings");		
	}
	
	private void uploadZipFile(byte[] zipFile, long projectId) throws CxClientException, IOException {
        log.info("Uploading zip file");

        try (InputStream is = new ByteArrayInputStream(zipFile)) {
            InputStreamBody streamBody = new InputStreamBody(is, ContentType.APPLICATION_OCTET_STREAM, ZIPPED_SOURCE);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            builder.addPart(ZIPPED_SOURCE, streamBody);
            HttpEntity entity = builder.build();
            httpClient.postRequest(AST_ZIP_ATTACHMENTS.replace(PROJECT_ID_PATH_PARAM, Long.toString(projectId)), null, new BufferedHttpEntity(entity), null, 204, "upload ZIP file");
        }
    }

	public List<LastScanResponse> getLatestASTStatus(long projectId) throws IOException {
        return (List<LastScanResponse>) httpClient.getRequest(AST_GET_PROJECT_SCANS.replace(PROJECT_ID_PATH_PARAM, Long.toString(projectId)), CONTENT_TYPE_APPLICATION_JSON_V1, LastScanResponse.class, 200, "last AST scan ID", true);
    }

	private ASTResults retrieveASTResults(long scanId, long projectId) throws IOException {

		ASTStatisticsResponse statisticsResults = getScanStatistics(scanId);

		astResults.setResults(scanId, statisticsResults, config.getUrl(), projectId);

		// AST detailed report
		if (config.getGenerateXmlReport() == null || config.getGenerateXmlReport()) {
			byte[] cxOneReport = getScanReport(astResults.getScanId(), ReportType.XML, CONTENT_TYPE_APPLICATION_XML_V1);
			CxXMLResults reportObj = convertToXMLResult(cxOneReport);
			astResults.setScanDetailedReport(reportObj, config);
			astResults.setRawXMLReport(cxOneReport);
		}
		astResults.setSastResultsReady(true);
		return astResults;
	}

	private ASTStatisticsResponse getScanStatistics(long scanId) throws IOException {
		return httpClient.getRequest(AST_SCAN_RESULTS_STATISTICS.replace(SCAN_ID_PATH_PARAM, Long.toString(scanId)), CONTENT_TYPE_APPLICATION_JSON_V1, ASTStatisticsResponse.class, 200, "AST scan statistics", false);
	}

	private byte[] getScanReport(long scanId, ReportType reportType, String contentType) throws IOException {
		CreateReportRequest reportRequest = new CreateReportRequest(scanId, reportType.name());
        CreateReportResponse createReportResponse = createScanReport(reportRequest);
        int reportId = createReportResponse.getReportId();
        reportWaiter.waitForTaskToFinish(Long.toString(reportId), reportTimeoutSec, log);

        return getReport(reportId, contentType);
	}

	 /*
     * Suppress only those conditions for which it is generally acceptable
     * to have plugin not error out so that rest of the pipeline can continue.
     */
	private boolean errorToBeSuppressed(Exception error) {

		final String additionalMessage = "Build status will be marked successfull as this error is benign. Results from last scan will be displayed, if available.";
		boolean suppressed = false;

		// log actual error as it is first.
		log.error(error.getMessage());

		if (error instanceof ConditionTimeoutException && config.getContinueBuild()) {
			suppressed = true;
		}
		// Plugins will control if errors handled here will be ignored.
		else if (config.isIgnoreBenignErrors()) {

			if (error.getMessage().contains("source folder is empty,") || (astResults.getException() != null
					&& astResults.getException().getMessage().contains("No files to zip"))) {

				suppressed = true;
			} else if (error.getMessage().contains("No files to zip")) {
				suppressed = true;
			} else if (error.getMessage().equalsIgnoreCase(MSG_AVOID_DUPLICATE_PROJECT_SCANS)) {
				suppressed = true;
			}
		}

	}

	private void resolveASTViolation(ASTResults astResults , long projectId) {
		try {
			cxOneARMWaiter.waitForTaskToFinish(Long.toString(projectId), cxOneARMTimeoutSec, log);
			getProjectViolatedPolicies(httpClient, config.getCxARMUrl(), projectId, AST.value())
					.forEach(astResults::addPolicy);
		} catch (Exception ex) {
			throw new CxClientException(
					"CxOneARM is not available. Policy violations for AST cannot be calculated: " + ex.getMessage());
		}
	}

	private Waiter<CxOneARMStatus> cxARMWaiter = new Waiter<CxOneARMStatus>("CxOneARM policy violations", 20, 3) {
	        @Override
	        public CxOneARMStatus getStatus(String id) throws IOException {
	            return getCxOneARMStatus(id);
	        }
	
	private List<ResponseQueueScanStatus> getQueueScans(long projectId) throws IOException {
        return (List<ResponseQueueScanStatus>) httpClient.getRequest(AST_GET_QUEUED_SCANS.replace(PROJECT_ID_PATH_PARAM, Long.toString(projectId)), CONTENT_TYPE_APPLICATION_JSON_V1, ResponseQueueScanStatus.class, 200, "scans in the queue. (projectId: )" + projectId, true);
		return null;
    }
	
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
    
    private boolean isScanWithSettingsSupported() {
        try {
            HashMap swaggerResponse = this.httpClient.getRequest(SWAGGER_LOCATION, CONTENT_TYPE_APPLICATION_JSON, HashMap.class, 200, AST_SCAN, false);
            return swaggerResponse.toString().contains("/ast/scanWithSettings");
        } catch (Exception e) {
            return false;
        }
    }
    
    private long createLocalASTScan(long projectId) throws IOException {
        if (isScanWithSettingsSupported()) {
            log.info("Uploading the zipped source code.");
            PathFilter filter = new PathFilter(config.getAstFolderExclusions(), config.getAstFilterPattern(), log);
            byte[] zipFile = CxZipUtils.getZippedSources(config, filter, config.getSourceDir(), log);
            ScanWithSettingsResponse response = scanWithSettings(zipFile, projectId, false);
            return response.getId();
        } else {
            configureScanSettings(projectId);
            //prepare sources for scan
            PathFilter filter = new PathFilter(config.getAstFolderExclusions(), config.getAstFilterPattern(), log);
            byte[] zipFile = CxZipUtils.getZippedSources(config, filter, config.getSourceDir(), log);
            uploadZipFile(zipFile, projectId);

            return createScan(projectId);
        }
}

	private ScanSettingResponse scanWithSettings(byte[] zipFile, long projectId, boolean isRemote) {
		 log.info("Uploading zip file");
	        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
	        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
	        if (!isRemote) {
	            try (InputStream is = new ByteArrayInputStream(zipFile)) {
	                InputStreamBody streamBody = new InputStreamBody(is, ContentType.APPLICATION_OCTET_STREAM, ZIPPED_SOURCE);
	                builder.addPart(ZIPPED_SOURCE, streamBody);
	            }
	        }
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
	            //Define create ASTScan settings
	            defineScanSetting(scanSettingRequest);
	        }
	        
	        public ScanSettingResponse getScanSetting(long projectId) throws IOException {
	            return httpClient.getRequest(AST_GET_SCAN_SETTINGS.replace(PROJECT_ID_PATH_PARAM, Long.toString(projectId)), CONTENT_TYPE_APPLICATION_JSON_V1, ScanSettingResponse.class, 200, "Scan setting", false);
	        }
	        
	        private void defineScanSetting(ScanSettingRequest scanSetting) throws IOException {
	            StringEntity entity = new StringEntity(convertToJson(scanSetting), StandardCharsets.UTF_8);
	            httpClient.putRequest(AST_UPDATE_SCAN_SETTINGS, CONTENT_TYPE_APPLICATION_JSON_V1, entity, CxID.class, 200, "define scan setting");
	        }
	        
	
	}
	
