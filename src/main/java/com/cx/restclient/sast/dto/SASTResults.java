package com.cx.restclient.sast.dto;

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
import com.cx.restclient.sast.utils.LegacyClient;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


import static com.cx.restclient.common.CxPARAM.AUTHENTICATION;
import static com.cx.restclient.cxArm.utils.CxARMUtils.getPolicyList;
import static com.cx.restclient.sast.utils.SASTParam.PROJECT_LINK_FORMAT;
import static com.cx.restclient.sast.utils.SASTParam.SCAN_LINK_FORMAT;


/**
 * Created by Galn on 05/02/2018.
 */
public class SASTResults extends Results implements Serializable {

    private long scanId;
    private static final String DEFAULT_AUTH_API_PATH = "CxRestApi/auth/" + AUTHENTICATION;
    private boolean sastResultsReady = false;
    private int high = 0;
    private int medium = 0;
    private int low = 0;
    private int information = 0;
    
    private int newHigh = 0;
    private int newMedium = 0;
    private int newLow = 0;
    private int newInfo = 0;

    private String sastScanLink;
    private String sastProjectLink;
    private String sastPDFLink;

    private String scanStart = "";
    private String scanTime = "";
    private String scanStartTime = "";
    private String scanEndTime = "";
    private String language="";
    private Locale locale;
    private transient Map languageMap;
  
    public Map getLanguageMap() {
		return languageMap;
	}

