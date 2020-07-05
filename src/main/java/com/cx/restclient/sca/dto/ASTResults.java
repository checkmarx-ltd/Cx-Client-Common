package com.cx.restclient.sca.dto;

import com.cx.restclient.dto.IResults;
import com.cx.restclient.sca.dto.report.ASTSummaryResults;
import com.cx.restclient.sca.dto.report.PackageUsage;


import java.io.Serializable;


public class ASTResults implements Serializable, IResults {
    private String scanId;
    private ASTSummaryResults summary;


    public String getScanId() {
        return scanId;
    }

    public void setScanId(String scanId) {
        this.scanId = scanId;
    }

    public ASTSummaryResults getSummary() {
        return summary;
    }

    public void setSummary(ASTSummaryResults summary) {
        this.summary = summary;
    }

  
}
