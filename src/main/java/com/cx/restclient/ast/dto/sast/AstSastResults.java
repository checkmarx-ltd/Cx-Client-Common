package com.cx.restclient.ast.dto.sast;

import static com.cx.restclient.sast.utils.SASTParam.PROJECT_LINK_FORMAT;
import static com.cx.restclient.sast.utils.SASTParam.SCAN_LINK_FORMAT;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.checkmarx.one.dto.resultsummary.ResultSummaryResponse;
import com.checkmarx.one.dto.scan.ResultDataResponse;
import com.checkmarx.one.dto.scan.ResultDetailsResponse;
import com.checkmarx.one.dto.scan.ResultsResponse;
import com.cx.restclient.ast.dto.sast.report.AstSastSummaryResults;
import com.cx.restclient.ast.dto.sast.report.Finding;
import com.cx.restclient.configuration.CxScanConfig;
import com.cx.restclient.cxArm.dto.Policy;
import com.cx.restclient.dto.Results;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AstSastResults extends Results implements Serializable {
    private String scanId;
    private AstSastSummaryResults summary;
    private String webReportLink;
    private List<Finding> findings;
    private int high = 0;
    private int medium = 0;
    private int low = 0;
    private int information = 0;
    
    private int newHigh = 0;
    private int newMedium = 0;
    private int newLow = 0;
    private int newInfo = 0;

    private String astSastScanLink;
    private String sastProjectLink;
    private String sastPDFLink;

    private String scanStart = "";
    private String scanTime = "";
    private String scanStartTime = "";
    private String scanEndTime = "";
    
    private String filesScanned;
    private String LOC;
    private List<ResultDataResponse> queryList;

    private byte[] rawXMLReport;
    private byte[] PDFReport;
    private String pdfFileName;

    private List<Policy> sastPolicies = new ArrayList<>();
	private boolean astSastResultsReady;
	private String astScanLink;
	private String astSastProjectLink;

    public enum Severity {
        High, Medium, Low, Information;
    }
    
    public String getScanId() {
        return scanId;
    }

    public void setScanId(String scanId) {
        this.scanId = scanId;
    }
    
    public void setResults(String scanId, ResultSummaryResponse statisticsResults, String url, String projectId) {
        setScanId(scanId);
        setHigh(statisticsResults.getSastCounters().getSeverityCounters().size());
        //TODO : To uncomment after implementing individual method to return individual severity count
//        setMedium(statisticsResults.getMediumSeverity());
//        setLow(statisticsResults.getLowSeverity());
//        setInformation(statisticsResults.getInfoSeverity());
        setAstSastScanLink(url, scanId, projectId);
        setAstSastProjectLink(url, projectId);
    }
 public void setScanDetailedReport(ResultsResponse reportObj,CxScanConfig config) throws IOException {
    	
        for (ResultDetailsResponse q : reportObj.getResults()) {
            List<ResultDetailsResponse> qResult = reportObj.getResults();
            for (int i = 0; i < qResult.size(); i++) {
            	ResultDetailsResponse result = qResult.get(i);
                 if ("New".equals(result.getStatus())) {
                    Severity sev = Severity.valueOf(result.getSeverity());
                    switch (sev) {
                        case High:
                            newHigh++;
                            break;
                        case Medium:
                            newMedium++;
                            break;
                        case Low:
                            newLow++;
                            break;
                        case Information:
                            newInfo++;
                            break;
                    }
                }
            }
        }
    }
 
    public void setAstSastScanLink(String astScanLink) {
        this.astScanLink = astScanLink;
    }

    public void setAstSastScanLink(String url, String scanId, String projectId) {
        this.astScanLink = String.format(url + SCAN_LINK_FORMAT, scanId, projectId);
    }
    
    public boolean isAstSastResultsReady() {
        return astSastResultsReady;
    }

    public void setSastResultsReady(boolean astSastResultsReady) {
        this.astSastResultsReady = astSastResultsReady;
    } 
    
    private void setAstSastProjectLink() {
    	 this.astSastProjectLink = astSastProjectLink;
    }

    public void setAstSastProjectLink(String url, String projectId) {
        this.astSastProjectLink = String.format(url + PROJECT_LINK_FORMAT, projectId);
    }

    public int getHigh() {
        return high;
    }

    public void setHigh(int high) {
        this.high = high;
    }

    public int getMedium() {
        return medium;
    }

    public void setMedium(int medium) {
        this.medium = medium;
    }

    public int getLow() {
        return low;
    }

    public void setLow(int low) {
        this.low = low;
    }

    public int getInformation() {
        return information;
    }

    public void setInformation(int information) {
        this.information = information;
    }
}
