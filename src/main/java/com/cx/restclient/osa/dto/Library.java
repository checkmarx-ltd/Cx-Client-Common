package com.cx.restclient.osa.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Library implements Serializable {

    private String id;//:"36b32b00-9ee6-4e2f-85c9-3f03f26519a9",
    private String name;//:"lib-name",
    private String version;//:"lib-version",
    @JsonProperty("highUniqueVulnerabilityCount")
    private int highVulnerabilityCount;//:1,
    @JsonProperty("mediumUniqueVulnerabilityCount")
    private int mediumVulnerabilityCount;//:1,
    @JsonProperty("lowUniqueVulnerabilityCount")
    private int lowVulnerabilityCount;//:1,
    private String newestVersion;//:"1.0.0",
    private String newestVersionReleaseDate;//:"2016-12-19T10:16:19.1206743Z",
    private int numberOfVersionsSinceLastUpdate;//":10,
    private int confidenceLevel;//":100


    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return this.version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getHighVulnerabilityCount() {
        return this.highVulnerabilityCount;
    }

    public void setHighVulnerabilityCount(int highVulnerabilityCount) {
        this.highVulnerabilityCount = highVulnerabilityCount;
    }

    public int getMediumVulnerabilityCount() {
        return this.mediumVulnerabilityCount;
    }

    public void setMediumVulnerabilityCount(int mediumVulnerabilityCount) {
        this.mediumVulnerabilityCount = mediumVulnerabilityCount;
    }

    public int getLowVulnerabilityCount() {
        return this.lowVulnerabilityCount;
    }

    public void setLowVulnerabilityCount(int lowVulnerabilityCount) {
        this.lowVulnerabilityCount = lowVulnerabilityCount;
    }

    public String getNewestVersion() {
        return this.newestVersion;
    }

    public void setNewestVersion(String newestVersion) {
        this.newestVersion = newestVersion;
    }

    public String getNewestVersionReleaseDate() {
        return this.newestVersionReleaseDate;
    }

    public void setNewestVersionReleaseDate(String newestVersionReleaseDate) {
        this.newestVersionReleaseDate = newestVersionReleaseDate;
    }

    public int getNumberOfVersionsSinceLastUpdate() {
        return this.numberOfVersionsSinceLastUpdate;
    }

    public void setNumberOfVersionsSinceLastUpdate(int numberOfVersionsSinceLastUpdate) {
        this.numberOfVersionsSinceLastUpdate = numberOfVersionsSinceLastUpdate;
    }

    public int getConfidenceLevel() {
        return this.confidenceLevel;
    }

    public void setConfidenceLevel(int confidenceLevel) {
        this.confidenceLevel = confidenceLevel;
    }

}