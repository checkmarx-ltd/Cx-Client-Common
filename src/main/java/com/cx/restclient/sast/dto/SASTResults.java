package com.cx.restclient.sast.dto;

import com.cx.restclient.cxArm.dto.Policy;
import com.cx.restclient.dto.Results;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.cx.restclient.cxArm.utils.CxARMUtils.getPolicyList;
import static com.cx.restclient.sast.utils.SASTParam.PROJECT_LINK_FORMAT;
import static com.cx.restclient.sast.utils.SASTParam.SCAN_LINK_FORMAT;


/**
 * Created by Galn on 05/02/2018.
 */
public class SASTResults extends Results implements Serializable {

    private long scanId;

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

    public void setScanDetailedReport(CxXMLResults reportObj) {
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

    private String formatToDisplayDate(Date date) {
        //"26/2/17 12:17"
        String displayDatePattern = "dd/MM/yy HH:mm";
        Locale locale = Locale.ENGLISH;

        return new SimpleDateFormat(displayDatePattern, locale).format(date);
    }

    private Date createStartDate(String scanStart) throws Exception {
        DateFormat formatter;
        Date formattedDate = null;

        for (SupportedLanguage lang : SupportedLanguage.values()) {
            try {
                formatter = new SimpleDateFormat(lang.getFormat(), lang.getLocale());
                formattedDate = formatter.parse(scanStart);
                break;
            } catch (Exception ignored) {
            }
        }

        if(formattedDate == null){
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
