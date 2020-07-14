package com.cx.restclient.ast.dto.sca;

import com.cx.restclient.dto.Results;
import com.cx.restclient.ast.dto.sca.report.Finding;
import com.cx.restclient.ast.dto.sca.report.Package;
import com.cx.restclient.ast.dto.sca.report.AstScaSummaryResults;


import java.io.Serializable;
import java.util.List;


public class AstScaResults implements Serializable, Results {
    private String scanId;
    private AstScaSummaryResults summary;
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

    public AstScaSummaryResults getSummary() {
        return summary;
    }

    public void setSummary(AstScaSummaryResults summary) {
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
