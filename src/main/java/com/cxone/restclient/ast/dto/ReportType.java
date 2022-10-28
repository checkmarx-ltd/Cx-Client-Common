package com.cxone.restclient.ast.dto;

/**
 * Created by Galn on 12/02/2018.
 */
public enum ReportType {

    PDF,
    RTF,
    CSV,
    XML;

    public String value() {
        return name();
    }

    public ReportType fromValue(String v) {
        return valueOf(v);
    }

}
