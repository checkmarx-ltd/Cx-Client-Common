package com.cxone.restclient.ast.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EmailNotifications {
	 private List<String> failedScan;
	    private List<String> beforeScan;
	    private List<String> afterScan;

	    public List<String> getFailedScan() {
	        return failedScan;
	    }

	    public void setFailedScan(List<String> failedScan) {
	        this.failedScan = failedScan;
	    }

	    public List<String> getBeforeScan() {
	        return beforeScan;
	    }

	    public void setBeforeScan(List<String> beforeScan) {
	        this.beforeScan = beforeScan;
	    }

	    public List<String> getAfterScan() {
	        return afterScan;
	    }

	    public void setAfterScan(List<String> afterScan) {
	        this.afterScan = afterScan;
	    }

}
