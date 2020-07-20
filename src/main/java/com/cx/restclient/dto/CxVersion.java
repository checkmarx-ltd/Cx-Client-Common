package com.cx.restclient.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Created by Galn on 4/1/2019.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CxVersion implements Serializable {
    private String version;
    private String hotFix;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getHotFix() {
        return hotFix;
    }

    public void setHotFix(String hotFix) {
        this.hotFix = hotFix;
    }
}
