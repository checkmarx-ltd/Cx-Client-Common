package com.cx.restclient.sast.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CxDateAndTimeObj {
    private String startedOn;

    public String getStartedOn() {
        return startedOn;
    }

    public void setStartedOn(String startedOn) {
        this.startedOn = startedOn;
    }

    public String getFinishedOn() {
        return finishedOn;
    }

    public void setFinishedOn(String finishedOn) {
        this.finishedOn = finishedOn;
    }

    public String getEngineStartedOn() {
        return engineStartedOn;
    }

    public void setEngineStartedOn(String engineStartedOn) {
        this.engineStartedOn = engineStartedOn;
    }

    public String getEngineFinishedOn() {
        return engineFinishedOn;
    }

    public void setEngineFinishedOn(String engineFinishedOn) {
        this.engineFinishedOn = engineFinishedOn;
    }

    private String finishedOn;
    private String engineStartedOn;
    private String engineFinishedOn;

}
