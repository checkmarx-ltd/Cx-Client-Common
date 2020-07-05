package com.cx.restclient.common.summary;

import com.cx.restclient.dto.Results;
import com.cx.restclient.dto.ScannerType;
import com.cx.restclient.dto.scansummary.Severity;
import com.cx.restclient.osa.dto.CVEReportTableRow;
import com.cx.restclient.osa.dto.OSAResults;
import com.cx.restclient.sca.dto.SCAResults;
import com.cx.restclient.sca.dto.report.Finding;

import java.util.ArrayList;
import java.util.List;

import static com.cx.restclient.common.ShragaUtils.formatDate;

public class DependencyResult  implements Results {
    private ScannerType dependencyType;
    private boolean resultReady;
    private int highVulnerability;
    private int mediumVulnerability;
    private int lowVulnerability;
    private String summaryLink;
    private int vulnerableAndOutdated;
    private int nonVulnerableLibraries;
    private String scanStartTime;
    private String scanEndTime;
    private List<CVEReportTableRow> dependencyHighCVEReportTable = new ArrayList<CVEReportTableRow>();
    private List<CVEReportTableRow> dependencyMediumCVEReportTable = new ArrayList<CVEReportTableRow>();
    private List<CVEReportTableRow> dependencyLowCVEReportTable = new ArrayList<CVEReportTableRow>();
    private int totalLibraries;

    DependencyResult(){}

    DependencyResult(SCAResults scaResults){
        this.dependencyType = ScannerType.SCA;
        this.highVulnerability = scaResults.getSummary().getHighVulnerabilityCount();
        this.mediumVulnerability = scaResults.getSummary().getMediumVulnerabilityCount();
        this.lowVulnerability = scaResults.getSummary().getLowVulnerabilityCount();
        this.resultReady = scaResults.isScaResultReady();
        this.summaryLink = scaResults.getWebReportLink();
        this.vulnerableAndOutdated = scaResults.getSummary().getTotalOutdatedPackages();
        this.nonVulnerableLibraries = scaResults.getSummary().getTotalOkLibraries();
        this.scanStartTime = formatDate(scaResults.getSummary().getCreatedOn(), "yyyy-MM-dd'T'HH:mm:ss.SSSSSSS", "dd/MM/yy HH:mm");
        //this.scanEndTime = formatDate(scaResults.getSummary().getCreatedOn(), "yyyy-MM-dd'T'HH:mm:ss.SSSSSSS", "dd/MM/yy HH:mm");
        this.scanEndTime ="";
        this.setDependencyCVEReportTableSCA(scaResults.getFindings());
        this.setTotalLibraries(scaResults.getSummary().getTotalPackages());
    }


    DependencyResult(OSAResults osaResults){
        this.dependencyType = ScannerType.OSA;
        this.highVulnerability = osaResults.getResults().getTotalHighVulnerabilities();
        this.mediumVulnerability = osaResults.getResults().getTotalMediumVulnerabilities();
        this.lowVulnerability = osaResults.getResults().getTotalLowVulnerabilities();
        this.resultReady = osaResults.isOsaResultsReady();
        this.summaryLink = osaResults.getOsaProjectSummaryLink();
        this.vulnerableAndOutdated = osaResults.getResults().getVulnerableAndOutdated();
        this.nonVulnerableLibraries = osaResults.getResults().getNonVulnerableLibraries();
        this.scanStartTime =osaResults.getScanStartTime();
        this.scanEndTime = osaResults.getScanEndTime();
        this.setDependencyCVEReportTableOsa(osaResults.getOsaLowCVEReportTable(),osaResults.getOsaMediumCVEReportTable(),osaResults.getOsaHighCVEReportTable());
        this.setTotalLibraries(osaResults.getResults().getTotalLibraries());
    }

