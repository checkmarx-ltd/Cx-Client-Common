package com.cx.restclient.exception;

/**
 * Created by Ilan Dayan
 */
public class CxOSAException extends CxClientException {

    public CxOSAException() {
        super();
    }

    public CxOSAException(String message) {
        super(message);
    }

    public CxOSAException(String message, Throwable cause) {
        super(message, cause);
    }

    public CxOSAException(Throwable cause) {
        super(cause);
    }

}