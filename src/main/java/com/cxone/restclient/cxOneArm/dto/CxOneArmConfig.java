package com.cxone.restclient.cxOneArm.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Galn on 7/11/2018.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CxOneArmConfig {

    private String webServer;
    private String cxOneARMPolicyURL;
    private boolean isConfidenceLevelColumnOptional;


    public String getWebServer() {
        return webServer;
    }

    public void setWebServer(String webServer) {
        this.webServer = webServer;
    }

    public String getCxOneARMPolicyURL() {
        return cxOneARMPolicyURL;
    }

    public void setCxOneARMPolicyURL(String cxARMPolicyURL) {
        this.cxOneARMPolicyURL = cxARMPolicyURL;
    }

    public boolean getIsConfidenceLevelColumnOptional() {
        return isConfidenceLevelColumnOptional;
    }

    public void setIsConfidenceLevelColumnOptional(boolean confidenceLevelColumnOptional) {
        isConfidenceLevelColumnOptional = confidenceLevelColumnOptional;
    }

   
}
