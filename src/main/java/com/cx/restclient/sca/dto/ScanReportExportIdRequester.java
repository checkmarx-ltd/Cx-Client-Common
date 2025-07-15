package com.cx.restclient.sca.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ScanReportExportIdRequester {
	
	@JsonProperty("ScanId")
	private String scanId;
	
	@JsonProperty("FileFormat")
    private String fileFormat;

	public ScanReportExportIdRequester(String scanId, String fileFormat) {
		this.scanId = scanId;
		this.fileFormat = fileFormat;
	}

	public String getScanId() {
		return scanId;
	}

	public void setScanId(String scanId) {
		this.scanId = scanId;
	}

	public String getFileFormat() {
		return fileFormat;
	}

	public void setFileFormat(String fileFormat) {
		this.fileFormat = fileFormat;
	}

	@Override
	public String toString() {
		return "ScanReportExporter [scanId=" + scanId + ", fileFormat=" + fileFormat + "]";
	}

}


