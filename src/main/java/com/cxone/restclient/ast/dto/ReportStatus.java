package com.cxone.restclient.ast.dto;

import com.cx.restclient.dto.BaseStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Galn on 07/03/2018.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReportStatus extends BaseStatus {
    private String contentType;
    private CxOneValueObj status;

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public CxOneValueObj getStatus() {
        return status;
    }

    public void setStatus(CxOneValueObj status) {
        this.status = status;
    }
}
