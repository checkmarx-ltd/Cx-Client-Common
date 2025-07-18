package com.cx.restclient.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Galn on 4/1/2019.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CxVersion {
    private String version;
    private String hotFix;
    private String enginePackVersion;

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

	public String getEnginePackVersion() {
		return enginePackVersion;
	}

	public void setEnginePackVersion(String enginePackVersion) {
		this.enginePackVersion = enginePackVersion;
	}
}
