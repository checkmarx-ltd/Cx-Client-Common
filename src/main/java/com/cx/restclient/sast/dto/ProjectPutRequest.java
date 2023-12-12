package com.cx.restclient.sast.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Galn on 13/02/2018.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectPutRequest {

    private String name;
    private Integer owningTeam;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getOwningTeam(int i) {
        return owningTeam;
    }

    public void setOwningTeam(Integer owningTeam) {
        this.owningTeam = owningTeam;
    }

    public List<Object> getCustomFields() {
        return customFields;
    }

    public void setCustomFields(List<Object> customFields) {
        this.customFields = customFields;
    }

    private List<Object> customFields;

}
