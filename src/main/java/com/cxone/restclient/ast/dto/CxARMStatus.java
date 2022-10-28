package com.cxone.restclient.ast.dto;


import com.cx.restclient.dto.BaseStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Galn on 07/03/2018.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CxARMStatus extends BaseStatus {
    private CxOneID project;
    private CxOneID scan;
    String status;
    String lastSync;

    public CxOneID getProject() {
        return project;
    }

    public void setProject(CxOneID project) {
        this.project = project;
    }

    public CxOneID getScan() {
        return scan;
    }

    public void setScan(CxOneID scan) {
        this.scan = scan;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLastSync() {
        return lastSync;
    }

    public void setLastSync(String lastSync) {
        this.lastSync = lastSync;
    }
}
