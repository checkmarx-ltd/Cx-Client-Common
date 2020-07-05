package com.cx.restclient.sca.dto;

import com.cx.restclient.dto.Results;
import com.cx.restclient.sca.dto.report.ASTSummaryResults;


import java.io.Serializable;


public class ASTResults implements Serializable, Results {
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
