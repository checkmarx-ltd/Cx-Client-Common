package com.cxone.restclient.ast.dto;

import com.cx.restclient.ast.dto.common.ScanConfig;
import com.cx.restclient.common.UrlUtils;
import com.cx.restclient.configuration.CxScanConfig;
import com.cx.restclient.cxArm.dto.Policy;
import com.cx.restclient.dto.LoginSettings;
import com.cx.restclient.dto.Results;
import com.cx.restclient.dto.TokenLoginResponse;
import com.cx.restclient.exception.CxClientException;
import com.cx.restclient.httpClient.CxHttpClient;
import com.cx.restclient.osa.dto.ClientType;
import com.cx.restclient.sast.dto.CxXMLResults.Query;
import com.cx.restclient.sast.dto.SupportedLanguage;
import com.cx.restclient.sast.utils.LegacyClient;


import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;


import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.*;


import static com.cx.restclient.common.CxPARAM.AUTHENTICATION;
import static com.cx.restclient.cxArm.utils.CxARMUtils.getPolicyList;
import static com.cx.restclient.sast.utils.SASTParam.PROJECT_LINK_FORMAT;
import static com.cx.restclient.sast.utils.SASTParam.SCAN_LINK_FORMAT;


/**
 * Created by Galn on 05/02/2018.
 */
public class ASTResults1 extends Results implements Serializable {

    private long scanId;
    private static final String DEFAULT_AUTH_API_PATH = "CxRestApi/auth/" + AUTHENTICATION;
    private boolean astResultsReady = false;
    private int high = 0;
    private int medium = 0;
    private int low = 0;
    private int information = 0;
    
    private int newHigh = 0;
    private int newMedium = 0;
    private int newLow = 0;
    private int newInfo = 0;

    private String astScanLink;
    private String astProjectLink;
    private String astPDFLink;

    private String scanStart = "";
    private String scanTime = "";
    private String scanStartTime = "";
    private String scanEndTime = "";
    private String astLanguage="en-US";
    public Exception exception;
  
    public Exception getException() {
		return exception;
	}

	public void setException(Exception exception) {
		this.exception = exception;
	}

	private Map<String,String> languageMap;
  
	public Map<String, String> getLanguageMap() {
		return languageMap;
	}

	public void setLanguageMap(Map<String, String> languageMap) {
		this.languageMap = languageMap;
	}
	
	public String getAstLanguage() {
		return astLanguage;
	}

	public void setAstLanguage(String astLanguage) {
		this.astLanguage = astLanguage;
	}

	private String filesScanned;
    private String LOC;
    private List<CxOneXMLResults.Query> queryList;

    private byte[] rawXMLReport;
    private byte[] PDFReport;
    private String pdfFileName;

    private List<Policy> astPolicies = new ArrayList<>();

    public enum Severity {
        High, Medium, Low, Information;
    }
    

