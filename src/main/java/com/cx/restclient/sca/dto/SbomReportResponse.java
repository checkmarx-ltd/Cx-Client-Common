package com.cx.restclient.sca.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SbomReportResponse {

    private String exportId;
    private String exportStatus;
    private String fileUrl;

    public SbomReportResponse() {
        // default constructor
    }

    @JsonCreator
    public SbomReportResponse(
            @JsonProperty("exportId") String exportId,
            @JsonProperty("exportStatus") String exportStatus,
            @JsonProperty("fileUrl") String fileUrl) {
        this.exportId = exportId;
        this.exportStatus = exportStatus;
        this.fileUrl = fileUrl;
    }

    public String getExportId() {
        return exportId;
    }

    public void setExportId(String exportId) {
        this.exportId = exportId;
    }

    public String getExportStatus() {
        return exportStatus;
    }

    public void setExportStatus(String exportStatus) {
        this.exportStatus = exportStatus;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    @Override
    public String toString() {
        return "SbomReportResponse [exportId=" + exportId + 
               ", exportStatus=" + exportStatus + 
               ", fileUrl=" + fileUrl + "]";
    }
}
