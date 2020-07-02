package com.cx.restclient.dto;

public enum ScannerType {
    /**
     * Indicates that dependency scan should not be performed.
     */
    SAST("CxSAST"),

    OSA("CxOSA"),
    SCA("CxSCA");

    private final String displayName;

    ScannerType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
