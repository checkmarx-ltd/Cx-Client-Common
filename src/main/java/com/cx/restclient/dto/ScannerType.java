package com.cx.restclient.dto;

public enum ScannerType {
    SAST("CxSAST"),
    OSA("CxOSA"),
    SCA("CxSCA"),
    AST("CxAST");

    private final String displayName;

    ScannerType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}