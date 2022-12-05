package com.cx.restclient;

import static com.cx.restclient.httpClient.utils.ContentType.CONTENT_TYPE_APPLICATION_XML_V1;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import org.awaitility.core.ConditionTimeoutException;
import org.slf4j.Logger;

import com.checkmarx.one.CxOneClient;
import com.checkmarx.one.dto.CxOneConfig;
import com.checkmarx.one.dto.TaskStatus;
import com.checkmarx.one.dto.report.CreateAstReportRequest;
import com.checkmarx.one.dto.resultsummary.ResultSummaryResponse;
import com.checkmarx.one.dto.scan.ResultDetailsResponse;
import com.checkmarx.one.dto.scan.ResultsRequest;
import com.checkmarx.one.dto.scan.ResultsResponse;
import com.checkmarx.one.dto.scan.ResultsSummaryRequest;
import com.checkmarx.one.dto.scan.ScanIdConfigRequest;
import com.checkmarx.one.dto.scan.ScanResponse;
import com.checkmarx.one.dto.scan.ScanStatusResponse;
import com.cx.restclient.ast.dto.sast.AstSastResults;
import com.cx.restclient.ast.dto.sast.AstSastUtils;
import com.cx.restclient.astglue.CxConfigParamsTransformerServiceFactory;
import com.cx.restclient.astglue.TransformerService;
import com.cx.restclient.common.Scanner;
import com.cx.restclient.configuration.CxScanConfig;
import com.cx.restclient.dto.Results;
import com.cx.restclient.dto.ScannerType;
import com.cx.restclient.exception.CxClientException;
import com.cx.restclient.sast.dto.CreateReportResponse;
import com.cx.restclient.sast.dto.CxXMLResults;
import com.cx.restclient.sast.dto.ReportType;
import com.cx.restclient.sast.utils.State;

public class CxOneWrapperClient implements Scanner{
    private CxScanConfig config;
    private CxOneConfig oneConfig ;
    private CxOneClient cxOneClient;
    private Logger log;
    private List<String> groups;
    private State state = State.SUCCESS;
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
		log.info("SAST scan created successfully: Scan ID is {}", scanId);
		
        return astSastResults;
	}

	@Override
	public Results waitForScanResults() {
		try {
        try {
        	log.info("------------------------------------Get AST Results:-----------------------------------");
        	log.info("Waiting for AST scan to finish.");
        	ScanStatusResponse scanStatusRes = cxOneClient.waitForScanToResolve(scanId);
        	ResultsResponse results = cxOneClient.getResults(new ResultsRequest(scanStatusRes.getId(), null, null));
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
          //PDF report
            if (config.getGeneratePDFReport()) {
                log.info("Generating PDF report");
                //TODO : AFter we confirm PDF report is supported by AST, we can have the code to generate and write results to PDF doc.
//                For now just do nothing if user has selected "Generate PDF"
                
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

		ResultSummaryResponse statisticsResults = getScanStatistics(scanId);

	        astSastResults.setResults(scanId, statisticsResults, config.getUrl(), projectId);

	        //AST detailed report
	        if (config.getGenerateXmlReport() == null || config.getGenerateXmlReport()) {
	            byte[] cxReport = getScanReport(astSastResults.getScanId(), ReportType.XML, CONTENT_TYPE_APPLICATION_XML_V1);
	            ResultsResponse reportObj = AstSastUtils.convertToXMLResult(cxReport);
	            astSastResults.setScanDetailedReport(reportObj,config);
//	            astSastResults.setRawXMLReport(cxReport);
	        }
	        astSastResults.setSastResultsReady(true);
	        return astSastResults;
	    }
	
	 private byte[] getScanReport(String scanId, ReportType reportType, String contentType) throws IOException {
		 //TODO : Remove compilation issues
//	        CreateAstReportRequest reportRequest = new CreateAstReportRequest(scanId, reportType.name());
//	        CreateReportResponse createReportResponse = createScanReport(reportRequest);
//	        int reportId = createReportResponse.getReportId();
//	        reportWaiter.waitForTaskToFinish(Long.toString(reportId), reportTimeoutSec, log);
//
//	        return getReport(reportId, contentType);
		 return null;
	    }
	 
	 private ResultSummaryResponse getScanStatistics(String scanId) throws IOException {
			ResultsSummaryRequest summaryPayLoad = new ResultsSummaryRequest(scanId, true, false, false, false, language);
			ResultSummaryResponse resultsSummary = cxOneClient.getResultsSummary(summaryPayLoad);
	        return resultsSummary;
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
				if (oneConfig.isIsNewProject()) {
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
