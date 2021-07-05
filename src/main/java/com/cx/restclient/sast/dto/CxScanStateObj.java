package com.cx.restclient.sast.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CxScanStateObj {
    private String path;
    private String sourceId;
    private long filesCount;
    private long linesOfCode;
    private long failedLinesOfCode;
    private String cxVersion;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public long getFilesCount() {
        return filesCount;
    }

    public void setFilesCount(long filesCount) {
        this.filesCount = filesCount;
    }

    public long getLinesOfCode() {
        return linesOfCode;
    }

    public void setLinesOfCode(long linesOfCode) {
        this.linesOfCode = linesOfCode;
    }

    public long getFailedLinesOfCode() {
        return failedLinesOfCode;
    }

    public void setFailedLinesOfCode(long failedLinesOfCode) {
        this.failedLinesOfCode = failedLinesOfCode;
    }

    public String getCxVersion() {
        return cxVersion;
    }

    public void setCxVersion(String cxVersion) {
        this.cxVersion = cxVersion;
    }

}
