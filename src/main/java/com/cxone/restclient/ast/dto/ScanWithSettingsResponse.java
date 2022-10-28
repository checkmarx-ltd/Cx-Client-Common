package com.cxone.restclient.ast.dto;

public class ScanWithSettingsResponse {
    private int id;

    public ScanWithSettingsResponse(int id) {
        this.id = id;
    }

    public ScanWithSettingsResponse() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
