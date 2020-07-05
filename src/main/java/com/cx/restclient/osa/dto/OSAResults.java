package com.cx.restclient.osa.dto;

import com.cx.restclient.cxArm.dto.Policy;
import com.cx.restclient.dto.Results;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.cx.restclient.common.ShragaUtils.formatDate;
import static com.cx.restclient.cxArm.utils.CxARMUtils.getPolicyList;

/**
 * Created by Galn on 07/02/2018.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OSAResults implements Serializable, Results {
    private String osaScanId;
    private OSASummaryResults results;
    private List<Library> osaLibraries;
    private List<CVE> osaVulnerabilities;
    private OSAScanStatus osaScanStatus;
    private String osaProjectSummaryLink;
    private boolean osaResultsReady = false;
    private List<CVEReportTableRow> osaHighCVEReportTable = new ArrayList<CVEReportTableRow>();
    private List<CVEReportTableRow> osaMediumCVEReportTable = new ArrayList<CVEReportTableRow>();
    private List<CVEReportTableRow> osaLowCVEReportTable = new ArrayList<CVEReportTableRow>();

    private String scanStartTime;
    private String scanEndTime;

    private List<Policy> osaPolicies = new ArrayList<>();

    public OSAResults() {
    }

    public OSAResults(String osaScanId) {
        this.osaScanId = osaScanId;
    }

    public void setResults(OSASummaryResults osaSummaryResults, List<Library> osaLibraries, List<CVE> osaVulnerabilities, OSAScanStatus osaScanStatus, String url, long projectId) {
        setResults(osaSummaryResults);
        setOsaLibraries(osaLibraries);
        setOsaVulnerabilities(osaVulnerabilities);
        setOsaCVEReportTable(osaVulnerabilities, osaLibraries);
        setDates(osaScanStatus);
        setOsaScanStatus(osaScanStatus);
        setOsaProjectSummaryLink(url, projectId);
        setOsaResultsReady(true);
    }

    public List<Library> getOsaLibraries() {
        return osaLibraries;
    }

    public void setOsaLibraries(List<Library> osaLibraries) {
        this.osaLibraries = osaLibraries;
    }

    public List<CVE> getOsaVulnerabilities() {
        return osaVulnerabilities;
    }

    public void setOsaVulnerabilities(List<CVE> osaVulnerabilities) {
        this.osaVulnerabilities = osaVulnerabilities;
    }

    public OSASummaryResults getResults() {
        return results;
    }

    public void setResults(OSASummaryResults results) {
        this.results = results;
    }

    public OSAScanStatus getOsaScanStatus() {
        return osaScanStatus;
    }

    public void setOsaScanStatus(OSAScanStatus osaScanStatus) {
        this.osaScanStatus = osaScanStatus;
    }

    public String getOsaProjectSummaryLink() {
        return osaProjectSummaryLink;
    }

    public void setOsaProjectSummaryLink(String osaProjectSummaryLink) {
        this.osaProjectSummaryLink = osaProjectSummaryLink;
    }

    public void setOsaProjectSummaryLink(String url, long projectId) {
        this.osaProjectSummaryLink = String.format(url + "/CxWebClient/SPA/#/viewer/project/%s", projectId);
    }

    public boolean isOsaResultsReady() {
        return osaResultsReady;
    }

    public void setOsaResultsReady(boolean osaResultsReady) {
        this.osaResultsReady = osaResultsReady;
    }

    public String getOsaScanId() {
        return osaScanId;
    }

    public void setOsaScanId(String osaScanId) {
        this.osaScanId = osaScanId;
    }

    public List<CVEReportTableRow> getOsaHighCVEReportTable() {
        return osaHighCVEReportTable;
    }

    public List<CVEReportTableRow> getOsaMediumCVEReportTable() {
        return osaMediumCVEReportTable;
    }

    public List<CVEReportTableRow> getOsaLowCVEReportTable() {
        return osaLowCVEReportTable;
    }

    public String getScanStartTime() {
        return scanStartTime;
    }

    public String getScanEndTime() {
        return scanEndTime;
    }

    private void setOsaCVEReportTable(List<CVE> osaVulnerabilities, List<Library> osaLibraries) {
        Map<String, CVEReportTableRow> cveMap = new HashMap<String, CVEReportTableRow>();
        Map<String, Library> libMap = new HashMap<String, Library>();

        for (Library l : osaLibraries) {
            libMap.put(l.getId(), l);
        }

        //create uniqueness by key: cve + libraryId
        for (CVE cve : osaVulnerabilities) {
            Library lib = libMap.get(cve.getLibraryId());
            String publishDate = formatDate(cve.getPublishDate(), "yyyy-MM-dd'T'HH:mm:ss", "dd/MM/yy");
            cveMap.put(cve.getCveName() + "," + cve.getLibraryId(), new CVEReportTableRow(cve.getCveName(), cve.getSeverity().getName(), publishDate, lib.getName(), cve.getState().getName()));
        }

        for (CVEReportTableRow row : cveMap.values()) {
            if ("High".equals(row.getSeverity())) {
                osaHighCVEReportTable.add(row);
            } else if ("Medium".equals(row.getSeverity())) {
                osaMediumCVEReportTable.add(row);
            } else if ("Low".equals(row.getSeverity())) {
                osaLowCVEReportTable.add(row);
            }

        }
    }

    public void setDates(OSAScanStatus status) {
        this.scanStartTime = formatDate(status.getStartAnalyzeTime(), "yyyy-MM-dd'T'HH:mm:ss.SSSSSSS", "dd/MM/yy HH:mm");
        this.scanEndTime = formatDate(status.getEndAnalyzeTime(), "yyyy-MM-dd'T'HH:mm:ss.SSSSSSS", "dd/MM/yy HH:mm");
    }

    public void setOsaHighCVEReportTable(List<CVEReportTableRow> osaHighCVEReportTable) {
        this.osaHighCVEReportTable = osaHighCVEReportTable;
    }

    public void setOsaMediumCVEReportTable(List<CVEReportTableRow> osaMediumCVEReportTable) {
        this.osaMediumCVEReportTable = osaMediumCVEReportTable;
    }

    public void setOsaLowCVEReportTable(List<CVEReportTableRow> osaLowCVEReportTable) {
        this.osaLowCVEReportTable = osaLowCVEReportTable;
    }

    public void setScanStartTime(String scanStartTime) {
        this.scanStartTime = scanStartTime;
    }

    public void setScanEndTime(String scanEndTime) {
        this.scanEndTime = scanEndTime;
    }

    public void addPolicy(Policy policy) {
        this.osaPolicies.addAll(getPolicyList(policy));
    }

    public List<Policy> getOsaPolicies() {
        return osaPolicies;
    }

    public void setOsaPolicies(List<Policy> osaPolicies) {
        this.osaPolicies = osaPolicies;
    }
}