    public void setScanDetailedReport(CxOneXMLResults reportObj,CxScanConfig config) throws IOException {
    	
    	setLanguageEquivalent(astLanguage);
    	
    	this.scanStart = reportObj.getScanStart();
        this.scanTime = reportObj.getScanTime();
        setScanStartEndDates(this.scanStart, this.scanTime,astLanguage);
        this.LOC = reportObj.getLinesOfCodeScanned();
        this.filesScanned = reportObj.getFilesScanned();
        
        
        for (CxOneXMLResults.Query q : reportObj.getQuery()) {
            List<CxOneXMLResults.Query.Result> qResult = q.getResult();
            for (int i = 0; i < qResult.size(); i++) {
                CxOneXMLResults.Query.Result result = qResult.get(i);
                if ("True".equals(result.getFalsePositive())) {
                    qResult.remove(i);
                } else if ("New".equals(result.getStatus())) {
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
        this.queryList = reportObj.getQuery();
    }
        
    /* 
     *It will create a map for lanaguage specific severity 
     * */ 
	private void setLanguageEquivalent(String astLanguage) {
		//Setting ast language equivalent for HTML Report 
		if(astLanguage!=null){
		Locale l = Locale.forLanguageTag(astLanguage);
		final String languageTag = StringUtils.upperCase(l.getLanguage()+ l.getCountry());
		
        languageMap = new HashMap<String,String>();
        SupportedLanguage lang = SupportedLanguage.valueOf(languageTag);
        languageMap.put("High", lang.getHigh());
        languageMap.put("Medium", lang.getMedium());
        languageMap.put("Low", lang.getLow());
		}
	}
		
    public void setResults(long scanId, ASTStatisticsResponse statisticsResults, String url, long projectId) {
        setScanId(scanId);
        setHigh(statisticsResults.getHighSeverity());
        setMedium(statisticsResults.getMediumSeverity());
        setLow(statisticsResults.getLowSeverity());
        setInformation(statisticsResults.getInfoSeverity());
        setAstScanLink(url, scanId, projectId);
        setAstProjectLink(url, projectId);
    }

    public void addPolicy(Policy policy) {
        this.astPolicies.addAll(getPolicyList(policy));
    }

    public long getScanId() {
        return scanId;
    }

    public void setScanId(long scanId) {
        this.scanId = scanId;
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

    public String getAstScanLink() {
        return astScanLink;
    }

    public void setAstScanLink(String astScanLink) {
        this.astScanLink = astScanLink;
    }

    public void setAstScanLink(String url, long scanId, long projectId) {
        this.astScanLink = String.format(url + SCAN_LINK_FORMAT, scanId, projectId);
    }

    public String getAstProjectLink() {
        return astProjectLink;
    }

    public void setAstProjectLink(String astProjectLink) {
        this.astProjectLink = astProjectLink;
    }

    public void setAstProjectLink(String url, long projectId) {
        this.astProjectLink = String.format(url + PROJECT_LINK_FORMAT, projectId);
    }

    public String getAstPDFLink() {
        return astPDFLink;
    }

    public void setAstPDFLink(String astPDFLink) {
        this.astPDFLink = astPDFLink;
    }

    public String getScanStart() {
        return scanStart;
    }

    public void setScanStart(String scanStart) {
        this.scanStart = scanStart;
    }

    public String getScanTime() {
        return scanTime;
    }

    public void setScanTime(String scanTime) {
        this.scanTime = scanTime;
    }

    public String getScanStartTime() {
        return scanStartTime;
    }

    public void setScanStartTime(String scanStartTime) {
        this.scanStartTime = scanStartTime;
    }

    public String getScanEndTime() {
        return scanEndTime;
    }

    public void setScanEndTime(String scanEndTime) {
        this.scanEndTime = scanEndTime;
    }

    public String getFilesScanned() {
        return filesScanned;
    }

    public void setFilesScanned(String filesScanned) {
        this.filesScanned = filesScanned;
    }

    public boolean isAstResultsReady() {
        return astResultsReady;
    }

    public void setAstResultsReady(boolean astResultsReady) {
        this.astResultsReady = astResultsReady;
    }

    public String getLOC() {
        return LOC;
    }

    public void setLOC(String LOC) {
        this.LOC = LOC;
    }

    public void setQueryList(List<CxOneXMLResults.Query> queryList) {
        this.queryList = queryList;
    }

    public List<CxOneXMLResults.Query> getQueryList() {
        return queryList;
    }

    public byte[] getRawXMLReport() {
        return rawXMLReport;
    }

    public String getPdfFileName() {
        return pdfFileName;
    }

    public void setPdfFileName(String pdfFileName) {
        this.pdfFileName = pdfFileName;
    }

    public void setRawXMLReport(byte[] rawXMLReport) {
        this.rawXMLReport = rawXMLReport;
    }

    public byte[] getPDFReport() {
        return PDFReport;
    }

    public void setPDFReport(byte[] PDFReport) {
        this.PDFReport = PDFReport;
    }

    public boolean hasNewResults() {
        return newHigh + newMedium + newLow > 0;
    }

    private void setScanStartEndDates(String scanStart, String scanTime, String lang) {

        try {
            //turn strings to date objects
            LocalDateTime scanStartDate = createStartDate(scanStart, lang);
            LocalTime scanTimeDate = createTimeDate(scanTime);
            LocalDateTime scanEndDate = createEndDate(scanStartDate, scanTimeDate);
            //turn dates back to strings
            String scanStartDateFormatted = formatToDisplayDate(scanStartDate);
            String scanEndDateFormatted = formatToDisplayDate(scanEndDate);

            //set ast scan result object with formatted strings
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

    public List<Policy> getAstPolicies() {
        return astPolicies;
    }

    public void setAstPolicies(List<Policy> astPolicies) {
        this.astPolicies = astPolicies;
    }

}
