package com.cx.restclient.ast.dto.sast;

import com.cx.restclient.ast.dto.sast.report.AstSastSummaryResults;
import com.cx.restclient.ast.dto.sast.report.Finding;
import com.cx.restclient.cxArm.dto.Policy;
import com.cx.restclient.dto.Results;
import com.cx.restclient.sast.dto.CxXMLResults;
import com.cx.restclient.sast.dto.SASTStatisticsResponse;

import lombok.Getter;
import lombok.Setter;

import static com.cx.restclient.sast.utils.SASTParam.SCAN_LINK_FORMAT;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
    private List<CxXMLResults.Query> queryList;

    private byte[] rawXMLReport;
    private byte[] PDFReport;
    private String pdfFileName;

    private List<Policy> sastPolicies = new ArrayList<>();

    public enum Severity {
        High, Medium, Low, Information;
    }
    
    public String getScanId() {
        return scanId;
    }

    public void setScanId(String scanId) {
        this.scanId = scanId;
    }
    
    public void setResults(String scanId, SASTStatisticsResponse statisticsResults, String url, long projectId) {
        setScanId(scanId);
//        setHigh(statisticsResults.getHighSeverity());
//        setMedium(statisticsResults.getMediumSeverity());
//        setLow(statisticsResults.getLowSeverity());
//        setInformation(statisticsResults.getInfoSeverity());
//        setSastScanLink(url, scanId, projectId);
//        setSastProjectLink(url, projectId);
    }
}
