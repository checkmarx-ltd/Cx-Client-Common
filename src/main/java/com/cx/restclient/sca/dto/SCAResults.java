package com.cx.restclient.sca.dto;

import com.cx.restclient.dto.IResults;
import com.cx.restclient.sca.dto.report.Finding;
import com.cx.restclient.sca.dto.report.Package;
import com.cx.restclient.sca.dto.report.SCASummaryResults;


import java.io.Serializable;
import java.util.List;


public class SCAResults implements Serializable, IResults {
    private String scanId;
    private SCASummaryResults summary;
    private String webReportLink;
    private List<Finding> findings;
    private List<Package> packages;
    private boolean scaResultReady;

    public String getScanId() {
        return scanId;
    }

    public void setScanId(String scanId) {
        this.scanId = scanId;
    }

    public SCASummaryResults getSummary() {
        return summary;
    }

    public void setSummary(SCASummaryResults summary) {
        this.summary = summary;
    }

    public String getWebReportLink() {
        return webReportLink;
    }

    public void setWebReportLink(String webReportLink) {
        this.webReportLink = webReportLink;
    }

    public List<Finding> getFindings() {
        return findings;
    }

    public void setFindings(List<Finding> findings) {
        this.findings = findings;
    }

    public List<Package> getPackages() {
        return packages;
    }

    public void setPackages(List<Package> packages) {
        this.packages = packages;
    }

    public boolean isScaResultReady() {
        return scaResultReady;
    }

    public void setScaResultReady(boolean scaResultReady) {
        this.scaResultReady = scaResultReady;
    }
}
