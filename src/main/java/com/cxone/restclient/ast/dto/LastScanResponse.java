package com.cxone.restclient.ast.dto;

import com.cx.restclient.sast.dto.CxNameObj;
import com.cx.restclient.sast.dto.DateAndTime;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LastScanResponse {
	private long id;
	private CxNameObj status;
	private DateAndTime dateAndTime;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public CxNameObj getStatus() {
		return status;
	}

	public void setStatus(CxNameObj status) {
		this.status = status;
	}

	public DateAndTime getDateAndTime() {
		return dateAndTime;
	}

	public void setDateAndTime(DateAndTime dateAndTime) {
		this.dateAndTime = dateAndTime;
	}

}
