package com.cx.restclient.sca.dto.report;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class SCASummaryResults extends SummaryResults implements Serializable {
    private String riskReportId;
    private int highVulnerabilityCount;
    private int mediumVulnerabilityCount;
    private int lowVulnerabilityCount;
    private int totalPackages;
    private int directPackages;
    private String createdOn;
    private double riskScore;
    private int totalOutdatedPackages;

    public int getTotalOkLibraries(){
        int totalOk = (totalPackages-(highVulnerabilityCount+mediumVulnerabilityCount+lowVulnerabilityCount));
        totalOk = Math.max(totalOk,0);
        return totalOk;
    }
}
