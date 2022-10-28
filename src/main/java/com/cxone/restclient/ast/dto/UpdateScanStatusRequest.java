package com.cxone.restclient.ast.dto;

import com.cxone.restclient.ast.dto.CurrentStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateScanStatusRequest {
	
	private String status;

    public UpdateScanStatusRequest(CurrentStatus status) {
        this.status = status.value();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(CurrentStatus status) {
        this.status = status.value();
    }

}
