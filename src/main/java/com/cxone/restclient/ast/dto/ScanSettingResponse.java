package com.cxone.restclient.ast.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by: dorg.
 * Date: 05/03/2018.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ScanSettingResponse {

    private CxOneID project;
    private CxOneID preset;
    private CxOneID engineConfiguration;
    private CxOneID postScanAction;
    private EmailNotifications emailNotifications;
	public CxOneID getProject() {
		return project;
	}
	public void setProject(CxOneID project) {
		this.project = project;
	}
	public CxOneID getPreset() {
		return preset;
	}
	public void setPreset(CxOneID preset) {
		this.preset = preset;
	}
	public CxOneID getEngineConfiguration() {
		return engineConfiguration;
	}
	public void setEngineConfiguration(CxOneID engineConfiguration) {
		this.engineConfiguration = engineConfiguration;
	}
	public CxOneID getPostScanAction() {
		return postScanAction;
	}
	public void setPostScanAction(CxOneID postScanAction) {
		this.postScanAction = postScanAction;
	}
	public EmailNotifications getEmailNotifications() {
		return emailNotifications;
	}
	public void setEmailNotifications(EmailNotifications emailNotifications) {
		this.emailNotifications = emailNotifications;
	}

    
}
