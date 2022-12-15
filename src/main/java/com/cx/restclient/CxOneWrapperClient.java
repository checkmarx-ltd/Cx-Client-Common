package com.cx.restclient;

import static com.cx.restclient.httpClient.utils.ContentType.CONTENT_TYPE_APPLICATION_PDF_V1;
import static com.cx.restclient.sast.utils.SASTParam.PDF_REPORT_NAME;
import static com.cx.restclient.sast.utils.SASTUtils.writePDFReport;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.awaitility.core.ConditionTimeoutException;
import org.slf4j.Logger;

import com.checkmarx.one.CxOneClient;
import com.checkmarx.one.dto.CxOneConfig;
import com.checkmarx.one.dto.TaskStatus;
import com.checkmarx.one.dto.report.CreateAstReportRequest;
import com.checkmarx.one.dto.report.CreateAstReportResponse;
import com.checkmarx.one.dto.resultsummary.ResultSummaryResponse;
import com.checkmarx.one.dto.resultsummary.SeverityEnum;
import com.checkmarx.one.dto.scan.ResultsRequest;
import com.checkmarx.one.dto.scan.ResultsSummaryRequest;
import com.checkmarx.one.dto.scan.ScanIdConfigRequest;
import com.checkmarx.one.dto.scan.ScanResponse;
import com.checkmarx.one.dto.scan.ScanStatusResponse;
import com.checkmarx.one.dto.scan.ScansResponse;
import com.checkmarx.one.dto.scan.sast.SastResultDetails;
import com.checkmarx.one.dto.scan.sast.SastResultsResponse;
import com.checkmarx.one.dto.scan.sast.ScanMetricsResponse;
import com.cx.restclient.ast.dto.sast.AstSastQueryCounter;
import com.cx.restclient.ast.dto.sast.AstSastResults;
import com.cx.restclient.ast.dto.sast.AstSastUtils;
import com.cx.restclient.astglue.CxConfigParamsTransformerServiceFactory;
import com.cx.restclient.astglue.TransformerService;
import com.cx.restclient.common.Scanner;
import com.cx.restclient.configuration.CxScanConfig;
import com.cx.restclient.dto.Results;
import com.cx.restclient.dto.ScannerType;
import com.cx.restclient.exception.CxClientException;
import com.cx.restclient.sast.dto.ReportType;
import com.cx.restclient.sast.utils.State;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class CxOneWrapperClient implements Scanner{
    private CxScanConfig config;
    private CxOneConfig oneConfig ;
    private CxOneClient cxOneClient;
    private Logger log;
    private List<String> groups;
    private State state = State.SUCCESS;
    private int reportTimeoutSec = 5000;
    private String scanId;
    private String projectId;
    private AstSastResults astSastResults = new AstSastResults();
    private static final String MSG_AVOID_DUPLICATE_PROJECT_SCANS= "\nAvoid duplicate project scans in queue\n";
    private String language = "en-US";
    
	CxOneWrapperClient(CxScanConfig config, Logger log) throws MalformedURLException {
		this.config = config;
        this.log = log;
		CxConfigParamsTransformerServiceFactory factory = new CxConfigParamsTransformerServiceFactory();
		TransformerService service = factory.create(ScannerType.SAST, ScannerType.CXONE_SAST, config, log);
		oneConfig = service.getCxOneConfig();
        oneConfig.setAccessControlBaseUrl(config.getAccessControlBaseUrl());
        oneConfig.setApiBaseUrl(config.getApiBaseUrl());
        oneConfig.setClientId(config.getClientId());
        oneConfig.setClientSecret(config.getClientSecret());
        oneConfig.setTenant(config.getTenant());
        cxOneClient = new CxOneClient(oneConfig);
        projectId = oneConfig.getScanConfig().getProject().getId();
	}

	@Override
	public Results init() {
		AstSastResults initAstSastResults = new AstSastResults();
        try {
            initiate();
            language = cxOneClient.getLanguageFromAccessToken();
        } catch (CxClientException e) {
            setState(State.FAILED);
            initAstSastResults.setException(e);
        	log.error("error while initializing AST scanner");
        	log.error(e.getMessage());
        }
        return initAstSastResults;
	}

	private void initiate() {
		try {
            if (config.isSubmitToAST()) {
            	// This will invoke init() of CxHttpClient and then generate access token within this call.Login happens in this method
    			cxOneClient.init();
    			String accessToken = cxOneClient.getAccessToken();
    			log.info("Login successful to AST" + accessToken);
            }
        } catch (Exception e) {
            throw new CxClientException(e);
        }		
	}
	@Override
	public Results initiateScan() {
		astSastResults = new AstSastResults();
		ScanResponse results = cxOneClient.scan(oneConfig.getScanConfig());
		scanId = (String)results.getId();
		astSastResults.setScanId(scanId);
		astSastResults.setScanResponse(results);
		log.info("CxOne SAST scan created successfully: Scan ID is {}", scanId);
		
        return astSastResults;
	}

	@Override
	public Results waitForScanResults() {
		try {
        try {
        	log.info("------------------------------------Get AST Results:-----------------------------------");
        	log.info("Waiting for AST scan to finish.");
        	ScanStatusResponse scanStatusRes = cxOneClient.waitForScanToResolve(scanId);
        	log.info("Retrieving AST scan results");
        	astSastResults = retrieveAstSastResults(scanStatusRes.getId(), projectId);
        	log.info("Retrieved AST scan results.");
        }
        	catch (ConditionTimeoutException e) {
    			
    			if (!errorToBeSuppressed(e)) {
    				// throw the exception so that caught by outer catch
    				throw new Exception(e.getMessage());
    			}
    		} catch (CxClientException e) {
    			if (!errorToBeSuppressed(e)) {
    				// throw the exception so that caught by outer catch
    				throw new Exception(e.getMessage());
    			}
    		} 
        if (config.getEnablePolicyViolations()) {
            // DO Nothing . Policy violation feature remains silent
        }
		if (astSastResults.getAstSastScanLink() != null)
			AstSastUtils.printAstSASTResultsToConsole(astSastResults, config.getEnablePolicyViolations(), log);

          //PDF report
            if (config.getGeneratePDFReport()) {
                log.info("Generating PDF report");
                byte[] pdfReport = getScanReport(astSastResults.getScanId(), ReportType.PDF, CONTENT_TYPE_APPLICATION_PDF_V1);
                astSastResults.setPDFReport(pdfReport);
                if (config.getReportsDir() != null) {
                    String now = new SimpleDateFormat("dd_MM_yyyy-HH_mm_ss").format(new Date());
                    String pdfFileName = PDF_REPORT_NAME + "_" + now + ".pdf";
                    String pdfLink = writePDFReport(pdfReport, config.getReportsDir(), pdfFileName, log);
                    astSastResults.setASTSastPDFLink(pdfLink);
                    astSastResults.setPdfFileName(pdfFileName);
                }
                            
                }
        } catch (Exception e) {            
            if(!errorToBeSuppressed(e))
            	astSastResults.setException(new CxClientException(e));
        }

//            return results.getResults();
        return astSastResults;
    }

	@Override
	public AstSastResults getLatestScanResults() {
		ScanIdConfigRequest scanIdConfig = new ScanIdConfigRequest();
		scanIdConfig.setField("scan-ids");
		scanIdConfig.setProjectId(projectId);
		scanIdConfig.setSort("+created_at");
		scanIdConfig.setStatuses("Completed");
		scanIdConfig.setLimit(1);
		scanIdConfig.setOffset(0);

		String lastScanId = cxOneClient.getLatestScanId(scanIdConfig);
		ScanStatusResponse lastScanResponse = cxOneClient.getScanStatus(lastScanId);
		if (TaskStatus.COMPLETED.getValue().equals(lastScanResponse.getStatus().getValue())) {
            try {
				return retrieveAstSastResults(lastScanId, projectId);
			} catch (Exception e) {
	            log.error(e.getMessage());
	            astSastResults.setException(new CxClientException(e));
	        }
        }
		return null;
	}

	private AstSastResults retrieveAstSastResults(String scanId, String projectId) throws IOException {

			ResultSummaryResponse resultsSummaryRes = getScanStatistics(scanId);
			ScanMetricsResponse scanMetrics = cxOneClient.getScanMetrics(scanId);
	        astSastResults.setResults(scanId, resultsSummaryRes, oneConfig, projectId);

        	SastResultsResponse results = cxOneClient.getSASTResults(new ResultsRequest(scanId, null, null));
        	ScansResponse scanDetails = cxOneClient.getScanDetails(scanId);
        	populateAstSastResults(results, scanDetails);
            astSastResults.setScanDetailedReport(results,scanMetrics, config);
	        
	        astSastResults.setAstSastResultsReady(true);
	        return astSastResults;
	    }
	
	 private byte[] getScanReport(String scanId, ReportType reportType, String contentType) throws IOException {
	        CreateAstReportRequest reportRequest = new CreateAstReportRequest(scanId, reportType.name());
	        CreateAstReportResponse reportResponse = cxOneClient.createScanReport(reportRequest);
	        String reportId = reportResponse.getReportId();
	        cxOneClient.waitForReportToResolve(reportId, reportTimeoutSec, log);

	        return cxOneClient.getReportInBytes(reportId);
	    }
	 
	 private ResultSummaryResponse getScanStatistics(String scanId) throws IOException {
			ResultsSummaryRequest summaryPayLoad = new ResultsSummaryRequest(scanId, true, false, false, false, language);
			ResultSummaryResponse resultsSummary = cxOneClient.getResultsSummary(summaryPayLoad);
	        return resultsSummary;
	    }
	 
	private void populateAstSastResults(SastResultsResponse results, ScansResponse scanDetails) throws CxClientException, JsonMappingException, JsonProcessingException {
		
		astSastResults.updateAstSastResult(scanDetails);
		
		for (SastResultDetails q : results.getContent().getResults()) {
			List<SastResultDetails> qResult = results.getContent().getResults();
			for (int i = 0; i < qResult.size(); i++) {
				SastResultDetails result = qResult.get(i);
				AstSastQueryCounter queryCtr = new AstSastQueryCounter();
				SeverityEnum sev = SeverityEnum.valueOf(result.getSeverity());
				switch (sev) {
				case HIGH:
					queryCtr.incrementHighCounter(result.getQueryName());
					break;
				case MEDIUM:
					queryCtr.incrementMediumCounter(result.getQueryName());
					break;
				case LOW:
					queryCtr.incrementLowCounter(result.getQueryName());
					break;
				case INFO:
					queryCtr.incrementInfoCounter(result.getQueryName());
					break;
				}
			}
		}
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
		else if(config.isIgnoreBenignErrors()) {
			
			if (error.getMessage().contains("source folder is empty,") || (astSastResults.getException() != null
					&& astSastResults.getException().getMessage().contains("No files to zip"))) {
				
				suppressed = true;
			} else if (error.getMessage().contains("No files to zip")) {
				suppressed = true;
			} else if (error.getMessage().equalsIgnoreCase(MSG_AVOID_DUPLICATE_PROJECT_SCANS)) {
				suppressed = true;
			}
		}
		
		if(suppressed) {			
			log.info(additionalMessage);
			try {
				astSastResults = getLatestScanResults();
				if (oneConfig.isIsNewProject() && astSastResults.getAstSastScanLink() == null) {
					String message = String
							.format("The project %s is a new project. Hence there is no last scan report to be shown.", config.getProjectName());
					log.info(message);
				}
			}catch(Exception okayToNotHaveResults){
				astSastResults = null;
			}
			
			if(astSastResults == null)
				astSastResults = new AstSastResults();
			
			astSastResults.setException(null);
			setState(State.SKIPPED);						
			
		}		
		return suppressed;
	}
	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}
	 public State getState() {
	        return state;
	    }

	    public void setState(State state) {
	        this.state = state;
	    }

}