    public void setDependencyCVEReportTableOsa(List<CVEReportTableRow> osaCVEResultsLow,List<CVEReportTableRow> osaCVEResultsMedium,List<CVEReportTableRow> osaCVEResultsHigh){
        CVEReportTableRow row;
        for(CVEReportTableRow lowCVE :osaCVEResultsLow ){
            row = lowCVE;
            this.dependencyLowCVEReportTable.add(row);
        }
        for(CVEReportTableRow mediumCVE :osaCVEResultsMedium ){
            row = mediumCVE;
            this.dependencyMediumCVEReportTable.add(row);
        }
        for(CVEReportTableRow highCVE :osaCVEResultsHigh ){
            row = highCVE;
            this.dependencyHighCVEReportTable.add(row);
        }
    }

    public void setDependencyCVEReportTableSCA(List<Finding> scaFindings){
        CVEReportTableRow row;
        for(Finding scaFinding :scaFindings ){
            row =new CVEReportTableRow(scaFinding);
            if(scaFinding.getSeverity() == Severity.LOW){
                this.dependencyLowCVEReportTable.add(row);
            }else if(scaFinding.getSeverity() == Severity.MEDIUM){
                this.dependencyMediumCVEReportTable.add(row);
            }else if(scaFinding.getSeverity() == Severity.HIGH){
                this.dependencyHighCVEReportTable.add(row);
            }
        }
    }

    public ScannerType getDependencyType() {
        return dependencyType;
    }

    public void setDependencyType(ScannerType dependencyType) {
        this.dependencyType = dependencyType;
    }

    public boolean isResultReady() {
        return resultReady;
    }

    public void setResultReady(boolean resultReady) {
        this.resultReady = resultReady;
    }

    public int getHighVulnerability() {
        return highVulnerability;
    }

    public void setHighVulnerability(int highVulnerability) {
        this.highVulnerability = highVulnerability;
    }

    public int getMediumVulnerability() {
        return mediumVulnerability;
    }

    public void setMediumVulnerability(int mediumVulnerability) {
        this.mediumVulnerability = mediumVulnerability;
    }

    public int getLowVulnerability() {
        return lowVulnerability;
    }

    public void setLowVulnerability(int lowVulnerability) {
        this.lowVulnerability = lowVulnerability;
    }

    public String getSummaryLink() {
        return summaryLink;
    }

    public void setSummaryLink(String summaryLink) {
        this.summaryLink = summaryLink;
    }

    public int getVulnerableAndOutdated() {
        return vulnerableAndOutdated;
    }

    public void setVulnerableAndOutdated(int vulnerableAndOutdated) {
        this.vulnerableAndOutdated = vulnerableAndOutdated;
    }

    public int getNonVulnerableLibraries() {
        return nonVulnerableLibraries;
    }

    public void setNonVulnerableLibraries(int nonVulnerableLibraries) {
        this.nonVulnerableLibraries = nonVulnerableLibraries;
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

    public List<CVEReportTableRow> getDependencyHighCVEReportTable() {
        return dependencyHighCVEReportTable;
    }

    public void setDependencyHighCVEReportTable(List<CVEReportTableRow> dependencyHighCVEReportTable) {
        this.dependencyHighCVEReportTable = dependencyHighCVEReportTable;
    }

    public List<CVEReportTableRow> getDependencyMediumCVEReportTable() {
        return dependencyMediumCVEReportTable;
    }

    public void setDependencyMediumCVEReportTable(List<CVEReportTableRow> dependencyMediumCVEReportTable) {
        this.dependencyMediumCVEReportTable = dependencyMediumCVEReportTable;
    }

    public List<CVEReportTableRow> getDependencyLowCVEReportTable() {
        return dependencyLowCVEReportTable;
    }

    public void setDependencyLowCVEReportTable(List<CVEReportTableRow> dependencyLowCVEReportTable) {
        this.dependencyLowCVEReportTable = dependencyLowCVEReportTable;
    }

    public int getTotalLibraries() {
        return totalLibraries;
    }

    public void setTotalLibraries(int totalLibraries) {
        this.totalLibraries = totalLibraries;
    }

}
