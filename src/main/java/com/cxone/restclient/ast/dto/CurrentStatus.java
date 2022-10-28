package com.cxone.restclient.ast.dto;

public enum CurrentStatus {
	
	    QUEUED("Queued"),
	    WORKING("Working"),
	    FINISHED("Finished"),
	    FAILED("Failed"),
	    CANCELED("Canceled"),
	    DELETED("Deleted"),
	    UNKNOWN("Unknown"),
	    UNZIPPING("Unzipping"),
	    WAITING_TO_PROCESS("WaitingToProcess");

	    private final String value;

	    CurrentStatus(String value) {
	        this.value = value;
	    }

	    public String value() {
	        return this.value;
	    }

}
