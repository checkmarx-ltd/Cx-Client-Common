package com.cx.restclient.dto;

public abstract class Results {
    protected Exception createException;
    protected Exception waitException;

    public Exception getCreateException() {
        return createException;
    }

    public void setCreateException(Exception createException) {
        this.createException = createException;
    }

    public Exception getWaitException() {
        return waitException;
    }

    public void setWaitException(Exception waitException) {
        this.waitException = waitException;
    }
}
