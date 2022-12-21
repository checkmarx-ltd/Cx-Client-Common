package com.cx.restclient.ast.dto.sast;

import static com.cx.restclient.ast.dto.sast.AstSASTParam.OVERVIEW_BRANCH;
import static com.cx.restclient.ast.dto.sast.AstSASTParam.PROJECT_FOR_SCAN;
import static com.cx.restclient.ast.dto.sast.AstSASTParam.SCAN_LINK_BRANCH;

import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

import com.checkmarx.one.dto.CxOneConfig;
import com.checkmarx.one.dto.resultsummary.ResultSummaries;
import com.checkmarx.one.dto.resultsummary.ResultSummary;
import com.checkmarx.one.dto.resultsummary.ResultSummaryResponse;
import com.checkmarx.one.dto.scan.ScanResponse;
import com.checkmarx.one.dto.scan.ScansResponse;
import com.checkmarx.one.dto.scan.sast.SastResultDetails;
import com.checkmarx.one.dto.scan.sast.SastResultsResponse;
import com.checkmarx.one.dto.scan.sast.ScanMetricsResponse;
import com.cx.restclient.ast.dto.sast.report.AstSastSummaryResults;
import com.cx.restclient.ast.dto.sast.report.Finding;
import com.cx.restclient.configuration.CxScanConfig;
import com.cx.restclient.cxArm.dto.Policy;
import com.cx.restclient.dto.Results;
import com.cx.restclient.sast.dto.SupportedLanguage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AstSastResults extends Results implements Serializable {
    private String scanId;
    private ScanResponse scanRes;
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

    private String cxonesastPDFLink;

    private String scanStartTime = "";
    private String scanEndTime = "";
    
    private int filesScanned = 0;
	private int LOC = 0;

	private byte[] rawXMLReport;
    private byte[] PDFReport;
    private String pdfFileName;

    private List<Policy> sastPolicies = new ArrayList<>();
	private boolean cxoneSastResultsReady = false;
	private String cxOneScanLink;
	private String astSastProjectLink;
	private String cxOneLanguage = "en-US";
	private List<SastResultDetails> sastResults = new ArrayList<>();

	public enum Severity {
        High, Medium, Low, Information;
    }
    
    public String getScanId() {
        return scanId;
    }

    public void setScanId(String scanId) {
        this.scanId = scanId;
    }
    public int getFilesScanned() {
		return filesScanned;
	}
    public int getLOC() {
		return LOC;
	}
    public void setFilesScanned(int filesScanned) {
        this.filesScanned = filesScanned;
    }
    public void setLOC(int LOC) {
        this.LOC = LOC;
    }

    public void setScanResponse(ScanResponse results) {
        this.scanRes = results;
    }
    public boolean hasNewResults() {
        return newHigh + newMedium + newLow > 0;
    }
    public ScanResponse getScanResponse() {
        return scanRes;
    }
    public String getCxOneSastScanLink() {
        return cxOneScanLink;
    }

    public void setCxOneSastScanLink(String astScanLink) {
        this.cxOneScanLink = astScanLink;
    }
    public int getNewHigh() {
        return newHigh;
    }

    public void setNewHigh(int newHigh) {
        this.newHigh = newHigh;
    }

    public int getNewMedium() {
        return newMedium;
    }

    public void setNewMedium(int newMedium) {
        this.newMedium = newMedium;
    }

    public int getNewLow() {
        return newLow;
    }

    public void setNewLow(int newLow) {
        this.newLow = newLow;
    }

    public int getNewInfo() {
        return newInfo;
    }

    public void setNewInfo(int newInfo) {
        this.newInfo = newInfo;
    }
    public boolean isCxoneSastResultsReady() {
		return cxoneSastResultsReady;
	}

	public void setCxoneSastResultsReady(boolean cxoneSastResultsReady) {
		this.cxoneSastResultsReady = cxoneSastResultsReady;
	}

	public List<SastResultDetails> getSastResults() {
		return sastResults;
	}

	public void setSastResults(List<SastResultDetails> sastResults) {
		this.sastResults = sastResults;
	}
    
    /**
     * This method sets the result details to the AstSastResults
     * @param scanId
     * @param resultsSummaryRes
     * @param oneConfig
     * @param projectId
     * @throws JsonMappingException
     * @throws JsonProcessingException
     */
	public void setResults(String scanId, ResultSummaryResponse resultsSummaryRes, CxOneConfig oneConfig, String projectId)
			throws JsonMappingException, JsonProcessingException {
		setScanId(scanId);
		//Setting the result high/medium/low count to AstSastResults
		ResultSummaries scansSummaries;
		scansSummaries = resultsSummaryRes.getContent();

		for (Iterator iterator = scansSummaries.getScansSummaries().iterator(); iterator.hasNext();) {
			ResultSummary resultSummaryResponse = (ResultSummary) iterator.next();
			if (resultSummaryResponse.getScanId().equalsIgnoreCase(scanId)) {
				setLow(resultSummaryResponse.getLowSeverityCounter());
				setHigh(resultSummaryResponse.getHighSeverityCounter());
				setMedium(resultSummaryResponse.getMediumSeverityCounter());
				setLow(resultSummaryResponse.getLowSeverityCounter());
				setInformation(resultSummaryResponse.getInfoSeverityCounter());
			}
		}

		setAstSastScanLink(oneConfig, scanId, projectId);
		setAstSastProjectLink(oneConfig, projectId);
	}
	/**
	 * Sets the details report
	 * @param results
	 * @param scanMetrics
	 * @param config
	 * @throws IOException
	 */
 public void setScanDetailedReport(SastResultsResponse results,ScanMetricsResponse scanMetrics, CxScanConfig config) throws IOException {
     this.LOC = scanMetrics.getTotalLoc();
     this.filesScanned = scanMetrics.getTotalScannedFiles();
     
        for (SastResultDetails q : results.getContent().getResults()) {
            List<SastResultDetails> qResult = results.getContent().getResults();
            for (int i = 0; i < qResult.size(); i++) {
            	SastResultDetails result = qResult.get(i);
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
        sastResults  = results.getContent().getResults();
    }

    public void setAstSastScanLink(CxOneConfig oneConfig, String scanId, String projectId) {
    	this.cxOneScanLink = oneConfig.getApiBaseUrl() + PROJECT_FOR_SCAN + projectId + SCAN_LINK_BRANCH + getScanResponse().getBranch()+"&id="+ scanId;
    }
    
    private void setAstSastProjectLink() {
    	 this.astSastProjectLink = astSastProjectLink;
    }

    public void setAstSastProjectLink(CxOneConfig oneConfig, String projectId) {
		this.astSastProjectLink = oneConfig.getApiBaseUrl() + PROJECT_FOR_SCAN + projectId + OVERVIEW_BRANCH
				+ getScanResponse().getBranch();
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

	public void setRawXMLReport(byte[] cxReport) {
		this.rawXMLReport = rawXMLReport;		
	}

	public void setPDFReport(byte[] pdfReport2) {
		this.PDFReport = PDFReport;		
	}

	public void setCxOneSastPDFLink(String pdfLink) {
		this.cxonesastPDFLink = cxonesastPDFLink;		
	}
	
	public String getCxOneSastPDFLink(String pdfLink) {
		return cxonesastPDFLink;		
	}
	
	public void setPdfFileName(String pdfFileName2) {
		this.pdfFileName = pdfFileName;		
	}
	public byte[] getRawXMLReport() {
        return rawXMLReport;
    }

    public String getPdfFileName() {
        return pdfFileName;
    }
    public byte[] getPDFReport() {
        return PDFReport;
    }
/**
 * Updated scan start and end time
 * @param scanDetails
 * @throws JsonMappingException
 * @throws JsonProcessingException
 */
	public void updateAstSastResult(ScansResponse scanDetails) throws JsonMappingException, JsonProcessingException {
		this.scanStartTime = scanDetails.getCreatedAt();
        this.scanEndTime = scanDetails.getUpdatedAt();
        setScanStartEndDates(this.scanStartTime, this.scanEndTime, cxOneLanguage);
	}
	public void setCxOneLanguage(String cxOneLanguage) {
		this.cxOneLanguage = cxOneLanguage;
	}
	 private void setScanStartEndDates(String scanStart, String scanEnd, String lang) {

	        try {
	            //turn strings to date objects
	            LocalDateTime scanStartDate = createStartDate(scanStart, lang);
	            LocalDateTime scanEndDate = createStartDate(scanEnd, lang);
	            //turn dates back to strings
	            String scanStartDateFormatted = formatToDisplayDate(scanStartDate);
	            String scanEndDateFormatted = formatToDisplayDate(scanEndDate);

	            //set sast scan result object with formatted strings
	            this.scanStartTime = scanStartDateFormatted;
	            this.scanEndTime = scanEndDateFormatted;
	        } catch (Exception ignored) {
	            //ignored
	       	 ignored.printStackTrace();
	        }

	    }
	 

	    private String formatToDisplayDate(LocalDateTime date) throws ParseException{
	    	 String displayDatePattern = "dd/MM/yy HH:mm:ss";
	    	return date.format(DateTimeFormatter.ofPattern(displayDatePattern));
	    }

	    
	    /*
	     * Convert localized date to english date
	     */
	    private LocalDateTime createStartDate(String scanStart, String langTag) throws Exception {
	    	LocalDateTime startDate = LocalDateTime.now();
	    	
			Locale l = Locale.forLanguageTag(langTag);
			try {
		   	   	  final String languageTag =  StringUtils.upperCase(l.getLanguage() + l.getCountry());
		    	  final DateTimeFormatter formatter = new DateTimeFormatterBuilder()
		    	             .parseCaseInsensitive()
		    	             .appendPattern(SupportedLanguage.valueOf(languageTag).getDatePattern())
		    	             .toFormatter(l);
					
		    	  		
		    	 startDate = LocalDateTime.parse(scanStart, formatter);
				} catch (Exception ignored) {

			    }
			
	        return startDate;
	    }

	    private LocalTime createTimeDate(String hhmmss) throws ParseException {
	    	LocalTime scanTime = LocalTime.parse(hhmmss,DateTimeFormatter.ofPattern("HH'h':mm'm':ss's'"));
	    	return scanTime;
	    }

	    private LocalDateTime createEndDate(LocalDateTime scanStartDate, LocalTime scanTime) {
	        return  scanStartDate.plusHours(scanTime.getHour()).plusMinutes(scanTime.getMinute()).plusSeconds(scanTime.getSecond());
	    }
		
}
