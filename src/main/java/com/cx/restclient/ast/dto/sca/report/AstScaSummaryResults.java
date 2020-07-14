package com.cx.restclient.ast.dto.sca.report;

import com.cx.restclient.ast.dto.common.SummaryResults;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class AstScaSummaryResults extends SummaryResults implements Serializable {
    private String riskReportId;
    private int totalPackages;
    private int directPackages;
    private String createdOn;
    private double riskScore;
    private int totalOutdatedPackages;

    public int getTotalOkLibraries() {
        int totalOk = (totalPackages - (getHighVulnerabilityCount() + getMediumVulnerabilityCount() + getLowVulnerabilityCount()));
        totalOk = Math.max(totalOk, 0);
        return totalOk;
    }
}
