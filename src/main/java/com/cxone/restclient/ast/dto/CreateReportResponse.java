package com.cxone.restclient.ast.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Galn on 12/02/2018.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateReportResponse {
    private int reportId;

    public int getReportId() {
        return reportId;
    }

    public void setReportId(int reportId) {
        this.reportId = reportId;
    }

}
