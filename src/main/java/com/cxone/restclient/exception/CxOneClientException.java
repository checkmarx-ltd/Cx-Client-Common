package com.cxone.restclient.exception;

public class CxOneClientException extends RuntimeException{
	
	 public CxOneClientException() {
	        super();
	    }

	    public CxOneClientException(String message) {
	        super(message);
	    }

	    public CxOneClientException(String message, Throwable cause) {
	        super(message, cause);
	    }

	    public CxOneClientException(Throwable cause) {
	        super(cause);
	    }


}
