package com.cxone.restclient.exception;

/**
 * Created by Galn on 05/02/2018.
 */
public class CxOneHTTPClientException extends CxOneClientException {
    private int statusCode = 0;
    private String responseBody;

    public CxOneHTTPClientException(int statusCode, String message, String responseBody) {
        super(message);
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    public CxOneHTTPClientException() {
        super();
    }

    public CxOneHTTPClientException(String message) {
        super(message);
    }

    public CxOneHTTPClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public CxOneHTTPClientException(Throwable cause) {
        super(cause);
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public String getResponseBody() {
        return responseBody;
    }
}
