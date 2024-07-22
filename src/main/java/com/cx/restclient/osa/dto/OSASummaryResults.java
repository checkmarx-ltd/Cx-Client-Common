package com.cx.restclient.osa.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Created by: Dorg.
 * Date: 09/10/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OSASummaryResults implements Serializable {

    private int totalLibraries;
    private int highVulnerabilityLibraries;
    private int mediumVulnerabilityLibraries;
    private int lowVulnerabilityLibraries;
    private int nonVulnerableLibraries;
    private int vulnerableAndUpdated;
    private int vulnerableAndOutdated;
    private String vulnerabilityScore;
    private int totalHighVulnerabilities;
    private int totalMediumVulnerabilities;
    private int totalLowVulnerabilities;

    public int getTotalLibraries() {
        return totalLibraries;
    }

    public void setTotalLibraries(int totalLibraries) {
        this.totalLibraries = totalLibraries;
    }

    public int getHighVulnerabilityLibraries() {
        return highVulnerabilityLibraries;
    }

    public void setHighVulnerabilityLibraries(int highVulnerabilityLibraries) {
        this.highVulnerabilityLibraries = highVulnerabilityLibraries;
    }

    public int getMediumVulnerabilityLibraries() {
        return mediumVulnerabilityLibraries;
    }

    public void setMediumVulnerabilityLibraries(int mediumVulnerabilityLibraries) {
        this.mediumVulnerabilityLibraries = mediumVulnerabilityLibraries;
    }

    public int getLowVulnerabilityLibraries() {
        return lowVulnerabilityLibraries;
    }

    public void setLowVulnerabilityLibraries(int lowVulnerabilityLibraries) {
        this.lowVulnerabilityLibraries = lowVulnerabilityLibraries;
    }

    public int getNonVulnerableLibraries() {
        return nonVulnerableLibraries;
    }

    public void setNonVulnerableLibraries(int nonVulnerableLibraries) {
        this.nonVulnerableLibraries = nonVulnerableLibraries;
    }

    public int getVulnerableAndUpdated() {
        return vulnerableAndUpdated;
    }

    public void setVulnerableAndUpdated(int vulnerableAndUpdated) {
        this.vulnerableAndUpdated = vulnerableAndUpdated;
    }

    public int getVulnerableAndOutdated() {
        return vulnerableAndOutdated;
    }

    public void setVulnerableAndOutdated(int vulnerableAndOutdated) {
        this.vulnerableAndOutdated = vulnerableAndOutdated;
    }

    public String getVulnerabilityScore() {
        return vulnerabilityScore;
    }

    public void setVulnerabilityScore(String vulnerabilityScore) {
        this.vulnerabilityScore = vulnerabilityScore;
    }

    public int getTotalHighVulnerabilities() {
        return totalHighVulnerabilities;
    }

    public void setTotalHighVulnerabilities(int totalHighVulnerabilities) {
        this.totalHighVulnerabilities = totalHighVulnerabilities;
    }

    public int getTotalMediumVulnerabilities() {
        return totalMediumVulnerabilities;
    }

    public void setTotalMediumVulnerabilities(int totalMediumVulnerabilities) {
        this.totalMediumVulnerabilities = totalMediumVulnerabilities;
    }

    public int getTotalLowVulnerabilities() {
        return totalLowVulnerabilities;
    }

    public void setTotalLowVulnerabilities(int totalLowVulnerabilities) {
        this.totalLowVulnerabilities = totalLowVulnerabilities;
    }
}
