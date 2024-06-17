package com.cx.restclient.sast.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateBranchStatus {
	
    private long id;
    private long originalProjectId;
    private String originalProjectName;
    private long branchedOnScanId;
    private long branchedProjectId;
    private String timestamp;
    private String comment;
    private Status status;
    private String errorMessage;
    public CreateBranchStatus(long id, long originalProjectId, String originalProjectName, long branchedOnScanId,
			long branchedProjectId, String timestamp, String comment, Status status, String errorMessage) {
		this.id = id;
		this.originalProjectId = originalProjectId;
		this.originalProjectName = originalProjectName;
		this.branchedOnScanId = branchedOnScanId;
		this.branchedProjectId = branchedProjectId;
		this.timestamp = timestamp;
		this.comment = comment;
		this.status = status;
		this.errorMessage = errorMessage;
	}
    
    public CreateBranchStatus() {
    }

	public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public long getOriginalProjectId() {
        return originalProjectId;
    }
    public void setOriginalProjectId(Integer originalProjectId) {
        this.originalProjectId = originalProjectId;
    }
    public String getOriginalProjectName() {
        return originalProjectName;
    }
    public void setOriginalProjectName(String originalProjectName) {
        this.originalProjectName = originalProjectName;
    }
    public long getBranchedOnScanId() {
        return branchedOnScanId;
    }
    public void setBranchedOnScanId(long branchedOnScanId) {
        this.branchedOnScanId = branchedOnScanId;
    }
    public long getBranchedProjectId() {
        return branchedProjectId;
    }
    public void setBranchedProjectId(long branchedProjectId) {
        this.branchedProjectId = branchedProjectId;
    }
    public String getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    public Status getStatus() {
        return status;
    }
    public void setStatus(Status status) {
        this.status = status;
    }
    public String getErrorMessage() {
        return errorMessage;
    }
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
}