	public void setLanguageMap(Map languageMap) {
		this.languageMap = languageMap;
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

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

    public void setScanDetailedReport(CxXMLResults reportObj,CxScanConfig config) throws IOException {
    	
    	//intiate httpclient for access token
    	CxHttpClient client = getHttpClient(config);
    	LoginSettings loginsetting = getLoginSettings(config);
    	TokenLoginResponse token = client.generateToken(loginsetting);
    	getLocaleFromAccessToken(token);
    	fillLanguageEquivalent(this.language);
    	
    	this.scanStart = reportObj.getScanStart();
        this.scanTime = reportObj.getScanTime();
        setScanStartEndDates(this.scanStart, this.scanTime);
        this.LOC = reportObj.getLinesOfCodeScanned();
        this.filesScanned = reportObj.getFilesScanned();
        
        
        for (CxXMLResults.Query q : reportObj.getQuery()) {
            List<CxXMLResults.Query.Result> qResult = q.getResult();
            for (int i = 0; i < qResult.size(); i++) {
                CxXMLResults.Query.Result result = qResult.get(i);
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
     * This will return string from encoded access token 
     * which will use to identify which language is used in SAST
     * 
     * */ 
    private void getLocaleFromAccessToken(TokenLoginResponse token) {
    	String actToken = token.getAccess_token();
    	String locale="";
    	String[] split_string = actToken.split("\\.");
        String base64EncodedBody = split_string[1];
        String base64EncodedSignature = split_string[2];
        Base64 base64Url = new Base64(true);
        String body = new String(base64Url.decode(base64EncodedBody));
        String tokenToParse=body.replace("\"", "'");
        JSONObject json = new JSONObject(tokenToParse);  
        locale = json.getString("locale");
        this.locale=new Locale(locale);
        this.language=locale.replace("-", "").toUpperCase();
	}

    /* 
     *It will create a map for lanaguage specific severity 
     * */ 
	private void fillLanguageEquivalent(String locale) {
		//Setting sast language equivalent for HTML Report 
        Map<String, String> languageMap = new HashMap<String,String>();
        SupportedLanguage lang = SupportedLanguage.valueOf(locale);
        languageMap.put("High", lang.getHigh());
        languageMap.put("Medium", lang.getMedium());
        languageMap.put("Low", lang.getLow());
        this.languageMap =languageMap;
	}
	
	private LoginSettings getLoginSettings(CxScanConfig config) throws MalformedURLException {
    	String baseUrl = UrlUtils.parseURLToString(config.getUrl(), DEFAULT_AUTH_API_PATH);
    	LoginSettings loginsetting = LoginSettings.builder()
                 .accessControlBaseUrl(baseUrl)
                 .username(config.getUsername())
                 .password(config.getPassword())
                 .clientTypeForPasswordAuth(ClientType.RESOURCE_OWNER)
                 .clientTypeForRefreshToken(ClientType.CLI)
                 .build();
    	loginsetting.getSessionCookies().addAll(config.getSessionCookie());
		return loginsetting;
	}

	public CxHttpClient getHttpClient(CxScanConfig config) throws  MalformedURLException{
    	CxHttpClient httpClient=null;
    	 if (!org.apache.commons.lang3.StringUtils.isEmpty(config.getUrl())) {
    		 httpClient = new CxHttpClient(
                     UrlUtils.parseURLToString(config.getUrl(), "CxRestAPI/"),
                     config.getCxOrigin(),
                     config.getCxOriginUrl(),
                     config.isDisableCertificateValidation(),
                     config.isUseSSOLogin(),
                     config.getRefreshToken(),
                     config.isProxy(),
                     config.getProxyConfig(),
                     null,
                     config.getNTLM());
         }
		return httpClient;
    	
    }

    public void setResults(long scanId, SASTStatisticsResponse statisticsResults, String url, long projectId) {
        setScanId(scanId);
        setHigh(statisticsResults.getHighSeverity());
        setMedium(statisticsResults.getMediumSeverity());
        setLow(statisticsResults.getLowSeverity());
        setInformation(statisticsResults.getInfoSeverity());
        setSastScanLink(url, scanId, projectId);
        setSastProjectLink(url, projectId);
    }

    public void addPolicy(Policy policy) {
        this.sastPolicies.addAll(getPolicyList(policy));
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

    public String getSastScanLink() {
        return sastScanLink;
    }

    public void setSastScanLink(String sastScanLink) {
        this.sastScanLink = sastScanLink;
    }

    public void setSastScanLink(String url, long scanId, long projectId) {
        this.sastScanLink = String.format(url + SCAN_LINK_FORMAT, scanId, projectId);
    }

    public String getSastProjectLink() {
        return sastProjectLink;
    }

    public void setSastProjectLink(String sastProjectLink) {
        this.sastProjectLink = sastProjectLink;
    }

    public void setSastProjectLink(String url, long projectId) {
        this.sastProjectLink = String.format(url + PROJECT_LINK_FORMAT, projectId);
    }

    public String getSastPDFLink() {
        return sastPDFLink;
    }

    public void setSastPDFLink(String sastPDFLink) {
        this.sastPDFLink = sastPDFLink;
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

    public boolean isSastResultsReady() {
        return sastResultsReady;
    }

    public void setSastResultsReady(boolean sastResultsReady) {
        this.sastResultsReady = sastResultsReady;
    }

    public String getLOC() {
        return LOC;
    }

    public void setLOC(String LOC) {
        this.LOC = LOC;
    }

    public void setQueryList(List<CxXMLResults.Query> queryList) {
        this.queryList = queryList;
    }

    public List<CxXMLResults.Query> getQueryList() {
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

    private void setScanStartEndDates(String scanStart, String scanTime) {

        try {
            //turn strings to date objects
            Date scanStartDate = createStartDate(scanStart);
            Date scanTimeDate = createTimeDate(scanTime);
            Date scanEndDate = createEndDate(scanStartDate, scanTimeDate);

            //turn dates back to strings
            String scanStartDateFormatted = formatToDisplayDate(scanStartDate);
            String scanEndDateFormatted = formatToDisplayDate(scanEndDate);

            //set sast scan result object with formatted strings
            this.scanStartTime = scanStartDateFormatted;
            this.scanEndTime = scanEndDateFormatted;

        } catch (Exception ignored) {
            //ignored
        }

    }

    private String formatToDisplayDate(Date date) throws ParseException{
    	 String displayDatePattern = "dd/MM/yy HH:mm";
         Locale locale = Locale.ENGLISH;
         return new SimpleDateFormat(displayDatePattern, locale).format(date);
    }

    
    
    private Date createStartDate(String scanStart) throws Exception {
		DateFormat formatter;
		Date formattedDate = null;
		SupportedLanguage lang = SupportedLanguage.valueOf(this.language);
		try {
			lang.getLocale();
			DateFormat df = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.DEFAULT, lang.getLocale());
			formattedDate = df.parse(scanStart);
		} catch (Exception ignored) {
			DateFormat df = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.DEFAULT, lang.getLocale());
			formattedDate = df.parse(scanStart);
		}
		if (formattedDate == null) {
			throw new Exception(String.format("Failed parsing date [%s]", scanStart));
		}
        return formattedDate;
    }

    
    
    

    private Date createTimeDate(String scanTime) throws ParseException {
        //"00h:00m:30s"
        String oldPattern = "HH'h':mm'm':ss's'";

        DateFormat oldTimeFormat = new SimpleDateFormat(oldPattern);
        oldTimeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        return oldTimeFormat.parse(scanTime);
    }

    private Date createEndDate(Date scanStartDate, Date scanTimeDate) {
        long time = scanStartDate.getTime() + scanTimeDate.getTime();
        return new Date(time);
    }

    public List<Policy> getSastPolicies() {
        return sastPolicies;
    }

    public void setSastPolicies(List<Policy> sastPolicies) {
        this.sastPolicies = sastPolicies;
    }

}
