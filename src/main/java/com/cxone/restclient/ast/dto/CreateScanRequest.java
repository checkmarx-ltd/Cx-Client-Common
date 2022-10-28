package com.cxone.restclient.ast.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Galn on 05/02/2018.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateScanRequest {
    private long projectId;
    private boolean isIncremental;
    private boolean isPublic;
    private boolean forceScan;
    private String comment;

    public CreateScanRequest(long projectId, boolean isIncremental, boolean isPublic, boolean forceScan,String comment) {
        this.projectId = projectId;
        this.isIncremental = isIncremental;
        this.isPublic = isPublic;
        this.forceScan = forceScan;
        this.comment = comment;
    }

    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }

    public boolean isIsIncremental() {
        return isIncremental;
    }

    public void setIsIncremental(boolean incremental) {
        isIncremental = incremental;
    }

    public boolean isIsPublic() {
        return isPublic;
    }

    public void setIsPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public boolean isForceScan() {
        return forceScan;
    }

    public void setForceScan(boolean forceScan) {
        this.forceScan = forceScan;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
