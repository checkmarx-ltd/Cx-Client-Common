package com.cx.restclient.sast.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Galn on 13/02/2018.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectPutRequest {

    private String name;
    private Integer owningTeam;
    private List<ProjectLevelCustomFields> customFields;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getOwningTeam() {
        return owningTeam;
    }

    public void setOwningTeam(Integer owningTeam) {
        this.owningTeam = owningTeam;
    }

    public List<ProjectLevelCustomFields> getCustomFields() {
        return customFields;
    }

    public void setCustomFields(ArrayList<ProjectLevelCustomFields> custObj) {
        this.customFields = custObj;
    }

}
