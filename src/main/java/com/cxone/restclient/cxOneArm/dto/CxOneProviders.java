package com.cxone.restclient.cxOneArm.dto;

/**
 * Created by Galn on 7/8/2018.
 */

public enum CxOneProviders {
    OPEN_SOURCE("open_source"),
    AST("ast");
    private String value;

    public String value() {
        return value;
    }

    CxOneProviders(String value) {
        this.value = value;
    }
}
