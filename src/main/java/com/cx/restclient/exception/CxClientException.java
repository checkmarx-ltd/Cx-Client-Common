package com.cx.restclient.exception;

/**
 * Created by Galn on 05/02/2018.
 */
public class CxClientException extends RuntimeException {
    public CxClientException() {
        super();
    }

    public CxClientException(String message) {
        super(message);
    }

    public CxClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public CxClientException(Throwable cause) {
        super(cause);
    }

}
