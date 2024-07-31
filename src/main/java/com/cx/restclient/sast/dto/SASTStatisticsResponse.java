package com.cx.restclient.sast.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Galn on 29/03/2018.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SASTStatisticsResponse {
    private int highSeverity;
    private int mediumSeverity;
    private int lowSeverity;
    private int infoSeverity;

    public int getHighSeverity() {
        return highSeverity;
    }

    public void setHighSeverity(int highSeverity) {
        this.highSeverity = highSeverity;
    }

    public int getMediumSeverity() {
        return mediumSeverity;
    }

    public void setMediumSeverity(int mediumSeverity) {
        this.mediumSeverity = mediumSeverity;
    }

    public int getLowSeverity() {
        return lowSeverity;
    }

    public void setLowSeverity(int lowSeverity) {
        this.lowSeverity = lowSeverity;
    }

    public int getInfoSeverity() {
        return infoSeverity;
    }

    public void setInfoSeverity(int infoSeverity) {
        this.infoSeverity = infoSeverity;
    }
}
