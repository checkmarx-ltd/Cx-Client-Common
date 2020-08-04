package com.cx.configprovider.exceptions;

public class ConfigProviderException extends RuntimeException {
    public ConfigProviderException(String message, Throwable e) {
        super(message, e);
    }

    public ConfigProviderException(String message) {
        super(message);
    }
}
