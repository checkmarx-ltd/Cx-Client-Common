package com.cx.restclient;

import static com.cx.restclient.httpClient.utils.ContentType.CONTENT_TYPE_APPLICATION_PDF_V1;
import static com.cx.restclient.httpClient.utils.ContentType.CONTENT_TYPE_APPLICATION_XML_V1;
import static com.cx.restclient.sast.utils.SASTParam.PDF_REPORT_NAME;
import static com.cx.restclient.sast.utils.SASTUtils.convertToXMLResult;
import static com.cx.restclient.sast.utils.SASTUtils.writePDFReport;
import static com.cx.restclient.sast.utils.SASTUtils.writeReport;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.awaitility.core.ConditionTimeoutException;
import org.slf4j.Logger;

import com.checkmarx.one.CxOneClient;
import com.checkmarx.one.dto.CxOneConfig;
import com.checkmarx.one.dto.TaskStatus;
import com.checkmarx.one.dto.scan.ResultsRequest;
import com.checkmarx.one.dto.scan.ResultsResponse;
import com.checkmarx.one.dto.scan.ResultsSummaryRequest;
import com.checkmarx.one.dto.scan.ScanIdConfigRequest;
import com.checkmarx.one.dto.scan.ScanResponse;
import com.checkmarx.one.dto.scan.ScanStatusResponse;
import com.cx.restclient.ast.dto.sast.AstSastResults;
import com.cx.restclient.astglue.CxConfigParamsTransformerServiceFactory;
import com.cx.restclient.astglue.TransformerService;
import com.cx.restclient.common.Scanner;
import com.cx.restclient.configuration.CxScanConfig;
import com.cx.restclient.dto.Results;
import com.cx.restclient.dto.ScannerType;
import com.cx.restclient.exception.CxClientException;
import com.cx.restclient.sast.dto.CxXMLResults;
import com.cx.restclient.sast.dto.ReportType;
import com.cx.restclient.sast.dto.SASTStatisticsResponse;
import com.cx.restclient.sast.utils.SASTUtils;
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
    private String language = "en-US";
    
	CxOneWrapperClient(CxScanConfig config, Logger log) throws MalformedURLException {
		this.config = config;
        this.log = log;
		CxConfigParamsTransformerServiceFactory factory = new CxConfigParamsTransformerServiceFactory();
		TransformerService service = factory.create(ScannerType.SAST, ScannerType.CXONE_SAST, config);
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
    			// TODO : Remove this AT
    			log.info("Login successful to AST" + accessToken);
//                resolveTeam();
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
        	log.info("------------------------------------Get AST Results:-----------------------------------");
        	log.info("Waiting for AST scan to finish.");
        	ScanStatusResponse scanStatusRes = cxOneClient.waitForScanToResolve(scanId);
        	ResultsResponse results = cxOneClient.getResults(new ResultsRequest(scanStatusRes.getId(), null, null));
            
            
          //PDF report
            if (config.getGeneratePDFReport()) {
                log.info("Generating PDF report");
                //TODO : AFter we confirm PDF report is supported by AST, we can have the code to generate and write results to PDF doc.
//                For now just do nothing if user has selected "Generate PDF"
                
                }
//            return results.getResults();
            return astSastResults;
        } catch (Exception e) {            
            	astSastResults.setException(new CxClientException(e));
        }

        return astSastResults;
    }

	@Override
	public Results getLatestScanResults() {
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
				return retrieveSASTResults(lastScanId, projectId);
			} catch (Exception e) {
	            log.error(e.getMessage());
	            astSastResults.setException(new CxClientException(e));
	        }
        }
		return null;
	}

	private AstSastResults retrieveSASTResults(String scanId, String projectId) throws IOException {

//		 ResultsSummaryRequest summaryPayLoad = new ResultsSummaryRequest(scanId, includeSeverityStatus, includeQueries, includeFiles, applyPredicates, language)
//	        SASTStatisticsResponse statisticsResults = getScanStatistics(scanId);
//
//	        astSastResults.setResults(scanId, statisticsResults, config.getUrl(), projectId);
//
//	        //SAST detailed report
//	        if (config.getGenerateXmlReport() == null || config.getGenerateXmlReport()) {
//	            byte[] cxReport = getScanReport(astSastResults.getScanId(), ReportType.XML, CONTENT_TYPE_APPLICATION_XML_V1);
//	            CxXMLResults reportObj = convertToXMLResult(cxReport);
//	            astSastResults.setScanDetailedReport(reportObj,config);
//	            astSastResults.setRawXMLReport(cxReport);
//	        }
//	        astSastResults.setSastResultsReady(true);
	        return astSastResults;
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
