package com.cxone.restclient.ast.dto;

import com.cx.restclient.dto.BaseStatus;
import com.cx.restclient.sast.dto.Project;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseQueueScanStatus extends BaseStatus{

	private long id;
    private CxOneValueObj stage;
    private String stageDetails;
    private String stepDetails;
    private Project project;
    private int totalPercent;
    private int stagePercent;
    
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public CxOneValueObj getStage() {
		return stage;
	}
	public void setStage(CxOneValueObj stage) {
		this.stage = stage;
	}
	public String getStageDetails() {
		return stageDetails;
	}
	public void setStageDetails(String stageDetails) {
		this.stageDetails = stageDetails;
	}
	public String getStepDetails() {
		return stepDetails;
	}
	public void setStepDetails(String stepDetails) {
		this.stepDetails = stepDetails;
	}
	public Project getProject() {
		return project;
	}
	public void setProject(Project project) {
		this.project = project;
	}
	public int getTotalPercent() {
		return totalPercent;
	}
	public void setTotalPercent(int totalPercent) {
		this.totalPercent = totalPercent;
	}
	public int getStagePercent() {
		return stagePercent;
	}
	public void setStagePercent(int stagePercent) {
		this.stagePercent = stagePercent;
	}
    
    
}
